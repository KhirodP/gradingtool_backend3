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
import com.pwc.grading.reportingdb.dao.IFormGradingDAO;
import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.db.constant.ReportingDatabaseQueryConstants;
import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.model.FormGradingTable;
import com.pwc.grading.reportingdb.model.ReportDBFieldAuditor;
import com.pwc.grading.util.InQueryBuilderUtil;

/**
 * Implementation class for {@link IFormGradingDAO} 
 *
 */
//@Singleton
public class FormGradingDAOImpl implements IFormGradingDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(FormGradingDAOImpl.class);

	/**
	 * This method is used to add the grading form data into FormGradingTable.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param formGradingObj object containing the details of the grading form.
	 * @return the formUUID of this entry.
	 * @throws ReportingDbDAOException if any exception occurs when creating the entry.
	 */
	@Override
	public String addFormGradingData(Connection connection, String databaseName, FormGradingTable formGradingObj)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside addFormGradingData(con) method of FormGradingDAOImpl class.");
//		LOGGER.debug("Incoming connection is : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.INSERT_FORM_GRADING
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			LOGGER.debug("FormGrading INSERT QUERY : " + insertQuery);

			prepareStatement.setString(1, formGradingObj.getId());
			prepareStatement.setString(2, formGradingObj.getFormId());
			prepareStatement.setString(3, formGradingObj.getPartnerProjectId());
			prepareStatement.setString(4, formGradingObj.getFormName());
			prepareStatement.setInt(5, formGradingObj.getMaxMarks());
			prepareStatement.setInt(6, formGradingObj.getSaScore());
			prepareStatement.setInt(7, formGradingObj.getFaScore());
			prepareStatement.setInt(8, formGradingObj.getVariance());
			prepareStatement.setBoolean(9, formGradingObj.getStatus());
			prepareStatement.setString(10, formGradingObj.getPiaDateAssigned());
			prepareStatement.setString(11, formGradingObj.getPiaDateCompletion());
			prepareStatement.setString(12, formGradingObj.getPiaStartTime());
			prepareStatement.setString(13, formGradingObj.getPiaEndTime());
			prepareStatement.setString(14, formGradingObj.getFaName());
			prepareStatement.setString(15, formGradingObj.getFaPhone());
			prepareStatement.setString(16, formGradingObj.getSecondaryAuditorName());
			prepareStatement.setString(17, formGradingObj.getFaLocation());
			prepareStatement.setString(18, formGradingObj.getFaDateAssigned());
			prepareStatement.setString(19, formGradingObj.getFaDateCompleted());
			prepareStatement.setString(20, formGradingObj.getFaStartTime());
			prepareStatement.setString(21, formGradingObj.getFaEndTime());
			prepareStatement.setString(22, formGradingObj.getSignoffTime());
			prepareStatement.setString(23, formGradingObj.getOtp());

			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " FormGradingData added successfully.");
			return formGradingObj.getId(); // Returning form UUID
		} catch (Exception e) {
			LOGGER.error("Unable to add FormGradingData, " + e.getMessage());
			throw new ReportingDbDAOException("Unable to add FormGradingData, " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormGradingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}
	
	/**
	 * This method is used to update the grading form data into FormGradingTable.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param id the formUUID.
	 * @param formGradingObj object containing the details of the grading form.
	 * @return  the formUUID of this entry.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	@Override
	public String updateFormGradingData(Connection connection, String databaseName, String id,
			FormGradingTable formGradingObj) throws ReportingDbDAOException {
		LOGGER.debug(".inside updateFormGradingData(con) method of FormGradingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection is : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.UPDATE_FORM_GRADING
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			LOGGER.debug("FormGrading UPDATE QUERY : " + insertQuery);
			prepareStatement.setString(1, formGradingObj.getFormName());
			prepareStatement.setInt(2, formGradingObj.getMaxMarks());
			prepareStatement.setInt(3, formGradingObj.getSaScore());
			prepareStatement.setInt(4, formGradingObj.getFaScore());
			prepareStatement.setInt(5, formGradingObj.getVariance());
			prepareStatement.setBoolean(6, formGradingObj.getStatus());
			prepareStatement.setString(7, formGradingObj.getPiaDateAssigned());
			prepareStatement.setString(8, formGradingObj.getPiaDateCompletion());
			prepareStatement.setString(9, formGradingObj.getPiaStartTime());
			prepareStatement.setString(10, formGradingObj.getPiaEndTime());
			prepareStatement.setString(11, formGradingObj.getFaName());
			prepareStatement.setString(12, formGradingObj.getFaPhone());
			prepareStatement.setString(13, formGradingObj.getSecondaryAuditorName());
			prepareStatement.setString(14, formGradingObj.getFaLocation());
			prepareStatement.setString(15, formGradingObj.getFaDateAssigned());
			prepareStatement.setString(16, formGradingObj.getFaDateCompleted());
			prepareStatement.setString(17, formGradingObj.getFaStartTime());
			prepareStatement.setString(18, formGradingObj.getFaEndTime());
			prepareStatement.setString(19, formGradingObj.getSignoffTime());
			prepareStatement.setString(20, formGradingObj.getOtp());
			prepareStatement.setString(21, id);
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " FormGradingData Updated successfully.");
			return id; // Returning form UUID
		} catch (Exception e) {
			LOGGER.error("Unable to Update FormGradingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to Update FormGradingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormGradingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to delete the grading forms for the given partnerprojectId list.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param partnerProjectIdList a list of partner project Ids.
	 * @throws ReportingDbDAOException  if any exception occurs when deleting the grading forms.
	 */
	@Override
	public void deleteFormGradingDataForPartnerProjectIdList(Connection connection, String databaseName,
			List<String> partnerProjectIdList) throws ReportingDbDAOException {
		LOGGER.debug(".inside deleteFormGradingDataForPartnerProjectIdList(con) method of FormGradingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection is : "+connection);
		PreparedStatement prepareStatement = null;
//		String inStr = convertListToInString(partnerProjectIdList);
		try {
			String deleteQuery = ReportingDatabaseQueryConstants.DELETE_FG_BY_PARTNERPROJECTID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
//			deleteQuery = deleteQuery + inStr;
//			LOGGER.debug("FormGrading DELETE QUERY : " + deleteQuery);
			String queryWithPlaceHolders = InQueryBuilderUtil.buildPlaceHolders(deleteQuery+" (", partnerProjectIdList.size());
			prepareStatement = connection.prepareStatement(queryWithPlaceHolders);
			for(int i = 1; i <= partnerProjectIdList.size(); i++){
				prepareStatement.setString(i, partnerProjectIdList.get(i-1));
			}
			int rowsDeleted = prepareStatement.executeUpdate();
			LOGGER.debug(rowsDeleted + " rows DELETED from FormGrading table.");
		} catch (Exception e) {
			LOGGER.error("Unable to DELETE FormGradingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to DELETE FormGradingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormGradingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to get the grading form using formId and partnerProjectId.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param formId the id of the form.
	 * @param partnerProjectId the partnerProjectId.
	 * @return a {@link FormGradingTable} object which is having the details of the grading form.
	 * @throws ReportingDbDAOException  if any exception occurs when fetching the entry.
	 */
	@Override
	public FormGradingTable getFormByFormIdAndPartnerProjectId(Connection connection, String databaseName,
			String formId, String partnerProjectId) throws ReportingDbDAOException {
		LOGGER.debug(".inside getFormByFormIdAndPartnerProjectId(con) method of FormGradingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection is : "+connection);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
//			connection = ReportDBMSSqlServerUtill.getConnection();
			String insertQuery = ReportingDatabaseQueryConstants.SELECT_FG_BY_FORMID_PARTNERPROJECTID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			LOGGER.debug("FormGrading SELECT QUERY : " + insertQuery);
			prepareStatement.setString(1, formId);
			prepareStatement.setString(2, partnerProjectId);
			resultSet = prepareStatement.executeQuery();
			FormGradingTable obj = getObjectFromResultSet(resultSet);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Unable to get FormGradingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get FormGradingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormGradingData resultSet, prepareStatement");
			ReportDBMSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
	}

	/**
	 * This method is used to get all the formUUID for the given partner project Ids.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param partnerProjectIdList a list of partner project Ids.
	 * @return a list which is containing all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the data.
	 */
	@Override
	public List<String> getFormUUIDForPartnerProjectIdList(Connection connection, String databaseName,
			List<String> partnerProjectIdList) throws ReportingDbDAOException {
		LOGGER.debug(".inside getFormUUIDForPartnerProjectIdList(con) method of FormGradingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection is : "+connection);
//		String inStr = convertListToInString(partnerProjectIdList);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<String> formUUIDList = new ArrayList<String>();
		try {
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_FG_FORMUUID_BY_PARTNERPROJECTID
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
			LOGGER.error("Unable to get formUUID from FormGradingData table" + e.getMessage());
			throw new ReportingDbDAOException("Unable to get formUUID from FormGradingData table" + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormGradingData resultSet, prepareStatement");
			ReportDBMSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
	}

	/**
	 * This method is used to update the field auditor details.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param object details of the Field Auditor.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	@Override
	public void updateFADetails(Connection connection, String databaseName, ReportDBFieldAuditor object)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside updateFADetails(con) method of FormGradingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection is : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String updateQuery = ReportingDatabaseQueryConstants.UPDATE_FG_FA_BY_FORMID_PARTNERPROJECTID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			LOGGER.debug("FormGrading UPDATE FA QUERY : " + updateQuery);
			prepareStatement = connection.prepareStatement(updateQuery);
			prepareStatement.setString(1, object.getFaName());
			prepareStatement.setString(2, object.getFaPhone());
			prepareStatement.setString(3, object.getPartnerProjectId());
			prepareStatement.setString(4, object.getFormId());
			int rows = prepareStatement.executeUpdate();
			LOGGER.debug(rows + " rows of Field_auditor data updated in FormGradingData table");
		} catch (Exception e) {
			LOGGER.error("Unable to update Field_auditor data of FormGradingData table" + e.getMessage());
			throw new ReportingDbDAOException(
					"Unable to update Field_auditor data of FormGradingData table" + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormGradingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to delete the grading forms for the given Form UUID list.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param formUUIDList a list of Form UUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when deleting the grading forms. 
	 */
	@Override
	public void deleteFormGradingDataForFormUUIDList(Connection connection, String databaseName,
			List<String> formUUIDList) throws ReportingDbDAOException {
		LOGGER.debug(".inside deleteFormGradingDataForPartnerProjectIdList(con) method of FormGradingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection is : "+connection);
		PreparedStatement prepareStatement = null;
//		String inStr = convertListToInString(formUUIDList);
		try {
			String deleteQuery = ReportingDatabaseQueryConstants.DELETE_FG_BY_FORMUUID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
//			deleteQuery = deleteQuery + inStr;
			String queryWithPlaceHolders = InQueryBuilderUtil.buildPlaceHolders(deleteQuery+" (", formUUIDList.size());
//			LOGGER.debug("FormGrading formUUIDList DELETE QUERY : " + deleteQuery);
			prepareStatement = connection.prepareStatement(queryWithPlaceHolders);
			for(int i = 1; i <= formUUIDList.size(); i++){
				prepareStatement.setString(i, formUUIDList.get(i-1));
			}
			int rowsDeleted = prepareStatement.executeUpdate();
			LOGGER.debug(rowsDeleted + " rows DELETED from FormGrading table.");
		} catch (Exception e) {
			LOGGER.error("Unable to DELETE FormGradingData for given formUUIDList" + e.getMessage());
			throw new ReportingDbDAOException(
					"Unable to DELETE FormGradingData for given formUUIDList" + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormGradingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to update the form grading data for the given partnerproject Id.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param partnerProjectIdIfExists the partner project Id.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	@Override
	public void updateFormGradingDataByPartnerProjectId(Connection connection, String databaseName,
			String partnerProjectId) throws ReportingDbDAOException {
		LOGGER.debug(".inside updateFormGradingDataByPartnerProjectId(con) method of FormGradingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection is : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.UPDATE_FORM_GRADING_BY_PARTNER_PROJECT_ID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			LOGGER.debug("FormGrading UPDATE QUERY : " + insertQuery);
			prepareStatement.setString(1, null);
			prepareStatement.setString(2, partnerProjectId);
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " FormGradingData Updated successfully.");
		} catch (Exception e) {
			LOGGER.error("Unable to Update FormGradingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to Update FormGradingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the FormGradingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}
	
	
	private FormGradingTable getObjectFromResultSet(ResultSet rs) throws SQLException {
		FormGradingTable obj = null;
		while (rs.next()) {
			obj = new FormGradingTable();
			obj.setId(rs.getString(ReportingDBTableConstant.ID));
			obj.setFormId(rs.getString(ReportingDBTableConstant.FORM_ID));
			obj.setPartnerProjectId(rs.getString(ReportingDBTableConstant.PARTNER_PROJECT_ID));
			obj.setFormName(rs.getString(ReportingDBTableConstant.FORM_NAME));
			obj.setMaxMarks(rs.getInt(ReportingDBTableConstant.MAX_MARKS));
			obj.setSaScore(rs.getInt(ReportingDBTableConstant.SA_SCORE));
			obj.setFaScore(rs.getInt(ReportingDBTableConstant.FA_SCORE));
			obj.setVariance(rs.getInt(ReportingDBTableConstant.VARIANCE));
			obj.setStatus(rs.getBoolean(ReportingDBTableConstant.STATUS));
			obj.setPiaDateAssigned(rs.getString(ReportingDBTableConstant.PIA_DATE_ASSIGNED));
			obj.setPiaDateCompletion(rs.getString(ReportingDBTableConstant.PIA_DATE_COMPLETION));
			obj.setPiaStartTime(rs.getString(ReportingDBTableConstant.PIA_START_TIME));
			obj.setPiaEndTime(rs.getString(ReportingDBTableConstant.PIA_END_TIME));
			obj.setFaName(rs.getString(ReportingDBTableConstant.FA_NAME));
			obj.setFaPhone(rs.getString(ReportingDBTableConstant.FA_PHONE));
			obj.setSecondaryAuditorName(rs.getString(ReportingDBTableConstant.SEC_AUDITOR_NAME));
			obj.setFaLocation(rs.getString(ReportingDBTableConstant.FA_LOCATION));
			obj.setFaDateAssigned(rs.getString(ReportingDBTableConstant.FA_DATE_ASSIGNED));
			obj.setFaDateCompleted(rs.getString(ReportingDBTableConstant.FA_DATE_COMPLETED));
			obj.setFaStartTime(rs.getString(ReportingDBTableConstant.FA_START_TIME));
			obj.setFaEndTime(rs.getString(ReportingDBTableConstant.FA_END_TIME));
			obj.setSignoffTime(rs.getString(ReportingDBTableConstant.SIGN_OFF_TIME));
			obj.setOtp(rs.getString(ReportingDBTableConstant.OTP));
		}
		return obj;
	}

//	private String convertListToInString(List<String> partnerProjectIdList) {
//		if (partnerProjectIdList != null && partnerProjectIdList.size() > 0) {
//			String inStr = "";
//			String content = "";
//			for (String partnerProjectId : partnerProjectIdList) {
//				content += "'" + partnerProjectId + "',";
//			}
//			content = content.substring(0, content.length() - 1);
//			inStr = "(" + content + ")";
//			return inStr;
//		} else {
//			return "('')";
//		}
//
//	}



}
