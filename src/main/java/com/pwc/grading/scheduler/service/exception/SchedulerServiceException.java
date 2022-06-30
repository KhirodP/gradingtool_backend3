package com.pwc.grading.scheduler.service.exception;
/**
 * An exception class raised when any error occurs in scheduler service operations.
 *
 */
public class SchedulerServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public SchedulerServiceException() {
		super();
		
	}

	public SchedulerServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public SchedulerServiceException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public SchedulerServiceException(String message) {
		super(message);
		
	}

	public SchedulerServiceException(Throwable cause) {
		super(cause);
		
	}

}
