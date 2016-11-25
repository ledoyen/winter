package com.github.ledoyen.winter.stepdef;

import cucumber.api.java.en.Given;

/**
 * The sole purpose of this class is to anchor the stepdef package in case of refactoring.
 */
public final class AnchorStepDef {
	private AnchorStepDef() {
	}

	@Given("nothing")
	public void given_nothing() {
	}
}
