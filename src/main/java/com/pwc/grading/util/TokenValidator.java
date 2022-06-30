package com.pwc.grading.util;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.GradingToolApplicationConstant;
import com.pwc.grading.tracking.dao.ITrackingDao;
import com.pwc.grading.usertoken.model.ResponseSubmission;
import com.pwc.grading.usertoken.model.TokenPayload;
import com.pwc.grading.util.exception.JsonUtillException;
import com.pwc.grading.util.exception.TokenValidatorException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class TokenValidator {
	@Inject
	private static ITrackingDao iLogTrackingDao;

	private static final Logger logger = LoggerFactory.getLogger(TokenValidator.class);

	/**
	 * This method will validate the Jwt token and extract the details from the JWT Token
	 * and return the details.
	 * @param jwtToken the token which is to be validated.
	 * @return a token payload having the details. 
	 * @throws TokenValidatorException if the token is invalid (or) expired.
	 */
	public static TokenPayload validateJWTTokenAndGetTokenPayload(String jwtToken) throws TokenValidatorException {
		try {
			String secretKey = ReadPropertiesFile.readRequestProperty(GradingToolApplicationConstant.SECRET_KEY);
			logger.debug(" secretKey :: " + secretKey);
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey.trim()))
					.parseClaimsJws(jwtToken).getBody();
			logger.debug("claims is " + claims.toString());
			JSONObject jwtPayloadData = converClaimsToJsonObject(claims);
			logger.debug("jwtPayloadData " + jwtPayloadData);
			long jwtExp = (long) jwtPayloadData.get(GradingToolApplicationConstant.EXP);
			long currentTime = new Date().getTime() / 1000;
			logger.debug("currentTime " + currentTime);
			logger.debug("jwtExp      " + jwtExp);
			String tenantId = JsonUtill.getString(jwtPayloadData, GradingToolApplicationConstant.TENANT_ID);
			String sessionId = JsonUtill.getString(jwtPayloadData, GradingToolApplicationConstant.SESSION_ID);
			if (currentTime > jwtExp) {
				logger.error("token expires");
				iLogTrackingDao.updateLogTrackingBySessionId(tenantId, sessionId, new Date().getTime());
				return null;
			} else {
				logger.debug("token is valid");
				TokenPayload tokenPalLoad = new TokenPayload();
				JSONObject userData = JsonUtill.getJsonObject(jwtPayloadData, GradingToolApplicationConstant.USER);
				String userEmail = JsonUtill.getString(userData, GradingToolApplicationConstant.USER_EMAIL_ID);
				String firstName = JsonUtill.getString(userData, GradingToolApplicationConstant.USER_FIRST_NAME);
				String role = JsonUtill.getString(userData, GradingToolApplicationConstant.USER_ROLE);
				String lastName = JsonUtill.getString(userData, GradingToolApplicationConstant.USER_LAST_NAME);
				tokenPalLoad.setTenantKey(tenantId);
				tenantId = ReadPropertiesFile.readTenantProperty(tenantId);
				tokenPalLoad.setTenantId(tenantId);
				tokenPalLoad.setUserEmail(userEmail);
				tokenPalLoad.setUserFirstName(firstName);
				tokenPalLoad.setUserLastName(lastName);
				tokenPalLoad.setUserRole(role);
				tokenPalLoad.setSessionId(sessionId);
				logger.debug("tokenPalLoad is " + tokenPalLoad);
				return tokenPalLoad;
			}
		} catch (Exception e) {
			throw new TokenValidatorException(e.getMessage());
		}

	}

	/**
	 * Convert the Jwt claims to the Json Object.
	 */
	@SuppressWarnings("unchecked")
	private static JSONObject converClaimsToJsonObject(Claims claims) {
		JSONObject jsonObject = new JSONObject();
		JSONObject authJsonObject = new JSONObject();
		Map<String, Object> map1 = (Map<String, Object>) claims.get(GradingToolApplicationConstant.AUTH_DATA);
		String object = (String) map1.get(GradingToolApplicationConstant.ACCESS_TOKEN);
		authJsonObject.put(GradingToolApplicationConstant.ACCESS_TOKEN, object);
		String sessionId = (String) claims.get(GradingToolApplicationConstant.SESSION_ID);
		String tenantId = (String) claims.get(GradingToolApplicationConstant.TENANT_ID);
		Object expObj = claims.get(GradingToolApplicationConstant.EXP);
		long exp = Long.parseLong(expObj + "");
		JSONObject userJsonObject = new JSONObject();
		Map<String, Object> userMap = (Map<String, Object>) claims.get(GradingToolApplicationConstant.USER);
		String firstName = (String) userMap.get(GradingToolApplicationConstant.USER_FIRST_NAME);
		String lastName = (String) userMap.get(GradingToolApplicationConstant.USER_LAST_NAME);
		String role = (String) userMap.get(GradingToolApplicationConstant.USER_ROLE);
		String email = (String) userMap.get(GradingToolApplicationConstant.USER_EMAIL_ID);
		userJsonObject.put(GradingToolApplicationConstant.USER_FIRST_NAME, firstName);
		userJsonObject.put(GradingToolApplicationConstant.USER_LAST_NAME, lastName);
		userJsonObject.put(GradingToolApplicationConstant.USER_ROLE, role);
		userJsonObject.put(GradingToolApplicationConstant.USER_EMAIL_ID, email);
		jsonObject.put(GradingToolApplicationConstant.AUTH_DATA, authJsonObject);
		jsonObject.put(GradingToolApplicationConstant.TENANT_ID, tenantId);
		jsonObject.put(GradingToolApplicationConstant.SESSION_ID, sessionId);
		jsonObject.put(GradingToolApplicationConstant.EXP, exp);
		jsonObject.put(GradingToolApplicationConstant.USER, userJsonObject);
		return jsonObject;

	}

	/**
	 * This method will validate the JWT Token.
	 * @param jwtToken the token which is to be validated.
	 * @return true if valid, false if expired (or) invalid.
	 */
	public static boolean validateJWTToken(String jwtToken) {
		try {
			String secretKey = ReadPropertiesFile.readRequestProperty(GradingToolApplicationConstant.SECRET_KEY);
			logger.debug(" secretKey :: " + secretKey);
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey.trim()))
					.parseClaimsJws(jwtToken).getBody();
			JSONObject jwtPayloadData = converClaimsToJsonObject(claims);
			logger.debug("jwtPayloadData " + jwtPayloadData);
			long jwtExp = JsonUtill.getLong(jwtPayloadData, GradingToolApplicationConstant.EXP);
			logger.debug("jwt expiration time : " + jwtExp);
			long currentTime = new Date().getTime() / 1000;
			logger.debug("currentTime " + currentTime);

			if (currentTime > jwtExp) {
				logger.error("token expires");
				return false;
			} else {
				logger.debug("token is valid");
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * This method will validate the OneTime Jwt token and extract the details from the JWT Token
	 * and return the details.
	 * @param jwtToken the token which is to be validated.
	 * @return a token payload having the details. 
	 * @throws TokenValidatorException if the token is invalid (or) expired.
	 */
	public static TokenPayload validateOneTimeJWTTokenAndGetTokenPayload(String jwtToken)
			throws TokenValidatorException {
		try {
			logger.debug("inside validateOneTimeJWTTokenAndGetTokenPayload method of TokenValidator class.");
			String secretKey = ReadPropertiesFile.readRequestProperty(GradingToolApplicationConstant.SECRET_KEY);
			logger.debug(" secretKey :: " + secretKey);
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey.trim()))
					.parseClaimsJws(jwtToken).getBody();
			logger.debug("claims is " + claims.toString());
			JSONObject jwtPayloadData = convertOnetimeJWTClaimsToJsonObject(claims);
			logger.debug("jwtPayloadData " + jwtPayloadData);
			long jwtExp = (long) jwtPayloadData.get(GradingToolApplicationConstant.EXP);
			long currentTime = new Date().getTime() / 1000;
			logger.debug("currentTime " + currentTime);
			logger.debug("jwtExp      " + jwtExp);
			String tenantId = JsonUtill.getString(jwtPayloadData, GradingToolApplicationConstant.TENANT_ID);
//		String sessionId = JsonUtill.getString(jwtPayloadData, GradingToolApplicationConstant.SESSION_ID);
			if (currentTime > jwtExp) {
				logger.error("one time jwt token expires");
//			iLogTrackingDao.updateLogTrackingBySessionId(tenantId, sessionId, new Date().getTime());
				return null;
			} else {
				logger.debug("token is valid");
				logger.debug("tenantId is :: " + tenantId);
//			JSONObject userData = JsonUtill.getJsonObject(jwtPayloadData, GradingToolApplicationConstant.USER);
				String userEmail = JsonUtill.getString(jwtPayloadData, GradingToolApplicationConstant.USER_EMAIL_ID);
//			String firstName = JsonUtill.getString(userData, GradingToolApplicationConstant.USER_FIRST_NAME);
				boolean verificationDone = JsonUtill.getBoolean(jwtPayloadData,
						GradingToolApplicationConstant.VERIFICATION_DONE);
//			String role = JsonUtill.getString(userData, GradingToolApplicationConstant.USER_ROLE);
//			String lastName = JsonUtill.getString(userData, GradingToolApplicationConstant.USER_LAST_NAME);
				tenantId = ReadPropertiesFile.readTenantProperty(tenantId);
				TokenPayload tokenPalLoad = new TokenPayload();
				tokenPalLoad.setTenantId(tenantId);
				tokenPalLoad.setUserEmail(userEmail);
				tokenPalLoad.setVerification(verificationDone);
				logger.debug("tokenPalLoad is " + tokenPalLoad);
				return tokenPalLoad;
			}
		} catch (Exception e) {
			throw new TokenValidatorException(e.getMessage());
		}

	}

	/**
	 * This method will validate the Jwt token for survey submission and extract the details from the JWT Token
	 * and return the details.
	 * @param jwtToken the token which is to be validated.
	 * @return the details of the response submission.
	 * @throws JsonUtillException if json utility unable to fetch keys.
	 * @throws ParseException if any parse error happens.
	 */
	public static ResponseSubmission validateSurveySubmissionJWTToken(String jwtToken)
			throws JsonUtillException, ParseException {
		logger.debug("inside validateSurveySubmissionJWTToken method of TokenValidator class.");
		String secretKey = ReadPropertiesFile.readRequestProperty(GradingToolApplicationConstant.SECRET_KEY);
		logger.debug(" secretKey :: " + secretKey);
		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey.trim()))
				.parseClaimsJws(jwtToken).getBody();
		logger.debug("claims is " + claims.toString());
		JSONObject jwtPayloadData = convertResponseSubmissionClaimsToJsonObject(claims);
		logger.debug("jwtPayloadData " + jwtPayloadData);
		long jwtExp = (long) jwtPayloadData.get(GradingToolApplicationConstant.EXP);
		long currentTime = new Date().getTime() / 1000;
		logger.debug("currentTime " + currentTime);
		logger.debug("jwtExp      " + jwtExp);
		if (currentTime > jwtExp) {
			logger.error("one time jwt token expires");
			return null;
		} else {
			logger.debug("token is valid");
			String userEmail = JsonUtill.getString(jwtPayloadData, GradingToolApplicationConstant.USER_EMAIL_ID);
			String operationName = JsonUtill.getString(jwtPayloadData, GradingToolApplicationConstant.OPERATION_NAME);
			String otp = JsonUtill.getString(jwtPayloadData, GradingToolApplicationConstant.OTP);
			boolean status = JsonUtill.getBoolean(jwtPayloadData, GradingToolApplicationConstant.STATUS);

			ResponseSubmission tokenPalLoad = new ResponseSubmission();
			tokenPalLoad.setUserEmail(userEmail);
			tokenPalLoad.setOperationName(operationName);
			tokenPalLoad.setOtp(otp);
			tokenPalLoad.setStatus(status);
			logger.debug("ResponseSubmission is ::" + tokenPalLoad);
			return tokenPalLoad;
		}

	}

	/**
	 * Convert the one time Jwt claims to the Json Object.
	 */
	@SuppressWarnings("unchecked")
	private static JSONObject convertOnetimeJWTClaimsToJsonObject(Claims claims) {
		JSONObject jsonObject = new JSONObject();
		String tenantId = (String) claims.get(GradingToolApplicationConstant.TENANT_ID);
		boolean verification = (boolean) claims.get(GradingToolApplicationConstant.VERIFICATION_DONE);
		Object expObj = claims.get(GradingToolApplicationConstant.EXP);
		long exp = Long.parseLong(expObj + "");
		String email = (String) claims.get(GradingToolApplicationConstant.USER_EMAIL_ID);
		jsonObject.put(GradingToolApplicationConstant.TENANT_ID, tenantId);
		jsonObject.put(GradingToolApplicationConstant.VERIFICATION_DONE, verification);
		jsonObject.put(GradingToolApplicationConstant.EXP, exp);
		jsonObject.put(GradingToolApplicationConstant.USER_EMAIL_ID, email);
		return jsonObject;
	}

	/**
	 * Convert the response submission claims to the Json Object.
	 */
	@SuppressWarnings("unchecked")
	private static JSONObject convertResponseSubmissionClaimsToJsonObject(Claims claims) {
		JSONObject jsonObject = new JSONObject();
		String otp = claims.get(GradingToolApplicationConstant.OTP).toString();
		Object expObj = claims.get(GradingToolApplicationConstant.EXP);
		long exp = Long.parseLong(expObj + "");
		boolean verification = (boolean) claims.get(GradingToolApplicationConstant.STATUS);
		String operationName = claims.get(GradingToolApplicationConstant.OPERATION_NAME).toString();
		String email = (String) claims.get(GradingToolApplicationConstant.USER_EMAIL_ID);
		jsonObject.put(GradingToolApplicationConstant.STATUS, verification);
		jsonObject.put(GradingToolApplicationConstant.OPERATION_NAME, operationName);
		jsonObject.put(GradingToolApplicationConstant.OTP, otp);
		jsonObject.put(GradingToolApplicationConstant.EXP, exp);
		jsonObject.put(GradingToolApplicationConstant.USER_EMAIL_ID, email);
		return jsonObject;
	}
}
