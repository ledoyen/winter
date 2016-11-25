package com.github.ledoyen.winter.stepdef;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * The sole purpose of this class is to anchor the stepdef package in case of refactoring.
 */
public final class AnchorStepDef {
	private AnchorStepDef() {
	}

	@Given("nothing")
	public void given_nothing() {
	}

	@When("nothing")
	public void when_nothing() {
	}

	@Then("nothing")
	public void then_nothing() {
	}
}
