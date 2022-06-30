package com.pwc.grading.user.model;

/**
 * A class which contains the forgot password otp details
 * for the user.
 *
 */
public class ForgotPasswordUserOtp {
	private String emailOtp;
	private String smsOtp;
	private long expiryTime;
	
	public ForgotPasswordUserOtp() {
		// TODO Auto-generated constructor stub
	}
	public ForgotPasswordUserOtp(String emailOtp, String smsOtp, long expiryTime) {
		super();
		this.emailOtp = emailOtp;
		this.smsOtp = smsOtp;
		this.expiryTime = expiryTime;
	}

	public String getEmailOtp() {
		return emailOtp;
	}

	public String getSmsOtp() {
		return smsOtp;
	}

	public long getExpiryTime() {
		return expiryTime;
	}

	public void setEmailOtp(String emailOtp) {
		this.emailOtp = emailOtp;
	}

	public void setSmsOtp(String smsOtp) {
		this.smsOtp = smsOtp;
	}

	public void setExpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}

	@Override
	public String toString() {
		return "ForgotPasswordUserOtp [emailOtp=" + emailOtp + ", smsOtp=" + smsOtp + ", expiryTime=" + expiryTime
				+ "]";
	}
	
}
