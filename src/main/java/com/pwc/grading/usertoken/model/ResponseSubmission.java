package com.pwc.grading.usertoken.model;

/**
 * A class having all the details related to response submission.
 *
 */
public class ResponseSubmission {
	private String otp;
	private String userEmail;
	private boolean status;
	private String operationName;

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	@Override
	public String toString() {
		return "ResponseSubmission [otp=" + otp + ", userEmail=" + userEmail + ", status=" + status + ", operationName="
				+ operationName + "]";
	}
}
