package com.github.ledoyen.winter.stepdef;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.When;

public class BatchStepDefs {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Map<String, Job> jobsByName;

	@When("^the job (.+) is launched$")
	public void using_a_header(String jobName) throws JobExecutionException {
		Job job = jobsByName.get(jobName);
		jobLauncher.run(job, new JobParameters());
	}
}
