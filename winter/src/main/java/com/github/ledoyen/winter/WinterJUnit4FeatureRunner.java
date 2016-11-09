package com.github.ledoyen.winter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.ContextConfiguration;

import com.github.ledoyen.automocker.tools.ThrowingFunction;
import com.github.ledoyen.winter.stepdef.AnchorStepDef;

import cucumber.api.java.ObjectFactory;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.winter.WinterObjectFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;

public class WinterJUnit4FeatureRunner extends Cucumber {

	private static final String CONTEXT_CONFIGURATION_CLASS_KEY = "winter.ContextConfiguration";

	static {
		initializeCucumberProperties();
	}

	public WinterJUnit4FeatureRunner(Class<?> clazz) throws InitializationError, IOException {
		super(clazz);

		Optional<Class<?>> systemProvidedConfigurationClass = Optional.ofNullable(System.getProperty(CONTEXT_CONFIGURATION_CLASS_KEY))
				.map(ThrowingFunction.silent(s -> Class.forName(s)));

		if (WinterObjectFactory.dependsOnSpringContext(clazz)) {
			WinterObjectFactory.getInstance().addContextClass(clazz);
		} else if (systemProvidedConfigurationClass.isPresent()) {
			Class<?> configurationClass = buildConfigurationClass(systemProvidedConfigurationClass.get());
			WinterObjectFactory.getInstance().addContextClass(configurationClass);
		}
	}

	private Class<?> buildConfigurationClass(Class<?> systemProvidedConfigurationClass) {
		ByteBuddy bb = new ByteBuddy();
		AnnotationDescription contextConfigurationDescription = AnnotationDescription.Builder.ofType(ContextConfiguration.class)
				.defineTypeArray("classes", systemProvidedConfigurationClass).build();
		Class<?> configurationClass = bb.subclass(Object.class).annotateType(contextConfigurationDescription).make().load(getClass().getClassLoader()).getLoaded();
		return configurationClass;
	}

	private static void initializeCucumberProperties() {
		List<String> args = new ArrayList<>();
		Package stepDefPackage = AnchorStepDef.class.getPackage();
		args.add("-g classpath:" + stepDefPackage.getName().replaceAll("\\.", "/"));
		System.setProperty(ObjectFactory.class.getName(), WinterObjectFactory.class.getName());
		System.setProperty("cucumber.options", args.stream().collect(Collectors.joining(" ")));
	}
}
