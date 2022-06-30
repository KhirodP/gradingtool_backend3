package com.pwc.grading.project.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.masterdata.dao.IMasterDataManagmentDao;
import com.pwc.grading.masterdata.dao.exception.MasterDataManagmentDaoException;
import com.pwc.grading.masterdata.model.GradingEnable;
import com.pwc.grading.masterdata.model.ProjectMapping;
import com.pwc.grading.mediabucket.dao.IMediaDao;
import com.pwc.grading.partner.dao.IPartnerDao;
import com.pwc.grading.partner.dao.exception.PartnerDaoException;
import com.pwc.grading.partner.model.PartnerDetails;
import com.pwc.grading.partner.model.TrainingCenterDetails;
import com.pwc.grading.project.dao.IProjectDao;
import com.pwc.grading.project.dao.exception.ProjectDaoException;
import com.pwc.grading.project.model.AuditDetails;
import com.pwc.grading.project.model.FormData;
import com.pwc.grading.project.model.FormDataOfUser;
import com.pwc.grading.project.model.ProjectData;
import com.pwc.grading.project.model.ProjectDetailOfUser;
import com.pwc.grading.project.model.Survey;
import com.pwc.grading.project.service.IProjectService;
import com.pwc.grading.project.service.ProjectServiceConstant;
import com.pwc.grading.project.service.exception.ProjectServiceException;
import com.pwc.grading.reportingdb.ReportingDBService;
import com.pwc.grading.reportingdb.assigner.AddGradingJobAssigner;
import com.pwc.grading.reportingdb.assigner.AddRatingJobAssigner;
import com.pwc.grading.reportingdb.assigner.DeleteJobAssigner;
import com.pwc.grading.reportingdb.assigner.exception.AssignerException;
import com.pwc.grading.reportingdb.constant.ReportingDbJSONConstant;
import com.pwc.grading.scheduler.dao.ISchedulerDao;
import com.pwc.grading.scheduler.dao.exception.SchedulerDaoException;
import com.pwc.grading.scheduler.model.GradingType;
import com.pwc.grading.scheduler.model.RatingType;
import com.pwc.grading.scheduler.model.SchedulerMapping;
import com.pwc.grading.surveyresponse.dao.ISurveyResponseDao;
import com.pwc.grading.surveyresponse.dao.exception.SurveyResponseDaoException;
import com.pwc.grading.surveyresponse.model.SurveyResponse;
import com.pwc.grading.surveyresponse.service.SurveyResponseServiceConstant;
import com.pwc.grading.tracking.dao.ITrackingDao;
import com.pwc.grading.user.dao.IUserDao;
import com.pwc.grading.user.dao.exception.UserDaoException;
import com.pwc.grading.user.model.User;
import com.pwc.grading.user.service.registration.UserAccessManagementServiceConstants;
import com.pwc.grading.util.JsonUtill;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.exception.JsonUtillException;

/**
 * Implementation class for {@link IProjectService}
 *
 */
@Singleton
public class ProjectServiceImpl implements IProjectService {
	private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
	@Inject
	private IProjectDao surveyDao;
	@Inject
	private ISurveyResponseDao iSurveyResponseDao;
	@Inject
	private IUserDao iUserDao;
	@Inject
	private IMasterDataManagmentDao masterDataService;

	@Inject
	private ITrackingDao trackingDao;
	
	@Inject
	private IPartnerDao iPartnerDao;
	@Inject
	private ISchedulerDao iSchedulerDao;

