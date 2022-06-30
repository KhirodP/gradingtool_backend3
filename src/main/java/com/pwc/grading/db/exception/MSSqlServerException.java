package com.pwc.grading.db.exception;

/**
 * An exception class which is raised when unable to get the connection from database
 * or unable to close the resources.
 *
 */
public class MSSqlServerException extends Exception{

	private static final long serialVersionUID = 1L;

	public MSSqlServerException() {
		
	}

	public MSSqlServerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public MSSqlServerException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public MSSqlServerException(String message) {
		super(message);
		
	}

	public MSSqlServerException(Throwable cause) {
		super(cause);
		
	}

}
