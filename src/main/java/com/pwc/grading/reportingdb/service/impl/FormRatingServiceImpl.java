package com.pwc.grading.reportingdb.service.impl;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.pwc.grading.reportingdb.constant.ReportingDbJSONConstant;
import com.pwc.grading.reportingdb.dao.IFormRatingDAO;
import com.pwc.grading.reportingdb.dao.IParameterRatingDAO;
import com.pwc.grading.reportingdb.dao.IPartnersReportingDAO;
import com.pwc.grading.reportingdb.dao.impl.FormRatingDAOImpl;
import com.pwc.grading.reportingdb.dao.impl.ParameterRatingDAOImpl;
import com.pwc.grading.reportingdb.dao.impl.PartnersReportingDAOImpl;
import com.pwc.grading.reportingdb.db.constant.ReportingDatabaseQueryConstants;
import com.pwc.grading.reportingdb.model.FormRatingTable;
import com.pwc.grading.reportingdb.model.PartnersReportingTable;
import com.pwc.grading.reportingdb.model.ReportDBCenterIncharge;
import com.pwc.grading.reportingdb.model.ReportDBFieldAuditor;
import com.pwc.grading.reportingdb.model.ReportDBTrainingCenter;
import com.pwc.grading.reportingdb.model.ReportDbDeleteRating;
import com.pwc.grading.reportingdb.service.IFormRatingService;
import com.pwc.grading.reportingdb.service.exp.ReportingDbServiceException;
import com.pwc.grading.reportingdb.util.FormsUtil;
import com.pwc.grading.util.JsonUtill;

/**
 * Implementation class for {@link IFormRatingService}
 *
 */
//@Singleton
public class FormRatingServiceImpl implements IFormRatingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FormRatingServiceImpl.class);

//	@Inject
	private IFormRatingDAO formRatingDAO;

