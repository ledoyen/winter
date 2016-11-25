package com.github.ledoyen.winter.stepdef;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ledoyen.automocker.tools.Classes;
import com.github.ledoyen.winter.internal.LogbackUtil;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class LogStepDefs {

	private static final boolean LOGBACK_PRESENT = Classes.isPresent("ch.qos.logback.classic.Logger");

	@Given("^logs are recorded$")
	public void logs_are_recorded() throws Exception {
		if (LOGBACK_PRESENT) {
			LogbackUtil.record();
		}
	}

	@Then("^logger (.+) have been used (\\d+) times$")
	public void logger_have_been_used_times(String loggerName, int invocationTimes) throws Exception {
		if (LOGBACK_PRESENT) {
			assertThat(LogbackUtil.getEvents()
					.stream()
					.filter(e -> loggerName.equals(e.getLoggerName()))
					.count()).as("Logger [" + loggerName + "] invocation count")
							.isEqualTo(invocationTimes);
		}
	}

	@After
	public void tearDown() {
		if (LOGBACK_PRESENT) {
			LogbackUtil.clear();
		}
	}
}
