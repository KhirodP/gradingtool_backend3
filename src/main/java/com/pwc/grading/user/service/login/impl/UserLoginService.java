package com.pwc.grading.user.service.login.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.GradingToolApplicationConstant;
import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.mail.service.IMailService;
import com.pwc.grading.tracking.dao.ITrackingDao;
import com.pwc.grading.user.dao.IUserDao;
import com.pwc.grading.user.model.ForgotPasswordUserOtp;
import com.pwc.grading.user.model.User;
import com.pwc.grading.user.model.UserOtp;
import com.pwc.grading.user.service.login.IUserLoginService;
import com.pwc.grading.user.service.login.UserLoginServiceConstant;
import com.pwc.grading.user.service.login.exception.UserLoginServiceException;
import com.pwc.grading.user.service.registration.UserAccessManagementServiceConstants;
import com.pwc.grading.user.util.UserUtil;
import com.pwc.grading.user.verification.dao.IUserVerificationDAO;
import com.pwc.grading.user.verification.model.UserVerification;
import com.pwc.grading.usertoken.dao.IUserTokenDao;
import com.pwc.grading.usertoken.model.TokenPayload;
import com.pwc.grading.usertoken.model.UserToken;
import com.pwc.grading.util.JsonUtill;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.TokenBuilder;

/**
 * Implementation class for {@link IUserLoginService}
 *
 */
@Singleton
public class UserLoginService implements IUserLoginService {

	private static final Logger logger = LoggerFactory.getLogger(UserLoginService.class);
	@Inject
	private IUserDao userDao;
	@Inject
	private TokenBuilder tokenBuilder;
	@Inject
	private IUserTokenDao userTokenDao;
//	@Inject
//	private ISmsService smsService;
	@Inject
	private IMailService mailService;
	@Inject
	private ITrackingDao iLogTrackingDao;
	@Inject
	private IUserVerificationDAO userVerificationDao;

