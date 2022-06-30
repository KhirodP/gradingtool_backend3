package com.pwc.grading.reportingdb.constant;

/**
 * A class which contains the constant values for the reporting tables column
 * names.
 *
 */
public class ReportingDBTableConstant {

	// PartnersReportingTable column name constants.
	public static final String PARTNER_PROJECT_ID = "partnerProjectId";
	public static final String PROJECT_ID = "projectId";
	public static final String PROJECT_NAME = "projectName";
	public static final String PARTNER_ID = "partnerId";
	public static final String PIA_NAME = "piaName";
	public static final String DISTRICT = "district";
	public static final String HEAD_PERSONNEL = "headPersonnel";
	public static final String CONTACT = "contact";
	public static final String HEAD_PERSON_EMAIL = "headPersonEmail";
	public static final String CENTER_RATING = "centerRating";
	public static final String PROJECT_GRADING = "projectGrading";
	public static final String FINAL_PERCENTAGE = "finalPercentage";
	public static final String GRADE = "grade";
	public static final String STATUS = "status";

	// FormGradingTable column name constants.
	public static final String ID = "id";
	public static final String FORM_ID = "formId";
//	public static final String PARTNER_PROJECT_ID= "partnerProjectId";
	public static final String FORM_NAME = "formName";
	public static final String MAX_MARKS = "maxMarks";
	public static final String SA_SCORE = "saScore";
	public static final String FA_SCORE = "faScore";
	public static final String VARIANCE = "variance";
//	public static final String STATUS= "status";
	public static final String PIA_DATE_ASSIGNED = "piaDateAssigned";
	public static final String PIA_DATE_COMPLETION = "piaDateCompletion";
	public static final String PIA_START_TIME = "piaStartTime";
	public static final String PIA_END_TIME = "piaEndTime";
	public static final String FA_NAME = "faName";
	public static final String FA_PHONE = "faPhone";
	public static final String SEC_AUDITOR_NAME = "secondaryAuditorName";
	public static final String FA_LOCATION = "faLocation";
	public static final String FA_DATE_ASSIGNED = "faDateAssigned";
	public static final String FA_DATE_COMPLETED = "faDateCompleted";
	public static final String FA_START_TIME = "faStartTime";
	public static final String FA_END_TIME = "faEndTime";
	public static final String SIGN_OFF_TIME = "signoffTime";
	public static final String OTP = "otp";

	// FormRatingTable column name constants.
	public static final String TC_NAME = "tcName";
	public static final String TC_ID = "tcId";
	public static final String CENTER_ADDRESS = "centerAddress";
	public static final String CIC_NAME = "centerInchargeName";
	public static final String CIC_PHONE = "centerInchargePhone";
	public static final String CENTER_LOCATION = "centerLocation";
	public static final String SA_DATE_ASSIGNED = "saDateAssigned";
	public static final String SA_DATE_COMPLETION = "saDateCompletion";
	public static final String SA_START_TIME = "saStartTime";
	public static final String SA_END_TIME = "saEndTime";

	// ParameterGrading column name constants.
	public static final String FORM_UUID = "formUUID";
	public static final String SECTION_ID = "sectionId";
	public static final String PARAMETER_ID = "parameterId";

	// JOB table column constants
	public static final String JOB_ID = "id";
	public static final String JOB_JSON_OBJ = "jsonObj";
	public static final String JOB_START_TIME = "startTime";
	public static final String JOB_END_TIME = "endTime";
	public static final String JOB_STATUS = "status";
	public static final String JOB_FAILURE_MSG = "failureMessage";
	public static final String JOB_RETRY_COUNT = "retryCount";
	public static final String JOB_OPERATION_TYPE = "operationType";
	public static final String FA_REMARK = "faRemark";
	public static final String SA_REMARK = "saRemark";

}
