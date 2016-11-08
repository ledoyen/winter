package com.github.ledoyen.winter.stepdef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class WebStepDefs {

	@Autowired
	private MockMvc mockMvc;

	private ResultActions lastRequest;

	@When("^an HTTP GET request is made on resource (.*)$")
	public void a_get_request_is_made_on(String resource) throws Exception {
		lastRequest = mockMvc.perform(MockMvcRequestBuilders.get(resource));
	}

	@Then("^the HTTP response code should be OK$")
	public void the_response_code_should_be_ok() throws Exception {
		lastRequest.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Then("^the HTTP response body should be (.*)$")
	public void the_response_body_should_be(String expectedBody) throws Exception {
		lastRequest.andExpect(MockMvcResultMatchers.content().string(expectedBody));
	}
}
