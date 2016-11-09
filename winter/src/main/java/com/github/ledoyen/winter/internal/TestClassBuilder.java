package com.github.ledoyen.winter.internal;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.github.ledoyen.automocker.tools.ThrowingFunction;
import com.google.common.base.Splitter;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

public final class TestClassBuilder {

	private static final String CONTEXT_CONFIGURATION_CLASS_KEY = "winter.ContextConfiguration";
	private static final String TEST_PROPERTY_SOURCE_LOCATIONS_KEY = "winter.TestPropertySource.locations";

	private TestClassBuilder() {
	}

	public static Optional<Class<?>> getSystemProvidedConfigurationClass() {
		return Optional.ofNullable(System.getProperty(CONTEXT_CONFIGURATION_CLASS_KEY))
				.map(ThrowingFunction.silent(s -> Class.forName(s)));
	}

	public static Class<?> buildTestClass(Class<?> systemProvidedConfigurationClass) {
		AnnotationDescription contextConfigurationDescription = AnnotationDescription.Builder
				.ofType(ContextConfiguration.class)
				.defineTypeArray("classes", systemProvidedConfigurationClass)
				.build();

		// Cannot use ifPresent here as AnnotationDescription.Builder is immutable
		Optional<AnnotationDescription> testPropertySourceDescription = Optional
				.ofNullable(System.getProperty(TEST_PROPERTY_SOURCE_LOCATIONS_KEY))
				.map(s -> Splitter.on(',')
						.trimResults()
						.splitToList(s))
				.map(a -> AnnotationDescription.Builder.ofType(TestPropertySource.class)
						.defineArray("locations", a.toArray(new String[a.size()]))
						.build());

		DynamicType.Builder<?> configurationClassBuilder = new ByteBuddy().subclass(Object.class)
				.name("GeneratedTestClass_" + systemProvidedConfigurationClass.getSimpleName() + "_"
						+ new SecureRandom().nextInt(Integer.MAX_VALUE))
				.annotateType(contextConfigurationDescription)
				.method(ElementMatchers.named("toString"))
				.intercept(FixedValue
						.value("Test class for " + systemProvidedConfigurationClass.getSimpleName()));

		if (testPropertySourceDescription.isPresent()) {
			configurationClassBuilder = configurationClassBuilder
					.annotateType(testPropertySourceDescription.get());
		}

		Class<?> configurationClass = configurationClassBuilder.make()
				.load(TestClassBuilder.class.getClassLoader())
				.getLoaded();
		return configurationClass;
	}
}
