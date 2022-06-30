package com.pwc.grading.mail.service.exception;

/**
 * An exception is to be thrown when email is unable to send.
 *
 */
public class MailServiceException extends Exception {

	private static final long serialVersionUID = -8771548898595496127L;

	public MailServiceException() {
		super();
		
	}

	public MailServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public MailServiceException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public MailServiceException(String message) {
		super(message);
		
	}

	public MailServiceException(Throwable cause) {
		super(cause);
		
	}

}
