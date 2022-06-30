package com.pwc.grading.util;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.DatatypeConverter;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.GradingToolApplicationConstant;
import com.pwc.grading.tracking.dao.ITrackingDao;
import com.pwc.grading.user.model.User;
import com.pwc.grading.user.service.login.UserLoginServiceConstant;
import com.pwc.grading.user.verification.dao.IUserVerificationDAO;
import com.pwc.grading.user.verification.dao.exception.UserVerificationDAOException;
import com.pwc.grading.user.verification.model.UserVerification;
import com.pwc.grading.usertoken.dao.IUserTokenDao;
import com.pwc.grading.usertoken.dao.exception.UserTokenDaoException;
import com.pwc.grading.usertoken.model.UserLogTracking;
import com.pwc.grading.usertoken.model.UserToken;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Singleton
public class TokenBuilder {

	private static final Logger logger = LoggerFactory.getLogger(TokenBuilder.class);
	private static final SimpleDateFormat dateFromater = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	@Inject
	private IUserTokenDao userTokenDao;
	@Inject
	private ITrackingDao iLogTrackingDao;
	@Inject
	private IUserVerificationDAO userVerificationDAO;

	/**
	 * This method is used to build the access token.
	 * @param user the user details
	 * @param userJSON the json containing the details.
	 * @param tenantId the database name.
	 * @return the accessToken which is built.
	 * @throws UserTokenDaoException if any exception occurs when performing this operation.
	 */
	public String buildAccessToken(User user, JSONObject userJSON, String tenantId) throws UserTokenDaoException {
		logger.debug(" inside buildAccessToken of TokenBuilder");
		String userId = user.getUserId();
		String deviceId = getVlaueFromJSON(userJSON, UserLoginServiceConstant.DEVICE_ID);
		logger.debug(" userId :: " + userId + " deviceId :: " + deviceId);
		UserToken userToken = userTokenDao.getUserTokenByDeviceIdAndUserId(deviceId, userId, tenantId);
		String accessToken = buildAndStoreUserToken(userToken, user, userJSON, deviceId, tenantId);
		return accessToken;
	}

	/**
	 * This method is used to build the one time access token.
	 * @param user the user details
	 * @param databaseName the database name.
	 * @return the accessToken which is built.
	 * @throws UserVerificationDAOException if any exception occurs when performing this operation.
	 */
	public String buildOneTimeAccessToken(User user, String databaseName) throws UserVerificationDAOException {
		logger.debug(" inside buildOneTimeAccessToken of TokenBuilder");
		String userId = user.getUserId();
		UserVerification userVerificationByUserId = userVerificationDAO.getUserVerificationByUserId(databaseName,
				userId);
		logger.debug(" Got UserVerification in TokenBuilder: " + userVerificationByUserId);
		String accessToken = buildAndUpdateOneTimeAccessToken(user, userVerificationByUserId, databaseName);
		return accessToken;
	}

	/**
	 * This method will create the one time access token and it will update in the
	 * user-verification table.
	 */
	private String buildAndUpdateOneTimeAccessToken(User user, UserVerification userVerify, String databaseName)
			throws UserVerificationDAOException {
		logger.debug(" inside buildAndUpdateOneTimeAccessToken of TokenBuilder ");
		String password = userVerify.getTempPassword();
		String email = user.getEmail();
		Date date = new Date();
		String dateString = dateFromater.format(date);
		String tokenUUID = UUID.randomUUID().toString();
		String accessTokenString = tokenUUID + GradingToolApplicationConstant.TOKEN_SPLITER + email
				+ GradingToolApplicationConstant.TOKEN_SPLITER + password + GradingToolApplicationConstant.TOKEN_SPLITER
				+ dateString;
		logger.debug("OneTime accessTokenString :  " + accessTokenString);
		String accessToken = Base64.getEncoder().encodeToString(accessTokenString.getBytes());
		logger.debug("OneTime accessToken :: " + accessToken);
		accessToken = UserSecurityProtecter.protectUserData(accessToken);
		logger.debug("OneTime accessToken after user protection :: " + accessToken);
		String minutesStr = ReadPropertiesFile
				.readRequestProperty(GradingToolApplicationConstant.ONE_TIME_TOKEN_EXPIRY_MIN);
		Integer minutes = getMinutesFromString(minutesStr);
		long expTime = getExpritionTimeByMinutes(date, minutes);
		userVerify.setOneTimeAccessToken(accessToken);
		userVerify.setExpiryTime(expTime);
		userVerificationDAO.updateAccessTokenAndExpiry(databaseName, userVerify);
		return accessToken;
	}

