package com.pwc.grading.reportingdb.util;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.ReportingDbJSONConstant;
import com.pwc.grading.util.JsonUtill;
import com.pwc.grading.util.exception.JsonUtillException;

public class FormsUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(FormsUtil.class);
	
	private static final String GRADING_FORM = "Grading";
	private static final String RATING_FORM = "Rating";
	
	/**
	 * This method is used to check if it is grading form {@link JSONObject}
	 * @param jsonObj the object to be checked.
	 * @return true if grading form.
	 * @throws JsonUtillException if unable to fetch JSON data.
	 */
	public static boolean isGradingForm(JSONObject jsonObj) throws JsonUtillException {
		LOGGER.debug(".inside isGradingForm method of FormsUtil class.");
		String formType = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.FORM_TYPE);
		if(formType.equalsIgnoreCase(GRADING_FORM)) {
			LOGGER.debug("Form is : Grading form");
			return true;
		}
		return false;
	}
	
	/**
	 * This method is used to check if it is rating form {@link JSONObject}
	 * @param jsonObj the object to be checked.
	 * @return true if rating form.
	 * @throws JsonUtillException if unable to fetch JSON data.
	 */
	public static boolean isRatingForm(JSONObject jsonObj) throws JsonUtillException {
		LOGGER.debug(".inside isRatingForm method of FormsUtil class.");
		String formType = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.FORM_TYPE);
		if(formType.equalsIgnoreCase(RATING_FORM)) {
			LOGGER.debug("Form is : Rating form");
			return true;
		}
		return false;
	}
	
	/**
	 * Used to get the form type.
	 * @param jsonObj the object to be checked.
	 * @return the form type. (Rating or Grading)
	 * @throws JsonUtillException if unable to fetch JSON data.
	 */
	public static String getFormType(JSONObject jsonObj) throws JsonUtillException {
		LOGGER.debug(".inside getFormType method of FormsUtil class.");
		String formType = JsonUtill.getString(jsonObj, ReportingDbJSONConstant.FORM_TYPE);
		return formType;
	}
	
	/**
	 * This method is used to check if the JSON passed is a Grading form JSON.
	 * @param json the json to be checked.
	 * @return true if the JSON passed is a Grading form JSON.
	 * @throws JsonUtillException if unable to fetch JSON data.
	 */
	public static boolean isGradingFormJSON(String json) throws JsonUtillException {
		LOGGER.debug(".inside isGradingFormJSON method of FormsUtil class.");		
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
			JSONObject projectJsonObject = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.PROJECT);
			JSONObject formJsonObject = JsonUtill.getJsonObject(projectJsonObject, ReportingDbJSONConstant.FORM);
			String formType = JsonUtill.getString(formJsonObject, ReportingDbJSONConstant.FORM_TYPE);
			if(formType.equalsIgnoreCase(GRADING_FORM)) {
				LOGGER.debug("Form is : Grading form");
				return true;
			}
			return false;
			
		} catch (ParseException e) {
			LOGGER.debug("Cannot parse the JSON to find formType");
			throw new JsonUtillException("Cannot parse the JSON to find formType: "+e.getMessage(),e);
		}
	}
	
	/**
	 * This method is used to check if the JSON passed is a Rating form JSON.
	 * @param json the json to be checked.
	 * @return true if the JSON passed is a Rating form JSON.
	 * @throws JsonUtillException if unable to fetch JSON data.
	 */
	public static boolean isRatingFormJSON(String json) throws JsonUtillException {
		LOGGER.debug(".inside isRatingFormJSON method of FormsUtil class.");		
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
			JSONObject projectJsonObject = JsonUtill.getJsonObject(jsonObj, ReportingDbJSONConstant.PROJECT);
			JSONObject formJsonObject = JsonUtill.getJsonObject(projectJsonObject, ReportingDbJSONConstant.FORM);
			String formType = JsonUtill.getString(formJsonObject, ReportingDbJSONConstant.FORM_TYPE);
			if(formType.equalsIgnoreCase(RATING_FORM)) {
				LOGGER.debug("Form is : Rating form");
				return true;
			}
			return false;
			
		} catch (ParseException e) {
			LOGGER.debug("Cannot parse the JSON to find formType");
			throw new JsonUtillException("Cannot parse the JSON to find formType: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Get SA Score from the {@link JSONObject}. If any exception occurs returns 0.
	 * @param jsonObj the json object.
	 * @return saScore 
	 */
	public static int getSAScore(JSONObject jsonObj) {
		LOGGER.debug(".inside getSAScore method of FormsUtil class.");
		int saScore = 0;
		try {
			saScore = JsonUtill.getInt(jsonObj, ReportingDbJSONConstant.SA_SCORE);
			return saScore;
		} catch (Exception e) {
//			LOGGER.error("Exp calculating SA Score :",e);
			saScore = 0;
		}
		return 0;
	}
	
	/**
	 * Get FA Score from the {@link JSONObject}. If any exception occurs returns 0.
	 * @param jsonObj the json object.
	 * @return faScore 
	 */
	public static int getFAScore(JSONObject jsonObj) {
		LOGGER.debug(".inside getFAScore method of FormsUtil class.");
		int saScore = 0;
		try {
			saScore = JsonUtill.getInt(jsonObj, ReportingDbJSONConstant.FA_SCORE);
			return saScore;
		} catch (Exception e) {
//			LOGGER.error("Exp calculating FA Score :",e);
			saScore = 0;
		}
		return 0;
	}
}
