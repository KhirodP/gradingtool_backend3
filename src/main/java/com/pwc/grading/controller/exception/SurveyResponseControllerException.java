package com.pwc.grading.controller.exception;

/**
 * An exception class for  SurveyResponseController
 *
 */
public class SurveyResponseControllerException extends RootControllerException {

	private static final long serialVersionUID = 2452699998343381201L;

	public SurveyResponseControllerException(int errorCode, String developerMessage, String message) {
		super(errorCode, developerMessage, message);
		// TODO Auto-generated constructor stub
	}

	public SurveyResponseControllerException(String message, Throwable cause, int errorCode, String developerMessage) {
		super(message, cause, errorCode, developerMessage);
		// TODO Auto-generated constructor stub
	}

}
