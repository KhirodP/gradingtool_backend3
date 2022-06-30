package com.pwc.grading.surveyresponse.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.project.dao.ProjectQueryConstans;
import com.pwc.grading.surveyresponse.dao.ISurveyResponseDao;
import com.pwc.grading.surveyresponse.dao.SurveyResponseDaoConstant;
import com.pwc.grading.surveyresponse.dao.SurveyResponseQueryConstants;
import com.pwc.grading.surveyresponse.dao.exception.SurveyResponseDaoException;
import com.pwc.grading.surveyresponse.model.SurveyResponse;

/**
 * Implementation class for {@link ISurveyResponseDao} 
 *
 */
@Singleton
public class SurveyResponseMongodbImpl implements ISurveyResponseDao {

	private static final Logger logger = LoggerFactory.getLogger(SurveyResponseMongodbImpl.class);

	/**
	 * This method is used to add the survey response data. 
	 * @param databaseName the database name
	 * @param surveyResponse details of survey response.
	 * @return the id of the survey response.
	 * @throws SurveyResponseDaoException  if any exception occurs while performing this operation.
	 */
	@Override
	public String addSurveyResponseData(String databaseName, SurveyResponse surveyResponse)
			throws SurveyResponseDaoException {
		logger.debug(".in add SurveyResponseData, SurveyResponse is: " + surveyResponse);
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(SurveyResponseQueryConstants.INSERT_SURVEY_RESPONSE
					.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			String surveyResponseId = UUID.randomUUID().toString();
			prepareStatement.setString(1, surveyResponseId);
			prepareStatement.setString(2, surveyResponse.getSurveyId());
			prepareStatement.setString(3, surveyResponse.getUserId());
			prepareStatement.setLong(4, surveyResponse.getSaveTime());
			prepareStatement.setLong(5, surveyResponse.getSubmitTime());
			prepareStatement.setString(6, surveyResponse.getResponderType());
			prepareStatement.setInt(7, surveyResponse.getTotalScore());
			prepareStatement.setBoolean(8, surveyResponse.isSubmited());
			prepareStatement.setString(9, surveyResponse.getCenterId());
			prepareStatement.setString(10, surveyResponse.getPartnerId());
			prepareStatement.setString(11, surveyResponse.getAuditFor());
			prepareStatement.setString(12, surveyResponse.getAuditForId());
			prepareStatement.setInt(13, surveyResponse.getMaxMarks());
			prepareStatement.setString(14, surveyResponse.getSurveyResponseData());
			prepareStatement.executeUpdate();
			return surveyResponseId;
		} catch (Exception e) {
			logger.error("unable to add SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to add SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Suvey Response connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

	}

	/**
	 * This method is used to update the survey response data. 
	 * @param connection if this operation is to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param surveyResponse details of survey response.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public void updateSurveyResponseById(Connection connection, String databaseName, SurveyResponse surveyResponse)
			throws SurveyResponseDaoException {
		logger.debug(".in update SurveyResponseData, SurveyResponse is: " + surveyResponse);
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(SurveyResponseQueryConstants.UPDATE_SURVEY_RESPONSE_BY_SURVEY_RESPONSE_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));

			prepareStatement.setString(1, surveyResponse.getSurveyId());
			prepareStatement.setString(2, surveyResponse.getUserId());
			prepareStatement.setLong(3, surveyResponse.getSaveTime());
			prepareStatement.setLong(4, surveyResponse.getSubmitTime());
			prepareStatement.setString(5, surveyResponse.getResponderType());
			prepareStatement.setInt(6, surveyResponse.getTotalScore());
			prepareStatement.setBoolean(7, surveyResponse.isSubmited());

			prepareStatement.setString(8, surveyResponse.getCenterId());
			prepareStatement.setString(9, surveyResponse.getPartnerId());

			prepareStatement.setString(10, surveyResponse.getAuditFor());
			prepareStatement.setString(11, surveyResponse.getAuditForId());
			prepareStatement.setInt(12, surveyResponse.getMaxMarks());
			prepareStatement.setString(13, surveyResponse.getSurveyResponseData());
			prepareStatement.setString(14, surveyResponse.getSurveyResponseId());
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " rows updated");
		} catch (Exception e) {
			logger.error("unable to update SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to update SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to delete the survey response data. 
	 * @param databaseName the database name
	 * @param surveyResponseById  the id of the survey response.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public void deleteSurveyResponseById(String databaseName, String surveyResponseId)
			throws SurveyResponseDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SurveyResponseQueryConstants.DELETE_SURVEY_RESPONSE_BY_SURVEY_RESPONSE_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyResponseId);
			prepareStatement.executeUpdate();
		} catch (Exception e) {
			logger.error("unable to delete SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to delete SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

	}

	/**
	 * This method is used to get the survey response data for the given surveyId.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @return all the surveyResponse details for given surveyId.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public List<SurveyResponse> getSurveyResponsesBySurveyId(String databaseName, String surveyId)
			throws SurveyResponseDaoException {
		List<SurveyResponse> surveyResponseList = new ArrayList<SurveyResponse>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_SURVEY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			ResultSet resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				SurveyResponse surveyResponse = buildSurveyResponseByResultSet(resultSet);
				surveyResponseList.add(surveyResponse);
			}
		} catch (Exception e) {
			logger.error("unable to get SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to get SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

		return surveyResponseList;
	}
	
	@Override
	public List<SurveyResponse> getSurveyResponsesBySurveyId(Connection connection,String databaseName, String surveyId)
			throws SurveyResponseDaoException {
		List<SurveyResponse> surveyResponseList = new ArrayList<SurveyResponse>();
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_SURVEY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			ResultSet resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				SurveyResponse surveyResponse = buildSurveyResponseByResultSet(resultSet);
				surveyResponseList.add(surveyResponse);
			}
		} catch (Exception e) {
			logger.error("unable to get SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to get SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response prepareStatement");
			MSSqlServerUtill.close(prepareStatement, null);
		}

		return surveyResponseList;
	}


	private SurveyResponse buildSurveyResponseByResultSet(ResultSet resultSet) throws SQLException {
		SurveyResponse surveyResponse = new SurveyResponse();

		surveyResponse.setSurveyResponseId(resultSet.getString(SurveyResponseDaoConstant.SURVEY_RESPONSE_ID));
		logger.debug("setSurveyResponseId");
		surveyResponse.setSurveyId(resultSet.getString(SurveyResponseDaoConstant.SURVEY_ID));
		logger.debug("setSurveyId");
		surveyResponse.setUserId(resultSet.getString(SurveyResponseDaoConstant.USER_ID));
		logger.debug("setUserId");
		surveyResponse.setSaveTime(resultSet.getLong(SurveyResponseDaoConstant.SAVE_TIME));
		surveyResponse.setSubmitTime(resultSet.getLong(SurveyResponseDaoConstant.SUBMIT_TIME));
		surveyResponse.setResponderType(resultSet.getString(SurveyResponseDaoConstant.RESPONDER_TYPE));
		surveyResponse.setTotalScore(resultSet.getInt(SurveyResponseDaoConstant.TOTAL_SCORE));
		surveyResponse.setSubmited(resultSet.getBoolean(SurveyResponseDaoConstant.IS_SUBMITED));
		surveyResponse.setAuditFor(resultSet.getString(SurveyResponseDaoConstant.AUDIT_FOR));
		surveyResponse.setAuditForId(resultSet.getString(SurveyResponseDaoConstant.AUDIT_FOR_ID));
		surveyResponse.setCenterId(resultSet.getString(SurveyResponseDaoConstant.CENTER_ID));
		surveyResponse.setPartnerId(resultSet.getString(SurveyResponseDaoConstant.PARTNER_ID));
		surveyResponse.setMaxMarks(resultSet.getInt(SurveyResponseDaoConstant.MAX_MARKS));
		surveyResponse.setSurveyResponseData(resultSet.getString(SurveyResponseDaoConstant.SURVEY_RESPONSE_DATA));
		return surveyResponse;
	}

	/**
	 * This method is used to get the survey response data for the given surveyResponseId.
	 * @param databaseName the database name
	 * @param surveyResponseId the id of the survey response.
	 * @return the surveyResponse details for given surveyResponseId.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public SurveyResponse getSurveyResponseBySurveyResponseId(String databaseName, String surveyResponseId)
			throws SurveyResponseDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_SURVEY_RESPONSE_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyResponseId);
			ResultSet resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("surveyResponse exist");
				SurveyResponse surveyResponse = buildSurveyResponseByResultSet(resultSet);
				return surveyResponse;
			}
		} catch (Exception e) {
			logger.error("unable to get SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to get SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

		return null;
	}

//	@Override
//	public SurveyResponse getSurveyResponsesBySurveyIdAndUserId(String databaseName, String surveyId, String userId)
//			throws SurveyResponseDaoException {
//		logger.debug(".in getSurveyResponsesBySurveyIdAndUserId, dataBaseName: " + databaseName + " surveyId: "
//				+ surveyId + " userId: " + userId);
//		Connection connection = null;
//		PreparedStatement prepareStatement = null;
//		ResultSet resultSet = null;
//		try {
//			connection = MSSqlServerUtill.getConnection();
//			prepareStatement = connection
//					.prepareStatement(SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_SURVEY_ID_AND_USER_ID
//							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
//			prepareStatement.setString(1, surveyId);
//			prepareStatement.setString(2, userId);
//			resultSet = prepareStatement.executeQuery();
//			if (resultSet.next()) {
//				SurveyResponse surveyResponse = buildSurveyResponseByResultSet(resultSet);
//				return surveyResponse;
//			}
//		} catch (Exception e) {
//			logger.error("unable to get SurveyResponseData " + e.getMessage());
//			throw new SurveyResponseDaoException("unable to get SurveyResponseData " + e.getMessage());
//		} finally {
//			logger.debug("closing the Survey response connections");
//			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
//		}
//
//		return null;
//	}

	/**
	 * This method is used to get the survey response data for the given userId.
	 * @param databaseName the database name
	 * @param userId the id of the user.
	 * @return the surveyResponse details for given userId.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public List<SurveyResponse> getSurveyResponsesByUserId(String databaseName, String userId)
			throws SurveyResponseDaoException {
		// TODO Auto-generated method stub
		return null;
	}

//	private boolean isSurveyResponseExist(String databaseName, String surveyResponseId)
//			throws SurveyResponseDaoException {
//		Connection connection = null;
//		PreparedStatement prepareStatement = null;
//		try {
//			connection = MSSqlServerUtill.getConnection();
//			prepareStatement = connection
//					.prepareStatement(SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_SURVEY_RESPONSE_ID
//							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
//			prepareStatement.setString(1, surveyResponseId);
//			ResultSet resultSet = prepareStatement.executeQuery();
//			if (resultSet.next()) {
//				return true;
//			}
//		} catch (Exception e) {
//			logger.error("unable to find SurveyResponseData " + e.getMessage());
//			throw new SurveyResponseDaoException("unable to find SurveyResponseData " + e.getMessage());
//		} finally {
//			logger.debug("closing the Survey response connections");
//			MSSqlServerUtill.close(prepareStatement, connection);
//		}
//
//		return false;
//	}

	/**
	 * This method is used to delete all the survey response data for the given surveyId.
	 * @param connection if this operation is to be performed in a single transaction.
	 * @param tenantId the database name
	 * @param surveyId the id of the survey 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public void deleteSurveyResponseBySurveyId(Connection connection, String databaseName, String surveyId)
			throws SurveyResponseDaoException {
		logger.debug("in delete SurveyResponse By SurveyId surveyId is " + surveyId);
		PreparedStatement prepareStatement = null;
		boolean isParentConnection = true;
		try {
			if (connection == null) {
				logger.debug("connection is not parent connection");
				connection = MSSqlServerUtill.getConnection();
				isParentConnection = false;
			}
			prepareStatement = connection
					.prepareStatement(SurveyResponseQueryConstants.DELETE_SURVEY_RESPONSE_BY_SURVEY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count+" Survey responses deleted.");
		} catch (Exception e) {
			logger.error("unable to delete SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to delete SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			if (isParentConnection) {
				logger.debug("Conneciton is parent so not closing the connection , closing the preparedStatment");
				MSSqlServerUtill.close(prepareStatement, null);
			} else {
				logger.debug("closing the local connection and local preparedStatment");
				MSSqlServerUtill.close(prepareStatement, connection);
			}

		}

	}

//	@Override
//	public List<SurveyResponse> getSurveyResponsesByTrainingCenterIdAndSurveyId(String databaseName, String tcId,
//			String surveyId) throws SurveyResponseDaoException {
//		Connection connection = null;
//		PreparedStatement prepareStatement = null;
//		ResultSet resultSet = null;
//		List<SurveyResponse> surveyResponses = new ArrayList<SurveyResponse>();
//		try {
//			connection = MSSqlServerUtill.getConnection();
//			prepareStatement = connection
//					.prepareStatement(SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_AUDIT_ID_AND_SURVERY_ID
//							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
//			prepareStatement.setString(1, tcId);
//			prepareStatement.setString(2, surveyId);
//			prepareStatement.setString(3, SurveyResponseQueryConstants.AUDIT_FOR_TRANING_CENTER);
//			resultSet = prepareStatement.executeQuery();
//			while (resultSet.next()) {
//				SurveyResponse surveyResponse = buildSurveyResponseByResultSet(resultSet);
//				surveyResponses.add(surveyResponse);
//			}
//			return surveyResponses;
//		} catch (Exception e) {
//			logger.error("unable to get SurveyResponseData " + e.getMessage());
//			throw new SurveyResponseDaoException("unable to get SurveyResponseData " + e.getMessage());
//		} finally {
//			logger.debug("closing the Survey response connections");
//			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
//		}
//	}

//	@Override
//	public List<SurveyResponse> getSurveyResponsesByTrainingCenterIdAndSurveyId(String databaseName, String tcId,
//			String surveyId) throws SurveyResponseDaoException {
//		Connection connection = null;
//		PreparedStatement prepareStatement = null;
//		ResultSet resultSet = null;
//		List<SurveyResponse> surveyResponses = new ArrayList<SurveyResponse>();
//		try {
//			connection = MSSqlServerUtill.getConnection();
//			prepareStatement = connection
//					.prepareStatement(SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_AUDIT_ID_AND_SURVERY_ID
//							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
//			prepareStatement.setString(1, surveyId);
//			prepareStatement.setString(2, tcId);
//
//			resultSet = prepareStatement.executeQuery();
//			while (resultSet.next()) {
//				SurveyResponse surveyResponse = buildSurveyResponseByResultSet(resultSet);
//				surveyResponses.add(surveyResponse);
//			}
//			return surveyResponses;
//		} catch (Exception e) {
//			logger.error("unable to get SurveyResponseData " + e.getMessage());
//			throw new SurveyResponseDaoException("unable to get SurveyResponseData " + e.getMessage());
//		} finally {
//			logger.debug("closing the Survey response connections");
//			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
//		}
//	}

	/**
	 * This method is used to get the survey response data for the given training center Id
	 * and the given surveyId.
	 * @param databaseName the database name
	 * @param tcId the id of the training center.
	 * @param surveyId the id of the survey 
	 * @return the surveyResponse details 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public SurveyResponse getSurveyResponsesByCenterIdAndSurveyId(String databaseName, String centerId, String surveyId)
			throws SurveyResponseDaoException {
		logger.debug("getSurveyResponsesByCenterIdAndSurveyId centerId:: " + centerId + " surveyId: " + surveyId);
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_CENTER_ID_AND_SURVERY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, centerId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				SurveyResponse surveyResponse = buildSurveyResponseByResultSet(resultSet);
				return surveyResponse;
			}
			logger.debug("response not found");
		} catch (Exception e) {
			logger.error("unable to get SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to get SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

	/**
	 * This method is used to get the survey response data for the given training center Id
	 * and the given surveyId.
	 * @param connection if this is to be performed in transaction.
	 * @param databaseName the database name
	 * @param tcId the id of the training center.
	 * @param surveyId the id of the survey 
	 * @return the surveyResponse details 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public SurveyResponse getSurveyResponsesByCenterIdAndSurveyId(Connection connection,String databaseName, String centerId, String surveyId)
			throws SurveyResponseDaoException {
		logger.debug("getSurveyResponsesByCenterIdAndSurveyId centerId:: " + centerId + " surveyId: " + surveyId);
//		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
//			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_CENTER_ID_AND_SURVERY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, centerId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				SurveyResponse surveyResponse = buildSurveyResponseByResultSet(resultSet);
				return surveyResponse;
			}
			logger.debug("response not found");
		} catch (Exception e) {
			logger.error("unable to get SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to get SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response resultSet,prepareStatement");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return null;
	}
	
	/**
	 * This method is used to delete the survey response data for the given training center id and surveyId.
	 * @param connection  if this operation is to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param centerId the id of the training center.
	 * @param surveyId the id of the survey.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public void deleteSurveyResponsesByCenterIdAndSurveyId(Connection connection, String databaseName, String centerId,
			String surveyId) throws SurveyResponseDaoException {
		logger.debug(
				".in deleteSurveyResponsesByCenterIdAndSurveyId centerId:: " + centerId + " surveyId: " + surveyId);
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(SurveyResponseQueryConstants.DELETE_SURVEY_RESPONSE_BY_CENTER_ID_AND_SURVERY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, centerId);
			int count = prepareStatement.executeUpdate();

			logger.debug(count + " row deleted in surveyResponse");
		} catch (Exception e) {
			logger.error("unable to delete SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to delete SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to delete the survey response data for the given partnerId aand surveyId.
	 * @param connection if this operation is to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @param partnerId the id of the partner 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public void deleteSurveyResponsesByPartnerIdAndSurveyId(Connection connection, String databaseName, String surveyId,
			String partnerId) throws SurveyResponseDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(SurveyResponseQueryConstants.DELETE_SURVEY_RESPONSE_BY_PARTNER_ID_AND_SURVERY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, partnerId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " rows deleted in surveyResponses");
		} catch (Exception e) {
			logger.error("unable to delete SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to delete SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to get the survey response data for the given partnerId and the given surveyId.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @param partnerId the id of the partner.
	 * @return the surveyResponse details 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public SurveyResponse getSurveyResponsesByPartnerIdAndSurveyId(String databaseName, String surveyId,
			String partnerId) throws SurveyResponseDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_PARTNER_ID_AND_SURVERY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, partnerId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				SurveyResponse surveyResponse = buildSurveyResponseByResultSet(resultSet);
				return surveyResponse;
			}

		} catch (Exception e) {
			logger.error("unable to get SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to get SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}
	
	/**
	 * This method is used to get the survey response data for the given partnerId and the given surveyId.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @param partnerId the id of the partner.
	 * @return the surveyResponse details 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public SurveyResponse getSurveyResponsesByPartnerIdAndSurveyId(Connection connection,String databaseName, String surveyId,
			String partnerId) throws SurveyResponseDaoException {
//		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
//			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_PARTNER_ID_AND_SURVERY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, partnerId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				SurveyResponse surveyResponse = buildSurveyResponseByResultSet(resultSet);
				return surveyResponse;
			}

		} catch (Exception e) {
			logger.error("unable to get SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to get SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return null;
	}

	/**
	 * This method is used to get the survey response data for the given surveyId and auditForId.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @param auditFor the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @return the surveyResponse details 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public SurveyResponse getSurveyResponsesBySurveyIdAndAuditForId(String databaseName, String surveyId,
			String auditFor, String auditForId) throws SurveyResponseDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(
					SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_AUDIT_FOR_ID_ID_AND_SURVERY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, auditFor);
			prepareStatement.setString(3, auditForId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				SurveyResponse surveyResponse = buildSurveyResponseByResultSet(resultSet);
				logger.debug("------------------------------");
				logger.debug(surveyResponse.toString());
				logger.debug("------------------------------");
				return surveyResponse;
			}

		} catch (Exception e) {
			logger.error("unable to get SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to get SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

	/**
	 * This method is used to get the survey response data for the given surveyId and auditForId.
	 * @param connection if this operation is to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @param auditFor the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @return the surveyResponse details 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public SurveyResponse getSurveyResponsesBySurveyIdAndAuditForId(Connection connection, String databaseName,
			String surveyId, String auditFor, String auditForId) throws SurveyResponseDaoException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(
					SurveyResponseQueryConstants.SELECT_SURVEY_RESPONSE_BY_AUDIT_FOR_ID_ID_AND_SURVERY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, auditFor);
			prepareStatement.setString(3, auditForId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				SurveyResponse surveyResponse = buildSurveyResponseByResultSet(resultSet);
				logger.debug("------------------------------");
				logger.debug(surveyResponse.toString());
				logger.debug("------------------------------");
				return surveyResponse;
			}

		} catch (Exception e) {
			logger.error("unable to get SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to get SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return null;
	}

	/**
	 * This method is used to delete the survey response data for the given audit for id and surveyId.
	 * @param connection  if this operation is to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @param auditFor the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public void deleteSurveyResponsesBySurveyIdAndAuditForId(Connection connection, String databaseName,
			String surveyId, String auditFor, String auditForId) throws SurveyResponseDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(
					SurveyResponseQueryConstants.DELETE_SURVEY_RESPONSE_BY_AUDIT_FOR_ID_ID_AND_SURVERY_ID
							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, surveyId);
			prepareStatement.setString(2, auditFor);
			prepareStatement.setString(3, auditForId);
			prepareStatement.executeUpdate();

		} catch (Exception e) {
			logger.error("unable to delete SurveyResponseData " + e.getMessage());
			throw new SurveyResponseDaoException("unable to delete SurveyResponseData " + e.getMessage());
		} finally {
			logger.debug("closing the Survey response connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

//	@Override
//	public void deleteSurveyResponseByTcId(Connection connection, String databaseName, String )
//			throws SurveyResponseDaoException {
//		Connection connection = null;
//		PreparedStatement prepareStatement = null;
//		try {
//			connection = MSSqlServerUtill.getConnection();
//			prepareStatement = connection
//					.prepareStatement(SurveyResponseQueryConstants.DELETE_SURVEY_RESPONSE_BY_SURVEY_RESPONSE_ID
//							.replace(ProjectQueryConstans.DATA_BASE_PLACE_HOLDER, databaseName));
//			prepareStatement.setString(1, surveyResponseId);
//			prepareStatement.executeUpdate();
//		} catch (Exception e) {
//			logger.error("unable to delete SurveyResponseData " + e.getMessage());
//			throw new SurveyResponseDaoException("unable to delete SurveyResponseData " + e.getMessage());
//		} finally {
//			logger.debug("closing the Survey response connections");
//			MSSqlServerUtill.close(prepareStatement, connection);
//		}
//
//	}
}
