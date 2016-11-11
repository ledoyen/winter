package com.github.ledoyen.winter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ledoyen.winter.internal.TestClassBuilder;
import com.github.ledoyen.winter.stepdef.AnchorStepDef;
import com.google.common.base.Stopwatch;

import cucumber.api.PendingException;
import cucumber.api.java.ObjectFactory;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.winter.WinterObjectFactory;

public class WinterJUnit4FeatureRunner extends Cucumber {

	private static final Logger LOGGER = LoggerFactory.getLogger(WinterJUnit4FeatureRunner.class);
	private static final Stopwatch sw = Stopwatch.createStarted();

	static {
		initializeCucumberProperties();
	}

	public static String getTimeSinceStart() {
		return sw.toString();
	}

	public WinterJUnit4FeatureRunner(Class<?> clazz) throws InitializationError, IOException {
		super(clazz);
		if (WinterObjectFactory.dependsOnSpringContext(clazz)) {
			WinterObjectFactory.getInstance()
					.addContextClass(clazz);
		} else {
			TestClassBuilder.getSystemProvidedConfigurationClass()
					.map(TestClassBuilder::buildTestClass)
					.ifPresent(WinterObjectFactory.getInstance()::addContextClass);
		}
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.addListener(new RunListener() {
			public void testFailure(Failure failure) throws Exception {
				if (!(failure.getException() instanceof PendingException)
						&& !(failure.getException() instanceof AssertionError)) {
					LOGGER.error(failure.getDescription() + ": " + failure.getMessage(),
							failure.getException());
				}
			}
		});
		super.run(notifier);
		LOGGER.info("All tests done in " + getTimeSinceStart());
	}

	private static void initializeCucumberProperties() {
		List<String> args = new ArrayList<>();
		Package stepDefPackage = AnchorStepDef.class.getPackage();
		args.add("-g classpath:" + stepDefPackage.getName()
				.replaceAll("\\.", "/"));
		System.setProperty(ObjectFactory.class.getName(), WinterObjectFactory.class.getName());
		System.setProperty("cucumber.options", args.stream()
				.collect(Collectors.joining(" ")));
	}
}
