package com.pwc.grading.geolocation.dao.exception;

/**
 * An exception class which is raised when any geographical dao error occurs.
 *
 */
public class GeoLocationDAOException extends Exception {

	private static final long serialVersionUID = 1L;

	public GeoLocationDAOException() {

	}

	public GeoLocationDAOException(String message) {
		super(message);

	}

	public GeoLocationDAOException(Throwable cause) {
		super(cause);

	}

	public GeoLocationDAOException(String message, Throwable cause) {
		super(message, cause);

	}

	public GeoLocationDAOException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

}
