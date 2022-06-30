package com.pwc.grading.sms.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.sms.service.ISmsService;
import com.pwc.grading.sms.service.SmsServiceConstant;
import com.pwc.grading.sms.service.exception.SmsServiceException;
import com.pwc.grading.user.model.User;
import com.pwc.grading.util.JsonUtill;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.exception.JsonUtillException;
/**
 * Implementation class for {@link ISmsService}
 *
 */
@Singleton
public class TextlocalSmsServiceImpl implements ISmsService {

	private static final Logger logger = LoggerFactory.getLogger(TextlocalSmsServiceImpl.class);

//	@Override
//	public void sendOtp(User user, String otp) throws SmsServiceException {
//		logger.debug("inside sendOtp service of TextlocalSmsServiceImpl");
//		try {
//			String apiKey = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_API_KEY);
//			String message = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_OTP_MSG);
//			String sender = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_SENDER);
//			String mobileNumber = user.getPhone();
//			message = message + " " + otp;
//			String data = SmsServiceConstant.TEXT_LOCAL_URL_API_KEY + apiKey + SmsServiceConstant.TEXT_LOCAL_URL_NUMBER
//					+ mobileNumber + SmsServiceConstant.TEXT_LOCAL_URL_MSG + message;
//			if (sender != null) {
//				data = SmsServiceConstant.TEXT_LOCAL_URL_SENDER + sender;
//			}
//			send(data);
//		} catch (Exception e) {
//			logger.error(" unable to send otp ", e);
//			throw new SmsServiceException(" unable to send otp ", e);
//		}
//
//	}

//	public void sendTempPassword(User user, String tempPassword) throws SmsServiceException {
//		logger.debug("inside sendOtp service of TextlocalSmsServiceImpl" + Thread.currentThread().getName());
//		try {
//			String apiKey = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_API_KEY);
//			String message = ReadPropertiesFile
//					.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_TEMPORARY_PASSWORD_MSG);
//			String sender = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_SENDER);
//			String mobileNumber = user.getPhone();
//			message = message + " " + tempPassword;
//			String data = SmsServiceConstant.TEXT_LOCAL_URL_API_KEY + apiKey + SmsServiceConstant.TEXT_LOCAL_URL_NUMBER
//					+ mobileNumber + SmsServiceConstant.TEXT_LOCAL_URL_MSG + message;
//			if (sender != null) {
//				data = SmsServiceConstant.TEXT_LOCAL_URL_SENDER + sender;
//			}
//			send(data);
//		} catch (Exception e) {
//			logger.error(" unable to send otp ", e);
//			throw new SmsServiceException(" unable to send otp ", e);
//		}
//
//	}

//	@Override
//	public void sendSms(User user, String message) throws SmsServiceException {
//		logger.debug("inside sendOtp service of TextlocalSmsServiceImpl");
//		try {
//			String apiKey = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_API_KEY);
//			String sender = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_SENDER);
//			String mobileNumber = user.getPhone();
//			String data = SmsServiceConstant.TEXT_LOCAL_API_KEY + apiKey + SmsServiceConstant.TEXT_LOCAL_URL_NUMBER
//					+ mobileNumber + SmsServiceConstant.TEXT_LOCAL_URL_MSG + message;
//			if (sender != null) {
//				data = SmsServiceConstant.TEXT_LOCAL_URL_SENDER + sender;
//			}
//			send(data);
//		} catch (Exception e) {
//			logger.error(" unable to send message ", e);
//			throw new SmsServiceException(" unable to send message ", e);
//		}
//	}

//	private String send(String data) throws IOException, SmsServiceException, JsonUtillException {
//		logger.debug(" data  :: " + data);
//		HttpURLConnection conn = (HttpURLConnection) new URL(SmsServiceConstant.TEXT_LOCAL_URL).openConnection();
//		conn.setDoOutput(true);
//		conn.setRequestMethod(SmsServiceConstant.POST);
//		conn.setRequestProperty(SmsServiceConstant.CONTENT_LENGTH, Integer.toString(data.length()));
//		conn.getOutputStream().write(data.getBytes(SmsServiceConstant.UTF));
//		conn.connect();
//		final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		final StringBuffer stringBuffer = new StringBuffer();
//		String line;
//		while ((line = rd.readLine()) != null) {
//			stringBuffer.append(line);
//		}
//		rd.close();
//		int resonseCode = conn.getResponseCode();
//		logger.debug("responseCode :: " + resonseCode);
//		conn.disconnect();
//		String responseMsg = stringBuffer.toString();
//		logger.debug("responseMsg :: " + responseMsg);
//		JSONObject jsonObject = (JSONObject) JSONValue.parse(responseMsg);
//		String status = JsonUtill.getString(jsonObject, SmsServiceConstant.STATUS);
//
//		logger.debug(" response of sms service :: " + responseMsg);
//		if (resonseCode != 200 || status.equalsIgnoreCase(SmsServiceConstant.FAILURE)) {
//			logger.error(" unable to send otp ");
////			throw new SmsServiceException(" unable to send otp ");
//		}
//
//		return responseMsg;
//	}

