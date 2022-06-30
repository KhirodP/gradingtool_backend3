package com.pwc.grading.reportingdb.service.exp;

/**
 * When assigning a job, we are validating the JSON in the job, valid or not, If
 * not valid , then this exception is thrown.
 *
 */
public class JSONValidationFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public JSONValidationFailedException() {
		super();
		
	}

	public JSONValidationFailedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public JSONValidationFailedException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public JSONValidationFailedException(String message) {
		super(message);
		
	}

	public JSONValidationFailedException(Throwable cause) {
		super(cause);
		
	}

	
}
