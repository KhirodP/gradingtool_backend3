package com.pwc.grading.reportingdb.db.exception;

/**
 * An exception class which is thrown whenever any error occurs
 * while connecting to the database.
 *
 */
public class ReportDBMSSqlServerException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReportDBMSSqlServerException() {
		super();
	}

	public ReportDBMSSqlServerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ReportDBMSSqlServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReportDBMSSqlServerException(String message) {
		super(message);
	}

	public ReportDBMSSqlServerException(Throwable cause) {
		super(cause);
	}

	
}
