package com.pwc.grading.reportingdb.service.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.ReportingDbJSONConstant;
import com.pwc.grading.reportingdb.dao.IParameterGradingDAO;
import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.dao.impl.ParameterGradingDAOImpl;
import com.pwc.grading.reportingdb.model.ParameterGradingTable;
import com.pwc.grading.reportingdb.service.IParameterGradingService;
import com.pwc.grading.reportingdb.service.exp.ReportingDbServiceException;
import com.pwc.grading.reportingdb.util.ParametersUtil;
import com.pwc.grading.util.JsonUtill;

/**
 * Implementation class for {@link IParameterGradingService}
 *
 */
//@Singleton
public class ParameterGradingServiceImpl implements IParameterGradingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParameterGradingServiceImpl.class);

//	@Inject
	private IParameterGradingDAO parameterGradingDAO;

	public ParameterGradingServiceImpl() {
		parameterGradingDAO = new ParameterGradingDAOImpl();
	}

	/**
	 * This method is used to add the parameters of a grading form if entry not
	 * exists.If exists it <strong>will not add a new entry again </strong>, but
	 * <strong>it will update the existing entry.</strong>
	 * 
	 * @param connection   used to perform the database operations in a single
	 *                     transaction.
	 * @param databaseName the database name indicating which database is used for
	 *                     this operation.
	 * @param json         the JSON containing all the sections and parameters.
	 * @param formUUID     the FormUUID.
	 * @throws ReportingDbServiceException if any exception occurs when processing
	 *                                     the given JSON (or) when any error occurs
	 *                                     while performing this operation.
	 */
	@Override
	public void addParameterGradingData(Connection connection, String databaseName, String json, String formUUID)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside addParameterGradingData(con) method of ParameterGradingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			List<ParameterGradingTable> parameterGradingTableList = processJsonAndGetList(json, formUUID);
			for (ParameterGradingTable obj : parameterGradingTableList) {
				boolean exists = checkParameterGradingEntryExists(connection, databaseName, obj);
				if (exists) {
					LOGGER.debug("*** ParameterGradingTable Entry exists, Updating Scores in it..");
					parameterGradingDAO.updateParameterGradingData(connection, databaseName, obj);
				} else {
					LOGGER.debug("*** ParameterGradingTable Entry NOT Exists, Creating new entry..");
					parameterGradingDAO.addParameterGradingData(connection, databaseName, obj);
				}
			}
			// parameterGradingDAO.addParameterGradingDataList(connection,databaseName,
			// parameterGradingTableList);
		} catch (Exception e) {
			LOGGER.error("Unable to add ParameterGradingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to add ParameterGradingData: " + e.getMessage(), e);
		}

	}

	/**
	 * This method is used to the update the parameter grading data, the
	 * <strong>entry should exists</strong> to update or this method will throw
	 * {@link ReportingDbServiceException}
	 * 
	 * @param connection   used to perform the database operations in a single
	 *                     transaction.
	 * @param databaseName the database name indicating which database is used for
	 *                     this operation.
	 * @param json         the JSON containing all the sections and parameters.
	 * @param formUUID     the FormUUID.
	 * @throws ReportingDbServiceException if any exception occurs when processing
	 *                                     the given JSON (or) when any error occurs
	 *                                     while performing this operation.
	 */
	@Override
	public void updateParameterGradingData(Connection connection, String databaseName, String json, String formUUID)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updateParameterGradingData(con) method of ParameterGradingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			List<ParameterGradingTable> parameterGradingTableList = processJsonAndGetList(json, formUUID);
			for (ParameterGradingTable obj : parameterGradingTableList) {
				boolean exists = checkParameterGradingEntryExists(connection, databaseName, obj);
				if (exists) {
					LOGGER.debug("*** ParameterGradingTable Entry exists, Updating Scores in it..");
					parameterGradingDAO.updateParameterGradingData(connection, databaseName, obj);
				} else {
					LOGGER.debug("*** ParameterGradingTable Entry NOT Exists, Throwing exception..");
					throw new ReportingDbServiceException(
							"Cannot update non-existing entry of ParameterGradingTable with formUUID '"
									+ obj.getFormId() + "', SectionID '" + obj.getSectionId() + "', and parameterID '"
									+ obj.getParameterId() + "'.");
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Update ParameterGradingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Update ParameterGradingData: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to delete the parameter grading data for all the given
	 * formUUIDs.
	 * 
	 * @param connection   used to perform the database operations in a single
	 *                     transaction.
	 * @param databaseName the database name indicating which database is used for
	 *                     this operation.
	 * @param formUUIDList the list of formUUIDs.
	 * @throws ReportingDbServiceException if any exception occurs when processing
	 *                                     the given JSON (or) when any error occurs
	 *                                     while performing this operation.
	 */
	@Override
	public void deleteParameterGradingData(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside deleteParameterGradingData(con) method of ParameterGradingServiceImpl class.");
//		LOGGER.debug("Incoming service connection : "+connection);
		try {
			if (formUUIDList != null && formUUIDList.size() > 0) {
				LOGGER.debug("Deleting ParameterGradingTable data for formUUID List: " + StringEscapeUtils.escapeJava(formUUIDList.toString()));
				parameterGradingDAO.deleteParameterGradingData(connection, databaseName, formUUIDList);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Delete ParameterGradingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Delete ParameterGradingData: " + e.getMessage(), e);
		}

	}

	/**
	 * This method is used to update all the FA Scores in parameter grading data.
	 * 
	 * @param connection   used to perform the database operations in a single
	 *                     transaction.
	 * @param databaseName the database name indicating which database is used for
	 *                     this operation.
	 * @param json         the JSON containing all the sections and parameters.
	 * @param formUUID     the FormUUID.
	 * @throws ReportingDbServiceException if any exception occurs when processing
	 *                                     the given JSON (or) when any error occurs
	 *                                     while performing this operation.
	 */
	@Override
	public void updateParameterGradingDataFA(Connection connection, String databaseName, String json, String formUUID)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updateParameterGradingDataFA(con) method of ParameterGradingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			List<ParameterGradingTable> parameterGradingTableList = processJsonAndGetListFA(json, formUUID);
			for (ParameterGradingTable newObj : parameterGradingTableList) {
				ParameterGradingTable oldObj = getParameterGradingEntryExists(connection, databaseName, newObj);
				if (oldObj != null) {
					LOGGER.debug("*** ParameterGradingTable Entry exists, Updating FA Scores in it..");
					oldObj.setFaScore(newObj.getFaScore());
					oldObj.setFaRemark(newObj.getFaRemark());
					int variance = oldObj.getSaScore() - newObj.getFaScore();
					oldObj.setVariance(variance);
					parameterGradingDAO.updateParameterGradingData(connection, databaseName, oldObj);
				} else {
					LOGGER.debug("*** ParameterGradingTable Entry NOT Exists, Throwing exception..");
					throw new ReportingDbServiceException(
							"Cannot update FAScores of non-existing entry of ParameterGradingTable with formUUID '"
									+ newObj.getFormId() + "', SectionID '" + newObj.getSectionId()
									+ "', and parameterID '" + newObj.getParameterId() + "'.");
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Update FAScores ParameterGradingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Update FAScores ParameterGradingData: " + e.getMessage(),
					e);
		}
	}

	/**
	 * This method is used to update all the SA Scores in parameter grading data.
	 * 
	 * @param connection   used to perform the database operations in a single
	 *                     transaction.
	 * @param databaseName the database name indicating which database is used for
	 *                     this operation.
	 * @param json         the JSON containing all the sections and parameters.
	 * @param formUUID     the FormUUID.
	 * @throws ReportingDbServiceException if any exception occurs when processing
	 *                                     the given JSON (or) when any error occurs
	 *                                     while performing this operation.
	 */
	@Override
	public void updateParameterGradingDataCS(Connection connection, String databaseName, String json, String formUUID)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updateParameterGradingDataCS(con) method of ParameterGradingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			List<ParameterGradingTable> parameterGradingTableList = processJsonAndGetListCS(json, formUUID);
			for (ParameterGradingTable newObj : parameterGradingTableList) {
				ParameterGradingTable oldObj = getParameterGradingEntryExists(connection, databaseName, newObj);
				if (oldObj != null) {
					LOGGER.debug("*** ParameterGradingTable Entry exists, Updating SA Scores in it..");
					oldObj.setSaScore(newObj.getSaScore());
					oldObj.setSaRemark(newObj.getSaRemark());
					int variance = newObj.getSaScore() - oldObj.getFaScore();
					oldObj.setVariance(variance);
					parameterGradingDAO.updateParameterGradingData(connection, databaseName, oldObj);
				} else {
					LOGGER.debug("*** ParameterGradingTable Entry NOT Exists, Throwing exception..");
					throw new ReportingDbServiceException(
							"Cannot update SAScores of non-existing entry of ParameterGradingTable with formUUID '"
									+ newObj.getFormId() + "', SectionID '" + newObj.getSectionId()
									+ "', and parameterID '" + newObj.getParameterId() + "'.");
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Update SAScores ParameterGradingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Update SAScores ParameterGradingData: " + e.getMessage(),
					e);
		}

	}

	/**
	 * This method is used to process the JSON and get all the sections and its
	 * parameters in list.
	 * 
	 * @param inputJSON the JSON from the processor.
	 * @param formUUID  the formUUID.
	 * @return list of sections and its parameters.
	 * @throws ReportingDbServiceException if any error occurs in getting data from
	 *                                     the JSON.
	 */
	private List<ParameterGradingTable> processJsonAndGetList(String inputJSON, String formUUID)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside processJsonAndGetList method of ParameterGradingServiceImpl class.");
		try {
			List<ParameterGradingTable> dataList = new ArrayList<ParameterGradingTable>();
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject projectJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.PROJECT);
			JSONObject formJsonObject = JsonUtill.getJsonObject(projectJsonObj, ReportingDbJSONConstant.FORM);
			JSONArray sectionsJsonArray = JsonUtill.getJsonArray(formJsonObject, ReportingDbJSONConstant.SECTIONS);
			LOGGER.info("Number of Sections in the form : " + sectionsJsonArray.size());
			for (int i = 0; i < sectionsJsonArray.size(); i++) {
				JSONObject sectionJsonObj = (JSONObject) sectionsJsonArray.get(i);
				String sectionId = JsonUtill.getString(sectionJsonObj, ReportingDbJSONConstant.SECTION_ID);
				JSONArray parameterJsonArray = JsonUtill.getJsonArray(sectionJsonObj,
						ReportingDbJSONConstant.PARAMETERS);
				LOGGER.info("Number of Parameters in Section " + (i + 1) + " : " + parameterJsonArray.size());
				for (int j = 0; j < parameterJsonArray.size(); j++) {
					JSONObject parameterJsonObj = (JSONObject) parameterJsonArray.get(j);
					String parameterId = JsonUtill.getString(parameterJsonObj, ReportingDbJSONConstant.PARAMETER_ID);
					int maxMarks = JsonUtill.getInt(parameterJsonObj, ReportingDbJSONConstant.MAX_MARKS);
					int saScore = ParametersUtil.getSAScore(parameterJsonObj);
					int faScore = ParametersUtil.getFAScore(parameterJsonObj);

					String saRemark = JsonUtill.getString(parameterJsonObj, ReportingDbJSONConstant.SA_REMARK);
					String faRemark = JsonUtill.getString(parameterJsonObj, ReportingDbJSONConstant.FA_REMARK);

					int variance = saScore - faScore; // Calculating variance.
					LOGGER.debug("Successfully fetched the required details of ParameterGradingTable from the JSON.");
					ParameterGradingTable obj = new ParameterGradingTable(); // Creating object
					obj.setFormId(formUUID);
					obj.setSectionId(sectionId);
					obj.setParameterId(parameterId);
					obj.setMaxmarks(maxMarks);
					obj.setFaScore(faScore);
					obj.setSaScore(saScore);
					obj.setVariance(variance);
					obj.setFaRemark(faRemark);
					obj.setSaRemark(saRemark);
					dataList.add(obj);
				}
			}
			return dataList;
		} catch (Exception e) {
			LOGGER.error("Cannot create ParameterGradingTable list from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot create ParameterGradingTable list from the JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to process the JSON and get all the sections and its
	 * parameters for Field Auditor in list.
	 * 
	 * @param inputJSON the JSON from the processor.
	 * @param formUUID  the formUUID.
	 * @return list of sections and its parameters.
	 * @throws ReportingDbServiceException if any error occurs in getting data from
	 *                                     the JSON.
	 */
	private List<ParameterGradingTable> processJsonAndGetListFA(String inputJSON, String formUUID)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside processJsonAndGetListFA method of ParameterGradingServiceImpl class.");
		try {
			List<ParameterGradingTable> dataList = new ArrayList<ParameterGradingTable>();
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject projectJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.PROJECT);
			JSONObject formJsonObject = JsonUtill.getJsonObject(projectJsonObj, ReportingDbJSONConstant.FORM);
			JSONArray sectionsJsonArray = JsonUtill.getJsonArray(formJsonObject, ReportingDbJSONConstant.SECTIONS);
			LOGGER.info("Number of Sections in the form : " + sectionsJsonArray.size());
			for (int i = 0; i < sectionsJsonArray.size(); i++) {
				JSONObject sectionJsonObj = (JSONObject) sectionsJsonArray.get(i);
				String sectionId = JsonUtill.getString(sectionJsonObj, ReportingDbJSONConstant.SECTION_ID);
				JSONArray parameterJsonArray = JsonUtill.getJsonArray(sectionJsonObj,
						ReportingDbJSONConstant.PARAMETERS);
				LOGGER.info("Number of Parameters in Section " + (i + 1) + " : " + parameterJsonArray.size());
				for (int j = 0; j < parameterJsonArray.size(); j++) {
					JSONObject parameterJsonObj = (JSONObject) parameterJsonArray.get(j);
					String parameterId = JsonUtill.getString(parameterJsonObj, ReportingDbJSONConstant.PARAMETER_ID);
					int maxMarks = JsonUtill.getInt(parameterJsonObj, ReportingDbJSONConstant.MAX_MARKS);
					// int saScore = ParametersUtil.getSAScore(parameterJsonObj);
					int faScore = ParametersUtil.getFAScore(parameterJsonObj);

					String faRemark = parameterJsonObj.get(ReportingDbJSONConstant.FA_REMARK).toString();
					// int variance = saScore - faScore; //Calculating variance.
					LOGGER.debug(
							"Successfully fetched the required details of ParameterGradingTable FA from the JSON.");
					ParameterGradingTable obj = new ParameterGradingTable(); // Creating object
					obj.setFormId(formUUID);
					obj.setSectionId(sectionId);
					obj.setParameterId(parameterId);
					obj.setMaxmarks(maxMarks);
					obj.setFaScore(faScore);
					obj.setFaRemark(faRemark);
					// obj.setSaScore(saScore);
					// obj.setVariance(variance);
					dataList.add(obj);
				}
			}
			return dataList;
		} catch (Exception e) {
			LOGGER.error("Cannot create FA ParameterGradingTable list from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot create FA ParameterGradingTable list from the JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to process the JSON and get all the sections and its
	 * parameters for Client Sponsor, in list.
	 * 
	 * @param inputJSON the JSON from the processor.
	 * @param formUUID  the formUUID.
	 * @return list of sections and its parameters.
	 * @throws ReportingDbServiceException if any error occurs in getting data from
	 *                                     the JSON.
	 */
	private List<ParameterGradingTable> processJsonAndGetListCS(String inputJSON, String formUUID)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside processJsonAndGetListCS method of ParameterGradingServiceImpl class.");
		try {
			List<ParameterGradingTable> dataList = new ArrayList<ParameterGradingTable>();
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputJSON);
			JSONObject projectJsonObj = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.PROJECT);
			JSONObject formJsonObject = JsonUtill.getJsonObject(projectJsonObj, ReportingDbJSONConstant.FORM);
			JSONArray sectionsJsonArray = JsonUtill.getJsonArray(formJsonObject, ReportingDbJSONConstant.SECTIONS);
			LOGGER.info("Number of Sections in the form : " + sectionsJsonArray.size());
			for (int i = 0; i < sectionsJsonArray.size(); i++) {
				JSONObject sectionJsonObj = (JSONObject) sectionsJsonArray.get(i);
				String sectionId = JsonUtill.getString(sectionJsonObj, ReportingDbJSONConstant.SECTION_ID);
				JSONArray parameterJsonArray = JsonUtill.getJsonArray(sectionJsonObj,
						ReportingDbJSONConstant.PARAMETERS);
				LOGGER.info("Number of Parameters in Section " + (i + 1) + " : " + parameterJsonArray.size());
				for (int j = 0; j < parameterJsonArray.size(); j++) {
					JSONObject parameterJsonObj = (JSONObject) parameterJsonArray.get(j);
					String parameterId = JsonUtill.getString(parameterJsonObj, ReportingDbJSONConstant.PARAMETER_ID);
					int maxMarks = JsonUtill.getInt(parameterJsonObj, ReportingDbJSONConstant.MAX_MARKS);
					int saScore = ParametersUtil.getSAScore(parameterJsonObj);
//					int faScore = ParametersUtil.getFAScore(parameterJsonObj);
//					int variance = saScore - faScore;			//Calculating variance.
					LOGGER.debug(
							"Successfully fetched the required details of ParameterGradingTable CS from the JSON.");
					String saRemark = parameterJsonObj.containsKey(ReportingDbJSONConstant.SA_REMARK)
							? parameterJsonObj.get(ReportingDbJSONConstant.SA_REMARK).toString()
							: "";
					ParameterGradingTable obj = new ParameterGradingTable(); // Creating object
					obj.setFormId(formUUID);
					obj.setSectionId(sectionId);
					obj.setParameterId(parameterId);
					obj.setMaxmarks(maxMarks);
//					obj.setFaScore(faScore);
					obj.setSaScore(saScore);
					obj.setSaRemark(saRemark);
//					obj.setVariance(variance);
					dataList.add(obj);
				}
			}
			return dataList;
		} catch (Exception e) {
			LOGGER.error("Cannot create CS ParameterGradingTable list from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot CS create ParameterGradingTable list from the JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to check if the parameter grading entry is exists or not
	 * 
	 * @param connection   used to perform the database operations in a single
	 *                     transaction.
	 * @param databaseName the database name indicating which database is used for
	 *                     this operation.
	 * @param object       the object containing formUUID, sectionId and
	 *                     ParameterId.
	 * @return true if exists, false if not exists.
	 * @throws ReportingDbServiceException if any exception occurs when checking.
	 */
	private boolean checkParameterGradingEntryExists(Connection connection, String databaseName,
			ParameterGradingTable object) throws ReportingDbServiceException {
		try {
			ParameterGradingTable pgObject = parameterGradingDAO.getPGDataByFormSectionParameterIds(connection,
					databaseName, object.getFormId(), object.getSectionId(), object.getParameterId());
			if (pgObject != null) {
				return true;
			}
		} catch (ReportingDbDAOException e) {
			LOGGER.error("Cannot check if " + object + " exists, ", e);
			throw new ReportingDbServiceException("Cannot check if " + object + " exists, " + e.getMessage(), e);
		}
		return false;
	}

	/**
	 * This method is used to get the parameter grading entry if exists.
	 * 
	 * @param connection   used to perform the database operations in a single
	 *                     transaction.
	 * @param databaseName the database name indicating which database is used for
	 *                     this operation.
	 * @param object       the object containing formUUID, sectionId and
	 *                     ParameterId.
	 * @return the object if exists, null if not exists.
	 * @throws ReportingDbServiceException if any exception occurs when fetching.
	 */
	private ParameterGradingTable getParameterGradingEntryExists(Connection connection, String databaseName,
			ParameterGradingTable object) throws ReportingDbServiceException {
		try {
			ParameterGradingTable pgObject = parameterGradingDAO.getPGDataByFormSectionParameterIds(connection,
					databaseName, object.getFormId(), object.getSectionId(), object.getParameterId());
			if (pgObject != null) {
				return pgObject;
			}
		} catch (ReportingDbDAOException e) {
			LOGGER.error("Cannot check if " + object + " exists, ", e);
			throw new ReportingDbServiceException("Cannot check if " + object + " exists, " + e.getMessage(), e);
		}
		return null;
	}

}
