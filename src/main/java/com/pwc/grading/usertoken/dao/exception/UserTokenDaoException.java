package com.pwc.grading.usertoken.dao.exception;

/**
 * An exception class which is raised when any error occurs in the 
 * dao layer of User token related operations.
 *
 */
public class UserTokenDaoException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserTokenDaoException() {
		super();
		
	}

	public UserTokenDaoException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		
	}

	public UserTokenDaoException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		
	}

	public UserTokenDaoException(String arg0) {
		super(arg0);
		
	}

	public UserTokenDaoException(Throwable arg0) {
		super(arg0);
		
	}

}
