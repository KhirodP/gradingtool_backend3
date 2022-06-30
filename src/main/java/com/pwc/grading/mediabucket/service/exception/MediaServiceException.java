package com.pwc.grading.mediabucket.service.exception;

/**
 * An exception class which is raised when any error occurs when
 * performing the media service related operations.
 * 
 *
 */
public class MediaServiceException extends Exception {

	private static final long serialVersionUID = 9089735915676431378L;

	public MediaServiceException() {
		super();
		
	}

	public MediaServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public MediaServiceException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public MediaServiceException(String message) {
		super(message);
		
	}

	public MediaServiceException(Throwable cause) {
		super(cause);
		
	}

}
