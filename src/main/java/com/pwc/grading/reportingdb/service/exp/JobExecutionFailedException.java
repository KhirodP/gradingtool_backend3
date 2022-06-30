package com.pwc.grading.reportingdb.service.exp;

/**
 * An exception which is thrown when execution of job is failed due to some exception.
 *
 */
public class JobExecutionFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public JobExecutionFailedException() {
		super();
	
	}

	public JobExecutionFailedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	
	}

	public JobExecutionFailedException(String message, Throwable cause) {
		super(message, cause);
	
	}

	public JobExecutionFailedException(String message) {
		super(message);
	
	}

	public JobExecutionFailedException(Throwable cause) {
		super(cause);
	
	}

	
}
