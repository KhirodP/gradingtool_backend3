package com.pwc.grading.user.verification.dao.exception;

/**
 * An exception to be raised when any error occurs in the 
 * user verification services.
 *
 */
public class UserVerificationDAOException extends Exception{

	private static final long serialVersionUID = 1L;

	public UserVerificationDAOException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserVerificationDAOException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public UserVerificationDAOException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UserVerificationDAOException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UserVerificationDAOException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
}