	private boolean sendMsg(String data) throws IOException, SmsServiceException, JsonUtillException {
		logger.debug(" data  :: " + data);
		
		HttpURLConnection conn = (HttpURLConnection) new URL(SmsServiceConstant.TEXT_LOCAL_URL).openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(SmsServiceConstant.POST);
		conn.setRequestProperty(SmsServiceConstant.CONTENT_LENGTH, Integer.toString(data.length()));
		conn.getOutputStream().write(data.getBytes(SmsServiceConstant.UTF));
		conn.connect();
		final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		final StringBuffer stringBuffer = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			stringBuffer.append(line);
		}
		rd.close();
		int resonseCode = conn.getResponseCode();
		logger.debug("responseCode :: " + resonseCode);
		conn.disconnect();
		String responseMsg = stringBuffer.toString();
		logger.debug("responseMsg :: " + responseMsg);
		JSONObject jsonObject = (JSONObject) JSONValue.parse(responseMsg);
		String status = JsonUtill.getString(jsonObject, SmsServiceConstant.STATUS);

		logger.debug(" response of sms service :: " + responseMsg);
		if (resonseCode != 200 || status.equalsIgnoreCase(SmsServiceConstant.FAILURE)) {
			logger.error("unable to send otp to user");
			return false;
		}

