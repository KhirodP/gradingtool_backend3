package com.pwc.grading.controller.exception;

/**
 * An exception class for UserAccessManagementController
 *
 */
public class UserAccessManagementControllerException extends RootControllerException {

	private static final long serialVersionUID = -3014290500929349324L;

	public UserAccessManagementControllerException(int errorCode, String developerMessage, String message) {
		super(errorCode, developerMessage, message);
		// TODO Auto-generated constructor stub
	}

	public UserAccessManagementControllerException(String message, Throwable cause, int errorCode,
			String developerMessage) {
		super(message, cause, errorCode, developerMessage);
		// TODO Auto-generated constructor stub
	}

}
