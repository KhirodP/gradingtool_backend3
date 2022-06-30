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
import com.pwc.grading.reportingdb.dao.IPartnersReportingDAO;
import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.db.constant.ReportingDatabaseQueryConstants;
import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.model.PartnersReportingTable;
import com.pwc.grading.reportingdb.model.ReportDBClientSponsor;

/**
 * Implementation class for {@link IPartnersReportingDAO} 
 *
 */
//@Singleton
public class PartnersReportingDAOImpl implements IPartnersReportingDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(PartnersReportingDAOImpl.class);

	/**
	 * This method is used to add the partners reporting data into the PartnersReportingTable.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param partnersReportingObj object containing the details of PartnersReporting
	 * @return the partnerProjectId belongs to the created project and partner.
	 * @throws ReportingDbDAOException if any exception occurs when creating the entry.
	 */
	@Override
	public String addPartnersReportingData(Connection connection, String databaseName,
			PartnersReportingTable partnersReportingObj) throws ReportingDbDAOException {
		LOGGER.debug(".inside addPartnersReportingData(con) method of PartnersReportingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.INSERT_PARTNER_REPORTING
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			LOGGER.debug("PartnersReporting INSERT QUERY : " + insertQuery);
			prepareStatement.setString(1, partnersReportingObj.getPartnerProjectId());
			prepareStatement.setString(2, partnersReportingObj.getProjectId());
			prepareStatement.setString(3, partnersReportingObj.getProjectName());
			prepareStatement.setString(4, partnersReportingObj.getPartnerId());
			prepareStatement.setString(5, partnersReportingObj.getPiaName());
//			prepareStatement.setString(6, partnersReportingObj.getDistrict());
			prepareStatement.setString(6, partnersReportingObj.getHeadPersonnel());
			prepareStatement.setString(7, partnersReportingObj.getContact());
			prepareStatement.setString(8, partnersReportingObj.getHeadPersonEmail());
			prepareStatement.setDouble(9, partnersReportingObj.getCenterRating());
			prepareStatement.setDouble(10, partnersReportingObj.getProjectGrading());
			prepareStatement.setDouble(11, partnersReportingObj.getFinalPercentage());
			prepareStatement.setString(12, partnersReportingObj.getGrade());
			prepareStatement.setBoolean(13, partnersReportingObj.getStatus());
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " PartnersReportingData added successfully.");
			return partnersReportingObj.getPartnerProjectId(); // Returing Partner Project Id
		} catch (Exception e) {
			LOGGER.error("Unable to add PartnersReportingData into database" + e.getMessage());
			throw new ReportingDbDAOException("Unable to add PartnersReportingData into database" + e.getMessage());
		} finally {
			LOGGER.debug("closing the PartnersReportingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to update the details of Partners reporting data into the PartnersReportingTable
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param id the partnerProjectId
	 * @param partnersReportingObj object containing the details of PartnersReporting to be updated.
	 * @return  the partnerProjectId belongs to the updated project and partner.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	@Override
	public String updatePartnersReportingData(Connection connection, String databaseName, String partnerProjectId,
			PartnersReportingTable partnersReportingObj) throws ReportingDbDAOException {
		LOGGER.debug(".inside updatePartnersReportingData(con) method of PartnersReportingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection is : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.UPDATE_PARTNER_REPORTING
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
//			LOGGER.debug("PartnersReporting UPDATE QUERY : "+insertQuery);
			prepareStatement.setString(1, partnersReportingObj.getProjectName());
			prepareStatement.setString(2, partnersReportingObj.getPiaName());
//			prepareStatement.setString(3, partnersReportingObj.getDistrict());
			prepareStatement.setString(3, partnersReportingObj.getHeadPersonnel());
			prepareStatement.setString(4, partnersReportingObj.getContact());
			prepareStatement.setString(5, partnersReportingObj.getHeadPersonEmail());
			prepareStatement.setDouble(6, partnersReportingObj.getCenterRating());
			prepareStatement.setDouble(7, partnersReportingObj.getProjectGrading());
			prepareStatement.setDouble(8, partnersReportingObj.getFinalPercentage());
			prepareStatement.setString(9, partnersReportingObj.getGrade());
			prepareStatement.setBoolean(10, partnersReportingObj.getStatus());
			prepareStatement.setString(11, partnerProjectId);

			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " PartnersReportingData Updated successfully.");
			return partnerProjectId; // Returing Partner Project Id
		} catch (Exception e) {
			LOGGER.error("Unable to Update PartnersReportingData into database" + e.getMessage());
			throw new ReportingDbDAOException("Unable to Update PartnersReportingData into database" + e.getMessage());
		} finally {
			LOGGER.debug("closing the PartnersReportingData connections");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to delete the Partners Reporting data for the given project Id.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param projectId the id of the project.
	 * @throws ReportingDbDAOException if any exception occurs when deleting the entry.
	 */
	@Override
	public void deletePartnersReportingData(Connection connection, String databaseName, String projectId)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside deletePartnersReportingData(con) method of PartnersReportingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String selectQuery = ReportingDatabaseQueryConstants.DELETE_BY_PROJECTID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(selectQuery);
			prepareStatement.setString(1, projectId);
			int rowsDeleted = prepareStatement.executeUpdate();
			LOGGER.debug(rowsDeleted + " rows deleted from PartnersReportingTable for projectId '" + projectId + "'");
		} catch (Exception e) {
			LOGGER.error("Unable to Delete PartnersReportingData from database, " + e.getMessage());
			throw new ReportingDbDAOException(
					"Unable to Delete PartnersReportingData from database, " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the PartnersReportingData prepareStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to fetch the Partners Reporting data for the given projectId and the partnerId.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param projectId the id of the project.
	 * @param partnerId the id of the partner.
	 * @return object containing the details of PartnersReporting
	 * @throws ReportingDbDAOException if any exception occurs when fetching the entry.
	 */
	@Override
	public PartnersReportingTable getDataByProjectIdAndPartnerId(Connection connection, String databaseName,
			String projectId, String partnerId) throws ReportingDbDAOException {
		LOGGER.debug(".inside getDataByProjectIdAndPartnerId(con) method of PartnersReportingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection : "+connection);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_PR_BY_PROJECT_AND_PARTNER_ID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(selectQuery);
			// LOGGER.debug("PartnersReporting getDataByProjectIdAndPartnerId SELECT QUERY :
			// "+insertQuery);
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			resultSet = prepareStatement.executeQuery();
			PartnersReportingTable obj = getObjectFromResultSet(resultSet);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Unable to get PartnersReportingData from database, " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get PartnersReportingData from database, " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the PartnersReportingData resultSet, prepareStatement");
			ReportDBMSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
	}

	/**
	 * This method is used to fetch all the partners reporting data belonging to a particular project.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param projectId the id of the project.
	 * @return the {@link List} of {@link PartnersReportingTable} objects which is having the data.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the entries.
	 */
	@Override
	public List<PartnersReportingTable> getByProjectId(Connection connection, String databaseName, String projectId)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside getByProjectId(con) method of PartnersReportingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection : "+connection);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_PR_BY_PROJECT_ID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(selectQuery);
			// LOGGER.debug("PartnersReporting getDataByProjectIdAndPartnerId SELECT QUERY :
			// "+insertQuery);
			prepareStatement.setString(1, projectId);
			resultSet = prepareStatement.executeQuery();
			List<PartnersReportingTable> objList = getListFromResultSet(resultSet);
			return objList;

		} catch (Exception e) {
			LOGGER.error("Unable to get PartnersReportingData from database, " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get PartnersReportingData from database, " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the PartnersReportingData resultSet, prepareStatement");
			ReportDBMSSqlServerUtill.close(resultSet, prepareStatement, null);
		}

	}

	/**
	 * This method is used to fetch all the partners reporting data belonging to a particular Partner.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param partnerId the id of the partner.
	 * @return the {@link List} of {@link PartnersReportingTable} objects which is having the data.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the entries.
	 */
	@Override
	public List<PartnersReportingTable> getByPartnerId(Connection connection, String databaseName, String partnerId)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside getByPartnerId(con) method of PartnersReportingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection : "+connection);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
//			connection = ReportDBMSSqlServerUtill.getConnection();
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_PR_BY_PARTNER_ID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(selectQuery);
			// LOGGER.debug("PartnersReporting getDataByProjectIdAndPartnerId SELECT QUERY :
			// "+insertQuery);
			prepareStatement.setString(1, partnerId);
			resultSet = prepareStatement.executeQuery();
			List<PartnersReportingTable> objList = getListFromResultSet(resultSet);
			return objList;

		} catch (Exception e) {
			LOGGER.error("Unable to get PartnersReportingData from database, " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get PartnersReportingData from database, " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the PartnersReportingData resultSet, prepareStatement");
			ReportDBMSSqlServerUtill.close(resultSet, prepareStatement, null);
		}

	}

	/**
	 * This method is used to fetch the partnerProjectIds for the given projectId.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param projectId the id of the project.
	 * @return the list of String which contains the PartnerProjectIds.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the partnerProjectIds.
	 */
	@Override
	public List<String> getPartnerProjectIdsForProjectId(Connection connection, String databaseName, String projectId)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside getPartnerProjectIdsForProjectId(con) method of PartnersReportingDAOImpl class.");
