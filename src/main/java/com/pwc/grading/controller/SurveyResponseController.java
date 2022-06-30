package com.pwc.grading.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.controller.exception.SurveyResponseControllerException;
import com.pwc.grading.surveyresponse.service.ISurveyResponseService;
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

/**
 * This class is used to perform the survey response related tasks.
 *
 */
@Controller("gradingTool/surveyResponse")
public class SurveyResponseController {

	private static final Logger logger = LoggerFactory.getLogger(SurveyResponseController.class);

	@Inject
	private ISurveyResponseService surveyResponseService;

	/**
	 * This method is used to get the survey response data of the user for the given surveyId.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param surveyId the surveyId passed as a path variable.
	 * @param auditFor the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @return the json response containing the survey response data.
	 * @throws SurveyResponseControllerException if any exception occurs while performing this operation.
	 */
	@Get(uri = "user/surveyId/{surveyId}")
	public HttpResponse<String> getsurveyResponseDataOfUserBySurveyId(@Header("jwtToken") String jwtToken,
			@PathVariable("surveyId") String surveyId,
			@QueryValue(value = "auditFor", defaultValue = "") String auditFor,
			@QueryValue(value = "auditForId", defaultValue = "") String auditForId)
			throws SurveyResponseControllerException {
		try {
			logger.debug(" inside getsurveyResponseDataByUserId method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userEmail = tokenPayload.getUserEmail();
			String response = surveyResponseService.getSurveyResponsesOfUserBySurveyId(tenantId, surveyId, userEmail,
					auditFor, auditForId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (SurveyResponseControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to get survey Response data ," + e.getMessage(), e);
			throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("407"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get the survey response data for the given surveyId.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param surveyId the surveyId passed as a path variable.
	 * @return the json response containing the survey response data.
	 * @throws SurveyResponseControllerException if any exception occurs while performing this operation.
	 */
	@Get(uri = "/surveyId/{surveyId}")
	public HttpResponse<String> getsurveyResponseDataBySurveyId(@Header("jwtToken") String jwtToken,
			@PathVariable("surveyId") String surveyId) throws SurveyResponseControllerException {
		try {
			logger.debug(" inside getsurveyResponseDataByUserId method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String response = surveyResponseService.getSurveyResponsesBySurveyId(tenantId, surveyId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (SurveyResponseControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to find survey Response data ," + e.getMessage(), e);
			throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("407"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get the survey response data for the given surveyResponseId.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param surveyResponseId the id of the survey response.
	 * @return the json response containing the survey response data.
	 * @throws SurveyResponseControllerException if any exception occurs while performing this operation.
	 */
	@Get(uri = "/{surveyResponseId}")
	public HttpResponse<String> getSurveyResponseDataBySurveyResponseId(@Header("jwtToken") String jwtToken,
			@PathVariable("surveyResponseId") String surveyResponseId) throws SurveyResponseControllerException {
		try {
			logger.debug(" inside getSurveyResponseDataBySurveyResponseId method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String response = surveyResponseService.getSurveyResponseBySurveyResponseById(tenantId, surveyResponseId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (SurveyResponseControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to find surveyReponse data ," + e.getMessage(), e);
			throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("407"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to add the survey response data.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param requestJSON the JSON containing the survey response data.
	 * @return the json response message.
	 * @throws SurveyResponseControllerException if any exception occurs while performing this operation.
	 */
	@Post("/")
	public HttpResponse<String> addSurveyResponseData(@Header("jwtToken") String jwtToken, @Body String requestJSON)
			throws SurveyResponseControllerException {
		try {
			logger.debug(" inside addSurveyData method SurveyController");
			validateRequestBody(requestJSON);
			logger.debug("request body :: " + requestJSON);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userEmail = tokenPayload.getUserEmail();
			String response = surveyResponseService.addSurveyResponseData(tenantId, requestJSON, userEmail);
			logger.debug(" response :: " + response);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (SurveyResponseControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to store survey data ," + e.getMessage(), e);
			throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("408"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to update the survey response data.
	 * @param jwtToken  the jwt token which is to be passed in the request header.
	 * @param surveyResponseId the id of the survey response.
	 * @param requestJSON the JSON containing the survey response data.
	 * @return the json response message.
	 * @throws SurveyResponseControllerException if any exception occurs while performing this operation.
	 */
	@Patch(uri = "/{surveyResponseId}")
	public HttpResponse<String> updateSurveyResponseData(@Header("jwtToken") String jwtToken,
			@PathVariable("surveyResponseId") String surveyResponseId, @Body String requestJSON)
			throws SurveyResponseControllerException {
		try {
			logger.debug(" inside updateSurveyData method SurveyController");
			validateRequestBody(requestJSON);
			logger.debug("request body :: " + requestJSON);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userEmail = tokenPayload.getUserEmail();
			String response = surveyResponseService.updateSurveyResponseById(tenantId, surveyResponseId, requestJSON,
					userEmail);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (SurveyResponseControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to update survey data ," + e.getMessage(), e);
			throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("409"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to delete the survey response data for the given surveyResponseId.
	 * @param jwtToken  the jwt token which is to be passed in the request header.
	 * @param surveyResponseId the id of the survey response.
	 * @return the json response message.
	 * @throws SurveyResponseControllerException if any exception occurs while performing this operation.
	 */
	@Delete(uri = "/{surveyResponseId}")
	public HttpResponse<String> deleteSurveyData(@Header("jwtToken") String jwtToken,
			@PathVariable("surveyResponseId") String surveyResponseId) throws SurveyResponseControllerException {
		try {
			logger.debug(" inside getALLSurveyData method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String response = surveyResponseService.deleteSurveyResponseById(tenantId, surveyResponseId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (SurveyResponseControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to delete survey data ," + e.getMessage(), e);
			throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("410"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get the audit data.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param surveyId  the surveyId passed as a path variable.
	 * @param auditFor training center or partner.
	 * @param tcId the id of the training center
	 * @param partnerId the id of the partner
	 * @return the json response containing the audit data.
	 * @throws SurveyResponseControllerException if any exception occurs while performing this operation.
	 */
	@Get(uri = "/auditData")
	public HttpResponse<String> getAuditData(@Header("jwtToken") String jwtToken,
			@QueryValue(value = "surveyId") String surveyId, @QueryValue(value = "auditFor") String auditFor,
			@QueryValue(value = "tcId", defaultValue = "") String tcId,
			@QueryValue(value = "partnerId", defaultValue = "") String partnerId)
			throws SurveyResponseControllerException {
		try {
			logger.debug(" inside getAuditData method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String response = surveyResponseService.getAuditData(tenantId, surveyId, auditFor, tcId, partnerId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (SurveyResponseControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to delete survey data ," + e.getMessage(), e);
			throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("410"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to send otp to the user.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param auditFor training center or partner.
	 * @param auditForId training center id or partner id.
	 * @return the json response message.
	 * @throws SurveyResponseControllerException if any exception occurs while performing this operation.
	 */
	@Get("submit/otp")
	public HttpResponse<String> sendOtp(@Header("jwtToken") String jwtToken,
			@QueryValue(value = "auditFor", defaultValue = "") String auditFor,
			@QueryValue(value = "auditForId", defaultValue = "") String auditForId)
			throws SurveyResponseControllerException {
		try {
			logger.debug(" inside sendOtp of SurveyResponseController");
			logger.debug("jwtToken : " + jwtToken);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userEmail = tokenPayload.getUserEmail();
			logger.debug("userEmail :" + userEmail + " tenantId : " + tenantId);
			String response = surveyResponseService.sendOtp(userEmail, tenantId, auditFor, auditForId);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (SurveyResponseControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to send otp, exception :: " + e.getMessage(), e);
			throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("426"), e, 400,
					e.getMessage());
		}

	}

	/**
	 *  This method is used to verify otp from the user.
	 * @param jwtToken  the jwt token which is to be passed in the request header.
	 * @param otp otp entered by the user.
	 * @param auditFor training center or partner.
	 * @param auditForId training center id or partner id.
	 * @return the json response message.
	 * @throws SurveyResponseControllerException if any exception occurs while performing this operation.
	 */
	@Get("submit/verifyOtp")
	public HttpResponse<String> verifyOtp(@Header("jwtToken") String jwtToken, @Header("otp") String otp,
			@QueryValue(value = "auditFor", defaultValue = "") String auditFor,
			@QueryValue(value = "auditForId", defaultValue = "") String auditForId)
			throws SurveyResponseControllerException {
		try {
			logger.debug(" inside sendOtp of SurveyResponseController");
			logger.debug("jwtToken : " + jwtToken);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userEmail = tokenPayload.getUserEmail();
			logger.debug("userEmail :" + userEmail + " tenantId : " + tenantId);
			String response = surveyResponseService.verifyOtp(userEmail, otp, tenantId, auditFor, auditForId);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (SurveyResponseControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("invalid otp, exception :: " + e.getMessage(), e);
			throw new SurveyResponseControllerException(ReadPropertiesFile.readResponseProperty("427"), e, 400,
					e.getMessage());
		}

	}

	/**
	 * To validate the request body for not empty.
	 * @param requestBody the request body.
	 * @throws SurveyResponseControllerException if request body is empty.
	 */
	private void validateRequestBody(String requestBody) throws SurveyResponseControllerException {
		if (requestBody == null || requestBody.isEmpty()) {
			logger.error("requestBody cant be null or empty");
			throw new SurveyResponseControllerException(401, "request cant Be empty or null",
					ReadPropertiesFile.readResponseProperty("425"));
		}
	}

}
