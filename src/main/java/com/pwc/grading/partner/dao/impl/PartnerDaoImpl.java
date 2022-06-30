package com.pwc.grading.partner.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.masterdata.dao.MasterDatatQueryConstants;
import com.pwc.grading.partner.dao.IPartnerDao;
import com.pwc.grading.partner.dao.PartnerDaoConstant;
import com.pwc.grading.partner.dao.PartnerQueryConstants;
import com.pwc.grading.partner.dao.exception.PartnerDaoException;
import com.pwc.grading.partner.model.PartnerDetails;
import com.pwc.grading.partner.model.TrainingCenterDetails;

/**
 * Implementation class for {@link IPartnerDao} 
 *
 */
@Singleton
public class PartnerDaoImpl implements IPartnerDao {
	private static final Logger logger = LoggerFactory.getLogger(PartnerDaoImpl.class);

	/**
	 * Get all the partners.
	 * @param tenantId the database name.
	 * @return all the partner details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public List<PartnerDetails> getAllPartner(String databaseName) throws PartnerDaoException {
		List<PartnerDetails> listPartners = new ArrayList<PartnerDetails>();
		PreparedStatement statement = null;
		Connection connection = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			statement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_PARTNERS
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				PartnerDetails partnerDetails = buildPartnerDetailsByResultSet(resultSet);
				listPartners.add(partnerDetails);
			}
		} catch (Exception e) {
			logger.error("unable to get Partners " + e.getMessage());
			throw new PartnerDaoException("unable to get Partners " + e.getMessage());
		} finally {
			logger.debug("closing the Partner connections");
			MSSqlServerUtill.close(resultSet, statement, connection);
		}
		return listPartners;
	}

	/**
	 * Get all the training center belongs to a particular partner.
	 * @param tenantId the database name
	 * @param partnerId the partner id.
	 * @return all the training center details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public List<TrainingCenterDetails> getAllTrainingCenterDetailsByPartnerId(String databaseName, String partnerId)
			throws PartnerDaoException {
		logger.debug("in getAllTrainingCenterDetails ");
		List<TrainingCenterDetails> listOfTc = new ArrayList<TrainingCenterDetails>();
		PreparedStatement statement = null;
		Connection connection = null;
		ResultSet resultSet = null;
		try {
			logger.debug("getting the connection");
			connection = MSSqlServerUtill.getConnection();
			logger.debug("got the connection");
			logger.debug("executing the Query");
			statement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_TC_BY_PARTNER_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));

			statement.setString(1, partnerId);
			resultSet = statement.executeQuery();
			logger.debug("Query Executed");
			while (resultSet.next()) {
				TrainingCenterDetails tcDetails = buildTrainingCenterDetailsByResultSet(resultSet);
				listOfTc.add(tcDetails);
			}
			logger.debug("closing try block");
		} catch (Exception e) {
			logger.error("unable to get TcDetails  By partnerId " + e.getMessage());
			throw new PartnerDaoException("unable to get TcDetails  By partnerId ::" + e.getMessage());
		} finally {
			logger.debug("closing the TcDetails connections");
			MSSqlServerUtill.close(resultSet, statement, connection);
		}
		logger.debug("returning the response");
		return listOfTc;
	}

	/**
	 * Update the training center details
	 * @param connection if this operation to be performed in a single transaction.
	 * @param databaseName  the database name
	 * @param tcDetails the training center details to be updated.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public void updateTrainingCenterDetails(Connection connection, String databaseName, TrainingCenterDetails tcDetails)
			throws PartnerDaoException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.prepareStatement(PartnerQueryConstants.UPDATE_TC_BY_TC_ID
					.replace(PartnerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			statement.setString(1, tcDetails.getTcName());
			statement.setString(2, tcDetails.getDistrict());
			statement.setString(3, tcDetails.getCenterAddress());
			statement.setString(4, tcDetails.getCenterInchargeId());
			statement.setString(5, tcDetails.getLatitude());
			statement.setString(6, tcDetails.getLongitude());
			statement.setString(7, tcDetails.getTcId());
			statement.executeUpdate();
		} catch (Exception e) {
			logger.error("unable to update TcDetails  By tcid " + e.getMessage());
			throw new PartnerDaoException("unable to update TcDetails  By tcid ::" + e.getMessage());
		} finally {
			logger.debug("closing the TcDetails connections");
			MSSqlServerUtill.close(resultSet, statement, null);
		}
	}

	/**
	 * Get the partner details by the partner Id.
	 * @param databaseNamec
	 * @param partnerId the partner id.
	 * @return  all the partner details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public PartnerDetails getPartnerById(String databaseName, String partnerId) throws PartnerDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_PARTNER_BY_PARTNER_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, partnerId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				PartnerDetails partnerDetails = buildPartnerDetailsByResultSet(resultSet);
				return partnerDetails;
			}
		} catch (Exception e) {
			logger.error("unable to get Partner " + e.getMessage());
			throw new PartnerDaoException("unable to get Partner " + e.getMessage());
		} finally {
			logger.debug("closing the Partner connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
		return null;
	}

	/**
	 * Get the training center details by tcId and the partnerID
	 * @param databaseName the database name
	 * @param partnerId the partner id.
	 * @param tcId the training center id
	 * @return the training center details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public TrainingCenterDetails getTrainingCenterDetailsByTCIdAndPartnerId(String databaseName, String partnerId,
			String tcId) throws PartnerDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_TC_BY_TC_ID_AND_PARTNER_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, partnerId);
			prepareStatement.setString(2, tcId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				TrainingCenterDetails tcDetails = buildTrainingCenterDetailsByResultSet(resultSet);
				return tcDetails;
			}
		} catch (Exception e) {
			logger.error("unable to get TrainingCenterDetails " + e.getMessage());
			throw new PartnerDaoException("unable to get TrainingCenterDetails " + e.getMessage());
		} finally {
			logger.debug("closing the TrainingCenterDetails connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
		return null;
	}

//	@Override
//	public TrainingCenterDetails getTrainingCenterDetailsByTcId(String databaseName, String tcId) throws PartnerDaoException {
//		Connection connection = null;
//		PreparedStatement prepareStatement = null;
//		ResultSet resultSet = null;
//		try {
//			connection = MSSqlServerUtill.getConnection();
//			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_TC_BY_TC_ID_AND_PARTNER_ID
//					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
//			prepareStatement.setString(1, tcId);
//			prepareStatement.setString(2, tcId);
//			resultSet = prepareStatement.executeQuery();
//			if (resultSet.next()) {
//				TrainingCenterDetails tcDetails = buildTrainingCenterDetailsByResultSet(resultSet);
//				return tcDetails;
//			}
//		} catch (Exception e) {
//			logger.error("unable to get TrainingCenterDetails " + e.getMessage());
//			throw new PartnerDaoException("unable to get TrainingCenterDetails " + e.getMessage());
//		} finally {
//			logger.debug("closing the TrainingCenterDetails connections");
//			MSSqlServerUtill.close(prepareStatement, connection);
//		}
//		return null;
//	}

	private TrainingCenterDetails buildTrainingCenterDetailsByResultSet(ResultSet resultSet) throws SQLException {
		TrainingCenterDetails tcDetails = new TrainingCenterDetails();
		String partnerId = resultSet.getString(PartnerDaoConstant.PARTNER_ID);
		String tcId = resultSet.getString(PartnerDaoConstant.TC_ID);
		String tcName = resultSet.getString(PartnerDaoConstant.TC_NAME);
		String district = resultSet.getString(PartnerDaoConstant.DISTRICT);
		String centerAddress = resultSet.getString(PartnerDaoConstant.CENTER_ADDRESS);
		String centerInchargeId = resultSet.getString(PartnerDaoConstant.CENTER_INCHARGE_ID);
		String latitude = resultSet.getString(PartnerDaoConstant.LATITUDE);
		String longitude = resultSet.getString(PartnerDaoConstant.LONGITUDE);

		tcDetails.setPartnerId(partnerId);
		tcDetails.setTcId(tcId);
		tcDetails.setTcName(tcName);
		tcDetails.setCenterAddress(centerAddress);
		tcDetails.setDistrict(district);
		tcDetails.setCenterInchargeId(centerInchargeId);
		tcDetails.setLatitude(latitude);
		tcDetails.setLongitude(longitude);
		return tcDetails;
	}

	private PartnerDetails buildPartnerDetailsByResultSet(ResultSet resultSet) throws SQLException {
		PartnerDetails partnerDetails = new PartnerDetails();
		String partnerId = resultSet.getString(PartnerDaoConstant.PARTNER_ID);
		String partnerName = resultSet.getString(PartnerDaoConstant.PARTNER_NAME);
		String clientSponsorId = resultSet.getString(PartnerDaoConstant.CLIENT_SPONSOR_ID);
		partnerDetails.setPartnerId(partnerId);
		partnerDetails.setPartnerName(partnerName);
		partnerDetails.setClientSponsorId(clientSponsorId);
		return partnerDetails;
	}

	/**
	 * Get the training center details by tcId
	 * @param tenantId the database name
	 * @param tcId  the training center id
	 * @return the training center details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public TrainingCenterDetails getTrainingCenterDetailsByTcId(String databaseName, String tcId)
			throws PartnerDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_TC_BY_TC_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, tcId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				TrainingCenterDetails tcDetails = buildTrainingCenterDetailsByResultSet(resultSet);
				return tcDetails;
			}
		} catch (Exception e) {
			logger.error("unable to get TrainingCenterDetails " + e.getMessage());
			throw new PartnerDaoException("unable to get TrainingCenterDetails " + e.getMessage());
		} finally {
			logger.debug("closing the TrainingCenterDetails connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
		return null;
	}

	/**
	 * Get the training center details by tcId
	 * @param connection if this operation to be performed in a single transaction.
	 * @param tenantId the database name
	 * @param tcId  the training center id
	 * @return the training center details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public TrainingCenterDetails getTrainingCenterDetailsByTcId(Connection connection, String databaseName, String tcId)
			throws PartnerDaoException {
		logger.debug("connection " + connection);
		logger.debug("databaseName " + databaseName);
		logger.debug("tcId " + tcId);
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_TC_BY_TC_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, tcId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				TrainingCenterDetails tcDetails = buildTrainingCenterDetailsByResultSet(resultSet);
				return tcDetails;
			}
		} catch (Exception e) {
			logger.error("unable to get TrainingCenterDetails " + e.getMessage());
			throw new PartnerDaoException("unable to get TrainingCenterDetails " + e.getMessage());
		} finally {
			logger.debug("closing the TrainingCenterDetails connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
		return null;
	}

	/**
	 * Get the partner details by the partner Id.
	 * @param connection if this operation to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param partnerId the partner id.
	 * @return the partner details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public PartnerDetails getPartnerById(Connection connection, String databaseName, String partnerId)
			throws PartnerDaoException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_PARTNER_BY_PARTNER_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, partnerId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				PartnerDetails partnerDetails = buildPartnerDetailsByResultSet(resultSet);
				return partnerDetails;
			}
		} catch (Exception e) {
			logger.error("unable to get Partner " + e.getMessage());
			throw new PartnerDaoException("unable to get Partner " + e.getMessage());
		} finally {
			logger.debug("closing the Partner connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
		return null;
	}

	/**
	 * Update the Partner details by partnerId
	 * @param connection if this operation to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param clientSponsorId the id of the client sponsor
	 * @param partnerId the partner id.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public void updatePartnerById(Connection connection, String databaseName, String clientSponsorId, String partnerId)
			throws PartnerDaoException {
		logger.debug("in updatePartnerById ");
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(PartnerQueryConstants.UPDATE_PARTNER_BY_PARTNER_ID
					.replace(PartnerQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, clientSponsorId);
			prepareStatement.setString(2, partnerId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " rows updated in Partner Table");
		} catch (Exception e) {
			logger.error("unable to Update Partner " + e.getMessage());
			throw new PartnerDaoException("unable to Update Partner " + e.getMessage());
		} finally {
			logger.debug("closing the Partner connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * Get the Partner details by clientSponsorId
	 * @param connection if this operation to be performed in a single transaction.
	 * @param tanantId  the database name
	 * @param userId the id of the client sponsor
	 * @return the partner details.
	 * @throws PartnerDaoException  if any exception occurs while performing this operation.
	 */
	@Override
	public PartnerDetails getPartnerByClientSponsorId(Connection connection, String databaseName, String userId)
			throws PartnerDaoException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_PARTNER_BY_CLIENT_SPONSOR_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, userId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				PartnerDetails partnerDetails = buildPartnerDetailsByResultSet(resultSet);
				return partnerDetails;
			}
		} catch (Exception e) {
			logger.error("unable to get Partner " + e.getMessage());
			throw new PartnerDaoException("unable to get Partner " + e.getMessage());
		} finally {
			logger.debug("closing the Partner connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
		return null;
	}

	/**
	 * Get the Partner details by clientSponsorId
	 * @param tanantId  the database name
	 * @param userId the id of the client sponsor
	 * @return the partner details.
	 * @throws PartnerDaoException  if any exception occurs while performing this operation.
	 */
	@Override
	public PartnerDetails getPartnerByClientSponsorId(String databaseName, String userId) throws PartnerDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_PARTNER_BY_CLIENT_SPONSOR_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, userId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				PartnerDetails partnerDetails = buildPartnerDetailsByResultSet(resultSet);
				return partnerDetails;
			}
		} catch (Exception e) {
			logger.error("unable to get Partner " + e.getMessage());
			throw new PartnerDaoException("unable to get Partner " + e.getMessage());
		} finally {
			logger.debug("closing the Partner connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
		return null;
	}

	/**
	 * Get all the partners count.
	 * @param databaseName  the database name
	 * @return the count of partners.
	 * @throws PartnerDaoException  if any exception occurs while performing this operation.
	 */
	@Override
	public int getTotalPartnerCount(String databaseName) throws PartnerDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		int totalCountPatner = 0;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_TOATL_PARTNER_COUNT
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));

			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				totalCountPatner = resultSet.getInt(1);
			}
			logger.debug("totalCountPatner ::" + totalCountPatner);
		} catch (Exception e) {
			logger.error("unable to get Partners count " + e.getMessage());
			throw new PartnerDaoException("unable to get Partners count " + e.getMessage());
		} finally {
			logger.debug("closing the Partner connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
		return totalCountPatner;

	}

	/**
	 * Get all the training center details by the partnerid and projectId in mapping.
	 * @param tenantId the database name
	 * @param projectId the project id.
	 * @param partnerId the partner id.
	 * @return all the training center details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public List<TrainingCenterDetails> getAllTrainingCenterDetailsByPartnerIdAndProjectIdInMapping(String tenantId,
			String projectId, String partnerId) throws PartnerDaoException {
		List<String> listOfTcid = new ArrayList<String>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<TrainingCenterDetails> listOfTcs = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.SELECT_MAPPING_DATA_BY_PROJECT_ID_AND_PARTNER_ID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				String tcId = resultSet.getString(PartnerDaoConstant.TC_ID);
				listOfTcid.add(tcId);
			}
			prepareStatement.close();
			resultSet.close();
			String ids = getTcIdsInString(listOfTcid);
			if (ids.isEmpty()) {
				return new ArrayList<TrainingCenterDetails>();
			}
			logger.debug("ids :: " + ids);
			listOfTcs = new ArrayList<TrainingCenterDetails>();
			String query = MasterDatatQueryConstants.SELECT_TC_DETAILS_BY_TC_IDS
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId).replace("?", ids);
			logger.debug("Query is :" + query);
			prepareStatement = connection.prepareStatement(query);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				TrainingCenterDetails tcDetails = buildTrainingCenterDetailsByResultSet(resultSet);
				listOfTcs.add(tcDetails);
			}
		} catch (Exception e) {
			logger.error("unable to get project mapping " + e.getMessage());
			throw new PartnerDaoException("unable to get project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfTcs;
	}

	private String getTcIdsInString(List<String> listOfTcid) {
		StringBuilder ids = new StringBuilder();
//		ids.append("(");
		int size = listOfTcid.size();
		int lastCount = size - 1;
		for (int i = 0; i < size; i++) {
			String id = listOfTcid.get(i);
			ids.append("'");
			ids.append(id);
			ids.append("'");
			if (i != lastCount)
				ids.append(",");
		}
//		ids.append(")");
		return ids.toString();
	}

}