	@Inject
	private IMediaDao mediaDao;
	/**
	 * this method is used to create ProjectData
	 * 
	 * @param tenantId
	 * @param requestJson
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String createProjectData(String databaseName, String requestJSON) throws ProjectServiceException {
		try {
			logger.debug("inside createProjectData method of SurveyServiceImpl , databaseName:  " + databaseName);
			logger.debug(" requestJSON :: " + requestJSON);
			JSONObject requestProjectData = (JSONObject) JSONValue.parse(requestJSON);
			ProjectData projectData = getProjectDataFromRequest(requestProjectData);
			validateDeadLine(projectData);
			String projectId = surveyDao.createProjectData(databaseName, projectData);
			logger.info(" Project data successfully added");
			JSONObject responseJSON = new JSONObject();
			responseJSON.put(ProjectServiceConstant.PROJECT_ID, projectId);
			responseJSON.put(ProjectServiceConstant.RESPONSE_MESSAGE, ReadPropertiesFile.readResponseProperty("210"));
			return responseJSON.toString();
		} catch (Exception e) {
			logger.error(" unable to create project " + e.getMessage(), e);
			throw new ProjectServiceException(" unable to create Project " + e.getMessage(), e);
		}
	}

	/**
	 * this method is used to create FormData
	 * 
	 * @param tenantId
	 * @param requestJson
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String createFormData(String tenantId, String requestJson) throws ProjectServiceException {
		Connection connection = null;
		try {
			JSONObject requestBody = (JSONObject) JSONValue.parseWithException(requestJson);
			FormData formData = buildFormDataFromJson(requestBody);
			ProjectData projectById = surveyDao.getProjectById(tenantId, formData.getProjectId());
			if (projectById == null) {
				throw new ProjectServiceException("Project ID does not exist");
			}
			validateProjectEndDate(projectById);
			logger.debug("Project End date is valid, proceeding to create form..");
			connection = MSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			Map<String, String> idMap = surveyDao.createFormData(connection, tenantId, formData);
			String formId = idMap.get(ProjectServiceConstant.FORM_ID);
			String surveyId = idMap.get(ProjectServiceConstant.SURVEY_ID);
			surveyDao.addSurveyData(connection, tenantId, surveyId);
			formData.setFormId(formId);
			formData.setSurveyId(surveyId);
			// no need to add form to scheduler because by default form will not be publish
			// so while publishing the form you have to add the form to scheduler
			// add forms to scheduler
			// addSchedulerData(connection, tenantId, formData);
			connection.commit();
			JSONObject responseJSON = new JSONObject(idMap);
			responseJSON.put(ProjectServiceConstant.RESPONSE_MESSAGE, ReadPropertiesFile.readResponseProperty("203"));
			return responseJSON.toString();
		} catch (Exception e) {
			if (connection != null) {
				try {
					logger.info(" ---------- Rolling Back ----------");
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw new ProjectServiceException("unable to create form, ::" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(null, connection);
		}
	}

	/**
	 * this method is used to get All Project Data
	 * 
	 * @param databaseName
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getAllProjectData(String databaseName) throws ProjectServiceException {
		try {
			JSONObject responseJSON = new JSONObject();
			logger.debug(" inside getAllProjectData method of SurveyServiceImpl , databaseName:  " + databaseName);
			List<ProjectData> projectsList = surveyDao.getAllProjects(databaseName);
			JSONArray projectJSONData = getJSONFromProjectList(projectsList);
			responseJSON.put(ProjectServiceConstant.PROJECTS, projectJSONData);
			return responseJSON.toString();
		} catch (Exception e) {
			logger.error(" unable to find Projcet Data ", e);
			throw new ProjectServiceException(" unable to find Project Data " + e.getMessage(), e);
		}
	}

	/**
	 * this method is used to get All SurveyData
	 * 
	 * @param databaseName
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getAllSurveyData(String databaseName) throws ProjectServiceException {
		try {
			logger.debug(" inside getAllSurveyData method of SurveyServiceImpl , databaseName:  " + databaseName);
			List<Survey> survey = surveyDao.getAllSuveys(databaseName);
			JSONObject responseJSON = new JSONObject();
			JSONArray surveyJSONData = getJSONFromSurveyList(survey);
			responseJSON.put(ProjectServiceConstant.SURVEY, surveyJSONData);
			return responseJSON.toString();
		} catch (Exception e) {
			logger.error(" unable to find Survey Data ", e);
			throw new ProjectServiceException(" unable to find Survey Data " + e.getMessage(), e);
		}

	}

	/**
	 * this method is used to get Project By Id
	 * 
	 * @param databaseName
	 * @param id
	 * @return
	 * @throws ProjectServiceException
	 * @throws ProjectDaoException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getProjectById(String databaseName, String id) throws ProjectServiceException, ProjectDaoException {
		try {
			logger.debug("inside getProjectById method of SurveyServiceImpl , databaseName:  " + databaseName);
			validateId(id, ProjectServiceConstant.PROJECT_ID);
			logger.debug(" id :: " + id);
			ProjectData projectDetails = surveyDao.getProjectById(databaseName, id);
			if (projectDetails == null) {
				throw new ProjectServiceException(" invalid project Id");
			}
			JSONObject responseJSON = new JSONObject();
			JSONObject surveyJSON = getJSONFromProjectDetails(projectDetails);
			responseJSON.put(ProjectServiceConstant.PROJECT, surveyJSON);
			return responseJSON.toString();
		} catch (ProjectDaoException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to find project Data ", e);
			throw new ProjectServiceException(" unable to find project Data " + e.getMessage(), e);
		}
	}

	/**
	 * this method is used to get Form By Id
	 * 
	 * @param tenantId
	 * @param formId
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getFormById(String databaseName, String formId) throws ProjectServiceException {
		try {
			logger.debug("inside getSurveyById method of SurveyServiceImpl , databaseName:  " + databaseName);
			validateId(formId, ProjectServiceConstant.FORM_ID);
			logger.debug(" id :: " + formId);
			FormData formData = surveyDao.getFormById(databaseName, formId);
			if (formData == null) {
				throw new ProjectServiceException(" invalid form Id");
			}
			JSONObject responseJSON = new JSONObject();
			JSONObject surveyJSON = buildJsonFromFormData(formData);
			responseJSON.put(ProjectServiceConstant.Form_Data, surveyJSON);
			return responseJSON.toString();
		} catch (Exception e) {
			logger.error(" unable to find form Data ", e);
			throw new ProjectServiceException(" unable to find form Data " + e.getMessage(), e);
		}
	}

	/**
	 * this method is used to update Project By Id
	 * 
	 * @param databaseName
	 * @param id
	 * @param requestJSON
	 * @return
	 * @throws ProjectServiceException
	 * @throws ProjectDaoException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String updateProjectById(String databaseName, String id, String requestJSON)
			throws ProjectServiceException, ProjectDaoException {
		try {
			logger.debug(" inside updateProjectById method of SurveyServiceImpl , databaseName:  " + databaseName);
			validateId(id, ProjectServiceConstant.PROJECT_ID);
			logger.debug(" id :: " + id);
			boolean projectExist = surveyDao.isProjectExist(databaseName, id);
			if (!projectExist) {
				throw new ProjectServiceException("project id does not exist");
			}

			logger.debug(" requestJSON :: " + requestJSON);
			JSONObject requestProjectJson = (JSONObject) JSONValue.parse(requestJSON);
			ProjectData project = getProjectDataFromRequest(requestProjectJson);
			validateDeadLine(project);
			project.setProjectId(id);
			surveyDao.updateProjectData(databaseName, project);
			logger.info(" Project data successfully updated ");
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("msg", ReadPropertiesFile.readResponseProperty("226"));
			return responseJSON.toString();
		} catch (ProjectServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to update Project Data " + e.getMessage(), e);
			throw new ProjectServiceException(" unable to update Survey Data " + e.getMessage(), e);
		}
	}

	/**
	 * this method is used to update Form By formId
	 * 
	 * @param tenantId
	 * @param formId
	 * @param requestJson
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String updateFormById(String databaseName, String formId, String requestJSON)
			throws ProjectServiceException {
		try {
			logger.debug(" inside updateFormById method of SurveyServiceImpl , databaseName:  " + databaseName);
			validateId(formId, ProjectServiceConstant.FORM_ID);
			logger.debug(" id :: " + formId);
			validateUpdateFormRequest(databaseName, formId);
			logger.debug(" requestJSON :: " + requestJSON);
			JSONObject requestSurveyJson = (JSONObject) JSONValue.parseWithException(requestJSON);
			FormData formData = buildFormDataFromJson(requestSurveyJson);
			surveyDao.updateFormData(databaseName, formId, formData);
			JSONObject responseJSON = new JSONObject();
			responseJSON.put(ProjectServiceConstant.RESPONSE_MESSAGE, ReadPropertiesFile.readResponseProperty("228"));
			return responseJSON.toString();
		} catch (ProjectServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to update form Data " + e.getMessage(), e);
			throw new ProjectServiceException(" unable to update form Data " + e.getMessage(), e);
		}
	}

	/**
	 * this method is used to update Survey By Id
	 * 
	 * @param databaseName
	 * @param id
	 * @param requestSurvey
	 * @return
	 * @throws ProjectServiceException
	 * @throws ProjectDaoException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String updateSurveyById(String databaseName, String surveyId, String requestJSON)
			throws ProjectServiceException, ProjectDaoException {
		try {
			logger.debug(" inside updateSurveyById method of SurveyServiceImpl , databaseName:  " + databaseName);
			validateId(surveyId, ProjectServiceConstant.SURVEY_ID);
			logger.debug(" id :: " + surveyId);
			validateUpdateSurvey(databaseName, surveyId);
			logger.debug(" requestJSON :: " + requestJSON);
			JSONObject requestSurveyJson = (JSONObject) JSONValue.parse(requestJSON);
			Survey survey = getSurveyFromRequest(requestSurveyJson);
			survey.setSurveyId(surveyId);
			Integer totalScore = calculateMaxScore(survey);
			logger.debug("total score " + totalScore);
			survey.setMaxScore(totalScore);
			surveyDao.updateSurveyData(databaseName, survey);
			logger.info(" Survey data successfully updated ");
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("msg", ReadPropertiesFile.readResponseProperty("205"));
			return responseJSON.toString();
		} catch (ProjectServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to update Survey Data " + e.getMessage(), e);
			throw new ProjectServiceException(" unable to update Survey Data " + e.getMessage(), e);
		}
	}

	/**
	 * this method is used to get Survey By Id
	 * 
	 * @param databaseName
	 * @param id
	 * @param userRole
	 * @param auditFor
	 * @param auditForId
	 * @param taskType
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getSurveyById(String databaseName, String id, String userRole, String auditFor, String auditForId,
			String taskType) throws ProjectServiceException {
		try {
			logger.debug("inside getSurveyById method of SurveyServiceImpl , databaseName:  " + databaseName);
			validateId(id, ProjectServiceConstant.SURVEY_ID);
			logger.debug(" id :: " + id);
			Survey survey = surveyDao.getSurveyById(databaseName, id);
			logger.debug("survey " + survey);
			if (survey == null) {
				throw new ProjectServiceException(" invalid survery Id");
			}
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				if (taskType.equals(ProjectServiceConstant.ASSIGNED_TASK)) {
					validateDeadLine(databaseName, survey, userRole, auditFor, auditForId);
				} else if (taskType.equals(ProjectServiceConstant.COMPLETED_TASK)) {
					logger.debug("no condition applied, just returning the survey");
				} else {
					throw new ProjectServiceException("invalid/empty task type");
				}
			}
			JSONObject responseJSON = new JSONObject();
			JSONObject surveyJSON = getJSONFromSurvey(survey);
			responseJSON.put(ProjectServiceConstant.SURVEY, surveyJSON);
			return responseJSON.toString();
		} catch (ProjectServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to find Survey Data ", e);
			throw new ProjectServiceException(" unable to find Survey Data " + e.getMessage(), e);
		}
	}

	/**
	 * this method is used to get Forms By ProjectId
	 * 
	 * @param tenantId
	 * @param projectId
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getFormsByProjectId(String tenantId, String projectId) throws ProjectServiceException {
		try {
			validateId(projectId, ProjectServiceConstant.PROJECT_ID);
			List<FormData> formDataList = surveyDao.getFormsByProjectId(tenantId, projectId);
			JSONArray formArray = buildJsonFromFormDataList(formDataList);
			JSONObject responseJson = new JSONObject();
			responseJson.put(ProjectServiceConstant.RESPONSE_MESSAGE, formArray);
			return responseJson.toString();
		} catch (Exception e) {
			logger.error(" unable to update forms " + e.getMessage(), e);
			throw new ProjectServiceException(" unable to update forms " + e.getMessage(), e);
		}

	}

	/**
	 * this method is used to delete Project By Id
	 * 
	 * @param databaseName
	 * @param id
	 * @return
	 * @throws ProjectServiceException
	 * @throws ProjectDaoException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deleteProjectById(String databaseName, String projectId)
			throws ProjectServiceException, ProjectDaoException {
		Connection connection = null;
		List<String> allMediaIdsForProject = new ArrayList<String>();
		try {
			logger.debug(" inside deleteProjectById method of SurveyServiceImpl , databaseName:  " + databaseName);
			validateId(projectId, ProjectServiceConstant.PROJECT_ID);
//			checkMappingBeforDeletingProject(databaseName, projectId);
			logger.debug(" id :: " + projectId);
			List<FormData> forms = surveyDao.getFormsByProjectId(databaseName, projectId);
			connection = MSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			
//			deleteMappingby projectId  [METHOD]
			masterDataService.deleteProjectMappingForProjectId(connection, databaseName, projectId);
			logger.debug(">>> Project Mappings are deleted for projectId: "+projectId);
			for (FormData form : forms) {
				String formId = form.getFormId();				
				FormData formData = surveyDao.getFormById(connection,databaseName, formId);
				String surveyId = formData.getSurveyId();
//				validateDeleteForm(connection, databaseName, formData);
				logger.debug(">>> Processing Form Deletion :: FormId ["+formId+"], Its SurveyId ["+surveyId+"]");
	
				List<String> allMediaIdsForSurvey = getMediaIdListFromSurveyId(connection,databaseName,surveyId);
				logger.debug("For surveyId '"+surveyId+"' , Media count is : "+allMediaIdsForSurvey.size());
				if(allMediaIdsForSurvey.size() > 0 ) {
					logger.debug("Adding "+allMediaIdsForSurvey.size()+" mediaIds to allMediaIdsForProject list.");
					allMediaIdsForProject.addAll(allMediaIdsForSurvey);
				}
				//	deleteSurveyResponsesTrackingBySurveyId [METHOD]
				trackingDao.deleteSurveyResponsesTrackingBySurveyId(connection, databaseName, surveyId);
				logger.debug(">>> Deleted Survey Responses Tracking for surveyId :: "+surveyId);
				iSurveyResponseDao.deleteSurveyResponseBySurveyId(connection, databaseName, surveyId);
				logger.debug(">>> Deleted Survey Response for surveyId :: "+surveyId);
				surveyDao.deleteSurveyById(connection, databaseName, surveyId);
				logger.debug(">>> Deleted Survey for surveyId :: "+surveyId);
				iSchedulerDao.deleteGradingTypeDate(connection, databaseName, formId);
				logger.debug(">>> Deleted Grading Type data for formId :: "+formId);
				iSchedulerDao.deleteRatingTypeDate(connection, databaseName, formId);
				logger.debug(">>> Deleted Rating Type data for formId :: "+formId);
				surveyDao.deleteFormById(connection, databaseName, formId);
				logger.debug(">>> Deleted Form for formId :: "+formId);
			}
			masterDataService.deleteGradingEnableByProjectId(connection, databaseName, projectId);
			logger.debug(">>> Deleted GradingEnable for projectId : "+projectId);
			
			// Delete all media associated with that project
			logger.debug("Total media files for the project '"+projectId+"' is : "+allMediaIdsForProject.size());
			mediaDao.deleteMediaList(connection, databaseName, allMediaIdsForProject);
			logger.debug(">>> Deleted Media files for projectId :: "+projectId);
			
			//Finally deleting the project.
			surveyDao.deleteProjectById(connection, databaseName, projectId);
			logger.debug(">>> Deleted the project for projectId : "+projectId);
			connection.commit();

			// *************** Start Integrate **********************
			logger.debug("Assigning delete job to reporting database.");
			JSONObject deleteProjectJson = new JSONObject();
			deleteProjectJson.put(ReportingDbJSONConstant.PROJECT_ID, projectId);
			String jsonStr = deleteProjectJson.toString();
			logger.debug("Delete project JSON: " + jsonStr);
			DeleteJobAssigner assigner = new DeleteJobAssigner();
			assigner.assignDeleteJobToDatabase(databaseName, jsonStr);
			// *************** End Integrate **********************

			JSONObject responseJSON = new JSONObject();
			responseJSON.put(ProjectServiceConstant.RESPONSE_MESSAGE, ReadPropertiesFile.readResponseProperty("204"));
			return responseJSON.toString();
		} catch (Exception e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			logger.error(" unable to delete Project Data " + e.getMessage(), e);
			throw new ProjectServiceException(" unable to delete Project Data " + e.getMessage(), e);
		} finally {
			MSSqlServerUtill.close(null, connection);
		}
	}

	private List<String> getMediaIdListFromSurveyId(Connection connection, String databaseName, String surveyId) throws ProjectServiceException {
		List<String> mediaIdList = new ArrayList<String>();
		try {
			List<SurveyResponse> surveyResponses = iSurveyResponseDao.getSurveyResponsesBySurveyId(connection, databaseName, surveyId);
			if(surveyResponses.size() > 0) {
				logger.debug(surveyResponses.size()+" Survey Responses found for surveyId '"+surveyId+"'.");
				for(SurveyResponse surveyResponse: surveyResponses) {
					String surveyResponseData = surveyResponse.getSurveyResponseData();
					if(surveyResponseData != null && !surveyResponseData.isEmpty()) {
						JSONArray surveyResponseDataJson = (JSONArray) JSONValue.parseWithException(surveyResponseData);
						for (int i = 0; i < surveyResponseDataJson.size(); i++) {
							JSONObject singleSection = (JSONObject) surveyResponseDataJson.get(i);
						//	String sectionId = JsonUtill.getString(singleSection, ProjectServiceConstant._SECTION_ID);
							JSONArray sectionResponseArray = JsonUtill.getJsonArray(singleSection,
									ProjectServiceConstant._SECTION_RESPONSE_DATA);
							for (int j = 0; j < sectionResponseArray.size(); j++) {
								JSONObject singleQuestion = (JSONObject) sectionResponseArray.get(j);
						//		String questionId = JsonUtill.getString(singleQuestion, ProjectServiceConstant._QUESTION_ID);
								JSONObject responseData = JsonUtill.getJsonObject(singleQuestion,
										ProjectServiceConstant._RESPONSE_DATA);
								JSONObject mediaResponseJsonObject = JsonUtill.getJsonObject(responseData,
										ProjectServiceConstant._MEDIA_RESPONSE);
								JSONArray imageJsonArray = (JSONArray) mediaResponseJsonObject.get(ProjectServiceConstant._IMAGE);
								for (int k = 0; k < imageJsonArray.size(); k++) {
									JSONObject singleMedia = (JSONObject) imageJsonArray.get(k);
									String mediaId = JsonUtill.getString(singleMedia, ProjectServiceConstant._MEDIA_ID);
									mediaIdList.add(mediaId);   //Image type media Ids
								}
								JSONArray docJsonArray = (JSONArray) mediaResponseJsonObject.get(ProjectServiceConstant._DOCS);
								for (int k = 0; k < docJsonArray.size(); k++) {
									JSONObject singleMedia = (JSONObject) docJsonArray.get(k);
									String mediaId = JsonUtill.getString(singleMedia, ProjectServiceConstant._MEDIA_ID);
									mediaIdList.add(mediaId);   //Document type media Ids.
								}
							}
						}
					}
				}
				logger.debug(mediaIdList.size()+" media files found for surveyId '"+surveyId+"'.");
			} else {
				logger.debug("No Survey Responses found for surveyId '"+surveyId+"'.");
				logger.debug("Returning empty media list for surveyId '"+surveyId+"'.");
			}
			
		}catch (Exception e) {
			logger.error("Unable to get the mediaIds for surveyId, ",e);	
			throw new ProjectServiceException("Unable to get the mediaIds for surveyId, "+e.getMessage());
		}
		return mediaIdList;
	}

	/**
	 * this method is used to get Forms Of specific User
	 * 
	 * @param tenantId
	 * @param userRole
	 * @param projectId
	 * @param taskType
	 * @param partnerId
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getFormsOfUser(String tenantId, String userEmail, String projectId, String taskType, String partnerId)
			throws ProjectServiceException {
		try {
			validateProjectId(tenantId, projectId);
			List<FormDataOfUser> listOfFormsForUser = null;
			logger.debug(".in  getFormsOfUser ,  tenantId is " + tenantId + " and userEmail is " + userEmail);
			validateParams(taskType);
			boolean isCompletedRequired = taskType.equals(ProjectServiceConstant.COMPLETED_TASK);
			boolean isAssignedRequired = taskType.equals(ProjectServiceConstant.ASSIGNED_TASK);
			User user = iUserDao.getUserByEmail(tenantId, userEmail);
			String userRole = user.getUserRole();
			JSONObject responseJson = new JSONObject();
			if (userRole.equals(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
				if (partnerId == null || partnerId.isEmpty()) {
					throw new ProjectServiceException("Invalid Request , [partnerId] header not Found");
				}
				listOfFormsForUser = getFormsOfFieldAuditor(tenantId, projectId, user, isCompletedRequired,
						isAssignedRequired, partnerId);
			} else if (userRole.equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
				listOfFormsForUser = getFormsOfCenterIncharge(tenantId, projectId, user, isCompletedRequired,
						isAssignedRequired);
			} else if (userRole.equals(UserAccessManagementServiceConstants.CLIENT_SPONSOR)) {
				listOfFormsForUser = getFormsForClientSponsor(tenantId, projectId, user, isCompletedRequired,
						isAssignedRequired);
			} else {
				logger.debug("User is not allowed for the opertion");
				throw new ProjectServiceException("User is not allowed for the opertion");
			}
			JSONArray jsonArray = buildJsonFromFormDataOfUserList(listOfFormsForUser);
			responseJson.put(ProjectServiceConstant.USER_FORMS, jsonArray);
			return responseJson.toString();
		} catch (Exception e) {
			logger.debug("uable to get forms for user" + e.getMessage());
			throw new ProjectServiceException("uable to get forms for user " + e.getMessage());
		}

	}

	/**
	 * this method is used to delete FormData
	 * 
	 * @param tenantId
	 * @param formId
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deleteFormData(String databaseName, String formId) throws ProjectServiceException {
		Connection connection = null;
		try {
			logger.debug(" inside deleteFormtData method of SurveyServiceImpl , databaseName:  " + databaseName);
			validateId(formId, ProjectServiceConstant.PROJECT_ID);
			logger.debug(" id :: " + formId);
			FormData formData = surveyDao.getFormById(databaseName, formId);
			if (formData == null) {
				throw new ProjectServiceException(" invalid formId ");
			}
			validateDeleteForm(databaseName, formData);
			connection = MSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			String surveyId = formData.getSurveyId();
			surveyDao.deleteSurveyById(connection, databaseName, surveyId);
			iSchedulerDao.deleteGradingTypeDate(connection, databaseName, formId);
			iSchedulerDao.deleteRatingTypeDate(connection, databaseName, formId);
			surveyDao.deleteFormById(connection, databaseName, formId);
			connection.commit();
			JSONObject responseJSON = new JSONObject();
			responseJSON.put(ProjectServiceConstant.RESPONSE_MESSAGE, ReadPropertiesFile.readResponseProperty("229"));
			return responseJSON.toString();
		} catch (Exception e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			logger.error(" unable to delete form Data " + e.getMessage(), e);
			throw new ProjectServiceException(" unable to delete form Data " + e.getMessage(), e);
		} finally {
			logger.error("closing the conneciton in service layer");
			MSSqlServerUtill.close(null, connection);
		}
	}

	/**
	 * this method is used to get Project Details Of User
	 * 
	 * @param tenantId
	 * @param userRole
	 * @param userEmail
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getProjectDetailsOfUser(String tenantId, String userEmail, String taskType)
			throws ProjectServiceException {
		try {
			logger.debug(".in get project details of User , tenantId is " + tenantId + " user email is ::" + userEmail
					+ " and userEmail is " + userEmail);
			validateParams(taskType);
			List<ProjectDetailOfUser> projectsForUser = null;
			List<ProjectDetailOfUser> responseUserProject = null;
			JSONObject responseJson = new JSONObject();
			User user = iUserDao.getUserByEmail(tenantId, userEmail);
			String userRole = user.getUserRole();
			boolean isCompletedRequired = taskType.equals(ProjectServiceConstant.COMPLETED_TASK);
			boolean isAssignedRequired = taskType.equals(ProjectServiceConstant.ASSIGNED_TASK);
			if (userRole.equalsIgnoreCase(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
				logger.debug("user is an field auditor");
				projectsForUser = getProjectForFieldAuditor(tenantId, user);
			} else if (userRole.equalsIgnoreCase(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
				projectsForUser = getProjectForCenterInCharge(tenantId, user);
			} else if (userRole.equalsIgnoreCase(UserAccessManagementServiceConstants.CLIENT_SPONSOR)) {
				projectsForUser = getProjectsForClientSponsor(tenantId, user);
			} else {
				throw new ProjectServiceException("User is not allowed for the opertion");
			}
			logger.debug(" projectsForUser :: " + projectsForUser);
			if (isCompletedRequired) {
				responseUserProject = projectsForUser.stream()
						.filter(p -> (p.isStatus() == true) || p.isAssignedFormsCompleted() == true)
						.collect(Collectors.toList());
			} else if (isAssignedRequired) {
				responseUserProject = projectsForUser.stream().filter(p -> !(p.isStatus()) == isAssignedRequired)
						.collect(Collectors.toList());
			}
			logger.debug(".responseUserProject " + responseUserProject);
			JSONArray projectJSONData = getJSONFromProjectDetailOfUserList(responseUserProject);
			responseJson.put(ProjectServiceConstant.USER_PROJECTS, projectJSONData);
			return responseJson.toString();
		} catch (Exception e) {
			logger.error("unable to get projects for user" + e.getMessage());
			throw new ProjectServiceException("uable to get projects for user " + e.getMessage());
		}

	}

	/**
	 * this method is used to get ProgressMeter Data
	 * 
	 * @param tenantId
	 * @param projectId
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getProgressMeterData(String tenantId, String projectId) throws ProjectServiceException {
		try {
			logger.debug("inside getProgressMeterData method of projectServiceIMPL  , databaseName:  " + tenantId);
			validateId(projectId, ProjectServiceConstant.PROJECT_ID);
			logger.debug(" projectId :: " + projectId);
			List<FormData> formsForProject = surveyDao.getFormsByProjectId(tenantId, projectId);

//			List<GradingEnable> gradingEnablelList = masterDataService.getGradingEnableByProjectId(tenantId, projectId);// imasterDao.getPartnerCountByProjectId(tenantId,
//			long partnerCount = gradingEnablelList.size();

			List<PartnerDetails> details = masterDataService.getPartnerDetailsByProjectId(tenantId, projectId);

			logger.debug("total number of partner who took the project is  " + details.size());
			int totalPatner = iPartnerDao.getTotalPartnerCount(tenantId);

			Map<String, Object> responseMap = getTrainingCenterStatusForPartners(tenantId, details, formsForProject,
					projectId);
			JSONObject responsJSON = new JSONObject(responseMap);
			responsJSON.put(ProjectServiceConstant.PARTNERS_COVERED, details.size());
			responsJSON.put(ProjectServiceConstant.TOTAL_PARTNERS, totalPatner);
			return responsJSON.toJSONString();
		} catch (Exception e) {
			logger.error(" unable to find project Data ", e);
			throw new ProjectServiceException(" unable to find project Data " + e.getMessage(), e);
		}
	}

	/**
	 * this method is used to publish FormData By Id. once the form is published it
	 * is not possible to update or delete the form
	 * 
	 * @param tenantId
	 * @param formId
	 * @param requestBody
	 * @return
	 * @throws ProjectServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String publishFormDataById(String databaseName, String formId, String requestBody)
			throws ProjectServiceException {
		Connection connection = null;
		try {
			logger.debug(" inside publishFormDataById method of SurveyServiceImpl , databaseName:  " + databaseName);
			validateId(formId, ProjectServiceConstant.FORM_ID);
			logger.debug(" formId is  :: " + formId);
			JSONObject requestJson = (JSONObject) JSONValue.parseWithException(requestBody);
			boolean status = JsonUtill.getBoolean(requestJson, "status");
			FormData formData = validateFormForPublish(databaseName, formId);
			String surveyId = formData.getSurveyId();
			connection = MSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			surveyDao.publishFormDataById(connection, databaseName, formId, status);
			Survey survey = surveyDao.getSurveyById(connection, databaseName, surveyId);
			String surveyData = survey.getSurveyData();
			if (surveyData.isEmpty()) {
				JSONObject surveyJson = (JSONObject) JSONValue.parse(surveyData);
				if (surveyJson.isEmpty()) {
					throw new ProjectServiceException("survey Data is empty");
				}
			}
			surveyDao.publishSurveyDataById(connection, databaseName, surveyId, status);
			// add forms to scheduler
			addSchedulerData(connection, databaseName, formData);
			connection.commit();
			logger.info("  FormData successfully published ");
			JSONObject responseJSON = new JSONObject();
//			responseJSON.put("msg", ReadPropertiesFile.readResponseProperty("204"));
			responseJSON.put("msg", " FormData successfully published");
			return responseJSON.toString();
		} catch (Exception e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			logger.error(" unable to publish Survey Data " + e.getMessage(), e);
			throw new ProjectServiceException(" unable to publish Survey Data " + e.getMessage(), e);
		} finally {
			MSSqlServerUtill.close(null, connection);
		}

	}

//	private void checkMappingBeforDeletingProject(String databaseName, String projectId)
//			throws MasterDataManagmentDaoException, ProjectServiceException {
//		logger.debug("projectId " + projectId);
//		List<ProjectMapping> projectMappingData = masterDataService.getProjectMappingData(databaseName);
//		for (ProjectMapping projectMappings : projectMappingData) {
//			if (projectMappings.getProjectId().contentEquals(projectId)) {
//				throw new ProjectServiceException(
//						"cant delete project, project is mapped to " + projectMappings.getProjectMappingId());
//			}
//		}
//		logger.debug("safe--");
//	}

	private void validateDeleteForm(String databaseName, FormData form)
			throws SurveyResponseDaoException, ProjectServiceException {
		boolean publish = form.isPublish();
		if (publish) {
			logger.debug("form is published , so cant delete it");
			throw new ProjectServiceException(" cant delete published form ");
//			logger.debug("form is published deleting the Responses associated with form");
//			iSurveyResponseDao.deleteSurveyResponseBySurveyId(connection, databaseName, form.getSurveyId());
//			logger.debug("deleted all the forms assosiated with form and surveys");
		}
	}

	private List<ProjectDetailOfUser> getProjectsForClientSponsor(String tenantId, User user)
			throws MasterDataManagmentDaoException, ProjectDaoException, PartnerDaoException, ProjectServiceException,
			SurveyResponseDaoException {
		List<ProjectDetailOfUser> listOfProjectOfUser = new ArrayList<ProjectDetailOfUser>();
		String userId = user.getUserId();
		PartnerDetails partnerDetails = iPartnerDao.getPartnerByClientSponsorId(tenantId, userId);
		String partnerName = partnerDetails.getPartnerName();
		List<GradingEnable> gradingEnableList = masterDataService.getGradingEnableByPartnerId(tenantId,
				partnerDetails.getPartnerId());
		logger.debug("gradingEnableList:: size is:: " + gradingEnableList);
		for (GradingEnable singleGradingEnable : gradingEnableList) {
			boolean gradingEnable = singleGradingEnable.isGradingEnable();
			if (gradingEnable) {
				String projectId = singleGradingEnable.getProjectId();
				ProjectData projectData = surveyDao.getProjectById(tenantId, projectId);
				logger.debug("projectData " + projectData);
				List<FormData> formsList = surveyDao.getFormsByProjectId(tenantId, projectId);
				List<FormData> formsListForCS = getFormsForUser(formsList,
						UserAccessManagementServiceConstants.CLIENT_SPONSOR);
				boolean status = isAllCSFormsSubmitted(tenantId, partnerDetails.getPartnerId(), formsListForCS);
				boolean formStatus = isOneCSFormsSubmitted(tenantId, partnerDetails.getPartnerId(), formsListForCS);
				ProjectDetailOfUser userProjectDetails = new ProjectDetailOfUser(partnerName, status, projectData);
				userProjectDetails.setPartnerId("");
				userProjectDetails.setAssignedFormsCompleted(formStatus);
				listOfProjectOfUser.add(userProjectDetails);
			}
		}
		return listOfProjectOfUser;
	}

	private boolean isOneCSFormsSubmitted(String tenantId, String partnerId, List<FormData> formsListForCS)
			throws SurveyResponseDaoException {
		for (FormData singleForm : formsListForCS) {
			String surveyId = singleForm.getSurveyId();
			SurveyResponse surveyResponse = iSurveyResponseDao.getSurveyResponsesByPartnerIdAndSurveyId(tenantId,
					surveyId, partnerId);
			logger.debug("surveyResponse ::" + surveyResponse);
			if (surveyResponse != null) {
				if (surveyResponse.isSubmited()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isAllCSFormsSubmitted(String tenantId, String partnerId, List<FormData> formsListForCS)
			throws SurveyResponseDaoException {
		for (FormData singleForm : formsListForCS) {
			String surveyId = singleForm.getSurveyId();
			SurveyResponse surveyResponse = iSurveyResponseDao.getSurveyResponsesByPartnerIdAndSurveyId(tenantId,
					surveyId, partnerId);
			logger.debug("surveyResponse ::" + surveyResponse);
			if (surveyResponse == null) {
				return false;
			} else {
				boolean submited = surveyResponse.isSubmited();
				if (!submited) {
					return false;
				}
			}
		}
		return true;
	}

	private void validateDeadLine(String databaseName, Survey survey, String userRole, String auditFor,
			String auditForId)
			throws ProjectDaoException, ProjectServiceException, PartnerDaoException, SchedulerDaoException {
		FormData formData = surveyDao.getFormsBySurveyId(databaseName, survey.getSurveyId());
		ProjectData project = surveyDao.getProjectById(databaseName, formData.getProjectId());
		if ((userRole.equalsIgnoreCase(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)
				|| userRole.equalsIgnoreCase(UserAccessManagementServiceConstants.CLIENT_SPONSOR))) {
			long selfAssignmentDeadline = project.getSelfAssignmentDeadline();
			long currentTime = new Date().getTime();
			if (currentTime > selfAssignmentDeadline) {
				throw new ProjectServiceException("The final deadline for submissions is "
						+ getStringFormateTimeFromMiliSecond(selfAssignmentDeadline));
			}
		}
		if (userRole.equalsIgnoreCase(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
			validateId(auditFor, ProjectServiceConstant.AUDIT_FOR);
			validateId(auditForId, ProjectServiceConstant.AUDIT_FOR_ID);
			if (auditFor.equals(ProjectServiceConstant.TRAINING_CENTER)) {
				TrainingCenterDetails tcDetails = iPartnerDao.getTrainingCenterDetailsByTcId(databaseName, auditForId);
				String partnerId = tcDetails.getPartnerId();
				RatingType ratingType = iSchedulerDao.getRatingTypeDataByProjectIdParterIdFormIdAndTcId(databaseName,
						partnerId, formData.getProjectId(), formData.getFormId(), auditForId);
				boolean ratingSelfAssignment = ratingType.isSelfAssignmentStatus();
				if (!ratingSelfAssignment) {
					throw new ProjectServiceException("Self-Assessment for Rating form is not completed.");
				}
				long selfAssessmentDeadLine = ratingType.getAuditDate();
				Date date = new Date();
				long currentTime = date.getTime();
				if (currentTime > selfAssessmentDeadLine && selfAssessmentDeadLine != 0) {
					throw new ProjectServiceException("The final deadline for submissions is "
							+ getStringFormateTimeFromMiliSecond(selfAssessmentDeadLine));
				}

			} else if (auditFor.equals(ProjectServiceConstant.PARTNER)) {
				GradingType gradingType = iSchedulerDao.getGradingTypeDataByPartnerIdProjectIdAndFormId(databaseName,
						auditForId, formData.getProjectId(), formData.getFormId());
				boolean gradingSelfAssignment = gradingType.isSelfAssignmentStatus();
				if (!gradingSelfAssignment) {
					throw new ProjectServiceException("Self-Assessment for grading form is not completed.");
				}
				long selfAssessmentDeadLine = gradingType.getAuditDate();
				Date date = new Date();
				long currentTime = date.getTime();
				if (currentTime > selfAssessmentDeadLine && selfAssessmentDeadLine != 0) {
					throw new ProjectServiceException("The final deadline for submissions is "
							+ getStringFormateTimeFromMiliSecond(selfAssessmentDeadLine));
				}
			} else {
				throw new ProjectServiceException("Invalid auditFor category.");
			}
		}

	}

	private String getStringFormateTimeFromMiliSecond(long time) {
		if (time == 0) {
			return "";
		}
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat(SurveyResponseServiceConstant.TIME_FORMATE);
		return format.format(date);
	}

	private void validateUpdateFormRequest(String databaseName, String formId)
			throws ProjectDaoException, ProjectServiceException {
		FormData form = surveyDao.getFormById(databaseName, formId);
		if (form == null) {
			logger.error("Invalid formId ");
			throw new ProjectServiceException("Can't update the form, Invalid formId  ");
		}
		boolean publish = form.isPublish();
		if (publish) {
			logger.error("Can't update the form after published ");
			throw new ProjectServiceException("Can't update the form after published  ");
		}

	}

	private void validateUpdateSurvey(String databaseName, String surveyId)
			throws ProjectDaoException, ProjectServiceException {
		Survey survey = surveyDao.getSurveyById(databaseName, surveyId);
		if (survey == null) {
			logger.error(" unable to update Survey Data, Invalid surveyId ");
			throw new ProjectServiceException(" unable to update Survey Data, Invalid surveyId ");
		}
		boolean publish = survey.isPublish();
		if (publish) {
			logger.error(" unable to update Survey Data when survey is Published ");
			throw new ProjectServiceException(" can't update Survey Data when survey is Published");
		}

	}

	private void validateProjectId(String tenantId, String projectId)
			throws ProjectDaoException, ProjectServiceException {
		boolean projectExist = surveyDao.isProjectExist(tenantId, projectId);
		if (!projectExist) {
			throw new ProjectServiceException("Invalid projectId");
		}

	}

	private List<FormDataOfUser> getFormsOfFieldAuditor(String tenantId, String projectId, User user,
			boolean isCompletedOnly, boolean isAssignedOnly, String partnerId)
			throws MasterDataManagmentDaoException, ProjectDaoException, SchedulerDaoException, PartnerDaoException {
		List<FormDataOfUser> listOfFormsOfUser = new ArrayList<FormDataOfUser>();
		String fieldAuditorId = user.getUserId();
		List<GradingType> gradingTypeList = iSchedulerDao.getGradingTypeDataByFieldAuditorIdProjectIdAndPartnerId(
				tenantId, fieldAuditorId, projectId, partnerId);
		logger.debug("gradingTypeList size is " + gradingTypeList.size());
		PartnerDetails partner = iPartnerDao.getPartnerById(tenantId, partnerId);
		AuditDetails auditDetails = new AuditDetails(ProjectServiceConstant.PARTNER, partnerId,
				partner.getPartnerName());
		for (GradingType gradingType : gradingTypeList) {
			FormDataOfUser formDataOfUser = new FormDataOfUser();
			String formId = gradingType.getFormId();
			FormData formData = surveyDao.getFormById(tenantId, formId);
			formDataOfUser.setFormData(formData);
			formDataOfUser.setPartnerId(partner.getPartnerId());
			formDataOfUser.setPartnerName(partner.getPartnerName());
			formDataOfUser.setAuditDetails(auditDetails);
			long auditDate = gradingType.getAuditDate();
			formDataOfUser.setFormDeadLine(auditDate);
			boolean formExpire = isFormExpired(auditDate);
			formDataOfUser.setFormExpire(formExpire);
			if (gradingType.isAuditStatus() == isCompletedOnly) {
				listOfFormsOfUser.add(formDataOfUser);
			} else if (!(gradingType.isAuditStatus()) == isAssignedOnly) {
				listOfFormsOfUser.add(formDataOfUser);
			}
		}
		List<RatingType> ratingTypeList = iSchedulerDao.getRatingTypeDataByFieldAuditorIdProjectIdAndPartnerId(tenantId,
				fieldAuditorId, projectId, partnerId);
		logger.debug("ratingTypeList size is " + ratingTypeList.size());
		for (RatingType ratingType : ratingTypeList) {
			FormDataOfUser formDataOfUser = new FormDataOfUser();
			String formId = ratingType.getFormId();
			FormData formData = surveyDao.getFormById(tenantId, formId);
			formDataOfUser.setFormData(formData);
			formDataOfUser.setPartnerId(partner.getPartnerId());
			formDataOfUser.setFormDeadLine(ratingType.getAuditDate());
			formDataOfUser.setPartnerName(partner.getPartnerName());

			long auditDate = ratingType.getAuditDate();
			formDataOfUser.setFormDeadLine(auditDate);
			boolean formExpire = isFormExpired(auditDate);
			formDataOfUser.setFormExpire(formExpire);

			String tcId = ratingType.getTcId();
			TrainingCenterDetails tcDetails = iPartnerDao.getTrainingCenterDetailsByTcId(tenantId, tcId);
			AuditDetails auditDetailsForTc = new AuditDetails(ProjectServiceConstant.TRAINING_CENTER, tcId,
					tcDetails.getTcName());
			formDataOfUser.setAuditDetails(auditDetailsForTc);
			if (ratingType.isAuditStatus() == isCompletedOnly) {
				listOfFormsOfUser.add(formDataOfUser);
			} else if (!(ratingType.isAuditStatus()) == isAssignedOnly) {
				listOfFormsOfUser.add(formDataOfUser);
			}
		}
		return listOfFormsOfUser;

	}

	private List<FormDataOfUser> getFormsForClientSponsor(String tenantId, String projectId, User user,
			boolean isCompletedOnly, boolean isAssignedOnly)
			throws MasterDataManagmentDaoException, ProjectDaoException, PartnerDaoException, SchedulerDaoException {
		List<FormDataOfUser> listOfFormsOfUser = new ArrayList<FormDataOfUser>();
		String userId = user.getUserId();
		ProjectData project = surveyDao.getProjectById(tenantId, projectId);
		long selfAssignmentDeadline = project.getSelfAssignmentDeadline();
//		String selfAssDeadline = getProjectStringFormateTimeFromMiliSecond(selfAssignmentDeadline);
		PartnerDetails partnerDetails = iPartnerDao.getPartnerByClientSponsorId(tenantId, userId);
		List<GradingType> gradingTypeList = iSchedulerDao.getGradingTypeDataByPartnerIdAndProjectId(tenantId,
				partnerDetails.getPartnerId(), projectId);

		logger.debug("forms for clientSponsor is ::" + gradingTypeList.size());
		for (GradingType gradingType : gradingTypeList) {
			FormDataOfUser formDataOfUser = new FormDataOfUser();
			String formId = gradingType.getFormId();
			FormData formData = surveyDao.getFormById(tenantId, formId);
			formDataOfUser.setFormData(formData);
			formDataOfUser.setPartnerId(partnerDetails.getPartnerId());
			formDataOfUser.setPartnerName(partnerDetails.getPartnerName());
//			formDataOfUser.setFormDeadLine(gradingType.getSelfAssessmentDeadLine());
			formDataOfUser.setFormDeadLine(selfAssignmentDeadline);
			boolean formExpire = isFormExpired(selfAssignmentDeadline);
			formDataOfUser.setFormExpire(formExpire);
			if (gradingType.isSelfAssignmentStatus() == isCompletedOnly) {
				listOfFormsOfUser.add(formDataOfUser);
			} else if (!(gradingType.isSelfAssignmentStatus()) == isAssignedOnly) {
				listOfFormsOfUser.add(formDataOfUser);
			}
		}
		return listOfFormsOfUser;

	}

	@SuppressWarnings("unchecked")
	private JSONArray buildJsonFromFormDataOfUserList(List<FormDataOfUser> listOfFormsForUser) {
		JSONArray jsonArray = new JSONArray();
		for (FormDataOfUser formOfUser : listOfFormsForUser) {
			JSONObject jsonObject = buildJsonFromFormData(formOfUser.getFormData());
			String partnerId = formOfUser.getPartnerId();
			jsonObject.put(ProjectServiceConstant.PARTNER_ID, partnerId);
			jsonObject.put(ProjectServiceConstant.PARTNER_NAME, formOfUser.getPartnerName());
			String centerId = formOfUser.getCenterId();
			if (centerId != null) {
				jsonObject.put(ProjectServiceConstant.CENTER_ID, centerId);

			} else {
				jsonObject.put(ProjectServiceConstant.CENTER_ID, "");
			}
			JSONObject auditDetailsJson = buildJsonFromAuditDetails(formOfUser.getAuditDetails());
			jsonObject.put(ProjectServiceConstant.AUDIT_DETAILS, auditDetailsJson);

			jsonObject.put(ProjectServiceConstant.FORM_DEAD_LINE,
					getProjectStringFormateTimeFromMiliSecond(formOfUser.getFormDeadLine()));
			jsonObject.put(ProjectServiceConstant.FORM_EXPIRE, formOfUser.isFormExpire());

			jsonArray.add(jsonObject);

		}
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonFromAuditDetails(AuditDetails auditDetails) {
		JSONObject jsonObject = new JSONObject();
		if (auditDetails == null) {
			return jsonObject;
		}
		jsonObject.put("auditFor", auditDetails.getAuditFor());
		jsonObject.put("id", auditDetails.getAuditForId());
		jsonObject.put("name", auditDetails.getName());
		return jsonObject;

	}

	private void validateParams(String taskType) throws ProjectServiceException {
		if (!(taskType.equals("completedTask") || taskType.equals("assignedTask"))) {
			throw new ProjectServiceException(taskType + " is inValid param");
		}
	}

	private List<FormDataOfUser> getFormsOfCenterIncharge(String tenantId, String projectId, User user,
			boolean isCompletedOnly, boolean isAssignedOnly)
			throws MasterDataManagmentDaoException, ProjectDaoException, SchedulerDaoException, PartnerDaoException {
		List<FormDataOfUser> listOfFormsOfUser = new ArrayList<FormDataOfUser>();
		String tcId = user.getCenterId();
		TrainingCenterDetails tcDetails = iPartnerDao.getTrainingCenterDetailsByTcId(tenantId, tcId);
		PartnerDetails parter = iPartnerDao.getPartnerById(tenantId, tcDetails.getPartnerId());
		List<RatingType> ratingTypeList = iSchedulerDao.getRatingTypeDataByProjectIdParterIdAndTcId(tenantId,
				tcDetails.getPartnerId(), projectId, tcId);
		ProjectData project = surveyDao.getProjectById(tenantId, projectId);
		long selfAssignmentDeadline = project.getSelfAssignmentDeadline();
		logger.debug("forms for centerIncharge is ::" + ratingTypeList.size());
		for (RatingType ratingType : ratingTypeList) {
			FormDataOfUser formDataOfUser = new FormDataOfUser();
			String formId = ratingType.getFormId();
			FormData formData = surveyDao.getFormById(tenantId, formId);
			formDataOfUser.setCenterId(ratingType.getTcId());
			formDataOfUser.setFormData(formData);
			formDataOfUser.setPartnerId(parter.getPartnerId());
			formDataOfUser.setPartnerName(parter.getPartnerName());
			formDataOfUser.setFormDeadLine(selfAssignmentDeadline);
			boolean formExpire = isFormExpired(selfAssignmentDeadline);
			formDataOfUser.setFormExpire(formExpire);
			if (ratingType.isSelfAssignmentStatus() == isCompletedOnly) {
				listOfFormsOfUser.add(formDataOfUser);
			} else if (!(ratingType.isSelfAssignmentStatus()) == isAssignedOnly) {
				listOfFormsOfUser.add(formDataOfUser);
			}
		}

		return listOfFormsOfUser;
	}

	private boolean isFormExpired(long selfAssignmentDeadline) {
		long currentTime = new Date().getTime();
		if (currentTime > selfAssignmentDeadline) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getJSONFromProjectList(List<ProjectData> projectsList) {
		JSONArray jsonArray = new JSONArray();
		for (ProjectData project : projectsList) {
			JSONObject surveyJSON = getJSONFromProjectDetails(project);
			jsonArray.add(surveyJSON);
		}
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getJSONFromProjectDetailOfUserList(List<ProjectDetailOfUser> projectsList) {
		JSONArray jsonArray = new JSONArray();
		for (ProjectDetailOfUser project : projectsList) {
			JSONObject surveyJSON = getJSONFromProjectDetailsOfUser(project);
			jsonArray.add(surveyJSON);
		}
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getJSONFromProjectDetailsOfUser(ProjectDetailOfUser projectDetOfUser) {
		String partnerName = projectDetOfUser.getPartnerName();
		boolean status = projectDetOfUser.isStatus();
		ProjectData project = projectDetOfUser.getProjectData();
		JSONObject surveyJSON = getJSONFromProjectDetails(project);
		surveyJSON.put(ProjectServiceConstant.PARTNER_NAME, partnerName);
		surveyJSON.put(ProjectServiceConstant.PATNER_ID, projectDetOfUser.getPartnerId());
		surveyJSON.put(ProjectServiceConstant.STATUS, status);
		surveyJSON.put(ProjectServiceConstant.FORM_STATUS, projectDetOfUser.isAssignedFormsCompleted());
		return surveyJSON;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getJSONFromProjectDetails(ProjectData project) {
		JSONObject surveyJSON = new JSONObject();
		surveyJSON.put(ProjectServiceConstant.PROJECT_ID, project.getProjectId());
		surveyJSON.put(ProjectServiceConstant.PROJECT_NAME, project.getProjectName());
		surveyJSON.put(ProjectServiceConstant.PROJECT_DESCRIPTION, project.getProjectDescription());
		surveyJSON.put(ProjectServiceConstant.START_DATE,
				getProjectStringFormateTimeFromMiliSecond(project.getStartDate()));
		surveyJSON.put(ProjectServiceConstant.END_DATE,
				getProjectStringFormateTimeFromMiliSecond(project.getEndDate()));
		surveyJSON.put(ProjectServiceConstant.SELF_ASSIGNMENT_DEAD_LINE,
				getProjectStringFormateTimeFromMiliSecond(project.getSelfAssignmentDeadline()));
		return surveyJSON;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getJSONFromSurveyList(List<Survey> surveys) throws org.json.simple.parser.ParseException {
		JSONArray jsonArray = new JSONArray();
		for (Survey survey : surveys) {
			JSONObject surveyJSON = getJSONFromSurvey(survey);
			jsonArray.add(surveyJSON);
		}
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getJSONFromSurvey(Survey survey) throws org.json.simple.parser.ParseException {
		JSONObject surveyJSON = new JSONObject();
		surveyJSON.put(ProjectServiceConstant.SURVEY_ID, survey.getSurveyId());
		surveyJSON.put(ProjectServiceConstant.TIME_OF_CREATE,
				getSurveyStringFormateTimeFromMiliSecond(survey.getTime()));
		long lastUpdate = survey.getLastUpdate();
		surveyJSON.put(ProjectServiceConstant.LAST_UPDATE, getSurveyStringFormateTimeFromMiliSecond(lastUpdate));
		surveyJSON.put(ProjectServiceConstant.MAX_SCORE, survey.getMaxScore());
		JSONObject surveyDataJson = (JSONObject) JSONValue.parseWithException(survey.getSurveyData());
		surveyJSON.put(ProjectServiceConstant.SURVEY_DATA, surveyDataJson);
		return surveyJSON;
	}

	private Survey getSurveyFromRequest(JSONObject surveyJson) throws JsonUtillException, ParseException {
		Survey survey = new Survey();
		String lastUpdate = JsonUtill.getString(surveyJson, ProjectServiceConstant.LAST_UPDATE);
		JSONObject surveyData = JsonUtill.getJsonObject(surveyJson, ProjectServiceConstant.SURVEY_DATA);
		survey.setLastUpdate(getSurveyTimeMiliSecond(lastUpdate));
		survey.setSurveyData(surveyData.toString());
		return survey;
	}

	private Integer calculateMaxScore(Survey survey) throws JsonUtillException, ProjectServiceException {
		logger.debug("in calculating Max score method");
		JSONObject surveyJson = (JSONObject) JSONValue.parse(survey.getSurveyData());
		List<Integer> calculateTotalScore = new ArrayList<>();
		JSONArray jsonArray = (JSONArray) surveyJson.get(ProjectServiceConstant.SECTIONS);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject currentJsonObject = (JSONObject) jsonArray.get(i);
			Integer sectionMaxMark = calculateSectionMaxMarks(currentJsonObject);
			calculateTotalScore.add(sectionMaxMark);
		}
		survey.setSurveyData(surveyJson.toJSONString());
		Integer maxSectionScore = calculateTotalScore.stream().mapToInt(Integer::intValue).sum();
		return maxSectionScore;
	}

	@SuppressWarnings("unchecked")
	private Integer calculateSectionMaxMarks(JSONObject singleSection)
			throws JsonUtillException, ProjectServiceException {
		JSONArray sectionQuestions = (JSONArray) singleSection.get(ProjectServiceConstant.SECTION_QUESTIONS);
		List<Integer> sectionQuesMaxMarks = new ArrayList<>();
		for (int i = 0; i < sectionQuestions.size(); i++) {
			JSONObject currentQues = (JSONObject) sectionQuestions.get(i);
			Integer maxScore = calculateSingleQuesMaxMark(currentQues);
			sectionQuesMaxMarks.add(maxScore);
			currentQues.put("score", maxScore);
		}
		int maxSectionScore = sectionQuesMaxMarks.stream().mapToInt(Integer::intValue).sum();
		logger.debug("max Score currentSction " + maxSectionScore);
		singleSection.put(ProjectServiceConstant.SECTION_SCORE, maxSectionScore);
		return maxSectionScore;
	}

	private Integer calculateSingleQuesMaxMark(JSONObject currentQues)
			throws JsonUtillException, ProjectServiceException {
		JSONObject jsonObject = (JSONObject) currentQues.get(ProjectServiceConstant.QUES_DATA);
		JSONArray jsonArray = (JSONArray) jsonObject.get(ProjectServiceConstant.OPTIONS);
		List<Integer> list = new ArrayList<>();
		String questionType = (String) ((JSONObject) currentQues.get(ProjectServiceConstant.QUES_META_DATA))
				.get(ProjectServiceConstant.QUESTION_TYPE);
		switch (questionType) {
		case ProjectServiceConstant.OPEN_ENDED:
			logger.debug("current is====>>  " + questionType);
			getOptionScoreForOpenEnd(jsonObject, list);
			break;
		case ProjectServiceConstant.MULTIPLE_CHOICE:
			logger.debug("current is====>>  " + questionType);
			getOptionScoreForChoiceBasedQues(jsonArray, list);
			break;
		case ProjectServiceConstant.DROP_DOWN:
			logger.debug("current is====>>  " + questionType);
			getOptionScoreForChoiceBasedQues(jsonArray, list);
			break;
		case ProjectServiceConstant.CHECK_BOX:
			logger.debug("current is====>>  " + questionType);
			int checkBoxQues = getOptionScoreForCheckBoxQues(jsonArray, list);
			return checkBoxQues;
		default:
			throw new ProjectServiceException("In-valid Question Type : " + questionType);
		}
		Integer maxScore = Collections.max(list);
		return maxScore;
	}

	private int getOptionScoreForCheckBoxQues(JSONArray jsonArray, List<Integer> list) throws JsonUtillException {
		for (int i = 0; i < jsonArray.size(); i++) {
			int optionScore = 0;
			JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
			optionScore = JsonUtill.getInt(jsonObject2, ProjectServiceConstant.OPTION_WEIGHTAGE);
			list.add(optionScore);
			logger.debug("current option  " + jsonObject2);
		}
		return list.stream().mapToInt(Integer::intValue).sum();

	}

	private void getOptionScoreForOpenEnd(JSONObject jsonObject, List<Integer> list) throws JsonUtillException {
		int optionScore = 0;
		optionScore = JsonUtill.getInt(jsonObject, ProjectServiceConstant.WEIGHTAGE);
		logger.debug("optionScore " + optionScore);
		list.add(optionScore);
	}

	private void getOptionScoreForChoiceBasedQues(JSONArray jsonArray, List<Integer> list) throws JsonUtillException {
		for (int i = 0; i < jsonArray.size(); i++) {
			int optionScore = 0;
			JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
			optionScore = JsonUtill.getInt(jsonObject2, ProjectServiceConstant.OPTION_WEIGHTAGE);
			list.add(optionScore);
			logger.debug("current option  " + jsonObject2);
		}
	}

	private ProjectData getProjectDataFromRequest(JSONObject requestProjectData)
			throws JsonUtillException, ParseException {
		ProjectData projectData = new ProjectData();
		projectData.setProjectName(JsonUtill.getString(requestProjectData, ProjectServiceConstant.PROJECT_NAME));
		projectData.setProjectDescription(
				JsonUtill.getString(requestProjectData, ProjectServiceConstant.PROJECT_DESCRIPTION));
		projectData.setStartDate(
				getProjectTimeMiliSecond(JsonUtill.getString(requestProjectData, ProjectServiceConstant.START_DATE)));
		projectData.setEndDate(
				getProjectTimeMiliSecond(JsonUtill.getString(requestProjectData, ProjectServiceConstant.END_DATE)));
		projectData.setSelfAssignmentDeadline(getProjectTimeMiliSecond(
				JsonUtill.getString(requestProjectData, ProjectServiceConstant.SELF_ASSIGNMENT_DEAD_LINE)));
		return projectData;
	}

	private long getProjectTimeMiliSecond(String time) throws ParseException {
		if (time == null || time.isEmpty()) {
			return 0;
		}
		SimpleDateFormat format = new SimpleDateFormat(ProjectServiceConstant.PROJECT_TIME_FORMATE);
		Date date = format.parse(time);
		return date.getTime();
	}

	private String getProjectStringFormateTimeFromMiliSecond(long time) {
		if (time == 0) {
			return "";
		}
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat(ProjectServiceConstant.PROJECT_TIME_FORMATE);
		return format.format(date);
	}

	private long getSurveyTimeMiliSecond(String time) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(ProjectServiceConstant.SURVEY_TIME_FORMATE);
		Date date = format.parse(time);
		return date.getTime();
	}

	private String getSurveyStringFormateTimeFromMiliSecond(long time) {
		if (time == 0) {
			return "";
		}
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat(ProjectServiceConstant.SURVEY_TIME_FORMATE);
		return format.format(date);
	}

	private List<ProjectDetailOfUser> getProjectForCenterInCharge(String tenantId, User user)
			throws ProjectServiceException, UserDaoException, ProjectDaoException, MasterDataManagmentDaoException,
			SurveyResponseDaoException, PartnerDaoException {
		logger.debug("working fhfhsdf");
		List<ProjectDetailOfUser> listOfProjectOfUser = new ArrayList<ProjectDetailOfUser>();
		String centerId = user.getCenterId();
		if (centerId == null) {
			return listOfProjectOfUser;
		} else {
			List<ProjectMapping> projectMappingList = masterDataService.getProjectMappingByTcId(tenantId, centerId);
			logger.debug("projectMapping for TcId is " + projectMappingList);
			for (ProjectMapping projectMapping : projectMappingList) {
				ProjectDetailOfUser projectOfUser = new ProjectDetailOfUser();
				String projectId = projectMapping.getProjectId();
				logger.debug("project id is " + projectId);
				ProjectData projectData = surveyDao.getProjectById(tenantId, projectId);
				logger.debug("projectData is " + projectData);
				String tcId = projectMapping.getTcId();
				String partnerId = projectMapping.getPartnerId();
				PartnerDetails partnerDetails = iPartnerDao.getPartnerById(tenantId, partnerId);
//				TrainingCenterDetails tcDetails = iPartnerDao.getTrainingCenterDetailsByTcId(tenantId, tcId);
				// get survey
				List<FormData> formData = surveyDao.getFormsByProjectId(tenantId, projectId);
				List<FormData> surveyForUser = getFormsForUser(formData,
						UserAccessManagementServiceConstants.CENTER_IN_CHARGE);
				boolean status = isAllFormsSubmitted(tenantId, tcId, surveyForUser);
				boolean formsSubmitted = isOneFormsSubmitted(tenantId, tcId, surveyForUser);
				projectOfUser.setStatus(status);
				projectOfUser.setAssignedFormsCompleted(formsSubmitted);
				projectOfUser.setPartnerName(partnerDetails.getPartnerName());
				projectOfUser.setProjectData(projectData);
				projectOfUser.setPartnerId("");
				logger.debug("project of user is " + projectOfUser);
				listOfProjectOfUser.add(projectOfUser);
			}
		}
		return listOfProjectOfUser;
	}

	private boolean isAllFormsSubmitted(String dataBaseName, String tcId, List<FormData> surveyForUser)
			throws SurveyResponseDaoException {
		for (FormData singleForm : surveyForUser) {
			String surveyId = singleForm.getSurveyId();
			SurveyResponse surveyResponse = iSurveyResponseDao.getSurveyResponsesByCenterIdAndSurveyId(dataBaseName,
					tcId, surveyId);
			logger.debug("surveyResponse ::" + surveyResponse);
			if (surveyResponse == null) {
				return false;
			} else {
				boolean submited = surveyResponse.isSubmited();
				if (!submited) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isOneFormsSubmitted(String dataBaseName, String tcId, List<FormData> surveyForUser)
			throws SurveyResponseDaoException {
		for (FormData singleForm : surveyForUser) {
			String surveyId = singleForm.getSurveyId();
			SurveyResponse surveyResponse = iSurveyResponseDao.getSurveyResponsesByCenterIdAndSurveyId(dataBaseName,
					tcId, surveyId);
			logger.debug("surveyResponse ::" + surveyResponse);
			if (surveyResponse != null) {
				if (surveyResponse.isSubmited()) {
					return true;
				}
			}
		}
		return false;
	}

	private List<FormData> getFormsForUser(List<FormData> formDataList, String userRole)
			throws ProjectServiceException {
		List<FormData> listOfForms = new ArrayList<FormData>();
		for (FormData formData : formDataList) {
			List<String> usersRolesAllowed = formData.getUsersRolesAllowed();
			for (String role : usersRolesAllowed) {
				if (role.equals(userRole)) {
					listOfForms.add(formData);
				}
			}
		}
		return listOfForms;

	}

	private List<ProjectDetailOfUser> getProjectForFieldAuditor(String tenantId, User user)
			throws MasterDataManagmentDaoException, ProjectDaoException, PartnerDaoException, SchedulerDaoException {
		List<ProjectDetailOfUser> listOfProjectOfUser = new ArrayList<ProjectDetailOfUser>();
		String fieldAuditorId = user.getUserId();
		Set<String> uniqueProjects = iSchedulerDao.getGradingTypeDataByFieldAuditorIdUniqueByProjectId(tenantId,
				fieldAuditorId);
		Set<String> uniqueProjectsIds = iSchedulerDao.getRatingTypeDataByFieldAuditorIdUniqueByProjectId(tenantId,
				fieldAuditorId);
		uniqueProjects.addAll(uniqueProjectsIds);
		logger.debug("uniqueProjects he has ::" + uniqueProjects + " size is :: " + uniqueProjects.size());
		for (String projectId : uniqueProjects) {
			Set<String> partnerIds = iSchedulerDao.getRatingTypeDataByFieldAuditorIdAndProjectId(tenantId,
					fieldAuditorId, projectId);
			Set<String> gradingsPartnerIds = iSchedulerDao.getGradingTypeDataByFieldAuditorIdAndProjectId(tenantId,
					fieldAuditorId, projectId);
			partnerIds.addAll(gradingsPartnerIds);
			for (String partnerId : partnerIds) {
				ProjectDetailOfUser projectOfUser = new ProjectDetailOfUser();
				PartnerDetails partner = iPartnerDao.getPartnerById(tenantId, partnerId);
				ProjectData projectData = surveyDao.getProjectById(tenantId, projectId);
				boolean status = getStatusOfProjectForFieldAuditor(tenantId, projectId, partnerId, fieldAuditorId);
				boolean formStatus = getStatusOfFormProjectForFieldAuditor(tenantId, projectId, partnerId,
						fieldAuditorId);
				projectOfUser.setPartnerName(partner.getPartnerName());
				projectOfUser.setAssignedFormsCompleted(formStatus);
				projectOfUser.setProjectData(projectData);
				projectOfUser.setStatus(status);
				projectOfUser.setPartnerId(partnerId);
				listOfProjectOfUser.add(projectOfUser);
			}
		}
		logger.debug("listOfProjectOfUser ::" + listOfProjectOfUser);
		return listOfProjectOfUser;
	}

	private boolean getStatusOfProjectForFieldAuditor(String tenantId, String projectId, String partnerId,
			String fieldAuditorId) throws SchedulerDaoException {
		List<GradingType> gradingTypeList = iSchedulerDao.getGradingTypeDataByFieldAuditorIdProjectIdAndPartnerId(
				tenantId, fieldAuditorId, projectId, partnerId);
		for (GradingType gradingType : gradingTypeList) {
			if (!gradingType.isAuditStatus()) {
				return false;
			}

		}
		List<RatingType> ratingTypeList = iSchedulerDao.getRatingTypeDataByFieldAuditorIdProjectIdAndPartnerId(tenantId,
				fieldAuditorId, projectId, partnerId);
		for (RatingType ratingType : ratingTypeList) {
			if (!ratingType.isAuditStatus()) {
				return false;
			}
		}
		return true;
	}

	private boolean getStatusOfFormProjectForFieldAuditor(String tenantId, String projectId, String partnerId,
			String fieldAuditorId) throws SchedulerDaoException {
		List<GradingType> gradingTypeList = iSchedulerDao.getGradingTypeDataByFieldAuditorIdProjectIdAndPartnerId(
				tenantId, fieldAuditorId, projectId, partnerId);
		for (GradingType gradingType : gradingTypeList) {
			if (gradingType.isAuditStatus()) {
				return true;
			}

		}
		List<RatingType> ratingTypeList = iSchedulerDao.getRatingTypeDataByFieldAuditorIdProjectIdAndPartnerId(tenantId,
				fieldAuditorId, projectId, partnerId);
		for (RatingType ratingType : ratingTypeList) {
			if (ratingType.isAuditStatus()) {
				return true;
			}
		}
		return false;
	}

	private void validateId(String id, String keyName) throws ProjectServiceException {
		if (id == null || id.isEmpty()) {
			throw new ProjectServiceException(" invalid  " + keyName);
		}
	}

	private FormData buildFormDataFromJson(JSONObject requestBody) throws JsonUtillException, ProjectServiceException {
		FormData formData = new FormData();
		String projectId = JsonUtill.getString(requestBody, ProjectServiceConstant.PROJECT_ID);
		String formName = JsonUtill.getString(requestBody, ProjectServiceConstant.FORM_NAME);
		JSONArray usersRoleAllowed = JsonUtill.getJsonArray(requestBody, ProjectServiceConstant.USER_ROLES_ALLOWED);
		List<String> userRolesAllowedList = getUserRolesFromArray(usersRoleAllowed);
		formData.setFormName(formName);
		formData.setProjectId(projectId);
		formData.setUsersRolesAllowed(userRolesAllowedList);
		logger.debug("formData ::" + formData);
		return formData;
	}

	private List<String> getUserRolesFromArray(JSONArray usersRoleAllowed) throws ProjectServiceException {
		List<String> userRolesAllowedList = new ArrayList<String>();
		boolean isEmpty = usersRoleAllowed.isEmpty();
		if (isEmpty) {
			throw new ProjectServiceException("please specify atleast one user role, that can acces the form");
		}
		for (int i = 0; i < usersRoleAllowed.size(); i++) {
			String userRole = (String) usersRoleAllowed.get(i);
			if (userRole.equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)
					|| userRole.equals(UserAccessManagementServiceConstants.CLIENT_SPONSOR)
					|| userRole.equals(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
				userRolesAllowedList.add(userRole);
			} else {
				throw new ProjectServiceException(userRole + " user role does not exist");
			}
		}
		return userRolesAllowedList;
	}

	@SuppressWarnings("unchecked")
	private JSONArray buildJsonFromFormDataList(List<FormData> formDataList) {
		JSONArray formArray = new JSONArray();
		for (FormData singleForm : formDataList) {
			JSONObject formJson = buildJsonFromFormData(singleForm);
			formArray.add(formJson);
		}
		return formArray;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonFromFormData(FormData formData) {
		String formName = formData.getFormName();
		String formId = formData.getFormId();
		String projectId = formData.getProjectId();
		String surveyId = formData.getSurveyId();
		long createdTime = formData.getCreatedTime();
		boolean isPublish = formData.isPublish();
		List<String> usersRolesAllowed = formData.getUsersRolesAllowed();
		JSONArray userRoles = new JSONArray();
		for (String userRole : usersRolesAllowed) {
			userRoles.add(userRole);
		}
		JSONObject formJson = new JSONObject();
		formJson.put(ProjectServiceConstant.FORM_ID, formId);
		formJson.put(ProjectServiceConstant.FORM_NAME, formName);
		formJson.put(ProjectServiceConstant.PROJECT_ID, projectId);
		formJson.put(ProjectServiceConstant.SURVEY_ID, surveyId);
		formJson.put(ProjectServiceConstant.TIME_OF_CREATE, getSurveyStringFormateTimeFromMiliSecond(createdTime));
		formJson.put(ProjectServiceConstant.USER_ROLES_ALLOWED, userRoles);
		formJson.put(ProjectServiceConstant.IS_PUBLISH, isPublish);
		return formJson;
	}

	private FormData validateFormForPublish(String databaseName, String formId)
			throws ProjectServiceException, ProjectDaoException {
		FormData formData = surveyDao.getFormById(databaseName, formId);
		if (formData == null) {
			throw new ProjectServiceException(" inValid formId ");
		}
		boolean publish = formData.isPublish();
		if (publish) {
			logger.error("form is already published ");
			throw new ProjectServiceException("form is already published ");
		}
		return formData;
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public String getPublishFormDataById(String databaseName, String formId) throws ProjectServiceException {
//		try {
//			logger.debug("inside getPublishFormDataById method of SurveyServiceImpl , databaseName:  " + databaseName);
//			validateId(formId, ProjectServiceConstant.FORM_ID);
//			logger.debug(" id :: " + formId);
//			FormData formData = surveyDao.getFormById(databaseName, formId);
//			if (formData == null) {
//				throw new ProjectServiceException(" invalid form Id");
//			}
//			JSONObject responseJSON = new JSONObject();
//			responseJSON.put(ProjectServiceConstant.STATUS, formData.isPublish());
//			return responseJSON.toString();
//		} catch (Exception e) {
//			logger.error(" unable to find form Data ", e);
//			throw new ProjectServiceException(" unable to find form Data " + e.getMessage(), e);
//		}
//	}

	private List<TrainingCenterDetails> getTrainingCentersForPartner(String tenantId, String patnerId, String projectId)
			throws PartnerDaoException {
		logger.debug("inside getTrainingCentersForPartner method of projectServiceIMPL  , databaseName:  " + tenantId);
//		List<TrainingCenterDetails> tcs = iPartnerDao.getAllTrainingCenterDetailsByPartnerId(tenantId, patnerId);
//		masterDataService.getProjectMappingByProjectIdAndPartnerId(tenantId, projectId, patnerId);
		List<TrainingCenterDetails> tcsList = iPartnerDao
				.getAllTrainingCenterDetailsByPartnerIdAndProjectIdInMapping(tenantId, projectId, patnerId);
		return tcsList;
	}

	private Map<String, Object> getTrainingCenterStatusForPartners(String tenantId, List<PartnerDetails> details,
			List<FormData> formsForProject, String projectId)
			throws PartnerDaoException, SurveyResponseDaoException, org.json.simple.parser.ParseException {
		List<FormData> ratingFormList = getRatingForm(formsForProject);
		Map<String, Object> responseMap = new HashMap<String, Object>();
		int totalTranningCenter = 0;
		int comletedTC = 0;
		List<Double> precentMarksList = new ArrayList<Double>();
		for (PartnerDetails partnerDetail : details) {
			String partnerId = partnerDetail.getPartnerId();
			logger.debug("current PartnerName is " + partnerDetail.getPartnerName());
			List<TrainingCenterDetails> tcs = getTrainingCentersForPartner(tenantId, partnerId, projectId);
			// partnerWisetcMap.put(partnerId, tcs);
			for (TrainingCenterDetails tc : tcs) {
				String tcId = tc.getTcId();
				logger.debug("current tcId is ::" + tcId);
				++totalTranningCenter;
				boolean completedProject = true;
				for (FormData formData : ratingFormList) {
					String surveyId = formData.getSurveyId();
					SurveyResponse tempsurveyResponse = iSurveyResponseDao
							.getSurveyResponsesByCenterIdAndSurveyId(tenantId, tcId, surveyId);
//					surveyResponses.add(tempsurveyResponse);
//					for (SurveyResponse surveyResponse : tempsurveyResponse) {
					logger.debug(
							tcId + " has submitted ther form " + formData.getFormName() + " ? " + tempsurveyResponse);
					if (tempsurveyResponse != null && tempsurveyResponse.isSubmited()) {
						logger.debug("if :: " + tempsurveyResponse);
						double precentMarks = ((double) tempsurveyResponse.getTotalScore()
								/ (double) tempsurveyResponse.getMaxMarks()) * 100.0;
						logger.debug("precentMarks " + precentMarks);
						precentMarksList.add(precentMarks);
//						if (completedProject) {
//							completedProject = false;
//						}
//						}
					} else {
						completedProject = false;
					}
				} // end of formData loop
				if (completedProject)
					++comletedTC;

			} // end of tc loop

		} // end of partnerLoop
		logger.debug("precentMarksList ::" + precentMarksList);
		double totalprecentMarks = 0.0;
		for (double double1 : precentMarksList) {
			totalprecentMarks += double1;
		}
		totalprecentMarks = totalprecentMarks / precentMarksList.size();
		responseMap.put(ProjectServiceConstant.AVG_PRECENTAGE_MARKS, totalprecentMarks);
		responseMap.put(ProjectServiceConstant.TOATL_TRANING_CENTER_COMPLETED, comletedTC);
		responseMap.put(ProjectServiceConstant.TOATL_TRANING_CENTER, totalTranningCenter);

		return responseMap;
	}

	private List<FormData> getRatingForm(List<FormData> formDataList) {
		List<FormData> ratingFormDataList = new ArrayList<FormData>();
		List<String> usersRolesList = new ArrayList<String>();
		usersRolesList.add(UserAccessManagementServiceConstants.CENTER_IN_CHARGE);
		usersRolesList.add(UserAccessManagementServiceConstants.FIELD_AUDITOR);
		for (int i = 0; i < formDataList.size(); i++) {
			FormData formData = formDataList.get(i);
			List<String> usersRolesAllowed = formData.getUsersRolesAllowed();
			if (usersRolesAllowed.size() == 2 && usersRolesAllowed.containsAll(usersRolesList)) {
				ratingFormDataList.add(formData);
			}
		}
		return ratingFormDataList;
	}

	private void validateDeadLine(ProjectData projectData) throws ProjectServiceException {
		long endDate = projectData.getEndDate();
		long selfAssignmentDeadline = projectData.getSelfAssignmentDeadline();
		if (selfAssignmentDeadline > endDate) {
			throw new ProjectServiceException("Self assessment dead line cannot be more than Project's dead line.");
		}
	}

	private void validateProjectEndDate(ProjectData projectById) throws ProjectServiceException {
		logger.debug("### Validating whether form creation is happening before projectEndDate...");
		long endDate = projectById.getEndDate();
		logger.debug("endDate: " + endDate);
		long current = new Date().getTime();
		logger.debug("current: " + current);
		if (current > endDate) {
			throw new ProjectServiceException("Forms cannot be created after Project's Dead line.");
		}
	}

	private void addSchedulerData(Connection connection, String databaseName, FormData formData)
			throws SchedulerDaoException, MasterDataManagmentDaoException, PartnerDaoException, UserDaoException,
			ProjectDaoException, JsonUtillException, ProjectServiceException, AssignerException {
		logger.debug("in addSchedulerData: " + formData);
		boolean gradingTypeDate = isFromGradingForm(formData);
		boolean ratingTypeDate = isFromRatingForm(formData);
		logger.debug("mapping for the project is not present");
		List<ProjectMapping> projectMappingList = masterDataService.getProjectMappingByProjectId(connection,
				databaseName, formData.getProjectId());
		logger.debug("(No tcs)projectMappingList size: " + projectMappingList.size());
		String formId = formData.getFormId();
		String projectId = formData.getProjectId();
		// list
		List<String> ratingJSONS = new ArrayList<String>();
		List<String> gradingJSONS = new ArrayList<String>();
		for (ProjectMapping ProjectMapping : projectMappingList) {
			SchedulerMapping scheduler = new SchedulerMapping();
			String partnerId = ProjectMapping.getPartnerId();
			scheduler.setFormId(formId);
			scheduler.setProjectId(projectId);
			scheduler.setPartnerId(partnerId);
			GradingEnable gradingEnable = masterDataService.getGradingEnableByProjectIdAndPartnerId(connection,
					databaseName, projectId, partnerId);
			boolean oldEnable = gradingEnable.isGradingEnable();
			GradingType gradingType = iSchedulerDao.getGradingTypeDataByPartnerIdProjectIdAndFormId(connection,
					databaseName, partnerId, projectId, formId);

			if (gradingTypeDate && oldEnable && gradingType == null) {
				iSchedulerDao.addGradingTypeData(connection, databaseName, scheduler);
				if (ReportingDBService.ENABLED) {
					JSONObject jsonObject = buildJsonArrayForAddingGradingFormToReportDb(connection, databaseName,
							ProjectMapping, formData);
					logger.debug("GradingForm:: " + jsonObject);
					gradingJSONS.add(jsonObject.toString());

				}
			} else if (ratingTypeDate) {
				String tcId = ProjectMapping.getTcId();
				scheduler.setTcId(tcId);
				iSchedulerDao.addRatingTypeData(connection, databaseName, scheduler);
				// send json to ReportDB
				if (ReportingDBService.ENABLED) {
					JSONObject jsonObject = buildJsonArrayForAddingRatingFormToReportDb(connection, databaseName,
							ProjectMapping, formData);
					// list.add(jsonObject)
					logger.debug("RatingForm:: " + jsonObject);
					ratingJSONS.add(jsonObject.toString());
				}

			}
		}
		logger.debug("Rating JSON size: " + ratingJSONS.size());
		logger.debug("Grading JSON size: " + gradingJSONS.size());
		if (ReportingDBService.ENABLED) {
			if (ratingJSONS.size() > 0) {
				AddRatingJobAssigner assigner = new AddRatingJobAssigner();
				assigner.assignAddRatingJobToDatabase(databaseName, ratingJSONS);
				logger.debug("Rating forms assigned.");
			}
			if (gradingJSONS.size() > 0) {
				AddGradingJobAssigner assigner = new AddGradingJobAssigner();
				assigner.assignAddGradingJobToDatabase(databaseName, gradingJSONS);
				logger.debug("Grading forms assigned.");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonArrayForAddingRatingFormToReportDb(Connection connection, String databaseName,
			ProjectMapping projectMapping, FormData formData) throws PartnerDaoException, UserDaoException,
			ProjectDaoException, JsonUtillException, ProjectServiceException {
//		JSONObject jsonObject = new JSONObject();
		String tcId = projectMapping.getTcId();
		String partnerId = projectMapping.getPartnerId();
		PartnerDetails partnerDetails = iPartnerDao.getPartnerById(connection, databaseName, partnerId);
		TrainingCenterDetails tcDetails = iPartnerDao.getTrainingCenterDetailsByTcId(connection, databaseName, tcId);
		JSONObject ratingFormJson = buildPartnerJsonForReportDb(connection, databaseName, partnerDetails);
		JSONObject projectJsonObject = buildProjectJsonForReportDb(connection, databaseName, formData);
		JSONObject trainingCenterJson = buildtrainingCenterJsonForReportDb(connection, databaseName, tcDetails);
		ratingFormJson.put(ReportingDbJSONConstant.TRAINING_CENTER, trainingCenterJson);
		ratingFormJson.put(ReportingDbJSONConstant.PROJECT, projectJsonObject);
		return ratingFormJson;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonArrayForAddingGradingFormToReportDb(Connection connection, String databaseName,
			ProjectMapping projectMapping, FormData formData) throws PartnerDaoException, UserDaoException,
			ProjectDaoException, JsonUtillException, ProjectServiceException {
//		JSONObject jsonObject = new JSONObject();
		String partnerId = projectMapping.getPartnerId();
		PartnerDetails partnerDetails = iPartnerDao.getPartnerById(connection, databaseName, partnerId);
//		TrainingCenterDetails tcDetails = iPartnerDao.getTrainingCenterDetailsByTcId(connection, databaseName, tcId);
		JSONObject ratingFormJson = buildPartnerJsonForReportDb(connection, databaseName, partnerDetails);
		JSONObject projectJsonObject = buildProjectJsonForReportDb(connection, databaseName, formData);
//		JSONObject trainingCenterJson = buildtrainingCenterJsonForReportDb(connection, databaseName, tcDetails);
		ratingFormJson.put(ReportingDbJSONConstant.PROJECT, projectJsonObject);
		return ratingFormJson;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildPartnerJsonForReportDb(Connection connection, String databaseName,
			PartnerDetails partnerDetails) throws UserDaoException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ReportingDbJSONConstant.PIA_NAME, partnerDetails.getPartnerName());
		jsonObject.put(ReportingDbJSONConstant.PARTNER_ID, partnerDetails.getPartnerId());
//		jsonObject.put(ReportingDbJSONConstant.DISTRICT, "");
		String clientSponsorId = partnerDetails.getClientSponsorId();
		User user = iUserDao.getUserByUserId(connection, databaseName, clientSponsorId);
		jsonObject.put(ReportingDbJSONConstant.HEAD_PERSONNEL, user.getFirstName());
		jsonObject.put(ReportingDbJSONConstant.HEAD_PERSON_EMAIL, user.getEmail());
		jsonObject.put(ReportingDbJSONConstant.CONTACT, user.getPhone());
		jsonObject.put(ReportingDbJSONConstant.STATUS, false);
		JSONObject scoreJson = new JSONObject();
		scoreJson.put(ReportingDbJSONConstant.CENTER_RATING, 0.0);
		scoreJson.put(ReportingDbJSONConstant.FINAL_AVG, 0.0);
		scoreJson.put(ReportingDbJSONConstant.PROJECT_GRADING, 0.0);
		scoreJson.put(ReportingDbJSONConstant.GRADE, "");
		jsonObject.put(ReportingDbJSONConstant.SCORE, scoreJson);
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildtrainingCenterJsonForReportDb(Connection connection, String databaseName,
			TrainingCenterDetails tcDetails) throws UserDaoException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ReportingDbJSONConstant.TC_ID, tcDetails.getTcId());
		jsonObject.put(ReportingDbJSONConstant.TC_NAME, tcDetails.getTcName());
		jsonObject.put(ReportingDbJSONConstant.CENTER_ADDRESS, tcDetails.getCenterAddress());
		jsonObject.put(ReportingDbJSONConstant.LATITUDE, Double.parseDouble(tcDetails.getLatitude()));
		jsonObject.put(ReportingDbJSONConstant.LONGITUDE, Double.parseDouble(tcDetails.getLongitude()));
		jsonObject.put(ReportingDbJSONConstant.C_DISTRICT, tcDetails.getDistrict());
		String centerInchargeId = tcDetails.getCenterInchargeId();
		User user = iUserDao.getUserByUserId(connection, databaseName, centerInchargeId);
		jsonObject.put(ReportingDbJSONConstant.CIC_NAME, user.getFirstName());
		jsonObject.put(ReportingDbJSONConstant.CIC_PHONE, user.getPhone());
		return jsonObject;

	}

	@SuppressWarnings("unchecked")
	private JSONObject buildProjectJsonForReportDb(Connection connection, String databaseName, FormData formData)
			throws ProjectDaoException, JsonUtillException, ProjectServiceException {
		JSONObject jsonObject = new JSONObject();
		String projectId = formData.getProjectId();
		ProjectData project = surveyDao.getProjectById(connection, databaseName, projectId);
		jsonObject.put(ReportingDbJSONConstant.PROJECT_NAME, project.getProjectName());
		jsonObject.put(ReportingDbJSONConstant.PROJECT_ID, project.getProjectId());
		JSONObject formJson = buildFormForAddingRating(connection, databaseName, formData);
		jsonObject.put(ReportingDbJSONConstant.FORM, formJson);
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildFormForAddingRating(Connection connection, String databaseName, FormData formData)
			throws ProjectDaoException, JsonUtillException, ProjectServiceException {
		JSONObject jsonObject = new JSONObject();
		String surveyId = formData.getSurveyId();
		String type = isFromGradingForm(formData) ? ProjectServiceConstant.GRADING_TYPE
				: isFromRatingForm(formData) ? ProjectServiceConstant.RATING_TYPE : null;
		if (type == null) {
			return new JSONObject();
		}
		Survey survey = surveyDao.getSurveyById(connection, databaseName, surveyId);
		jsonObject.put(ProjectServiceConstant.FORM_NAME, formData.getFormName());
		jsonObject.put(ReportingDbJSONConstant.FORM_ID, formData.getFormId());
		jsonObject.put(ReportingDbJSONConstant.FORM_TYPE, type);
		jsonObject.put(ReportingDbJSONConstant.FORM_STATUS, false);
		jsonObject.put(ReportingDbJSONConstant.MAX_MARKS, survey.getMaxScore());
		jsonObject.put(ReportingDbJSONConstant.SA_SCORE, 0);
		jsonObject.put(ReportingDbJSONConstant.FA_SCORE, 0);
		JSONObject timeJsonObject = buildTimingJsonForReportDb();
		jsonObject.put(ReportingDbJSONConstant.TIMING, timeJsonObject);
		JSONObject fieldAuditorJsonObject = buildFieldAuditorForReportDb();
		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR, fieldAuditorJsonObject);
		JSONArray sectionsJsonObject = buildSectionJsonForReportingDb(survey);
		jsonObject.put(ReportingDbJSONConstant.SECTIONS, sectionsJsonObject);

		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	private JSONArray buildSectionJsonForReportingDb(Survey survey) throws JsonUtillException, ProjectServiceException {
		String surveyData = survey.getSurveyData();
		JSONArray jsonArrayResponse = new JSONArray();
		JSONObject surveyJson = (JSONObject) JSONValue.parse(surveyData);
		JSONArray sectionsArray = (JSONArray) surveyJson.get(ProjectServiceConstant.SECTIONS);
		for (int i = 0; i < sectionsArray.size(); i++) {
			JSONArray parameterArray = new JSONArray();
			JSONObject singleSection = new JSONObject();
			JSONObject jsonObject = (JSONObject) sectionsArray.get(i);
			String sectionId = jsonObject.get(ProjectServiceConstant.SECTION_ID).toString();
			JSONArray sectionQuesArray = (JSONArray) jsonObject.get(ProjectServiceConstant.SECTION_QUESTIONS);
			for (int j = 0; j < sectionQuesArray.size(); j++) {
				JSONObject singleQuesJson = new JSONObject();
				JSONObject singleQues = (JSONObject) sectionQuesArray.get(j);
				String parameter = singleQues.get(ProjectServiceConstant.QUES_ID).toString();
				singleQuesJson.put(ReportingDbJSONConstant.PARAMETER_ID, parameter);
//				Integer maxScore = calculateSingleQuesMaxMark(singleQues);
				int maxScore = Integer.parseInt(singleQues.get(ProjectServiceConstant.SCORE) + "");
				singleQuesJson.put(ReportingDbJSONConstant.MAX_MARKS, maxScore);
				singleQuesJson.put(ReportingDbJSONConstant.SA_SCORE, 0);
				singleQuesJson.put(ReportingDbJSONConstant.FA_SCORE, 0);
				singleQuesJson.put(ReportingDbJSONConstant.SA_REMARK, "");
				singleQuesJson.put(ReportingDbJSONConstant.FA_REMARK, "");
				parameterArray.add(singleQuesJson);
			}
			singleSection.put(ReportingDbJSONConstant.SECTION_ID, sectionId);
			singleSection.put(ReportingDbJSONConstant.PARAMETERS, parameterArray);
			jsonArrayResponse.add(singleSection);
		}
		return jsonArrayResponse;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildFieldAuditorForReportDb() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR_NAME, "");
		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR_PHONE, "");
		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR_LOCATION, "");
		jsonObject.put(ReportingDbJSONConstant.SEC_FIELD_AUDITOR_NAME, "");
		jsonObject.put(ReportingDbJSONConstant.FA_START_TIME, "");
		jsonObject.put(ReportingDbJSONConstant.FA_END_TIME, "");
		jsonObject.put(ReportingDbJSONConstant.FA_START_DATE, "");
		jsonObject.put(ReportingDbJSONConstant.FA_END_DATE, "");
		jsonObject.put(ReportingDbJSONConstant.SIGN_OFF_TIME, "");
		jsonObject.put(ReportingDbJSONConstant.OTP, "");
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildTimingJsonForReportDb() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ReportingDbJSONConstant.START_TIME, "");
		jsonObject.put(ReportingDbJSONConstant.END_TIME, "");
		jsonObject.put(ReportingDbJSONConstant.START_DATE, "");
		jsonObject.put(ReportingDbJSONConstant.END_DATE, "");
		return jsonObject;
	}

	private boolean isFromGradingForm(FormData formData) {
		List<String> usersRolesList = new ArrayList<String>();
		usersRolesList.add(UserAccessManagementServiceConstants.CLIENT_SPONSOR);
		usersRolesList.add(UserAccessManagementServiceConstants.FIELD_AUDITOR);
		List<String> usersRolesAllowed = formData.getUsersRolesAllowed();
		if (usersRolesAllowed.size() == 2 && usersRolesAllowed.containsAll(usersRolesList)) {
			return true;
		}
		return false;
	}

	private boolean isFromRatingForm(FormData formData) {
		List<String> usersRolesList = new ArrayList<String>();
		usersRolesList.add(UserAccessManagementServiceConstants.CENTER_IN_CHARGE);
		usersRolesList.add(UserAccessManagementServiceConstants.FIELD_AUDITOR);
		List<String> usersRolesAllowed = formData.getUsersRolesAllowed();
		if (usersRolesAllowed.size() == 2 && usersRolesAllowed.containsAll(usersRolesList)) {
			return true;
		}
		return false;
	}
}