		return true;
	}
	
	/**
	 * This method is used to send the first time registration sms to the user.
	 * @param user the user details.
	 * @param smsOtp the otp to be send in sms.
	 * @throws SmsServiceException if any exception occurs when sending the sms.
	 */
	@Override
	public void sendFirstTimeSmsToUser(User user, String smsOtp) throws SmsServiceException {
		logger.debug("inside sendFirstTimeSmsToUser of TextlocalSmsServiceImpl, :: " + Thread.currentThread().getName());
		try {
			String apiKey = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_API_KEY);
			String message = SmsServiceConstant.FIRST_TIME_REG_SMS.replace(SmsServiceConstant.USER_NAME_$, user.getFirstName());
			message = message.replace(SmsServiceConstant.SMS_OTP_$, smsOtp);
			String mess = "Hi there, thank you for sending your first test message from Textlocal. See how you can send effective SMS campaigns here: https://tx.gl/r/2nGVj/";
			String encode = URLEncoder.encode(mess, SmsServiceConstant.UTF);
//			logger.debug("URL encoded data : "+encode);
			if(message.length() > 160) {
				logger.warn("The message is exceeding its 160 length, Message is :"+StringEscapeUtils.escapeJava(message));
			}
			String sender = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_SENDER);
			String mobileNumber = user.getPhone();
			String data = SmsServiceConstant.TEXT_LOCAL_URL_API_KEY + apiKey + SmsServiceConstant.TEXT_LOCAL_URL_NUMBER
					+ mobileNumber + SmsServiceConstant.TEXT_LOCAL_URL_MSG + encode;			
			if (sender != null) {
				data = SmsServiceConstant.TEXT_LOCAL_URL_SENDER + sender;
			}
			if(sendMsg(data)) {
				logger.debug("SMS sent successfully to : "+StringEscapeUtils.escapeJava(user.toString()));
			} else {
				logger.debug("SMS sent Failed to : "+StringEscapeUtils.escapeJava(user.toString()));
			}
			
		} catch (Exception e) {
			logger.error("Unable to send first time sms to user, ", e);
			throw new SmsServiceException("Unable to send first time sms to user, ", e);
		}
		
	}
	
	/**
	 * When the user is requesting OTP for Forgot Password (OR) Reset password service, this sms method has to be called.
	 * @param smsOtp The user's SMS OTP
	 * @param user User Details (name, phone)
	 */
	@Override
	public void sendResetPasswordSmsToUser(User user, String smsOtp) throws SmsServiceException {
		logger.debug("inside sendResetPasswordSmsToUser of TextlocalSmsServiceImpl, :: " + Thread.currentThread().getName());
		try {
			String apiKey = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_API_KEY);
			String message = SmsServiceConstant.FORGOT_PWD_SMS.replace(SmsServiceConstant.USER_NAME_$, user.getFirstName());
			message = message.replace(SmsServiceConstant.SMS_OTP_$, smsOtp);
			if(message.length() > 160) {
				logger.warn("The message is exceeding its 160 length, Message is :"+StringEscapeUtils.escapeJava(message));
			}
			String sender = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_SENDER);
			String mobileNumber = user.getPhone();
			String data = SmsServiceConstant.TEXT_LOCAL_URL_API_KEY + apiKey + SmsServiceConstant.TEXT_LOCAL_URL_NUMBER
					+ mobileNumber + SmsServiceConstant.TEXT_LOCAL_URL_MSG + message;			
			if (sender != null) {
				data = SmsServiceConstant.TEXT_LOCAL_URL_SENDER + sender;
			}
			if(sendMsg(data)) {
				logger.debug("SMS sent successfully to : "+StringEscapeUtils.escapeJava(user.toString()));
			} else {
				logger.debug("SMS sent Failed to : "+StringEscapeUtils.escapeJava(user.toString()));
			}
			logger.debug("SMS sent successfully to : "+StringEscapeUtils.escapeJava(user.toString()));
		} catch (Exception e) {
			logger.error("Unable to send first time sms to user, ", e);
			throw new SmsServiceException("Unable to send first time sms to user, ", e);
		}
		
	}
	
	/**
	 * This method is used to send the otp to the user when submitting response.
	 * @param user the user details.
	 * @param smsOtp the otp to be send in sms.
	 * @throws SmsServiceException if any exception occurs when sending the sms.
	 */
	@Override
	public void sendOtpFieldAuditorSubmission(User user,String smsOtp) throws SmsServiceException {
		logger.debug("inside sendOtpFieldAuditorSubmission of TextlocalSmsServiceImpl, :: " + Thread.currentThread().getName());
		try {
			String apiKey = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_API_KEY);
			String message = SmsServiceConstant.FA_SUMBIT_SMS.replace(SmsServiceConstant.USER_NAME_$, user.getFirstName());
			message = message.replace(SmsServiceConstant.SMS_OTP_$, smsOtp);
			if(message.length() > 160) {
				logger.warn("The message is exceeding its 160 length, Message is :"+StringEscapeUtils.escapeJava(message));
			}
			String sender = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_SENDER);
			String mobileNumber = user.getPhone();
			String data = SmsServiceConstant.TEXT_LOCAL_URL_API_KEY + apiKey + SmsServiceConstant.TEXT_LOCAL_URL_NUMBER
					+ mobileNumber + SmsServiceConstant.TEXT_LOCAL_URL_MSG + message;			
			if (sender != null) {
				data = SmsServiceConstant.TEXT_LOCAL_URL_SENDER + sender;
			}
			if(sendMsg(data)) {
				logger.debug("SMS sent successfully to : "+StringEscapeUtils.escapeJava(user.toString()));
			} else {
				logger.debug("SMS sent Failed to : "+StringEscapeUtils.escapeJava(user.toString()));
			}
			logger.debug("SMS sent successfully to : "+StringEscapeUtils.escapeJava(user.toString()));
		} catch (Exception e) {
			logger.error("Unable to send first time sms to user, ", e);
			throw new SmsServiceException("Unable to send first time sms to user, ", e);
		}
		
	}
	
	/**
	 * This method is used to send the otp to the user when submitting response.
	 * @param user the user details.
	 * @param smsOtp the otp to be send in sms.
	 * @throws SmsServiceException if any exception occurs when sending the sms.
	 */
	@Override
	public void sendOtpSelfAssesmentSubmission(User user ,String smsOtp) throws SmsServiceException {
		logger.debug("inside sendOtpSelfAssesmentSubmission of TextlocalSmsServiceImpl, :: " + Thread.currentThread().getName());
		try {
			String apiKey = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_API_KEY);
			String message = SmsServiceConstant.CIC_CS_SUBMIT_SMS.replace(SmsServiceConstant.USER_NAME_$, user.getFirstName());
			message = message.replace(SmsServiceConstant.SMS_OTP_$, smsOtp);
			if(message.length() > 160) {
				logger.warn("The message is exceeding its 160 length, Message is :"+StringEscapeUtils.escapeJava(message));
			}
			String sender = ReadPropertiesFile.readRequestProperty(SmsServiceConstant.TEXT_LOCAL_SENDER);
			String mobileNumber = user.getPhone();
			String data = SmsServiceConstant.TEXT_LOCAL_URL_API_KEY + apiKey + SmsServiceConstant.TEXT_LOCAL_URL_NUMBER
					+ mobileNumber + SmsServiceConstant.TEXT_LOCAL_URL_MSG + message;			
			if (sender != null) {
				data = SmsServiceConstant.TEXT_LOCAL_URL_SENDER + sender;
			}
			sendMsg(data);
			logger.debug("SMS sent successfully to : "+StringEscapeUtils.escapeJava(user.toString()));
		} catch (Exception e) {
			logger.error("Unable to send first time sms to user, ", e);
			throw new SmsServiceException("Unable to send first time sms to user, ", e);
		}
		
	}

}
