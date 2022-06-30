package com.pwc.grading.masterdata.dao.exception;

/**
 * An exception which is raised when any error related to
 * master data dao operations.
 *
 */
public class MasterDataManagmentDaoException extends Exception {

	private static final long serialVersionUID = 1L;

	public MasterDataManagmentDaoException() {
		super();
		
	}

	public MasterDataManagmentDaoException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public MasterDataManagmentDaoException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public MasterDataManagmentDaoException(String message) {
		super(message);
		
	}

	public MasterDataManagmentDaoException(Throwable cause) {
		super(cause);
		
	}

}
