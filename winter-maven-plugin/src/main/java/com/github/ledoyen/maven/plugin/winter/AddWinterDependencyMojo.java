package com.github.ledoyen.maven.plugin.winter;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Add winter dependency, needed to run generated test-class (through <b>generateTestClass</b> goal).
 */
@Mojo(
        name = "addWinterDependency",
        defaultPhase = LifecyclePhase.PROCESS_TEST_SOURCES,
        requiresDependencyResolution = ResolutionScope.TEST,
        requiresProject = true)
public class AddWinterDependencyMojo extends AbstractMojo {

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        project.getDependencyArtifacts().add(getWinterArtifact());
    }

    private Artifact getWinterArtifact() {
        PluginDescriptor desc = (PluginDescriptor) getPluginContext().get("pluginDescriptor");
        List<Artifact> pluginDependencies = desc.getArtifacts();

        return pluginDependencies.stream().filter(p -> "winter".equals(p.getArtifactId())).findAny().get();
    }
}
