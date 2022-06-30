package com.pwc.grading.scheduler.dao.exception;

public class SchedulerDaoException extends Exception {

	private static final long serialVersionUID = 1L;

	public SchedulerDaoException() {
		super();
		
	}

	public SchedulerDaoException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public SchedulerDaoException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public SchedulerDaoException(String message) {
		super(message);
		
	}

	public SchedulerDaoException(Throwable cause) {
		super(cause);
		
	}

}
