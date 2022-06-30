package com.pwc.grading.partner.service.exception;

/**
 * An exception class which is raised when any error occurs 
 * related to partner service operations.
 *
 */
public class PartnerServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public PartnerServiceException() {
		super();
		
	}

	public PartnerServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public PartnerServiceException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public PartnerServiceException(String message) {
		super(message);
		
	}

	public PartnerServiceException(Throwable cause) {
		super(cause);
		
	}

}
