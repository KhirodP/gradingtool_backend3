package com.pwc.grading.mail.service.impl;

import javax.inject.Singleton;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.mail.service.IMailService;
import com.pwc.grading.mail.service.MailServiceConstant;
import com.pwc.grading.mail.service.exception.MailServiceException;
import com.pwc.grading.mail.session.MailSessionInstance;
import com.pwc.grading.util.ReadPropertiesFile;

/**
 * Implementation class for {@link IMailService}
 *
 */
@Singleton
public class MailServiceImpl implements IMailService {

	private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

//	@Override
//	public void sendTempPasswordToUser(String recipientEmail, String tempPassword) throws MailServiceException {
//		try {
//			logger.debug(" inside send Mail Service method :recipientEmail is ::"+recipientEmail);
//
//			logger.debug("checking thread name " + Thread.currentThread().getName() + " id :"
//					+ Thread.currentThread().getId());
//			Session session = MailSessionInstance.getMailSession();
//			logger.debug("got the session object");
//			MimeMessage message = new MimeMessage(session);
//			String userName = ReadPropertiesFile.readRequestProperty(MailServiceConstant.USER_NAME);
//			logger.debug(" userName ::" + userName);
//			message.setFrom(new InternetAddress(userName));
//			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
//			message.setSubject(MailServiceConstant.ONE_TIME_PASSWORD_MAIL_SUBJECT);
//			message.setContent(MailServiceConstant.ONE_TIME_PASSWORD_MAIL_BODY_REG
//					.replace(MailServiceConstant.TEMP_PASSWORD_$, tempPassword), MailServiceConstant.CONTENT_TYPE_HTML);
//			Transport.send(message);
//			logger.debug(" Mail Successfully Send to user");
//		} catch (Exception ex) {
//			logger.error(" unable to send tempassword to user ", ex);
//			throw new MailServiceException("unable to send tempassword to user ", ex);
//		}
//
//	}

	/**
	 * This method is used to send the first time registration mail to the user.
	 * @param transport the {@link Transport} object.
	 * @param recipientEmail the user email
	 * @param firstName the user first name
	 * @param tempPassword the password generated for the user.
	 * @param emailOtp the otp of the user to send in the mail
	 * @throws MailServiceException if unable to send the email.
	 */
	@Override
	public void sendFirstTimeRegistrationMailToUser(Transport transport, String recipientEmail, String firstName,
			String tempPassword, String emailOtp) throws MailServiceException {
		try {
			logger.debug(
					" inside sendFirstTimeRegistrationMailToUser(4 arg) method :recipientEmail is ::" + recipientEmail);
			logger.debug("checking thread name " + Thread.currentThread().getName() + " id :"
					+ Thread.currentThread().getId());

			Session session = MailSessionInstance.getMailSession();

			logger.debug("got the session object");
			MimeMessage message = new MimeMessage(session);
			String userName = ReadPropertiesFile.readRequestProperty(MailServiceConstant.USER_NAME);
			logger.debug(" userName ::" + userName);
			message.setFrom(new InternetAddress(userName));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
			message.setSubject(MailServiceConstant.FIRST_TIME_REG_SUBJECT);

			String content = MailServiceConstant.FIRST_TIME_REG_MAIL_BODY.replace(MailServiceConstant.USER_NAME_$,
					firstName);
			content = content.replace(MailServiceConstant.PWD_$, tempPassword);
			content = content.replace(MailServiceConstant.EMAIL_OTP_$, emailOtp);
			content = content.replace(MailServiceConstant.APK_DOWNLOAD_LINK_$,
					ReadPropertiesFile.readRequestProperty(MailServiceConstant.GRADINGTOOL_APP_DOWNLOAD));
			// String content = MailServiceConstant.FIRST_TIME_REG_MAIL_BODY;
			message.setContent(content, MailServiceConstant.CONTENT_TYPE_HTML);
			logger.debug("Sending The Mail ........ ");
			long start = System.currentTimeMillis();
			transport.sendMessage(message, message.getAllRecipients());
			long end = System.currentTimeMillis();
//			Transport.send(message);
			logger.debug(" Mail sent in: " + (end - start) + " ms ");
			logger.debug("First time Registration Mail Successfully Send to user : " + recipientEmail);
		} catch (Exception ex) {
			logger.error("Unable to send first time Registration Email to user.", ex);
			throw new MailServiceException("Unable to send first time Registration Email to user.", ex);
		}

	}

	/**
	 * This method is used to send the forgot password mail to the user.
	 * @param recipientEmail the user email
	 * @param firstName the user first name
	 * @param emailOtp the otp of the user to send in the mail
	 * @throws MailServiceException if unable to send the email.
	 */
	@Override
	public void sendForgetPasswordMailToUser(String recipientEmail, String firstName, String emailOtp)
			throws MailServiceException {
		try {
			logger.debug(" inside sendForgetPasswordMailToUser(3 arg) method :recipientEmail is ::" + recipientEmail);
			logger.debug("checking thread name " + Thread.currentThread().getName() + " id :"
					+ Thread.currentThread().getId());

			Session session = MailSessionInstance.getMailSession();
			logger.debug("got the session object");
			MimeMessage message = new MimeMessage(session);
			String userName = ReadPropertiesFile.readRequestProperty(MailServiceConstant.USER_NAME);
			logger.debug(" userName ::" + userName);
			message.setFrom(new InternetAddress(userName));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
			message.setSubject(MailServiceConstant.OTP_MAIL_SUBJECT);

			String content = MailServiceConstant.FORGOT_PWD_MAIL_BODY.replace(MailServiceConstant.USER_NAME_$,
					firstName);
//			content = content.replace(MailServiceConstant.PASSWORD_$, tempPassword);
			content = content.replace(MailServiceConstant.EMAIL_OTP_$, emailOtp);

			message.setContent(content, MailServiceConstant.CONTENT_TYPE_HTML);
			Transport.send(message);
//			logger.debug("Sending The Mail ........ ");
//			Transport transport = MailSessionInstance.getTransport();
//			long start = System.currentTimeMillis();
//			transport.sendMessage(message, message.getAllRecipients());
//			long end = System.currentTimeMillis();
//			logger.debug(" Mail sent in: " + (end - start) + " ms.");

			logger.debug("Forgot Password Mail Successfully Send to user : " + recipientEmail);
		} catch (Exception ex) {
			logger.error("Unable to send Forgot password email to user.", ex);
			throw new MailServiceException("Unable to send Forgot password email to user.", ex);
		}

	}

}
