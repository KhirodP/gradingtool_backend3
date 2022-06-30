package com.pwc.grading;

/**
 * An exception class which is raised, when the application is unable to start
 * due to some issues.
 *
 */
public class GradingToolApplicationException extends Exception {

	private static final long serialVersionUID = 628703153956771269L;

	public GradingToolApplicationException() {
		super();
		
	}

	public GradingToolApplicationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public GradingToolApplicationException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public GradingToolApplicationException(String message) {
		super(message);
		
	}

	public GradingToolApplicationException(Throwable cause) {
		super(cause);
		
	}

	
	
}
