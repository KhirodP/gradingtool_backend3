package com.pwc.grading.db;

/**
 * MSSqlServerDBConstants class contains the Constans to create Database Tables
 * 
 *
 */
public class MSSqlServerDBConstants {

	public static final String USER_ADMIN_FIRST_NAME = ".user.admin.firstName";
	public static final String USER_ADMIN_LAST_NAME = ".user.admin.lastName";
	public static final String USER_ADMIN_PHONE = ".user.admin.phone";
	public static final String USER_ADMIN_EMAIL = ".user.admin.email";
	public static final String USER_ADMIN_PWD = ".user.admin.password";

//	protected static final String PWD_PLACE_HOLDER = "#$PASSWORD#$";
//	protected static final String INSERT_ADMIN = "INSERT INTO #$DataBaseName#$.DBO.UserDetails VALUES(NEWID(),'admin','','#$PASSWORD#$','admin@gradingtool.com','---','Admin','','active','','','','','')";
	public static final String DB_PROPERTIES_FILE_NAME = "database.properties";
	protected static final String DATA_BASE_PLACE_HOLDER = "#$DataBaseName#$";
	protected static final String USER_TOKEN_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.UserToken(tokenId uniqueidentifier ,deviceId varchar(30),expTime bigint ,userId uniqueidentifier,platform varchar(50),accesToken varchar(100), CONSTRAINT FK_UserId FOREIGN KEY (userId) REFERENCES	#$DataBaseName#$.dbo.UserDetails(userId),CONSTRAINT PK_TokenId PRIMARY KEY CLUSTERED (tokenId))";
	protected static final String USER_DETAILS_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.UserDetails(userId uniqueidentifier, firstName varchar(50) NOT NULL, lastName varchar(50), password varchar(100),email varchar(50) NOT NULL UNIQUE, phone varchar(20) NOT NULL UNIQUE, role varchar(20) NOT NULL, centerId varchar(50) , status varchar(20) NOT NULL, otp varchar(10), expiryDate bigint,forgetPasswordEmailOtp varchar(10),forgetPasswordSmsOtp varchar(10),forgetPasswordExpiryDate bigint,CONSTRAINT PK_UserId PRIMARY KEY CLUSTERED (userId))";
	// protected static final String USER_DETAILS_TABLE = "CREATE TABLE
	// #$DataBaseName#$.dbo.UserDetails(userId uniqueidentifier, firstName
	// varchar(50) NOT NULL, lastName varchar(50), password varchar(50),email
	// varchar(50) NOT NULL UNIQUE, phone varchar(20) NOT NULL UNIQUE, role
	// varchar(20) NOT NULL, centerId varchar(50) , status varchar(20) NOT NULL, otp
	// varchar(10), expiryDate bigint,forgetPasswordOtp
	// varchar(10),forgetPasswordExpiryDate bigint,CONSTRAINT PK_UserId PRIMARY KEY
	// CLUSTERED (userId))";
	protected static final String USER_VERIFICATION_TABLE = "create table #$DataBaseName#$.dbo.UserVerification(userId uniqueidentifier,tempPassword varchar(100),oneTimeAccessToken varchar(100),expiryTime bigint,emailOtp varchar(20),smsOtp varchar(20),CONSTRAINT FK_UV_UserId FOREIGN KEY (userId) REFERENCES #$DataBaseName#$.dbo.UserDetails(userId))";

	// Partner Tables
	protected static final String PARTNER_DETAILS_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.PartnerDetails(partnerId uniqueidentifier, partnerName varchar(50) UNIQUE NOT NULL,clientSponsorId uniqueidentifier UNIQUE NOT NULL, CONSTRAINT FK_ClientSponsorId FOREIGN KEY (clientSponsorId) REFERENCES #$DataBaseName#$.dbo.UserDetails(userId),CONSTRAINT PK_PartnerId PRIMARY KEY CLUSTERED (partnerId))";
	protected static final String TRAINING_CENTER_DETAILS_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.TrainingCenterDetails(partnerId uniqueidentifier,TcId varchar(50),tcName varchar(50),district varchar(50),centerAddress varchar(100),centerInchargeId uniqueidentifier UNIQUE NOT NULL,latitude varchar(20),longitude varchar(20),tcGeoLocation Geography,CONSTRAINT TC_FK_PartnerId FOREIGN KEY (partnerId) REFERENCES #$DataBaseName#$.dbo.PartnerDetails(partnerId),CONSTRAINT FK_CenterInChargeId FOREIGN KEY (centerInchargeId) REFERENCES #$DataBaseName#$.dbo.UserDetails(userId),CONSTRAINT PK_TcId PRIMARY KEY CLUSTERED (TcId))";
	protected static final String PROJECT_MAPPING_DATA_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.ProjectMappingData(projectMappingId uniqueidentifier,partnerProjectId varchar(50),TcId varchar(50),projectId uniqueidentifier,partnerId uniqueidentifier,CONSTRAINT PK_ProjectMappingId PRIMARY KEY CLUSTERED (projectMappingId),CONSTRAINT FK_TcId FOREIGN KEY (TcId) REFERENCES #$DataBaseName#$.dbo.TrainingCenterDetails(TcId),CONSTRAINT FK_ProjectDataId FOREIGN KEY (projectId) REFERENCES #$DataBaseName#$.dbo.ProjectData(projectId),CONSTRAINT FK_PartnerId FOREIGN KEY (partnerId) REFERENCES #$DataBaseName#$.dbo.PartnerDetails(partnerId))";
	protected static final String GRADING_ENABLE_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.GradingEnable(projectId uniqueidentifier,partnerId uniqueidentifier,isGradingEnable bit,CONSTRAINT FK_GE_ProjectDataId FOREIGN KEY (projectId) REFERENCES #$DataBaseName#$.dbo.ProjectData(projectId),CONSTRAINT FK_GE_PartnerId FOREIGN KEY (partnerId) REFERENCES #$DataBaseName#$.dbo.PartnerDetails(partnerId))";

	// Scheduler Tables
	protected static final String GRADING_TYPE_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.GradingType(partnerId uniqueidentifier,	projectId uniqueidentifier,formId uniqueidentifier, selfAssismentStatus bit,  fieldAuditorId uniqueidentifier,	auditDate bigint,auditStatus bit,	isAuditCancel bit,secondaryFieldAuditorName varchar(50),CONSTRAINT FK_GT_ProjectId FOREIGN KEY (projectId) REFERENCES #$DataBaseName#$.dbo.ProjectData(projectId),CONSTRAINT FK_GT_formId FOREIGN KEY (formId) REFERENCES #$DataBaseName#$.dbo.FormData(formId),CONSTRAINT FK_GT_PartnerId FOREIGN KEY (partnerId) REFERENCES #$DataBaseName#$.dbo.PartnerDetails(partnerId),CONSTRAINT UNI_COLUMNS_GT UNIQUE(partnerId, projectId,formId))";
	protected static final String RATING_TYPE_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.RatingType(partnerId uniqueidentifier,tcId varchar(100),	projectId uniqueidentifier,formId uniqueidentifier,	selfAssismentStatus bit, fieldAuditorId uniqueidentifier,	auditDate bigint,auditStatus bit,	isAuditCancel bit,secondaryFieldAuditorName varchar(50),CONSTRAINT FK_RT_ProjectId FOREIGN KEY (projectId) REFERENCES #$DataBaseName#$.dbo.ProjectData(projectId),CONSTRAINT FK_RT_formId FOREIGN KEY (formId) REFERENCES #$DataBaseName#$.dbo.FormData(formId),CONSTRAINT FK_RT_PartnerId FOREIGN KEY (partnerId) REFERENCES #$DataBaseName#$.dbo.PartnerDetails(partnerId),CONSTRAINT UNI_COLUMNS_RT UNIQUE(partnerId,tcId,projectId,formId))";
	// Manage Rating Forms
	protected static final String PROJECT_DATA_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.ProjectData(projectId uniqueidentifier, projectName varchar(50), projectDescription text,startDate  bigint,endDate bigint,selfAssignmentDeadLine bigint,CONSTRAINT PK_ProjectId PRIMARY KEY CLUSTERED (projectId))";
	protected static final String FORM_DATA_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.FormData( projectId  uniqueidentifier, formId uniqueidentifier, formName varchar(50), timeOfCreate bigint, surveyId uniqueidentifier not null unique, userRolesAllowed varchar(100),publish bit,CONSTRAINT PK_FormId PRIMARY KEY CLUSTERED (formId),CONSTRAINT FK_ProjectId FOREIGN KEY (projectId) REFERENCES #$DataBaseName#$.dbo.ProjectData(projectId))";
	protected static final String SURVEY_DATA_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.SurveyData(surveyId uniqueidentifier unique not null, timeOfcreate bigint, lastUpdate bigint , maxScore int, surveyData text,publish bit,CONSTRAINT FK_SurveyId FOREIGN KEY (surveyId) REFERENCES #$DataBaseName#$.dbo.FormData(surveyId))";

	// ,CONSTRAINT FK_SRD_TcId FOREIGN KEY (centerId) REFERENCES
	// #$DataBaseName#$.dbo.TrainingCenterDetails(TcId) ,CONSTRAINT FK_SRD_PartnerId
	// FOREIGN KEY (partnerId) REFERENCES
	// #$DataBaseName#$.dbo.PartnerDetails(partnerId)
	protected static final String SURVEY_RESPONSE_DATA_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.SurveyResponse(surveyResponseId uniqueidentifier, surveyId uniqueidentifier,userId uniqueidentifier, saveTime bigint,submitTime bigint,responderType varchar(30),totalScore int,isSubmited bit,centerId varchar(50),partnerId uniqueidentifier ,auditFor varchar(50),auditForId varchar(100),maxMarks int,surveyResponseData text,CONSTRAINT FK_SurveyDataId FOREIGN KEY (surveyId) REFERENCES #$DataBaseName#$.dbo.FormData(surveyId),CONSTRAINT FK_RespondedUserId FOREIGN KEY (userId) REFERENCES #$DataBaseName#$.dbo.UserDetails(userId),CONSTRAINT FK_SRD_TcId FOREIGN KEY (centerId) REFERENCES #$DataBaseName#$.dbo.TrainingCenterDetails(TcId) ,CONSTRAINT FK_SRD_PartnerId FOREIGN KEY (partnerId) REFERENCES #$DataBaseName#$.dbo.PartnerDetails(partnerId))";
	protected static final String MEDIA_DATA_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.MediaData(mediaId uniqueidentifier, mediaName varchar(300), uploadDate bigint, type varchar(100),inputStream VARBINARY(MAX),thumbnailInputStream VARBINARY(MAX),CONSTRAINT PK_MediaId PRIMARY KEY CLUSTERED (mediaId))";

	// no need to create forgin key in tracking table i think
	// ,CONSTRAINT FK_LogTracking_UserId FOREIGN KEY (userId) REFERENCES
	// #$DataBaseName#$.dbo.UserDetails(userId)
	protected static final String USER_LOG_TRACKING_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.UserLogTracking(sessionId uniqueidentifier,userId uniqueidentifier,logInTime BIGINT,logOutTime BIGINT)";
	// ,CONSTRAINT FK_UserSurveyResponeTracking_UserId FOREIGN KEY (userId)
	// REFERENCES #$DataBaseName#$.dbo.UserDetails(userId)
	protected static final String USER_SURVEY_RESPONSE_TRACKING_TABLE = "CREATE TABLE #$DataBaseName#$.dbo.UserSurveyResponseTracking(projectId uniqueidentifier,surveyId uniqueidentifier,centerId varchar(50),partnerId uniqueidentifier ,auditFor varchar(50),auditForId varchar(100),variance varchar(50),startDate DATE,startTime TIME,endDate DATE,endTime TIME,geoLocation Geography)";

	// INDEXES:
	// ========
	// SurveyResponse
	protected static final String INDEX_SURVEY_RESPONSE_TRINING_CENTER = "CREATE UNIQUE INDEX INX_SR_TC ON #$DataBaseName#$.dbo.SurveyResponse(SURVEYID,centerId) WHERE CENTERID IS NOT NULL";
	protected static final String INDEX_SURVEY_RESPONSE_PARTNER = "CREATE UNIQUE INDEX INX_SR_PARTNER ON #$DataBaseName#$.dbo.SurveyResponse(SURVEYID,PARTNERID) WHERE PARTNERID IS NOT NULL";
	protected static final String INDEX_SURVEY_RESPONSE_FIELD_AUDITOR = "CREATE UNIQUE INDEX INX_SR_FA ON #$DataBaseName#$.dbo.SurveyResponse(SURVEYID,AUDITFOR,AUDITFORID) WHERE AUDITFOR IS NOT NULL AND AUDITFORID IS NOT NULL";

	// PROJECT_MAPPING
	protected static final String INDEX_PROJECT_MAPPING_TCID_AND_PROJECTID = "CREATE UNIQUE INDEX INX_TCID_PROJECTID ON #$DataBaseName#$.dbo.ProjectMappingData(TcId,projectId)";

	// USER_DETAILS
	protected static final String INDEX_USER_EMAIL = "CREATE UNIQUE INDEX INX_USER_EMAIL ON #$DataBaseName#$.dbo.UserDetails(email)";
	protected static final String INDEX_USER_PHONE = "CREATE UNIQUE INDEX INX_USER_PHONE ON #$DataBaseName#$.dbo.UserDetails(phone)";

}
// add this in surveyResponse after creating tCdetais table,CONSTRAINT FK_centerId FOREIGN KEY (centerId) REFERENCES #$DataBaseName#$.dbo.TrainingCenterDetails(TcId
