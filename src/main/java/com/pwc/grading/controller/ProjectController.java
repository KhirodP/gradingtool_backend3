package com.pwc.grading.controller;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.controller.exception.ProjectControllerException;
import com.pwc.grading.project.service.IProjectService;
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

/**
 * ProjectController class is used to create projects, forms and surveys.
 * 
 *
 */
@Controller("gradingTool/project")
public class ProjectController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
	@Inject
	private IProjectService surveyService;

	/**
	 * This method is used to create Project
	 * 
	 * @param requestJson the json containing the project details.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return  the response message of this method.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Post(consumes = MediaType.APPLICATION_JSON)
	public HttpResponse<String> createProjectData(@Body @NotBlank String requestJson,
			@Header("jwtToken") String jwtToken) throws ProjectControllerException {
		try {
			logger.debug(" inside createProjectData method SurveyController");
			logger.debug("request body :: " + requestJson);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			String response = surveyService.createProjectData(tenantId, requestJson);
			logger.debug(" response :: " + response);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to create project data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("421"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This used to create form for the project.
	 * 
	 * @param requestJson the json containing the form details.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return the response message of this method.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Post(consumes = MediaType.APPLICATION_JSON, uri = "/form")
	public HttpResponse<String> createFormData(@Body @NotBlank String requestJson, @Header("jwtToken") String jwtToken)
			throws ProjectControllerException {
		try {
			logger.debug(" inside createFormData method SurveyController");
			logger.debug("request body :: " + requestJson);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			String response = surveyService.createFormData(tenantId, requestJson);
			logger.debug(" response :: " + response);
			return HttpResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to create form data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("433"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to update the surveyData survey data contains Questions
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param surveyId the id of the survey
	 * @param requestJson the json containing the survey details
	 * @return  the response message of this method.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Patch(uri = "/survey/{surveyId}")
	public HttpResponse<String> updateSurveyData(@Header("jwtToken") String jwtToken, @PathVariable String surveyId,
			@Body @NotBlank String requestJson) throws ProjectControllerException {
		try {
			logger.debug(" inside updateSurveyData method SurveyController");
			logger.debug("request body :: " + requestJson);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			String response = surveyService.updateSurveyById(tenantId, surveyId, requestJson);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to update survey data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("406"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to update the project details
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param projectId the id of the project.
	 * @param requestJSON the json containing the project details
	 * @return the response message of this method.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Patch("/{projectId}")
	public HttpResponse<String> updateProjectData(@Header("jwtToken") String jwtToken,
			@PathVariable("projectId") String projectId, @Body @NotBlank String requestJSON)
			throws ProjectControllerException {
		try {
			logger.debug(" inside updateProjectData method SurveyController");
			logger.debug("request body :: " + requestJSON);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);

			String response = surveyService.updateProjectById(tenantId, projectId, requestJSON);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to update project data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("422"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get all the surveys
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return the json response which has the survey data.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Get("/survey")
	public HttpResponse<String> getALLSurveyData(@Header("jwtToken") String jwtToken)
			throws ProjectControllerException {
		try {
			logger.debug(" inside getALLSurveyData method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String response = surveyService.getAllSurveyData(tenantId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to find survey data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("404"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get all the projects
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return  the json response which has the Project data.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Get("/")
	public HttpResponse<String> getALLProjectData(@Header("jwtToken") String jwtToken)
			throws ProjectControllerException {
		try {
			logger.debug(" inside getALLProjectData method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
//			String userRole = tokenPayload.getUserRole();
//			if (!userRole.equals(UserAccessManagementConstants.ADMIN)) {
//				logger.error("User is not allowed for the opertion ");
//				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"),
//						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
//			}
			String response = surveyService.getAllProjectData(tenantId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to find survey data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("423"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get project details by projectId
	 * 
	 * @param jwtToken  the jwt token which is to be passed in the request header.
	 * @param projectId the id of the project.
	 * @return  the response message of this method.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Get("/{projectId}")
	public HttpResponse<String> getProjectDataById(@Header("jwtToken") String jwtToken,
			@PathVariable("projectId") String projectId) throws ProjectControllerException {
		try {
			logger.debug(" inside getProjectDataById method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
//			String userRole = tokenPayload.getUserRole();
//			if (!userRole.equals(UserAccessManagementConstants.ADMIN)) {
//				logger.error("User is not allowed for the opertion ");
//				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"),
//						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
//			}
			String response = surveyService.getProjectById(tenantId, projectId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to find project data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("423"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get survey question by surveyId
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param surveyId  the survey id
	 * @param taskType   - task type is either completed or assigned task
	 * @param auditFor the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @return the json response which has the survey data.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Get("/survey/{surveyId}")
	public HttpResponse<String> getSurveyDataById(@Header("jwtToken") String jwtToken,
			@PathVariable("surveyId") String surveyId,
			@QueryValue(value = "taskType", defaultValue = "") String taskType,
			@QueryValue(value = "auditFor", defaultValue = "") String auditFor,
			@QueryValue(value = "auditForId", defaultValue = "") String auditForId) throws ProjectControllerException {
		try {
			logger.debug(" inside getSurveyDataById method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
//			String userEmail = tokenPayload.getUserEmail();
			String userRole = tokenPayload.getUserRole();
			String response = surveyService.getSurveyById(tenantId, surveyId, userRole, auditFor, auditForId, taskType);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to find survey data ," + e.getMessage(), e);
			throw new ProjectControllerException(e.getMessage(), e, 400,
					ReadPropertiesFile.readResponseProperty("404") + e.getMessage());
		}
	}

	/**
	 * This method is used to delete the project
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param projectId the id of the project.
	 * @return the response message of this method.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Delete("/{projectId}")
	public HttpResponse<String> deleteProjectById(@Header("jwtToken") String jwtToken,
			@PathVariable("projectId") String projectId) throws ProjectControllerException {
		try {
			logger.debug(" inside deleteProjectData method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = surveyService.deleteProjectById(tenantId, projectId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to delete project data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("415"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get forms by projectId
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param projectId the id of the project.
	 * @return  the json response which has the forms data.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Get("/{projectId}/form")
	public HttpResponse<String> getFormsByProjectId(@Header("jwtToken") String jwtToken,
			@PathVariable("projectId") String projectId) throws ProjectControllerException {
		try {
			logger.debug(" inside getFormDataById method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = surveyService.getFormsByProjectId(tenantId, projectId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to find project data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("432"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get form data by id
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param formId the id of the form.
	 * @return the json response which has the form data.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Get("/form/{formId}")
	public HttpResponse<String> getFormDataById(@Header("jwtToken") String jwtToken,
			@PathVariable("formId") String formId) throws ProjectControllerException {
		try {
			logger.debug(" inside getFormDataById method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String response = surveyService.getFormById(tenantId, formId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to find project data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("432"), e, 400,
					e.getMessage());
		}
	}

//	@Get("{projectId}/form/{formId}")
//	public HttpResponse<String> getFormDataById(@Header("jwtToken") String jwtToken,
//			@PathVariable("formId") String formId) throws SurveyControllerException {
//		try {
//			logger.debug(" inside getFormDataById method SurveyController");
//			TokenPayload tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
//			if (tokenPayload == null) {
//				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
//						401, ReadPropertiesFile.readResponseProperty("101"));
//			}
//			String tenantId = tokenPayload.getTenantId();
//			String response = surveyService.getFormById(tenantId, formId);
//			logger.debug(" response :: " + response);
//			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
//		} catch (Exception e) {
//			logger.error(" unable to find project data ," + e.getMessage(), e);
//			throw new SurveyControllerException(ReadPropertiesFile.readResponseProperty("432"), e, 400, e.getMessage());
//		}
//	}
	/**
	 * This method is used to delete the form data by formId. <b>Note:</b> if the form is
	 * published then it is not possible to delete it
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param formId the id of the form.
	 * @return the response message of this method.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Delete("/form/{formId}")
	public HttpResponse<String> deleteFormtData(@Header("jwtToken") String jwtToken,
			@PathVariable("formId") String formId) throws ProjectControllerException {
		try {
			logger.debug(" inside deleteFormtData method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = surveyService.deleteFormData(tenantId, formId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to delete project data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("438"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get projects related to users
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param taskType - task type is either completed or assigned task
	 * @return  the json response which has the project data.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Get("/user")
	public HttpResponse<String> getProjectOfUser(@Header("jwtToken") String jwtToken,
			@QueryValue("taskType") String taskType) throws ProjectControllerException {
		try {
			logger.debug(" inside getProjectByUserEmail method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userEmail = tokenPayload.getUserEmail();
			String response = surveyService.getProjectDetailsOfUser(tenantId, userEmail, taskType);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to get Projects Of User ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("440"), e, 400,
					e.getMessage());
		}

	}

	/**
	 * This method is used to get forms related to user
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param projectId the id of the project.
	 * @param partnerId the id of the partner.
	 * @param taskType  - task type is either completed or assigned task
	 * @return the json response which has the forms data.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Get("/user/form/{projectId}")
	public HttpResponse<String> getFormsOfUser(@Header("jwtToken") String jwtToken, @PathVariable String projectId,
			@Header(defaultValue = "") String partnerId, @QueryValue(value = "taskType") String taskType)
			throws ProjectControllerException {
		try {
			logger.debug(" inside getFormsOfUser method SurveyController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
//			String userRole = tokenPayload.getUserRole();
			String userEmail = tokenPayload.getUserEmail();
			String response = surveyService.getFormsOfUser(tenantId, userEmail, projectId, taskType, partnerId);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to get forms Of User ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("441"), e, 400,
					e.getMessage());
		}

	}

	/**
	 * This method is used to update the form data
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param formId the id of the form.
	 * @param requestJson the json which has the form data.
	 * @return the response message of this method.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Patch(uri = "/form/{formId}")
	public HttpResponse<String> updateFormData(@Header("jwtToken") String jwtToken, @PathVariable String formId,
			@Body @NotBlank String requestJson) throws ProjectControllerException {
		try {
			logger.debug(" inside updateFormData method SurveyController");
			logger.debug("request body :: " + requestJson);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			String response = surveyService.updateFormById(tenantId, formId, requestJson);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to update form data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("431"), e, 400,
					e.getMessage());
		}
	}

	/**
	 * This method is used to publish the form. Once the form is published it is not
	 * possible to update or delete the form.
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param requestBody the json which has the form publishing data.
	 * @param formId the id of the form.
	 * @return the response message of this method.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */
	@Post(uri = "/form/publish/{formId}")
	public HttpResponse<String> publishFormData(@Header("jwtToken") String jwtToken, @Body String requestBody,
			@PathVariable String formId) throws ProjectControllerException {
		try {
			logger.debug(" inside publishFormData method ProjectController");

			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			String response = surveyService.publishFormDataById(tenantId, formId, requestBody);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to publish form data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("442"), e, 400,
					e.getMessage());
		}
	}

