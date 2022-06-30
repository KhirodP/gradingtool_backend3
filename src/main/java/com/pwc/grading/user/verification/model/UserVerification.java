package com.pwc.grading.user.verification.model;

/**
 * A class having the details of the user-verification.
 *
 */
public class UserVerification {

	private String userId;
	private String tempPassword;
	private String oneTimeAccessToken;
	private long expiryTime;
	private String emailOtp;
	private String smsOtp;
	
	public UserVerification() {
		
	}

	public String getUserId() {
		return userId;
	}

	public String getTempPassword() {
		return tempPassword;
	}

	public String getOneTimeAccessToken() {
		return oneTimeAccessToken;
	}

	public long getExpiryTime() {
		return expiryTime;
	}

	public String getEmailOtp() {
		return emailOtp;
	}

	public String getSmsOtp() {
		return smsOtp;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}

	public void setOneTimeAccessToken(String oneTimeAccessToken) {
		this.oneTimeAccessToken = oneTimeAccessToken;
	}

	public void setExpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}

	public void setEmailOtp(String emailOtp) {
		this.emailOtp = emailOtp;
	}

	public void setSmsOtp(String smsOtp) {
		this.smsOtp = smsOtp;
	}

	@Override
	public String toString() {
		return "UserVerification [userId=" + userId + ", tempPassword=" + tempPassword + ", oneTimeAccessToken="
				+ oneTimeAccessToken + ", expiryTime=" + expiryTime + ", emailOtp=" + emailOtp + ", smsOtp=" + smsOtp
				+ "]";
	}

	
	
	
}
