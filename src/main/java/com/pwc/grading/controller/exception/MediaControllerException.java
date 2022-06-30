package com.pwc.grading.controller.exception;

/**
 * An exception class for MediaController
 *
 */
public class MediaControllerException extends RootControllerException {

	private static final long serialVersionUID = 1L;

	public MediaControllerException(int errorCode, String developerMessage, String message) {
		super(errorCode, developerMessage, message);
		// TODO Auto-generated constructor stub
	}

	public MediaControllerException(String message, Throwable cause, int errorCode, String developerMessage) {
		super(message, cause, errorCode, developerMessage);
		// TODO Auto-generated constructor stub
	}

}
