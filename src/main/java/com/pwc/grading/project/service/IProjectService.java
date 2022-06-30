package com.pwc.grading.project.service;

import com.pwc.grading.project.dao.exception.ProjectDaoException;
import com.pwc.grading.project.service.exception.ProjectServiceException;

/**
 * IProjectService interface is used to create the project, form and surveys
 * 
 *
 */
public interface IProjectService {
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
	public String getSurveyById(String databaseName, String id, String userRole, String auditFor, String auditForId,
			String taskType) throws ProjectServiceException;

	/**
	 * this method is used to get All Project Data
	 * 
	 * @param databaseName
	 * @return
	 * @throws ProjectServiceException
	 */
	public String getAllProjectData(String databaseName) throws ProjectServiceException;

	/**
	 * this method is used to get All SurveyData
	 * 
	 * @param databaseName
	 * @return
	 * @throws ProjectServiceException
	 */
	public String getAllSurveyData(String databaseName) throws ProjectServiceException;

	/**
	 * this method is used to updateProjectById
	 * 
	 * @param databaseName
	 * @param id
	 * @param requestJSON
	 * @return
	 * @throws ProjectServiceException
	 * @throws ProjectDaoException
	 */
	public String updateProjectById(String databaseName, String id, String requestJSON)
			throws ProjectServiceException, ProjectDaoException;

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
	public String updateSurveyById(String databaseName, String id, String requestSurvey)
			throws ProjectServiceException, ProjectDaoException;

	/**
	 * this method is used to deleteProjectById
	 * 
	 * @param databaseName
	 * @param id
	 * @return
	 * @throws ProjectServiceException
	 * @throws ProjectDaoException
	 */
	public String deleteProjectById(String databaseName, String id) throws ProjectServiceException, ProjectDaoException;

	/**
	 * this method is used to create ProjectData
	 * 
	 * @param tenantId
	 * @param requestJson
	 * @return
	 * @throws ProjectServiceException
	 */
	public String createProjectData(String tenantId, String requestJson) throws ProjectServiceException;

	/**
	 * this method is used to get Project By Id
	 * 
	 * @param databaseName
	 * @param id
	 * @return
	 * @throws ProjectServiceException
	 * @throws ProjectDaoException
	 */
	public String getProjectById(String databaseName, String id) throws ProjectServiceException, ProjectDaoException;

	/**
	 * this method is used to get Project Details Of User
	 * 
	 * @param tenantId
	 * @param userRole
	 * @param userEmail
	 * @return
	 * @throws ProjectServiceException
	 */
	public String getProjectDetailsOfUser(String tenantId, String userRole, String userEmail)
			throws ProjectServiceException;

	/**
	 * this method is used to createFormData
	 * 
	 * @param tenantId
	 * @param requestJson
	 * @return
	 * @throws ProjectServiceException
	 */
	public String createFormData(String tenantId, String requestJson) throws ProjectServiceException;

	/**
	 * this method is used to update Form By formId
	 * 
	 * @param tenantId
	 * @param formId
	 * @param requestJson
	 * @return
	 * @throws ProjectServiceException
	 */
	public String updateFormById(String tenantId, String formId, String requestJson) throws ProjectServiceException;

	/**
	 * this method is used to get Forms By ProjectId
	 * 
	 * @param tenantId
	 * @param projectId
	 * @return
	 * @throws ProjectServiceException
	 */
	public String getFormsByProjectId(String tenantId, String projectId) throws ProjectServiceException;

	/**
	 * this method is used to delete FormData
	 * 
	 * @param tenantId
	 * @param formId
	 * @return
	 * @throws ProjectServiceException
	 */
	public String deleteFormData(String tenantId, String formId) throws ProjectServiceException;

	/**
	 * this method is used to get Form By Id
	 * 
	 * @param tenantId
	 * @param formId
	 * @return
	 * @throws ProjectServiceException
	 */
	public String getFormById(String tenantId, String formId) throws ProjectServiceException;

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
	public String getFormsOfUser(String tenantId, String userRole, String projectId, String taskType, String partnerId)
			throws ProjectServiceException;

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
	public String publishFormDataById(String tenantId, String formId, String requestBody)
			throws ProjectServiceException;

	/**
	 * this method is used to get ProgressMeter Data
	 * 
	 * @param tenantId
	 * @param projectId
	 * @return
	 * @throws ProjectServiceException
	 */
	public String getProgressMeterData(String tenantId, String projectId) throws ProjectServiceException;

}
