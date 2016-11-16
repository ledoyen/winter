package com.github.ledoyen.maven.plugin.winter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.surefire.SurefirePlugin;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.springframework.boot.loader.tools.MainClassFinder;

@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.TEST)
public class WinterPlugin extends SurefirePlugin {

	@Parameter(property = "winter.configurationClass")
	private String configurationClass;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String configurationClass = getConfigurationClass();
		getLog().info("Using configurationClass: " + configurationClass);

		winterConfiguration(configurationClass);

		super.execute();
	}

	@SuppressWarnings("unchecked")
	private void winterConfiguration(String configurationClass) throws MojoExecutionException {
		setTest("WinterTestLauncher");
		setDependenciesToScan(new String[] { "com.github.ledoyen.winter:winter" });
		Map<String, String> systemProperties = new HashMap<>();
		systemProperties.put("winter.ContextConfiguration", configurationClass);
		setSystemPropertyVariables(systemProperties);

		PluginDescriptor desc = (PluginDescriptor) getPluginContext().get("pluginDescriptor");
		List<Artifact> pluginDependencies = desc.getArtifacts();

		Artifact junit = getJUnitArtifact(pluginDependencies);

		getProject().getArtifacts()
				.addAll(pluginDependencies);
		getProjectArtifactMap().putIfAbsent(getJunitArtifactName(), junit);
		assertJUnitCompatibility();
	}

	private void assertJUnitCompatibility() throws MojoExecutionException {
		try {
			if (getProjectArtifactMap().get(getJunitArtifactName())
					.getSelectedVersion()
					.getMajorVersion() != 4) {
				throw new MojoExecutionException("Winter cannot work with JUnit 3.x");
			}
		} catch (OverConstrainedVersionException e) {
			throw new MojoExecutionException("Cannot determine JUnit version");
		}
	}

	private Artifact getJUnitArtifact(List<Artifact> pluginDependencies) {
		Artifact junit = pluginDependencies.stream()
				.filter(a -> a.getGroupId()
						.equals("junit")
						&& a.getArtifactId()
								.equals("junit"))
				.findAny()
				.get();
		return junit;
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
}
