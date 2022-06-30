package com.pwc.grading.masterdata.service.exception;

/**
 * An exception class which is thrown if any occurs in master data management
 * service operations.
 *
 */
public class MasterDataManagmentServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public MasterDataManagmentServiceException() {
	
	}

	public MasterDataManagmentServiceException(String message) {
		super(message);
	
	}

	public MasterDataManagmentServiceException(Throwable cause) {
		super(cause);
	
	}

	public MasterDataManagmentServiceException(String message, Throwable cause) {
		super(message, cause);
	
	}

	public MasterDataManagmentServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	
	}

}
