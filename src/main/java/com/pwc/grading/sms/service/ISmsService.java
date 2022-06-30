package com.pwc.grading.sms.service;

import com.pwc.grading.sms.service.exception.SmsServiceException;
import com.pwc.grading.user.model.User;
/**
 * An interface class which is used to perform all
 * sms sending operations.
 *
 */
public interface ISmsService {

//	public void sendOtp(User user, String otp) throws SmsServiceException;
//
//	public void sendSms(User user, String sms) throws SmsServiceException;

//	public void sendTempPassword(User user, String tempPassword) throws SmsServiceException;
	
	/**
	 * This method is used to send the first time registration sms to the user.
	 * @param user the user details.
	 * @param smsOtp the otp to be send in sms.
	 * @throws SmsServiceException if any exception occurs when sending the sms.
	 */
	public void sendFirstTimeSmsToUser(User user, String smsOtp) throws SmsServiceException;

	/**
	 * This method is used to send the otp to the user for reset password.
	 * @param user the user details.
	 * @param smsOtp  the otp to be send in sms.
	 * @throws SmsServiceException if any exception occurs when sending the sms.
	 */
	void sendResetPasswordSmsToUser(User user, String smsOtp) throws SmsServiceException;

	/**
	 * This method is used to send the otp to the user when submitting response.
	 * @param user the user details.
	 * @param smsOtp the otp to be send in sms.
	 * @throws SmsServiceException if any exception occurs when sending the sms.
	 */
	void sendOtpFieldAuditorSubmission(User user, String smsOtp) throws SmsServiceException;
	
	/**
	 * This method is used to send the otp to the user when submitting response.
	 * @param user the user details.
	 * @param smsOtp the otp to be send in sms.
	 * @throws SmsServiceException if any exception occurs when sending the sms.
	 */
	void sendOtpSelfAssesmentSubmission(User user, String smsOtp) throws SmsServiceException;
}
