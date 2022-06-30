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
import com.pwc.grading.reportingdb.dao.IPartnersReportingDAO;
import com.pwc.grading.reportingdb.dao.impl.FormGradingDAOImpl;
import com.pwc.grading.reportingdb.dao.impl.PartnersReportingDAOImpl;
import com.pwc.grading.reportingdb.model.FormGradingTable;
import com.pwc.grading.reportingdb.model.PartnersReportingTable;
import com.pwc.grading.reportingdb.model.ReportDBDeleteGrading;
import com.pwc.grading.reportingdb.model.ReportDBFieldAuditor;
import com.pwc.grading.reportingdb.service.IFormGradingService;
import com.pwc.grading.reportingdb.service.exp.ReportingDbServiceException;
import com.pwc.grading.reportingdb.util.FormsUtil;
import com.pwc.grading.util.JsonUtill;

/**
 * Implementation class for {@link IFormGradingService}
 *
 */
//@Singleton
public class FormGradingServiceImpl implements IFormGradingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FormGradingServiceImpl.class);

//	@Inject
	private IFormGradingDAO formGradingDAO;

//	@Inject
	private IPartnersReportingDAO partnersReportingDAO;

	public FormGradingServiceImpl() {
		formGradingDAO = new FormGradingDAOImpl();
		partnersReportingDAO = new PartnersReportingDAOImpl();
	}

	/**
	 * This method is used to add the grading form data if entry not exists, if the entry already exists it <strong>will not add a new entry again </strong>, but <strong>it will update the 
	 * existing entry.</strong>
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the grading form details.
	 * @param partnerProjectId the partnerprojectId.
	 * @return the formUUID belongs to the created entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public String addFormGradingData(Connection connection, String databaseName, String json, String partnerProjectId)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside addFormGradingData(con) method of FormGradingServiceImpl");
//		LOGGER.debug("Incoming service connection: "+connection);
		try {
			FormGradingTable formGradingObj = processJsonAndGetObject(json);
			String formId = formGradingObj.getFormId();
			String formUUIDIfExists = getFormUUIDIfExists(connection, databaseName, formId, partnerProjectId);
			if (formUUIDIfExists != null && formUUIDIfExists.length() > 0) {
				LOGGER.debug("*** FormUUID exists, Updating FormGradingTable's data.");
				formGradingDAO.updateFormGradingData(connection, databaseName, formUUIDIfExists, formGradingObj);
				return formUUIDIfExists;
			} else {
				LOGGER.debug("*** FormId '" + StringEscapeUtils.escapeJava(formId) + "' and partnerProjectId '" + StringEscapeUtils.escapeJava(partnerProjectId)
						+ "' NOT EXISTS, Creating new FormGradingTable entry..");
				String formUUID = UUID.randomUUID().toString();
				formGradingObj.setId(formUUID); // Generating form UUID.
				formGradingObj.setPartnerProjectId(partnerProjectId); // Setting incoming partnerProjectId
				LOGGER.debug("Created from JSON, FormGradingTable obj: " + StringEscapeUtils.escapeJava(formGradingObj.toString()));
				formGradingDAO.addFormGradingData(connection, databaseName, formGradingObj);
				return formUUID;
			}
		} catch (Exception e) {
			LOGGER.error("Unable to add FormGradingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to add FormGradingData: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to the update the form grading data, the <strong>entry should exists</strong> to update
	 * or this method will throw {@link ReportingDbServiceException}
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the grading form details.
	 * @param partnerProjectId  the partnerprojectId.
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public String updateFormGradingData(Connection connection, String databaseName, String json,
			String partnerProjectId) throws ReportingDbServiceException {
		LOGGER.debug(".inside updateFormGradingData(con) method of FormGradingServiceImpl");
//		LOGGER.debug("Incoming service connection: "+connection);
		try {
			FormGradingTable formGradingObj = processJsonAndGetObject(json);
			String formId = formGradingObj.getFormId();
			String formUUIDIfExists = getFormUUIDIfExists(connection, databaseName, formId, partnerProjectId);
			if (formUUIDIfExists != null && formUUIDIfExists.length() > 0) { // Form must be exist, or throwing
																				// exception
				LOGGER.debug("*** FormUUID exists, Updating FormGradingTable's data.");
				formGradingDAO.updateFormGradingData(connection, databaseName, formUUIDIfExists, formGradingObj);
				return formUUIDIfExists;
			} else {
				LOGGER.debug("*** FormId '" + StringEscapeUtils.escapeJava(formId) + "' and partnerProjectId '" + StringEscapeUtils.escapeJava(partnerProjectId)
						+ "' NOT EXISTS, Throwing exception..");
				throw new ReportingDbServiceException(
						"Cannot update non-existing entry of FormGradingTable for FormId '" + formId
								+ "' and partnerProjectId '" + partnerProjectId + "'");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Update FormGradingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Update FormGradingData: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get all the formUUIDs for all the given partnerProjectIds.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param partnerProjectIdList all the partnerProject Ids.
	 * @return all the formUUID in the form of {@link List}
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public List<String> getFormUUIDList(Connection connection, String databaseName, List<String> partnerProjectIdList)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside getFormUUIDList(con) method of FormGradingServiceImpl");
		List<String> formUUIDList = null;
		try {
			if (partnerProjectIdList != null && partnerProjectIdList.size() > 0) {
				formUUIDList = formGradingDAO.getFormUUIDForPartnerProjectIdList(connection, databaseName,
						partnerProjectIdList);
				LOGGER.debug("*** FormUUID List for partnerprojectId List : " + StringEscapeUtils.escapeJava(formUUIDList.toString()));
				LOGGER.info("*** FormUUID list size : " + formUUIDList.size());
				return formUUIDList;
			}
		} catch (Exception e) {
			LOGGER.error("Unable to get FormUUID List from FormGradingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to get FormUUID List from FormGradingData: " + e.getMessage(),
					e);
		}
		return formUUIDList;
	}

	/**
	 * This method is used to delete the form grading data for all the partnerProjectIds.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param partnerProjectIdList  all the partnerProject Ids.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public void deleteFormGradingDataForPartnerProjectIdList(Connection connection, String databaseName,
			List<String> partnerProjectIdList) throws ReportingDbServiceException {
		LOGGER.debug(".inside deleteFormGradingDataForPartnerProjectIdList(con) method of FormGradingServiceImpl");

		try {
			if (partnerProjectIdList != null && partnerProjectIdList.size() > 0) {
				LOGGER.debug("*** Deleting FormGradingData having given partner project id list..");
				formGradingDAO.deleteFormGradingDataForPartnerProjectIdList(connection, databaseName,
						partnerProjectIdList);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Delete FormGradingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Delete FormGradingData: " + e.getMessage(), e);
		}

	}

	/**
	 * This method is used to update the Field Auditor details for the particular form.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the Field-Auditor details.
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public String updateFAByPartnerProjectIdAndFormId(Connection connection, String databaseName, String json)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updateFAByPartnerProjectIdAndFormId(con) method of FormGradingServiceImpl");
		try {
			ReportDBFieldAuditor object = processFAJsonAndGetObject(json);
			String projectId = object.getProjectId();
			String partnerId = object.getPartnerId();
			PartnersReportingTable prObj = partnersReportingDAO.getDataByProjectIdAndPartnerId(connection, databaseName,
					projectId, partnerId);
			if (prObj != null) {
				object.setPartnerProjectId(prObj.getPartnerProjectId());
				String formUUIDIfExists = getFormUUIDIfExists(connection, databaseName, object.getFormId(),
						object.getPartnerProjectId());
				if (formUUIDIfExists != null && formUUIDIfExists.length() > 0) {
					LOGGER.debug("*** FormUUID exists, Updating FormGradingTable's Field Auditor data.");
					formGradingDAO.updateFADetails(connection, databaseName, object);
					return formUUIDIfExists;
				} else {
					LOGGER.debug("*** FormId '" + StringEscapeUtils.escapeJava(object.getFormId()) + "' and partnerProjectId '"
							+ StringEscapeUtils.escapeJava(object.getPartnerProjectId()) + "' NOT EXISTS In FORM GRDAING TABLE..Returning null");
					throw new Exception("Cannot update non-existing entry of FormGradingTable for FormId '"
							+ object.getFormId() + "' and partnerProjectId '" + object.getPartnerProjectId() + "'");
				}
			} else {
				LOGGER.debug("*** ProjectId '" + projectId + "' and partnerId '" + partnerId
						+ "' NOT EXISTS..Throwing exception..");
				throw new Exception("PartnerProjectId not exists for ProjectId '" + projectId + "' and partnerId '"
						+ partnerId + "' to update Field Auditor of FormGradingTable.");
			}

		} catch (Exception e) {
			LOGGER.error("Cannot update FieldAuditor data of FormGradingTable: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot update FieldAuditor data of FormGradingTable: " + e.getMessage(), e);
		}

	}

	/**
	 * This method is used to update the field auditor response for the form which the field auditor is submitted.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the details of field-auditor response.
	 * @param partnerProjectId the partnerprojectId.
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public String updateFormGradingDataFieldAuditorSubmit(Connection connection, String databaseName, String json,
			String partnerProjectId) throws ReportingDbServiceException {
		LOGGER.debug(".inside updateFormGradingDataFieldAuditorSubmit(con) method of FormGradingServiceImpl");
		try {
			FormGradingTable formGradingObj = processJsonAndGetFieldAuditorFields(json);
			String formId = formGradingObj.getFormId();
			FormGradingTable oldFormGrading = getFormGradingIfExists(connection, databaseName, formId,
					partnerProjectId);
			if (oldFormGrading != null) {
				LOGGER.debug("*** FormGrading entry exists, Updating FormGradingTable's FieldAuditor data.");
				LOGGER.debug("Old Form Grading : " + StringEscapeUtils.escapeJava(oldFormGrading.toString()));
				oldFormGrading.setFaScore(formGradingObj.getFaScore());
				int variance = oldFormGrading.getSaScore() - formGradingObj.getFaScore();
				oldFormGrading.setVariance(variance);
				oldFormGrading.setFaName(formGradingObj.getFaName());
				oldFormGrading.setStatus(formGradingObj.getStatus());
				oldFormGrading.setFaPhone(formGradingObj.getFaPhone());
				oldFormGrading.setSecondaryAuditorName(formGradingObj.getSecondaryAuditorName());
				oldFormGrading.setFaLocation(formGradingObj.getFaLocation());
				oldFormGrading.setFaDateAssigned(formGradingObj.getFaDateAssigned());
				oldFormGrading.setFaDateCompleted(formGradingObj.getFaDateCompleted());
				oldFormGrading.setFaStartTime(formGradingObj.getFaStartTime());
				oldFormGrading.setFaEndTime(formGradingObj.getFaEndTime());
				oldFormGrading.setSignoffTime(formGradingObj.getSignoffTime());
				oldFormGrading.setOtp(formGradingObj.getOtp());
				LOGGER.debug("New Form Grading FA Updated: " + StringEscapeUtils.escapeJava(oldFormGrading.toString()));
				formGradingDAO.updateFormGradingData(connection, databaseName, oldFormGrading.getId(), oldFormGrading);
				return oldFormGrading.getId();
			} else {
				LOGGER.debug("*** FormId '" + StringEscapeUtils.escapeJava(formId) + "' and partnerProjectId '" + StringEscapeUtils.escapeJava(partnerProjectId)
						+ "' NOT EXISTS, Throwing exception..");
				throw new ReportingDbServiceException(
						"Cannot update FieldAuditor data for non-existing entry of FormGradingTable for FormId '"
								+ formId + "' and partnerProjectId '" + partnerProjectId + "'");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Update FieldAuditor data of FormGradingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Unable to Update FieldAuditor data of FormGradingData: " + e.getMessage(), e);
		}
	}
	
	/**
	 * This method is used to update the client sponsor response for the form which the client sponsor is submitted.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the details of client sponsor response.
	 * @param partnerProjectId the partnerprojectId.
	 * @return  the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public String updateFormGradingDataClientSponsorSubmit(Connection connection, String databaseName, String json,
			String partnerProjectId) throws ReportingDbServiceException {
		LOGGER.debug(".inside updateFormGradingDataClientSponsorSubmit(con) method of FormGradingServiceImpl");
		try {
			FormGradingTable formGradingObj = processJsonAndGetClientSponsorFields(json);
			String formId = formGradingObj.getFormId();
			FormGradingTable oldFormGrading = getFormGradingIfExists(connection, databaseName, formId,
					partnerProjectId);
			if (oldFormGrading != null) {
				LOGGER.debug("*** FormGrading entry exists, Updating FormGradingTable's ClientSponsor data.");
				LOGGER.debug("Old Form Grading : " + StringEscapeUtils.escapeJava(oldFormGrading.toString()));
				oldFormGrading.setSaScore(formGradingObj.getSaScore());
				int variance = formGradingObj.getSaScore() - oldFormGrading.getFaScore();
				oldFormGrading.setVariance(variance);
				oldFormGrading.setPiaDateAssigned(formGradingObj.getPiaDateAssigned());
				oldFormGrading.setPiaDateCompletion(formGradingObj.getPiaDateCompletion());
				oldFormGrading.setPiaStartTime(formGradingObj.getPiaStartTime());
				oldFormGrading.setPiaEndTime(formGradingObj.getPiaEndTime());
				LOGGER.debug("New Form Grading CS Updated: " + StringEscapeUtils.escapeJava(oldFormGrading.toString()));
				formGradingDAO.updateFormGradingData(connection, databaseName, oldFormGrading.getId(), oldFormGrading);
				return oldFormGrading.getId();
			} else {
				LOGGER.debug("*** FormId '" + StringEscapeUtils.escapeJava(formId) + "' and partnerProjectId '" + StringEscapeUtils.escapeJava(partnerProjectId)
						+ "' NOT EXISTS, Throwing exception..");
				throw new ReportingDbServiceException(
						"Cannot update ClientSponsor data for non-existing entry of FormGradingTable for FormId '"
								+ formId + "' and partnerProjectId '" + partnerProjectId + "'");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Update ClientSponsor data of FormGradingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Unable to Update ClientSponsor data of FormGradingData: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get all the formUUIDs for the given partnerProjectId
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the details of the projectId and partnerId.
	 * @return all the formUUID in the form of {@link List}
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public List<String> getFormUUIDListForPartnerProjectId(Connection connection, String databaseName, String json)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside getFormUUIDListForPartnerProjectId(con) method of FormGradingServiceImpl");
		try {
			ReportDBDeleteGrading object = processDeleteGradingJsonAndGetObject(json);
			String projectId = object.getProjectId();
			String partnerId = object.getPartnerId();
			PartnersReportingTable partnersReporting = partnersReportingDAO.getDataByProjectIdAndPartnerId(connection,
					databaseName, projectId, partnerId);
			if (partnersReporting != null) {
				object.setPartnerProjectId(partnersReporting.getPartnerProjectId());
				List<String> formUUIDList = formGradingDAO.getFormUUIDForPartnerProjectIdList(connection, databaseName,
						Arrays.asList(partnersReporting.getPartnerProjectId()));
				LOGGER.debug("FormUUID List : " + StringEscapeUtils.escapeJava(formUUIDList.toString()));
				LOGGER.debug("FormUUID List : " + formUUIDList.size());
				return formUUIDList;
			} else {
				LOGGER.debug("*** ProjectId '" + projectId + "' and partnerId '" + partnerId
						+ "' NOT EXISTS..Throwing exception..");
				throw new Exception("PartnerProjectId not exists for ProjectId '" + projectId + "' and partnerId '"
						+ partnerId + "' to delete Grading forms of FormGradingTable.");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to get FormUUIDList to delete grading from FormGradingTable, " + e);
			throw new ReportingDbServiceException(
					"Unable to get FormUUIDList to delete grading from FormGradingTable, " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to delete the form grading entries for all the given formUUIDs.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param formUUIDList all the formUUIDs which are to be deleted.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public void deleteByFormUUIDList(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside deleteByFormUUIDList(con) method of FormGradingServiceImpl");
		try {
			if (formUUIDList != null && formUUIDList.size() > 0) {
				LOGGER.debug("*** Deleting FormGradingData having given FormUUID list..");
				formGradingDAO.deleteFormGradingDataForFormUUIDList(connection, databaseName, formUUIDList);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Delete FormGradingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Delete FormGradingData: " + e.getMessage(), e);
		}
	}
	
	/**
	 * This helper method is used to get the formUUID for the given formId and partnerProjectId
	 * @param connection connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param formId the id of the form.
	 * @param partnerProjectId the partnerProjectId
	 * @return the formUUID if exists, else return <strong>null</strong>
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private String getFormUUIDIfExists(Connection connection, String databaseName, String formId,
			String partnerProjectId) throws ReportingDbServiceException {
		LOGGER.debug(".inside getFormUUIDIfExists(con) method of FormGradingServiceImpl");
		String formUUID = null;
		try {
			FormGradingTable obj = formGradingDAO.getFormByFormIdAndPartnerProjectId(connection, databaseName, formId,
					partnerProjectId);
			if (obj != null) {
				LOGGER.debug("FormId '" + StringEscapeUtils.escapeJava(formId) + "' and partnerProjectId '" + StringEscapeUtils.escapeJava(partnerProjectId) + "' EXISTS..");
				formUUID = obj.getId();
			}
		} catch (Exception e) {
			LOGGER.error("Cannot check if FormId and partnerProjectId is already there.", e);
			throw new ReportingDbServiceException(
					"Cannot check if FormId and partnerProjectId is already there." + e.getMessage());
		}
		return formUUID;
	}

	/**
	 * This helper method is used to get the form grading details for the given formId and partnerProjectId
	 * @param connection connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param formId the id of the form.
	 * @param partnerProjectId the partnerProjectId
	 * @return a {@link FormGradingTable} object which is having the required details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private FormGradingTable getFormGradingIfExists(Connection connection, String databaseName, String formId,
			String partnerProjectId) throws ReportingDbServiceException {
		LOGGER.debug(".inside getFormGradingIfExists(con) method of FormGradingServiceImpl");
		try {
			FormGradingTable obj = formGradingDAO.getFormByFormIdAndPartnerProjectId(connection, databaseName, formId,
					partnerProjectId);
			if (obj != null) {
				LOGGER.debug("FormId '" + StringEscapeUtils.escapeJava(formId) + "' and partnerProjectId '" + StringEscapeUtils.escapeJava(partnerProjectId) + "' EXISTS..");
				return obj;
			}
		} catch (Exception e) {
			LOGGER.error("Cannot check if FormId and partnerProjectId is already there.", e);
			throw new ReportingDbServiceException(
					"Cannot check if FormId and partnerProjectId is already there." + e.getMessage());
		}
		return null;
	}
	
	/**
	 * This method is used to process the JSON and get the fields of Form Grading.
	 * @param inputJSON the input JSON passed from the processor.
	 * @return a {@link FormGradingTable} object which is having the required details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private FormGradingTable processJsonAndGetObject(String inputJSON) throws ReportingDbServiceException {
		LOGGER.debug(".inside processJsonAndGetObject method of FormGradingServiceImpl");
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject projectJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.PROJECT);
			JSONObject formJsonObject = JsonUtill.getJsonObject(projectJsonObj, ReportingDbJSONConstant.FORM);
			JSONObject timingJsonObject = JsonUtill.getJsonObject(formJsonObject, ReportingDbJSONConstant.TIMING);
			JSONObject faJsonObject = JsonUtill.getJsonObject(formJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR);

			// Following values can be get from JSONObject "formJsonObject" directly.
			String formId = JsonUtill.getString(formJsonObject, ReportingDbJSONConstant.FORM_ID);
			String formName = JsonUtill.getString(formJsonObject, ReportingDbJSONConstant.FORM_NAME);
			int maxMarks = JsonUtill.getInt(formJsonObject, ReportingDbJSONConstant.MAX_MARKS);
			int saScore = FormsUtil.getSAScore(formJsonObject);
			int faScore = FormsUtil.getFAScore(formJsonObject);
			int variance = saScore - faScore;
			boolean status = JsonUtill.getBoolean(formJsonObject, ReportingDbJSONConstant.FORM_STATUS);
			LOGGER.debug("Successfully Fetched : <formId,formName,status,MaxMarks,SAScorce,FAScore>");

			// Following values can be get from JSONObject "timing".
			String piaDateAssigned = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.START_DATE);
			String piaDateCompletion = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.END_DATE);
			String piaStartTime = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.START_TIME);
			String piaEndTime = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.END_TIME);
			LOGGER.debug("Successfully Fetched : <piaDateAssigned,piaDateCompletion,piaStartTime,piaEndTime>");

			// Following values can be get from JSONObject "faJsonObject".
			String faName = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR_NAME);
			String faPhone = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR_PHONE);
			String secondaryAuditorName = JsonUtill.getString(faJsonObject,
					ReportingDbJSONConstant.SEC_FIELD_AUDITOR_NAME);
			String faLocation = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR_LOCATION);
			if (faLocation.isEmpty()) {
				faLocation = null;
			}
			String faDateAssigned = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_START_DATE);
			String faDateCompleted = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_END_DATE);
			String faStartTime = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_START_TIME);
			String faEndTime = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_END_TIME);
			String signoffTime = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.SIGN_OFF_TIME);
			String otp = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.OTP);
			LOGGER.debug(
					"Successfully Fetched : <faName,faPhone,secondaryAuditorName,faLocation,faDateAssigned,faDateCompleted,faStartTime,faEndTime,signoffTime,otp>");

			FormGradingTable obj = new FormGradingTable();
			obj.setFormId(formId);
			obj.setFormName(formName);
			obj.setMaxMarks(maxMarks);
			obj.setSaScore(saScore);
			obj.setFaScore(faScore);
			obj.setVariance(variance);
			obj.setStatus(status);
			obj.setPiaDateAssigned(piaDateAssigned);
			obj.setPiaDateCompletion(piaDateCompletion);
			obj.setPiaStartTime(piaStartTime);
			obj.setPiaEndTime(piaEndTime);
			obj.setFaName(faName);
			obj.setFaPhone(faPhone);
			obj.setSecondaryAuditorName(secondaryAuditorName);
			obj.setFaLocation(faLocation);
			obj.setFaDateAssigned(faDateAssigned);
			obj.setFaDateCompleted(faDateCompleted);
			obj.setFaStartTime(faStartTime);
			obj.setFaEndTime(faEndTime);
			obj.setSignoffTime(signoffTime);
			obj.setOtp(otp);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Cannot create FormGradingTable object from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot create FormGradingTable object from the JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to process the JSON and get the Field Auditor fields of Form Grading.
	 * @param inputJSON inputJSON the input JSON passed from the processor.
	 * @return  a {@link FormGradingTable} object which is having the Field Auditor details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private FormGradingTable processJsonAndGetFieldAuditorFields(String inputJSON) throws ReportingDbServiceException {
		LOGGER.debug(".inside processJsonAndGetFieldAuditorFields method of FormGradingServiceImpl");
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject projectJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.PROJECT);
			JSONObject formJsonObject = JsonUtill.getJsonObject(projectJsonObj, ReportingDbJSONConstant.FORM);
//					JSONObject timingJsonObject = JsonUtill.getJsonObject(formJsonObject, ReportingDbJSONConstant.TIMING);
			JSONObject faJsonObject = JsonUtill.getJsonObject(formJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR);

			// Following values can be get from JSONObject "formJsonObject" directly.
			String formId = JsonUtill.getString(formJsonObject, ReportingDbJSONConstant.FORM_ID);
			String formName = JsonUtill.getString(formJsonObject, ReportingDbJSONConstant.FORM_NAME);
			int maxMarks = JsonUtill.getInt(formJsonObject, ReportingDbJSONConstant.MAX_MARKS);
			int saScore = 0; // FormsUtil.getSAScore(formJsonObject);
			int faScore = FormsUtil.getFAScore(formJsonObject);
//			int variance = saScore - faScore;
			boolean status = JsonUtill.getBoolean(formJsonObject, ReportingDbJSONConstant.FORM_STATUS);
			LOGGER.debug("Successfully Fetched : <formId,formName,status,MaxMarks,FAScore>");

			// Following values can be get from JSONObject "timing".
//			String piaDateAssigned = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.START_DATE);
//			String piaDateCompletion = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.END_DATE);
//			String piaStartTime = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.START_TIME);
//			String piaEndTime = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.END_TIME);
//			LOGGER.debug("Successfully Fetched : <piaDateAssigned,piaDateCompletion,piaStartTime,piaEndTime>");

			// Following values can be get from JSONObject "faJsonObject".
			String faName = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR_NAME);
			String faPhone = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR_PHONE);
			String secondaryAuditorName = JsonUtill.getString(faJsonObject,
					ReportingDbJSONConstant.SEC_FIELD_AUDITOR_NAME);
			String faLocation = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR_LOCATION);
			String faDateAssigned = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_START_DATE);
			String faDateCompleted = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_END_DATE);
			String faStartTime = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_START_TIME);
			String faEndTime = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_END_TIME);
			String signoffTime = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.SIGN_OFF_TIME);
			String otp = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.OTP);
			LOGGER.debug(
					"Successfully Fetched : <faName,faPhone,secondaryAuditorName,faLocation,faDateAssigned,faDateCompleted,faStartTime,faEndTime,signoffTime,otp>");

			FormGradingTable obj = new FormGradingTable();
			obj.setFormId(formId);
			obj.setFormName(formName);
			obj.setMaxMarks(maxMarks);
			obj.setSaScore(saScore);
			obj.setFaScore(faScore);
//			obj.setVariance(variance);
			obj.setStatus(status);
//			obj.setPiaDateAssigned(piaDateAssigned);
//			obj.setPiaDateCompletion(piaDateCompletion);
//			obj.setPiaStartTime(piaStartTime);
//			obj.setPiaEndTime(piaEndTime);
			obj.setFaName(faName);
			obj.setFaPhone(faPhone);
			obj.setSecondaryAuditorName(secondaryAuditorName);
			obj.setFaLocation(faLocation);
			obj.setFaDateAssigned(faDateAssigned);
			obj.setFaDateCompleted(faDateCompleted);
			obj.setFaStartTime(faStartTime);
			obj.setFaEndTime(faEndTime);
			obj.setSignoffTime(signoffTime);
			obj.setOtp(otp);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Cannot create FormGradingTable FieldAuditorFields object from the JSON: " + e.getMessage(),
					e);
			throw new ReportingDbServiceException(
					"Cannot create FormGradingTable FieldAuditorFields object from the JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to process the JSON and get the Client Sponsor fields of Form Grading.
	 * @param inputJSON inputJSON the input JSON passed from the processor.
	 * @return  a {@link FormGradingTable} object which is having the Client Sponsor details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private FormGradingTable processJsonAndGetClientSponsorFields(String inputJSON) throws ReportingDbServiceException {
		LOGGER.debug(".inside processJsonAndGetClientSponsorFields method of FormGradingServiceImpl");
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject projectJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.PROJECT);
			JSONObject formJsonObject = JsonUtill.getJsonObject(projectJsonObj, ReportingDbJSONConstant.FORM);
			JSONObject timingJsonObject = JsonUtill.getJsonObject(formJsonObject, ReportingDbJSONConstant.TIMING);
			// JSONObject faJsonObject = JsonUtill.getJsonObject(formJsonObject,
			// ReportingDbJSONConstant.FIELD_AUDITOR);

			// Following values can be get from JSONObject "formJsonObject" directly.
			String formId = JsonUtill.getString(formJsonObject, ReportingDbJSONConstant.FORM_ID);
			String formName = JsonUtill.getString(formJsonObject, ReportingDbJSONConstant.FORM_NAME);
			int maxMarks = JsonUtill.getInt(formJsonObject, ReportingDbJSONConstant.MAX_MARKS);
			int saScore = FormsUtil.getSAScore(formJsonObject);
//			int faScore = FormsUtil.getFAScore(formJsonObject);
//			int variance = saScore - faScore;
			boolean status = JsonUtill.getBoolean(formJsonObject, ReportingDbJSONConstant.FORM_STATUS);
			LOGGER.debug("Successfully Fetched : <formId,formName,status,MaxMarks,SAScorce>");

			// Following values can be get from JSONObject "timing".
			String piaDateAssigned = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.START_DATE);
			String piaDateCompletion = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.END_DATE);
			String piaStartTime = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.START_TIME);
			String piaEndTime = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.END_TIME);
			LOGGER.debug("Successfully Fetched : <piaDateAssigned,piaDateCompletion,piaStartTime,piaEndTime>");

			// Following values can be get from JSONObject "faJsonObject".
//			String faName = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR_NAME);
//			String faPhone = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR_PHONE);
//			String secondaryAuditorName = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.SEC_FIELD_AUDITOR_NAME);
//			String faLocation = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR_LOCATION);
//			String faDateAssigned = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_START_DATE);
//			String faDateCompleted = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_END_DATE);
//			String faStartTime = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_START_TIME);
//			String faEndTime = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_END_TIME);
//			String signoffTime = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.SIGN_OFF_TIME);
//			String otp = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.OTP);
//			LOGGER.debug("Successfully Fetched : <faName,faPhone,secondaryAuditorName,faLocation,faDateAssigned,faDateCompleted,faStartTime,faEndTime,signoffTime,otp>");

			FormGradingTable obj = new FormGradingTable();
			obj.setFormId(formId);
			obj.setFormName(formName);
			obj.setMaxMarks(maxMarks);
			obj.setSaScore(saScore);
//			obj.setFaScore(faScore);
//			obj.setVariance(variance);
			obj.setStatus(status);
			obj.setPiaDateAssigned(piaDateAssigned);
			obj.setPiaDateCompletion(piaDateCompletion);
			obj.setPiaStartTime(piaStartTime);
			obj.setPiaEndTime(piaEndTime);
//			obj.setFaName(faName);
//			obj.setFaPhone(faPhone);
//			obj.setSecondaryAuditorName(secondaryAuditorName);
//			obj.setFaLocation(faLocation);
//			obj.setFaDateAssigned(faDateAssigned);
//			obj.setFaDateCompleted(faDateCompleted);
//			obj.setFaStartTime(faStartTime);
//			obj.setFaEndTime(faEndTime);
//			obj.setSignoffTime(signoffTime);
//			obj.setOtp(otp);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Cannot create FormGradingTable FieldAuditorFields object from the JSON: " + e.getMessage(),
					e);
			throw new ReportingDbServiceException(
					"Cannot create FormGradingTable FieldAuditorFields object from the JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to process the JSON and get the FieldAuditor updation fields of Form Grading.
	 * @param inputJSON inputJSON the input JSON passed from the processor.
	 * @return  a {@link ReportDBFieldAuditor} object which is having the FieldAuditor details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private ReportDBFieldAuditor processFAJsonAndGetObject(String inputJSON) throws ReportingDbServiceException {
		LOGGER.debug(".inside processFAJsonAndGetObject method of FormGradingServiceImpl");
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject faJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.FIELD_AUDITOR);
			String projectId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PROJECT_ID);
			String partnerId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PARTNER_ID);
			String formId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.FORM_ID);
			LOGGER.debug("Successfully Fetched : <projectId,partnerId,formId>");
			String faName = JsonUtill.getString(faJsonObj, ReportingDbJSONConstant.FIELD_AUDITOR_NAME);
			String faPhone = JsonUtill.getString(faJsonObj, ReportingDbJSONConstant.FIELD_AUDITOR_PHONE);
			LOGGER.debug("Successfully Fetched : <faName,faPhone>");
			ReportDBFieldAuditor obj = new ReportDBFieldAuditor();
			obj.setProjectId(projectId);
			obj.setPartnerId(partnerId);
			obj.setFormId(formId);
			obj.setFaName(faName);
			obj.setFaPhone(faPhone);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Cannot create ReportDBFieldAuditor object from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot create ReportDBFieldAuditor object from the JSON: " + e.getMessage(), e);
		}
	}
	
	
	/**
	 * This method is used to process the JSON and get the projectId and partnerId to delete the grading forms.
	 * @param inputJSON inputJSON the input JSON passed from the processor.
	 * @return a {@link ReportDBDeleteGrading} object which is having the projectId and partnerId details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private ReportDBDeleteGrading processDeleteGradingJsonAndGetObject(String inputJSON)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside processDeleteGradingJsonAndGetObject method of FormGradingServiceImpl");
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			String projectId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PROJECT_ID);
			String partnerId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PARTNER_ID);
//			String formId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.FORM_ID);
			LOGGER.debug("Successfully Fetched : <projectId,partnerId>");
			ReportDBDeleteGrading obj = new ReportDBDeleteGrading();
			obj.setProjectId(projectId);
			obj.setPartnerId(partnerId);
//			obj.setFormId(formId);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Cannot create FormGradingTable DeleteGrading object from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Cannot create DeleteGrading object from the JSON: " + e.getMessage(),
					e);
		}
	}
}
