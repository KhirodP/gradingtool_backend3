package com.pwc.grading.surveyresponse.dao.exception;

/**
 * An exception class for SurveyResponse DAO operations.
 *
 */
public class SurveyResponseDaoException extends Exception {

	private static final long serialVersionUID = 2006713139982366116L;

	public SurveyResponseDaoException() {
		super();
		
	}

	public SurveyResponseDaoException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public SurveyResponseDaoException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public SurveyResponseDaoException(String message) {
		super(message);
		
	}

	public SurveyResponseDaoException(Throwable cause) {
		super(cause);
		
	}

}
