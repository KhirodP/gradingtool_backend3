package com.pwc.grading.controller;

import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.controller.exception.MasterDataManagmentControllerException;
import com.pwc.grading.masterdata.service.IMasterDataManagmentService;
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
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.multipart.CompletedFileUpload;

/**
 * MasterDataManagmentController is used to map between the partner and the
 * 
 *
 */
@Controller("gradingTool/masterData")
public class MasterDataManagmentController {

	private static final Logger logger = LoggerFactory.getLogger(MasterDataManagmentController.class);
	@Inject
	private IMasterDataManagmentService patnerService;

	/**
	 * This method is used to add project master data
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param requestBody the request json containing the mapping details.
	 * @return the response message of this method.
	 * @throws MasterDataManagmentControllerException if any exception occurs while performing this operation.
	 */
	@Post("/")
	public HttpResponse<String> addProjectMasterData(@Header("jwtToken") String jwtToken, @Body String requestBody)
			throws MasterDataManagmentControllerException {
		try {
			logger.debug(" inside addProjectMasterData of MasterDataManagmentController");
			validateRequestBody(requestBody);
			logger.debug("request Body is " + StringEscapeUtils.escapeJava(requestBody));
			TokenPayload tokenPayload;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = patnerService.addProjectMasterData(tenantId, requestBody);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (MasterDataManagmentControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to add Project Master Data ," + e.getMessage(), e);
			throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("430"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get project master mapping Data
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return the response message of this method.
	 * @throws MasterDataManagmentControllerException if any exception occurs while performing this operation.
	 */
	@Get("/")
	public HttpResponse<String> getMasterMappingData(@Header("jwtToken") String jwtToken)
			throws MasterDataManagmentControllerException {
		logger.debug(" inside getMappingData of MasterDataManagmentController ");
		try {
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = patnerService.getMasterMappingData(tenantId);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (MasterDataManagmentControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to get Mapping Data , " + e.getMessage(), e);
			throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("433"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to delete the multiple mapping data
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param requestBody the json containing the multiple mapping ids.
	 * @return the response message of this method.
	 * @throws MasterDataManagmentControllerException if any exception occurs while performing this operation.
	 */
	@Post(uri = "/deleteMultipleMapping")
	public HttpResponse<String> deleteMultipleMappingDataById(@Header("jwtToken") String jwtToken,
			@Body String requestBody) throws MasterDataManagmentControllerException {
		logger.debug(" inside deleteMultipleMappingDataById of MasterDataManagmentController ");
		try {
			validateRequestBody(requestBody);
			logger.debug("request Body is " + StringEscapeUtils.escapeJava(requestBody));
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}

			String response = patnerService.deleteMultipleMappingDataById(tenantId, requestBody);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (MasterDataManagmentControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to get Mapping Data , " + e.getMessage(), e);
			throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("435"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to update the mapping data
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param mappingId the id of the project mapping.
	 * @param requestBody the request json containing the mapping details.
	 * @return the response message of this method.
	 * @throws MasterDataManagmentControllerException if any exception occurs while performing this operation.
	 */
	@Patch(uri = "/{mappingId}")
	public HttpResponse<String> updateProjectMasterData(@Header("jwtToken") String jwtToken,
			@PathVariable("mappingId") String mappingId, @Body String requestBody)
			throws MasterDataManagmentControllerException {
		logger.debug(" inside updateProjectMasterData of MasterDataManagmentController ");
		try {
			validateRequestBody(requestBody);
			logger.debug("request Body is " + StringEscapeUtils.escapeJava(requestBody));
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = patnerService.updateProjectMasterData(tenantId, mappingId, requestBody);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (MasterDataManagmentControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to get Mapping Data , " + e.getMessage(), e);
			throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("432"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * this method is used to filete the mapping by projectName, partnerName and Tcid
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param projectName the name of the project
	 * @param partnerName the name of the partner.
	 * @param tcId the name of the tcId.
	 * @return the response message of this method.
	 * @throws MasterDataManagmentControllerException if any exception occurs while performing this operation.
	 */
	@Get(uri = "/filter")
	public HttpResponse<String> filterProjectMasterData(@Header("jwtToken") String jwtToken,
			@QueryValue(value = "projectName", defaultValue = "") String projectName,
			@QueryValue(value = "partnerName", defaultValue = "") String partnerName,
			@QueryValue(value = "tcId", defaultValue = "") String tcId) throws MasterDataManagmentControllerException {
		logger.debug(" inside filterProjectMasterData of MasterDataManagmentController ");
		try {
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = patnerService.filterProjectMasterData(tenantId, projectName, partnerName, tcId);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (MasterDataManagmentControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to get Mapping Data , " + e.getMessage(), e);
			throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("434"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to upload MasterData Excel file
	 *  
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param projectId the id of th project
	 * @param uploadExcel the instance of the excel upload
	 * @return the response message of this method.
	 * @throws MasterDataManagmentControllerException if any exception occurs while performing this operation.s
	 */
	@Post(uri = "/upload", consumes = MediaType.MULTIPART_FORM_DATA)
	public HttpResponse<String> uploadProjectMasterDataFromExcel(@Header("jwtToken") String jwtToken,
			@Header("projectId") String projectId, CompletedFileUpload uploadExcel)
			throws MasterDataManagmentControllerException {
		logger.debug(" inside  uploadProjectMasterDataFromExcel of PartnerController ");
		try {
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("101"),
						new Throwable(), 401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new MasterDataManagmentControllerException(ReadPropertiesFile.readResponseProperty("400"),
						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = patnerService.uploadProjectMasterDataFromExcel(tenantId, uploadExcel, projectId);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (MasterDataManagmentControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to add partner , " + e.getMessage(), e);
			throw new MasterDataManagmentControllerException(e.getMessage(), e, 400, e.getMessage());
		}
	}

	/**
	 * To validate the request body for not empty.
	 * @param requestBody the request body.
	 * @throws MasterDataManagmentControllerException if request body is empty.
	 */
	private void validateRequestBody(String requestBody) throws MasterDataManagmentControllerException {
		if (requestBody == null || requestBody.isEmpty()) {
			logger.error("requestBody cant be null or empty");
			throw new MasterDataManagmentControllerException(401, "requestBody cant be null or empty",
					ReadPropertiesFile.readResponseProperty("425"));
		}
	}

}
