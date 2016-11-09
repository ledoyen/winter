package com.github.ledoyen.winter;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;

@RunWith(WinterJUnit4FeatureRunner.class)
@CucumberOptions(features = "classpath:features")
public class WinterTestLauncher {

}
