package com.pwc.grading.mail.service.exception;

/**
 *  An exception is to be thrown when any error occurs in acquiring mail session.
 *
 */
public class MailSessionException extends Exception {

	private static final long serialVersionUID = -9168096914465650906L;

	public MailSessionException() {
		super();
		
	}

	public MailSessionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public MailSessionException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public MailSessionException(String message) {
		super(message);
		
	}

	public MailSessionException(Throwable cause) {
		super(cause);
		
	}

}
