package com.pwc.grading.controller.exception;

/**
 * An exception class for ProjectController
 *
 */
public class ProjectControllerException extends RootControllerException {

	private static final long serialVersionUID = 1L;

	public ProjectControllerException(int errorCode, String developerMessage, String message) {
		super(errorCode, developerMessage, message);
		// TODO Auto-generated constructor stub
	}

	public ProjectControllerException(String message, Throwable cause, int errorCode, String developerMessage) {
		super(message, cause, errorCode, developerMessage);
		// TODO Auto-generated constructor stub
	}

	
}
