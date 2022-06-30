package com.pwc.grading.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.controller.exception.PartnerControllerException;
import com.pwc.grading.partner.service.IPartnerService;
import com.pwc.grading.user.service.registration.UserAccessManagementServiceConstants;
import com.pwc.grading.usertoken.model.TokenPayload;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.TokenValidator;
import com.pwc.grading.util.exception.TokenValidatorException;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;

/**
 * This controller is responsible for getting the partner details and training center details.
 *
 */
@Controller("gradingTool/partner")
public class PartnerController {
	private static final Logger logger = LoggerFactory.getLogger(PartnerController.class);
	@Inject
	private IPartnerService patnerService;

	/**
	 * This method is used to get the partner data.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return all the partners data
	 * @throws PartnerControllerException if any exception occurs while performing this operation.
	 */
	@Get("/")
	public HttpResponse<String> getPartnerData(@Header("jwtToken") String jwtToken) throws PartnerControllerException {
		logger.debug(" inside getPartnerData of PartnerController ");
		try {
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			logger.debug("tenantId ::" + tenantId + " userRole  : " + userRole);
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = patnerService.getAllPartners(tenantId);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (PartnerControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to  get Partner  Data , " + e.getMessage(), e);
			throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("436"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to the get the training center details for the partner.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param partnerId the id of the partner.
	 * @return all the training center details
	 * @throws PartnerControllerException  if any exception occurs while performing this operation.
	 */
	@Get("/{partnerId}/trainingCenter")
	public HttpResponse<String> getTrainingCenterDetails(@Header("jwtToken") String jwtToken,
			@PathVariable String partnerId) throws PartnerControllerException {
		logger.debug(" inside getTrainingCenterDetails of PartnerController ");
		try {
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = patnerService.getAllTrainingCenters(tenantId, partnerId);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (PartnerControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to get TrainingCenter Details , " + e.getMessage(), e);
			throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("437"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to the get the details for the partner.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param partnerId  the id of the partner.
	 * @return the partner data.
	 * @throws PartnerControllerException  if any exception occurs while performing this operation.
	 */
	@Get("/{partnerId}")
	public HttpResponse<String> getPartnerDataById(@Header("jwtToken") String jwtToken,
			@PathVariable("partnerId") String partnerId) throws PartnerControllerException {
		logger.debug(" inside getPartnerData of PartnerController ");
		try {
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = patnerService.getPartnerByPartnerId(tenantId, partnerId);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (PartnerControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to  get Partner  Data , " + e.getMessage(), e);
			throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("436"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to the get the training center details using tcId for the partner.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param partnerId  the id of the partner.
	 * @param tcId  the id of the training center.
	 * @return the training center details
	 * @throws PartnerControllerException if any exception occurs while performing this operation.
	 */
	@Get("/{partnerId}/trainingCenter/{tcId}")
	public HttpResponse<String> getTrainingCenterDetailsByTcId(@Header("jwtToken") String jwtToken,
			@PathVariable String partnerId, @PathVariable String tcId) throws PartnerControllerException {
		logger.debug(" inside getTrainingCenterDetails of PartnerController ");
		try {
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = patnerService.getTrainingCenterByTcId(tenantId, partnerId, tcId);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (PartnerControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to get TrainingCenter Details , " + e.getMessage(), e);
			throw new PartnerControllerException(ReadPropertiesFile.readResponseProperty("437"), e, 400,
					e.getMessage());
		}
	}
}
