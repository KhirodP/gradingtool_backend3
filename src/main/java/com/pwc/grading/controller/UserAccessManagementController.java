package com.pwc.grading.controller;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.controller.exception.UserAccessManagementControllerException;
import com.pwc.grading.user.service.registration.IUserRegistrationService;
import com.pwc.grading.user.service.registration.UserAccessManagementServiceConstants;
import com.pwc.grading.usertoken.model.TokenPayload;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.TokenValidator;
import com.pwc.grading.util.exception.TokenValidatorException;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.multipart.CompletedFileUpload;

/**
 * This controller is responsible for user related operations.
 *
 */
@Controller("/gradingTool/user")
public class UserAccessManagementController {

	private static final Logger logger = LoggerFactory.getLogger(UserAccessManagementController.class);
	@Inject
	private IUserRegistrationService registrationService;

	/**
	 * This method is used to register a new user.
	 * @param requestJson the JSON containing the user details.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return the response message.
	 * @throws UserAccessManagementControllerException if any exception occurs while performing this operation.
	 */
	@Post("/")
	public HttpResponse<String> registerUser(@Body @NotBlank String requestJson, @Header("jwtToken") String jwtToken)
			throws UserAccessManagementControllerException {
		try {
			logger.debug(" inside  registerUser of UserController ");
			logger.debug("request body :: " + requestJson);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			logger.debug(" User can access the service ");
			String response = registrationService.registerUser(requestJson, tenantId);
			logger.debug("response  :: " + response);

			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserAccessManagementControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to register user , " + e.getMessage(), e);
			throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("402"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to filter the existing users.
	 * @param firstName the firstname of the user.
	 * @param role the role of the user.
	 * @param phone the phone number of the user.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return the filtered users in the JSON.
	 * @throws UserAccessManagementControllerException if any exception occurs while performing this operation.
	 */
	@Get("/filter")
	public HttpResponse<String> filterUsers(@QueryValue(value = "firstName", defaultValue = "") String firstName,
			@QueryValue(value = "role", defaultValue = "") String role,
			@QueryValue(value = "phone", defaultValue = "") String phone, @Header("jwtToken") String jwtToken)
			throws UserAccessManagementControllerException {
		try {
			logger.debug(" inside  filterUsers of UserController ");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			logger.debug(" User can access the service ");
			String response = registrationService.filterUsers(firstName, role, phone, tenantId);
			logger.debug("response  :: " + response);

			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserAccessManagementControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to filter user , " + e.getMessage(), e);
			throw new UserAccessManagementControllerException("unable to filter users ", e, 400, e.getMessage());
		}
	}

	/**
	 * This method is used to upload the excel, containing the details of multiple users.
	 * @param multipeUsers the excel file uploaded object.
	 * @param jwtToken  the jwt token which is to be passed in the request header.
	 * @return the response message.
	 * @throws UserAccessManagementControllerException if any exception occurs while performing this operation.
	 */
	@Post(consumes = MediaType.MULTIPART_FORM_DATA, uri = "/upload")
	public HttpResponse<String> uploadUserAccessManagmentExcel(CompletedFileUpload multipeUsers,
			@Header("jwtToken") String jwtToken) throws UserAccessManagementControllerException {
		logger.debug(" inside  register multiple User of UserController ");
		try {
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = registrationService.registerMultipleUsers(tenantId, multipeUsers);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserAccessManagementControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to register multiple user , " + e.getMessage(), e);
			throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("402"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to update the status of the user.
	 * @param requestBody the JSON containing the user status.
	 * @param userId the id of the user.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return the response message.
	 * @throws UserAccessManagementControllerException if any exception occurs while performing this operation.
	 */
	@Patch("/status/{userId}")
	public HttpResponse<String> updateUserStatus(@Body @NotBlank String requestBody, @PathVariable String userId,
			@Header("jwtToken") String jwtToken) throws UserAccessManagementControllerException {
		try {
			logger.debug(" inside updateUserStatus of UserController");
			logger.debug("requestBody : " + requestBody);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			String tenantKey = tokenPayload.getTenantKey();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = registrationService.updateUserStatus(userId, requestBody, tenantId, tenantKey);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserAccessManagementControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to update user status, exception :: " + e.getMessage(), e);
			throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("418"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to update the existing user.
	 * @param requestBody the JSON containing the user details.
	 * @param userId the id of the user.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return the response message.
	 * @throws UserAccessManagementControllerException if any exception occurs while performing this operation.
	 */
	@Patch("/{userId}")
	public HttpResponse<String> updateUserById(@Body @NotBlank String requestBody, @PathVariable String userId,
			@Header("jwtToken") String jwtToken) throws UserAccessManagementControllerException {
		try {
			logger.debug(" inside updateUserById of UserController");
			logger.debug("requestBody : " + requestBody);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = registrationService.updateUser(userId, requestBody, tenantId,
					tokenPayload.getTenantKey());
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserAccessManagementControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to update user by id, exception :: " + e.getMessage(), e);
			throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("419"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to delete the existing user.
	 * @param userId the id of the user.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return the response message.
	 * @throws UserAccessManagementControllerException if any exception occurs while performing this operation.
	 */
	@Delete("/{userId}")
	public HttpResponse<String> deleteUserById(@PathVariable String userId, @Header("jwtToken") String jwtToken)
			throws UserAccessManagementControllerException {
		try {
			logger.debug(" inside delete user by id of UserController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			String tenantKey = tokenPayload.getTenantKey();
			String userEmail = tokenPayload.getUserEmail();
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = registrationService.deleteUserById(userId, tenantId, tenantKey, userEmail);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserAccessManagementControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to delete user by id exception :: " + e.getMessage(), e);
			throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("420"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to delete the existing multiple users.
	 * @param requestBody the JSON containing the user Ids to delete.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return the response message.
	 * @throws UserAccessManagementControllerException if any exception occurs while performing this operation.
	 */
	@Post("/deleteMultipleUsers")
	public HttpResponse<String> deleteMultipleUser(@Body String requestBody, @Header("jwtToken") String jwtToken)
			throws UserAccessManagementControllerException {
		try {
			logger.debug(" inside delete user by id of UserController");
			validateRequestBody(requestBody);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			String tenantKey = tokenPayload.getTenantKey();
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = registrationService.deleteMultipleUser(requestBody, tenantId, tenantKey,
					tokenPayload.getUserEmail());
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserAccessManagementControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to delete user by id exception :: " + e.getMessage(), e);
			throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("420"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * Validating the request body that it should not be empty.
	 * @param requestBody the request body to be validated.
	 * @throws UserAccessManagementControllerException if requestBody is empty.
	 */
	private void validateRequestBody(String requestBody) throws UserAccessManagementControllerException {
		if (requestBody == null || requestBody.isEmpty()) {
			throw new UserAccessManagementControllerException(404, "Invalid request", "Invalid request");
		}
	}

	/**
	 * This method is used to get all the users.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return all the user details in JSON.
	 * @throws UserAccessManagementControllerException if any exception occurs while performing this operation.
	 */
	@Get("/")
	public HttpResponse<String> getAllUsers(@Header("jwtToken") String jwtToken)
			throws UserAccessManagementControllerException {
		try {
			logger.debug(" inside get all user of UserController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = registrationService.getAllUsers(tenantId);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserAccessManagementControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to get all users, exception :: " + e.getMessage(), e);
			throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("401"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get user details for the account details.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return the json containing the user details.
	 * @throws UserAccessManagementControllerException if any exception occurs while performing this operation.
	 */
	@Get("/accountDetails")
	public HttpResponse<String> getUserForAccountDetails(@Header("jwtToken") String jwtToken)
			throws UserAccessManagementControllerException {
		try {
			logger.debug(" inside get User For AccountDetails of UserController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			String userEmail = tokenPayload.getUserEmail();
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = registrationService.getUserByEmail(tenantId, userEmail);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (UserAccessManagementControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to get all users, exception :: " + e.getMessage(), e);
			throw new UserAccessManagementControllerException(ReadPropertiesFile.readResponseProperty("401"), e, 400,
					e.getMessage());
		}
	}

}
