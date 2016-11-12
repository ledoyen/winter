package com.github.ledoyen.winter.stepdef;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.github.ledoyen.automocker.extension.mvc.HttpStatusMatcher;
import com.github.ledoyen.automocker.extension.mvc.JsonMatchers;
import com.github.ledoyen.automocker.tools.ThrowingConsumer;
import com.github.ledoyen.automocker.tools.ThrowingRunnable;
import com.github.ledoyen.winter.model.ExpressionAndValue;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class WebStepDefs {

	@Autowired
	private MockMvc mockMvc;

	private final HttpHeaders headers = new HttpHeaders();

	private ResultActions lastRequest;

	@When("^using an HTTP header (.+)=(.+)$")
	public void using_a_header(String key, String value) throws Exception {
		headers.add(key, value);
	}

	@When("^an HTTP GET request is made on resource (.+)$")
	public void a_get_request_is_made_on(String resource) throws Exception {
		lastRequest = mockMvc.perform(MockMvcRequestBuilders.get(resource)
				.headers(headers));
	}

	@When("^an HTTP POST request is made on resource (.+) with content$")
	public void a_post_request_is_made_on(String resource, String content) throws Exception {
		lastRequest = mockMvc.perform(MockMvcRequestBuilders.post(resource)
				.content(content)
				.headers(headers));
	}

	@Then("^the HTTP response code should be (\\w+)$")
	public void the_response_code_should_be(String expectedStatus) throws Exception {
		lastRequest.andExpect(HttpStatusMatcher.parse(expectedStatus));
	}

	@Then("^the HTTP response body should be (.+)$")
	public void the_response_body_should_be(String expectedBody) throws Exception {
		lastRequest.andExpect(MockMvcResultMatchers.content()
				.string(expectedBody));
	}

	@Then("^an HTTP GET request on resource (.+) should respond status (.+) with body$")
	public void a_get_request_on_resource_should_respond(String resource, String expectedStatus,
			String expectedContent) throws Exception {
		a_get_request_is_made_on(resource);
		the_response_code_should_be(expectedStatus);
		the_response_body_should_be(expectedContent);
	}

	@Then("^the HTTP response body should match (.+)=(.+)$")
	public void response_body_should_match(String expression, String expectedValue) {
		ThrowingRunnable.silent(() -> lastRequest
				.andExpect(MockMvcResultMatchers.jsonPath(expression, JsonMatchers.is(expectedValue.trim()))))
				.run();
	}

	@Then("^the HTTP response body should match$")
	public void response_body_should_match(List<ExpressionAndValue> expressionAndValues) throws Exception {
		expressionAndValues.forEach(
				ThrowingConsumer.silent(ev -> response_body_should_match(ev.getExpression(), ev.getValue())));
	}

	@Then("^the HTTP response code should be (\\w+) and body should match (.+)=(.+)$")
	public void the_response_code_should_be_and_body_should_match(String expectedStatus, String expression,
			String expectedValue) throws Exception {
		the_response_code_should_be(expectedStatus);
		response_body_should_match(expression, expectedValue);
	}

	@Then("^an HTTP GET request on resource (.+) should respond status (.+) with body matching$")
	public void a_get_request_on_resource_should_respond_matching(String resource, String expectedStatus,
			List<ExpressionAndValue> expressionAndValues) throws Exception {
		a_get_request_is_made_on(resource);
		the_response_code_should_be(expectedStatus);
		response_body_should_match(expressionAndValues);
	}

	@Then("^an HTTP GET request on resource (.+) should respond status (.+) with body matching (.+)=(.+)$")
	public void a_get_request_on_resource_should_respond_matching(String resource, String expectedStatus,
			String expression, String expectedValue) throws Exception {
		a_get_request_is_made_on(resource);
		the_response_code_should_be(expectedStatus);
		response_body_should_match(expression, expectedValue);
	}
}
