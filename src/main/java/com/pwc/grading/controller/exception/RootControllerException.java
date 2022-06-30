package com.pwc.grading.controller.exception;

/**
 * A parent exception class for all the controller classes.
 *
 */
public class RootControllerException extends Exception {

	private static final long serialVersionUID = -6125885623900270176L;

	private int errorCode;
	private String developerMessage;
	private String message;

	public RootControllerException(int errorCode, String developerMessage, String message) {
		super(message);
		this.errorCode = errorCode;
		this.developerMessage = developerMessage;
		this.message = message;
	}

	public RootControllerException(String message, Throwable cause, int errorCode, String developerMessage) {
		super(message, cause);
		this.errorCode = errorCode;
		this.developerMessage = developerMessage;
		this.message = message;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public String getMessage() {
		return message;
	}

}
