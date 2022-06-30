package com.pwc.grading.tracking.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.geolocation.dao.GeoLocationQueryConstants;
import com.pwc.grading.project.dao.ProjectQueryConstans;
import com.pwc.grading.surveyresponse.dao.exception.SurveyResponseDaoException;
import com.pwc.grading.tracking.dao.ITrackingDao;
import com.pwc.grading.tracking.dao.TrackingQueryConstants;
import com.pwc.grading.tracking.model.UserSurveyResponseTracking;
import com.pwc.grading.usertoken.model.UserLogTracking;

/**
 * Implementation class for {@link ITrackingDao} 
 *
 */
@Singleton
public class TrackingDaoImpl implements ITrackingDao {
	private static final Logger logger = LoggerFactory.getLogger(TrackingDaoImpl.class);

	/**
	 * This method is used to add log tracking of the user.
	 * @param tenantId the database name.
	 * @param userTracking the user tracking details.
	 */
	@Override
	public void addLogTrackingOfUser(String tenantId, UserLogTracking userTracking) {
		Connection connection = null;
		PreparedStatement preparedStatment = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			preparedStatment = connection.prepareStatement(
					TrackingQueryConstants.INSERT_LOG.replace(TrackingQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			preparedStatment.setString(1, userTracking.getSessionId());
			preparedStatment.setString(2, userTracking.getUserId());
			preparedStatment.setLong(3, userTracking.getLoginTime());
			preparedStatment.setLong(4, userTracking.getLogOutTime());
			preparedStatment.executeUpdate();
		} catch (Exception e) {
			logger.error("unable to add the log data ::" + e.getMessage());
			e.printStackTrace();

		} finally {
			MSSqlServerUtill.close(preparedStatment, connection);
		}
	}

	/**
	 * This method is used to update log tracking of the user.
	 * @param tenanId the database name
	 * @param sessionId the session Id
	 * @param time the time to be updated.
	 */
	@Override
	public void updateLogTrackingBySessionId(String tenantId, String sessionId, long logOutTime) {
		Connection connection = null;
		PreparedStatement preparedStatment = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			preparedStatment = connection.prepareStatement(
					TrackingQueryConstants.UPDATE_LOG.replace(TrackingQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			preparedStatment.setLong(1, logOutTime);
			preparedStatment.setString(2, sessionId);
			preparedStatment.executeUpdate();
		} catch (Exception e) {
			logger.error("unable to update log data ::" + e.getMessage());
			e.printStackTrace();

		} finally {
			MSSqlServerUtill.close(preparedStatment, connection);
		}

	}

	/**
	 * This method is used to add survey response tracking of the user.
	 * @param tenantId the database name
	 * @param userSurveyResponse
	 */
	@Override
	public void addUserSurveryResponseTracking(String tenantId, UserSurveyResponseTracking userSurveyResponse) {
		Connection connection = null;
		PreparedStatement preparedStatment = null;
		try {
			int i = 0;
			connection = MSSqlServerUtill.getConnection();
			preparedStatment = connection.prepareStatement(TrackingQueryConstants.INSERT_SURVEY_RESPONSE_TRACKING
					.replace(TrackingQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			preparedStatment.setString(++i, userSurveyResponse.getProjectId());
			preparedStatment.setString(++i, userSurveyResponse.getSurveyId());

			preparedStatment.setString(++i, userSurveyResponse.getCenterId());
			preparedStatment.setString(++i, userSurveyResponse.getPartnerId());
			preparedStatment.setString(++i, userSurveyResponse.getAuditFor());
			preparedStatment.setString(++i, userSurveyResponse.getAuditId());

			preparedStatment.setDouble(++i, userSurveyResponse.getVaraince());
			preparedStatment.setString(++i, userSurveyResponse.getStartDate());
			preparedStatment.setString(++i, userSurveyResponse.getStartTime());
			preparedStatment.setString(++i, userSurveyResponse.getEndDate());
			preparedStatment.setString(++i, userSurveyResponse.getEndTime());

			preparedStatment.setString(++i,
					getGeoLocationString(userSurveyResponse.getLatitude(), userSurveyResponse.getLongitude()));
			preparedStatment.executeUpdate();
		} catch (Exception e) {
			logger.error("unable to add user surveyResponse Tracking data ::" + e.getMessage());
			e.printStackTrace();
		} finally {
			MSSqlServerUtill.close(preparedStatment, connection);
		}
	}

	private String getGeoLocationString(double latitude, double longitude) throws SQLServerException {
		Geography geography = Geography.point(latitude, longitude, GeoLocationQueryConstants.SRID_FOR_GEOGRAPHY);
		return geography.toString();
	}
//	@Override
//	public void getUserSurveryResponseTracking(String tenantId) {
//		Connection connection = null;
//		PreparedStatement preparedStatment = null;
//		try {
//			int i = 0;
//			connection = MSSqlServerUtill.getConnection();
//			preparedStatment = connection.prepareStatement(TrackingQueryConstants.INSERT_SURVEY_RESPONSE_TRACKING
//					.replace(TrackingQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
//			preparedStatment.setString(++i, userSurveyResponse.getProjectId());
//			preparedStatment.setString(++i, userSurveyResponse.getSurveyId());
//
//			preparedStatment.setString(++i, userSurveyResponse.getCenterId());
//			preparedStatment.setString(++i, userSurveyResponse.getPartnerId());
//			preparedStatment.setString(++i, userSurveyResponse.getAuditFor());
//			preparedStatment.setString(++i, userSurveyResponse.getAuditId());
//
//			preparedStatment.setDouble(++i, userSurveyResponse.getVaraince());
//			preparedStatment.setString(++i, userSurveyResponse.getStartDate());
//			preparedStatment.setString(++i, userSurveyResponse.getStartTime());
//			preparedStatment.setString(++i, userSurveyResponse.getEndDate());
//			preparedStatment.setString(++i, userSurveyResponse.getEndTime());
//			preparedStatment.executeUpdate();
//		} catch (Exception e) {
//			logger.error("unable to add user surveyResponse Tracking data ::" + e.getMessage());
//			e.printStackTrace();
//
//		} finally {
//			MSSqlServerUtill.close(preparedStatment, connection);
//		}
//
//	}

	/**
	 * This method is used to get SurveyResponse Tracking for training center.
	 * @param tenantId the database name
	 * @param endDate the end date to be updated
	 * @param endTime the endTime to be updated
	 * @param surveyId the id of the survey
	 * @param centerId  training center Id 
	 */
	@Override
	public void updateUserSurveryResponseTrackingForTrainingCenter(String tenantId, String endDate, String endTime,
			String surveyId, String centerId) {
		Connection connection = null;
		PreparedStatement preparedStatment = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			preparedStatment = connection
					.prepareStatement(TrackingQueryConstants.UPDATE_SURVEY_RESPONSE_TRACKING_FOR_TRAINING_CENTER
							.replace(TrackingQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			preparedStatment.setString(1, endDate);
			preparedStatment.setString(2, endTime);
			preparedStatment.setString(3, surveyId);
			preparedStatment.setString(4, centerId);
			preparedStatment.executeUpdate();
		} catch (Exception e) {
			logger.error("unable to update user surveyResponse Tracking data ::" + e.getMessage());
			e.printStackTrace();
		} finally {
			MSSqlServerUtill.close(preparedStatment, connection);
		}

	}

	/**
	 * This method is used to get SurveyResponse Tracking for Partner.
	 * @param tenantId the database name
	 * @param endDate the end date to be updated
	 * @param endTime the endTime to be updated
	 * @param surveyId the id of the survey
	 * @param partnerId the id of the partner
	 */
	@Override
	public void updateUserSurveryResponseTrackingForPartner(String tenantId, String endDate, String endTime,
			String surveyId, String partnerId) {
		Connection connection = null;
		PreparedStatement preparedStatment = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			preparedStatment = connection
					.prepareStatement(TrackingQueryConstants.UPDATE_SURVEY_RESPONSE_TRACKING_FOR_PARTNER
							.replace(TrackingQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			preparedStatment.setString(1, endDate);
			preparedStatment.setString(2, endTime);
			preparedStatment.setString(3, surveyId);
			preparedStatment.setString(4, partnerId);
			preparedStatment.executeUpdate();
		} catch (Exception e) {
			logger.error("unable to update user surveyResponse Tracking data ::" + e.getMessage());
			e.printStackTrace();
		} finally {
			MSSqlServerUtill.close(preparedStatment, connection);
		}

	}

	/**
	 *  This method is used to get SurveyResponse Tracking for FieldAuditor.
	 * @param tenantId the database name
	 * @param endDate the end date to be updated
	 * @param endTime the endTime to be updated
	 * @param surveyId the id of the survey
	 * @param auditFor the audit for training center or partner.
	 * @param auditForId the audit for training center Id or partnerId.
	 */
	@Override
	public void updateUserSurveryResponseTrackingForFieldAuditor(String tenantId, String endDate, String endTime,
			String surveyId, String auditFor, String auditForId) {
		Connection connection = null;
		PreparedStatement preparedStatment = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			preparedStatment = connection
					.prepareStatement(TrackingQueryConstants.UPDATE_SURVEY_RESPONSE_TRACKING_FOR_FIELD_AUDITOR
							.replace(TrackingQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			preparedStatment.setString(1, endDate);
			preparedStatment.setString(2, endTime);
			preparedStatment.setString(3, surveyId);
			preparedStatment.setString(4, auditFor);
			preparedStatment.setString(5, auditForId);
			preparedStatment.executeUpdate();
		} catch (Exception e) {
			logger.error("unable to update user surveyResponse Tracking data ::" + e.getMessage());
			e.printStackTrace();
		} finally {
			MSSqlServerUtill.close(preparedStatment, connection);
		}

	}

	/**
	 * This method is used to get SurveyResponse Tracking With SurveyId and AuditFor Id.
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName  the database name
	 * @param surveyId the id of the survey
	 * @param auditFor the audit for training center or partner.
	 * @param auditForId the audit for training center Id or partnerId.
	 * @return the User Survey Response Tracking details
	 */
	@Override
	public UserSurveyResponseTracking getSurveyResponseTrackingWithSurveyIdandAuditForId(Connection connection,
			String databaseName, String surveyId, String auditFor, String auditForId) {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(
					TrackingQueryConstants.SELECT_USER_SURVEYRESPONSE_TRACKING_BY_AUDIT_ID_AND_SURVEY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, auditFor);
			prepareStatement.setString(3, auditForId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				UserSurveyResponseTracking surveyResponse = buildUserSurveyResponseTrackingByResultSet(resultSet);
				surveyResponse.setLatitude(resultSet.getDouble(12));
				surveyResponse.setLongitude(resultSet.getDouble(13));
				return surveyResponse;
			}

		} catch (Exception e) {
			logger.error("unable to get SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return null;

	}

	private UserSurveyResponseTracking buildUserSurveyResponseTrackingByResultSet(ResultSet resultSet)
			throws SQLException {
		UserSurveyResponseTracking usr = new UserSurveyResponseTracking();
		usr.setProjectId(resultSet.getString(1));
		usr.setSurveyId(resultSet.getString(2));
		usr.setCenterId(resultSet.getString(3));
		usr.setPartnerId(resultSet.getString(4));
		usr.setAuditFor(resultSet.getString(5));
		usr.setAuditId(resultSet.getString(6));
		usr.setVaraince(resultSet.getDouble(7));
		usr.setStartDate(resultSet.getString(8));
		usr.setStartTime(resultSet.getString(9));
		usr.setEndDate(resultSet.getString(10));
		usr.setEndTime(resultSet.getString(11));
		return usr;
	}

	/**
	 * This method is used to get SurveyResponse Tracking With SurveyId and CenterId.
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName  the database name
	 * @param surveyId the id of the survey
	 * @param centerId training center Id 
	 * @return the User Survey Response Tracking details
	 */
	@Override
	public UserSurveyResponseTracking getSurveyResponseTrackingWithSurveyIdandCenterId(Connection connection,
			String databaseName, String surveyId, String centerId) {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(
					TrackingQueryConstants.SELECT_USER_SURVEYRESPONSE_TRACKING_BY_CENTER_ID_AND_SURVEY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, centerId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				UserSurveyResponseTracking surveyResponse = buildUserSurveyResponseTrackingByResultSet(resultSet);
				return surveyResponse;
			}

		} catch (Exception e) {
			logger.error("unable to get SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return null;
	}

	/**
	 * This method is used to get SurveyResponse Tracking With SurveyId and PartnerId.
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName  the database name
	 * @param surveyId the id of the survey
	 * @param partnerId  the id of the partner
	 * @return the User Survey Response Tracking details
	 */
	@Override
	public UserSurveyResponseTracking getSurveyResponseTrackingWithSurveyIdandPartnerId(Connection connection,
			String databaseName, String surveyId, String partnerId) {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(
					TrackingQueryConstants.SELECT_USER_SURVEYRESPONSE_TRACKING_BY_PARTNER_ID_AND_SURVEY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, partnerId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				UserSurveyResponseTracking surveyResponse = buildUserSurveyResponseTrackingByResultSet(resultSet);
				return surveyResponse;
			}

		} catch (Exception e) {
			logger.error("unable to get SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return null;
	}

	/**
	 * This method is used to delete log tracking of the user.
	 * @param connection if this operation to be performed in single transaction.
	 * @param tenantId the database name
	 * @param userId the id of the user.
	 */
	@Override
	public void deleteLogTrackingByUserId(Connection connection, String tenantId, String userId) {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(TrackingQueryConstants.DELETE_LOG_TRACKING_BY_USER_ID
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, userId);
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " row deleted");
		} catch (Exception e) {
			logger.error("unable to delete tracking of user " + e.getMessage());
		} finally {
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to delete SurveyResponse Tracking With SurveyId and CenterId. 
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName  the database name
	 * @param centerId training center Id 
	 * @param surveyId the id of the survey
	 * @throws SurveyResponseDaoException if any exception occurs.
	 */
	@Override
	public void deleteSurveyResponsesTrackingByCenterIdAndSurveyId(Connection connection, String databaseName,
			String centerId, String surveyId) throws SurveyResponseDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(
					TrackingQueryConstants.SELECT_USER_SURVEYRESPONSE_TRACKING_BY_CENTER_ID_AND_SURVEY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, centerId);
			prepareStatement.executeUpdate();

		} catch (Exception e) {
			logger.error("unable to delete SurveyResponseTrackingData " + e.getMessage());
		} finally {
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to get SurveyResponse Tracking With SurveyId and AuditForId.
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey
	 * @param auditFor the audit for training center or partner.
	 * @param auditForId the audit for training center Id or partnerId.
	 * @throws SurveyResponseDaoException if any exception occurs.
	 */
	@Override
	public void deleteSurveyResponsesTrackingBySurveyIdAndAuditForId(Connection connection, String databaseName,
			String surveyId, String auditFor, String auditForId) throws SurveyResponseDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(TrackingQueryConstants.DELETE_SURVEY_RESPONSE_BY_AUDIT_FOR_ID_ID_AND_SURVERY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, auditFor);
			prepareStatement.setString(3, auditForId);
			prepareStatement.executeUpdate();

		} catch (Exception e) {
			logger.error("unable to delete SurveyResponseTrackingData " + e.getMessage());
		} finally {
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to delete SurveyResponse Tracking With SurveyId and PartnerId.
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey
	 * @param partnerId the id of the partner
	 * @throws SurveyResponseDaoException if any exception occurs.
	 */
	@Override
	public void deleteSurveyResponsesTrackingByPartnerIdAndSurveyId(Connection connection, String databaseName,
			String surveyId, String partnerId) throws SurveyResponseDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(
					TrackingQueryConstants.DELETE_USER_SURVEYRESPONSE_TRACKING_BY_PARTNER_ID_AND_SURVEY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, partnerId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row deleted in surveyTracking");
		} catch (Exception e) {
			logger.error("unable to delete SurveyResponseTrackingData " + e.getMessage());
		} finally {
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	
	/**
	 * This method is used to delete SurveyResponse Tracking With SurveyId.
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey
	 * @param partnerId the id of the partner
	 * @throws SurveyResponseDaoException if any exception occurs.
	 */
	@Override
	public void deleteSurveyResponsesTrackingBySurveyId(Connection connection, String databaseName,
			String surveyId) throws SurveyResponseDaoException {
		logger.debug(".inside deleteSurveyResponsesTrackingBySurveyId in TrackingDaoImpl...");
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(
					TrackingQueryConstants.DELETE_USER_SURVEYRESPONSE_TRACKING_BY_SURVEY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " rows deleted in SurveyTracking");
		} catch (Exception e) {
			logger.error("Unable to delete SurveyResponseTrackingData " + e.getMessage());
		} finally {
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}
//	deleteSurveyResponsesTrackingBySurveyId
}
