package com.pwc.grading.project.service.exception;

/**
 * An exception class for all the project service operations.
 *
 */
public class ProjectServiceException extends Exception {

	private static final long serialVersionUID = -4214881930371100811L;

	public ProjectServiceException() {
		super();
		
	}

	public ProjectServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public ProjectServiceException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ProjectServiceException(String message) {
		super(message);
		
	}

	public ProjectServiceException(Throwable cause) {
		super(cause);
		
	}

}
