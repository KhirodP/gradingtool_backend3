package com.pwc.grading.user.service.login;

import javax.validation.constraints.NotBlank;

import com.pwc.grading.user.service.login.exception.UserLoginServiceException;
import com.pwc.grading.usertoken.model.TokenPayload;
/**
 * An interface class which is used to perform all
 * User login related operations.
 *
 */
public interface IUserLoginService {

	/**
	 * This method is used to process the login of the user.
	 * @param requestJson the json containing the user credentials.
	 * @param tenantId the encoded tenantId
	 * @return the response if login is success.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	public String loginUser(String requestJson, String tenantId) throws UserLoginServiceException;

	/**
	 * This method is used to process the login of the user using the accessToken.
	 * @param requestJson  the json containing the user accessToken.
	 * @param tenantId the encoded tenantId
	 * @return  the response if login is success.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	public String loginWithAccessToken(String requestJson, String tenantId) throws UserLoginServiceException;

	/**
	 * This method is used to send the otp to the user.
	 * @param userEmail the user email id.
	 * @param tenantId the database name.
	 * @return the success response after otp is send.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	public String sendOtp(String userEmail, String tenantId) throws UserLoginServiceException;

	/**
	 * This method is used to verify the otp of the user.
	 * @param userEmail the user email id.
	 * @param tenantId the database name.
	 * @param otp the otp entered by the user.
	 * @return the success response after otp is verified.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	public String verifyOtp(String userEmail, String otp, String tenantId) throws UserLoginServiceException;

	/**
	 * This method is used to reset the password of the user.
	 * @param requestBody the json containing the password details.
	 * @param tenantId the database name.
	 * @param userEmail  the user email id.
	 * @return the response if password reset is success.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	public String resetPassword(String requestBody, String tenantId, String userEmail) throws UserLoginServiceException;

	/**
	 * This method is used to process the logout of the user and deleting the userToken.
	 * @param tenanId the database name.
	 * @param userEmail the user email id.
	 * @param sessionId the session id.
	 * @param requestJson the json containing the deviceId.
	 * @return the response if logout is success.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	public String logoutUser(String tenanId, String userEmail, String sessionId, String requestJson)
			throws UserLoginServiceException;

//	public String forgetPasswordCreateNewPassword(String requestBody, String tenantId, String userEmail) throws UserLoginServiceException;

	/**
	 * This method is used to send the Otps to the user when user is using forgot password service.
	 * @param requestBody the json containing the user email Id.
	 * @param tenantId  the database name.
	 * @return the response having oneTimeAccessToken in it.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	public String forgetPasswordSendOtp(String requestBody, String tenantId, String encryptedPlatform) throws UserLoginServiceException;

	/**
	 * This method is used to create password for the first time, after the user is verified in the first time login.
	 * @param requestJson the json containing the password.
	 * @param oneTimeAccessToken the access token provided to the user in the response.
	 * @param jwtToken the jwt token which is received in the response once the user verified the otps.
	 * @param tokenPayload the token and user details.
	 * @return the response message if operation is success.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	public String createPasswordforFirstTime(String requestJson, String oneTimeAccessToken, String jwtToken,
			TokenPayload tokenPayload) throws UserLoginServiceException;

	/**
	 * This method is used to verify the user for the first time.
	 * @param tenantId the database name.
	 * @param requestJson the json containing the Otps.
	 * @param oneTimeAccessToken the access token provided to the user in the response.
	 * @return the response message if operation is success.
	 * @throws UserLoginServiceException  if any exception occurs while performing this operation.
	 */
	public String verifyUser(String tenantId, @NotBlank String requestJson, String oneTimeAccessToken)
			throws UserLoginServiceException;

	/**
	 * This method is used to create the new password after the user provided the Otps for forgot password.
	 * @param tenantId  the database name.
	 * @param requestBody the json containing the password.
	 * @param oneTimeAccessToken  the access token provided to the user in the response.
	 * @param tokenPayload the token and user details.
	 * @return the response message if operation is success.
	 * @throws UserLoginServiceException if any exception occurs while performing this operation.
	 */
	String forgetPasswordCreateNewPassword(String tenantId, String requestBody, String oneTimeAccessToken,
			TokenPayload tokenPayload) throws UserLoginServiceException;

	/**
	 * This method is used to verify the user otps for the forgot password.
	 * @param tenantId  the database name.
	 * @param requestJson  the json containing the Otps.
	 * @param oneTimeAccessToken  the access token provided to the user in the response.
	 * @return the response message if operation is success.
	 * @throws UserLoginServiceException  if any exception occurs while performing this operation.
	 */
	public String verifyOtpforForgotPassword(String tenantId, @NotBlank String requestJson, String oneTimeAccessToken)
			throws UserLoginServiceException;
}