	/**
	 * This method is to build the JWT Token for the user.
	 * @param user the user details.
	 * @param accessToken the access token which is to be included in jwt token.
	 * @param tenantId the database name.
	 * @return the jwt token which is created.
	 */
	public String buildJwtToken(User user, String accessToken, String tenantId) {
		logger.debug(" inside buildJwtToken of TokenBuilder ");
		Map<String, String> userMap = getUserMap(user);
		Map<String, Object> payLoadDataMap = new HashMap<>();
		String sessionId = UUID.randomUUID().toString();
		payLoadDataMap.put(GradingToolApplicationConstant.SESSION_ID, sessionId);
		payLoadDataMap.put(GradingToolApplicationConstant.USER, userMap);
		payLoadDataMap.put(GradingToolApplicationConstant.TENANT_ID, tenantId);
		Map<String, String> authMap = getAuthData(accessToken);
		payLoadDataMap.put(GradingToolApplicationConstant.AUTH_DATA, authMap);
		Map<String, Object> jwtHeaderMap = new HashMap<>();
		jwtHeaderMap.put(GradingToolApplicationConstant.JWT_KEY, GradingToolApplicationConstant.JWT_KEY_VALUE);
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		Date date = new Date();
		String minutesStr = ReadPropertiesFile.readRequestProperty(GradingToolApplicationConstant.EXP_MINUTE);
		Integer minutes = getMinutesFromString(minutesStr);
		long expTime = getExpritionTimeByMinutes(date, minutes);
		logger.debug("expire time is " + expTime);
		payLoadDataMap.put(GradingToolApplicationConstant.EXP, expTime);
		String secretKey = ReadPropertiesFile.readRequestProperty(GradingToolApplicationConstant.SECRET_KEY);
		logger.debug(" secretKey :: " + secretKey);
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey.trim());
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		JwtBuilder jwtBuilder = Jwts.builder().setId(GradingToolApplicationConstant.JWT_DEFAULT_ID)
				.setHeader(jwtHeaderMap).setClaims(payLoadDataMap).signWith(signatureAlgorithm, signingKey);
		logger.debug("access token expire time in ms : " + expTime);
		if (expTime > 0) {
			long expMillis = expTime;
			Date exp = new Date(expMillis);
			dateFromater.setTimeZone(TimeZone.getTimeZone(GradingToolApplicationConstant.UTC));
			String strUTCDate = dateFromater.format(exp);
			logger.debug("expiration date and time : " + exp + ", formated in UTC : " + strUTCDate);
			jwtBuilder.setExpiration(exp);
		}
		String jwtTokenData = jwtBuilder.compact();
		addLogTrackingOfUser(user, sessionId, tenantId);
		logger.info("jwt token : " + jwtTokenData);
		return jwtTokenData;

	}

	/**
	 *Add the user log tracking.
	 */
	private void addLogTrackingOfUser(User user, String sessionId, String tenantId) {
		UserLogTracking userLogTracking = new UserLogTracking(user.getUserId(), new Date().getTime(), 0, sessionId);
		iLogTrackingDao.addLogTrackingOfUser(ReadPropertiesFile.readTenantProperty(tenantId), userLogTracking);
	}

	/**
	 * Build the JWT Token for the forgot password service.
	 * @param user the user details.
	 * @param accessToken the one time access token.
	 * @param tenantId the database name.
	 * @return the jwt token which is created.
	 */
	public String buildJwtTokenForForgetPassword(User user, String accessToken, String tenantId) {
		logger.debug(" inside buildJwtToken of TokenBuilder ");
		Map<String, String> userMap = getUserMapForForgetPassword(user);
		Map<String, Object> payLoadDataMap = new HashMap<>();
		String sessionId = UUID.randomUUID().toString();
		payLoadDataMap.put(GradingToolApplicationConstant.SESSION_ID, sessionId);
		payLoadDataMap.put(GradingToolApplicationConstant.VERIFY_OTP, false);
		payLoadDataMap.put(GradingToolApplicationConstant.USER, userMap);
		payLoadDataMap.put(GradingToolApplicationConstant.TENANT_ID, tenantId);
		Map<String, String> authMap = getAuthData(accessToken);
		payLoadDataMap.put(GradingToolApplicationConstant.AUTH_DATA, authMap);

		Map<String, Object> jwtHeaderMap = new HashMap<>();
		jwtHeaderMap.put(GradingToolApplicationConstant.JWT_KEY, GradingToolApplicationConstant.JWT_KEY_VALUE);
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		Date date = new Date();
		String minutesStr = ReadPropertiesFile.readRequestProperty(GradingToolApplicationConstant.EXP_MINUTE);
		Integer minutes = getMinutesFromString(minutesStr);
		long expTime = getExpritionTimeByMinutes(date, minutes);
		logger.debug("expire time is " + expTime);
		payLoadDataMap.put(GradingToolApplicationConstant.EXP, expTime);
		String secretKey = ReadPropertiesFile.readRequestProperty(GradingToolApplicationConstant.SECRET_KEY);
		logger.debug(" secretKey :: " + secretKey);
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey.trim());
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		JwtBuilder jwtBuilder = Jwts.builder().setId(GradingToolApplicationConstant.JWT_DEFAULT_ID)
				.setHeader(jwtHeaderMap).setClaims(payLoadDataMap).signWith(signatureAlgorithm, signingKey);
		logger.debug("access token expire time in ms : " + expTime);
		if (expTime > 0) {
			long expMillis = expTime;
			Date exp = new Date(expMillis);
			dateFromater.setTimeZone(TimeZone.getTimeZone(GradingToolApplicationConstant.UTC));
			String strUTCDate = dateFromater.format(exp);
			logger.debug("expiration date and time : " + exp + ", formated in UTC : " + strUTCDate);
			jwtBuilder.setExpiration(exp);
		}
		String jwtTokenData = jwtBuilder.compact();
		logger.info("jwt token : " + jwtTokenData);
		return jwtTokenData;

	}

	/**
	 * Build and store the user token in the UserToken table.
	 * 
	 */
	private String buildAndStoreUserToken(UserToken userToken, User user, JSONObject userJSON, String deviceId,
			String tenantId) throws UserTokenDaoException {
		logger.debug(" inside buildAndStoreUserToken of TokenBuilder ");
		boolean tokenExits = false;
		String password = user.getPassword();
		String email = user.getEmail();
		String platForm = getVlaueFromJSON(userJSON, UserLoginServiceConstant.PLATFORM);
		Date date = new Date();
		String dateString = dateFromater.format(date);
		String tokenUUID = UUID.randomUUID().toString();
		String accessTokenString = deviceId + GradingToolApplicationConstant.TOKEN_SPLITER + tokenUUID
				+ GradingToolApplicationConstant.TOKEN_SPLITER + email + GradingToolApplicationConstant.TOKEN_SPLITER
				+ password + GradingToolApplicationConstant.TOKEN_SPLITER + dateString;
		logger.debug("accessTokenString :  " + accessTokenString);
		String accessToken = Base64.getEncoder().encodeToString(accessTokenString.getBytes());
		logger.debug(" accessToken :: " + accessToken);
		accessToken = UserSecurityProtecter.protectUserData(accessToken);
		logger.debug(" accessToken after user protection :: " + accessToken);
		String minutesStr = ReadPropertiesFile.readRequestProperty(GradingToolApplicationConstant.EXP_MINUTE);
		Integer minutes = getMinutesFromString(minutesStr);
		long expTime = getExpritionTimeByMinutes(date, minutes);
		UserToken newUserToken = new UserToken();
		if (userToken != null) {
			String tokenId = userToken.getTokenId();
			newUserToken.setTokenId(tokenId);
			tokenExits = true;
		} else {
			newUserToken.setTokenId(String.valueOf(UUID.randomUUID()));
		}
		newUserToken.setAccessToken(accessToken);
		newUserToken.setDeviceId(deviceId);
		newUserToken.setPlatform(platForm);
		newUserToken.setExpiryTime(expTime);
		newUserToken.setUserId(user.getUserId());
		if (tokenExits) {
			logger.debug("tokenExits ");
			userTokenDao.updateUserTokenByTokenId(newUserToken, tenantId);
		} else {
			logger.debug("token doesn't Exits ");
			userTokenDao.storeUserToken(newUserToken, tenantId);
		}
		return accessToken;
	}

	private String getVlaueFromJSON(JSONObject jsonObject, String key) {
		logger.debug(" inside getVlaueFromJSON : " + key);
		String value = "";
		if (jsonObject.containsKey(key)) {
			value = (String) jsonObject.get(key);
		} else {
			logger.debug(key + " not found ");
		}
		return value;
	}

	private Integer getMinutesFromString(String days) {
		logger.debug(".inside getDaysFromString ");
		Integer integer = 15;
		try {
			integer = Integer.parseInt(days);
		} catch (Exception e) {
			logger.error("error in getDaysFromString ", e);
		}
		return integer;
	}