//		LOGGER.debug("Incoming DAO connection : "+connection);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<String> partnerProjectIdList = new ArrayList<String>();
		try {
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_PPID_FOR_PROJECTID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(selectQuery);
			// LOGGER.debug("PartnersReporting getDataByProjectIdAndPartnerId SELECT QUERY :
			// "+insertQuery);
			prepareStatement.setString(1, projectId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				partnerProjectIdList.add(resultSet.getString(ReportingDBTableConstant.PARTNER_PROJECT_ID));
			}
			return partnerProjectIdList;

		} catch (Exception e) {
			LOGGER.error("Unable to get PartnerprojectId from database, " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get PartnerprojectId from database, " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the PartnersReportingData resultSet, prepareStatement");
			ReportDBMSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
	}

	/**
	 * This method is used to update the client sponsor details for the particular partner.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param obj the object containing the details of partner and the client sponsor.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	@Override
	public void updateCSDetails(Connection connection, String databaseName, ReportDBClientSponsor obj)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside updateCSDetails(con) method of PartnersReportingDAOImpl class.");
//		LOGGER.debug("Incoming DAO Connection : "+connection);
		PreparedStatement prepareStatement = null;
		try {
			String insertQuery = ReportingDatabaseQueryConstants.UPDATE_CS_BY_PROJECT_AND_PARTNER_ID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
//			LOGGER.debug("PartnersReporting UPDATE CS QUERY : "+insertQuery);
			prepareStatement.setString(1, obj.getCsName());
			prepareStatement.setString(2, obj.getCsPhone());
			prepareStatement.setString(3, obj.getCsEmail());
			prepareStatement.setString(4, obj.getProjectId());
			prepareStatement.setString(5, obj.getPartnerId());
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " PartnersReporting Client-Sponsor Updated successfully.");
		} catch (Exception e) {
			LOGGER.error("Unable to Update Client-Sponsor of PartnersReportingData " + e.getMessage());
			throw new ReportingDbDAOException(
					"Unable to Update Client-Sponsor of PartnersReportingData " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the PartnersReportingData preparedStatement");
			ReportDBMSSqlServerUtill.close(prepareStatement, null);
		}

	}

	private PartnersReportingTable getObjectFromResultSet(ResultSet resultSet) throws SQLException {
		PartnersReportingTable object = null;
		while (resultSet.next()) {
			object = new PartnersReportingTable();
			object.setPartnerProjectId(resultSet.getString(ReportingDBTableConstant.PARTNER_PROJECT_ID));
			object.setProjectId(resultSet.getString(ReportingDBTableConstant.PROJECT_ID));
			object.setProjectName(resultSet.getString(ReportingDBTableConstant.PROJECT_NAME));
			object.setPartnerId(resultSet.getString(ReportingDBTableConstant.PARTNER_ID));
			object.setPiaName(resultSet.getString(ReportingDBTableConstant.PIA_NAME));
//			object.setDistrict(resultSet.getString(ReportingDBTableConstant.DISTRICT));
			object.setHeadPersonnel(resultSet.getString(ReportingDBTableConstant.HEAD_PERSONNEL));
			object.setContact(resultSet.getString(ReportingDBTableConstant.CONTACT));
			object.setHeadPersonEmail(resultSet.getString(ReportingDBTableConstant.HEAD_PERSON_EMAIL));
			object.setCenterRating(resultSet.getDouble(ReportingDBTableConstant.CENTER_RATING));
			object.setProjectGrading(resultSet.getDouble(ReportingDBTableConstant.PROJECT_GRADING));
			object.setFinalPercentage(resultSet.getDouble(ReportingDBTableConstant.FINAL_PERCENTAGE));
			object.setGrade(resultSet.getString(ReportingDBTableConstant.GRADE));
			object.setStatus(resultSet.getBoolean(ReportingDBTableConstant.STATUS));
		}
		return object;
	}

	private List<PartnersReportingTable> getListFromResultSet(ResultSet resultSet) throws SQLException {
		List<PartnersReportingTable> list = new ArrayList<PartnersReportingTable>();
		while (resultSet.next()) {
			PartnersReportingTable object = new PartnersReportingTable();
			object.setPartnerProjectId(resultSet.getString(ReportingDBTableConstant.PARTNER_PROJECT_ID));
			object.setProjectId(resultSet.getString(ReportingDBTableConstant.PROJECT_ID));
			object.setProjectName(resultSet.getString(ReportingDBTableConstant.PROJECT_NAME));
			object.setPartnerId(resultSet.getString(ReportingDBTableConstant.PARTNER_ID));
			object.setPiaName(resultSet.getString(ReportingDBTableConstant.PIA_NAME));
//			object.setDistrict(resultSet.getString(ReportingDBTableConstant.DISTRICT));
			object.setHeadPersonnel(resultSet.getString(ReportingDBTableConstant.HEAD_PERSONNEL));
			object.setContact(resultSet.getString(ReportingDBTableConstant.CONTACT));
			object.setHeadPersonEmail(resultSet.getString(ReportingDBTableConstant.HEAD_PERSON_EMAIL));
			object.setCenterRating(resultSet.getDouble(ReportingDBTableConstant.CENTER_RATING));
			object.setProjectGrading(resultSet.getDouble(ReportingDBTableConstant.PROJECT_GRADING));
			object.setFinalPercentage(resultSet.getDouble(ReportingDBTableConstant.FINAL_PERCENTAGE));
			object.setGrade(resultSet.getString(ReportingDBTableConstant.GRADE));
			object.setStatus(resultSet.getBoolean(ReportingDBTableConstant.STATUS));
			list.add(object);
		}
		return list;
	}

}