//	@Inject
	private IPartnersReportingDAO partnersReportingDAO;

	private IParameterRatingDAO iParameterRatingDao;

	public FormRatingServiceImpl() {
		formRatingDAO = new FormRatingDAOImpl();
		partnersReportingDAO = new PartnersReportingDAOImpl();
		iParameterRatingDao = new ParameterRatingDAOImpl();
	}

	/**
	 * This method is used to add the Rating form data if entry not exists, if the entry already exists for
	 * the partner and project, it <strong>will not add a new entry again </strong>, but <strong>it will update the 
	 * existing entry.</strong>
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the rating form details.
	 * @param partnerProjectId the partnerprojectId. 
	 * @return the formUUID belongs to the created entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public String addFormRatingData(Connection connection, String databaseName, String json, String partnerProjectId)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside addFormRatingData(con) method of FormRatingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			FormRatingTable formRatingObj = processJsonAndGetObject(json);
			String formId = formRatingObj.getFormId();
			String tcId = formRatingObj.getTcId();
			String formUUIDIfExists = getFormUUIDIfExists(connection, databaseName, formId, tcId, partnerProjectId);
			if (formUUIDIfExists != null && formUUIDIfExists.length() > 0) {
				LOGGER.debug("*** FormUUID exists, Updating FormRatingTable's data.");
				formRatingDAO.updateFormRatingData(connection, databaseName, formUUIDIfExists, formRatingObj);
				return formUUIDIfExists;
			} else {
				LOGGER.debug("*** FormId '" + StringEscapeUtils.escapeJava(formId) + "' and partnerProjectId '" + StringEscapeUtils.escapeJava(partnerProjectId) + "' and TcId '"
						+ StringEscapeUtils.escapeJava(tcId) + "' NOT EXISTS, Creating new FormRatingTable entry..");
				String formUUID = UUID.randomUUID().toString();
				formRatingObj.setId(formUUID); // Generating form UUID.
				formRatingObj.setPartnerProjectId(partnerProjectId); // Setting incoming partnerProjectId
				LOGGER.debug("Created from JSON, FormRatingTable obj: " + StringEscapeUtils.escapeJava(formRatingObj.toString()));
				formRatingDAO.addFormRatingData(connection, databaseName, formRatingObj);
				return formUUID;
			}

		} catch (Exception e) {
			LOGGER.error("Unable to add FormRatingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to add FormRatingData: " + e.getMessage(), e);
		}

	}

	/**
	 * This method is used to the update the form rating data, the <strong>entry should exists</strong> to update
	 * or this method will throw {@link ReportingDbServiceException}
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the rating form details.
	 * @param partnerProjectId the partnerprojectId. 
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public String updateFormRatingData(Connection connection, String databaseName, String json, String partnerProjectId)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updateFormRatingData(con) method of FormRatingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			FormRatingTable formRatingObj = processJsonAndGetObject(json);
			String formId = formRatingObj.getFormId();
			String tcId = formRatingObj.getTcId();
			String formUUIDIfExists = getFormUUIDIfExists(connection,databaseName, formId, tcId, partnerProjectId);
			if (formUUIDIfExists != null && formUUIDIfExists.length() > 0) { // Form must be exist, or throwing
																				// exception.
				LOGGER.debug("*** FormUUID exists, Updating FormRatingTable's data.");
				formRatingDAO.updateFormRatingData(connection, databaseName, formUUIDIfExists, formRatingObj);
				return formUUIDIfExists;
			} else {
				LOGGER.debug("*** FormId '" + StringEscapeUtils.escapeJava(formId) + "' and partnerProjectId '" + StringEscapeUtils.escapeJava(partnerProjectId) + " and TcId '"
						+ StringEscapeUtils.escapeJava(tcId) + "' NOT EXISTS, Throwing exception..");
				throw new ReportingDbServiceException("Cannot update non-existing entry of FormRatingTable for FormId '"
						+ formId + "' and partnerProjectId '" + partnerProjectId + "'");
			}

		} catch (Exception e) {
			LOGGER.error("Unable to update FormRatingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to update FormRatingData: " + e.getMessage(), e);
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
		LOGGER.debug(".inside getFormUUIDList(con) method of FormRatingServiceImpl class.");
		List<String> formUUIDList = null;
		try {
			if (partnerProjectIdList != null && partnerProjectIdList.size() > 0) {
				formUUIDList = formRatingDAO.getFormUUIDForPartnerProjectIdList(connection, databaseName,
						partnerProjectIdList);
				LOGGER.debug("*** FormUUID List for partnerprojectId List : " + StringEscapeUtils.escapeJava(formUUIDList.toString()));
				LOGGER.info("*** FormUUID list size : " + formUUIDList.size());
				return formUUIDList;
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Delete FormRatingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Delete FormRatingData: " + e.getMessage(), e);
		}
		return formUUIDList;
	}

	/**
	 * This method is used to delete the form rating data for all the partnerProjectIds.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param partnerProjectIdList  all the partnerProject Ids.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public void deleteFormRatingDataForPartnerProjectIdList(Connection connection, String databaseName,
			List<String> partnerProjectIdList) throws ReportingDbServiceException {
		LOGGER.debug(".inside deleteFormRatingDataForPartnerProjectIdList(con) method of FormRatingServiceImpl class.");
		try {
			if (partnerProjectIdList != null && partnerProjectIdList.size() > 0) {

				LOGGER.debug("*** Deleting FormRatingData having given partner project id list..");
				formRatingDAO.deleteFormRatingDataForPartnerProjectIdList(connection, databaseName,
						partnerProjectIdList);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Delete FormRatingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Delete FormRatingData: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to update the training center details 
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param trainingCenterJson the JSON containing the trainingcenter details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public void updateTCByPartnerProjectIdAndTcId(Connection connection, String databaseName, String trainingCenterJson)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updateTCByPartnerProjectIdAndTcId(con) method of FormRatingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			ReportDBTrainingCenter object = processTCJSONAndGetObject(trainingCenterJson);
			LOGGER.debug("Training Center Object from JSON: "+object);
			String projectId = object.getProjectId();
			String partnerId = object.getPartnerId();
			PartnersReportingTable prObj = partnersReportingDAO.getDataByProjectIdAndPartnerId(connection, databaseName,
					projectId, partnerId);
			if (prObj != null) {
				object.setPartnerProjectId(prObj.getPartnerProjectId());
				formRatingDAO.updateTCDetails(connection, databaseName, object);
			} else {
				LOGGER.debug("*** ProjectId '" + projectId + "' and partnerId '" + partnerId
						+ "' NOT EXISTS..Throwing exception..");
				throw new Exception("PartnerProjectId not exists for ProjectId '" + projectId + "' and partnerId '"
						+ partnerId + "' to update Training center.");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to update training center of FormRatingTable with--------------------", e);
			throw new ReportingDbServiceException(
					"Cannot check if FormId and partnerProjectId is already there." + e.getMessage());
		}
	}

	/**
	 * This method is used to update the CenterIncharge details 
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param cicJson the JSON containing the CenterIncharge details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public void updateCICByPartnerProjectIdAndTcId(Connection connection, String databaseName, String cicJson)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updateCICByPartnerProjectIdAndTcId(con) method of FormRatingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			ReportDBCenterIncharge object = processCICJSONAndGetObject(cicJson);
			String projectId = object.getProjectId();
			String partnerId = object.getPartnerId();
			String tcId = object.getTcid();
			PartnersReportingTable prObj = partnersReportingDAO.getDataByProjectIdAndPartnerId(connection, databaseName,
					projectId, partnerId);
			if (prObj != null) {
				object.setPartnerProjectId(prObj.getPartnerProjectId());
				LOGGER.debug("After assinging PartnerProjectId & Before updating new CIC : " + StringEscapeUtils.escapeJava(object.toString()));
				formRatingDAO.updateCICDetails(connection, databaseName, object);
				List<String> formUUIDList = formRatingDAO.getFormUUIDForPartnerProjectIdAndTcId(connection,
						databaseName, prObj.getPartnerProjectId(), tcId);
				iParameterRatingDao.updateParameterRatingDataByFormUUid(connection, databaseName, formUUIDList);
			} else {
				LOGGER.debug("*** ProjectId '" + projectId + "' and partnerId '" + partnerId
						+ "' NOT EXISTS..Throwing exception..");
				throw new Exception("PartnerProjectId not exists for ProjectId '" + projectId + "' and partnerId '"
						+ partnerId + "' to update Training center.");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to update center-in-charge of FormRatingTable, ", e);
			throw new ReportingDbServiceException(
					"Unable to update center-in-charge of FormRatingTable, " + e.getMessage());
		}

	}
	
	/**
	 * This method is used to update the Field Auditor details 
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the Field Auditor details.
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public String updateFieldAuditorDetails(Connection connection, String databaseName, String json)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updateFieldAuditorDetails(con) method of FormRatingServiceImpl");
//		LOGGER.debug("Incoming service connection :"+connection);
		try {
			ReportDBFieldAuditor object = processFAJsonAndGetObject(json);
			String projectId = object.getProjectId();
			String partnerId = object.getPartnerId();
			String tcId = object.getTcId();
			PartnersReportingTable prObj = partnersReportingDAO.getDataByProjectIdAndPartnerId(connection, databaseName,
					projectId, partnerId);
			if (prObj != null) {
				object.setPartnerProjectId(prObj.getPartnerProjectId());
				String formUUIDIfExists = getFormUUIDIfExists(connection, databaseName, object.getFormId(), tcId,
						object.getPartnerProjectId());
				if (formUUIDIfExists != null && formUUIDIfExists.length() > 0) {
					LOGGER.debug("*** FormUUID exists, Updating FormRatingTable's Field Auditor Name and Phone.");
					formRatingDAO.updateFADetails(connection, databaseName, formUUIDIfExists, object);
					return formUUIDIfExists;
				} else {
					LOGGER.debug("*** FormId '" + StringEscapeUtils.escapeJava(object.getFormId()) + "' and partnerProjectId '"
							+ StringEscapeUtils.escapeJava(object.getPartnerProjectId()) + "' and TcId '" + StringEscapeUtils.escapeJava(tcId)
							+ "' NOT EXISTS In FORM RATING TABLE");
					throw new Exception(
							"Cannot update Field Auditor name and phone for non-existing entry of FormRatingTable for FormId '"
									+ object.getFormId() + "' and partnerProjectId '" + object.getPartnerProjectId()
									+ "' and TcId '" + tcId + "'.");
				}
			} else {
				LOGGER.debug("*** ProjectId '" + projectId + "' and partnerId '" + partnerId
						+ "' NOT EXISTS..Throwing exception..");
				throw new Exception("PartnerProjectId not exists for ProjectId '" + projectId + "' and partnerId '"
						+ partnerId + "' to update Field Auditor Name and Phone of FormRatingTable.");
			}

		} catch (Exception e) {
			LOGGER.error("Cannot update FieldAuditor Name and phone of FormRatingTable: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot update FieldAuditor Name and phone of FormRatingTable: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to update the field auditor response for the form which the field auditor is submitted.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the details of field-auditor response.
	 * @param partnerProjectId the partnerprojectId.
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public String updateFormRatingDataFieldAuditorSubmit(Connection connection, String databaseName, String json,
			String partnerProjectId) throws ReportingDbServiceException {
		LOGGER.debug(".inside updateFormRatingDataFieldAuditorSubmit(con) method of FormRatingServiceImpl class.");
//		LOGGER.debug("Incoming service connection: "+(connection!=null));
		try {
			FormRatingTable formRatingObj = processJsonAndGetFieldAuditorFields(json);
			String formId = formRatingObj.getFormId();
			String tcId = formRatingObj.getTcId();
			FormRatingTable oldFormRating = getFormRatingIfExists(connection, databaseName, formId, tcId,
					partnerProjectId);
			if (oldFormRating != null) { // Form Rating must be exist, or throwing exception.
				LOGGER.debug("*** FormRatingTable entry exists, Updating FormRatingTable's Field Auditor submit data.");
				LOGGER.debug("Old Form Rating : " + StringEscapeUtils.escapeJava(oldFormRating.toString()));
				oldFormRating.setFaScore(formRatingObj.getFaScore());
				int variance = oldFormRating.getSaScore() - formRatingObj.getFaScore();
				oldFormRating.setVariance(variance);
				oldFormRating.setStatus(formRatingObj.getStatus());
				oldFormRating.setFaName(formRatingObj.getFaName());
				oldFormRating.setFaPhone(formRatingObj.getFaPhone());
				oldFormRating.setSecondaryAuditorName(formRatingObj.getSecondaryAuditorName());
				oldFormRating.setFaLocation(formRatingObj.getFaLocation());
				oldFormRating.setFaDateAssigned(formRatingObj.getFaDateAssigned());
				oldFormRating.setFaDateCompleted(formRatingObj.getFaDateCompleted());
				oldFormRating.setFaStartTime(formRatingObj.getFaStartTime());
				oldFormRating.setFaEndTime(formRatingObj.getFaEndTime());
				oldFormRating.setSignoffTime(formRatingObj.getSignoffTime());
				oldFormRating.setOtp(formRatingObj.getOtp());

				// Handling spatial Exception which occurs on the updation of 'centerLocation'
				oldFormRating.setCenterLocation(formRatingObj.getCenterLocation());

				formRatingDAO.updateFormRatingData(connection, databaseName, oldFormRating.getId(), oldFormRating);
				return oldFormRating.getId();
			} else {
				LOGGER.debug("*** FormId '" + StringEscapeUtils.escapeJava(formId) + "' and partnerProjectId '" + StringEscapeUtils.escapeJava(partnerProjectId) + "' and TcId '"
						+ tcId + "' NOT EXISTS, Throwing exception..");
				throw new ReportingDbServiceException(
						"Cannot update FieldAuditor Submit for non-existing entry of FormRatingTable for FormId '"
								+ formId + "' and partnerProjectId '" + partnerProjectId + "' and tcId '" + tcId
								+ "'.");
			}

		} catch (Exception e) {
			LOGGER.error("Unable to update FieldAuditor Submit data of FormRatingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Unable to update FieldAuditor Submit data of FormRatingData: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to update the Center-In-Charge response for the form which the Center-In-Charge is submitted.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the details of Center-In-Charge response.
	 * @param partnerProjectId the partnerprojectId.
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public String updateFormRatingDataCenterInChargeSubmit(Connection connection, String databaseName, String json,
			String partnerProjectId) throws ReportingDbServiceException {
		LOGGER.debug(".inside updateFormRatingDataCenterInChargeSubmit(con) method of FormRatingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			FormRatingTable formRatingObj = processJsonAndGetCICFields(json);
			String formId = formRatingObj.getFormId();
			String tcId = formRatingObj.getTcId();
			FormRatingTable oldFormRating = getFormRatingIfExists(connection, databaseName, formId, tcId,
					partnerProjectId);
			if (oldFormRating != null) { // Form Rating must be exist, or throwing exception.
				LOGGER.debug(
						"*** FormRatingTable entry exists, Updating FormRatingTable's Center-Incharge submit data.");
				LOGGER.debug("Old Form Rating : " + StringEscapeUtils.escapeJava(oldFormRating.toString()));
				oldFormRating.setSaScore(formRatingObj.getSaScore());
				int variance = formRatingObj.getSaScore() - oldFormRating.getFaScore();
				oldFormRating.setVariance(variance);
				oldFormRating.setSaDateAssigned(formRatingObj.getSaDateAssigned());
				oldFormRating.setSaDateCompletion(formRatingObj.getSaDateCompletion());
				oldFormRating.setSaStartTime(formRatingObj.getSaStartTime());
				oldFormRating.setSaEndTime(formRatingObj.getSaEndTime());

				// Handling spatial Exception which occurs on the updation of 'centerLocation'
				oldFormRating.setCenterLocation(formRatingObj.getCenterLocation());

				LOGGER.debug("New Form Rating CIC: " + StringEscapeUtils.escapeJava(oldFormRating.toString()));
				formRatingDAO.updateFormRatingData(connection, databaseName, oldFormRating.getId(), oldFormRating);
				return oldFormRating.getId();
			} else {
				LOGGER.debug("*** FormId '" + StringEscapeUtils.escapeJava(formId) + "' and partnerProjectId '" + StringEscapeUtils.escapeJava(partnerProjectId) + "' and TcId '"
						+ tcId + "' NOT EXISTS, Throwing exception..");
				throw new ReportingDbServiceException(
						"Cannot update Center-Incharge Submit data for non-existing entry of FormRatingTable for FormId '"
								+ formId + "' and partnerProjectId '" + partnerProjectId + "' and TcId '" + tcId
								+ "'.");
			}

		} catch (Exception e) {
			LOGGER.error("Unable to update Center-Incharge Submit data of FormRatingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Unable to update Center-Incharge Submit data of FormRatingData: " + e.getMessage(), e);
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
		LOGGER.debug(".inside getFormUUIDListForPartnerProjectId(con) method of FormRatingServiceImpl");
		try {
			ReportDbDeleteRating deletRating = processDeleteRatingJsonAndGetObject(json);
			String projectId = deletRating.getProjectId();
			String partnerId = deletRating.getPartnerId();
			String tcId = deletRating.getTcId();
			PartnersReportingTable partnersReporting = partnersReportingDAO.getDataByProjectIdAndPartnerId(connection,
					databaseName, projectId, partnerId);
			if (partnersReporting != null) {
				deletRating.setPartnerProjectId(partnersReporting.getPartnerProjectId());
				List<String> formUUIDList = formRatingDAO.getFormUUIDForPartnerProjectIdAndTcId(connection,
						databaseName, partnersReporting.getPartnerProjectId(), tcId);
				LOGGER.debug("FormUUID List : " + StringEscapeUtils.escapeJava(formUUIDList.toString()));
				LOGGER.debug("FormUUID List : " + formUUIDList.size());
				return formUUIDList;
			} else {
				LOGGER.debug("*** ProjectId '" + projectId + "' and partnerId '" + partnerId
						+ "' NOT EXISTS..Throwing exception..");
				throw new Exception("PartnerProjectId not exists for ProjectId '" + projectId + "' and partnerId '"
						+ partnerId + "' to delete Rating forms of FormRatingTable.");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to get FormUUIDList to delete Rating from FormRatingTable, " + e);
			throw new ReportingDbServiceException(
					"Unable to get FormUUIDList to delete Rating from FormRatingTable, " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to delete the form rating entries for all the given formUUIDs.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param formUUIDList all the formUUIDs which are to be deleted.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	@Override
	public void deleteFormRatingDataByFormUUid(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside deleteFormRatingDataByFormUUid(con) method of FormRatingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			formRatingDAO.deleteFormRatingDataByFormUUid(connection, databaseName, formUUIDList);
		} catch (Exception e) {
			LOGGER.error("Unable to Delete FormRatingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Delete FormRatingData: " + e.getMessage(), e);
		}

	}
	
	/**
	 * This method is used to process the JSON and get the fields of Form Rating.
	 * @param inputJSON the input JSON passed from the processor.
	 * @return a {@link FormRatingTable} object which is having the required details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private FormRatingTable processJsonAndGetObject(String inputJSON) throws ReportingDbServiceException {
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject tcJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.TRAINING_CENTER);
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
			String saDateAssigned = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.START_DATE);
			String saDateCompletion = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.END_DATE);
			String saStartTime = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.START_TIME);
			String saEndTime = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.END_TIME);
			LOGGER.debug("Successfully Fetched : <saDateAssigned,saDateCompletion,saStartTime,saEndTime>");

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

			// Following values can be get from JSONObject "tcJsonObject".
			String tcName = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.TC_NAME);
			String tcId = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.TC_ID);
			String centerAddress = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.CENTER_ADDRESS);
			String centerInchargeName = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.CIC_NAME);
			String centerInchargePhone = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.CIC_PHONE);
			String district = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.C_DISTRICT);
			double latitude = JsonUtill.getDouble(tcJsonObj, ReportingDbJSONConstant.LATITUDE);
			double longitude = JsonUtill.getDouble(tcJsonObj, ReportingDbJSONConstant.LONGITUDE);
			String centerLocation = getCenterLocationInGeography(latitude, longitude);
			LOGGER.debug(
					"Successfully Fetched : <tcName,tcId,centerAddress,centerInchargeName,centerInchargePhone,latitude,longitude>");

			FormRatingTable obj = new FormRatingTable();
			obj.setFormId(formId);
			obj.setFormName(formName);
			obj.setTcName(tcName);
			obj.setTcId(tcId);
			obj.setCenterAddress(centerAddress);
			obj.setDistrict(district);
			obj.setCenterInchargeName(centerInchargeName);
			obj.setCenterInchargePhone(centerInchargePhone);
			obj.setCenterLocation(centerLocation);
			obj.setMaxMarks(maxMarks);
			obj.setSaScore(saScore);
			obj.setFaScore(faScore);
			obj.setVariance(variance);
			obj.setStatus(status);
			obj.setSaDateAssigned(saDateAssigned);
			obj.setSaDateCompletion(saDateCompletion);
			obj.setSaStartTime(saStartTime);
			obj.setSaEndTime(saEndTime);
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
			LOGGER.error("Cannot create FormRatingTable object from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot create FormRatingTable object from the JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to process the JSON and get the Field Auditor fields of Form Rating.
	 * @param inputJSON inputJSON the input JSON passed from the processor.
	 * @return  a {@link FormRatingTable} object which is having the Field Auditor details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private FormRatingTable processJsonAndGetFieldAuditorFields(String inputJSON) throws ReportingDbServiceException {
		LOGGER.debug("inside processJsonAndGetFieldAuditorFields of FormRatingServiceImpl");
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject tcJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.TRAINING_CENTER);
			JSONObject projectJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.PROJECT);
			JSONObject formJsonObject = JsonUtill.getJsonObject(projectJsonObj, ReportingDbJSONConstant.FORM);
//					JSONObject timingJsonObject = JsonUtill.getJsonObject(formJsonObject, ReportingDbJSONConstant.TIMING);
			JSONObject faJsonObject = JsonUtill.getJsonObject(formJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR);

			// Following values can be get from JSONObject "formJsonObject" directly.
			String formId = JsonUtill.getString(formJsonObject, ReportingDbJSONConstant.FORM_ID);
			String formName = JsonUtill.getString(formJsonObject, ReportingDbJSONConstant.FORM_NAME);
			int maxMarks = JsonUtill.getInt(formJsonObject, ReportingDbJSONConstant.MAX_MARKS);
//			int saScore = FormsUtil.getSAScore(formJsonObject);
			int faScore = FormsUtil.getFAScore(formJsonObject);
//			int variance = saScore - faScore;
			boolean status = JsonUtill.getBoolean(formJsonObject, ReportingDbJSONConstant.FORM_STATUS);
			LOGGER.debug("Successfully Fetched : <formId,formName,status,MaxMarks,FAScore>");

			// Following values can be get from JSONObject "timing".
//			String saDateAssigned = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.START_DATE);
//			String saDateCompletion = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.END_DATE);
//			String saStartTime = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.START_TIME);
//			String saEndTime = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.END_TIME);
//			LOGGER.debug("Successfully Fetched : <saDateAssigned,saDateCompletion,saStartTime,saEndTime>");

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

			// Following values can be get from JSONObject "tcJsonObject".
			String tcName = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.TC_NAME);
			String tcId = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.TC_ID);
			String centerAddress = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.CENTER_ADDRESS);
			String centerInchargeName = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.CIC_NAME);
			String centerInchargePhone = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.CIC_PHONE);
			double latitude = JsonUtill.getDouble(tcJsonObj, ReportingDbJSONConstant.LATITUDE);
			double longitude = JsonUtill.getDouble(tcJsonObj, ReportingDbJSONConstant.LONGITUDE);
			String centerLocation = getCenterLocationInGeography(latitude, longitude);
			LOGGER.debug(
					"Successfully Fetched : <tcName,tcId,centerAddress,centerInchargeName,centerInchargePhone,latitude,longitude>");

			FormRatingTable obj = new FormRatingTable();
			obj.setFormId(formId);
			obj.setFormName(formName);
			obj.setTcName(tcName);
			obj.setTcId(tcId);
			obj.setCenterAddress(centerAddress);
			obj.setCenterInchargeName(centerInchargeName);
			obj.setCenterInchargePhone(centerInchargePhone);
			obj.setCenterLocation(centerLocation);
			obj.setMaxMarks(maxMarks);
//			obj.setSaScore(saScore);
			obj.setFaScore(faScore);
//			obj.setVariance(variance);
			obj.setStatus(status);
//			obj.setSaDateAssigned(saDateAssigned);
//			obj.setSaDateCompletion(saDateCompletion);
//			obj.setSaStartTime(saStartTime);
//			obj.setSaEndTime(saEndTime);
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
			LOGGER.error("Cannot create FormRatingTable FieldAuditorFields object from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot create FormRatingTable FieldAuditorFields object from the JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to process the JSON and get the Center-In-Charge fields of Form Rating.
	 * @param inputJSON inputJSON the input JSON passed from the processor.
	 * @return  a {@link FormRatingTable} object which is having the Client Sponsor details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private FormRatingTable processJsonAndGetCICFields(String inputJSON) throws ReportingDbServiceException {
		LOGGER.debug("inside processJsonAndGetCICFields of FormRatingServiceImpl");
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject tcJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.TRAINING_CENTER);
			JSONObject projectJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.PROJECT);
			JSONObject formJsonObject = JsonUtill.getJsonObject(projectJsonObj, ReportingDbJSONConstant.FORM);
			JSONObject timingJsonObject = JsonUtill.getJsonObject(formJsonObject, ReportingDbJSONConstant.TIMING);
//					JSONObject faJsonObject = JsonUtill.getJsonObject(formJsonObject, ReportingDbJSONConstant.FIELD_AUDITOR);

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
			String saDateAssigned = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.START_DATE);
			String saDateCompletion = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.END_DATE);
			String saStartTime = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.START_TIME);
			String saEndTime = JsonUtill.getString(timingJsonObject, ReportingDbJSONConstant.END_TIME);
			LOGGER.debug("Successfully Fetched : <saDateAssigned,saDateCompletion,saStartTime,saEndTime>");

			// Following values can be get from JSONObject "faJsonObject".
			/*
			 * String faName = JsonUtill.getString(faJsonObject,
			 * ReportingDbJSONConstant.FIELD_AUDITOR_NAME); String faPhone =
			 * JsonUtill.getString(faJsonObject,
			 * ReportingDbJSONConstant.FIELD_AUDITOR_PHONE); String secondaryAuditorName =
			 * JsonUtill.getString(faJsonObject,
			 * ReportingDbJSONConstant.SEC_FIELD_AUDITOR_NAME); String faLocation =
			 * JsonUtill.getString(faJsonObject,
			 * ReportingDbJSONConstant.FIELD_AUDITOR_LOCATION); String faDateAssigned =
			 * JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_START_DATE);
			 * String faDateCompleted = JsonUtill.getString(faJsonObject,
			 * ReportingDbJSONConstant.FA_END_DATE); String faStartTime =
			 * JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.FA_START_TIME);
			 * String faEndTime = JsonUtill.getString(faJsonObject,
			 * ReportingDbJSONConstant.FA_END_TIME); String signoffTime =
			 * JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.SIGN_OFF_TIME);
			 * String otp = JsonUtill.getString(faJsonObject, ReportingDbJSONConstant.OTP);
			 * LOGGER.
			 * debug("Successfully Fetched : <faName,faPhone,secondaryAuditorName,faLocation,faDateAssigned,faDateCompleted,faStartTime,faEndTime,signoffTime,otp>"
			 * );
			 */
			// Following values can be get from JSONObject "tcJsonObject".
			String tcName = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.TC_NAME);
			String tcId = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.TC_ID);
			String centerAddress = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.CENTER_ADDRESS);
			String centerInchargeName = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.CIC_NAME);
			String centerInchargePhone = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.CIC_PHONE);
			double latitude = JsonUtill.getDouble(tcJsonObj, ReportingDbJSONConstant.LATITUDE);
			double longitude = JsonUtill.getDouble(tcJsonObj, ReportingDbJSONConstant.LONGITUDE);
			String centerLocation = getCenterLocationInGeography(latitude, longitude);
			LOGGER.debug(
					"Successfully Fetched : <tcName,tcId,centerAddress,centerInchargeName,centerInchargePhone,latitude,longitude>");

			FormRatingTable obj = new FormRatingTable();
			obj.setFormId(formId);
			obj.setFormName(formName);
			obj.setTcName(tcName);
			obj.setTcId(tcId);
			obj.setCenterAddress(centerAddress);
			obj.setCenterInchargeName(centerInchargeName);
			obj.setCenterInchargePhone(centerInchargePhone);
			obj.setCenterLocation(centerLocation);
			obj.setMaxMarks(maxMarks);
			obj.setSaScore(saScore);
