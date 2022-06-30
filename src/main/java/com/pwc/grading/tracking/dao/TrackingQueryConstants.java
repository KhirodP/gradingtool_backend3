package com.pwc.grading.tracking.dao;

/**
 * A class contains all the database queries related to the tracking operations.
 *
 */
public class TrackingQueryConstants {
	public static final String DATA_BASE_PLACE_HOLDER = "#$DataBaseName#$";
	public static final String INSERT_LOG = "INSERT INTO #$DataBaseName#$.dbo.UserLogTracking VALUES(?,?,?,?)";
	public static final String UPDATE_LOG = "UPDATE #$DataBaseName#$.dbo.UserLogTracking SET LOGOUTTIME=? WHERE SESSIONID=?";
	public static final String DELETE_LOG_TRACKING_BY_USER_ID = "DELETE #$DataBaseName#$.dbo.UserLogTracking WHERE USERID=?";
	// SurveyResponse
	public static final String INSERT_SURVEY_RESPONSE_TRACKING = "INSERT INTO #$DataBaseName#$.dbo.UserSurveyResponseTracking VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

	public static final String UPDATE_SURVEY_RESPONSE_TRACKING_FOR_TRAINING_CENTER = "UPDATE #$DataBaseName#$.dbo.UserSurveyResponseTracking SET ENDDATE=?,ENDTIME=? WHERE SURVEYID=? AND CENTERID=?";
	public static final String UPDATE_SURVEY_RESPONSE_TRACKING_FOR_PARTNER = "UPDATE #$DataBaseName#$.dbo.UserSurveyResponseTracking SET ENDDATE=?,ENDTIME=? WHERE SURVEYID=? AND PARTNERID=?";
	public static final String UPDATE_SURVEY_RESPONSE_TRACKING_FOR_FIELD_AUDITOR = "UPDATE #$DataBaseName#$.dbo.UserSurveyResponseTracking SET ENDDATE=?,ENDTIME=? WHERE SURVEYID=? AND AUDITFOR=? AND AUDITFORID=?";

	public static final String SELECT_USER_SURVEYRESPONSE_TRACKING_BY_AUDIT_ID_AND_SURVEY_ID = "SELECT projectId,surveyId,centerId,partnerId,auditFor,auditForId,variance,startDate,startTime,endDate,endTime,geoLocation.Lat,geoLocation.Long FROM #$DataBaseName#$.dbo.UserSurveyResponseTracking WHERE SURVEYID=? AND AUDITFOR=? AND AUDITFORID=?";
	public static final String SELECT_USER_SURVEYRESPONSE_TRACKING_BY_CENTER_ID_AND_SURVEY_ID = "SELECT * FROM #$DataBaseName#$.dbo.UserSurveyResponseTracking WHERE SURVEYID=? AND CENTERID=? ";
	public static final String SELECT_USER_SURVEYRESPONSE_TRACKING_BY_PARTNER_ID_AND_SURVEY_ID = "SELECT * FROM #$DataBaseName#$.dbo.UserSurveyResponseTracking WHERE SURVEYID=? AND PARTNERID=? ";

	public static final String DELETE_SURVEY_RESPONSE_BY_AUDIT_FOR_ID_ID_AND_SURVERY_ID = "DELETE #$DataBaseName#$.dbo.UserSurveyResponseTracking WHERE SURVEYID=? AND AUDITFOR=? AND AUDITFORID=?";
	public static final String DELETE_USER_SURVEYRESPONSE_TRACKING_BY_CENTER_ID_AND_SURVEY_ID = "DELETE #$DataBaseName#$.dbo.UserSurveyResponseTracking WHERE SURVEYID=? AND CENTERID=? ";
	public static final String DELETE_USER_SURVEYRESPONSE_TRACKING_BY_PARTNER_ID_AND_SURVEY_ID = "DELETE #$DataBaseName#$.dbo.UserSurveyResponseTracking WHERE SURVEYID=? AND PARTNERID=? ";
	public static final String DELETE_USER_SURVEYRESPONSE_TRACKING_BY_SURVEY_ID = "DELETE #$DataBaseName#$.dbo.UserSurveyResponseTracking WHERE SURVEYID=? ";

}
