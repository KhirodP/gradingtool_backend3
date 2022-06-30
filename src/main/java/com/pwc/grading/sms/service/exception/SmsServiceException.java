package com.pwc.grading.sms.service.exception;

/**
 * An exception class which is thrown when any exception occurs
 * when sending the sms.
 *
 */
public class SmsServiceException extends Exception {

	private static final long serialVersionUID = -7159384676403720321L;

	public SmsServiceException() {
		super();
		
	}

	public SmsServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public SmsServiceException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public SmsServiceException(String message) {
		super(message);
		
	}

	public SmsServiceException(Throwable cause) {
		super(cause);
		
	}

}