//	private long getExpritionTimeByDates(Date date, Integer noOfDays) {
//		logger.debug(".inside getDaysFromString ");
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(date);
//		calendar.add(Calendar.DATE, noOfDays);
//		long expTime = calendar.getTimeInMillis();
//		return expTime;
//	}

	private long getExpritionTimeByMinutes(Date date, Integer minute) {
		logger.debug(".inside getDaysFromString ");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minute);
		long expTime = calendar.getTimeInMillis();
		return expTime;
	}

	private Map<String, String> getUserMap(User user) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(GradingToolApplicationConstant.USER_EMAIL_ID, user.getEmail());
		map.put(GradingToolApplicationConstant.USER_FIRST_NAME, user.getFirstName());
		map.put(GradingToolApplicationConstant.USER_LAST_NAME, user.getLastName());
		map.put(GradingToolApplicationConstant.USER_ROLE, user.getUserRole());
		return map;
	}

	private Map<String, String> getUserMapForForgetPassword(User user) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(GradingToolApplicationConstant.USER_EMAIL_ID, user.getEmail());
		map.put(GradingToolApplicationConstant.USER_FIRST_NAME, "");
		map.put(GradingToolApplicationConstant.USER_LAST_NAME, "");
		map.put(GradingToolApplicationConstant.USER_ROLE, "");
		return map;
	}

	private Map<String, String> getAuthData(String accessToken) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(GradingToolApplicationConstant.ACCESS_TOKEN, accessToken);
		return map;
	}

	/**
	 * This method is used to build one time JWT Token to the user.
	 * @param user the user details.
	 * @param accessToken the one time access token.
	 * @param tenantId the database name.
	 * @return the jwt token which is created.
	 * @throws UserVerificationDAOException if any exception occurs.
	 */
	public String buildOneTimeJwtToken(User user, String accessToken, String tenantId)
			throws UserVerificationDAOException {
		logger.debug(" inside buildOneTimeJwtToken of TokenBuilder ");
//		Map<String, String> userMap = getUserMap(user);
		Map<String, Object> payLoadDataMap = new HashMap<>();
//		String sessionId = UUID.randomUUID().toString();
//		payLoadDataMap.put(GradingToolApplicationConstant.SESSION_ID, sessionId);
		payLoadDataMap.put(GradingToolApplicationConstant.USER_EMAIL_ID, user.getEmail());
		payLoadDataMap.put(GradingToolApplicationConstant.TENANT_ID, tenantId);
//		Map<String, String> authMap = getAuthData(accessToken);
//		payLoadDataMap.put(GradingToolApplicationConstant.AUTH_DATA, authMap);
		Map<String, Object> jwtHeaderMap = new HashMap<>();
		jwtHeaderMap.put(GradingToolApplicationConstant.JWT_KEY, GradingToolApplicationConstant.JWT_KEY_VALUE);
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		Date date = new Date();
		String minutesStr = ReadPropertiesFile
				.readRequestProperty(GradingToolApplicationConstant.ONETIME_JWT_EXPIRY_MIN);
		Integer minutes = getMinutesFromString(minutesStr);
		logger.debug("One time JWT expiry min: " + minutes);
		long expTime = getExpritionTimeByMinutes(date, minutes);
//		logger.debug("One time JWT expire time is " + expTime);
		payLoadDataMap.put(GradingToolApplicationConstant.EXP, expTime);
		payLoadDataMap.put(GradingToolApplicationConstant.VERIFICATION_DONE, true);
		String secretKey = ReadPropertiesFile.readRequestProperty(GradingToolApplicationConstant.SECRET_KEY);
		logger.debug(" secretKey :: " + secretKey);
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey.trim());
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		JwtBuilder jwtBuilder = Jwts.builder().setId(GradingToolApplicationConstant.JWT_DEFAULT_ID)
				.setHeader(jwtHeaderMap).setClaims(payLoadDataMap).signWith(signatureAlgorithm, signingKey);
		logger.debug("One time JWT token expire time in ms : " + expTime);
		if (expTime > 0) {
			long expMillis = expTime;
			Date exp = new Date(expMillis);
			dateFromater.setTimeZone(TimeZone.getTimeZone(GradingToolApplicationConstant.UTC));
			String strUTCDate = dateFromater.format(exp);
			logger.debug("expiration date and time : " + exp + ", formated in UTC : " + strUTCDate);
			jwtBuilder.setExpiration(exp);
		}
		String jwtTokenData = jwtBuilder.compact();
