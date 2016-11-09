package com.github.ledoyen.winter.examples;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features", glue = { "classpath:com/github/ledoyen/winter/stepdef",
		"classpath:com/github/ledoyen/winter/examples/cucumber" })
public class MvcApplicationCucumberTest {

}
