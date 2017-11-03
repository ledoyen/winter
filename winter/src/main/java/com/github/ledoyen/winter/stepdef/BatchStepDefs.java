package com.github.ledoyen.winter.stepdef;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.ledoyen.automocker.api.batch.BatchLauncherMock;
import com.github.ledoyen.automocker.api.batch.BatchMock;

import cucumber.api.java.After;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BatchStepDefs {

	@Autowired
	private Provider<BatchLauncherMock> jobLauncher;

	private BatchMock lastJobExecution;

	@When("^the job (.+) is launched$")
	public void job_is_launched(String jobName) throws Exception {
		lastJobExecution = jobLauncher.get()
				.launch(jobName);
	}

	@Then("^execution is a success$")
	public void execution_is_a_success() {
		assertThat(lastJobExecution.getAllFailureExceptions()).as("Batch failure exceptions")
				.hasSize(0);
	}

	@After
	public void tearDownScenario() {
		lastJobExecution = null;
	}
}
