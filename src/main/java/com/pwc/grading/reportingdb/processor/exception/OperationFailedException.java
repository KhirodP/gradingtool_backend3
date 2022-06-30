package com.pwc.grading.reportingdb.processor.exception;

/**
 * An exception class which is to be thrown when the processor is failed due to any errors.
 *
 */
public class OperationFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public OperationFailedException() {
		super();
		
	}

	public OperationFailedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public OperationFailedException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public OperationFailedException(String message) {
		super(message);
		
	}

	public OperationFailedException(Throwable cause) {
		super(cause);
		
	}
	
	

}
