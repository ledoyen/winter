package com.github.ledoyen.winter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.runners.model.InitializationError;

import com.github.ledoyen.winter.internal.TestClassBuilder;
import com.github.ledoyen.winter.stepdef.AnchorStepDef;

import cucumber.api.java.ObjectFactory;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.winter.WinterObjectFactory;

public class WinterJUnit4FeatureRunner extends Cucumber {

	static {
		initializeCucumberProperties();
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
