package com.pwc.grading.mail.service;

import javax.mail.Transport;

import com.pwc.grading.mail.service.exception.MailServiceException;

/**
 * An interface class which is used to perform all
 * Email Sending related operations.
 *
 */
public interface IMailService {
//	public void sendTempPasswordToUser(String recipientEmail, String tempPassword) throws MailServiceException;

	/**
	 * This method is used to send the first time registration mail to the user.
	 * @param transport the {@link Transport} object.
	 * @param recipientEmail the user email
	 * @param firstName the user first name
	 * @param tempPassword the password generated for the user.
	 * @param emailOtp the otp of the user to send in the mail
	 * @throws MailServiceException if unable to send the email.
	 */
	public void sendFirstTimeRegistrationMailToUser(Transport transport, String recipientEmail, String firstName,
			String tempPassword, String emailOtp) throws MailServiceException;

	/**
	 * This method is used to send the forgot password mail to the user.
	 * @param recipientEmail the user email
	 * @param firstName the user first name
	 * @param emailOtp the otp of the user to send in the mail
	 * @throws MailServiceException if unable to send the email.
	 */
	public void sendForgetPasswordMailToUser(String recipientEmail, String firstName, String emailOtp)
			throws MailServiceException;
}
