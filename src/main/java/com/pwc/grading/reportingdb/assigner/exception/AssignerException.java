package com.pwc.grading.reportingdb.assigner.exception;

/**
 * An expection which is raised if some error occurs when assigning the job 
 * to the reporting related tables.
 *
 */
public class AssignerException extends Exception {

	private static final long serialVersionUID = 1L;

	public AssignerException() {
		super();
	}

	public AssignerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AssignerException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssignerException(String message) {
		super(message);
	}

	public AssignerException(Throwable cause) {
		super(cause);
	}

	
}
