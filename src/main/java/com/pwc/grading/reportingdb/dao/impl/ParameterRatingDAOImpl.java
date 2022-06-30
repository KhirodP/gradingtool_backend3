package com.pwc.grading.reportingdb.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.ReportingDBTableConstant;
import com.pwc.grading.reportingdb.dao.IParameterRatingDAO;
import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.db.constant.ReportingDatabaseQueryConstants;
import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.model.ParameterRatingTable;
import com.pwc.grading.util.InQueryBuilderUtil;

/**
 * Implementation class for {@link IParameterRatingDAO}
 *
 */
//@Singleton
public class ParameterRatingDAOImpl implements IParameterRatingDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParameterRatingDAOImpl.class);

	/**
	 * This method is used to add the Parameters List into the ParameterRatingTable
	 * table.
	 * 
	 * @param connection          used to perform the database operations in a
	 *                            transaction.
	 * @param databaseName        the database name which is having the
	 *                            ParameterRatingTable table.
	 * @param parameterRatingList the list of rating entries to be inserted.
	 * @throws ReportingDbDAOException if any exception occurs when creating the
	 *                                 entry.
	 */
	@Override
	public void addParameterRatingDataList(Connection connection, String databaseName,
			List<ParameterRatingTable> parameterRatingList) throws ReportingDbDAOException {
		LOGGER.debug(".inside addParameterRatingDataList(con) method of ParameterRatingDAOImpl class.");
//		LOGGER.debug("Incoming connection instance: "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.INSERT_PARAMETER_RATING
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);

			// Iterating the list. - BATCH OPERATIONS
			for (ParameterRatingTable parameterRatingObj : parameterRatingList) {
				prepareStatement.setString(1, parameterRatingObj.getFormId());
				prepareStatement.setString(2, parameterRatingObj.getSectionId());
				prepareStatement.setString(3, parameterRatingObj.getParameterId());
				prepareStatement.setInt(4, parameterRatingObj.getMaxmarks());
				prepareStatement.setInt(5, parameterRatingObj.getSaScore());
				prepareStatement.setInt(6, parameterRatingObj.getFaScore());
				prepareStatement.setInt(7, parameterRatingObj.getVariance());
				prepareStatement.setString(8, parameterRatingObj.getSaRemark());
				prepareStatement.setString(9, parameterRatingObj.getFaRemark());
				prepareStatement.addBatch();
			}
			prepareStatement.executeBatch();
			LOGGER.debug(parameterRatingList.size() + " ParameterRatingData inserted succesfully");
		} catch (Exception e) {
			LOGGER.error("Unable to add ParameterRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to add ParameterRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the ParameterRatingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to update the parameters of a rating form into the
	 * ParameterRatingTable
	 * 
	 * @param connection         used to perform the database operations in a
	 *                           transaction.
	 * @param databaseName       the database name which is having the
	 *                           ParameterRatingTable table.
	 * @param parameterRatingObj the object having the single parameter data to be
	 *                           updated.
	 * @throws ReportingDbDAOException if any exception occurs when updating the
	 *                                 entry.
	 */
	@Override
	public void updateParameterRatingData(Connection connection, String databaseName,
			ParameterRatingTable parameterRatingObj) throws ReportingDbDAOException {
		LOGGER.debug(".inside updateParameterRatingData(con) method of ParameterRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.UPDATE_PARAMETER_RATING
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			prepareStatement.setInt(1, parameterRatingObj.getMaxmarks());
			prepareStatement.setInt(2, parameterRatingObj.getSaScore());
			prepareStatement.setInt(3, parameterRatingObj.getFaScore());
			prepareStatement.setInt(4, parameterRatingObj.getVariance());

			prepareStatement.setString(5, parameterRatingObj.getSaRemark());
			prepareStatement.setString(6, parameterRatingObj.getFaRemark());

			prepareStatement.setString(7, parameterRatingObj.getFormId());
			prepareStatement.setString(8, parameterRatingObj.getSectionId());
			prepareStatement.setString(9, parameterRatingObj.getParameterId());
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " ParameterRatingData Updated successfully.");
		} catch (Exception e) {
			LOGGER.error("Unable to Update ParameterRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to Update ParameterRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the ParameterRatingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to delete all the parameters for all given formUUIDs.
	 * 
	 * @param connection   used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the
	 *                     ParameterRatingTable table.
	 * @param formUUIDList the list having all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when deleting the
	 *                                 entries.
	 */
	@Override
	public void deleteParameterRatingData(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside deleteParameterRatingData(con) method of ParameterRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection: "+connection);
//		String inStr = convertListToInString(formUUIDList);
		PreparedStatement prepareStatement = null;
		try {
			String deleteQuery = ReportingDatabaseQueryConstants.DELETE_PR_BY_FORMUUID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
//			deleteQuery = deleteQuery + inStr;
//			LOGGER.debug("ParameterRatingData delete Query : " + deleteQuery);
			String queryWithPlaceHolders = InQueryBuilderUtil.buildPlaceHolders(deleteQuery+" (", formUUIDList.size());
			prepareStatement = connection.prepareStatement(queryWithPlaceHolders);
			for(int i = 1; i <= formUUIDList.size(); i++){
				prepareStatement.setString(i, formUUIDList.get(i-1));
			}
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " ParameterRatingData rows deleted successfully.");
		} catch (Exception e) {
			LOGGER.error("Unable to Delete ParameterRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to Delete ParameterRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the ParameterRatingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to add the parameters of a rating form into the
	 * ParameterRatingTable
	 * 
	 * @param connection         used to perform the database operations in a
	 *                           transaction.
	 * @param databaseName       the database name which is having the
	 *                           ParameterRatingTable table.
	 * @param parameterRatingObj the object having the single parameter data.
	 * @throws ReportingDbDAOException if any exception occurs when creating the
	 *                                 entry.
	 */
	@Override
	public void addParameterRatingData(Connection connection, String databaseName,
			ParameterRatingTable parameterRatingObj) throws ReportingDbDAOException {
		LOGGER.debug(".inside addParameterRatingData(con) method of ParameterRatingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.INSERT_PARAMETER_RATING
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			prepareStatement.setString(1, parameterRatingObj.getFormId());
			prepareStatement.setString(2, parameterRatingObj.getSectionId());
			prepareStatement.setString(3, parameterRatingObj.getParameterId());
			prepareStatement.setInt(4, parameterRatingObj.getMaxmarks());
			prepareStatement.setInt(5, parameterRatingObj.getSaScore());
			prepareStatement.setInt(6, parameterRatingObj.getFaScore());
			prepareStatement.setInt(7, parameterRatingObj.getVariance());
			prepareStatement.setString(8, parameterRatingObj.getSaRemark());
			prepareStatement.setString(9, parameterRatingObj.getFaRemark());
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " ParameterRatingData added successfully.");
		} catch (Exception e) {
			LOGGER.error("Unable to add ParameterRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to add ParameterRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the ParameterRatingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to get a single parameter data of a rating form.
	 * 
	 * @param connection   used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the
	 *                     ParameterRatingTable table.
	 * @param formId       the formUUID of the rating form.
	 * @param sectionId    the sectionId of the section.
	 * @param parameterId  the parameterId of the parameter.
	 * @return a single parameter data.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the
	 *                                 entry.
	 */
	@Override
	public ParameterRatingTable getPRDataByFormSectionParameterIds(Connection connection, String databaseName,
			String formId, String sectionId, String parameterId) throws ReportingDbDAOException {
		LOGGER.debug(".inside getPRDataByFormSectionParameterIds(con) method of ParameterRatingDAOImpl class.");
//		LOGGER.debug("Incoming connection instance: "+connection);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_PR_BY_FORM_SECTION_PARAMETER_IDS
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(selectQuery);
			prepareStatement.setString(1, formId);
			prepareStatement.setString(2, sectionId);
			prepareStatement.setString(3, parameterId);
			resultSet = prepareStatement.executeQuery();
			ParameterRatingTable obj = getObjectFromResultSet(resultSet);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Unable to get ParameterRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get ParameterRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the ParameterRatingData resultSet, prepareStatement");
			ReportDBMSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
	}

	private ParameterRatingTable getObjectFromResultSet(ResultSet rs) throws SQLException {
		ParameterRatingTable obj = null;
		while (rs.next()) {
			obj = new ParameterRatingTable();
			obj.setFormId(rs.getString(ReportingDBTableConstant.FORM_UUID));
			obj.setSectionId(rs.getString(ReportingDBTableConstant.SECTION_ID));
			obj.setParameterId(rs.getString(ReportingDBTableConstant.PARAMETER_ID));
			obj.setMaxmarks(rs.getInt(ReportingDBTableConstant.MAX_MARKS));
			obj.setSaScore(rs.getInt(ReportingDBTableConstant.SA_SCORE));
			obj.setFaScore(rs.getInt(ReportingDBTableConstant.FA_SCORE));
			obj.setVariance(rs.getInt(ReportingDBTableConstant.VARIANCE));
			obj.setSaRemark(rs.getString(ReportingDBTableConstant.SA_REMARK));
			obj.setFaRemark(rs.getString(ReportingDBTableConstant.FA_REMARK));
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

	/**
	 * This method is used to update all the parameters for all given formUUIDs.
	 * 
	 * @param connection   used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the
	 *                     ParameterRatingTable table.
	 * @param formUUIDList the list having all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when updating the
	 *                                 entries.
	 */
	@Override
	public void updateParameterRatingDataByFormUUid(Connection connection, String databaseName,
			List<String> formUUIDList) throws ReportingDbDAOException {
		LOGGER.debug(".inside updateParameterRatingDataByFormUUid(con) method of ParameterRatingDAOImpl class.");
		PreparedStatement prepareStatement = null;
		try {
//			String list = convertListToInString(formUUIDList);
			String insertQuery = ReportingDatabaseQueryConstants.UPDATE_PARAMETER_RATING_BY_FORM_UUID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
//			insertQuery = insertQuery + list;
			String queryWithPlaceHolders = InQueryBuilderUtil.buildPlaceHolders(insertQuery+" (", formUUIDList.size());
			prepareStatement = connection.prepareStatement(queryWithPlaceHolders);
			for(int i = 1; i <= formUUIDList.size(); i++){
				prepareStatement.setString(i, formUUIDList.get(i-1));
			}
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " ParameterRatingData Updated successfully.");
		} catch (Exception e) {
			LOGGER.error("Unable to Update ParameterRatingData " + e.getMessage());
			throw new ReportingDbDAOException("Unable to Update ParameterRatingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the ParameterRatingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}
}
