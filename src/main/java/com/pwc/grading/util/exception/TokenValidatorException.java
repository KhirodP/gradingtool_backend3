package com.pwc.grading.util.exception;

/**
 * An exception class will be thrown when any error occurs in 
 * validating the JWT token
 *
 */
public class TokenValidatorException extends Exception {

	private static final long serialVersionUID = 1L;

	public TokenValidatorException() {
		super();
		
	}

	public TokenValidatorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public TokenValidatorException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public TokenValidatorException(String message) {
		super(message);
		
	}

	public TokenValidatorException(Throwable cause) {
		super(cause);
		
	}

}
