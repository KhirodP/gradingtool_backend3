package com.pwc.grading.project.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.project.dao.IProjectDao;
import com.pwc.grading.project.dao.ProjectDaoConstant;
import com.pwc.grading.project.dao.ProjectQueryConstans;
import com.pwc.grading.project.dao.exception.ProjectDaoException;
import com.pwc.grading.project.model.FormData;
import com.pwc.grading.project.model.ProjectData;
import com.pwc.grading.project.model.Survey;

/**
 * ProjectDaoImpl class is the implementation of {@link}IProjectDao.It is a Dao
 * class and It is used to create, update and delete the project,form and
 * surveys
 * 
 * @author Reactiveworks-21
 *
 */
@Singleton
public class ProjectDaoImpl implements IProjectDao {
	private static final Logger logger = LoggerFactory.getLogger(ProjectDaoImpl.class);

	/**
	 * This method is used to add SurveyData
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param surveyId the id of the survey
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	public void addSurveyData(Connection connection, String databaseName, String surveyId) throws ProjectDaoException {
		logger.debug(".in add survey, surveyId  is: " + surveyId);
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.INSERT_SURVEY
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setLong(2, new Date().getTime());
			prepareStatement.setString(3, null);
			prepareStatement.setString(4, null);
			prepareStatement.setString(5, "{}");
			prepareStatement.setBoolean(6, false);
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " survey created successfully");
		} catch (Exception e) {
			throw new ProjectDaoException("unable to add Survey:: " + e.getMessage());
		} finally {
			logger.debug("closing the preparedStatment connection");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to get Survey By Id
	 * 
	 * @param databaseName the database name.
	 * @param id the id of the survey
	 * @return the survey details
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public Survey getSurveyById(String databaseName, String surveyId) throws ProjectDaoException {
		logger.debug(".in getSurveyById in ProjectDaoImpl, databaseName:: " + databaseName);
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement preparedStatment = null;
		try {
			connection = MSSqlServerUtill.getConnection();

			preparedStatment = connection.prepareStatement(ProjectQueryConstans.SELECT_SURVEY_BY_SURVEY_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			preparedStatment.setString(1, surveyId);
			resultSet = preparedStatment.executeQuery();
			if (resultSet.next()) {
				logger.debug("Survey exist");
				Survey survey = buildSurveyFromResultSet(resultSet);
				return survey;
			}
		} catch (Exception e) {
			logger.error("unable to getSurveyById  " + e.getMessage());
			throw new ProjectDaoException("unable to get SurveyById  " + e.getMessage());
		} finally {
			logger.debug("closing the Survey connections");
			MSSqlServerUtill.close(resultSet, preparedStatment, connection);
		}
		return null;
	}

	/**
	 * this method is used to getSurveyById
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName the database name.
	 * @param surveyId the id of the project.
	 * @return the survey details.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public Survey getSurveyById(Connection connection, String databaseName, String surveyId)
			throws ProjectDaoException {
		logger.debug(".in getSurveyById in ProjectDaoImpl, databaseName:: " + databaseName);
		ResultSet resultSet = null;
		PreparedStatement preparedStatment = null;
		try {

			preparedStatment = connection.prepareStatement(ProjectQueryConstans.SELECT_SURVEY_BY_SURVEY_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			preparedStatment.setString(1, surveyId);
			resultSet = preparedStatment.executeQuery();
			if (resultSet.next()) {
				logger.debug("Survey exist");
				Survey survey = buildSurveyFromResultSet(resultSet);
				return survey;
			}
		} catch (Exception e) {
			logger.error("unable to getSurveyById  " + e.getMessage());
			throw new ProjectDaoException("unable to get SurveyById  " + e.getMessage());
		} finally {
			logger.debug("closing the Survey connections");
			MSSqlServerUtill.close(resultSet, preparedStatment, null);
		}
		return null;
	}

	/**
	 * This method is used to get All Surveys
	 * 
	 * @param databaseName the database name.
	 * @return all the survey details 
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<Survey> getAllSuveys(String databaseName) throws ProjectDaoException {
		logger.debug(".in getAllSurveys in ProjectDaoImpl, databaseName:: " + databaseName);
		List<Survey> surveyList = new ArrayList<Survey>();
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			statement = connection.prepareStatement(ProjectQueryConstans.SELECT_SURVEYS
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				logger.debug("Survey exist");
				Survey survey = buildSurveyFromResultSet(resultSet);
				surveyList.add(survey);
			}
		} catch (Exception e) {
			logger.error("unable to get All Surveys  " + e.getMessage());
			throw new ProjectDaoException("unable to get All Surveys  " + e.getMessage());
		} finally {
			logger.debug("closing the Survey connections");
			MSSqlServerUtill.close(resultSet, statement, connection);
		}
		return surveyList;
	}

	/**
	 * This method is used to get all Projects
	 * 
	 * @param databaseName the database name.
	 * @return all the project details 
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<ProjectData> getAllProjects(String databaseName) throws ProjectDaoException {
		logger.debug(".in get All project by id in ProjectDaoImpl, ");
		List<ProjectData> projectList = new ArrayList<ProjectData>();
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			statement = connection.prepareStatement(ProjectQueryConstans.SELECT_PROJECTS
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				logger.debug("project exist");
				ProjectData projectData = buildProjectByResultSet(resultSet);
				projectList.add(projectData);
			}
		} catch (Exception e) {
			logger.error("unable to get all project " + e.getMessage());
			throw new ProjectDaoException("unable to get all project " + e.getMessage());
		} finally {
			logger.debug("closing the project connections");
			MSSqlServerUtill.close(resultSet, statement, connection);
		}
		return projectList;
	}

	/**
	 * This method is used to update SurveyData
	 * 
	 * @param databaseName the database name.
	 * @param survey  the survey details to be updated.
	 * @throws ProjectDaoException  if any exception occurs when performing this operation.
	 */
	@Override
	public void updateSurveyData(String databaseName, Survey survey) throws ProjectDaoException {
		logger.debug(".in update survey data in ProjectDaoImpl, survey is " + survey);
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.UPDATE_SURVEY_BY_SURVEY_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setLong(1, survey.getLastUpdate());
			prepareStatement.setInt(2, survey.getMaxScore());
			prepareStatement.setString(3, survey.getSurveyData());
			prepareStatement.setString(4, survey.getSurveyId());
			int executeUpdate = prepareStatement.executeUpdate();
			if (executeUpdate > 0) {
				logger.debug("survey updated successfully");
			}
		} catch (Exception e) {
			logger.error("unable to update survey " + e.getMessage());
			throw new ProjectDaoException("unable to update survey " + e.getMessage());
		} finally {
			logger.debug("closing the survey connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

	}

	/**
	 * This method is used to update ProjectData
	 * 
	 * @param databaseName the database name.
	 * @param project the project details to be updated.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateProjectData(String databaseName, ProjectData project) throws ProjectDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.UPDATE_PROJECT_BY_PROJECT_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, project.getProjectName());
			prepareStatement.setString(2, project.getProjectDescription());
			prepareStatement.setLong(3, project.getStartDate());
			prepareStatement.setLong(4, project.getEndDate());
			prepareStatement.setLong(5, project.getSelfAssignmentDeadline());
			prepareStatement.setString(6, project.getProjectId());
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " row of projectData updated successfully");
		} catch (Exception e) {
			logger.error("unable to update project " + e.getMessage());
			throw new ProjectDaoException("unable to update project " + e.getMessage());
		} finally {
			logger.debug("closing the project connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

	}

	/**
	 * this method is used to delete Project By Id
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param id the id of the project.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void deleteProjectById(Connection connection, String databaseName, String projectId)
			throws ProjectDaoException {
		logger.debug(".in deleteProjectById in ProjectDaoImpl class, projectId is " + projectId);
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.DELETE_PROJECT_BY_PROJECT_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count +" rows deleted from ProjectData");
		} catch (Exception e) {
			logger.error("unable to delete project " + e.getMessage());
			throw new ProjectDaoException("unable to delete project " + e.getMessage());
		} finally {
			logger.debug("closing the project connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to create ProjectData
	 * 
	 * @param databaseName the database name.
	 * @param project  project details to be created.
	 * @return the id of the project.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public String createProjectData(String databaseName, ProjectData project) throws ProjectDaoException {
		logger.debug(".in add project, project is: " + project);
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.INSERT_PROJECT
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			String projectId = UUID.randomUUID().toString();
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, project.getProjectName());
			prepareStatement.setString(3, project.getProjectDescription());
			prepareStatement.setLong(4, project.getStartDate());
			prepareStatement.setLong(5, project.getEndDate());
			prepareStatement.setLong(6, project.getSelfAssignmentDeadline());
			int executeUpdate = prepareStatement.executeUpdate();
			if (executeUpdate > 0) {
				logger.debug("project created successfully");
			}
			return projectId;
		} catch (Exception e) {
			logger.error("unable to add project " + e.getMessage());
			throw new ProjectDaoException("unable to add project " + e.getMessage());
		} finally {
			logger.debug("closing the project connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

	}

	/**
	 * This method is used to get Project By Id
	 * 
	 * @param databaseName the database name.
	 * @param id the id of the project.
	 * @return the project details 
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public ProjectData getProjectById(String databaseName, String id) throws ProjectDaoException {
		logger.debug(".in get project by id in ProjectDaoImpl, projectId is: " + id);
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.SELECT_PROJECT_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, id);

			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("project exist");
				ProjectData projectData = buildProjectByResultSet(resultSet);
				logger.debug("project is " + projectData);
				return projectData;
			}
		} catch (Exception e) {
			logger.error("unable to get  project " + e.getMessage());
			throw new ProjectDaoException("unable to get  project " + e.getMessage(), e);
		} finally {
			logger.debug("closing the project connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

	/**
	 * this method is used to get Project By Id
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName the database name.
	 * @param id the id of the project.
	 * @return the project details.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public ProjectData getProjectById(Connection connection, String databaseName, String id)
			throws ProjectDaoException {
		logger.debug(".in get project by id in ProjectDaoImpl, projectId is: " + id);
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.SELECT_PROJECT_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, id);

			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("project exist");
				ProjectData projectData = buildProjectByResultSet(resultSet);
				logger.debug("project is " + projectData);
				return projectData;
			}
		} catch (Exception e) {
			logger.error("unable to get  project " + e.getMessage());
			throw new ProjectDaoException("unable to get  project " + e.getMessage(), e);
		} finally {
			logger.debug("closing the project connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return null;
	}

	/**
	 * this method is used to delete Survey By Id
	 * 
	 * @param connection  if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param id the id of the survey
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void deleteSurveyById(Connection connection, String databaseName, String surveyId)
			throws ProjectDaoException {
		logger.debug(".in deleteSurveyById in ProjectDaoImpl class, surveyId is " + surveyId);
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.DELETE_SURVEY_BY_SURVEY_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			logger.debug("executing.....");
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " Survey row Deleted");
		} catch (Exception e) {
			logger.error("unable to delete survey " + e.getMessage());
			throw new ProjectDaoException("unable to delete survey " + e.getMessage());
		} finally {
			logger.debug("closing the project connections");
			MSSqlServerUtill.close(prepareStatement, null);

		}
	}

	/**
	 * This method is used to check whether the project is exit or not
	 * 
	 * @param databaseName the database name.
	 * @param id the id of the project.
	 * @return true if exist, false if not exists.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public boolean isProjectExist(String databaseName, String id) throws ProjectDaoException {
		logger.debug(".in isProjectExist in ProjectDaoImpl, project Id is: " + id + " databaseName ::" + databaseName);
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.SELECT_PROJECT_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, id);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("project exist");
				return true;
			}
		} catch (Exception e) {
			logger.error("unable to find project ::" + e.getMessage());
			throw new ProjectDaoException("unable to find project " + e.getMessage());
		} finally {
			logger.debug("closing the project connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return false;
	}

	/**
	 * this method is used to create FormData
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param formData the form details
	 * @return formId and surveyId in the form of map.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public Map<String, String> createFormData(Connection connection, String databaseName, FormData formData)
			throws ProjectDaoException {
		logger.debug(".in add FORM, form is: " + formData);
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.INSERT_FORM
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			String formId = UUID.randomUUID().toString();
			String surveyId = UUID.randomUUID().toString();
			prepareStatement.setString(1, formData.getProjectId());
			prepareStatement.setString(2, formId);
			prepareStatement.setString(3, formData.getFormName());
			prepareStatement.setLong(4, new Date().getTime());
			prepareStatement.setString(5, surveyId);
			String usersRoleString = formData.getUsersRolesAllowed().stream().collect(Collectors.joining(","));
			prepareStatement.setString(6, usersRoleString);
			prepareStatement.setBoolean(7, false);
			prepareStatement.executeUpdate();

			Map<String, String> idMap = new HashMap<>();
			idMap.put(ProjectDaoConstant.FORM_ID, formId.toString());
			idMap.put(ProjectDaoConstant.SURVEY_ID, surveyId.toString());

			connection.commit();
			return idMap;
		} catch (Exception e) {
			logger.error("unable to add form " + e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException e1) {
			}
			throw new ProjectDaoException("unable to add form " + e.getMessage());
		} finally {
			logger.debug("closing the form connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to update FormData
	 * 
	 * @param databaseName  the database name.
	 * @param formId the id of the form.
	 * @param formData the survey details to be updated.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateFormData(String databaseName, String formId, FormData formData) throws ProjectDaoException {
		logger.debug(".in update FORM, form is: " + formData);
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.UPDATE_FORM_BY_FORM_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, formData.getProjectId());
			prepareStatement.setString(2, formData.getFormName());
			String usersRoleString = formData.getUsersRolesAllowed().stream().collect(Collectors.joining(","));
			prepareStatement.setString(3, usersRoleString);
			prepareStatement.setString(4, formId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row got updated in formData table");

		} catch (Exception e) {
			logger.error("unable to update form " + e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException e1) {
			}
			throw new ProjectDaoException("unable to update form " + e.getMessage());
		} finally {
			logger.debug("closing the form connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * This method is used to get the Forms by the ProjectId
	 * 
	 * @param tenantId the survey details to be updated.
	 * @param projectId the id of the project.
	 * @return all the form details for the project.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<FormData> getFormsByProjectId(String databaseName, String projectId) throws ProjectDaoException {
		logger.debug(".in get form by ProjectId in ProjectDaoImpl, ");
		List<FormData> formList = new ArrayList<FormData>();
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.SELECT_FORM_BY_PROJECT_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				logger.debug("form exist");
				FormData formData = buildFormDataByResultSet(resultSet);
				formList.add(formData);
			}
		} catch (Exception e) {
			logger.error("unable to get form by ProjectId  " + e.getMessage());
			throw new ProjectDaoException("unable to get form by ProjectId  " + e.getMessage());
		} finally {
			logger.debug("closing the form connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return formList;
	}

	/**
	 * This method is used to get Forms By ProjectId
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param projectId the id of the project.
	 * @return all the form details for the project.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<FormData> getFormsByProjectId(Connection connection, String databaseName, String projectId)
			throws ProjectDaoException {
		logger.debug(".in get form by ProjectId in ProjectDaoImpl, ");
		List<FormData> formList = new ArrayList<FormData>();
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.SELECT_FORM_BY_PROJECT_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				logger.debug("form exist");
				FormData formData = buildFormDataByResultSet(resultSet);
				formList.add(formData);
			}
		} catch (Exception e) {
			logger.error("unable to get form by ProjectId  " + e.getMessage());
			throw new ProjectDaoException("unable to get form by ProjectId  " + e.getMessage());
		} finally {
			logger.debug("closing the form connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return formList;
	}

	/**
	 * This method is used to get Form By Id
	 * 
	 * @param databaseName  the database name.
	 * @param formId the id of the form.
	 * @return the id of the project.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public FormData getFormById(String databaseName, String formId) throws ProjectDaoException {
		logger.debug(".in get form by FormId in ProjectDaoImpl, form Id is " + formId);
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.SELECT_FORM_BY_FORM_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, formId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("form exist");
				FormData formData = buildFormDataByResultSet(resultSet);
				return formData;
			}
		} catch (Exception e) {
			logger.error("unable to get form by FormId  " + e.getMessage());
			throw new ProjectDaoException("unable to get form by FormId  " + e.getMessage());
		} finally {
			logger.debug("closing the form connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

	/**
	 * this method is used to get Form By Id
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName the database name.
	 * @param formId the id of the form.
	 * @return the form details.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public FormData getFormById(Connection connection, String databaseName, String formId) throws ProjectDaoException {
		logger.debug(".in get form by FormId in ProjectDaoImpl, form Id is " + formId);
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.SELECT_FORM_BY_FORM_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, formId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("form exist");
				FormData formData = buildFormDataByResultSet(resultSet);
				return formData;
			}
		} catch (Exception e) {
			logger.error("unable to get form by FormId  " + e.getMessage());
			throw new ProjectDaoException("unable to get form by FormId  " + e.getMessage());
		} finally {
			logger.debug("closing the form connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return null;
	}

	/**
	 * this method is used to delete Form By Id
	 * 
	 * @param connection  if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param formId the id of the form.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void deleteFormById(Connection connection, String databaseName, String formId) throws ProjectDaoException {
		logger.debug(".in deleteFormById in ProjectDaoImpl class, formId is " + formId);
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.DELETE_FORM_BY_FORM_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, formId);
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate+" rows deleted from  FormData table.");
		} catch (Exception e) {
			logger.error("unable to delete form " + e.getMessage());
			throw new ProjectDaoException("unable to delete form " + e.getMessage());
		} finally {
			logger.debug("closing the project connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to get all Forms
	 * 
	 * @param databaseName  the database name.
	 * @return all the form details.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<FormData> getAllForms(String databaseName) throws ProjectDaoException {
		logger.debug(".in get All forms in ProjectDaoImpl, ");
		List<FormData> formList = new ArrayList<FormData>();
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			statement = connection.prepareStatement(ProjectQueryConstans.SELECT_FORMS
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				logger.debug("form exist");
				FormData formData = buildFormDataByResultSet(resultSet);
				formList.add(formData);
			}
		} catch (Exception e) {
			logger.error("unable to get All forms  " + e.getMessage());
			throw new ProjectDaoException("unable to get All forms  " + e.getMessage());
		} finally {
			logger.debug("closing the form connections");
			MSSqlServerUtill.close(resultSet, statement, connection);
		}
		return formList;
	}

	/**
	 * This method is used to publish FormData By Id
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param formId the id of the form
	 * @param status the status to be updated.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void publishFormDataById(Connection connection, String databaseName, String formId, boolean status)
			throws ProjectDaoException {
		logger.debug(".in publishFormDataById, formId is: " + formId);
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.PUBLISH_FORM_BY_FORM_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setBoolean(1, status);
			prepareStatement.setString(2, formId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row got published in formData table");
		} catch (Exception e) {
			logger.error("unable to publish form " + e.getMessage());
			throw new ProjectDaoException("unable to publish form " + e.getMessage());
		} finally {
			logger.debug("closing the form connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}
	/**
	 * this method is used to publish SurveyData By Id
	 * 
	 * @param connection if this operation is to be performed in transaction.
	 * @param databaseName  the database name.
	 * @param surveyId the id of the survey
	 * @param status the status to be updated.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void publishSurveyDataById(Connection connection, String databaseName, String surveyId, boolean status)
			throws ProjectDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.PUBLISH_SURVEY_BY_SURVEY_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setBoolean(1, status);
			prepareStatement.setString(2, surveyId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row got published in survey Data");
		} catch (Exception e) {
			logger.error("unable to publish survey " + e.getMessage());
			throw new ProjectDaoException("unable to publish survey " + e.getMessage());
		} finally {
			logger.debug("closing the survey prepareStatement");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * this method is used to get Forms By SurveyId
	 * 
	 * @param databaseName  the database name.
	 * @param surveyId the id of the survey
	 * @return all the form details.
	 * @throws ProjectDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public FormData getFormsBySurveyId(String databaseName, String surveyId) throws ProjectDaoException {
		logger.debug(".in get form by surveyId in ProjectDaoImpl, ");
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(ProjectQueryConstans.SELECT_FORM_BY_SURVEY_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("form exist");
				FormData formData = buildFormDataByResultSet(resultSet);
				return formData;
			}

		} catch (Exception e) {
			logger.error("unable to get form by surveyId  " + e.getMessage());
			throw new ProjectDaoException("unable to get form by surveyId  " + e.getMessage());
		} finally {
			logger.debug("closing the form connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

	private ProjectData buildProjectByResultSet(ResultSet resultSet) throws SQLException {
		ProjectData project = new ProjectData();
		project.setProjectId(resultSet.getString(ProjectDaoConstant.PROJECT_ID));
		project.setProjectName(resultSet.getString(ProjectDaoConstant.PROJECT_NAME));
		project.setProjectDescription(resultSet.getString(ProjectDaoConstant.PROJECT_DESCRIPTION));
		project.setStartDate(resultSet.getLong(ProjectDaoConstant.START_DATE));
		project.setEndDate(resultSet.getLong(ProjectDaoConstant.END_DATE));
		project.setSelfAssignmentDeadline(resultSet.getLong(ProjectDaoConstant.SELF_ASSIGNMENT_DEAD_LINE));
		logger.debug("project in resultset is " + project);
		return project;
	}

	private Survey buildSurveyFromResultSet(ResultSet resultSet) throws SQLException {
		Survey survey = new Survey();
		survey.setSurveyId(resultSet.getString(ProjectDaoConstant.SURVEY_ID));
		survey.setLastUpdate(resultSet.getLong(ProjectDaoConstant.LAST_UPDATE));
		survey.setTime(resultSet.getLong(ProjectDaoConstant.TIME_OF_CREATE));
		survey.setMaxScore(resultSet.getInt(ProjectDaoConstant.MAX_SCORE));
		survey.setSurveyData(resultSet.getString(ProjectDaoConstant.SURVEY_DATA));
		survey.setPublish(resultSet.getBoolean(ProjectDaoConstant.IS_PUBLISHED));
		return survey;
	}

	private FormData buildFormDataByResultSet(ResultSet resultSet) throws SQLException {
		FormData formData = new FormData();
		formData.setProjectId(resultSet.getString(ProjectDaoConstant.PROJECT_ID));
		formData.setFormId(resultSet.getString(ProjectDaoConstant.FORM_ID));
		formData.setFormName(resultSet.getString(ProjectDaoConstant.FORM_NAME));
		formData.setCreatedTime(resultSet.getLong(ProjectDaoConstant.TIME_OF_CREATE));
		formData.setSurveyId(resultSet.getString(ProjectDaoConstant.SURVEY_ID));
		String userRoles = resultSet.getString(ProjectDaoConstant.USER_ROLES_ALLOWED);
		formData.setUsersRolesAllowed(Arrays.asList(userRoles.split(",")));
		formData.setPublish(resultSet.getBoolean(ProjectDaoConstant.IS_PUBLISHED));
		return formData;
	}
}
