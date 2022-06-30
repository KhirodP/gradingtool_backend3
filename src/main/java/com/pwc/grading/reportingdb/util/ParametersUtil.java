package com.pwc.grading.reportingdb.util;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.ReportingDbJSONConstant;
import com.pwc.grading.util.JsonUtill;

public class ParametersUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParametersUtil.class);
	
	/**
	 * Get SA Score from the {@link JSONObject}. If any exception occurs returns 0.
	 * @param jsonObj the json object.
	 * @return saScore 
	 */
	
	public static int getSAScore(JSONObject jsonObj) {
		LOGGER.debug(".inside getSAScore method of ParametersUtil class.");
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
		LOGGER.debug(".inside getFAScore method of ParametersUtil class.");
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