	/**
	 * This method is used to process the login of the user.
	 * @param requestJson the json containing the user credentials.
	 * @param tenantId the encoded tenantId
	 * @return the response if login is success.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String loginUser(String requestString, String encodedtenantId) throws UserLoginServiceException {
		logger.debug(" inside loginUser method of user login service");
		try {
			if (encodedtenantId == null || encodedtenantId.isEmpty()) {
				throw new UserLoginServiceException("tenant can't be empty or null");
			}
			byte[] decodedTenantId = Base64.getDecoder().decode(encodedtenantId.trim());
			String tenantId = new String(decodedTenantId);
			String databaseName = ReadPropertiesFile.readTenantProperty(tenantId);
			JSONObject requestJSON = (JSONObject) JSONValue.parse(requestString);
			String email = JsonUtill.getString(requestJSON, UserLoginServiceConstant.EMAIL_ID).trim();
			String proctedPassword = JsonUtill.getString(requestJSON, UserLoginServiceConstant.PWD);
			logger.debug(" user email :" + email + " password : " + proctedPassword + " tenantId :: " + tenantId);
			User user = userDao.getUserByEmail(databaseName, email);
			if (user == null) {
				logger.debug("User does not Exist.");
				throw new UserLoginServiceException("User does not Exist.");
			}
			String userStatus = user.getStatus();
			String userPasswordEnc = user.getPassword();

			if (userPasswordEnc == null && userStatus.equals(UserAccessManagementServiceConstants.USER_UN_VERIFIED)) {
				logger.debug("===> Processing LOGIN OF UN_VERIFIED USER...");
				logger.debug("userStatus: " + userStatus);
				logger.debug("userPasswordEnc: " + userPasswordEnc);
				logger.debug("Creating one time access token for the user.");
				UserVerification userVerification = userVerificationDao.getUserVerificationByUserId(databaseName,
						user.getUserId());
				logger.debug("fdfdsfsdfd " + userVerification);
				if (!(userVerification.getTempPassword().equals(proctedPassword))) {
					throw new UserLoginServiceException("Invalid password");
				}
				String accessToken = tokenBuilder.buildOneTimeAccessToken(user, databaseName);
				JSONObject reponsejson = new JSONObject();
				reponsejson.put(UserLoginServiceConstant.USER_ROLE, user.getUserRole());
				reponsejson.put(UserLoginServiceConstant.ONE_TIME_ACCESS_TOKEN, accessToken);
				reponsejson.put(UserLoginServiceConstant.STATUS, UserAccessManagementServiceConstants.USER_UN_VERIFIED);
				return reponsejson.toString();
			} else if (userStatus.equals(UserAccessManagementServiceConstants.USER_ACTIVE)) {
				logger.debug("===> Processing LOGIN OF VERIFIED USER...");
				logger.debug(" user  :: " + user.toString());
//				String proctedPassword = UserSecurityProtecer.protectUserData(password);
				logger.debug(" proctedPassword :: " + proctedPassword);
				if (user.getPassword().equals(proctedPassword)) {
					logger.debug(" user password is correct ..");
					JSONObject reponsejson = new JSONObject();
					String accessToken = tokenBuilder.buildAccessToken(user, requestJSON, databaseName);
					String jwtToken = tokenBuilder.buildJwtToken(user, accessToken, tenantId);
					reponsejson.put(UserLoginServiceConstant.ACCESS_TOKEN, accessToken);
					reponsejson.put(UserLoginServiceConstant.JWT_TOKEN, jwtToken);
					reponsejson.put(UserLoginServiceConstant.USER_FIRST_NAME, user.getFirstName());
					reponsejson.put(UserLoginServiceConstant.USER_LAST_NAME, user.getLastName());
					reponsejson.put(UserLoginServiceConstant.USER_ROLE, user.getUserRole());
					reponsejson.put(UserLoginServiceConstant.STATUS, UserLoginServiceConstant.VERIFIED);
					return reponsejson.toString();

				} else {
					logger.error("password incorrect");
					throw new UserLoginServiceException(" password incorrect ");
				}
			} else if (userStatus.equals(UserAccessManagementServiceConstants.USER_DISABLE)) {
				logger.debug("===> Processing LOGIN OF DISABLED USER...");
				throw new UserLoginServiceException("User account is disabled.");
			} else {
				throw new UserLoginServiceException("Invalid userStatus");
			}

		} catch (UserLoginServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to login user : " + e.getMessage(), e);
			throw new UserLoginServiceException(e.getMessage(), e);
		}

	}

	/**
	 * This method is used to reset the password of the user.
	 * @param requestBody the json containing the password details.
	 * @param tenantId the database name.
	 * @param userEmail  the user email id.
	 * @return the response if password reset is success.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String resetPassword(String requestBody, String tenantId, String email) throws UserLoginServiceException {

		try {
			JSONObject requestJSON = (JSONObject) JSONValue.parse(requestBody);
			String proctedOldPassword = JsonUtill.getString(requestJSON, UserLoginServiceConstant.OLD_PWD);
			String proctedNewPassword = JsonUtill.getString(requestJSON, UserLoginServiceConstant.NEW_PWD);
			String proctedConfirmPassword = JsonUtill.getString(requestJSON, UserLoginServiceConstant.CONFIRM_PWD);
			if (!(proctedNewPassword.equals(proctedConfirmPassword))) {
				throw new UserLoginServiceException("new password and confirm password doesn't matches");
			}
			User user = userDao.getUserByEmail(tenantId, email);
			if (user == null) {
				throw new UserLoginServiceException("user with given email Id does not exist");
			}
			logger.debug("user exist");
			// User status check
			boolean userActive = UserUtil.isUserActive(user);
			if (!userActive) {
				throw new UserLoginServiceException("User account is disabled");
			} // End of User status check.
			String password = user.getPassword();
			if (password.equalsIgnoreCase(proctedNewPassword)) {
				throw new UserLoginServiceException("current Password and new password cant be same");
			}
			if (password.equals(proctedOldPassword)) {
				// update un verified user here
				userDao.updateUserPasswordByUserId(tenantId, user.getUserId(), proctedNewPassword);
				JSONObject reponsejson = new JSONObject();
				reponsejson.put(UserLoginServiceConstant.SUCCESS_MSG, "password reset successfully");
				return reponsejson.toString();
			} else {
				logger.error("old password doesnt match with current password");
				throw new UserLoginServiceException("old password and new password doesnt match");
			}
		} catch (Exception e) {
			logger.error("unable to reset password " + e.getMessage(), e);
			throw new UserLoginServiceException("unable to reset password " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to process the login of the user using the accessToken.
	 * @param requestJson  the json containing the user accessToken.
	 * @param tenantId the encoded tenantId
	 * @return  the response if login is success.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String loginWithAccessToken(String requestJson, String encodedtenantId) throws UserLoginServiceException {
		logger.debug("inside loginWithAccessToken method of UserLoginService ");
		try {
			if (encodedtenantId == null || encodedtenantId.isEmpty()) {
				throw new UserLoginServiceException("tenant can't be empty or null");
			}
			byte[] decodedTenantId = Base64.getDecoder().decode(encodedtenantId);
			String keyTenantId = new String(decodedTenantId);
			String tenantId = ReadPropertiesFile.readTenantProperty(keyTenantId);
			logger.debug("tenantId is " + tenantId);
			JSONObject requestJSON = (JSONObject) JSONValue.parseWithException(requestJson);
			String accessToken = JsonUtill.getString(requestJSON, GradingToolApplicationConstant.ACCESS_TOKEN);
			logger.debug(" request access Token :: " + accessToken);
			UserToken userToken = userTokenDao.getUserTokenByAccessToken(accessToken, tenantId);
			if (userToken == null) {
				throw new UserLoginServiceException("invalid access token");
			}
			long expTime = userToken.getExpiryTime();
			long currentTime = new Date().getTime();
			logger.info("expTime :: " + expTime + " currentTime ::" + currentTime);
			if (currentTime <= expTime) {
				logger.info(" access Token is valid ");
				String userId = userToken.getUserId();
				User user = userDao.getUserByUserId(tenantId, userId);
				if (user != null) {
					// User status check
					boolean userActive = UserUtil.isUserActive(user);
					if (!userActive) {
						throw new UserLoginServiceException("User account is disabled");
					} // End of User status check.
					String jwtToken = tokenBuilder.buildJwtToken(user, accessToken, keyTenantId);
					JSONObject responseObject = new JSONObject();
					responseObject.put(UserLoginServiceConstant.JWT_TOKEN, jwtToken);
					responseObject.put(UserLoginServiceConstant.USER_FIRST_NAME, user.getFirstName());
					responseObject.put(UserLoginServiceConstant.USER_LAST_NAME, user.getLastName());
					responseObject.put(UserLoginServiceConstant.USER_ROLE, user.getUserRole());
					return responseObject.toString();
				} else {
					throw new UserLoginServiceException("invalid user account ");
				}

			} else {
				throw new UserLoginServiceException("access token expired ");
			}
		} catch (UserLoginServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to login user : " + e.getMessage(), e);
			throw new UserLoginServiceException(e.getMessage(), e);
		}

	}

	/**
	 * This method is used to send the otp to the user.
	 * @param userEmail the user email id.
	 * @param tenantId the database name.
	 * @return the success response after otp is send.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String sendOtp(String userEmail, String tenantId) throws UserLoginServiceException {
		logger.debug(" inside sendOtp of loginService ");
		try {
			logger.debug(" userEmail :: " + userEmail);
			User user = userDao.getUserByEmail(tenantId, userEmail);
			if (user == null) {
				throw new UserLoginServiceException("unable to find user data");
			}
			logger.debug("user ::" + user.toString());
//			String otp = String.valueOf(UUID.randomUUID()).substring(0, 6);
//			smsService.sendOtp(user, otp);
			String otp = "654321";
			logger.debug(" user Otp :: " + otp);
			Integer expirty = getExpirty();
			logger.debug("expirty :: " + expirty);
			long expirtyTime = getExpritionTime(expirty);
			UserOtp userOtp = new UserOtp(otp, expirtyTime);
			userDao.updateUserOtpByEmail(tenantId, userEmail, userOtp);
			JSONObject reponseMessgae = new JSONObject();
			reponseMessgae.put(UserLoginServiceConstant.MESSGAE, ReadPropertiesFile.readResponseProperty("220"));
			JSONObject response = new JSONObject();
			response.put(UserLoginServiceConstant.RESPONSE, reponseMessgae);
			return response.toString();
		} catch (UserLoginServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to send otp to user : " + e.getMessage(), e);
			throw new UserLoginServiceException(e.getMessage(), e);
		}

	}

	/**
	 * This method is used to verify the otp of the user.
	 * @param userEmail the user email id.
	 * @param tenantId the database name.
	 * @param otp the otp entered by the user.
	 * @return the success response after otp is verified.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String verifyOtp(String userEmail, String otp, String tenantId) throws UserLoginServiceException {
		try {
			if (otp == null || otp.isEmpty()) {
				throw new UserLoginServiceException(" otp can't be null or empty");
			}
			logger.debug(" inside verifyOtp ..");
			boolean isOtpValid = false;
			UserOtp userOtp = userDao.getUserOtpByEmail(tenantId, userEmail);
			String validOtp = userOtp.getUserOtp();
			if (validOtp != null) {
				if (validOtp.equals(otp)) {
					logger.info(" user otp is valid checking expiry ");
					long expiry = userOtp.getExpiryDate();
					long currentTime = new Date().getTime();
					if (currentTime <= expiry) {
						logger.info(" otp is valid successfully...");
						isOtpValid = true;
					}
				}
			}
			if (!isOtpValid) {
				throw new UserLoginServiceException(" invalid or otp expire");
			}

		} catch (UserLoginServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to verify otp : " + e.getMessage(), e);
			throw new UserLoginServiceException(e.getMessage(), e);
		}
		JSONObject reponseMessgae = new JSONObject();
		reponseMessgae.put(UserLoginServiceConstant.MESSGAE, ReadPropertiesFile.readResponseProperty("221"));
		JSONObject response = new JSONObject();
		response.put(UserLoginServiceConstant.RESPONSE, reponseMessgae);
		return response.toString();
	}

	/**
	 * This method is used to process the logout of the user and deleting the userToken.
	 * @param tenanId the database name.
	 * @param userEmail the user email id.
	 * @param sessionId the session id.
	 * @param requestJson the json containing the deviceId.
	 * @return the response if logout is success.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String logoutUser(String tenanId, String userEmail, String sessionId, String requestJson)
			throws UserLoginServiceException {
		try {
			logger.debug(" inside logoutUser service :: ");
			if (userEmail == null || userEmail.isEmpty()) {
				throw new UserLoginServiceException(" Invalid user Email");
			}
			logger.debug("requestJson ::" + requestJson);
			JSONObject requetsJSON = (JSONObject) JSONValue.parse(requestJson);
			String deviceId = JsonUtill.getString(requetsJSON, UserLoginServiceConstant.DEVICE_ID).trim();
			User user = userDao.getUserByEmail(tenanId, userEmail);
			userTokenDao.deleteUserTokenByUserId(deviceId, user.getUserId(), tenanId);
			iLogTrackingDao.updateLogTrackingBySessionId(tenanId, sessionId, new Date().getTime());
			JSONObject reponseMessgae = new JSONObject();
			reponseMessgae.put(UserLoginServiceConstant.MESSGAE, ReadPropertiesFile.readResponseProperty("222"));
			JSONObject response = new JSONObject();
			response.put(UserLoginServiceConstant.RESPONSE, reponseMessgae);
			return response.toString();
		} catch (Exception e) {
			logger.error(" unable to logout user : " + e.getMessage(), e);
			throw new UserLoginServiceException(e.getMessage(), e);
		}

	}

	/**
	 * Get expiration time in long for given minutes.
	 * @param minutes to be converted into long.
	 * @return long minutes.
	 */
	private long getExpritionTime(Integer minutes) {
		logger.debug(".inside getDaysFromString ");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minutes);
		long expTime = calendar.getTimeInMillis();
		return expTime;
	}

	/**
	 * Read the expiry time from the external properties file.
	 * @return the expiry time.
	 */
	private int getExpirty() {
		Integer minutes = 4;
		try {
			String expirty = ReadPropertiesFile.readRequestProperty(UserLoginServiceConstant.OTP_EXPIRY);
			minutes = Integer.parseInt(expirty);
		} catch (Exception e) {
		}
		return minutes;
	}

	/**
	 * Read the expiry time for forgot password from the external properties file.
	 * @return the expiry time.
	 */
	private int getExpirtyForForgetPassword() {
		Integer minutes = 4;
		try {
			String expirty = ReadPropertiesFile
					.readRequestProperty(UserLoginServiceConstant.FORGET_PWD_OTP_EXPIRY);
			minutes = Integer.parseInt(expirty);
		} catch (Exception e) {
		}
		return minutes;
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public String forgetPasswordCreateNewPassword(String requestBody, String tenantId, String userEmail)
//			throws UserLoginServiceException {
//		try {
//			JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(requestBody);
//			String otp = JsonUtill.getString(jsonObject, UserLoginServiceConstant.OTP);
//			String password = JsonUtill.getString(jsonObject, UserLoginServiceConstant.PASSWORD);
//			UserOtp userOtp = userDao.getUserForgetPasswordOtpByEmail(tenantId, userEmail);
//			String validOtp = userOtp.getUserOtp();
//			boolean isOtpValid = false;
//			if (validOtp != null) {
//				if (validOtp.equals(otp)) {
//					logger.info(" user otp is valid checking expiry ");
//					long expiry = userOtp.getExpiryDate();
//					long currentTime = new Date().getTime();
//					if (currentTime <= expiry) {
//						logger.info(" otp is valid successfully...");
//						isOtpValid = true;
//					}
//				}
//			}
//			if (!isOtpValid) {
//				logger.error("invalid or otp expire ");
//				throw new UserLoginServiceException(" invalid or otp expire");
//			}
//			User user = userDao.getUserByEmail(tenantId, userEmail);
//			userDao.updateUserPasswordByUserId(tenantId, user.getUserId(), password);
////			userDao.updateUserForgetPasswordOtpByEmail(tenantId, userEmail, new UserOtp());
////			userDao.updateUserForgetPasswordOtpsByEmail(tenantId, emailId, userOtp);
//			JSONObject jsonResponse = new JSONObject();
//			jsonResponse.put("status", "password updated successfully");
//			return jsonResponse.toJSONString();
//		} catch (Exception e) {
//			logger.error(" unable to send otp to user " + e.getMessage(), e);
//			throw new UserLoginServiceException("unable to send otp to user " + e.getMessage(), e);
//		}
//	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public String forgetPasswordSendOtp(String requestBody, String encodedtenantId) throws UserLoginServiceException {
//		JSONObject jsonResponse = new JSONObject();
//		try {
//			if (encodedtenantId == null || encodedtenantId.isEmpty()) {
//				throw new UserLoginServiceException("tenant can't be empty or null");
//			}
//			byte[] decodedTenantId = Base64.getDecoder().decode(encodedtenantId.trim());
//			String databaseName = new String(decodedTenantId);
//			String tenantId = ReadPropertiesFile.readTenantProperty(databaseName);
//			JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(requestBody);
//			String emailId = JsonUtill.getString(jsonObject, UserLoginServiceConstant.EMAIL_ID);
//			User user = userDao.getUserByEmail(tenantId, emailId);
//			if (user == null) {
//				logger.error(" unable to send otp to user , user does not exist ");
//				throw new UserLoginServiceException("unable to send otp to user , user does not exist");
//			}
//			String tempPassword = UUID.randomUUID().toString().substring(0, 6);
//			Integer expirty = getExpirtyForForgetPassword();
//			logger.debug("expirty :: " + expirty);
//			long expirtyTime = getExpritionTime(expirty);
//			UserOtp userOtp = new UserOtp(tempPassword, expirtyTime);
//			userDao.updateUserForgetPasswordOtpByEmail(tenantId, emailId, userOtp);
////			mailService.sendTempPasswordToUser(emailId, tempPassword);
//			String accessToken = tokenBuilder.buildAccessToken(user, jsonObject, tenantId);
//			String jwtToken = tokenBuilder.buildJwtTokenForForgetPassword(user, accessToken, databaseName);
//
//			jsonResponse.put(UserLoginServiceConstant.JWT_TOKEN, jwtToken);
//		} catch (Exception e) {
//			logger.error(" unable to send otp to user " + e.getMessage(), e);
//			throw new UserLoginServiceException("unable to send otp to user " + e.getMessage(), e);
//		}
//		return jsonResponse.toJSONString();
//	}
	
	/**
	 * This method is used to send the Otps to the user when user is using forgot password service.
	 * @param requestBody the json containing the user email Id.
	 * @param tenantId  the database name.
	 * @return the response having oneTimeAccessToken in it.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String forgetPasswordSendOtp(String requestBody, String encodedtenantId, String encryptedPlatform) throws UserLoginServiceException {
		JSONObject jsonResponse = new JSONObject();
		validatePlatform(encryptedPlatform);
		try {
			if (encodedtenantId == null || encodedtenantId.isEmpty()) {
				throw new UserLoginServiceException("tenant can't be empty or null");
			}
			byte[] decodedTenantId = Base64.getDecoder().decode(encodedtenantId.trim());
			String databaseName = new String(decodedTenantId);
			String tenantId = ReadPropertiesFile.readTenantProperty(databaseName);
			JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(requestBody);
			String emailId = JsonUtill.getString(jsonObject, UserLoginServiceConstant.EMAIL_ID);
			User user = userDao.getUserByEmail(tenantId, emailId);
			if (user == null) {
				logger.error("User does not exist ");
				throw new UserLoginServiceException("User does not exist");
			}
			if (user.getStatus().equals(UserLoginServiceConstant.UN_VERIFIED)) {
				logger.error("Un-Verified user using Forget Password service..");
				throw new UserLoginServiceException("Un-Verified user cannot have access to this service.");
			}
			// User status check
			boolean userActive = UserUtil.isUserActive(user);
			if (!userActive) {
				throw new UserLoginServiceException("User account is disabled");
			} // End of User status check.
			
			checkuserRoleConditions(user,encryptedPlatform);
//			String emailOtp = "654321"; // generateRandomChars method have to be called here
			String emailOtp = generateRandomChars(UserAccessManagementServiceConstants.EMAIL_OTP_LENGTH);
			String smsOtp = "654321";
			Integer expirty = getExpirtyForForgetPassword();
			logger.debug("expirty :: " + expirty);
			long expirtyTime = getExpritionTime(expirty);
			
			//Email service enabled.
			mailService.sendForgetPasswordMailToUser(user.getEmail(), user.getFirstName(), emailOtp);
			
			ForgotPasswordUserOtp userOtp = new ForgotPasswordUserOtp(emailOtp, smsOtp, expirtyTime);
			userDao.updateUserForgetPasswordOtpsByEmail(tenantId, emailId, userOtp);

			String accessToken = tokenBuilder.buildAccessTokenForForgotPasswordService(user, jsonObject, tenantId);
			jsonResponse.put(UserLoginServiceConstant.ONE_TIME_ACCESS_TOKEN, accessToken);
			jsonResponse.put(UserLoginServiceConstant.STATUS, UserLoginServiceConstant.VERIFIED);
		} catch (Exception e) {
			logger.error(" unable to send otp to user " + e.getMessage(), e);
			throw new UserLoginServiceException("unable to send otp to user, " + e.getMessage(), e);
		}
		return jsonResponse.toJSONString();
	}

	private void validatePlatform(String encryptedPlatform) throws UserLoginServiceException {
		logger.debug("inside validatePlatform method..");
		if(encryptedPlatform.equals(UserLoginServiceConstant.PLATFORM_WEB_SHA_256)) {
			logger.debug("Platform is web-UI");
		}else if (encryptedPlatform.equals(UserLoginServiceConstant.PLATFORM_MOBILE_SHA_256)) {
			logger.debug("Platform is Mobile");
		}
		else {
			logger.debug("Invalid Platform Type");
			throw new UserLoginServiceException("Invalid Platform type.");
		}
		
	}

	private void checkuserRoleConditions(User user, String encryptedPlatform) throws UserLoginServiceException {
		logger.debug("inside checkuserRoleConditions method..");
		String userRole = user.getUserRole();
		if(userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
			logger.debug("inside Admin If.");
			if(!encryptedPlatform.equals(UserLoginServiceConstant.PLATFORM_WEB_SHA_256)) {
				throw new UserLoginServiceException("User does not have access to this platform.");
			}
		}
		
		if(userRole.equals(UserAccessManagementServiceConstants.FIELD_AUDITOR) ||
				userRole.equals(UserAccessManagementServiceConstants.CLIENT_SPONSOR) ||
				userRole.equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
			logger.debug("inside FA,CS,CIC If.");
			if(!encryptedPlatform.equals(UserLoginServiceConstant.PLATFORM_MOBILE_SHA_256)) {
				throw new UserLoginServiceException("User does not have access to this platform.");
			}
		}
		
	}

	/**
	 * This method is used to verify the user otps for the forgot password.
	 * @param tenantId  the database name.
	 * @param requestJson  the json containing the Otps.
	 * @param oneTimeAccessToken  the access token provided to the user in the response.
	 * @return the response message if operation is success.
	 * @throws UserLoginServiceException  if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String verifyOtpforForgotPassword(String encodedtenantId, String requestJson, String oneTimeAccessToken)
			throws UserLoginServiceException {
		logger.debug("inside verifyOtpforForgotPassword method of UserLoginService.");
		if (encodedtenantId == null || encodedtenantId.isEmpty()) {
			throw new UserLoginServiceException("tenant can't be empty or null");
		}
		byte[] decodedTenantId = Base64.getDecoder().decode(encodedtenantId.trim());
		String databaseName = new String(decodedTenantId);
		String tenantId = ReadPropertiesFile.readTenantProperty(databaseName);
		try {
			JSONObject requestJsonObject = (JSONObject) JSONValue.parseWithException(requestJson);
			String email = JsonUtill.getString(requestJsonObject, UserLoginServiceConstant.USER_EMAIL);
			String emailOtp = JsonUtill.getString(requestJsonObject, UserLoginServiceConstant.EMAIL_OTP);
			String smsOtp = JsonUtill.getString(requestJsonObject, UserLoginServiceConstant.SMS_OTP);
			ForgotPasswordUserOtp userOtp = userDao.getUserForgetPasswordOtpByEmail(tenantId, email);
			String validEmailOtp = userOtp.getEmailOtp();
			String validSmsOtp = userOtp.getSmsOtp();
			UserToken userToken = userTokenDao.getUserTokenByAccessToken(oneTimeAccessToken, tenantId);
			if (userToken == null) {
				logger.error("User token entry is not there.");
				throw new UserLoginServiceException("Invalid access token.");
			}
			User user = userDao.getUserByEmail(tenantId, email);
			if (!(user.getUserId().equals(userToken.getUserId()))) {
				logger.error("User ID mismatch for accessToken and userByEmail.");
				throw new UserLoginServiceException("Invalid access token for the user.");
			}
			long expiryTime = userToken.getExpiryTime();
			long currentTime1 = new Date().getTime();
			if (currentTime1 > expiryTime) {
				logger.error("Access token is expired.");
				throw new UserLoginServiceException("Access token is expired.");
			}
			if (validEmailOtp != null && validSmsOtp != null) {
				if (validEmailOtp.equals(emailOtp) && validSmsOtp.equals(smsOtp)) {
					logger.info("Otps are valid, Checking expiry ");
					long expiry = userOtp.getExpiryTime();
					long currentTime = new Date().getTime();
					if (currentTime <= expiry) {
						logger.info("Email and SMS otp is validated successfully...");
						// isOtpValid = true;
					} else {
						logger.error("The Otp is expired.");
						throw new UserLoginServiceException("The Otp is expired.");
					}
				} else {
					logger.error("The Otp entered is Invalid.");
					throw new UserLoginServiceException("The Otp entered is Invalid.");
				}
			} else {
				logger.error("The user has no OTP entries.");
				throw new UserLoginServiceException("Access to this service is denined.[No OTPs]");
			}
			String jwtToken = tokenBuilder.buildOneTimeJwtToken(user, oneTimeAccessToken, databaseName);

			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put(UserLoginServiceConstant.JWT_TOKEN, jwtToken);
			jsonResponse.put(UserLoginServiceConstant.VERIFICATION_STATUS, true);
			return jsonResponse.toJSONString();

		} catch (Exception e) {
			logger.error(" unable to Verify otp for user " + e.getMessage(), e);
			throw new UserLoginServiceException("unable to Verify otp for user, " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to create the new password after the user provided the Otps for forgot password.
	 * @param tenantId  the database name.
	 * @param requestBody the json containing the password.
	 * @param oneTimeAccessToken  the access token provided to the user in the response.
	 * @param tokenPayload the token and user details.
	 * @return the response message if operation is success.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String forgetPasswordCreateNewPassword(String tenantId, String requestBody, String oneTimeAccessToken,
			TokenPayload tokenPayload) throws UserLoginServiceException {
		Connection connection = null;
		try {
			JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(requestBody);
			String password = JsonUtill.getString(jsonObject, UserLoginServiceConstant.PWD); //This will come in encrypted format, no need to encrypt in backend
			if (password == null || password.isEmpty()) {
				throw new UserLoginServiceException("Password Cannot be empty.");
			}
			String userEmail = tokenPayload.getUserEmail();
			boolean verificationDone = tokenPayload.getVerification();
			if (!verificationDone) {
				logger.debug("Verification has to be done before creating the password..");
				throw new UserLoginServiceException("Verification has to be done before creating the password..");
			}
			User userByEmail = userDao.getUserByEmail(tenantId, userEmail);
			if (userByEmail == null) {
				throw new UserLoginServiceException("User is not found.");
			}
			UserToken userToken = userTokenDao.getUserTokenByAccessToken(oneTimeAccessToken, tenantId);
			if (userToken == null) {
				logger.error("User token entry is not there.");
				throw new UserLoginServiceException("Invalid access token.");
			}
			if (!(userByEmail.getUserId().equals(userToken.getUserId()))) {
				logger.error("User ID mismatch for accessToken and userByEmail.");
				throw new UserLoginServiceException("Invalid access token for the user.");
			}
			long expiryTime = userToken.getExpiryTime();
			long currentTime1 = new Date().getTime();
			if (currentTime1 > expiryTime) {
				logger.error("Access token is expired.");
				throw new UserLoginServiceException("Access token is expired.");
			}

//			if (password.equals(userByEmail.getPassword())) {
//				throw new UserLoginServiceException("Forget password cant be same as Old password");
//			}
			connection = MSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			userDao.updateUserPasswordByUserId(connection, tenantId, userByEmail.getUserId(), password);
			logger.debug("password updated.");
			userDao.updateUserForgetPasswordOtpsByEmail(connection, tenantId, userEmail, new ForgotPasswordUserOtp());
			logger.debug("forgot password Otps Removed.");
			userTokenDao.deleteUserTokenByUserId(connection, userByEmail.getUserId(), tenantId);
			connection.commit();
			logger.debug("User token entries belonging to that user-id is deleted.");
			JSONObject obj = new JSONObject();
			obj.put(UserLoginServiceConstant.STATUS, "success");
			obj.put(UserLoginServiceConstant.SUCCESS_MSG, "Password Updated Successfully");
			return obj.toString();
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			logger.error(" unable to update passsword to user " + e.getMessage(), e);
			throw new UserLoginServiceException(" unable to update passsword to user " + e.getMessage(), e);
		} finally {
			MSSqlServerUtill.close(null, connection);
		}
	}

	/**
	 * This method is used to verify the user for the first time.
	 * @param tenantId the database name.
	 * @param requestJson the json containing the Otps.
	 * @param oneTimeAccessToken the access token provided to the user in the response.
	 * @return the response message if operation is success.
	 * @throws UserLoginServiceException  if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String verifyUser(String encTenantId, String requestJson, String oneTimeAccessToken)
			throws UserLoginServiceException {
		logger.debug("inside verifyUser method of UserRegistrationService");
		try {
			byte[] decodedTenantId = Base64.getDecoder().decode(encTenantId.trim());
			String tenantId = new String(decodedTenantId);
			String databaseName = ReadPropertiesFile.readTenantProperty(tenantId);

			JSONObject requestJsonObject = (JSONObject) JSONValue.parse(requestJson);
			logger.debug("requestJsonObject: " + requestJsonObject.toString());
			String email = JsonUtill.getString(requestJsonObject, UserLoginServiceConstant.USER_EMAIL);
			String emailOtp = JsonUtill.getString(requestJsonObject, UserLoginServiceConstant.EMAIL_OTP);
			String smsOtp = JsonUtill.getString(requestJsonObject, UserLoginServiceConstant.SMS_OTP);

			logger.debug("Getting UserVerification details..");
			User userByEmail = userDao.getUserByEmail(databaseName, email);
			if (userByEmail == null) {
				throw new UserLoginServiceException("User is not found.");
			}
			String userId = userByEmail.getUserId();
			UserVerification userVerificationByUserId = userVerificationDao.getUserVerificationByUserId(databaseName,
					userId);
			if (userVerificationByUserId == null) {
				throw new UserLoginServiceException("User-Verification details is not found for userId: " + userId);
			} else {
				String storedAccessToken = userVerificationByUserId.getOneTimeAccessToken();
				if (storedAccessToken.equals(oneTimeAccessToken)) {
					long expiryTime = userVerificationByUserId.getExpiryTime();
					long currentTime = new Date().getTime();
					if (currentTime < expiryTime) {
						logger.debug("One time AccessToken matches, and VALID..");
						// Checking the credentials.
						if (emailOtp.equals(userVerificationByUserId.getEmailOtp())
								&& smsOtp.equals(userVerificationByUserId.getSmsOtp())) {
							logger.debug("Email otp and sms otp matches, Creating one time JWT Token.");
							String jwtToken = tokenBuilder.buildOneTimeJwtToken(userByEmail, storedAccessToken,
									tenantId);
							JSONObject responseJSON = new JSONObject();
							responseJSON.put(UserLoginServiceConstant.JWT_TOKEN, jwtToken);
							responseJSON.put(UserLoginServiceConstant.VERIFICATION_STATUS, true);
							return responseJSON.toString();
						} else {
							logger.error("Invalid Credentials Entered.");
							throw new UserLoginServiceException("The Otp entered is invalid.");
						}
					} else {
						logger.error("One time access token is expired.");
						throw new UserLoginServiceException("One time access token is expired.");
					}
				} else {
					logger.error("Invalid Access Token for verification.");
					throw new UserLoginServiceException("Invalid Access Token for verification.");
				}

			}
		} catch (Exception e) {
			logger.error("Unable to verify user, " + e.getMessage(), e);
			throw new UserLoginServiceException("Unable to verify user, " + e.getMessage(), e);
		}

	}

	/**
	 * This method is used to create password for the first time, after the user is verified in the first time login.
	 * @param requestJson the json containing the password.
	 * @param oneTimeAccessToken the access token provided to the user in the response.
	 * @param jwtToken the jwt token which is received in the response once the user verified the otps.
	 * @param tokenPayload the token and user details.
	 * @return the response message if operation is success.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String createPasswordforFirstTime(String tenantId, String requestJson, String oneTimeAccessToken,
			TokenPayload tokenPayload) throws UserLoginServiceException {
		logger.debug(".inside createPasswordforFirstTime of UserLoginService..");
		Connection connection = null;
		boolean passwordUpdated = false;
		try {
			JSONObject requestJsonObject = (JSONObject) JSONValue.parse(requestJson);
			String password = JsonUtill.getString(requestJsonObject, UserLoginServiceConstant.PWD);
			if (password == null || password.isEmpty()) {
				throw new UserLoginServiceException("Password Cannot be empty.");
			}
			String userEmail = tokenPayload.getUserEmail();
			boolean verificationDone = tokenPayload.getVerification();
			if (!verificationDone) {
				logger.debug("Verification has to be done before creating the password..");
				throw new UserLoginServiceException("Verification has to be done before creating the password..");
			}
			User userByEmail = userDao.getUserByEmail(tenantId, userEmail);
			if (userByEmail == null) {
				throw new UserLoginServiceException("User is not found.");
			}
			String userId = userByEmail.getUserId();
			UserVerification userVerificationByUserId = userVerificationDao.getUserVerificationByUserId(tenantId,
					userId);
			if (userVerificationByUserId == null) {
				throw new UserLoginServiceException("User-Verification details is not found for userId: " + userId);
			} else {
				String storedAccessToken = userVerificationByUserId.getOneTimeAccessToken();
				if (storedAccessToken.equals(oneTimeAccessToken)) {
					long expiryTime = userVerificationByUserId.getExpiryTime();
					long currentTime = new Date().getTime();
					if (currentTime < expiryTime) {
						logger.debug("One time AccessToken matches, and VALID..");
						connection = MSSqlServerUtill.getConnection();
						connection.setAutoCommit(false);
						// Updating user new password
						userDao.updateUserPasswordAndStatusByUserId(connection, tenantId, userId, password,
								UserAccessManagementServiceConstants.USER_ACTIVE);
						// Deleting user-verification entry.
						userVerificationDao.deleteUserVerificationByUserId(connection, tenantId, userId);
						connection.commit();
						passwordUpdated = true;
					} else {
						logger.error("One time access token is expired.");
						throw new UserLoginServiceException("One time access token is expired.");
					}
				} else {
					logger.error("Invalid Access Token for password creation.");
					throw new UserLoginServiceException("Invalid Access Token for password creation.");
				}

			}
			if (passwordUpdated) {
				logger.debug("password is updated, creating response.");
				JSONObject obj = new JSONObject();
				obj.put(UserLoginServiceConstant.STATUS, "success");
				obj.put(UserLoginServiceConstant.SUCCESS_MSG, "Password created Successfully");
				return obj.toString();
			}
		} catch (Exception e) {
			logger.error("Unable to create password for user, " + e.getMessage(), e);
			throw new UserLoginServiceException("Unable to create password for user, " + e.getMessage(), e);
		} finally {
			logger.debug("Closing connection for user password updation and user-verification deletion.");
			MSSqlServerUtill.close(null, connection);
		}
		return null;
	}

	/**
	 * Generate random alpha numeric characters for the given length.
	 * @param n length of the output.
	 * @return random generated character.
	 */
	private String generateRandomChars(int n) {
		String uuid = UUID.randomUUID().toString();
		String replacedUUID = uuid.replaceAll("-", "");
		String random = replacedUUID.substring(0, n);
		return random;
	}
}