//		addLogTrackingOfUser(user, sessionId, tenantId);
		logger.info("One time jwt token : " + jwtTokenData);
		return jwtTokenData;
	}

	/**
	 * This method is used to build the one time access token for the
	 * forgot password service.
	 * @param user the user details.
	 * @param userJSON the JSON containing the user details.
	 * @param tenantId the database name.
	 * @return the access token which is created.
	 * @throws UserTokenDaoException
	 */
	public String buildAccessTokenForForgotPasswordService(User user, JSONObject userJSON, String tenantId)
			throws UserTokenDaoException {
		logger.debug(" inside buildAccessTokenForForgotPasswordService of TokenBuilder");
		String userId = user.getUserId();
		String deviceId = getVlaueFromJSON(userJSON, UserLoginServiceConstant.DEVICE_ID);
		logger.debug(" userId :: " + userId + " deviceId :: " + deviceId);
		UserToken userToken = userTokenDao.getUserTokenByDeviceIdAndUserId(deviceId, userId, tenantId);
		String accessToken = buildAndStoreUserTokenForForgotPasswordService(userToken, user, userJSON, deviceId,
				tenantId);
		return accessToken;
	}

	/**
	 * This method will build and store the accessToken for the forgot password service.
	 */
	private String buildAndStoreUserTokenForForgotPasswordService(UserToken userToken, User user, JSONObject userJSON,
			String deviceId, String tenantId) throws UserTokenDaoException {
		logger.debug(" inside buildAndStoreUserTokenForForgotPasswordService of TokenBuilder ");
		boolean tokenExits = false;
		String password = user.getPassword();
		String email = user.getEmail();
		String platForm = getVlaueFromJSON(userJSON, UserLoginServiceConstant.PLATFORM);
		Date date = new Date();
		String dateString = dateFromater.format(date);
		String tokenUUID = UUID.randomUUID().toString();
		String accessTokenString = deviceId + GradingToolApplicationConstant.TOKEN_SPLITER + tokenUUID
				+ GradingToolApplicationConstant.TOKEN_SPLITER + email + GradingToolApplicationConstant.TOKEN_SPLITER
				+ password + GradingToolApplicationConstant.TOKEN_SPLITER + dateString;
		logger.debug("accessTokenString :  " + accessTokenString);
		String accessToken = Base64.getEncoder().encodeToString(accessTokenString.getBytes());
		logger.debug(" accessToken :: " + accessToken);
		accessToken = UserSecurityProtecter.protectUserData(accessToken);
		logger.debug(" accessToken after user protection :: " + accessToken);
		String minutesStr = ReadPropertiesFile
				.readRequestProperty(GradingToolApplicationConstant.ONE_TIME_TOKEN_EXPIRY_MIN);
		Integer minutes = getMinutesFromString(minutesStr);
		long expTime = getExpritionTimeByMinutes(date, minutes);
		UserToken newUserToken = new UserToken();
		if (userToken != null) {
			String tokenId = userToken.getTokenId();
			newUserToken.setTokenId(tokenId);
			tokenExits = true;
		} else {
			newUserToken.setTokenId(String.valueOf(UUID.randomUUID()));
		}
		newUserToken.setAccessToken(accessToken);
		newUserToken.setDeviceId(deviceId);
		newUserToken.setPlatform(platForm);
		newUserToken.setExpiryTime(expTime);
		newUserToken.setUserId(user.getUserId());
		if (tokenExits) {
			logger.debug("tokenExits ");
			userTokenDao.updateUserTokenByTokenId(newUserToken, tenantId);
		} else {
			logger.debug("token doesn't Exits ");
			userTokenDao.storeUserToken(newUserToken, tenantId);
		}
		return accessToken;
	}

	/**
	 * This method is used to build the jwt token for the submission of the survey.
	 * @param user the user details.
	 * @param otp the otp from the user.
	 * @return the jwt token which is created.
	 */
	public String buildJwtTokenForSubmissionOfSurvey(User user, String otp) {
		logger.debug(" inside buildJwtToken of TokenBuilder ");
//		Map<String, String> userMap = getUserMap(user);
		Map<String, Object> payLoadDataMap = new HashMap<>();
		payLoadDataMap.put(GradingToolApplicationConstant.OTP, otp);
		payLoadDataMap.put(GradingToolApplicationConstant.OPERATION_NAME, "submitSurvey");
		payLoadDataMap.put(GradingToolApplicationConstant.STATUS, true);
		payLoadDataMap.put(GradingToolApplicationConstant.USER_EMAIL_ID, user.getEmail());
		Map<String, Object> jwtHeaderMap = new HashMap<>();
		jwtHeaderMap.put(GradingToolApplicationConstant.JWT_KEY, GradingToolApplicationConstant.JWT_KEY_VALUE);
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		Date date = new Date();
		String minutesStr = ReadPropertiesFile.readRequestProperty(GradingToolApplicationConstant.EXP_MINUTE);
		Integer minutes = getMinutesFromString(minutesStr);
		long expTime = getExpritionTimeByMinutes(date, minutes);
		logger.debug("expire time is " + expTime);
		payLoadDataMap.put(GradingToolApplicationConstant.EXP, expTime);
		String secretKey = ReadPropertiesFile.readRequestProperty(GradingToolApplicationConstant.SECRET_KEY);
		logger.debug(" secretKey :: " + secretKey);
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey.trim());
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		JwtBuilder jwtBuilder = Jwts.builder().setId(GradingToolApplicationConstant.JWT_DEFAULT_ID)
				.setHeader(jwtHeaderMap).setClaims(payLoadDataMap).signWith(signatureAlgorithm, signingKey);
		logger.debug("access token expire time in ms : " + expTime);
		if (expTime > 0) {
			long expMillis = expTime;
			Date exp = new Date(expMillis);
			dateFromater.setTimeZone(TimeZone.getTimeZone(GradingToolApplicationConstant.UTC));
			String strUTCDate = dateFromater.format(exp);
			logger.debug("expiration date and time : " + exp + ", formated in UTC : " + strUTCDate);
			jwtBuilder.setExpiration(exp);
		}
		String jwtTokenData = jwtBuilder.compact();
		logger.info("jwt token : " + jwtTokenData);
		return jwtTokenData;

	}
}
