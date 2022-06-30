package com.pwc.grading.util.exception;

/**
 * An exception class will be thrown when any error occurs in 
 * processing json and getting value from the JSON.
 *
 */
public class JsonUtillException extends Exception {

	private static final long serialVersionUID = 1L;

	public JsonUtillException() {
		
	}

	public JsonUtillException(String message) {
		super(message);
		
	}

	public JsonUtillException(Throwable cause) {
		super(cause);
		
	}

	public JsonUtillException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public JsonUtillException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
