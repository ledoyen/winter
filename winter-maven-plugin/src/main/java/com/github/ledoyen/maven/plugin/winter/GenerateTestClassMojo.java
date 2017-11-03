package com.github.ledoyen.maven.plugin.winter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.lang.model.element.Modifier;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.junit.runner.RunWith;
import org.springframework.boot.loader.tools.MainClassFinder;
import org.springframework.test.context.ContextConfiguration;

import com.github.ledoyen.automocker.base.Automocker;
import com.github.ledoyen.automocker.tools.ImmutableVersion;
import com.github.ledoyen.automocker.tools.Version;
import com.github.ledoyen.winter.WinterJUnit4FeatureRunner;
import com.google.common.base.Splitter;
import com.google.common.primitives.Ints;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import cucumber.api.CucumberOptions;

/**
 * Generate a test class to run any feature in <b>featurePaths</b> (default is <i>classpath:features</i>).<br/>
 * The test class runs with {@link WinterJUnit4FeatureRunner} and is configured with <i>spring-test</i> {@link ContextConfiguration} pointing to either:
 * <ul>
 *     <li>The main class automatically detected though <i>spring-boot</i> {@link MainClassFinder} utility</li>
 *     <li>User defined main class, through configuration with <b>configurationClass</b> or with the <b>winter.configurationClass</b> property</li>
 * </ul>
 * Finally the test class is configured with all default mocking strategies from <i>spring-automocker</i> {@link Automocker}.
 */
@Mojo(
        name = "generateTestClass",
        defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES,
        requiresDependencyResolution = ResolutionScope.TEST,
        requiresProject = true)
public class GenerateTestClassMojo extends AbstractMojo {

    static final Version SPRING_MIN_VERSION = ImmutableVersion.builder().major(4).minor(3).build();
    static final Version SPRING_BOOT_MIN_VERSION = ImmutableVersion.builder().major(1).minor(4).build();
    static final Version ASSERTJ_MIN_VERSION = ImmutableVersion.builder().major(3).minor(8).build();

    /**
     * Class to use as Spring root configuration.<br/>
     * If not specified, the plugin will attempt to find it using <b>spring-boot-loader-tools</b>.
     */
    @Parameter(property = "winter.configurationClass")
    private String configurationClass;

    @Parameter(property = "winter.featurePaths", defaultValue = "classpath:features")
    private String[] featurePaths;

    /**
     * Specify output directory where the Java files are generated.
     */
    @Parameter(
            property = "winter.outputDirectory",
            defaultValue = "${project.build.directory}/generated-test-sources/winter")
    private File outputDirectory;

    /**
     * Specify location of imported grammars and tokens files.
     */
    @Parameter(defaultValue = "${basedir}/src/main/java")
    private File sourceDirectory;

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String resolvedConfigurationClass = getConfigurationClass();
        getLog().info("Using configurationClass: " + resolvedConfigurationClass);

        checkDependenciesVersion();

        writeTestClass(buildTestClass(resolvedConfigurationClass));

        project.addTestCompileSourceRoot(outputDirectory.getPath());
    }

    private void checkDependenciesVersion() {
        checkVersion("org.springframework", SPRING_MIN_VERSION, "Spring");
        checkVersion("org.springframework.boot", SPRING_BOOT_MIN_VERSION, "Spring-Boot");
        checkVersion("org.assertj", ASSERTJ_MIN_VERSION, "AssertJ");
    }

    private void checkVersion(String groupId, Version minVersion, String module) {
        Set<Artifact> problematicArtifacts = toArtifacts(project.getArtifacts())
                .stream()
                .filter(a -> groupId.equals(a.getGroupId()))
                .filter(a -> isVersionLessThan(a.getVersion(), minVersion))
                .collect(Collectors.toSet());
        if(!problematicArtifacts.isEmpty()) {
            getLog().warn("Found " + module + " artifacts in version too old for Winter (min : " + minVersion + "):\n" + problematicArtifacts.stream().map(a -> "\t" + a.getGroupId() + ":" + a.getArtifactId() + ":" + a.getVersion()).collect(Collectors.joining("\n")));
        }
    }

    private boolean isVersionLessThan(String version, Version minVersion) {
        List<String> tokenizedSpringVersion = Splitter.on('.').splitToList(version);
        int major = Ints.tryParse(tokenizedSpringVersion.get(0));
        int minor = Ints.tryParse(tokenizedSpringVersion.get(1));

        if (major < minVersion.major() || major == minVersion.major() && minor < minVersion.minor()) {
            return true;
        }
        return false;
    }

    private static Set<Artifact> toArtifacts(Set s) {
        return (Set<Artifact>) s;
    }

    @SuppressWarnings("unchecked")
    private void writeTestClass(JavaFile javaFile) throws MojoExecutionException {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        try {
            javaFile.writeTo(outputDirectory);
            getLog().info("Test class written in : " + outputDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to write file into : " + outputDirectory, e);
        }
    }

    private JavaFile buildTestClass(String resolvedConfigurationClass) {
        ClassName configurationClassName = ClassName.bestGuess(resolvedConfigurationClass);
        TypeSpec testClass = TypeSpec.classBuilder("RunWinterTest")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(
                        AnnotationSpec
                                .builder(RunWith.class)
                                .addMember("value", "$T.class", WinterJUnit4FeatureRunner.class)
                                .build()
                )
                .addAnnotation(
                        AnnotationSpec
                                .builder(CucumberOptions.class)
                                .addMember("features", IntStream.range(0, featurePaths.length).mapToObj(i -> "$S").collect(Collectors.joining(", ", "{", "}")), featurePaths)
                                .build()
                )
                .addAnnotation(
                        AnnotationSpec
                                .builder(ContextConfiguration.class)
                                .addMember("classes", "$T.class", configurationClassName)
                                .build()
                )
                .addAnnotation(Automocker.class)
                .build();
        return JavaFile.builder("winter", testClass)
                .addFileComment("Generated by winter-maven-plugin")
                .build();
    }

    private String getConfigurationClass() throws MojoExecutionException {
        String mainClass = this.configurationClass;
        if (mainClass == null) {
            try {
                mainClass = MainClassFinder.findSingleMainClass(getClassesDirectory());
            } catch (IOException ex) {
                throw new MojoExecutionException(ex.getMessage(), ex);
            } catch (IllegalStateException ex) {
                throw new MojoExecutionException(
                        ex.getMessage() + ", please add a 'configurationClass' property");
            }
        }
        if (mainClass == null) {
            throw new MojoExecutionException("Unable to find a suitable configuration class, "
                    + "please add a 'configurationClass' property");
        }
        return mainClass;
    }

    public File getClassesDirectory() {
        return new File(project.getBuild().getOutputDirectory());
    }
}
