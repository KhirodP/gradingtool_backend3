package com.pwc.grading.controller;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.controller.exception.UserLoginControllerException;
import com.pwc.grading.user.service.login.IUserLoginService;
import com.pwc.grading.usertoken.model.TokenPayload;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.TokenValidator;
import com.pwc.grading.util.exception.TokenValidatorException;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.Post;

/**
 * This controller is responsible for the User Login functionalities.
 *
 */
@Controller("gradingTool/user")
public class UserLoginController {
	private static final Logger logger = LoggerFactory.getLogger(UserLoginController.class);
	@Inject
	private IUserLoginService loginService;

	/**
	 * This method is used for the login of the user.
	 * <br>If the user is login for the first time, the user must have to verify by providing the 
	 * one time password (otp) which is received in the email and in the sms. Then, the user needs to create a new password.
	 * After creating the new password, the user has to use the new password for login again. </br>
	 * @param requestBody the request body having the login credentials of the user.
	 * @param tenantId the encoded tenantId.
	 * @return the response of the user login.
	 * @throws UserLoginControllerException if any exception occurs while performing this operation.
	 */
	@Post("/login")
	public HttpResponse<String> loginUser(@Body String requestBody, @Header("tenantId") String tenantId)
			throws UserLoginControllerException {
		try {
			logger.debug(" inside loginUser of UserLoginController,, headers are");
			validateRequestBody(requestBody);
			logger.debug("requestBody : " + requestBody);
			String response = loginService.loginUser(requestBody, tenantId);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserLoginControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to login, exception :: " + e.getMessage(), e);
			throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("401"), e, 401,
					e.getMessage());
		}
	}

	/**
	 * This method is used for the login of the user by accessToken.
	 * @param requestBody the request body having the accessToken of the user.
	 * @param tenantId the encoded tenantId.
	 * @return the response of the user login.
	 * @throws UserLoginControllerException if any exception occurs while performing this operation.
	 */
	@Post("/loginWithAccessToken")
	public HttpResponse<String> loginUserWithAccessToken(@Body @NotBlank String requestBody,
			@Header("tenantId") String tenantId) throws UserLoginControllerException {
		try {
			logger.debug(" inside loginUserWithAccessToken of UserLoginController");
			validateRequestBody(requestBody);
			logger.debug("requestBody : " + requestBody);
			String response = loginService.loginWithAccessToken(requestBody, tenantId);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserLoginControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to login user with access token ,exception :: " + e.getMessage(), e);
			throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("403"), e, 401,
					e.getMessage());
		}

	}

	/**
	 * This method is used to send the otp to the user.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return the response message of this method.
	 * @throws UserLoginControllerException if any exception occurs while performing this operation.
	 */
	@Get("/otp")
	public HttpResponse<String> sendOtp(@Header("jwtToken") String jwtToken) throws UserLoginControllerException {
		try {
			logger.debug(" inside sendOtp of UserLoginController");
			logger.debug("jwtToken : " + jwtToken);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userEmail = tokenPayload.getUserEmail();
			logger.debug("userEmail :" + userEmail + " tenantId : " + tenantId);
			String response = loginService.sendOtp(userEmail, tenantId);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserLoginControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to send otp, exception :: " + e.getMessage(), e);
			throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("426"), e, 400,
					e.getMessage());
		}

	}

	/*
	 * @Post("/forgetPassword/sendOtp") public HttpResponse<String>
	 * forgetPasswordSendOtp(@Body String requestBody, @Header String tenantId)
	 * throws UserLoginControllerException { try {
	 * logger.debug(" inside sendOtp of UserLoginController, RequestBody is ::" +
	 * requestBody); validateRequestBody(requestBody); String response =
	 * loginService.forgetPasswordSendOtp(requestBody, tenantId); return
	 * HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response); }
	 * catch (Exception e) { logger.error("unable to send otp, exception :: " +
	 * e.getMessage(), e); throw new
	 * UserLoginControllerException(ReadPropertiesFile.readResponseProperty("426"),
	 * e, 401, e.getMessage()); }
	 * 
	 * }
	 */

	/**
	 *  This method is used to verify the otp provided by the user.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param otp otp provided by the user.
	 * @return the response message.
	 * @throws UserLoginControllerException if any exception occurs while performing this operation.
	 */
	@Get("/verifyOtp")
	public HttpResponse<String> verifyOtp(@Header("jwtToken") String jwtToken, @Header("otp") String otp)
			throws UserLoginControllerException {
		try {
			logger.debug(" inside sendOtp of UserLoginController");
			logger.debug("jwtToken : " + jwtToken);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userEmail = tokenPayload.getUserEmail();
			logger.debug("userEmail :" + userEmail + " tenantId : " + tenantId);
			String response = loginService.verifyOtp(userEmail, otp, tenantId);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserLoginControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("invalid otp, exception :: " + e.getMessage(), e);
			throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("427"), e, 400,
					e.getMessage());
		}

	}

	/**
	 * This method is used for reset password of the user.
	 * @param requestBody the json containing the new password details.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return response message of this method.
	 * @throws UserLoginControllerException if any exception occurs while performing this operation.
	 */
	@Patch(uri = "/resetPassword")
	public HttpResponse<String> resetPassword(@Body @NotBlank String requestBody, @Header("jwtToken") String jwtToken)
			throws UserLoginControllerException {
		try {
			logger.debug(" inside resetPassword of UserLoginController");
			validateRequestBody(requestBody);
			logger.debug("requestBody : " + requestBody);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userEmail = tokenPayload.getUserEmail();
			logger.debug("userEmail :" + userEmail + " tenantId : " + tenantId);
			String response = loginService.resetPassword(requestBody, tenantId, userEmail);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserLoginControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to reset password, exception :: " + e.getMessage(), e);
			throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("428"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method will logout the user from the application.
	 * The user again has to provide the login credentials to login again.
	 * @param requestBody the json containing the device details.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return response message of this method.
	 * @throws UserLoginControllerException if any exception occurs while performing this operation.
	 */
	@Post("/logout")
	public HttpResponse<String> logoutUser(@Body String requestBody, @Header("jwtToken") String jwtToken)
			throws UserLoginControllerException {
		try {
			logger.debug(" inside logoutUser of UserLoginController");
			logger.debug("requestBody : " + requestBody);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						400, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userEmail = tokenPayload.getUserEmail();
			String sessionId = tokenPayload.getSessionId();
			logger.debug("userEmail :" + userEmail + " tenantId : " + tenantId);
			String response = loginService.logoutUser(tenantId, userEmail, sessionId, requestBody);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserLoginControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to logout exception :: " + e.getMessage(), e);
			throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("429"), e, 401,
					e.getMessage());
		}
	}

	/**
	 * This method is used to send the Otps to Email and phone when the user forgot the password.
	 * Note that, only a verified user can use this functionality.
	 * @param requestBody the request containing the user email id.
	 * @param tenantId the encoded tenantId.
	 * @return the response which contains one time access token.
	 * @throws UserLoginControllerException if any exception occurs while performing this operation.
	 */ 
	@Post("/forgetPassword/sendOtp")
	public HttpResponse<String> forgetPasswordSendOtp(@Body String requestBody, @Header String tenantId,@Header("platform") String platform)
			throws UserLoginControllerException {
		try {
			logger.debug(" inside forgetPasswordSendOtp of UserLoginController, RequestBody is ::" + requestBody);
			validateRequestBody(requestBody);
			String response = loginService.forgetPasswordSendOtp(requestBody, tenantId,platform);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserLoginControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to send otp, exception :: " + e.getMessage(), e);
			throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("426"), e, 400,
					e.getMessage());
		}

	}

	/**
	 * This method is used to verify the Otps provided by the user for forgot password.
	 * @param requestJson the json containing the otps given by user.
	 * @param oneTimeAccessToken the one time access token
	 * @param tenantId the encoded tenantId.
	 * @return the response which contains the one time jwt token.
	 * @throws UserLoginControllerException if any exception occurs while performing this operation.
	 */
	@Post("/forgetPassword/verifyOtp")
	public HttpResponse<String> verifyOtpforForgotPassword(@Body @NotBlank String requestJson,
			@Header("oneTimeAccessToken") String oneTimeAccessToken, @Header("tenantId") String tenantId)
			throws UserLoginControllerException {
		logger.debug(" inside verifyOtpforForgotPassword of UserLoginController ");
		try {
			validateRequestBody(requestJson);
			logger.debug("request body :: " + requestJson);
			String response = loginService.verifyOtpforForgotPassword(tenantId, requestJson, oneTimeAccessToken);
			logger.debug("response  :: " + response);
			return HttpResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserLoginControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Unable to Verify User , " + e.getMessage(), e);
			throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("601"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used the new password after user verified the otps for Forgot password.
	 * @param requestBody the json containing the new password.
	 * @param oneTimeAccessToken the one time access token.
	 * @param jwtToken the one time jwt token.
	 * @return the response message of this method.
	 * @throws UserLoginControllerException if any exception occurs while performing this operation.
	 */
	@Post("/forgetPassword/createNewPassword")
	public HttpResponse<String> forgetPasswordCreateNewPassword(@Body @NotBlank String requestBody,
			@Header("oneTimeAccessToken") String oneTimeAccessToken, @Header("jwtToken") String jwtToken)
			throws UserLoginControllerException {
		try {
			logger.debug(" inside forgetPasswordCreateNewPassword of UserLoginController");
			logger.debug("requestBody : " + requestBody);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateOneTimeJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userEmail = tokenPayload.getUserEmail();
			logger.debug("userEmail :" + userEmail + " tenantId : " + tenantId);
			String response = loginService.forgetPasswordCreateNewPassword(tenantId, requestBody, oneTimeAccessToken,
					tokenPayload);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserLoginControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" exception :: " + e.getMessage(), e);
			throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("401"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to verify the otps after the first time login of the user.
	 * @param requestJson the json containing the otps.
	 * @param oneTimeAccessToken the one time access token in the header.
	 * @param tenantId the encoded tenantId.
	 * @return the response message of this method.
	 * @throws UserLoginControllerException if any exception occurs while performing this operation.
	 */
	@Post("/verifyOtpFirstTimeLogin")
	public HttpResponse<String> verifyOtpforFirstTimeLogin(@Body @NotBlank String requestJson,
			@Header("oneTimeAccessToken") String oneTimeAccessToken, @Header("tenantId") String tenantId)
			throws UserLoginControllerException {
		logger.debug(" inside verifyOtpforFirstTimeLogin of UserLoginController ");
		try {
			validateRequestBody(requestJson);
			logger.debug("request body :: " + requestJson);
			String response = loginService.verifyUser(tenantId, requestJson, oneTimeAccessToken);
			logger.debug("response  :: " + response);
			return HttpResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserLoginControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Unable to Verify User , " + e.getMessage(), e);
			throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("601"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used the new password after user verified the otps for first time login
	 * @param requestBody the json containing the new password.
	 * @param oneTimeAccessToken the one time access token.
	 * @param jwtToken the one time jwt token.
	 * @return the response message of this method.
	 * @throws UserLoginControllerException if any exception occurs while performing this operation.
	 */
	@Post("/createPasswordFirstTimeLogin")
	public HttpResponse<String> createPasswordforFirstTimeLogin(@Body @NotBlank String requestJson,
			@Header("oneTimeAccessToken") String oneTimeAccessToken, @Header("jwtToken") String jwtToken)
			throws UserLoginControllerException {
		logger.debug(" inside createPasswordforFirstTimeLogin of UserLoginController ");
		try {
			validateRequestBody(requestJson);
			logger.debug("request body :: " + requestJson);
//			if (tokenPayload == null) {
//				throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("102"), new Throwable(),
//						401, ReadPropertiesFile.readResponseProperty("102"));
//			}
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateOneTimeJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String response = loginService.createPasswordforFirstTime(tenantId, requestJson, oneTimeAccessToken,
					tokenPayload);
			logger.debug("response  :: " + response);
			return HttpResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserLoginControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Unable to create password , " + e.getMessage(), e);
			throw new UserLoginControllerException(ReadPropertiesFile.readResponseProperty("602"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * Validating the request body that it should not be empty.
	 * @param body the request body to be validated.
	 * @throws UserLoginControllerException if requestBody is empty.
	 */
	private void validateRequestBody(String body) throws UserLoginControllerException {
		if (body == null || body.isEmpty()) {
			throw new UserLoginControllerException(400, "in-valid request body ", body);
		}
	}
}