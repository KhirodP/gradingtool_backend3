package com.pwc.grading.masterdata.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.geolocation.dao.GeoLocationQueryConstants;
import com.pwc.grading.masterdata.dao.IMasterDataManagmentDao;
import com.pwc.grading.masterdata.dao.MasterDataManagmentDaoConstant;
import com.pwc.grading.masterdata.dao.MasterDatatQueryConstants;
import com.pwc.grading.masterdata.dao.exception.MasterDataManagmentDaoException;
import com.pwc.grading.masterdata.model.GradingEnable;
import com.pwc.grading.masterdata.model.ProjectMapping;
import com.pwc.grading.partner.model.PartnerDetails;
import com.pwc.grading.partner.model.TrainingCenterDetails;

/**
 * Implementation class for {@link IMasterDataManagmentDao} 
 *
 */
@Singleton
public class MasterDataManagmentDaoImpl implements IMasterDataManagmentDao {
	private static final Logger logger = LoggerFactory.getLogger(MasterDataManagmentDaoImpl.class);

	/**
	 * This method is used to add partner.
	 * @param tenantId the database name.
	 * @param partner the partner details
	 * @return the response created by this method.
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public String addPartner(String databaseName, PartnerDetails partner) throws MasterDataManagmentDaoException {
		logger.debug(".in add partner, partner is: " + StringEscapeUtils.escapeJava(partner.toString()));
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.INSERT_PARTNER
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			String partnerId = UUID.randomUUID().toString();
			prepareStatement.setString(1, partnerId);
			prepareStatement.setString(2, partner.getPartnerName());
			prepareStatement.setString(3, partner.getClientSponsorId());
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " partner created successfully");
			return partnerId;
		} catch (Exception e) {
			logger.error("unable to add partner " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to add partner " + e.getMessage());
		} finally {
			logger.debug("closing the parnter connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * This method is used to add Partner.
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param partner the partner details
	 * @return the partner Id.
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public String addPartner(Connection connection, String databaseName, PartnerDetails partner)
			throws MasterDataManagmentDaoException {
		logger.debug(".in add partner, partner is: " + partner);
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.INSERT_PARTNER
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			String partnerId = UUID.randomUUID().toString();
			prepareStatement.setString(1, partnerId);
			prepareStatement.setString(2, partner.getPartnerName());
			prepareStatement.setString(3, partner.getClientSponsorId());
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " partner created successfully");
			return partnerId;
		} catch (Exception e) {
			logger.error("unable to add partner " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to add partner " + e.getMessage());
		} finally {
			logger.debug("closing the parnter connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to add Training Center Details.
	 * @param tenantId the database name
	 * @param tcDetails the Training Center details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void addTrainingCenterDetails(String databaseName, TrainingCenterDetails tcDetails)
			throws MasterDataManagmentDaoException {
		logger.debug(".in add tcDetails, tcDetails is: " + StringEscapeUtils.escapeJava(tcDetails.toString()));
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.INSERT_TC_DETAIL
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, tcDetails.getPartnerId());
			prepareStatement.setString(2, tcDetails.getTcId());
			prepareStatement.setString(3, tcDetails.getTcName());
			prepareStatement.setString(4, tcDetails.getDistrict());
			prepareStatement.setString(5, tcDetails.getCenterAddress());
			prepareStatement.setString(6, tcDetails.getCenterInchargeId());
			prepareStatement.setString(7, tcDetails.getLatitude());
			prepareStatement.setString(8, tcDetails.getLongitude());
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " tcDetails created successfully");
		} catch (Exception e) {
			logger.error("unable to add tcDetails " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to add tcDetails " + e.getMessage());
		} finally {
			logger.debug("closing the tcDetails connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * This method is used to add Training Center Details
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param tcDetails the training center details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void addTrainingCenterDetails(Connection connection, String databaseName, TrainingCenterDetails tcDetails)
			throws MasterDataManagmentDaoException {
		logger.debug(".in add tcDetails, tcDetails is: " + tcDetails + " connection :" + connection.hashCode());
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.INSERT_TC_DETAIL
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, tcDetails.getPartnerId());
			prepareStatement.setString(2, tcDetails.getTcId());
			prepareStatement.setString(3, tcDetails.getTcName());
			prepareStatement.setString(4, tcDetails.getDistrict());
			prepareStatement.setString(5, tcDetails.getCenterAddress());
			prepareStatement.setString(6, tcDetails.getCenterInchargeId());
			String latitude = tcDetails.getLatitude();
			prepareStatement.setString(7, latitude);
			String longitude = tcDetails.getLongitude();
			prepareStatement.setString(8, longitude);
			prepareStatement.setString(9, getGeoLocationString(latitude, longitude));
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " tcDetails created successfully");
		} catch (Exception e) {
			logger.error("unable to add tcDetails " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to add tcDetails " + e.getMessage());
		} finally {
			logger.debug("closing the tcDetails connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	private String getGeoLocationString(String latitude, String longitude) throws SQLServerException {
		double dlatitude = Double.parseDouble(latitude);
		double dlongitude = Double.parseDouble(longitude);
		Geography geography = Geography.point(dlatitude, dlongitude, GeoLocationQueryConstants.SRID_FOR_GEOGRAPHY);
		return geography.toString();
	}

	/**
	 * This method is used to add the project mapping .
	 * @param databaseName the database name the database name
	 * @param mapping the project mapping details the mapping details.
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void addProjectMapping(String databaseName, ProjectMapping mapping) throws MasterDataManagmentDaoException {
		logger.debug(".in addProjectMapping, project mapping is: " + StringEscapeUtils.escapeJava(mapping.toString()));
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.INSERT_MAPPING_DATA
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			String projectMappingId = UUID.randomUUID().toString();
			prepareStatement.setString(1, projectMappingId);
			prepareStatement.setString(2, mapping.getPartnerProjectId());
			prepareStatement.setString(4, mapping.getTcId());
			prepareStatement.setString(5, mapping.getProjectId());
			prepareStatement.setString(6, mapping.getPartnerId());
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " project mapping created successfully");
		} catch (Exception e) {
			logger.error("unable to add project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to add project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

	}

	/**
	 * This method is used to add Project Mapping
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param mapping the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void addProjectMapping(Connection connection, String databaseName, ProjectMapping mapping)
			throws MasterDataManagmentDaoException {
		logger.debug(".in addProjectMapping, project mapping is: " + mapping + " connection :" + connection.hashCode());
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.INSERT_MAPPING_DATA
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			String projectMappingId = UUID.randomUUID().toString();
			prepareStatement.setString(1, projectMappingId);
			prepareStatement.setString(2, mapping.getPartnerProjectId());
			prepareStatement.setString(3, mapping.getTcId());
			prepareStatement.setString(4, mapping.getProjectId());
			prepareStatement.setString(5, mapping.getPartnerId());
//			prepareStatement.setBoolean(6, mapping.isGradingEnable());
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " project mapping created successfully");
		} catch (Exception e) {
			logger.error("unable to add project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to add project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to get Project Mapping By TcId
	 * @param databaseName the database name the database name
	 * @param tcId the Training Center Id.
	 * @return list of project mapping details.
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<ProjectMapping> getProjectMappingByTcId(String databaseName, String tcId)
			throws MasterDataManagmentDaoException {
		List<ProjectMapping> listOfPrjectMapping = new ArrayList<ProjectMapping>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_MAPPING_DATA_BY_TC_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, tcId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				logger.debug("tcId exist");
				ProjectMapping mapping = buildProjectMappingByResultSet(resultSet);
				listOfPrjectMapping.add(mapping);
			}
		} catch (Exception e) {
			logger.error("unable to get project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfPrjectMapping;
	}

	private ProjectMapping buildProjectMappingByResultSet(ResultSet resultSet) throws SQLException {
		ProjectMapping projectMapping = new ProjectMapping();
		projectMapping.setProjectMappingId(resultSet.getString(MasterDataManagmentDaoConstant.PROJECT_MAPPING_ID));
		projectMapping.setPartnerProjectId(resultSet.getString(MasterDataManagmentDaoConstant.PARTNER_PROJECT_ID));
		projectMapping.setTcId(resultSet.getString(MasterDataManagmentDaoConstant.TC_ID));
		projectMapping.setProjectId(resultSet.getString(MasterDataManagmentDaoConstant.PROJECT_ID));
		projectMapping.setPartnerId(resultSet.getString(MasterDataManagmentDaoConstant.PARTNER_ID));
		return projectMapping;
	}

//	@Override
//	public List<ProjectMapping> getProjectMappingByFieldAuditorId(String databaseName, String fieldAuditorId)
//			throws MasterDataManagmentDaoException {
//		List<ProjectMapping> listOfPrjectMapping = new ArrayList<ProjectMapping>();
//		Connection connection = null;
//		PreparedStatement prepareStatement = null;
//		try {
//			connection = MSSqlServerUtill.getConnection();
//			prepareStatement = connection
//					.prepareStatement(MasterDatatQueryConstants.SELECT_MAPPING_DATA_BY_FIELD_AUDITOR_ID
//							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
//			prepareStatement.setString(1, fieldAuditorId);
//			ResultSet resultSet = prepareStatement.executeQuery();
//			while (resultSet.next()) {
//				ProjectMapping mapping = buildProjectMappingByResultSet(resultSet);
//				listOfPrjectMapping.add(mapping);
//			}
//
//		} catch (Exception e) {
//			logger.error("unable to get project mapping " + e.getMessage());
//			throw new MasterDataManagmentDaoException("unable to get project mapping " + e.getMessage());
//		} finally {
//			logger.debug("closing the project mapping connections");
//			MSSqlServerUtill.close(prepareStatement, connection);
//		}
//		return listOfPrjectMapping;
//	}

//	@Override
//	public PartnerDetails getPartnerById(String databaseName, String partnerId) throws MasterDataManagmentDaoException {
//		Connection connection = null;
//		PreparedStatement prepareStatement = null;
//		ResultSet resultSet = null;
//		try {
//			connection = MSSqlServerUtill.getConnection();
//			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_PARTNER_BY_PARTNER_ID
//					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
//			prepareStatement.setString(1, partnerId);
//			resultSet = prepareStatement.executeQuery();
//			if (resultSet.next()) {
//				PartnerDetails partnerDetails = buildPartnerDetailsByResultSet(resultSet);
//				return partnerDetails;
//			}
//		} catch (Exception e) {
//			logger.error("unable to get Partner " + e.getMessage());
//			throw new MasterDataManagmentDaoException("unable to get Partner " + e.getMessage());
//		} finally {
//			logger.debug("closing the Partner connections");
//			MSSqlServerUtill.close(prepareStatement, connection);
//		}
//		return null;
//	}

	private PartnerDetails buildPartnerDetailsByResultSet(ResultSet resultSet) throws SQLException {
		PartnerDetails partnerDetails = new PartnerDetails();
		String partnerId = resultSet.getString(MasterDataManagmentDaoConstant.PARTNER_ID);
		String partnerName = resultSet.getString(MasterDataManagmentDaoConstant.PARTNER_NAME);
		String clientSponsorId = resultSet.getString(MasterDataManagmentDaoConstant.CLIENT_SPONSOR_ID);
		partnerDetails.setPartnerId(partnerId);
		partnerDetails.setPartnerName(partnerName);
		partnerDetails.setClientSponsorId(clientSponsorId);
		return partnerDetails;
	}

	/**
	 * This method is used to update Partner 
	 * @param connection if this operation is to be performed in single transaction if this operation is to be performed in single transaction.
	 * @param databaseName the database name the database name
	 * @param partnerId the id of the partner the partner Id 
	 * @param partnerDetail the partner details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updatePatnerById(Connection connection, String databaseName, String partnerId,
			PartnerDetails partnerDetail) throws MasterDataManagmentDaoException {

	}

	/**
	 * This method is used to get Project Mapping Data.
	 * @param tenantId the database name
	 * @return  list of project mapping details.
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<ProjectMapping> getProjectMappingData(String databaseName) throws MasterDataManagmentDaoException {
		logger.debug("in getProjectMappingData DB is:: " + databaseName);
		List<ProjectMapping> listOfPrjectMapping = new ArrayList<ProjectMapping>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_MAPPING_DATA
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				ProjectMapping mapping = buildProjectMappingByResultSet(resultSet);
				logger.debug("mapping ::" + mapping);
				listOfPrjectMapping.add(mapping);
			}

		} catch (Exception e) {
			logger.error("unable to get project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfPrjectMapping;
	}

	/**
	 * This method is used to get Project Mapping Data.
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @return all the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<ProjectMapping> getProjectMappingData(Connection connection, String databaseName)
			throws MasterDataManagmentDaoException {
		List<ProjectMapping> listOfPrjectMapping = new ArrayList<ProjectMapping>();
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_MAPPING_DATA
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				ProjectMapping mapping = buildProjectMappingByResultSet(resultSet);
				listOfPrjectMapping.add(mapping);
			}

		} catch (Exception e) {
			logger.error("unable to get project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return listOfPrjectMapping;
	}

	/**
	 * This method is used to get the Project Mapping Data By ParterProjectId
	 * @param tenantId the database name
	 * @param parternProjectId the ParterProjectId.
	 * @return list of project mapping details.
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */ 
	@Override
	public List<ProjectMapping> getProjectMappingDataByParterProjectId(String databaseName, String parternProjectId)
			throws MasterDataManagmentDaoException {
		List<ProjectMapping> listOfPrjectMapping = new ArrayList<ProjectMapping>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.SELECT_MAPPING_DATA_BY_PARTNER_PROJECT_ID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, parternProjectId);
			ResultSet resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				ProjectMapping mapping = buildProjectMappingByResultSet(resultSet);
				listOfPrjectMapping.add(mapping);
			}

		} catch (Exception e) {
			logger.error("unable to get project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
		return listOfPrjectMapping;
	}

	private TrainingCenterDetails buildTrainingCenterDetailsByResultSet(ResultSet resultSet) throws SQLException {
		TrainingCenterDetails tcDetails = new TrainingCenterDetails();
		String partnerId = resultSet.getString(MasterDataManagmentDaoConstant.PARTNER_ID);
		String tcId = resultSet.getString(MasterDataManagmentDaoConstant.TC_ID);
		String tcName = resultSet.getString(MasterDataManagmentDaoConstant.TC_NAME);
		String district = resultSet.getString(MasterDataManagmentDaoConstant.DISTRICT);
		String centerAddress = resultSet.getString(MasterDataManagmentDaoConstant.CENTER_ADDRESS);
		String centerInchargeId = resultSet.getString(MasterDataManagmentDaoConstant.CENTER_INCHARGE_ID);
		String latitude = resultSet.getString(MasterDataManagmentDaoConstant.LATITUDE);
		String longitude = resultSet.getString(MasterDataManagmentDaoConstant.LONGITUDE);

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

	/**
	 * This method is used to delete Mapping Data By mapping Id
	 * @param connection if this operation is to be performed in single transaction
	 * @param tenantId the database name
	 * @param mappingId the project mapping id
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void deleteMappingDataById(Connection connection, String databaseName, String mappingId)
			throws MasterDataManagmentDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.DELETE_PROJECT_MAPPING_BY_MAPPING_ID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, mappingId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " project mapping row deleted");

		} catch (Exception e) {
			logger.error("unable to get project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to update the Project Mapping
	 * @param databaseName the database name
	 * @param mappingId the project mapping id
	 * @param mapping the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateProjectMapping(String databaseName, String mappingId, ProjectMapping mapping)
			throws MasterDataManagmentDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.UPDATE_MAPPING_DATA_BY_PROJECT_MAPPING_ID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, mapping.getPartnerProjectId());
			prepareStatement.setString(2, mapping.getTcId());
			prepareStatement.setString(3, mapping.getProjectId());
			prepareStatement.setString(4, mapping.getPartnerId());
			prepareStatement.setString(5, mappingId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + "row updated in  project mapping");

		} catch (Exception e) {
			logger.error("unable to update project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to update project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * This method is used to update the Project Mapping.
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param mappingId the project mapping id
	 * @param mapping the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateProjectMapping(Connection connection, String databaseName, String mappingId,
			ProjectMapping mapping) throws MasterDataManagmentDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.UPDATE_MAPPING_DATA_BY_PROJECT_MAPPING_ID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, mapping.getPartnerProjectId());
			prepareStatement.setString(2, mapping.getTcId());
			prepareStatement.setString(3, mapping.getProjectId());
			prepareStatement.setString(4, mapping.getPartnerId());
			prepareStatement.setString(5, mappingId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + "row updated in  project mapping");

		} catch (Exception e) {
			logger.error("unable to update project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to update project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to get ProjectMapping By mapping Id
	 * @param databaseName the database name
	 * @param id the project mapping id
	 * @return the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public ProjectMapping getProjectMappingById(String databaseName, String id) throws MasterDataManagmentDaoException {
		logger.debug("in getProjectMappingById ,databaseName:" + databaseName + " id is ::" + id);
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.SELECT_MAPPING_DATA_BY_PROJECT_MAPPING_ID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, id);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("mapping exist");
				ProjectMapping mapping = buildProjectMappingByResultSet(resultSet);
				logger.debug("mapping is ::" + mapping);
				return mapping;
			}

		} catch (Exception e) {
			logger.error("unable to get project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

//	@Override
//	public TrainingCenterDetails getTrainingCenterDetailsById(String databaseName, String tcId)
//			throws MasterDataManagmentDaoException {
//		Connection connection = null;
//		PreparedStatement prepareStatement = null;
//		ResultSet resultSet = null;
//		try {
//			connection = MSSqlServerUtill.getConnection();
//			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_TC_BY_TC_ID
//					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
//			prepareStatement.setString(1, tcId);
//			resultSet = prepareStatement.executeQuery();
//			if (resultSet.next()) {
//				TrainingCenterDetails tcDetails = buildTrainingCenterDetailsByResultSet(resultSet);
//				return tcDetails;
//			}
//		} catch (Exception e) {
//			logger.error("unable to get TrainingCenterDetails " + e.getMessage());
//			throw new MasterDataManagmentDaoException("unable to get TrainingCenterDetails " + e.getMessage());
//		} finally {
//			logger.debug("closing the TrainingCenterDetails connections");
//			MSSqlServerUtill.close(prepareStatement, connection);
//		}
//		return null;
//	}

	/**
	 * This method is used to get Project Mapping By ProjectId.
	 * @param databaseName the database name
	 * @param projectId the id of the project the id of the project
	 * @return all the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<ProjectMapping> getProjectMappingByProjectId(String databaseName, String projectId)
			throws MasterDataManagmentDaoException {
		List<ProjectMapping> listOfPrjectMapping = new ArrayList<ProjectMapping>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_MAPPING_DATA_BY_PROJECT_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				ProjectMapping mapping = buildProjectMappingByResultSet(resultSet);
				listOfPrjectMapping.add(mapping);
			}

		} catch (Exception e) {
			logger.error("unable to get project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfPrjectMapping;
	}

	/**
	 * This method is used to get Project Mapping By ProjectId.
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param projectId the id of the project
	 * @return the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<ProjectMapping> getProjectMappingByProjectId(Connection connection, String databaseName,
			String projectId) throws MasterDataManagmentDaoException {
		List<ProjectMapping> listOfPrjectMapping = new ArrayList<ProjectMapping>();
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_MAPPING_DATA_BY_PROJECT_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				ProjectMapping mapping = buildProjectMappingByResultSet(resultSet);
				listOfPrjectMapping.add(mapping);
			}

		} catch (Exception e) {
			logger.error("unable to get project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return listOfPrjectMapping;
	}

//	@Override
//	public long getPartnerCountByProjectId(String databaseName, String projectId)
//			throws MasterDataManagmentDaoException {
//		Connection connection = null;
//		PreparedStatement prepareStatement = null;
//		ResultSet resultSet = null;
//		try {
//			connection = MSSqlServerUtill.getConnection();
//			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_PARTNER_COUNT_BY_PROJECT_ID
//					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
//			prepareStatement.setString(1, projectId);
//			resultSet = prepareStatement.executeQuery();
//			if (resultSet.next()) {
//				long count = resultSet.getLong(1);
//				return count;
//			}
//
//		} catch (Exception e) {
//			logger.error("unable to get partner count  " + e.getMessage());
//			throw new MasterDataManagmentDaoException("unable to get partner count " + e.getMessage());
//		} finally {
//			logger.debug("closing the project mapping connections");
//			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
//		}
//		return 0;
//	}

	/**
	 * This method is used to get All Training Center Details By PartnerId.
	 * @param databaseName the database name
	 * @param partnerId the id of the partner
	 * @return the training center details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<TrainingCenterDetails> getAllTrainingCenterDetailsByPartnerId(String databaseName, String partnerId)
			throws MasterDataManagmentDaoException {
		List<TrainingCenterDetails> listOfTc = new ArrayList<TrainingCenterDetails>();
		PreparedStatement statement = null;
		Connection connection = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			statement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_TC_BY_PARTNER_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			statement.setString(1, partnerId);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				TrainingCenterDetails partnerDetails = buildTrainingCenterDetailsByResultSet(resultSet);
				listOfTc.add(partnerDetails);
			}
		} catch (Exception e) {
			logger.error("unable to get TcDetails " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get TcDetails " + e.getMessage());
		} finally {
			logger.debug("closing the TcDetails connections");
			MSSqlServerUtill.close(resultSet, statement, connection);
		}
		return listOfTc;
	}

	/**
	 * This method is used to get project mapping by partner id, tc id and project id.
	 * @param databaseName the database name
	 * @param partnerId the id of the partner
	 * @param tcId the id of the training center
	 * @param projectId the id of the project the id of the project
	 * @return the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public ProjectMapping getProjectMappingByPartnerIdTcIdAndProjectId(String databaseName, String partnerId,
			String tcId, String projectId) throws MasterDataManagmentDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.SELECT_MAPPING_DATA_BY_TCID_AND_PROJECTID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, partnerId);
			prepareStatement.setString(2, tcId);
			prepareStatement.setString(3, projectId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				ProjectMapping mapping = buildProjectMappingByResultSet(resultSet);
				return mapping;
			}

		} catch (Exception e) {
			logger.error("unable to get project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

	/**
	 * This method is used to get project mapping by project id and partner id.
	 * @param databaseName the database name
	 * @param projectId the id of the project the id of the project
	 * @param partnerId the id of the partner
	 * @return all the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<ProjectMapping> getProjectMappingByProjectIdAndPartnerId(String databaseName, String projectId,
			String partnerId) throws MasterDataManagmentDaoException {
		List<ProjectMapping> listOfPrjectMapping = new ArrayList<ProjectMapping>();
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.SELECT_MAPPING_DATA_BY_PROJECT_ID_AND_PARTNER_ID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				ProjectMapping mapping = buildProjectMappingByResultSet(resultSet);
				listOfPrjectMapping.add(mapping);
			}

		} catch (Exception e) {
			logger.error("unable to get project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return listOfPrjectMapping;
	}

	/**
	 * This method is used to get Partner By Partner Name With Case InSensitive
	 * @param databaseName the database name the database name
	 * @param partnerName the partner Name
	 * @return the partner details 
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public PartnerDetails getPartnerByPartnerNameWithCaseInSensitive(String databaseName, String partnerName)
			throws MasterDataManagmentDaoException {
		// TODO Auto-generated method stub
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_PARTNER_BY_PARTNER_NAME
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, partnerName.toLowerCase());
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				PartnerDetails partnerDetails = buildPartnerDetailsByResultSet(resultSet);
				return partnerDetails;
			}
		} catch (Exception e) {
			logger.error("unable to get Partner " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get Partner " + e.getMessage());
		} finally {
			logger.debug("closing the Partner connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
		return null;
	}

	/**
	 * This method is used to get Partner By partner name With Case InSensitive
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param partnerName the name of the partner
	 * @return the partner details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public PartnerDetails getPartnerByPartnerNameWithCaseInSensitive(Connection connection, String databaseName,
			String partnerName) throws MasterDataManagmentDaoException {
		// TODO Auto-generated method stub
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_PARTNER_BY_PARTNER_NAME
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, partnerName.toLowerCase());
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				PartnerDetails partnerDetails = buildPartnerDetailsByResultSet(resultSet);
				return partnerDetails;
			}
		} catch (Exception e) {
			logger.error("unable to get Partner " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get Partner " + e.getMessage());
		} finally {
			logger.debug("closing the Partner connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
		return null;
	}

	/**
	 * This method is used to add GradingEnable data.
	 * @param connection if this operation is to be performed in single transaction
	 * @param tenantId the database name
	 * @param gradingEnable the grading enable details 
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void addGradingEnable(Connection connection, String databaseName, GradingEnable gradingEnable)
			throws MasterDataManagmentDaoException {
		logger.debug(".in addGradingEnable :gradingEnable " + gradingEnable);
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.INSERT_GRADING_ENABLE
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, gradingEnable.getProjectId());
			prepareStatement.setString(2, gradingEnable.getPartnerId());
			prepareStatement.setBoolean(3, gradingEnable.isGradingEnable());
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " row added in GradingEnable");
		} catch (Exception e) {
			logger.error("unable to add GradingEnable " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to add GradingEnable " + e.getMessage());
		} finally {
			logger.debug("closing the parnter connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to get grading enable by project id and partner id.
	 * @param connection if this operation is to be performed in single transaction
	 * @param tenantId the database name
	 * @param projectId the id of the project the id of the project
	 * @param partnerId the id of the partner
	 * @return the grading enable details 
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public GradingEnable getGradingEnableByProjectIdAndPartnerId(Connection connection, String databaseName,
			String projectId, String partnerId) throws MasterDataManagmentDaoException {
		logger.debug(".in get GradingEnable By ProjectId And PartnerId :");
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.SELECT_GRADING_ENABLE_WHERE_PROJECTID_AND_PARTNERID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			ResultSet executeQuery = prepareStatement.executeQuery();
			if (executeQuery.next()) {

				GradingEnable gradingEnable = buildGradingEnableFromResultSet(executeQuery);
				logger.debug("gradingEnable exist ::" + gradingEnable);
				return gradingEnable;
			}
		} catch (Exception e) {
			logger.error("unable to get GradingEnable " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get GradingEnable " + e.getMessage());
		} finally {
			logger.debug("closing the GradingEnable connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
		logger.debug("gradingEnable does not exist");
		return null;
	}

	/**
	 * This method is used to get Grading Enable By ProjectId And PartnerId.
	 * @param databaseName the database name
	 * @param projectId the id of the project
	 * @param partnerId the id of the partner
	 * @return  the grading enable details 
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public GradingEnable getGradingEnableByProjectIdAndPartnerId(String databaseName, String projectId,
			String partnerId) throws MasterDataManagmentDaoException {
		logger.debug(".in get GradingEnable By ProjectId And PartnerId :");
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.SELECT_GRADING_ENABLE_WHERE_PROJECTID_AND_PARTNERID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			ResultSet executeQuery = prepareStatement.executeQuery();
			if (executeQuery.next()) {
				GradingEnable gradingEnable = buildGradingEnableFromResultSet(executeQuery);
				logger.debug("gradingEnable exist ::" + gradingEnable);
				return gradingEnable;
			}
		} catch (Exception e) {
			logger.error("unable to get GradingEnable " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get GradingEnable " + e.getMessage());
		} finally {
			logger.debug("closing the GradingEnable connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
		logger.debug("gradingEnable does not exist");
		return null;
	}

	private GradingEnable buildGradingEnableFromResultSet(ResultSet executeQuery) throws SQLException {
		String projectId = executeQuery.getString(1);
		boolean isGradingEnable = executeQuery.getBoolean(3);
		String partnerId = executeQuery.getString(2);
		GradingEnable gradingEnable = new GradingEnable(projectId, partnerId, isGradingEnable);
		return gradingEnable;
	}

	/**
	 * This method is used to update Grading Enable.
	 * @param connection if this operation is to be performed in single transaction 
	 * @param tenantId the database name
	 * @param isGradingEnable
	 * @param partnerId the id of the partner
	 * @param projectId the id of the project the id of the project
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateGradingEnable(Connection connection, String databaseName, boolean isGradingEnable,
			String partnerId, String projectId) throws MasterDataManagmentDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.UPDATE_GRADING_ENABLE_WHERE_PROJECTID_AND_PARTNERID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setBoolean(1, isGradingEnable);
			prepareStatement.setString(2, projectId);
			prepareStatement.setString(3, partnerId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + "row updated in  project mapping");

		} catch (Exception e) {
			logger.error("unable to update project mapping " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to update project mapping " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to get Grading Enable By Partner Id.
	 * @param databaseName the database name
	 * @param partnerId the id of the partner
	 * @return all the grading enable details 
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<GradingEnable> getGradingEnableByPartnerId(String databaseName, String partnerId)
			throws MasterDataManagmentDaoException {
		logger.debug(".in get GradingEnable By ProjectId And PartnerId :");
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		List<GradingEnable> listOfGradingEnable = new ArrayList<GradingEnable>();
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.SELECT_GRADING_ENABLE_WHERE_PARTNERID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, partnerId);
			ResultSet executeQuery = prepareStatement.executeQuery();
			while (executeQuery.next()) {
				GradingEnable gradingEnable = buildGradingEnableFromResultSet(executeQuery);
				logger.debug("gradingEnable exist ::" + gradingEnable);
				listOfGradingEnable.add(gradingEnable);
			}
		} catch (Exception e) {
			logger.error("unable to get GradingEnable " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get GradingEnable " + e.getMessage());
		} finally {
			logger.debug("closing the GradingEnable connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
		logger.debug("gradingEnable does not exist");
		return listOfGradingEnable;
	}

	/**
	 * This method is used to get Grading Enable By Project Id.
	 * @param databaseName the database name
	 * @param ProjectId the id of the project
	 * @return  the grading enable details 
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<GradingEnable> getGradingEnableByProjectId(String databaseName, String ProjectId)
			throws MasterDataManagmentDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		List<GradingEnable> listOfGradingEnable = new ArrayList<GradingEnable>();
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.SELECT_GRADING_ENABLE_WHERE_PROJECT_ID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, ProjectId);
			ResultSet executeQuery = prepareStatement.executeQuery();
			while (executeQuery.next()) {
				GradingEnable gradingEnable = buildGradingEnableFromResultSet(executeQuery);
				logger.debug("gradingEnable exist ::" + gradingEnable);
				listOfGradingEnable.add(gradingEnable);
			}
		} catch (Exception e) {
			logger.error("unable to get GradingEnable By ProjectId " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get GradingEnable By ProjectId " + e.getMessage());
		} finally {
			logger.debug("closing the GradingEnable connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
		logger.debug("gradingEnable does not exist");
		return listOfGradingEnable;

	}

	/**
	 * This method is used to get Partner Details By Project Id.
	 * @param databaseName the database name
	 * @param ProjectId the id of the project
	 * @return  the Partner Details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public List<PartnerDetails> getPartnerDetailsByProjectId(String databaseName, String ProjectId)
			throws MasterDataManagmentDaoException {
		logger.debug("in .getPartnerDetailsByProjectId :ProjectId :: " + ProjectId);
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		List<PartnerDetails> patnerDetails = new ArrayList<PartnerDetails>();
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(MasterDatatQueryConstants.SELECT_PARTNER_IDS_BY_PROJECT_ID
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, ProjectId);
			resultSet = prepareStatement.executeQuery();
			List<String> patnerIds = new ArrayList<String>();
			while (resultSet.next()) {
				String patnerId = resultSet.getString(1);
				patnerIds.add(patnerId);
			}
			prepareStatement.close();
			resultSet.close();
			String ids = getPatnerIdsInString(patnerIds);
			if (ids.isEmpty()) {
				return new ArrayList<PartnerDetails>();
			}
			logger.debug("ids :: " + ids);
			String query = MasterDatatQueryConstants.SELECT_PARTNERS_BY_PARTNER_IDS
					.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName).replace("?", ids);
			logger.debug("Query is :" + query);
			prepareStatement = connection.prepareStatement(query);
//			String[] partnerIds = ids.split(",");
//			Array partnerIdArray = connection.createArrayOf("partnerId", partnerIds);
//			prepareStatement.setArray(1, partnerIdArray);
//			prepareStatement.setString(1, ids);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				PartnerDetails partnerDetails = buildPartnerDetailsByResultSet(resultSet);
				patnerDetails.add(partnerDetails);
			}
			return patnerDetails;
		} catch (Exception e) {
			logger.error("unable to get Partner Details By ProjectId " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to get Partner Details By ProjectId " + e.getMessage());
		} finally {
			logger.debug("closing the GradingEnable connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}

	}

	private String getPatnerIdsInString(List<String> patnerIds) {
		StringBuilder ids = new StringBuilder();
//		ids.append("(");
		int size = patnerIds.size();
		int lastCount = size - 1;
		for (int i = 0; i < size; i++) {
			String id = patnerIds.get(i);
			ids.append("'");
			ids.append(id);
			ids.append("'");
			if (i != lastCount)
				ids.append(",");
		}
//		ids.append(")");
		return ids.toString();
	}

	/**
	 * This method is used to delete Grading Enable By PartnerId And ProjectId
	 * @param connection if this operation is to be performed in single transaction
	 * @param tenantId the database name
	 * @param partnerId the id of the partner
	 * @param projectId the id of the project the id of the project
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void deleteGradingEnableByPartnerIdAndProjectId(Connection connection, String databaseName, String partnerId,
			String projectId) throws MasterDataManagmentDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.DELETE_GRADING_ENABLE_WHERE_PROJECTID_AND_PARTNERID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			prepareStatement.setString(2, partnerId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " gradingEnable row deleted");
		} catch (Exception e) {
			logger.error("unable to delete gradingEnable " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to delete gradingEnable " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to delete all the project mappings for given ProjectId
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param projectId the id of the project the id of the project
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void deleteProjectMappingForProjectId(Connection connection, String databaseName, String projectId) throws MasterDataManagmentDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.DELETE_PROJECT_MAPPING_BY_PROJECT_ID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " ProjectMapping rows deleted");
		} catch (Exception e) {
			logger.error("Unable to delete mappings for projectId " + e.getMessage());
			throw new MasterDataManagmentDaoException("Unable to delete mappings for projectId '"+projectId+"', " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * This method is used to delete Grading Enable By ProjectId
	 * @param connection if this operation is to be performed in single transaction
	 * @param tenantId the database name
	 * @param projectId the id of the project the id of the project
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void deleteGradingEnableByProjectId(Connection connection, String databaseName, String projectId)
			throws MasterDataManagmentDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(MasterDatatQueryConstants.DELETE_GRADING_ENABLE_WHERE_PROJECTID
							.replace(MasterDatatQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, projectId);
			int count = prepareStatement.executeUpdate();
			logger.debug(count + " GradingEnable row deleted");
		} catch (Exception e) {
			logger.error("unable to delete gradingEnable " + e.getMessage());
			throw new MasterDataManagmentDaoException("unable to delete gradingEnable " + e.getMessage());
		} finally {
			logger.debug("closing the project mapping connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}	
	}
}
