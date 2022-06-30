package com.pwc.grading.project.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.pwc.grading.project.dao.exception.ProjectDaoException;
import com.pwc.grading.project.model.FormData;
import com.pwc.grading.project.model.ProjectData;
import com.pwc.grading.project.model.Survey;

/**
 * An interface class which is used to perform all
 * Project related database operations.
 *
 */
public interface IProjectDao {
	/**
	 * This method is used to get Survey By Id
	 * 
	 * @param databaseName the database name.
	 * @param id the id of the survey
	 * @return the survey details
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public Survey getSurveyById(String databaseName, String id) throws ProjectDaoException;

	/**
	 * This method is used to get All Surveys
	 * 
	 * @param databaseName the database name.
	 * @return all the survey details 
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public List<Survey> getAllSuveys(String databaseName) throws ProjectDaoException;

	/**
	 * This method is used to get all Projects
	 * 
	 * @param databaseName the database name.
	 * @return all the project details 
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public List<ProjectData> getAllProjects(String databaseName) throws ProjectDaoException;

	/**
	 * This method is used to update SurveyData
	 * 
	 * @param databaseName the database name.
	 * @param survey  the survey details to be updated.
	 * @throws ProjectDaoException  if any exception occurs when performing this operation.
	 */
	public void updateSurveyData(String databaseName, Survey survey) throws ProjectDaoException;

	/**
	 * This method is used to update ProjectData
	 * 
	 * @param databaseName the database name.
	 * @param project the project details to be updated.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public void updateProjectData(String databaseName, ProjectData project) throws ProjectDaoException;

	/**
	 * This method is used to create ProjectData
	 * 
	 * @param databaseName the database name.
	 * @param project  project details to be created.
	 * @return the id of the project.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public String createProjectData(String databaseName, ProjectData project) throws ProjectDaoException;

	/**
	 * This method is used to get Project By Id
	 * 
	 * @param databaseName the database name.
	 * @param id the id of the project.
	 * @return the project details 
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	ProjectData getProjectById(String databaseName, String id) throws ProjectDaoException;

	/**
	 * This method is used to check whether the project is exit or not
	 * 
	 * @param databaseName the database name.
	 * @param id the id of the project.
	 * @return true if exist, false if not exists.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public boolean isProjectExist(String databaseName, String id) throws ProjectDaoException;

	/**
	 * This method is used to add SurveyData
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param surveyId the id of the survey
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public void addSurveyData(Connection connection, String databaseName, String surveyId) throws ProjectDaoException;

	/**
	 * This method is used to update FormData
	 * 
	 * @param databaseName  the database name.
	 * @param formId the id of the form.
	 * @param formData the survey details to be updated.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public void updateFormData(String databaseName, String formId, FormData formData) throws ProjectDaoException;

	/**
	 * This method is used to get the Forms by the ProjectId
	 * 
	 * @param tenantId the survey details to be updated.
	 * @param projectId the id of the project.
	 * @return all the form details for the project.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public List<FormData> getFormsByProjectId(String tenantId, String projectId) throws ProjectDaoException;

	/**
	 * This method is used to get Form By Id
	 * 
	 * @param databaseName  the database name.
	 * @param formId the id of the form.
	 * @return the id of the project.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	FormData getFormById(String databaseName, String formId) throws ProjectDaoException;

	/**
	 * This method is used to get all Forms
	 * 
	 * @param databaseName  the database name.
	 * @return all the form details.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public List<FormData> getAllForms(String databaseName) throws ProjectDaoException;

	/**
	 * this method is used to delete Survey By Id
	 * 
	 * @param connection  if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param id the id of the survey
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public void deleteSurveyById(Connection connection, String databaseName, String id) throws ProjectDaoException;

	/**
	 * this method is used to delete Form By Id
	 * 
	 * @param connection  if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param formId the id of the form.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public void deleteFormById(Connection connection, String databaseName, String formId) throws ProjectDaoException;

	/**
	 * this method is used to delete Project By Id
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param id the id of the project.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public void deleteProjectById(Connection connection, String databaseName, String id) throws ProjectDaoException;

	/**
	 * this method is used to get Forms By SurveyId
	 * 
	 * @param databaseName  the database name.
	 * @param surveyId the id of the survey
	 * @return all the form details.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	FormData getFormsBySurveyId(String databaseName, String surveyId) throws ProjectDaoException;

	/**
	 * This method is used to get Forms By ProjectId
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param projectId the id of the project.
	 * @return all the form details for the project.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	List<FormData> getFormsByProjectId(Connection connection, String databaseName, String projectId)
			throws ProjectDaoException;

	/**
	 * this method is used to create FormData
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param formData the form details
	 * @return formId and surveyId in the form of map.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	Map<String, String> createFormData(Connection connection, String databaseName, FormData formData)
			throws ProjectDaoException;

	/**
	 * This method is used to publish FormData By Id
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param formId the id of the form
	 * @param status the status to be updated.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public void publishFormDataById(Connection connection, String databaseName, String formId, boolean status)
			throws ProjectDaoException;

	/**
	 * this method is used to publish SurveyData By Id
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param surveyId the id of the survey
	 * @param status the status to be updated.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public void publishSurveyDataById(Connection connection, String databaseName, String surveyId, boolean status)
			throws ProjectDaoException;

	/**
	 * this method is used to get Project By Id
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName the database name.
	 * @param id the id of the project.
	 * @return the project details.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	ProjectData getProjectById(Connection connection, String databaseName, String id) throws ProjectDaoException;

	/**
	 * this method is used to getSurveyById
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName the database name.
	 * @param surveyId the id of the project.
	 * @return the survey details.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	Survey getSurveyById(Connection connection, String databaseName, String surveyId) throws ProjectDaoException;

	/**
	 * this method is used to get Form By Id
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName the database name.
	 * @param formId the id of the form.
	 * @return the form details.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	FormData getFormById(Connection connection, String databaseName, String formId) throws ProjectDaoException;

}
