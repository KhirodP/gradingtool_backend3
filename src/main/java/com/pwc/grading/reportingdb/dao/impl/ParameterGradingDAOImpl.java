package com.pwc.grading.reportingdb.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.ReportingDBTableConstant;
import com.pwc.grading.reportingdb.dao.IParameterGradingDAO;
import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.db.constant.ReportingDatabaseQueryConstants;
import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.model.ParameterGradingTable;
import com.pwc.grading.util.InQueryBuilderUtil;

/**
 * Implementation class for {@link IParameterGradingDAO}
 *
 */
//@Singleton
public class ParameterGradingDAOImpl implements IParameterGradingDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParameterGradingDAOImpl.class);

	/**
	 * This method is used to add the Parameters List into the ParameterGradingTable
	 * table.
	 * 
	 * @param connection           used to perform the database operations in a
	 *                             transaction.
	 * @param databaseName         the database name which is having the
	 *                             ParameterGradingTable table.
	 * @param parameterGradingList the list of grading entries to be inserted.
	 * @throws ReportingDbDAOException if any exception occurs when creating the
	 *                                 entry.
	 */
	@Override
	public void addParameterGradingDataList(Connection connection, String databaseName,
			List<ParameterGradingTable> parameterGradingList) throws ReportingDbDAOException {
		LOGGER.debug(".inside addParameterGradingDataList(con) method of ParameterGradingDAOImpl class.");
//		LOGGER.debug("Incoming connection instance: "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.INSERT_PARAMETER_GRADING
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);

			// Iterating the list. - BATCH OPERATIONS
			for (ParameterGradingTable parameterGradingObj : parameterGradingList) {
				prepareStatement.setString(1, parameterGradingObj.getFormId());
				prepareStatement.setString(2, parameterGradingObj.getSectionId());
				prepareStatement.setString(3, parameterGradingObj.getParameterId());
				prepareStatement.setInt(4, parameterGradingObj.getMaxmarks());
				prepareStatement.setInt(5, parameterGradingObj.getSaScore());
				prepareStatement.setInt(6, parameterGradingObj.getFaScore());
				prepareStatement.setInt(7, parameterGradingObj.getVariance());
				prepareStatement.addBatch();
			}
			prepareStatement.executeBatch();
		} catch (Exception e) {
			LOGGER.error("Unable to add ParameterGradingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to add ParameterGradingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the ParameterGradingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to add the parameters of a grading form into the
	 * ParameterGradingTable
	 * 
	 * @param connection          used to perform the database operations in a
	 *                            transaction.
	 * @param databaseName        the database name which is having the
	 *                            ParameterGradingTable table.
	 * @param parameterGradingObj the object having the single parameter data.
	 * @throws ReportingDbDAOException if any exception occurs when creating the
	 *                                 entry.
	 */
	@Override
	public void addParameterGradingData(Connection connection, String databaseName,
			ParameterGradingTable parameterGradingObj) throws ReportingDbDAOException {
		LOGGER.debug(".inside addParameterGradingData(con) method of ParameterGradingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection: "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.INSERT_PARAMETER_GRADING
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			prepareStatement.setString(1, parameterGradingObj.getFormId());
			prepareStatement.setString(2, parameterGradingObj.getSectionId());
			prepareStatement.setString(3, parameterGradingObj.getParameterId());
			prepareStatement.setInt(4, parameterGradingObj.getMaxmarks());
			prepareStatement.setInt(5, parameterGradingObj.getSaScore());
			prepareStatement.setInt(6, parameterGradingObj.getFaScore());
			prepareStatement.setInt(7, parameterGradingObj.getVariance());
			prepareStatement.setString(8, parameterGradingObj.getSaRemark());
			prepareStatement.setString(9, parameterGradingObj.getFaRemark());
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " ParameterGradingData added successfully.");
		} catch (Exception e) {
			LOGGER.error("Unable to add ParameterGradingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to add ParameterGradingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the ParameterGradingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to update the parameters of a grading form into the
	 * ParameterGradingTable
	 * 
	 * @param connection          used to perform the database operations in a
	 *                            transaction.
	 * @param databaseName        the database name which is having the
	 *                            ParameterGradingTable table.
	 * @param parameterGradingObj the object having the single parameter data to be
	 *                            updated.
	 * @throws ReportingDbDAOException if any exception occurs when updating the
	 *                                 entry.
	 */
	@Override
	public void updateParameterGradingData(Connection connection, String databaseName,
			ParameterGradingTable parameterGradingObj) throws ReportingDbDAOException {
		LOGGER.debug(".inside updateParameterGradingData(con) method of ParameterGradingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection: "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String updateQuery = ReportingDatabaseQueryConstants.UPDATE_PARAMETER_GRADING
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(updateQuery);
			prepareStatement.setInt(1, parameterGradingObj.getMaxmarks());
			prepareStatement.setInt(2, parameterGradingObj.getSaScore());
			prepareStatement.setInt(3, parameterGradingObj.getFaScore());
			prepareStatement.setInt(4, parameterGradingObj.getVariance());
			// new changes
			prepareStatement.setString(5, parameterGradingObj.getSaRemark());
			prepareStatement.setString(6, parameterGradingObj.getFaRemark());

			prepareStatement.setString(7, parameterGradingObj.getFormId());
			prepareStatement.setString(8, parameterGradingObj.getSectionId());
			prepareStatement.setString(9, parameterGradingObj.getParameterId());
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " ParameterGradingData updated successfully.");
		} catch (Exception e) {
			LOGGER.error("Unable to Update ParameterGradingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to Update ParameterGradingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the ParameterGradingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to delete all the parameters for all given formUUIDs.
	 * 
	 * @param connection   used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the
	 *                     ParameterGradingTable table.
	 * @param formUUIDList the list having all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when deleting the
	 *                                 entries.
	 */
	@Override
	public void deleteParameterGradingData(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside deleteParameterGradingData(con) method of ParameterGradingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection: "+connection);
//		String inStr = convertListToInString(formUUIDList);
		PreparedStatement prepareStatement = null;
		try {
			String deleteQuery = ReportingDatabaseQueryConstants.DELETE_PG_BY_FORMUUID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
//			deleteQuery = deleteQuery + inStr;
//			LOGGER.debug("ParameterGradingData delete Query : " + deleteQuery);
			String queryWithPlaceHolders = InQueryBuilderUtil.buildPlaceHolders(deleteQuery+" (", formUUIDList.size());
			prepareStatement = connection.prepareStatement(queryWithPlaceHolders);
			for(int i = 1; i <= formUUIDList.size(); i++){
				prepareStatement.setString(i, formUUIDList.get(i-1));
			}
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " ParameterGradingData rows deleted successfully.");
		} catch (Exception e) {
			LOGGER.error("Unable to Delete ParameterGradingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to Delete ParameterGradingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the ParameterGradingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to get a single parameter data of a grading form.
	 * 
	 * @param connection   used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the
	 *                     ParameterGradingTable table.
	 * @param formId       the formUUID of the grading form.
	 * @param sectionId    the sectionId of the section.
	 * @param parameterId  the parameterId of the parameter.
	 * @return a single parameter data.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the
	 *                                 entry.
	 */
	@Override
	public ParameterGradingTable getPGDataByFormSectionParameterIds(Connection connection, String databaseName,
			String formId, String sectionId, String parameterId) throws ReportingDbDAOException {
		LOGGER.debug(".inside getPGDataByFormSectionParameterIds(con) method of ParameterGradingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection: "+connection);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String updateQuery = ReportingDatabaseQueryConstants.SELECT_PG_BY_FORM_SECTION_PARAMETER_IDS
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(updateQuery);
			prepareStatement.setString(1, formId);
			prepareStatement.setString(2, sectionId);
			prepareStatement.setString(3, parameterId);
			resultSet = prepareStatement.executeQuery();
			ParameterGradingTable obj = getObjectFromResultSet(resultSet);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Unable to get ParameterGradingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get ParameterGradingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the ParameterGradingData resultSet, prepareStatement");
			ReportDBMSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
	}

	/**
	 * This method is used to update all the parameters for all given formUUIDs.
	 * 
	 * @param connection   used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the
	 *                     ParameterGradingTable table.
	 * @param formUUIDList the list having all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when updating the
	 *                                 entries.
	 */
	@Override
	public void updateParameterGradingDataByFormUUId(Connection connection, String databaseName,
			List<String> formUUIDList) throws ReportingDbDAOException {
		LOGGER.debug(".inside updateParameterGradingData method of ParameterGradingDAOImpl class.");
		PreparedStatement prepareStatement = null;
		try {
//			String list = convertListToInString(formUUIDList);
			String updateQuery = ReportingDatabaseQueryConstants.UPDATE_PARAMETER_GRADING_BY_FORM_UUID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
//			updateQuery = updateQuery + list;
			String queryWithPlaceHolders = InQueryBuilderUtil.buildPlaceHolders(updateQuery+" (", formUUIDList.size());
			prepareStatement = connection.prepareStatement(queryWithPlaceHolders);
			for(int i = 1; i <= formUUIDList.size(); i++){
				prepareStatement.setString(i, formUUIDList.get(i-1));
			}
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " ParameterGradingData updated successfully.");
		} catch (Exception e) {
			LOGGER.error("Unable to Update ParameterGradingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to Update ParameterGradingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the ParameterGradingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	private ParameterGradingTable getObjectFromResultSet(ResultSet rs) throws SQLException {
		ParameterGradingTable obj = null;
		while (rs.next()) {
			obj = new ParameterGradingTable();
			obj.setFormId(rs.getString(ReportingDBTableConstant.FORM_UUID));
			obj.setSectionId(rs.getString(ReportingDBTableConstant.SECTION_ID));
			obj.setParameterId(rs.getString(ReportingDBTableConstant.PARAMETER_ID));
			obj.setMaxmarks(rs.getInt(ReportingDBTableConstant.MAX_MARKS));
			obj.setSaScore(rs.getInt(ReportingDBTableConstant.SA_SCORE));
			obj.setFaScore(rs.getInt(ReportingDBTableConstant.FA_SCORE));
			obj.setVariance(rs.getInt(ReportingDBTableConstant.VARIANCE));
			obj.setFaRemark(rs.getString(ReportingDBTableConstant.FA_REMARK));
			obj.setSaRemark(rs.getString(ReportingDBTableConstant.SA_REMARK));
		}
		return obj;
	}

//	private String convertListToInString(List<String> formUUIDList) {
//		if (formUUIDList != null && formUUIDList.size() > 0) {
//			String inStr = "";
//			String content = "";
//			for (String partnerProjectId : formUUIDList) {
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
