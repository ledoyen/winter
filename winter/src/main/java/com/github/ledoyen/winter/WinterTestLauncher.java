package com.github.ledoyen.winter;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;

@RunWith(WinterJUnit4FeatureRunner.class)
@CucumberOptions(features = "classpath:features", plugin = "pretty", strict = true)
public class WinterTestLauncher {

}
