package com.pwc.grading.scheduler.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.scheduler.dao.ISchedulerDao;
import com.pwc.grading.scheduler.dao.SchedulerQueryConstants;
import com.pwc.grading.scheduler.dao.exception.SchedulerDaoException;
import com.pwc.grading.scheduler.model.GradingType;
import com.pwc.grading.scheduler.model.RatingType;
import com.pwc.grading.scheduler.model.SchedulerMapping;

/**
 * Implementation class for {@link ISchedulerDao} 
 *
 */
@Singleton
public class SchedulerDaoImpl implements ISchedulerDao {
	private static final Logger logger = LoggerFactory.getLogger(SchedulerDaoImpl.class);

	/**
	 * this method is used to update rating type date
	 */
	@Override
	public void updateRatingTypeData(String databaseName, SchedulerMapping scheduler) throws SchedulerDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(SchedulerQueryConstants.UPDATE_RATING_TYPE_DATA_SCHEDULER
					.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, scheduler.getFieldAuditorId());
			prepareStatement.setLong(2, scheduler.getAuditDate());
			prepareStatement.setBoolean(3, scheduler.isAuditStatus());
			prepareStatement.setBoolean(4, scheduler.isAuditCancled());
//			prepareStatement.setLong(5, scheduler.getSelfAssessmentDeadLine());
			prepareStatement.setString(5, scheduler.getProjectId());
			prepareStatement.setString(6, scheduler.getPartnerId());
			prepareStatement.setString(7, scheduler.getFormId());
			prepareStatement.setString(8, scheduler.getTcId());
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row updated in RatingType Data");
		} catch (Exception e) {
			logger.error("unable to update RatingType mapping " + e.getMessage());
			throw new SchedulerDaoException("unable to update RatingType mapping " + e.getMessage());
		} finally {
			logger.debug("closing the RatingType connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * this method is used to update grading type date
	 */
	@Override
	public void updateGradingTypeData(String databaseName, SchedulerMapping scheduler) throws SchedulerDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(SchedulerQueryConstants.UPDATE_GRADING_TYPE_DATA_SCHEDULER
					.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, scheduler.getFieldAuditorId());
			prepareStatement.setLong(2, scheduler.getAuditDate());
			prepareStatement.setBoolean(3, scheduler.isAuditStatus());
			prepareStatement.setBoolean(4, scheduler.isAuditCancled());
//			prepareStatement.setLong(5, scheduler.getSelfAssessmentDeadLine());
			prepareStatement.setString(5, scheduler.getProjectId());
			prepareStatement.setString(6, scheduler.getPartnerId());
			prepareStatement.setString(7, scheduler.getFormId());
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row updated in Gradingtype");
		} catch (Exception e) {
			logger.error("unable to update Gradingtype mapping " + e.getMessage());
			throw new SchedulerDaoException("unable to update Gradingtype mapping " + e.getMessage());
		} finally {
			logger.debug("closing the Gradingtype connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * this method is used to add rating type date
	 */
	@Override
	public void addRatingTypeData(Connection connection, String databaseName, SchedulerMapping scheduler)
			throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		try {
			logger.debug("in add Rating Type Data is : " + scheduler);
			prepareStatement = connection.prepareStatement(SchedulerQueryConstants.INSERT_REPORTING_TYPE_DATA
					.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			int i = 1;
			prepareStatement.setString(i++, scheduler.getPartnerId());
			prepareStatement.setString(i++, scheduler.getTcId());
			prepareStatement.setString(i++, scheduler.getProjectId());
			prepareStatement.setString(i++, scheduler.getFormId());
			prepareStatement.setBoolean(i++, scheduler.getSelfAssignmentStatus());
//			prepareStatement.setLong(i++, scheduler.getSelfAssessmentDeadLine());
			prepareStatement.setString(i++, scheduler.getFieldAuditorId());
			prepareStatement.setLong(i++, scheduler.getAuditDate());
			prepareStatement.setBoolean(i++, scheduler.isAuditStatus());
			prepareStatement.setBoolean(i++, scheduler.isAuditCancled());
			prepareStatement.setString(i++, scheduler.getSecondaryFieldAuditorName());
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row added in schedler");
		} catch (Exception e) {
			logger.error("unable to add Rating Type Data  " + e.getMessage());
			throw new SchedulerDaoException("unable to  add Rating Type Data " + e.getMessage());
		} finally {
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * this method is used to add Grading type date
	 */
	@Override
	public void addGradingTypeData(Connection connection, String databaseName, SchedulerMapping scheduler)
			throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		try {
			logger.debug("in addGradingTypeData  : GradingType:: " + scheduler);
			prepareStatement = connection.prepareStatement(SchedulerQueryConstants.INSERT_GRADING_TYPE_DATA
					.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			int i = 1;
			prepareStatement.setString(i++, scheduler.getPartnerId());
			prepareStatement.setString(i++, scheduler.getProjectId());
			prepareStatement.setString(i++, scheduler.getFormId());
			prepareStatement.setBoolean(i++, scheduler.getSelfAssignmentStatus());
//			prepareStatement.setLong(i++, scheduler.getSelfAssessmentDeadLine());
			prepareStatement.setString(i++, scheduler.getFieldAuditorId());
			prepareStatement.setLong(i++, scheduler.getAuditDate());
			prepareStatement.setBoolean(i++, scheduler.isAuditStatus());
			prepareStatement.setBoolean(i++, scheduler.isAuditCancled());
			prepareStatement.setString(i++, scheduler.getSecondaryFieldAuditorName());
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row added in GradingType Table");
		} catch (Exception e) {
			logger.error("unable to add in GradingType Table " + e.getMessage());
			throw new SchedulerDaoException("unable to add in GradingType data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * this method is used to delete Grading type date
	 */
	@Override
	public void deleteGradingTypeData(Connection connection, String databaseName, String partnerId, String projectId)
			throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		try {
			logger.debug("in deleteGradingTypeData  : partnerId:: " + partnerId + " projectId: " + projectId);
			prepareStatement = connection.prepareStatement(SchedulerQueryConstants.DELETE_GRADING_TYPE_DATA
					.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row deleted in GradingType Table");
		} catch (Exception e) {
			logger.error("unable to delete in GradingType Table " + e.getMessage());
			throw new SchedulerDaoException("unable to delete in GradingType data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * this method is used to check Grading type date exist or not
	 */
	@Override
	public boolean isGradingTypeDateExist(Connection connection, String databaseName, String projectId)
			throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		ResultSet resultset = null;
		try {
			logger.debug("in getGradingTypeDate  : ProjectId:: " + projectId);
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.SELECT_GRADING_TYPE_DATA_BY_PROJECT_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			resultset = prepareStatement.executeQuery();
			if (resultset.next()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("unable to add in GradingType Table " + e.getMessage());
			throw new SchedulerDaoException("unable to add in GradingType data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultset, prepareStatement, null);
		}
	}

	/**
	 * this method is used to check rating type date exist or not
	 */
	@Override
	public boolean isRatingTypeDateExist(Connection connection, String databaseName, String projectId)
			throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		ResultSet resultset = null;
		try {
			logger.debug("in get RatingTypeDate  : ProjectId:: " + projectId);
			prepareStatement = connection.prepareStatement(SchedulerQueryConstants.SELECT_RATING_TYPE_DATA_BY_PROJECT_ID
					.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			resultset = prepareStatement.executeQuery();
			if (resultset.next()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("unable to get RatingType Date  " + e.getMessage());
			throw new SchedulerDaoException("unable to get RatingType Date" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultset, prepareStatement, null);
		}
	}

	/**
	 * this method is used to delete Rating type data
	 */
	@Override
	public void deleteRatingTypeDate(Connection connection, String databaseName, String formId)
			throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		try {
			logger.debug("in delete RatingTypeDate  : formId:: " + formId);
			prepareStatement = connection.prepareStatement(SchedulerQueryConstants.DELETE_RATING_TYPE_DATA
					.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, formId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " rows got deleted in RatingType Table");
		} catch (Exception e) {
			logger.error("unable to delete form in  RatingType Date  " + e.getMessage());
			throw new SchedulerDaoException("unable to delete form in  RatingType Date" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * this method is used to delete Grading type date
	 */
	@Override
	public void deleteGradingTypeDate(Connection connection, String databaseName, String formId)
			throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		try {
			logger.debug("in delete GradingTypeDate  : formId:: " + formId);
			prepareStatement = connection.prepareStatement(SchedulerQueryConstants.DELETE_GRADING_TYPE_DATA_BY_FORM_ID
					.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, formId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " rows got deleted in GradingType Table");
		} catch (Exception e) {
			logger.error("unable to delete form in GradingTypeDate  " + e.getMessage());
			throw new SchedulerDaoException("unable to delete form in GradingType Date" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * this method is used to get Grading type date
	 */
	@Override
	public List<GradingType> getGradingTypeDataByPartnerIdAndProjectId(Connection connection, String databaseName,
			String partnerId, String projectId) throws SchedulerDaoException {
		List<GradingType> listOfGradingType = new ArrayList<GradingType>();
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			logger.debug("in getGradingTypeDate  : partnerId:: " + partnerId + " projectId:: " + projectId);
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.SELECT_GRADING_TYPE_DATA_BY_PARTNER_ID_AND_PROJECT_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, partnerId);
			prepareStatement.setString(2, projectId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				GradingType gradingtype = buildGradingTypeDataFromResultset(resultSet);
				listOfGradingType.add(gradingtype);
			}
		} catch (Exception e) {
			logger.error("unable to add in GradingType Table " + e.getMessage());
			throw new SchedulerDaoException("unable to add in GradingType data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return listOfGradingType;
	}

	private GradingType buildGradingTypeDataFromResultset(ResultSet resultSet) throws SQLException {
		GradingType gradingType = new GradingType();
		gradingType.setPartnerId(resultSet.getString(1));
		gradingType.setProjectId(resultSet.getString(2));
		gradingType.setFormId(resultSet.getString(3));
		gradingType.setSelfAssignmentStatus(resultSet.getBoolean(4));
//		gradingType.setSelfAssessmentDeadLine(resultSet.getLong(5));
		gradingType.setFieldAuditorId(resultSet.getString(5));
		gradingType.setAuditDate(resultSet.getLong(6));
		gradingType.setAuditStatus(resultSet.getBoolean(7));
		gradingType.setAuditCancled(resultSet.getBoolean(8));
		gradingType.setSecondaryFieldAuditorName(resultSet.getString(9));
		return gradingType;
	}

	/**
	 * this method is used to delete Rating type data
	 */
	@Override
	public void deleteRatingTypeDate(Connection connection, String databaseName, String tcId, String oldProjectId)
			throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		try {
			logger.debug("in delete RatingTypeDate  : tcId:: " + tcId);
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.DELETE_RATING_TYPE_DATA_BY_TCID_AND_PROJECTID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, tcId);
			prepareStatement.setString(2, oldProjectId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " rows got deleted in RatingType Table");
		} catch (Exception e) {
			logger.error("unable to delete form in  RatingType Date  " + e.getMessage());
			throw new SchedulerDaoException("unable to delete form in  RatingType Date" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * this method is used to delete Rating type data
	 */
	@Override
	public void deleteRatingTypeDateBytcIdProjectIdAndPartnerId(Connection connection, String databaseName, String tcId,
			String projectId, String partnerId) throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		try {
			logger.debug("in delete RatingTypeDate  : tcId:: " + tcId);
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.DELETE_RATING_TYPE_DATA_BY_TCID_PARTNER_ID_AND_PROJECTID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, tcId);
			prepareStatement.setString(2, projectId);
			prepareStatement.setString(3, partnerId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " rows got deleted in RatingType Table");
		} catch (Exception e) {
			logger.error("unable to delete form in  RatingType Date  " + e.getMessage());
			throw new SchedulerDaoException("unable to delete form in  RatingType Date" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * this method is used to get Grading type date
	 */
	@Override
	public GradingType getGradingTypeDataByPartnerIdProjectIdAndFormId(Connection connection, String databaseName,
			String partnerId, String projectId, String formId) throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {

			logger.debug("in getGradingTypeDate  : partnerId:: " + partnerId);
			prepareStatement = connection.prepareStatement(
					SchedulerQueryConstants.SELECT_GRADING_TYPE_DATA_BY_PARTNER_ID_PROJECT_ID_AND_FORM_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, partnerId);
			prepareStatement.setString(2, projectId);
			prepareStatement.setString(3, formId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				GradingType gradingtype = buildGradingTypeDataFromResultset(resultSet);
				return gradingtype;
			}
		} catch (Exception e) {
			logger.error("unable to get  GradingType Table " + e.getMessage());
			throw new SchedulerDaoException("unable to get  GradingType data :" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return null;
	}

	/**
	 * this method is used to get Grading type data
	 */
	@Override
	public GradingType getGradingTypeDataByPartnerIdProjectIdAndFormId(String databaseName, String partnerId,
			String projectId, String formId) throws SchedulerDaoException {
		logger.debug("in getGradingTypeDate  : partnerId:: " + partnerId);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(
					SchedulerQueryConstants.SELECT_GRADING_TYPE_DATA_BY_PARTNER_ID_PROJECT_ID_AND_FORM_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, partnerId);
			prepareStatement.setString(2, projectId);
			prepareStatement.setString(3, formId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				GradingType gradingtype = buildGradingTypeDataFromResultset(resultSet);
				return gradingtype;
			}
		} catch (Exception e) {
			logger.error("unable to get GradingType Table " + e.getMessage());
			throw new SchedulerDaoException("unable to get GradingType data :" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

	/**
	 * this method is used to get Grading type data
	 */
	@Override
	public List<GradingType> getAllGradingTypeData(String tenantId) throws SchedulerDaoException {
		logger.debug("in getAllGradingTypeData : tenantId " + tenantId);
		List<GradingType> listOfGradingType = new ArrayList<GradingType>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(SchedulerQueryConstants.SELECT_GRADING_TYPE_DATA
					.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				GradingType gradingtype = buildGradingTypeDataFromResultset(resultSet);
				listOfGradingType.add(gradingtype);
			}
		} catch (Exception e) {
			logger.error("unable to get GradingType Table " + e.getMessage());
			throw new SchedulerDaoException("unable to add in GradingType data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfGradingType;
	}

	/**
	 * this method is used to get Rating type data
	 */
	@Override
	public List<RatingType> getAllRatingTypeData(String tenantId) throws SchedulerDaoException {
		logger.debug("in getAllRatingTypeData : tenantId " + tenantId);
		List<RatingType> listOfGradingType = new ArrayList<RatingType>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(SchedulerQueryConstants.SELECT_RATING_TYPE_DATA
					.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				RatingType ratingType = buildRatingTypeFromResultSet(resultSet);
				listOfGradingType.add(ratingType);
			}
		} catch (Exception e) {
			logger.error("unable to get RatingType Data " + e.getMessage());
			throw new SchedulerDaoException("unable to get RatingType Data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfGradingType;
	}

	private RatingType buildRatingTypeFromResultSet(ResultSet resultSet) throws SQLException {
		RatingType ratingType = new RatingType();
		ratingType.setPartnerId(resultSet.getString(1));
		ratingType.setTcId(resultSet.getString(2));
		ratingType.setProjectId(resultSet.getString(3));
		ratingType.setFormId(resultSet.getString(4));
		ratingType.setSelfAssignmentStatus(resultSet.getBoolean(5));
//		ratingType.setSelfAssessmentDeadLine(resultSet.getLong(6));
		ratingType.setFieldAuditorId(resultSet.getString(6));
		ratingType.setAuditDate(resultSet.getLong(7));
		ratingType.setAuditStatus(resultSet.getBoolean(8));
		ratingType.setAuditCancled(resultSet.getBoolean(9));
		ratingType.setSecondaryFieldAuditorName(resultSet.getString(10));
		return ratingType;
	}

	/**
	 * this method is used to get Grading type data
	 */
	@Override
	public List<GradingType> getAllGradingTypeDataByFieldAuditorId(String tenantId, String fieldAuditorId)
			throws SchedulerDaoException {
		logger.debug("in getAllGradingTypeDataByFieldAuditorId : tenantId " + tenantId);
		List<GradingType> listOfGradingType = new ArrayList<GradingType>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.SELECT_GRADING_TYPE_DATA_BY_FIELD_AUDITOR_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, fieldAuditorId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				GradingType gradingtype = buildGradingTypeDataFromResultset(resultSet);
				listOfGradingType.add(gradingtype);
			}
		} catch (Exception e) {
			logger.error("unable to add in GradingType Table " + e.getMessage());
			throw new SchedulerDaoException("unable to add in GradingType data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfGradingType;
	}

	/**
	 * this method is used to get Rating type data
	 */
	@Override
	public List<RatingType> getAllRatingTypeDataByFieldAuditorId(String tenantId, String fieldAuditorId)
			throws SchedulerDaoException {
		logger.debug("in getAllRatingTypeDataByFieldAuditorId : tenantId " + tenantId);
		List<RatingType> listOfGradingType = new ArrayList<RatingType>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.SELECT_RATING_TYPE_DATA_BY_FIELD_AUDITOR_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, fieldAuditorId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				RatingType ratingType = buildRatingTypeFromResultSet(resultSet);
				listOfGradingType.add(ratingType);
			}
		} catch (Exception e) {
			logger.error("unable to get RatingType Data " + e.getMessage());
			throw new SchedulerDaoException("unable to get RatingType Data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfGradingType;
	}

	/**
	 * used to update the self assessment of centerIncharge in ratingType Table
	 */
	@Override
	public void updateSelfAssigInRatingTypeByProjectIdPartnerIdFormIdAndTcId(Connection connection, String databaseName,
			boolean isSubmited, String partnerId, String projectId, String formId, String tcId)
			throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.UPDATE_SELF_ASSIG_IN_RATING_TYPE_DATA_SCHEDULER
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setBoolean(1, isSubmited);
			prepareStatement.setString(2, projectId);
			prepareStatement.setString(3, partnerId);
			prepareStatement.setString(4, formId);
			prepareStatement.setString(5, tcId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row updated in RatingType Data");
		} catch (Exception e) {
			logger.error("unable to update RatingType mapping " + e.getMessage());
			throw new SchedulerDaoException("unable to update RatingType mapping " + e.getMessage());
		} finally {
			logger.debug("closing the RatingType connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * used to update the self assessment of client sponsor in GradingType table
	 */
	@Override
	public void updateSelfAssigInGradingTypeByProjectIdPartnerIdAndFormId(Connection connection, String databaseName,
			boolean isSubmited, String partnerId, String projectId, String formId) throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.UPDATE_SELF_ASSIG_IN_GRADING_TYPE_DATA_SCHEDULER
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setBoolean(1, isSubmited);
			prepareStatement.setString(2, projectId);
			prepareStatement.setString(3, partnerId);
			prepareStatement.setString(4, formId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row updated in Gradingtype");
		} catch (Exception e) {
			logger.error("unable to update Gradingtype mapping " + e.getMessage());
			throw new SchedulerDaoException("unable to update Gradingtype mapping " + e.getMessage());
		} finally {
			logger.debug("closing the Gradingtype connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * used to update the audit status of field-auditor in gradingType table
	 */
	@Override
	public void updateAuditStatusInGradingTypeByProjectIdPartnerIdAndFormId(Connection connection, String databaseName,
			boolean auditStatus, String secondaryFieldAuditorName, String partnerId, String projectId, String formId)
			throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.UPDATE_AUDIT_STATUS_IN_GRADING_TYPE_DATA_SCHEDULER
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setBoolean(1, auditStatus);
			prepareStatement.setString(2, secondaryFieldAuditorName);
			prepareStatement.setString(3, projectId);
			prepareStatement.setString(4, partnerId);
			prepareStatement.setString(5, formId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row updated in Gradingtype");
		} catch (Exception e) {
			logger.error("unable to update Gradingtype mapping " + e.getMessage());
			throw new SchedulerDaoException("unable to update Gradingtype mapping " + e.getMessage());
		} finally {
			logger.debug("closing the Gradingtype connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * used to update the audit status of field-auditor in ratingType table
	 */
	@Override
	public void updateAuditStatusInRatingTypeByProjectIdPartnerIdFormIdAndTcId(Connection connection,
			String databaseName, boolean auditStatus, String secondaryFieldAuditorName, String partnerId,
			String projectId, String formId, String tcId) throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.UPDATE_AUDIT_STATUS_IN_RATING_TYPE_DATA_SCHEDULER
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setBoolean(1, auditStatus);
			prepareStatement.setString(2, secondaryFieldAuditorName);
			prepareStatement.setString(3, projectId);
			prepareStatement.setString(4, partnerId);
			prepareStatement.setString(5, formId);
			prepareStatement.setString(6, tcId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " row updated in RatingType Data");
		} catch (Exception e) {
			logger.error("unable to update RatingType mapping " + e.getMessage());
			throw new SchedulerDaoException("unable to update RatingType mapping " + e.getMessage());
		} finally {
			logger.debug("closing the RatingType connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * this method is used to get Rating type data
	 */
	@Override
	public RatingType getRatingTypeDataByProjectIdParterIdFormIdAndTcId(String databaseName, String partnerId,
			String projectId, String formId, String tcId) throws SchedulerDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(
					SchedulerQueryConstants.SELECT_RATING_TYPE_DATA_SCHEDULER_BY_PROJECTID_PARTNERID_FORMID_AND_TCID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			prepareStatement.setString(3, formId);
			prepareStatement.setString(4, tcId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				logger.debug("ratingType exist");
				RatingType ratingType = buildRatingTypeFromResultSet(resultSet);
				return ratingType;
			}
		} catch (Exception e) {
			logger.error("unable to get RatingType mapping " + e.getMessage());
			throw new SchedulerDaoException("unable to get RatingType mapping " + e.getMessage());
		} finally {
			logger.debug("closing the RatingType connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

	/**
	 * this method is used to get Rating type data
	 */
	@Override
	public RatingType getRatingTypeDataByProjectIdParterIdFormIdAndTcId(Connection connection, String databaseName,
			String partnerId, String projectId, String formId, String tcId) throws SchedulerDaoException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(
					SchedulerQueryConstants.SELECT_RATING_TYPE_DATA_SCHEDULER_BY_PROJECTID_PARTNERID_FORMID_AND_TCID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			prepareStatement.setString(3, formId);
			prepareStatement.setString(4, tcId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				logger.debug("ratingType exist");
				RatingType ratingType = buildRatingTypeFromResultSet(resultSet);
				return ratingType;
			}
		} catch (Exception e) {
			logger.error("unable to get RatingType mapping " + e.getMessage());
			throw new SchedulerDaoException("unable to get RatingType mapping " + e.getMessage());
		} finally {
			logger.debug("closing the RatingType connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return null;
	}

	/**
	 * this method is used to get Rating type data
	 */
	@Override
	public List<RatingType> getRatingTypeDataByProjectIdParterIdAndTcId(String databaseName, String partnerId,
			String projectId, String tcId) throws SchedulerDaoException {
		List<RatingType> listOfRatingType = new ArrayList<RatingType>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(
					SchedulerQueryConstants.SELECT_RATING_TYPE_DATA_SCHEDULER_BY_PROJECTID_PARTNERID_AND_TCID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			prepareStatement.setString(3, tcId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				logger.debug("ratingType exist");
				RatingType ratingType = buildRatingTypeFromResultSet(resultSet);
				listOfRatingType.add(ratingType);
			}
		} catch (Exception e) {
			logger.error("unable to get RatingType mapping " + e.getMessage());
			throw new SchedulerDaoException("unable to get RatingType mapping " + e.getMessage());
		} finally {
			logger.debug("closing the RatingType connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfRatingType;
	}

	/**
	 * this method is used to get Rating type data
	 */
	@Override
	public List<RatingType> getRatingTypeDataByProjectIdParterIdAndTcId(Connection connection, String databaseName,
			String partnerId, String projectId, String tcId) throws SchedulerDaoException {
		List<RatingType> listOfRatingType = new ArrayList<RatingType>();
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(
					SchedulerQueryConstants.SELECT_RATING_TYPE_DATA_SCHEDULER_BY_PROJECTID_PARTNERID_AND_TCID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			prepareStatement.setString(3, tcId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				logger.debug("ratingType exist");
				RatingType ratingType = buildRatingTypeFromResultSet(resultSet);
				listOfRatingType.add(ratingType);
			}
		} catch (Exception e) {
			logger.error("unable to get RatingType mapping " + e.getMessage());
			throw new SchedulerDaoException("unable to get RatingType mapping " + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return listOfRatingType;
	}

	/**
	 * this method is used to get Rating type data
	 */
	@Override
	public List<RatingType> getRatingTypeDataByProjectIdAndParterId(String databaseName, String partnerId,
			String projectId) throws SchedulerDaoException {
		List<RatingType> listOfRatingType = new ArrayList<RatingType>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(
					SchedulerQueryConstants.SELECT_RATING_TYPE_DATA_SCHEDULER_BY_PROJECTID_AND_PARTNERID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				logger.debug("ratingType exist");
				RatingType ratingType = buildRatingTypeFromResultSet(resultSet);
				listOfRatingType.add(ratingType);
			}
		} catch (Exception e) {
			logger.error("unable to get RatingType mapping " + e.getMessage());
			throw new SchedulerDaoException("unable to get RatingType mapping " + e.getMessage());
		} finally {
			logger.debug("closing the RatingType connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfRatingType;

	}

	/**
	 * this method is used to get Rating type data
	 */
	@Override
	public List<RatingType> getRatingTypeDataByProjectIdAndParterId(Connection connection, String databaseName,
			String partnerId, String projectId) throws SchedulerDaoException {
		List<RatingType> listOfRatingType = new ArrayList<RatingType>();
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(
					SchedulerQueryConstants.SELECT_RATING_TYPE_DATA_SCHEDULER_BY_PROJECTID_AND_PARTNERID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				logger.debug("ratingType exist");
				RatingType ratingType = buildRatingTypeFromResultSet(resultSet);
				listOfRatingType.add(ratingType);
			}
		} catch (Exception e) {
			logger.error("unable to get RatingType mapping " + e.getMessage());
			throw new SchedulerDaoException("unable to get RatingType mapping " + e.getMessage());
		} finally {
			logger.debug("closing the RatingType connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return listOfRatingType;

	}

	/**
	 * this method is used to get Grading type data
	 */
	@Override
	public List<GradingType> getGradingTypeDataByProjectIdAndPartnerId(String databaseName, String partnerId,
			String projectId) throws SchedulerDaoException {
		List<GradingType> listOfGradingType = new ArrayList<GradingType>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			logger.debug("in getGradingTypeDate  : partnerId:: " + partnerId + " projectId:: " + projectId);
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.SELECT_GRADING_TYPE_DATA_BY_PARTNER_ID_AND_PROJECT_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, partnerId);
			prepareStatement.setString(2, projectId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				GradingType gradingtype = buildGradingTypeDataFromResultset(resultSet);
				listOfGradingType.add(gradingtype);
			}
		} catch (Exception e) {
			logger.error("unable to add in GradingType Table " + e.getMessage());
			throw new SchedulerDaoException("unable to add in GradingType data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfGradingType;
	}

	/**
	 * this method is used to get Grading type data
	 */
	@Override
	public List<GradingType> getGradingTypeDataByProjectIdAndPartnerId(Connection connection, String databaseName,
			String partnerId, String projectId) throws SchedulerDaoException {
		List<GradingType> listOfGradingType = new ArrayList<GradingType>();
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			logger.debug("in getGradingTypeDate  : partnerId:: " + partnerId + " projectId:: " + projectId);
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.SELECT_GRADING_TYPE_DATA_BY_PARTNER_ID_AND_PROJECT_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, partnerId);
			prepareStatement.setString(2, projectId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				GradingType gradingtype = buildGradingTypeDataFromResultset(resultSet);
				listOfGradingType.add(gradingtype);
			}
		} catch (Exception e) {
			logger.error("unable to add in GradingType Table " + e.getMessage());
			throw new SchedulerDaoException("unable to add in GradingType data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return listOfGradingType;
	}

	/**
	 * this method is used to get Grading type data
	 */
	@Override
	public List<GradingType> getGradingTypeDataByPartnerIdAndProjectId(String tenantId, String partnerId,
			String projectId) throws SchedulerDaoException {
		List<GradingType> listOfGradingType = new ArrayList<GradingType>();
		PreparedStatement prepareStatement = null;
		Connection connection = null;
		ResultSet resultSet = null;
		try {
			logger.debug("in getGradingTypeDate  : partnerId:: " + partnerId + " projectId:: " + projectId);
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.SELECT_GRADING_TYPE_DATA_BY_PARTNER_ID_AND_PROJECT_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, partnerId);
			prepareStatement.setString(2, projectId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				GradingType gradingtype = buildGradingTypeDataFromResultset(resultSet);
				listOfGradingType.add(gradingtype);
			}
		} catch (Exception e) {
			logger.error("unable to get GradingType data " + e.getMessage());
			throw new SchedulerDaoException("unable to  get GradingType data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfGradingType;
	}

	/**
	 * this method is used to get Grading type data
	 */
	@Override
	public List<GradingType> getGradingTypeDataByFieldAuditorIdProjectIdAndPartnerId(String tenantId,
			String fieldAuditorId, String projectId, String partnerId) throws SchedulerDaoException {
		logger.debug("in getGradingTypeDataByFieldAuditorIdAndProjectId : tenantId " + tenantId);
		List<GradingType> listOfGradingType = new ArrayList<GradingType>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			logger.debug("fieldAuditorId " + fieldAuditorId + " partnerId:" + partnerId + " projectId " + projectId);
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(
					SchedulerQueryConstants.SELECT_GRADING_TYPE_DATA_BY_FIELD_AUDITOR_ID_PROJECT_ID_AND_PARTNER_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, fieldAuditorId);
			prepareStatement.setString(2, projectId);
			prepareStatement.setString(3, partnerId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				GradingType gradingtype = buildGradingTypeDataFromResultset(resultSet);
				listOfGradingType.add(gradingtype);
			}
		} catch (Exception e) {
			logger.error("unable to GET in GradingType DATA " + e.getMessage());
			throw new SchedulerDaoException("unable to GET  GradingType data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfGradingType;
	}

	/**
	 * this method is used to get Grading type data
	 */
	@Override
	public Set<String> getGradingTypeDataByFieldAuditorIdUniqueByProjectId(String tenantId, String fieldAuditorId)
			throws SchedulerDaoException {
		logger.debug("in getGradingTypeDataByFieldAuditorIdUniqueByProjectIdAndPartnerId : tenantId " + tenantId);
		Set<String> listOfUniqueProjects = new HashSet<String>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.SELECT_DISTINCT_GRADING_TYPE_DATA_BY_FIELD_AUDITOR_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, fieldAuditorId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
//				Map<String, String> gradingMap = new HashedMap<String, String>();
//				GradingType gradingtype = buildUniqueGradingTypeDataFromResultset(resultSet);
//				listOfGradingType.add(gradingtype);
				String projectId = resultSet.getString(1);
				listOfUniqueProjects.add(projectId);
			}
		} catch (Exception e) {
			logger.error("unable to GET in GradingType DATA " + e.getMessage());
			throw new SchedulerDaoException("unable to GET  GradingType data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfUniqueProjects;

	}

	/**
	 * this method is used to get Rating type data
	 */
	@Override
	public Set<String> getRatingTypeDataByFieldAuditorIdUniqueByProjectId(String tenantId, String fieldAuditorId)
			throws SchedulerDaoException {
		logger.debug("in getGradingTypeDataByFieldAuditorIdUniqueByProjectIdFormId : tenantId " + tenantId);
		Set<String> listOfUniqueProjects = new HashSet<String>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(SchedulerQueryConstants.SELECT_DISTINCT_RATING_TYPE_DATA_BY_FIELD_AUDITOR_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, fieldAuditorId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
//				RatingType ratingType = buildUniqueRatingTypeFromResultSet(resultSet);
//				listOfRatingType.add(ratingType);
				String projectId = resultSet.getString(1);
				listOfUniqueProjects.add(projectId);
			}
		} catch (Exception e) {
			logger.error("unable to get RatingType Data " + e.getMessage());
			throw new SchedulerDaoException("unable to get RatingType Data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfUniqueProjects;
	}

	/**
	 * this method is used to get Rating type data
	 */
	@Override
	public List<RatingType> getRatingTypeDataByFieldAuditorIdProjectIdAndPartnerId(String tenantId,
			String fieldAuditorId, String projectId, String partnerId) throws SchedulerDaoException {
		logger.debug("in getRatingTypeDataByFieldAuditorIdProjectIdAndPartnerId : tenantId " + tenantId);
		List<RatingType> list = new ArrayList<RatingType>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(
					SchedulerQueryConstants.SELECT_RATING_TYPE_DATA_BY_FIELD_AUDITOR_ID_PARTNER_ID_AND_PROJECT_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, fieldAuditorId);
			prepareStatement.setString(2, projectId);
			prepareStatement.setString(3, partnerId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				RatingType ratingType = buildRatingTypeFromResultSet(resultSet);
				list.add(ratingType);
			}
		} catch (Exception e) {
			throw new SchedulerDaoException("unable to get RatingType Data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return list;
	}

	/**
	 * this method is used to get Grading type data
	 */
	@Override
	public Set<String> getGradingTypeDataByFieldAuditorIdAndProjectId(String tenantId, String fieldAuditorId,
			String projectId) throws SchedulerDaoException {
		logger.debug("in getGradingTypeDataByFieldAuditorIdAndProjectId : tenantId " + tenantId);
		Set<String> listOfUniquePartnerId = new HashSet<String>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(
					SchedulerQueryConstants.SELECT_DISTINCT_PARTNER_ID_FROM_GRADING_TYPE_DATA_BY_FIELD_AUDITOR_ID_AND_PROJECT_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, fieldAuditorId);
			prepareStatement.setString(2, projectId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
//				RatingType ratingType = buildRatingTypeFromResultSet(resultSet);
				listOfUniquePartnerId.add(resultSet.getString(1));
			}
		} catch (Exception e) {
			throw new SchedulerDaoException("unable to get RatingType Data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfUniquePartnerId;

	}

	/**
	 * this method is used to get Rating type data
	 */
	@Override
	public Set<String> getRatingTypeDataByFieldAuditorIdAndProjectId(String tenantId, String fieldAuditorId,
			String projectId) throws SchedulerDaoException {
		logger.debug("in getRatingTypeDataByFieldAuditorIdAndProjectId : tenantId " + tenantId);
		Set<String> listOfUniquePartnerId = new HashSet<String>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(
					SchedulerQueryConstants.SELECT_DISTINCT_PARTNER_ID_FROM_RATING_TYPE_DATA_BY_FIELD_AUDITOR_ID_AND_PROJECT_ID
							.replace(SchedulerQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, fieldAuditorId);
			prepareStatement.setString(2, projectId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
//				RatingType ratingType = buildRatingTypeFromResultSet(resultSet);
				listOfUniquePartnerId.add(resultSet.getString(1));
			}
		} catch (Exception e) {
			throw new SchedulerDaoException("unable to get RatingType Data" + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfUniquePartnerId;
	}
}
