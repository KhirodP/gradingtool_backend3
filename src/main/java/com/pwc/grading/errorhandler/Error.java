package com.pwc.grading.errorhandler;

import java.util.Date;

/**
 * A class holds the details of the error
 *
 */
public class Error {

	private int errorCode;
	private String developerMessage;
	private String message;
	private Date date;
//	private String Msg;

	public Error(int errorCode, String developerMessage, String message, Date date) {
		super();
		this.errorCode = errorCode;
		this.developerMessage = developerMessage;
		this.message = message;
		this.date = date;
	}

	public Error() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Error [errorCode=" + errorCode + ", developerMessage=" + developerMessage + ", message=" + message
				+ ", date=" + date + "]";
	}

}
