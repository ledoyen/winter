package com.github.ledoyen.winter.examples.cucumber;

import org.springframework.test.context.ContextConfiguration;

import com.github.ledoyen.winter.examples.MvcApplication;

import cucumber.api.java.en.Given;

/**
 * Only used by {@link cucumber.api.junit.Cucumber} runner.
 */
@ContextConfiguration(classes = MvcApplication.class)
public class SpringContextStepDefs {

	@Given("to never call")
	public void dummyStepDefinitionToAvoidGlueFiltering() {
	}
}
