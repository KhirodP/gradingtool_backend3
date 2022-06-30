package com.pwc.grading.mediabucket.dao.exception;

/**
 * An exception class which is to be thrown if any exceptions occur
 * when performing the database operations in the Media table.
 *
 */
public class MediaDaoException extends Exception {

	private static final long serialVersionUID = 1L;

	public MediaDaoException() {
		super();

	}

	public MediaDaoException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	public MediaDaoException(String message, Throwable cause) {
		super(message, cause);

	}

	public MediaDaoException(String message) {
		super(message);

	}

	public MediaDaoException(Throwable cause) {
		super(cause);

	}

}