//			obj.setFaScore(faScore);
//			obj.setVariance(variance);
			obj.setStatus(status);
			obj.setSaDateAssigned(saDateAssigned);
			obj.setSaDateCompletion(saDateCompletion);
			obj.setSaStartTime(saStartTime);
			obj.setSaEndTime(saEndTime);
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
			LOGGER.error("Cannot create FormRatingTable CIC fields object from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot create FormRatingTable CIC fields object from the JSON: " + e.getMessage(), e);
		}
	}

	private String getCenterLocationInGeography(double latitude, double longitude) throws SQLServerException {
		Geography geography = Geography.point(latitude, longitude, ReportingDatabaseQueryConstants.SRID_FOR_GEOGRAPHY);
		LOGGER.debug("Geography in string : " + geography.toString());
		return geography.toString();

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
	private String getFormUUIDIfExists(Connection connection, String databaseName, String formId, String tcId,
			String partnerProjectId) throws ReportingDbServiceException {
		LOGGER.debug(".inside getFormUUIDIfExists method of FormRatingServiceImpl class.");
		String formUUID = null;
		try {
			FormRatingTable object = formRatingDAO.getFormByFormIdAndTcIdAndPartnerProjectId(connection, databaseName,
					formId, tcId, partnerProjectId);
			if (object != null) {
				LOGGER.debug("FormId '" + StringEscapeUtils.escapeJava(formId) + "' and partnerProjectId '" + StringEscapeUtils.escapeJava(partnerProjectId) + "' and tcId '" + tcId
						+ "' EXISTS..");
				formUUID = object.getId();
			}
		} catch (Exception e) {
			LOGGER.error("Cannot check if FormId and partnerProjectId and TcId is already there.", e);
			throw new ReportingDbServiceException(
					"Cannot check if FormId and partnerProjectId and TcId is already there, " + e.getMessage());
		}
		return formUUID;
	}

	/**
	 * This helper method is used to get the form rating details for the given formId and partnerProjectId
	 * @param connection connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param formId the id of the form.
	 * @param partnerProjectId the partnerProjectId
	 * @return a {@link FormRatingTable} object which is having the required details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private FormRatingTable getFormRatingIfExists(Connection connection, String databaseName, String formId,
			String tcId, String partnerProjectId) throws ReportingDbServiceException {
		LOGGER.debug(".inside getFormRatingIfExists method of FormRatingServiceImpl class.");

		try {
			FormRatingTable object = formRatingDAO.getFormByFormIdAndTcIdAndPartnerProjectId(connection, databaseName,
					formId, tcId, partnerProjectId);
			if (object != null) {
				LOGGER.debug("FormId '" + StringEscapeUtils.escapeJava(formId) + "' and partnerProjectId '" + StringEscapeUtils.escapeJava(partnerProjectId) + "' and tcId '" + tcId
						+ "' EXISTS..");
				return object;
			}
		} catch (Exception e) {
			LOGGER.error("Cannot check if FormId and partnerProjectId and TcId is already there.", e);
			throw new ReportingDbServiceException(
					"Cannot check if FormId and partnerProjectId and TcId is already there." + e.getMessage());
		}
		return null;
	}


	/**
	 * This method is used to process the JSON and get the training center fields of Form Rating.
	 * @param inputJSON inputJSON the input JSON passed from the processor.
	 * @return  a {@link ReportDBTrainingCenter} object which is having the training center details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private ReportDBTrainingCenter processTCJSONAndGetObject(String trainingCenterJson)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside processTCJSONAndGetObject method of FormRatingServiceImpl class.");
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(trainingCenterJson);
			JSONObject tcJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.TRAINING_CENTER);
			String tcName = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.TC_NAME);
			String tcId = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.TC_ID);
			String centerAddress = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.CENTER_ADDRESS);
			String district = JsonUtill.getString(tcJsonObj, ReportingDbJSONConstant.C_DISTRICT);
			double latitude = JsonUtill.getDouble(tcJsonObj, ReportingDbJSONConstant.LATITUDE);
			double longitude = JsonUtill.getDouble(tcJsonObj, ReportingDbJSONConstant.LONGITUDE);
			String centerLocation = getCenterLocationInGeography(latitude, longitude);
			LOGGER.debug("Successfully Fetched : <tcName,tcId,centerAddress,district,latitude,longitude>");

			String projectId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PROJECT_ID);
			String partnerId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PARTNER_ID);
			ReportDBTrainingCenter obj = new ReportDBTrainingCenter();
			obj.setTcId(tcId);
			obj.setTcName(tcName);
			obj.setCenterAddress(centerAddress);
			obj.setDistrict(district);
			obj.setCenterLocation(centerLocation);
			obj.setPartnerId(partnerId);
			obj.setProjectId(projectId);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Cannot create ReportDBTrainingCenter object from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot create ReportDBTrainingCenter object from the JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to process the JSON and get the Center-in-charge fields of Form Rating.
	 * @param inputJSON inputJSON the input JSON passed from the processor.
	 * @return  a {@link ReportDBCenterIncharge} object which is having the CenterIncharge details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private ReportDBCenterIncharge processCICJSONAndGetObject(String trainingCenterJson)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside processCICJSONAndGetObject method of FormRatingServiceImpl class.");
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(trainingCenterJson);
			JSONObject cicJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.CENTER_IN_CHARGE);

			String tcId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.TC_ID);
			String projectId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PROJECT_ID);
			String partnerId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PARTNER_ID);
			LOGGER.debug("Successfully Fetched : <tcId,projectId,partnerId>");

			String centerInchargeName = JsonUtill.getString(cicJsonObj, ReportingDbJSONConstant.CIC_NAME);
			String centerInchargePhone = JsonUtill.getString(cicJsonObj, ReportingDbJSONConstant.CIC_PHONE);
			LOGGER.debug("Successfully Fetched : <centerInchargeName,centerInchargePhone>");
			ReportDBCenterIncharge obj = new ReportDBCenterIncharge();
			obj.setTcid(tcId);
			obj.setPartnerId(partnerId);
			obj.setProjectId(projectId);
			obj.setCenterInchargeName(centerInchargeName);
			obj.setCenterInchargePhone(centerInchargePhone);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Cannot create ReportDBTrainingCenter object from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot create ReportDBTrainingCenter object from the JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to process the JSON and get the FieldAuditor fields of Form Rating.
	 * @param inputJSON inputJSON the input JSON passed from the processor.
	 * @return  a {@link ReportDBFieldAuditor} object which is having the FieldAuditor details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private ReportDBFieldAuditor processFAJsonAndGetObject(String inputJSON) throws ReportingDbServiceException {
		LOGGER.debug(".inside processFAJsonAndGetObject method of FormRatingServiceImpl");
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject faJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.FIELD_AUDITOR);
			String projectId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PROJECT_ID);
			String partnerId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PARTNER_ID);
			String formId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.FORM_ID);
			String tcId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.TC_ID);
			LOGGER.debug("Successfully Fetched : <projectId,partnerId,formId,tcId>");
			String faName = JsonUtill.getString(faJsonObj, ReportingDbJSONConstant.FIELD_AUDITOR_NAME);
			String faPhone = JsonUtill.getString(faJsonObj, ReportingDbJSONConstant.FIELD_AUDITOR_PHONE);
			LOGGER.debug("Successfully Fetched : <faName,faPhone>");
			ReportDBFieldAuditor obj = new ReportDBFieldAuditor();
			obj.setProjectId(projectId);
			obj.setPartnerId(partnerId);
			obj.setFormId(formId);
			obj.setTcId(tcId);
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
	 * This method is used to process the JSON and get the projectId and partnerId to delete the rating forms.
	 * @param inputJSON inputJSON the input JSON passed from the processor.
	 * @return a {@link ReportDbDeleteRating} object which is having the projectId and partnerId details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	private ReportDbDeleteRating processDeleteRatingJsonAndGetObject(String inputJSON)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside processDeleteGradingJsonAndGetObject method of FormGradingServiceImpl");
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			String projectId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PROJECT_ID);
			String partnerId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.PARTNER_ID);
			String tcId = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.TC_ID);
			ReportDbDeleteRating obj = new ReportDbDeleteRating();
			obj.setProjectId(projectId);
			obj.setPartnerId(partnerId);
			obj.setTcId(tcId);
			return obj;
		} catch (Exception e) {
			LOGGER.error("Cannot create FormGradingTable DeleteGrading object from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Cannot create DeleteGrading object from the JSON: " + e.getMessage(),
					e);
		}
	}



}
