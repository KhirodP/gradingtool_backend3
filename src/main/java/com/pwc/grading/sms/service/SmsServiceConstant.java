package com.pwc.grading.sms.service;

/**
 * A class holds all the constant values related to the sms service.
 *
 */
public class SmsServiceConstant {

	public static final String TEXT_LOCAL_API_KEY = "sms.textlocal.apiKey";
	public static final String TEXT_LOCAL_URL = "https://api.textlocal.in/send/?";
	public static final String TEXT_LOCAL_OTP_MSG = "sms.textlocal.msg";
	public static final String TEXT_LOCAL_SENDER = "sms.textlocal.sender";
	public static final String TEXT_LOCAL_URL_API_KEY = "apikey=";
	public static final String TEXT_LOCAL_URL_MSG = "&message=";
	public static final String TEXT_LOCAL_URL_SENDER = "&sender=";
	public static final String TEXT_LOCAL_URL_NUMBER = "&numbers=";
	public static final String POST = "POST";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String UTF = "UTF-8";
	public static final String STATUS = "status";
	public static final String FAILURE = "failure";

	public static final String TEXT_LOCAL_TEMPORARY_PASSWORD_MSG = "sms.temporary.password.msg";
	
	public static final String FIRST_TIME_REG_SMS = "Hi UUUUUUUU, Registration Success for GradingTool. Please check your Email for temporary password to Login. Your SMS otp is XXXXXX";
	public static final String FORGOT_PWD_SMS = "Hi $$UserName$$, Your OTP for resetting your password is $$OTP$$";
	public static final String FA_SUMBIT_SMS = "Hi $$UserName$$, The OTP is $$OTP$$. Please share the OTP with the assessment officer to submit response.";
	public static final String CIC_CS_SUBMIT_SMS  = "Hi $$UserName$$, The OTP is $$OTP$$ and please enter this OTP to submit your response.";
	public static final String USER_NAME_$ = "UUUUUUUU";
	public static final String FA_NAME_$ = "$$FAName$$";
//	public static final String PASSWORD_$ = "$$password$$";
	public static final String SMS_OTP_$ = "XXXXXX";
}
