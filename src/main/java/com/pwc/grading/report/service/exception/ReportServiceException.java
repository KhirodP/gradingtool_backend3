package com.pwc.grading.report.service.exception;

/**
 * An exception class which is raised when any error occurs 
 * when generating the report.
 *
 */
public class ReportServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReportServiceException() {
		super();
		
	}

	public ReportServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public ReportServiceException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ReportServiceException(String message) {
		super(message);
		
	}

	public ReportServiceException(Throwable cause) {
		super(cause);
		
	}

}