//	@Get(uri = "/form/publish/{formId}")
//	public HttpResponse<String> getPublishFormData(@Header("jwtToken") String jwtToken, @PathVariable String formId)
//			throws SurveyControllerException {
//		try {
//			logger.debug(" inside publishFormData method ProjectController");
//
//			TokenPayload tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
//			if (tokenPayload == null) {
//				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
//						401, ReadPropertiesFile.readResponseProperty("101"));
//			}
//			String tenantId = tokenPayload.getTenantId();
//			String userRole = tokenPayload.getUserRole();
//			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
//				logger.error("User is not allowed for the opertion ");
//				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"),
//						new Throwable(), 401, ReadPropertiesFile.readResponseProperty("400"));
//			}
//			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
//			String response = surveyService.getPublishFormDataById(tenantId, formId);
//			logger.debug(" response :: " + response);
//			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
//		} catch (Exception e) {
//			logger.error(" unable to publish form data ," + e.getMessage(), e);
//			throw new SurveyControllerException(ReadPropertiesFile.readResponseProperty("432"), e, 400, e.getMessage());
//		}
//	}
	/**
	 * This method is used to give details of the project in progress meter
	 * 
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param projectId the id of the project.
	 * @return the json response which has the progress meter data.
	 * @throws ProjectControllerException if any exception occurs while performing this operation.
	 */

	@Get(uri = "/progressMeter/{projectId}")
	public HttpResponse<String> getProgressMeterData(@Header("jwtToken") String jwtToken, String projectId)
			throws ProjectControllerException {
		try {
			logger.debug(" inside publishFormData method ProjectController");

			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			logger.debug("userRole :" + userRole + " tenantId : " + tenantId);
			String response = surveyService.getProgressMeterData(tenantId, projectId);
			logger.debug(" response :: " + response);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);

		} catch (ProjectControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to publish form data ," + e.getMessage(), e);
			throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("432"), e, 400,
					e.getMessage());
		}
	}

}
