package com.github.ledoyen.winter.stepdef;

import java.util.HashMap;
import java.util.Map;

import com.github.ledoyen.automocker.api.jmx.LocalMBeans;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;

public class JmxStepDefs {

	private final Map<String, Map<String, Object>> recordedMBeanAttributesByObjectNames = new HashMap<>();

	@Given("JMX bean (.+) is recorded")
	public void jmx_bean_is_recorded(String name) {
		recordedMBeanAttributesByObjectNames.put(name, LocalMBeans.INSTANCE.listAttributes(name));
	}

	@After
	public void afterScenario() {
		recordedMBeanAttributesByObjectNames.clear();
	}
}
