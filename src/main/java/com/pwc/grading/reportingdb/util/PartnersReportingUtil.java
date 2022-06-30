package com.pwc.grading.reportingdb.util;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.ReportingDbJSONConstant;
import com.pwc.grading.util.JsonUtill;

public class PartnersReportingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(PartnersReportingUtil.class);
	
	/**
	 * Gets the center Rating value.
	 * returns 0.0 in case of any exception
	 * @param jsonObj
	 * @return center rating
	 */
	public static double getCenterRating(JSONObject jsonObj) {
		LOGGER.debug(".inside getCenterRating of PartnersReportingUtil class.");
		double centerRating = 0.0;
		try {
			centerRating = JsonUtill.getDouble(jsonObj, ReportingDbJSONConstant.CENTER_RATING);
		}catch (Exception e) {
		//	LOGGER.debug("Caught exp and assigning centerRating to zero."+e);
			centerRating = 0.0;
		}
		return centerRating;
	}
	
	/**
	 * Get the projectGrading value.
	 * returns 0.0 in case of any exception
	 * @param jsonObj
	 * @return projectGrading
	 */
	public static double getProjectGrading(JSONObject jsonObj) {
		LOGGER.debug(".inside getProjectGrading of PartnersReportingUtil class.");
		double projectGrading = 0.0;
		try {
			projectGrading = JsonUtill.getDouble(jsonObj, ReportingDbJSONConstant.PROJECT_GRADING);
		}catch (Exception e) {
	//		LOGGER.debug("Caught exp and assigning projectGrading to zero."+e);
			projectGrading = 0.0;
		}
		return projectGrading;
	}
	
	/**
	 * Get the Final percentage value.
	 * returns 0.0 in case of any exception
	 * @param jsonObj
	 * @return Final percentage
	 */
	public static double getFinalPercentage(JSONObject jsonObj) {
		LOGGER.debug(".inside getFinalPercentage of PartnersReportingUtil class.");
		double finalAvg = 0.0;
		try {
			finalAvg = JsonUtill.getDouble(jsonObj, ReportingDbJSONConstant.FINAL_AVG);
		}catch (Exception e) {
		//	LOGGER.debug("Caught exp and assigning finalAvg to zero.");
			finalAvg = 0.0;
		}
		return finalAvg;
	}
}
