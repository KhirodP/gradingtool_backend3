package com.pwc.grading.reportingdb.service.impl;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.ReportingDbJSONConstant;
import com.pwc.grading.reportingdb.dao.IFormGradingDAO;
import com.pwc.grading.reportingdb.dao.IParameterGradingDAO;
import com.pwc.grading.reportingdb.dao.IPartnersReportingDAO;
import com.pwc.grading.reportingdb.dao.impl.FormGradingDAOImpl;
import com.pwc.grading.reportingdb.dao.impl.ParameterGradingDAOImpl;
import com.pwc.grading.reportingdb.dao.impl.PartnersReportingDAOImpl;
import com.pwc.grading.reportingdb.model.PartnersReportingTable;
import com.pwc.grading.reportingdb.model.ReportDBClientSponsor;
import com.pwc.grading.reportingdb.service.IPartnersReportingService;
import com.pwc.grading.reportingdb.service.exp.ReportingDbServiceException;
import com.pwc.grading.reportingdb.util.PartnersReportingUtil;
import com.pwc.grading.util.JsonUtill;

/**
 * Implementation class for {@link IPartnersReportingService}
 *
 */
//@Singleton
public class PartnersReportingServiceImpl implements IPartnersReportingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PartnersReportingServiceImpl.class);

//	@Inject
	private IPartnersReportingDAO partnersReportingDAO;
	private IFormGradingDAO formGradingDao;
	private IParameterGradingDAO iParameterGradingDao;

	public PartnersReportingServiceImpl() {
		partnersReportingDAO = new PartnersReportingDAOImpl();
		formGradingDao = new FormGradingDAOImpl();
		iParameterGradingDao = new ParameterGradingDAOImpl();
	}

	/**
	 * This method is used to add the partners reporting data if entry not exists, if the entry already exists for
	 * the partner and project, it <strong>will not add a new entry again </strong>, but <strong>it will update the 
	 * existing entry.</strong>
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the details of PartnersReporting fields.
	 * @return the partnerProjectId belongs to the created project and partner.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public String addPartnersReportingData(Connection connection, String databaseName, String json)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside addPartnersReportingData(con) method of PartnersReportingServiceImpl class");
//		LOGGER.debug("Incoming Service connection ");   //+connection);
		try {
			PartnersReportingTable partnersReportingObj = processJsonAndGetObject(json);
			String projectId = partnersReportingObj.getProjectId();
			String partnerId = partnersReportingObj.getPartnerId();
			String partnerProjectIdIfExists = getPartnerProjectIdIfExists(connection, databaseName, projectId,
					partnerId);
			if (partnerProjectIdIfExists != null && partnerProjectIdIfExists.length() > 0) {
				LOGGER.debug("*** PartnerProjectId Exists, Updating PartnersReportingTable data.");
				partnersReportingDAO.updatePartnersReportingData(connection, databaseName, partnerProjectIdIfExists,
						partnersReportingObj);
				return partnerProjectIdIfExists;
			} else {
				LOGGER.debug("*** ProjectId '" + StringEscapeUtils.escapeJava(projectId) + "' and partnerId '" + StringEscapeUtils.escapeJava(partnerId)
						+ "' NOT EXISTS..Creating new Entry..");
				String partnerProjectId = UUID.randomUUID().toString();
				partnersReportingObj.setPartnerProjectId(partnerProjectId);
				LOGGER.debug("Created from JSON, PartnersReportingTable obj: " + StringEscapeUtils.escapeJava(partnersReportingObj.toString()));
				partnersReportingDAO.addPartnersReportingData(connection, databaseName, partnersReportingObj);
				return partnerProjectId;
			}

		} catch (Exception e) {
			LOGGER.error("Unable to add PartnersReportingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to add PartnersReportingData: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to update the Partners reporting data, the <strong>entry should exists</strong> to update
	 * or this method will throw {@link ReportingDbServiceException}
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the details of PartnersReporting fields.
	 * @return the partnerProjectId belongs to the updated project and partner.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public String updatePartnersReportingData(Connection connection, String databaseName, String json)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updatePartnersReportingData(con) method of PartnersReportingServiceImpl class");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			PartnersReportingTable partnersReportingObj = processJsonAndGetObject(json);
			String projectId = partnersReportingObj.getProjectId();
			String partnerId = partnersReportingObj.getPartnerId();
			String partnerProjectIdIfExists = getPartnerProjectIdIfExists(connection, databaseName, projectId,
					partnerId);
			if (partnerProjectIdIfExists != null && partnerProjectIdIfExists.length() > 0) { // This must be exist, to
																								// update, OR Throwing
																								// exception.
				LOGGER.debug("*** PartnerProjectId Exists, Updating PartnersReportingTable data.");
				partnersReportingDAO.updatePartnersReportingData(connection, databaseName, partnerProjectIdIfExists,
						partnersReportingObj);
				return partnerProjectIdIfExists;
			} else {
				LOGGER.debug("*** ProjectId '" + StringEscapeUtils.escapeJava(projectId) + "' and partnerId '" + StringEscapeUtils.escapeJava(partnerId)
						+ "' NOT EXISTS..Throwing Exception");
				throw new ReportingDbServiceException(
						"Cannot update Non-existing entry of PartnersReportingTable for ProjectId '" + projectId
								+ "' and partnerId '" + partnerId + "'");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Update PartnersReportingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Update PartnersReportingData: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get all the partner projectIds belonging to a particular project.
	 * @param connection used to perform this operation in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the Project ID.
	 * @return all the partner project Id.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public List<String> getPartnerProjectIdList(Connection connection, String databaseName, String json)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside getPartnerProjectIdList method of PartnersReportingServiceImpl class");
		List<String> partnerProjectIdList = null;
		try {
			String projectId = getProjectIdForDeleteOperation(json);
			if (projectId != null && projectId.length() > 0) {
				LOGGER.debug("*** Getting PartnerProject Ids belonging to projectId: " + projectId);
				partnerProjectIdList = partnersReportingDAO.getPartnerProjectIdsForProjectId(connection, databaseName,
						projectId);
				LOGGER.debug("*** PartnerProjectIdList: " + StringEscapeUtils.escapeJava(partnerProjectIdList.toString()));
				LOGGER.info("*** PartnerProjectIdList Size: " + partnerProjectIdList.size());
				// LOGGER.debug("*** Deleting PartnersReportingData for projectId: "+projectId);
				// partnersReportingDAO.deletePartnersReportingData(connection,databaseName,
				// projectId);
				return partnerProjectIdList;
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Delete PartnersReportingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to delete PartnersReportingData: " + e.getMessage(), e);
		}
		return partnerProjectIdList;
	}

	/**
	 * This method is used to delete all the data related to the given project.
	 * @param connection used to perform this operation in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the Project ID.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public void deletePartnersReportingByProjectId(Connection connection, String databaseName, String json)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside deletePartnersReportingByProjectId(con) method of PartnersReportingServiceImpl class");
//		LOGGER.debug("Incoming service connection : "+connection);
		try {
			String projectId = getProjectIdForDeleteOperation(json);
			if (projectId != null && projectId.length() > 0) {
//				LOGGER.debug("*** Getting PartnerProject Ids belonging to projectId: "+projectId);
//				partnerProjectIdList =  partnersReportingDAO.getPartnerProjectIdsForProjectId(connection,databaseName, projectId);
//				LOGGER.debug("*** PartnerProjectIdList: "+partnerProjectIdList);
//				LOGGER.info("*** PartnerProjectIdList Size: "+partnerProjectIdList.size());
				LOGGER.debug("*** Deleting PartnersReportingData for projectId: " + projectId);
				partnersReportingDAO.deletePartnersReportingData(connection, databaseName, projectId);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Delete PartnersReportingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to delete PartnersReportingData: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to update the client sponsor details.
	 * <br>Note: Updating Client Sponsor <strong>reset the previous scores</strong> for the Grading forms submitted. </br>
	 * @param connection used to perform this operation in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the client sponsor details and projectId and partnerId.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public void updateCSByProjectIdAndPartnerId(Connection connection, String databaseName, String json)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updateCSByProjectIdAndPartnerId(con) method of PartnersReportingServiceImpl class");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			ReportDBClientSponsor obj = processCSJsonAndGetObject(json);
			String partnerId = obj.getPartnerId();
			String projectId = obj.getProjectId();
			String partnerProjectIdIfExists = getPartnerProjectIdIfExists(connection, databaseName, projectId,
					partnerId);
			if (partnerProjectIdIfExists != null && partnerProjectIdIfExists.length() > 0) {
				partnersReportingDAO.updateCSDetails(connection, databaseName, obj);
				List<String> formUUIDList = formGradingDao.getFormUUIDForPartnerProjectIdList(connection, databaseName,
						Arrays.asList(partnerProjectIdIfExists));
				formGradingDao.updateFormGradingDataByPartnerProjectId(connection, databaseName,
						partnerProjectIdIfExists);
				iParameterGradingDao.updateParameterGradingDataByFormUUId(connection, databaseName, formUUIDList);
			} else {
				LOGGER.debug("*** ProjectId '" + projectId + "' and partnerId '" + partnerId
						+ "' NOT EXISTS..Throwing Exception");
				throw new ReportingDbServiceException(
						"Cannot update Non-existing entry of PartnersReportingTable for ProjectId '" + projectId
								+ "' and partnerId '" + partnerId + "'");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Update Client-Sponsor of PartnersReportingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Unable to Update Client-Sponsor of PartnersReportingData: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to process the given JSON and get the data required for PartnersReporting
	 * @param inputJSON the JSON containing the details of PartnersReporting
	 * @return a {@link PartnersReportingTable} object which is having the required details.
	 * @throws ReportingDbServiceException if any error occurs when getting the fields from the JSON.
	 */
	private PartnersReportingTable processJsonAndGetObject(String inputJSON) throws ReportingDbServiceException {
		LOGGER.debug(".inside processJsonAndGetObject method of PartnersReportingServiceImpl class");
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject projectJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.PROJECT);
			JSONObject scoreJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.SCORE);

			// Following values can be get from JSONObject "jsonObj" directly.
			String piaName = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PIA_NAME);
