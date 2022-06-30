package com.pwc.grading.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.controller.exception.SchedulerControllerException;
import com.pwc.grading.scheduler.service.ISchedulerService;
import com.pwc.grading.user.service.registration.UserAccessManagementServiceConstants;
import com.pwc.grading.usertoken.model.TokenPayload;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.TokenValidator;
import com.pwc.grading.util.exception.TokenValidatorException;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.QueryValue;

@Controller("gradingTool/scheduler")
/**
 * SchedulerController class used to assign audits for Field-Auditor
 * 
 *
 */
public class SchedulerController {
	private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);
	@Inject
	private ISchedulerService ischedulerService;

	/**
	 * This method is used to get the all the scheduler Data
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param schedulerType - it can be either ratingForm or gradingForm
	 * @return the json response which has the scheduler data.
	 * @throws SchedulerControllerException if any exception occurs when performing the operation.
	 */
	@Get("/")
	public HttpResponse<String> getSchedulerData(@Header("jwtToken") String jwtToken,
			@QueryValue(value = "schedulerType") String schedulerType) throws SchedulerControllerException {
		try {
			logger.debug(" inside getSchedulerData method SchedulerController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new SchedulerControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new SchedulerControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new SchedulerControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String schedulerResponse = ischedulerService.getScheduler(tenantId, schedulerType);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(schedulerResponse);
		} catch (SchedulerControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.debug("unable to get scheduelr data " + e.getMessage());
			throw new SchedulerControllerException(404, e.getMessage(), "unable get Scheduler data");
		}

	}

	/**
	 * This method is used to filter the scheduler date by projectName partnerName
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param projectName the name of the project.
	 * @param partnerName the name of the partner.
	 * @param schedulerType - it can be either ratingForm or gradingForm
	 * @return the json response which has the scheduler data.
	 * @throws SchedulerControllerException if any exception occurs when performing the operation.
	 */
	@Get("/filter")
	public HttpResponse<String> filterSchedulers(@Header("jwtToken") String jwtToken,
			@QueryValue(value = "projectName", defaultValue = "") String projectName,
			@QueryValue(value = "partnerName", defaultValue = "") String partnerName,
			@QueryValue(value = "schedulerType") String schedulerType) throws SchedulerControllerException {
		try {
			logger.debug(" inside filterSchedulers method SchedulerController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new SchedulerControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new SchedulerControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new SchedulerControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String schedulerResponse = ischedulerService.filterSchedulers(tenantId, projectName, partnerName,
					schedulerType);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(schedulerResponse);
		} catch (SchedulerControllerException e) {
			throw e;
		} catch (Exception e) {
			throw new SchedulerControllerException(404, e.getMessage(), "unable to filter Scheduler data");
		}

	}

	/**
	 * This method is used to update the scheduler data
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param requestBody the json which has the scheduler data.
	 * @param schedulerType it can be either ratingForm or gradingForm
	 * @return the response message of this method.
	 * @throws SchedulerControllerException if any exception occurs when performing the operation.
	 */
	@Patch("/")
	public HttpResponse<String> updateSchedulerData(@Header("jwtToken") String jwtToken, @Body String requestBody,
			@QueryValue(value = "schedulerType") String schedulerType) throws SchedulerControllerException {
		try {
			logger.debug(" inside updateSchedulerData method SchedulerController" + requestBody);
			validateRequestBody(requestBody);
			logger.debug("requestBody ::" + requestBody);
			TokenPayload tokenPayload = null;
			try {// validate the jwtToken
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new SchedulerControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new SchedulerControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new SchedulerControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = ischedulerService.updateScheduler(tenantId, requestBody, schedulerType);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (SchedulerControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to update scheduler " + e.getMessage(), e.getMessage());
			throw new SchedulerControllerException(404, e.getMessage(), "unable to update scheduler ");
		}

	}

	/**
	 * To validate the request body for not empty.
	 * @param requestBody the request body.
	 * @throws SchedulerControllerException if request body is empty.
	 */
	private void validateRequestBody(String requestBody) throws SchedulerControllerException {
		if (requestBody.isEmpty() || requestBody == null) {
			logger.error("requestBody cant be null or empty");
			throw new SchedulerControllerException(401, "requestBody cant be null or empty",
					ReadPropertiesFile.readResponseProperty("425"));
		}
	}
}
