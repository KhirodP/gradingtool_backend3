package com.pwc.grading.reportingdb.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.ReportingDBTableConstant;
import com.pwc.grading.reportingdb.dao.IFormRatingDAO;
import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.db.constant.ReportingDatabaseQueryConstants;
import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.model.FormRatingTable;
import com.pwc.grading.reportingdb.model.ReportDBCenterIncharge;
import com.pwc.grading.reportingdb.model.ReportDBFieldAuditor;
import com.pwc.grading.reportingdb.model.ReportDBTrainingCenter;
import com.pwc.grading.util.InQueryBuilderUtil;

/**
 * Implementation class for {@link IFormRatingDAO} 
 *
 */
//@Singleton
public class FormRatingDAOImpl implements IFormRatingDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(FormRatingDAOImpl.class);

	/**
	 * This method is used to add the rating form data into FormRatingTable.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param formRatingObj object containing the details of the rating form.
	 * @return the formUUID of this entry.
	 * @throws ReportingDbDAOException if any exception occurs when creating the entry.
	 */
	@Override
	public String addFormRatingData(Connection connection, String databaseName, FormRatingTable formRatingObj)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside addFormRatingData(con) method of FormRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.INSERT_FORM_RATING
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			LOGGER.debug("FormRating INSERT QUERY : " + insertQuery);

			prepareStatement.setString(1, formRatingObj.getId());
			prepareStatement.setString(2, formRatingObj.getFormId());
			prepareStatement.setString(3, formRatingObj.getPartnerProjectId());
			prepareStatement.setString(4, formRatingObj.getFormName());
			prepareStatement.setString(5, formRatingObj.getTcName());
			prepareStatement.setString(6, formRatingObj.getTcId());
			prepareStatement.setString(7, formRatingObj.getCenterAddress());
			prepareStatement.setString(8, formRatingObj.getDistrict());
			prepareStatement.setString(9, formRatingObj.getCenterInchargeName());
			prepareStatement.setString(10, formRatingObj.getCenterInchargePhone());
			prepareStatement.setString(11, formRatingObj.getCenterLocation());
			prepareStatement.setInt(12, formRatingObj.getMaxMarks());
			prepareStatement.setInt(13, formRatingObj.getSaScore());
			prepareStatement.setInt(14, formRatingObj.getFaScore());
			prepareStatement.setInt(15, formRatingObj.getVariance());
			prepareStatement.setBoolean(16, formRatingObj.getStatus());
			prepareStatement.setString(17, formRatingObj.getSaDateAssigned());
			prepareStatement.setString(18, formRatingObj.getSaDateCompletion());
			prepareStatement.setString(19, formRatingObj.getSaStartTime());
			prepareStatement.setString(20, formRatingObj.getSaEndTime());
			prepareStatement.setString(21, formRatingObj.getFaName());
			prepareStatement.setString(22, formRatingObj.getFaPhone());
			prepareStatement.setString(23, formRatingObj.getSecondaryAuditorName());
			prepareStatement.setString(24, formRatingObj.getFaLocation());
			prepareStatement.setString(25, formRatingObj.getFaDateAssigned());
			prepareStatement.setString(26, formRatingObj.getFaDateCompleted());
			prepareStatement.setString(27, formRatingObj.getFaStartTime());
			prepareStatement.setString(28, formRatingObj.getFaEndTime());
			prepareStatement.setString(29, formRatingObj.getSignoffTime());
			prepareStatement.setString(30, formRatingObj.getOtp());

			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " FormRatingData added successfully.");
			return formRatingObj.getId(); // Returning form UUID
		} catch (Exception e) {
			LOGGER.error("Unable to add FormRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to add FormRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormRatingData preparedStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to update the rating form data into FormRatingTable.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param id the formUUID.
	 * @param formRatingObj object containing the details of the rating form.
	 * @return  the formUUID of this entry.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	@Override
	public String updateFormRatingData(Connection connection, String databaseName, String id,
			FormRatingTable formRatingObj) throws ReportingDbDAOException {
		LOGGER.debug(".inside updateFormRatingData(con) method of FormRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.UPDATE_FORM_RATING
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			LOGGER.debug("FormRating UPDATE QUERY : " + insertQuery);
			prepareStatement.setString(1, formRatingObj.getFormName());
			prepareStatement.setString(2, formRatingObj.getTcName());
			prepareStatement.setString(3, formRatingObj.getTcId());
			prepareStatement.setString(4, formRatingObj.getCenterAddress());
			prepareStatement.setString(5, formRatingObj.getDistrict());
			prepareStatement.setString(6, formRatingObj.getCenterInchargeName());
			prepareStatement.setString(7, formRatingObj.getCenterInchargePhone());
			prepareStatement.setString(8, formRatingObj.getCenterLocation());
			prepareStatement.setInt(9, formRatingObj.getMaxMarks());
			prepareStatement.setInt(10, formRatingObj.getSaScore());
			prepareStatement.setInt(11, formRatingObj.getFaScore());
			prepareStatement.setInt(12, formRatingObj.getVariance());
			prepareStatement.setBoolean(13, formRatingObj.getStatus());
			prepareStatement.setString(14, formRatingObj.getSaDateAssigned());
			prepareStatement.setString(15, formRatingObj.getSaDateCompletion());
			prepareStatement.setString(16, formRatingObj.getSaStartTime());
			prepareStatement.setString(17, formRatingObj.getSaEndTime());
			prepareStatement.setString(18, formRatingObj.getFaName());
			prepareStatement.setString(19, formRatingObj.getFaPhone());
			prepareStatement.setString(20, formRatingObj.getSecondaryAuditorName());
			prepareStatement.setString(21, formRatingObj.getFaLocation());
			prepareStatement.setString(22, formRatingObj.getFaDateAssigned());
			prepareStatement.setString(23, formRatingObj.getFaDateCompleted());
			prepareStatement.setString(24, formRatingObj.getFaStartTime());
			prepareStatement.setString(25, formRatingObj.getFaEndTime());
			prepareStatement.setString(26, formRatingObj.getSignoffTime());
			prepareStatement.setString(27, formRatingObj.getOtp());
			prepareStatement.setString(28, id);
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " FormRatingData Updated successfully.");
			return formRatingObj.getId(); // Returning form UUID
		} catch (Exception e) {
			LOGGER.error("Unable to Update FormRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to Update FormRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormRatingData preparedStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to get the rating form using formId and partnerProjectId.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param formId the id of the form.
	 * @param partnerProjectId the partnerProjectId.
	 * @return a {@link FormRatingTable} object which is having the details of the rating form.
	 * @throws ReportingDbDAOException  if any exception occurs when fetching the entry.
	 */
	@Override
	public FormRatingTable getFormByFormIdAndPartnerProjectId(Connection connection, String databaseName, String formId,
			String partnerProjectId) throws ReportingDbDAOException {
		LOGGER.debug(".inside getFormByFormIdAndPartnerProjectId(con) method of FormRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_FR_BY_FORMID_PARTNERPROJECTID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(selectQuery);
			LOGGER.debug("FormRating SELECT QUERY : " + selectQuery);
			prepareStatement.setString(1, formId);
			prepareStatement.setString(2, partnerProjectId);

			resultSet = prepareStatement.executeQuery();
			FormRatingTable obj = getObjectFromResultSet(resultSet);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Unable to add FormRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to add FormRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormRatingData resultSet, prepareStatement");
			ReportDBMSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
	}

	/**
	 * This method is used to delete the rating forms for the given partnerprojectId list.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param partnerProjectIdList a list of partner project Ids.
	 * @throws ReportingDbDAOException  if any exception occurs when deleting the rating forms.
	 */
	@Override
	public void deleteFormRatingDataForPartnerProjectIdList(Connection connection, String databaseName,
			List<String> partnerProjectIdList) throws ReportingDbDAOException {
		LOGGER.debug(".inside deleteFormRatingDataForPartnerProjectIdList(con) method of FormRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
		PreparedStatement prepareStatement = null;
//		String inStr = convertListToInString(partnerProjectIdList);
		try {
			String deleteQuery = ReportingDatabaseQueryConstants.DELETE_FR_BY_PARTNERPROJECTID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			String queryWithPlaceHolders = InQueryBuilderUtil.buildPlaceHolders(deleteQuery+" (", partnerProjectIdList.size());
//			deleteQuery = deleteQuery + inStr;
//			LOGGER.debug("FormRating DELETE QUERY : " + deleteQuery);
			prepareStatement = connection.prepareStatement(queryWithPlaceHolders);
			for(int i = 1; i <= partnerProjectIdList.size(); i++){
				prepareStatement.setString(i, partnerProjectIdList.get(i-1));
			}
			int rowsDeleted = prepareStatement.executeUpdate();
			LOGGER.debug(rowsDeleted + " rows DELETED from FormRating table.");
		} catch (Exception e) {
			LOGGER.error("Unable to DELETE FormRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to DELETE FormRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormRatingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to get all the formUUID for the given partner project Ids.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param partnerProjectIdList a list of partner project Ids.
	 * @return a list which is containing all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the data.
	 */
	@Override
	public List<String> getFormUUIDForPartnerProjectIdList(Connection connection, String databaseName,
			List<String> partnerProjectIdList) throws ReportingDbDAOException {
		LOGGER.debug(".inside getFormUUIDForPartnerProjectIdList(con) method of FormRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
//		String inStr = convertListToInString(partnerProjectIdList);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<String> formUUIDList = new ArrayList<String>();
		try {
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_FR_FORMUUID_BY_PARTNERPROJECTID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
//			selectQuery = selectQuery + inStr;
//			LOGGER.debug("SELECT QUERY : " + selectQuery);
			String queryWithPlaceHolders = InQueryBuilderUtil.buildPlaceHolders(selectQuery+" (", partnerProjectIdList.size());
			prepareStatement = connection.prepareStatement(queryWithPlaceHolders);
			for(int i = 1; i <= partnerProjectIdList.size(); i++){
				prepareStatement.setString(i, partnerProjectIdList.get(i-1));
			}
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				formUUIDList.add(resultSet.getString(ReportingDBTableConstant.ID));
			}
			return formUUIDList;
		} catch (Exception e) {
			LOGGER.error("Unable to get formUUID from FormRatingData table" + e.getMessage());
			throw new ReportingDbDAOException("Unable to get formUUID from FormRatingData table" + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormRatingData resultSet, prepareStatement");
			ReportDBMSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
	}

	/**
	 * This method is used to get all the formUUIDs for the given tcId and partnerProjectId.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param partnerProjectId the partner projectId
	 * @param tcId the id of the training center.
	 * @return all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the data.
	 */
	@Override
	public List<String> getFormUUIDForPartnerProjectIdAndTcId(Connection connection, String databaseName,
			String partnerProjectId, String tcId) throws ReportingDbDAOException {
		LOGGER.debug(".inside getFormUUIDForPartnerProjectIdList(con) method of FormRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
//		String inStr = convertListToInString(partnerProjectIdList);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<String> formUUIDList = new ArrayList<String>();
		try {
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_FR_FORMUUID_BY_PARTNERPROJECTID_AND_TCID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
//			selectQuery = selectQuery + inStr;
			LOGGER.debug("SELECT QUERY : " + selectQuery);
			prepareStatement = connection.prepareStatement(selectQuery);
			prepareStatement.setString(1, partnerProjectId);
			prepareStatement.setString(2, tcId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				formUUIDList.add(resultSet.getString(ReportingDBTableConstant.ID));
			}
			return formUUIDList;
		} catch (Exception e) {
			LOGGER.error("Unable to get formUUID from FormRatingData table" + e.getMessage());
			throw new ReportingDbDAOException("Unable to get formUUID from FormRatingData table" + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormRatingData resultSet, prepareStatement");
			ReportDBMSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
	}

	/**
	 * This method is used to update the training center details for the rating forms.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param obj the object which is containing the training center details.
	 * @throws ReportingDbDAOException if any exception occurs when updating the data.
	 */
	@Override
	public void updateTCDetails(Connection connection, String databaseName, ReportDBTrainingCenter obj)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside updateTCDetails(con) method of FormRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.UPDATE_TC_BY_PARTNERPROJECTID_TCID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			LOGGER.debug("FormRating UPDATE TC QUERY : " + insertQuery);
			prepareStatement.setString(1, obj.getTcName());
			prepareStatement.setString(2, obj.getCenterAddress());
			prepareStatement.setString(3, obj.getCenterLocation());
			prepareStatement.setString(4, obj.getDistrict());  //Update district also along with TC Details.
			prepareStatement.setString(5, obj.getPartnerProjectId());
			prepareStatement.setString(6, obj.getTcId());
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " FormRatingData Training center Updated successfully.");
		} catch (Exception e) {
			LOGGER.error("Unable to Update Training center of FormRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to Update Training center of FormRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormRatingData preparedStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to update the Center-In-Charge details for the rating forms.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param obj the object which is containing the Center-In-Charge details.
	 * @throws ReportingDbDAOException if any exception occurs when updating the data.
	 */
	@Override
	public void updateCICDetails(Connection connection, String databaseName, ReportDBCenterIncharge obj)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside updateCICDetails(con) method of FormRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.UPDATE_CIC_BY_PARTNERPROJECTID_TCID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			LOGGER.debug("FormRating UPDATE CIC QUERY : " + insertQuery);
			prepareStatement.setString(1, obj.getCenterInchargeName());
			prepareStatement.setString(2, obj.getCenterInchargePhone());
			prepareStatement.setString(3, null);
			prepareStatement.setString(4, obj.getPartnerProjectId());
			prepareStatement.setString(5, obj.getTcid());
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " FormRatingData Center-In-Charge Updated successfully.");
		} catch (Exception e) {
			LOGGER.error("Unable to Update Center-In-Charge of FormRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to Update Center-In-Charge of FormRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormRatingData preparedStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to update the Field Auditor details for the rating forms.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param obj the object which is containing the Field Auditor details.
	 * @throws ReportingDbDAOException if any exception occurs when updating the data.
	 */
	@Override
	public void updateFADetails(Connection connection, String databaseName, String formUUID,
			ReportDBFieldAuditor object) throws ReportingDbDAOException {
		LOGGER.debug(".inside updateFADetails(con) method of FormRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection is : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String updateQuery = ReportingDatabaseQueryConstants.UPDATE_FR_FA_BY_FORMUUID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
//			LOGGER.debug("FormRating UPDATE FA QUERY : "+updateQuery);
			prepareStatement = connection.prepareStatement(updateQuery);
			prepareStatement.setString(1, object.getFaName());
			prepareStatement.setString(2, object.getFaPhone());
			prepareStatement.setString(3, formUUID);
			int rows = prepareStatement.executeUpdate();
			LOGGER.debug(rows + " Field_auditor name and Phone is updated in FormRatingData table");
		} catch (Exception e) {
			LOGGER.error("Unable to update Field_auditor name and Phone of FormRatingData table" + e.getMessage());
			throw new ReportingDbDAOException(
					"Unable to update Field_auditor name and Phone of FormRatingData table" + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormRatingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to get the rating form using formId, tcId and partnerProjectId.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param formId the id of the form.
	 * @param tcId the training center Id.
	 * @param partnerProjectId the partnerProjectId.
	 * @return a {@link FormRatingTable} object which is having the details of the rating form.
	 * @throws ReportingDbDAOException  if any exception occurs when fetching the entry.
	 */
	@Override
	public FormRatingTable getFormByFormIdAndTcIdAndPartnerProjectId(Connection connection, String databaseName,
			String formId, String tcId, String partnerProjectId) throws ReportingDbDAOException {
		LOGGER.debug(".inside getFormByFormIdAndTcIdAndPartnerProjectId(con) method of FormRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_FR_BY_FORMID_TCID_PARTNERPROJECTID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(selectQuery);
//			LOGGER.debug("FormRating SELECT QUERY : "+selectQuery);
			prepareStatement.setString(1, formId);
			prepareStatement.setString(2, tcId);
			prepareStatement.setString(3, partnerProjectId);
			resultSet = prepareStatement.executeQuery();
			FormRatingTable obj = getObjectFromResultSet(resultSet);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Unable to get FormRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get FormRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormRatingData resultSet, prepareStatement");
			ReportDBMSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
	}

	/**
	 * This method is used to delete the rating form entries for all the given formUUIDs.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param formUUIDList all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when deleting the data.
	 */
	@Override
	public void deleteFormRatingDataByFormUUid(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside deleteFormRatingDataForPartnerProjectIdList(con) method of FormRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
		PreparedStatement prepareStatement = null;
//		String inStr = convertListToInString(formUUIDList);
		try {
			String deleteQuery = ReportingDatabaseQueryConstants.DELETE_FR_BY_FORM_UUId
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
//			deleteQuery = deleteQuery + inStr;
//			LOGGER.debug("FormRating DELETE QUERY : " + deleteQuery);
			String queryWithPlaceHolders = InQueryBuilderUtil.buildPlaceHolders(deleteQuery+" (", formUUIDList.size());
			prepareStatement = connection.prepareStatement(queryWithPlaceHolders);
			for(int i = 1; i <= formUUIDList.size(); i++){
				prepareStatement.setString(i, formUUIDList.get(i-1));
			}
			int rowsDeleted = prepareStatement.executeUpdate();
			LOGGER.debug(rowsDeleted + " rows DELETED from FormRating table.");
		} catch (Exception e) {
			LOGGER.error("Unable to DELETE FormRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to DELETE FormRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormRatingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}
	
	private FormRatingTable getObjectFromResultSet(ResultSet rs) throws SQLException {
		FormRatingTable obj = null;
		while (rs.next()) {
			obj = new FormRatingTable();
			obj.setId(rs.getString(ReportingDBTableConstant.ID));
			obj.setFormId(rs.getString(ReportingDBTableConstant.FORM_ID));
			obj.setPartnerProjectId(rs.getString(ReportingDBTableConstant.PARTNER_PROJECT_ID));
			obj.setFormName(rs.getString(ReportingDBTableConstant.FORM_NAME));
			obj.setTcName(rs.getString(ReportingDBTableConstant.TC_NAME));
			obj.setTcId(rs.getString(ReportingDBTableConstant.TC_ID));
			obj.setCenterAddress(rs.getString(ReportingDBTableConstant.CENTER_ADDRESS));
			obj.setCenterInchargeName(rs.getString(ReportingDBTableConstant.CIC_NAME));
			obj.setCenterInchargePhone(rs.getString(ReportingDBTableConstant.CIC_PHONE));
			obj.setCenterLocation(rs.getString(ReportingDBTableConstant.CENTER_LOCATION));
			obj.setDistrict(rs.getString(ReportingDBTableConstant.DISTRICT));
			obj.setMaxMarks(rs.getInt(ReportingDBTableConstant.MAX_MARKS));
			obj.setSaScore(rs.getInt(ReportingDBTableConstant.SA_SCORE));
			obj.setFaScore(rs.getInt(ReportingDBTableConstant.FA_SCORE));
			obj.setVariance(rs.getInt(ReportingDBTableConstant.VARIANCE));
			obj.setStatus(rs.getBoolean(ReportingDBTableConstant.STATUS));
			obj.setSaDateAssigned(rs.getString(ReportingDBTableConstant.SA_DATE_ASSIGNED));
			obj.setSaDateCompletion(rs.getString(ReportingDBTableConstant.SA_DATE_COMPLETION));
			obj.setSaStartTime(rs.getString(ReportingDBTableConstant.SA_START_TIME));
			obj.setSaEndTime(rs.getString(ReportingDBTableConstant.SA_END_TIME));
			obj.setFaName(rs.getString(ReportingDBTableConstant.FA_NAME));
			obj.setFaPhone(rs.getString(ReportingDBTableConstant.FA_PHONE));
			obj.setSecondaryAuditorName(rs.getString(ReportingDBTableConstant.SEC_AUDITOR_NAME));
			obj.setFaLocation(rs.getString(ReportingDBTableConstant.FA_LOCATION));
			obj.setFaDateAssigned(rs.getString(ReportingDBTableConstant.FA_DATE_ASSIGNED));
			obj.setFaDateCompleted(rs.getString(ReportingDBTableConstant.FA_DATE_COMPLETED));
			obj.setFaStartTime(rs.getString(ReportingDBTableConstant.FA_START_TIME));
			obj.setFaEndTime(rs.getString(ReportingDBTableConstant.FA_END_TIME));
			obj.setSignoffTime(rs.getString(ReportingDBTableConstant.SIGN_OFF_TIME));
			obj.setOtp(rs.getString(ReportingDBTableConstant.SIGN_OFF_TIME));
		}
		return obj;
	}

//	private String convertListToInString(List<String> list) {
//		if (list != null && list.size() > 0) {
//			String inStr = "";
//			String content = "";
//			for (String partnerProjectId : list) {
//				content += "'" + partnerProjectId + "',";
//			}
//			content = content.substring(0, content.length() - 1);
//			inStr = "(" + content + ")";
//			return inStr;
//		} else {
//			return "()";
//		}
//
//	}


}