//			String district = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.DISTRICT);
			String headPersonnel = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.HEAD_PERSONNEL);
			String contact = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.CONTACT);
			String headPersonEmail = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.HEAD_PERSON_EMAIL);
			boolean status = JsonUtill.getBoolean(jsonObj, ReportingDbJSONConstant.STATUS);
			String partnerId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PARTNER_ID);
			LOGGER.debug(
					"Successfully Fetched : <piaName,district,headPersonnel,contact,headPersonEmail,status,partnerId>");

			// Following values can be get from JSONObject "projectJsonObj"
			String projectId = JsonUtill.getString(projectJsonObj, ReportingDbJSONConstant.PROJECT_ID);
			String projectName = JsonUtill.getString(projectJsonObj, ReportingDbJSONConstant.PROJECT_NAME);
			LOGGER.debug("Successfully Fetched : <projectId,projectName,partnerId>");

			// Following values can be get from JSONObject "scoreJsonObj"
			double centerRating = PartnersReportingUtil.getCenterRating(scoreJsonObj);
			double projectGrading = PartnersReportingUtil.getProjectGrading(scoreJsonObj);
			double finalPercentage = PartnersReportingUtil.getFinalPercentage(scoreJsonObj);
			String grade = JsonUtill.getString(scoreJsonObj, ReportingDbJSONConstant.GRADE);
			LOGGER.debug("Successfully Fetched : <centerRating,projectGrading,finalPercentage,grade>");

			PartnersReportingTable obj = new PartnersReportingTable();

			// Setting values got in json.
			obj.setProjectId(projectId);
			obj.setProjectName(projectName);
			obj.setPartnerId(partnerId);
			obj.setPiaName(piaName);
