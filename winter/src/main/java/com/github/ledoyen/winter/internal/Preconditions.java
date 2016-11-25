package com.github.ledoyen.winter.internal;

public final class Preconditions {

	private Preconditions() {
	}

	public static void classIsPresent(String domain, String className, String artifactId) {
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Winter is missing dependency [" + artifactId + "] to handle ["
					+ domain + "] Step Definitions, make sure " + artifactId + " is in the test classpath",
					e);
		}
	}
}
