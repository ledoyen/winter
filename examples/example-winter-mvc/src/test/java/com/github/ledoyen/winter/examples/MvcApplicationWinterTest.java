package com.github.ledoyen.winter.examples;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.github.ledoyen.winter.WinterJUnit4FeatureRunner;

import cucumber.api.CucumberOptions;

@RunWith(WinterJUnit4FeatureRunner.class)
@CucumberOptions(features = "classpath:features")
@ContextConfiguration(classes = MvcApplication.class)
public class MvcApplicationWinterTest {

}
