package com.pwc.grading.reportingdb.dao.exception;

/**
 * An exception class which is to be thrown when error occurs while performing
 * reporting tables DAO operation
 *
 */
public class ReportingDbDAOException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReportingDbDAOException() {
		super();
		
	}

	public ReportingDbDAOException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public ReportingDbDAOException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ReportingDbDAOException(String message) {
		super(message);
		
	}

	public ReportingDbDAOException(Throwable cause) {
		super(cause);
		
	}
	
	

}
