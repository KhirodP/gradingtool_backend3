package com.pwc.grading.partner.dao.exception;

/**
 * An exception class which is to be thrown when any occurs
 * while performing the Partner related operations.
 *
 */
public class PartnerDaoException extends Exception {

	private static final long serialVersionUID = 1L;

	public PartnerDaoException() {

	}

	public PartnerDaoException(String message) {
		super(message);
		
	}

	public PartnerDaoException(Throwable cause) {
		super(cause);
		
	}

	public PartnerDaoException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public PartnerDaoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
