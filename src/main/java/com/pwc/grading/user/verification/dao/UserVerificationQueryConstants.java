package com.pwc.grading.user.verification.dao;

public class UserVerificationQueryConstants {

	public static final String DATABASE_PLACE_HOLDER = "#$DataBaseName#$";
	public static final String INSERT_USER_VERIFICATION = "INSERT INTO #$DataBaseName#$.dbo.UserVerification VALUES(?, ?, ?, ?, ?, ?)";
	public static final String SELECT_ALL_USER_VERIFICATION = "SELECT userId, tempPassword, oneTimeAccessToken, expiryTime, emailOtp, smsOtp FROM #$DataBaseName#$.dbo.UserVerification";
	public static final String SELECT_BY_USERID = SELECT_ALL_USER_VERIFICATION +" where userId=?";
	public static final String DELETE_BY_USERID = "DELETE FROM #$DataBaseName#$.dbo.UserVerification WHERE userId=?";
	public static final String UPDATE_USER_VERIFICATION = "update #$DataBaseName#$.dbo.UserVerification set oneTimeAccessToken=?, expiryTime=? where userId=?";
	public static final String SELECT_BY_ACCESSTOKEN = SELECT_ALL_USER_VERIFICATION +" where oneTimeAccessToken=?";
	
	//UserVerification Column Names.
	public static final String USER_ID = "userId";
	public static final String TEMP_PWD = "tempPassword";
	public static final String ONETIME_ACCESS_TOKEN = "oneTimeAccessToken";
	public static final String EXPIRY_TIME = "expiryTime";
	public static final String EMAIL_OTP = "emailOtp";
	public static final String SMS_OTP = "smsOtp";
	
	
	
	
	
	
	
}
