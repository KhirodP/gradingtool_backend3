package com.pwc.grading.user.model;

/**
 * A class which contains the user otp details.
 *
 */
public class UserOtp {

	String userOtp;
	long expiryDate;

	public UserOtp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserOtp(String userOtp, long expiryDate) {
		super();
		this.userOtp = userOtp;
		this.expiryDate = expiryDate;
	}

	public String getUserOtp() {
		return userOtp;
	}

	public void setUserOtp(String userOtp) {
		this.userOtp = userOtp;
	}

	public long getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(long expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Override
	public String toString() {
		return "UserOtp [userOtp=" + userOtp + ", expiryDate=" + expiryDate + "]";
	}

}
