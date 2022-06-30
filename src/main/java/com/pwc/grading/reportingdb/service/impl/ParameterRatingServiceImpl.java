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
import com.pwc.grading.reportingdb.dao.IParameterRatingDAO;
import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.dao.impl.ParameterRatingDAOImpl;
import com.pwc.grading.reportingdb.model.ParameterRatingTable;
import com.pwc.grading.reportingdb.service.IParameterRatingService;
import com.pwc.grading.reportingdb.service.exp.ReportingDbServiceException;
import com.pwc.grading.reportingdb.util.ParametersUtil;
import com.pwc.grading.util.JsonUtill;

/**
 * Implementation class for {@link IParameterRatingService}
 *
 */
//@Singleton
public class ParameterRatingServiceImpl implements IParameterRatingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParameterRatingServiceImpl.class);

//	@Inject
	private IParameterRatingDAO parameterRatingDAO;

	public ParameterRatingServiceImpl() {
		parameterRatingDAO = new ParameterRatingDAOImpl();
	}

	/**
	 * This method is used to add the parameters of a rating form if entry not
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
	public void addParameterRatingData(Connection connection, String databaseName, String json, String formUUID)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside addParameterRatingData(con) method of ParameterRatingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			List<ParameterRatingTable> parameterRatingList = processJsonAndGetList(json, formUUID);
			LOGGER.debug("ParameterRatingTable entries size: " + parameterRatingList.size());
			for (ParameterRatingTable obj : parameterRatingList) {
				boolean exists = checkParameterRatingEntryExists(connection, databaseName, obj);
				if (exists) {
					LOGGER.debug("*** ParameterRatingTable Entry exists, Updating Scores in it..");
					parameterRatingDAO.updateParameterRatingData(connection, databaseName, obj);
				} else {
					LOGGER.debug("*** ParameterRatingTable Entry NOT Exists, Creating new entry..");
					parameterRatingDAO.addParameterRatingData(connection, databaseName, obj);
				}
			}
			// parameterRatingDAO.addParameterRatingDataList(connection,databaseName,
			// parameterRatingList);
		} catch (Exception e) {
			LOGGER.error("Unable to add ParameterRatingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to add ParameterRatingData: " + e.getMessage(), e);
		}

	}

	/**
	 * This method is used to the update the parameter rating data, the
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
	public void updateParameterRatingData(Connection connection, String databaseName, String json, String formUUID)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updateParameterRatingData(con) method of ParameterRatingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			List<ParameterRatingTable> parameterRatingList = processJsonAndGetList(json, formUUID);
			LOGGER.debug("ParameterRatingTable entries size: " + parameterRatingList.size());
			for (ParameterRatingTable obj : parameterRatingList) {
				boolean exists = checkParameterRatingEntryExists(connection, databaseName, obj);
				if (exists) {
					LOGGER.debug("*** ParameterRatingTable Entry exists, Updating Scores in it..");
					parameterRatingDAO.updateParameterRatingData(connection, databaseName, obj);
				} else {
					LOGGER.debug("*** ParameterRatingTable Entry NOT Exists, Throwing exception..");
					throw new ReportingDbServiceException(
							"Cannot update non-existing entry of ParameterRatingTable with formUUID '" + obj.getFormId()
									+ "', SectionID '" + obj.getSectionId() + "', and parameterID '"
									+ obj.getParameterId() + "'.");
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Update ParameterRatingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Update ParameterRatingData: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to delete the parameter rating data for all the given
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
	public void deleteParameterRatingData(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside deleteParameterRatingData(con) method of ParameterRatingServiceImpl class.");
//		LOGGER.debug("Incoming service connection : "+connection);
		try {
			if (formUUIDList != null && formUUIDList.size() > 0) {
				LOGGER.debug("Deleting ParameterRatingData data for formUUID List: " + StringEscapeUtils.escapeJava(formUUIDList.toString()));
				parameterRatingDAO.deleteParameterRatingData(connection, databaseName, formUUIDList);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Delete ParameterRatingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Delete ParameterRatingData: " + e.getMessage(), e);
		}

	}

	/**
	 * This method is used to update all the FA Scores in parameter rating data.
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
	public void updateParameterRatingDataFA(Connection connection, String databaseName, String json, String formUUID)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updateParameterRatingDataFA(con) method of ParameterRatingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			List<ParameterRatingTable> parameterRatingList = processJsonAndGetList(json, formUUID);
			LOGGER.debug("ParameterRatingTable entries size: " + parameterRatingList.size());
			for (ParameterRatingTable newObj : parameterRatingList) {
				ParameterRatingTable oldObj = getParameterRatingEntryExists(connection, databaseName, newObj);
				if (oldObj != null) {
					LOGGER.debug("*** ParameterRatingTable Entry exists, Updating FA Scores in it..");
					oldObj.setFaScore(newObj.getFaScore());
					oldObj.setFaRemark(newObj.getFaRemark());
					int variance = oldObj.getSaScore() - newObj.getFaScore();
					oldObj.setVariance(variance);
					parameterRatingDAO.updateParameterRatingData(connection, databaseName, oldObj);
				} else {
					LOGGER.debug("*** ParameterRatingTable Entry NOT Exists, Throwing exception..");
					throw new ReportingDbServiceException(
							"Cannot update FA Scores of non-existing entry of ParameterRatingTable with formUUID '"
									+ newObj.getFormId() + "', SectionID '" + newObj.getSectionId()
									+ "', and parameterID '" + newObj.getParameterId() + "'.");
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Update FAScores ParameterRatingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Update FAScores ParameterRatingData: " + e.getMessage(),
					e);
		}

	}

	/**
	 * This method is used to update all the SA Scores in parameter rating data.
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
	public void updateParameterRatingDataCIC(Connection connection, String databaseName, String json, String formUUID)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside updateParameterRatingDataCIC(con) method of ParameterRatingServiceImpl class.");
//		LOGGER.debug("Incoming Service connection : "+connection);
		try {
			List<ParameterRatingTable> parameterRatingList = processJsonAndGetList(json, formUUID);
			LOGGER.debug("ParameterRatingTable entries size: " + parameterRatingList.size());
			for (ParameterRatingTable newObj : parameterRatingList) {
				ParameterRatingTable oldObj = getParameterRatingEntryExists(connection, databaseName, newObj);
				if (oldObj != null) {
					LOGGER.debug("*** ParameterRatingTable Entry exists, Updating SA Scores in it..");
					oldObj.setSaScore(newObj.getSaScore());
					oldObj.setSaRemark(newObj.getSaRemark());
					int variance = newObj.getSaScore() - oldObj.getFaScore();
					oldObj.setVariance(variance);
					parameterRatingDAO.updateParameterRatingData(connection, databaseName, oldObj);
				} else {
					LOGGER.debug("*** ParameterRatingTable Entry NOT Exists, Throwing exception..");
					throw new ReportingDbServiceException(
							"Cannot update SA Scores of non-existing entry of ParameterRatingTable with formUUID '"
									+ newObj.getFormId() + "', SectionID '" + newObj.getSectionId()
									+ "', and parameterID '" + newObj.getParameterId() + "'.");
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Update SAScores ParameterRatingData: " + e.getMessage(), e);
			throw new ReportingDbServiceException("Unable to Update SAScores ParameterRatingData: " + e.getMessage(),
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
	private List<ParameterRatingTable> processJsonAndGetList(String inputJSON, String formUUID)
			throws ReportingDbServiceException {
		LOGGER.debug(".inside processJsonAndGetList method of ParameterRatingServiceImpl class.");
		try {
			List<ParameterRatingTable> dataList = new ArrayList<ParameterRatingTable>();
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

					String saRemark = parameterJsonObj.containsKey(ReportingDbJSONConstant.SA_REMARK)
							? parameterJsonObj.get(ReportingDbJSONConstant.SA_REMARK).toString()
							: "";
					String faRemark = parameterJsonObj.containsKey(ReportingDbJSONConstant.FA_REMARK)
							? parameterJsonObj.get(ReportingDbJSONConstant.FA_REMARK).toString()
							: "";

					int variance = saScore - faScore; // Calculating variance.
					LOGGER.debug("Successfully fetched the required details of ParameterRatingTable from the JSON.");
					ParameterRatingTable obj = new ParameterRatingTable(); // Creating object
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
			LOGGER.error("Cannot create ParameterRatingTable list from the JSON: " + e.getMessage(), e);
			throw new ReportingDbServiceException(
					"Cannot create ParameterRatingTable list from the JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to check if the parameter rating entry is exists or not
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
	private boolean checkParameterRatingEntryExists(Connection connection, String databaseName,
			ParameterRatingTable object) throws ReportingDbServiceException {
		try {
			ParameterRatingTable prObject = parameterRatingDAO.getPRDataByFormSectionParameterIds(connection,
					databaseName, object.getFormId(), object.getSectionId(), object.getParameterId());
			if (prObject != null) {
				return true;
			}
		} catch (ReportingDbDAOException e) {
			LOGGER.error("Cannot check if " + StringEscapeUtils.escapeJava(object.toString()) + " exists, ", StringEscapeUtils.escapeJava(e.toString()));
			throw new ReportingDbServiceException("Cannot check if " + object + " exists, " + e.getMessage(), e);
		}
		return false;
	}

	/**
	 * This method is used to get the parameter rating entry if exists.
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
	private ParameterRatingTable getParameterRatingEntryExists(Connection connection, String databaseName,
			ParameterRatingTable object) throws ReportingDbServiceException {
		try {
			ParameterRatingTable prObject = parameterRatingDAO.getPRDataByFormSectionParameterIds(connection,
					databaseName, object.getFormId(), object.getSectionId(), object.getParameterId());
			if (prObject != null) {
				return prObject;
			}
		} catch (ReportingDbDAOException e) {
			LOGGER.error("Cannot check if " + StringEscapeUtils.escapeJava(object.toString()) + " exists, ", StringEscapeUtils.escapeJava(e.toString()));
			throw new ReportingDbServiceException("Cannot check if " + object + " exists, " + e.getMessage(), e);
		}
		return null;
	}

}
