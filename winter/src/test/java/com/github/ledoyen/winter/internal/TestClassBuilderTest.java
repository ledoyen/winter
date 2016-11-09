package com.github.ledoyen.winter.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Condition;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import net.bytebuddy.ByteBuddy;

public class TestClassBuilderTest {

	private static final Class<?> CONFIGURATION_CLASS = new ByteBuddy().subclass(Object.class)
			.make()
			.load(TestClassBuilderTest.class.getClassLoader())
			.getLoaded();

	@Test
	public void systemProvidedConfigurationClass_is_detected() {
		System.clearProperty("winter.ContextConfiguration");
		assertThat(TestClassBuilder.getSystemProvidedConfigurationClass())
				.as("System provided Configuration class")
				.isEmpty();

		System.setProperty("winter.ContextConfiguration", CONFIGURATION_CLASS.getName());
		assertThat(TestClassBuilder.getSystemProvidedConfigurationClass())
				.as("System provided Configuration class")
				.hasValue(CONFIGURATION_CLASS);
	}

	@Test
	public void buildConfigurationClass_returns_correctly_configured_test_class() {
		Class<?> testClass = TestClassBuilder.buildTestClass(CONFIGURATION_CLASS);

		assertThat(testClass).as("Generated Test class")
				.isNotNull()
				.hasAnnotation(ContextConfiguration.class)
				.doesNotHave(new Condition<>(c -> c.isAnnotationPresent(TestPropertySource.class),
						TestPropertySource.class.getSimpleName() + " annotation present"));

		assertThat(testClass.getAnnotation(ContextConfiguration.class))
				.as("Generated Test class @ContextConfiguration")
				.has(new Condition<>(
						c -> c.classes().length == 1 && CONFIGURATION_CLASS.equals(c.classes()[0]),
						"classes attribute must contain the configurationClass"));

		System.setProperty("winter.TestPropertySource.locations", "test_path/, classpath:some/other/path");

		testClass = TestClassBuilder.buildTestClass(CONFIGURATION_CLASS);

		assertThat(testClass).as("Generated Test class with @TestPropertySource configuration")
				.isNotNull()
				.hasAnnotation(ContextConfiguration.class)
				.hasAnnotation(TestPropertySource.class);

		assertThat(testClass.getAnnotation(ContextConfiguration.class))
				.as("Generated Test class @ContextConfiguration")
				.has(new Condition<>(
						c -> c.classes().length == 1 && CONFIGURATION_CLASS.equals(c.classes()[0]),
						"classes attribute must contain the configurationClass"));
		assertThat(testClass.getAnnotation(TestPropertySource.class))
				.as("Generated Test class @TestPropertySource")
				.has(new Condition<>(c -> c.locations().length == 2,
						"locations attribute is an array of length 2"))
				.has(new Condition<>(c -> "test_path/".equals(c.locations()[0]),
						"locations attribute contains the first location path"))
				.has(new Condition<>(c -> "classpath:some/other/path".equals(c.locations()[1]),
						"locations attribute contains the second location path"));
	}
}