//			obj.setDistrict(district);
			obj.setHeadPersonnel(headPersonnel);
			obj.setContact(contact);
			obj.setHeadPersonEmail(headPersonEmail);
			obj.setCenterRating(centerRating);
			obj.setProjectGrading(projectGrading);
			obj.setFinalPercentage(finalPercentage);
			obj.setGrade(grade);
			obj.setStatus(status);
			return obj;

		} catch (Exception e) {
			LOGGER.error("Cannot create PartnersReportingTable object from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot create PartnersReportingTable object from the JSON: " + e.getMessage(), e);
		}

	}

	/**
	 * This method is used to process the given JSON and get the project Id
	 * @param inputJSON the JSON containing the project Id
	 * @return the projectId 
	 * @throws ReportingDbServiceException if any error occurs when getting the fields from the JSON.
	 */
	private String getProjectIdForDeleteOperation(String inputJSON) throws ReportingDbServiceException {
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			String projectId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PROJECT_ID);
			return projectId;
		} catch (Exception e) {
			LOGGER.error("Cannot get ProjectId from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Cannot get ProjectId from the JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * This helper method is used to get the PartnerProject Id for the given projectId and partnerId, if and only if 
	 * the entry exists. 
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param projectId the id of the project
	 * @param partnerId the id of the partner
	 * @return the partnerProjectId if exists,else return <strong>null</strong>
	 * @throws ReportingDbServiceException if any error occurs when performing this operation.
	 */
	private String getPartnerProjectIdIfExists(Connection connection, String databaseName, String projectId,
			String partnerId) throws ReportingDbServiceException {
		LOGGER.debug(".inside getPartnerProjectIdIfExists method of PartnersReportingServiceImpl class.");
		String partnerProjectId = null;
		try {
			PartnersReportingTable obj = partnersReportingDAO.getDataByProjectIdAndPartnerId(connection, databaseName,
					projectId, partnerId);
			if (obj != null) {
				LOGGER.debug("ProjectId '" + StringEscapeUtils.escapeJava(projectId) + "' and partnerId '" + StringEscapeUtils.escapeJava(partnerId) + "' exists..");
				partnerProjectId = obj.getPartnerProjectId();
			}
		} catch (Exception e) {
			LOGGER.error("Cannot check if partnerId and projectId is already there.", e);
			throw new ReportingDbServiceException(
					"Cannot check if partnerId and projectId is already there, " + e.getMessage());
		}
		return partnerProjectId;
	}

	/**
	 * This method is used to process the given JSON and get the client sponsor details which is to be updated.
	 * @param inputJSON the JSON containing the client sponsor details
	 * @return a {@link ReportDBClientSponsor} object which is having the required details to update client sponsor.
	 * @throws ReportingDbServiceException if any error occurs when getting the fields from the JSON.
	 */
	private ReportDBClientSponsor processCSJsonAndGetObject(String inputJSON) throws ReportingDbServiceException {
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject csJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.CLIENT_SPONSOR);

			String partnerId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PARTNER_ID);
			String projectId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PROJECT_ID);

			String csName = JsonUtill.getString(csJsonObj, ReportingDbJSONConstant.CS_NAME);
			String csPhone = JsonUtill.getString(csJsonObj, ReportingDbJSONConstant.CS_PHONE);
			String csEmail = JsonUtill.getString(csJsonObj, ReportingDbJSONConstant.CS_EMAIL);
			ReportDBClientSponsor obj = new ReportDBClientSponsor();
			obj.setProjectId(projectId);
			obj.setPartnerId(partnerId);
			obj.setCsName(csName);
			obj.setCsPhone(csPhone);
			obj.setCsEmail(csEmail);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Cannot create ReportDBClientSponsor object from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot create ReportDBClientSponsor object from the JSON: " + e.getMessage(), e);
		}
	}

}
