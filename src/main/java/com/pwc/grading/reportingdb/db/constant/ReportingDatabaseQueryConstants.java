package com.pwc.grading.reportingdb.db.constant;

/**
 * A class which holds all the queries related to the reporting tables.
 *
 */
public class ReportingDatabaseQueryConstants {

	public static final String DATA_BASE_PLACE_HOLDER = "#$DataBaseName#$";
	public static final int SRID_FOR_GEOGRAPHY = 4326;

	// PartnersReportingTable Queries
	public static final String INSERT_PARTNER_REPORTING = "INSERT INTO #$DataBaseName#$.dbo.PartnersReportingTable (partnerProjectId, projectId, projectName, partnerId, piaName,  headPersonnel, contact, headPersonEmail, centerRating, projectGrading, finalPercentage, grade, status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String SELECT_ALL_PARTNER_REPORTING = "SELECT * FROM #$DataBaseName#$.dbo.PartnersReportingTable";
	public static final String SELECT_PR_BY_PROJECT_AND_PARTNER_ID = SELECT_ALL_PARTNER_REPORTING
			+ " WHERE projectId=? AND partnerId=?";
	public static final String SELECT_PR_BY_PROJECT_ID = SELECT_ALL_PARTNER_REPORTING + " WHERE projectId=?";
	public static final String SELECT_PR_BY_PARTNER_ID = SELECT_ALL_PARTNER_REPORTING + " WHERE partnerId=?";
	public static final String UPDATE_PARTNER_REPORTING = "UPDATE #$DataBaseName#$.dbo.PartnersReportingTable SET projectName=?, piaName=?, headPersonnel=?, contact=?, headPersonEmail=?, centerRating=?, projectGrading=?, finalPercentage=?, grade=?, status=? WHERE partnerProjectId=?";
	public static final String DELETE_BY_PROJECTID = "DELETE FROM #$DataBaseName#$.dbo.PartnersReportingTable WHERE projectId=?";
	public static final String SELECT_PPID_FOR_PROJECTID = "SELECT partnerProjectId FROM #$DataBaseName#$.dbo.PartnersReportingTable WHERE projectId=?";
	public static final String UPDATE_CS_BY_PROJECT_AND_PARTNER_ID = "UPDATE #$DataBaseName#$.dbo.PartnersReportingTable SET headPersonnel=?, contact=?, headPersonEmail=? where projectId=? and partnerId=?";

	// FormGradingTable Queries
	public static final String INSERT_FORM_GRADING = "INSERT INTO #$DataBaseName#$.dbo.FormGradingTable VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String UPDATE_FORM_GRADING = "UPDATE #$DataBaseName#$.dbo.FormGradingTable SET formName=?, maxMarks=?, saScore=?, faScore=?, variance=?, status=?, piaDateAssigned=?, piaDateCompletion=?, piaStartTime=?, piaEndTime=?, faName=?, faPhone=?, secondaryAuditorName=?, faLocation=?, faDateAssigned=?, faDateCompleted=?, faStartTime=?, faEndTime=?, signoffTime=?, otp=? where id=?";

	public static final String UPDATE_FORM_GRADING_BY_PARTNER_PROJECT_ID = "UPDATE #$DataBaseName#$.dbo.FormGradingTable SET  saScore=0, faScore=0, variance=0, status=0, piaDateAssigned='', piaDateCompletion='', piaStartTime='', piaEndTime='', faName='', faPhone='', secondaryAuditorName='', faLocation=?, faDateAssigned='', faDateCompleted='', faStartTime='', faEndTime='', signoffTime='', otp='' where partnerProjectId=?";

	public static final String SELECT_ALL_FORM_GRADING = "SELECT id, formId, partnerProjectId, formName, maxMarks, saScore, faScore, variance, status, piaDateAssigned, piaDateCompletion, piaStartTime, piaEndTime, faName, faPhone, secondaryAuditorName, faLocation, faDateAssigned, faDateCompleted, faStartTime, faEndTime, signoffTime, otp FROM #$DataBaseName#$.dbo.FormGradingTable";
	public static final String SELECT_FG_BY_FORMID_PARTNERPROJECTID = SELECT_ALL_FORM_GRADING
			+ " where formId=? AND partnerProjectId= ?";
	public static final String DELETE_FG_BY_PARTNERPROJECTID = "DELETE FROM #$DataBaseName#$.dbo.FormGradingTable where partnerProjectId in ";
	public static final String SELECT_FG_FORMUUID_BY_PARTNERPROJECTID = "SELECT id from #$DataBaseName#$.dbo.FormGradingTable where partnerProjectId in ";
	public static final String UPDATE_FG_FA_BY_FORMID_PARTNERPROJECTID = "UPDATE #$DataBaseName#$.dbo.FormGradingTable SET faName=?, faPhone=? where partnerProjectId=? and formId=?";
	public static final String DELETE_FG_BY_FORMUUID = "DELETE FROM #$DataBaseName#$.dbo.FormGradingTable where id in ";

	// FormRatingTable Queries
	public static final String INSERT_FORM_RATING = "INSERT INTO #$DataBaseName#$.dbo.FormRatingTable VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String UPDATE_FORM_RATING = "UPDATE #$DataBaseName#$.dbo.FormRatingTable SET formName=?, tcName=?, tcId=?, centerAddress=?,district=?, centerInchargeName=?, centerInchargePhone=?, centerLocation=?, maxMarks=?, saScore=?, faScore=?, variance=?, status=?, saDateAssigned=?, saDateCompletion=?, saStartTime=?, saEndTime=?, faName=?, faPhone=?, secondaryAuditorName=?, faLocation=?, faDateAssigned=?, faDateCompleted=?, faStartTime=?, faEndTime=?, signoffTime=?, otp=? where id=?";
	public static final String SELECT_ALL_FORM_RATING = "SELECT * FROM #$DataBaseName#$.dbo.FormRatingTable";
	public static final String SELECT_FR_BY_FORMID_PARTNERPROJECTID = SELECT_ALL_FORM_RATING
			+ " where formId=? AND partnerProjectId= ?";
	public static final String SELECT_FR_BY_FORMID_TCID_PARTNERPROJECTID = SELECT_ALL_FORM_RATING
			+ " where formId=? AND tcId=? AND partnerProjectId= ?";
	public static final String DELETE_FR_BY_PARTNERPROJECTID = "DELETE FROM #$DataBaseName#$.dbo.FormRatingTable where partnerProjectId in ";

	public static final String DELETE_FR_BY_FORM_UUId = "DELETE FROM #$DataBaseName#$.dbo.FormRatingTable where ID in ";

	public static final String SELECT_FR_FORMUUID_BY_PARTNERPROJECTID = "SELECT id from #$DataBaseName#$.dbo.FormRatingTable where partnerProjectId in ";
//	public static final String SELECT_FR_FORMUUID_BY_PARTNERPROJECTID_TCID = "SELECT id from #$DataBaseName#$.dbo.FormRatingTable where partnerProjectId in ";
	public static final String SELECT_FR_FORMUUID_BY_PARTNERPROJECTID_AND_TCID = "SELECT id from #$DataBaseName#$.dbo.FormRatingTable where partnerProjectId=? AND TCID=? ";

	public static final String UPDATE_TC_BY_PARTNERPROJECTID_TCID = "UPDATE #$DataBaseName#$.dbo.FormRatingTable SET tcName=?, centerAddress=?, centerLocation=?, district=? WHERE partnerProjectId=? and tcId=?";
	public static final String UPDATE_CIC_BY_PARTNERPROJECTID_TCID = "UPDATE #$DataBaseName#$.dbo.FormRatingTable SET centerInchargeName=?, centerInchargePhone=? ,saScore=0 ,faScore=0,variance=0,status=0,saDateAssigned='',saDateCompletion='',saStartTime='',saEndTime='',faName='',faPhone='',secondaryAuditorName='',faLocation=?,faDateAssigned='',faDateCompleted='',faStartTime='',faEndTime='',signoffTime='',otp='' WHERE partnerProjectId=? and tcId=?";
	public static final String UPDATE_FR_FA_BY_FORMUUID = "UPDATE #$DataBaseName#$.dbo.FormRatingTable SET faName=?, faPhone=? where id=?";

	// ParameterGradingTable Queries
	public static final String INSERT_PARAMETER_GRADING = "INSERT INTO #$DataBaseName#$.dbo.ParameterGradingTable VALUES(?, ?, ?, ?, ?, ?, ?,?,?)";
	public static final String SELECT_ALL_PARAMETER_GRADING = "SELECT * FROM #$DataBaseName#$.dbo.ParameterGradingTable";
	public static final String SELECT_PG_BY_FORMUUID = SELECT_ALL_PARAMETER_GRADING + " where formUUID=?";
	public static final String SELECT_PG_BY_FORM_SECTION_PARAMETER_IDS = SELECT_ALL_PARAMETER_GRADING
			+ " where formUUID=? AND sectionId=? AND parameterId=?";
	public static final String SELECT_PG_BY_FORM_SECTION_IDS = SELECT_ALL_PARAMETER_GRADING
			+ " WHERE formUUID=? and sectionId=?";
	public static final String UPDATE_PARAMETER_GRADING = "UPDATE #$DataBaseName#$.dbo.ParameterGradingTable SET maxMarks=?, saScore=?, faScore=?, variance=?,saRemark=?,faRemark=? where formUUID=? AND sectionId=? AND parameterId=?";

	public static final String UPDATE_PARAMETER_GRADING_BY_FORM_UUID = "UPDATE #$DataBaseName#$.dbo.ParameterGradingTable SET saScore=0, faScore=0, variance=0 where formUUID IN ";

	public static final String DELETE_PG_BY_FORMUUID = "DELETE FROM #$DataBaseName#$.dbo.ParameterGradingTable where formUUID in ";

	// ParameterRatingTable Queries
	public static final String INSERT_PARAMETER_RATING = "INSERT INTO #$DataBaseName#$.dbo.ParameterRatingTable VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String SELECT_ALL_PARAMETER_RATING = "SELECT * FROM #$DataBaseName#$.dbo.ParameterRatingTable";
	public static final String SELECT_PR_BY_FORMUUID = SELECT_ALL_PARAMETER_RATING + " where formUUID=?";
	public static final String SELECT_PR_BY_FORM_SECTION_PARAMETER_IDS = SELECT_ALL_PARAMETER_RATING
			+ " where formUUID=? AND sectionId=? AND parameterId=?";
	public static final String SELECT_PR_BY_FORM_SECTION_IDS = SELECT_ALL_PARAMETER_RATING
			+ " WHERE formUUID=? and sectionId=?";
	public static final String UPDATE_PARAMETER_RATING = "UPDATE #$DataBaseName#$.dbo.ParameterRatingTable SET maxMarks=?, saScore=?, faScore=?, variance=?, saRemark=?, faRemark=? where formUUID=? AND sectionId=? AND parameterId=?";

	public static final String UPDATE_PARAMETER_RATING_BY_FORM_UUID = "UPDATE #$DataBaseName#$.dbo.ParameterRatingTable SET saScore=0, faScore=0, variance=0 where formUUID IN ";
	public static final String DELETE_PR_BY_FORMUUID = "DELETE FROM #$DataBaseName#$.dbo.ParameterRatingTable where formUUID in ";

	// Job Table Queries
	public static final String JOB_TABLE_NAME = "Job";
//	public static final String CREATE_JOB = "create table #$DataBaseName#$.dbo." + JOB_TABLE_NAME
//			+ "(id varchar(100),operationType varchar(30), jsonObj text, startTime varchar(30),endTime varchar(30),status varchar(30),failureMessage text,retryCount int)";
	public static final String INSERT_JOB = "INSERT INTO #$DataBaseName#$.dbo." + JOB_TABLE_NAME
			+ " (id, operationType, jsonObj, createdTime, startTime, endTime, status, failureMessage, retryCount) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String UPDATE_JOB = "UPDATE #$DataBaseName#$.dbo." + JOB_TABLE_NAME
			+ " SET startTime=?, endTime=?, status=?, failureMessage=?, retryCount=? where id=?";
	public static final String SELECT_ALL_JOB = "SELECT id,operationType, jsonObj,createdTime, startTime, endTime, status, failureMessage, retryCount FROM #$DataBaseName#$.dbo."
			+ JOB_TABLE_NAME;
	public static final String SELECT_JOB_BY_ID = SELECT_ALL_JOB + " where id =?";
	public static final String DELETE_JOB_BY_ID = "DELETE FROM #$DataBaseName#$.dbo." + JOB_TABLE_NAME
			+ " WHERE ID = ?";
	public static final String SELECT_JOBS_FAILED = "select top(200) * from #$DataBaseName#$.dbo.Job where status='FAILED' or status='RETRY FAILED' order by endTime asc";
	public static final String SELECT_JOBS_NEW = "select top(200) * from #$DataBaseName#$.dbo.Job  where status='NEW' or status='IN PROGRESS' ORDER BY createdTime asc";

	// All Reporting related table Creation queries.
	public static final String PARTNERS_REPORTING_TABLE = "create table #$DataBaseName#$.dbo.PartnersReportingTable( partnerProjectId varchar(100) primary key, projectId varchar(100), projectName varchar(150), partnerId varchar(100), piaName varchar(150), headPersonnel varchar(100), contact varchar(10), headPersonEmail varchar(150), centerRating float, projectGrading float, finalPercentage float, grade varchar(5), status bit)";
	public static final String FORM_GRADING_TABLE = "create table #$DataBaseName#$.dbo.FormGradingTable( id varchar(100) primary key, formId varchar(100), partnerProjectId varchar(100), formName varchar(150), maxMarks int, saScore int, faScore int, variance int, status bit, piaDateAssigned varchar(20), piaDateCompletion varchar(20), piaStartTime varchar(20), piaEndTime varchar(20), faName varchar(100), faPhone varchar(10), secondaryAuditorName varchar(100), faLocation geography, faDateAssigned varchar(20), faDateCompleted varchar(20), faStartTime varchar(20), faEndTime varchar(20), signoffTime varchar(20), otp varchar(20), CONSTRAINT FK_FG_PartnerProjectId FOREIGN KEY (partnerProjectId) REFERENCES #$DataBaseName#$.dbo.PartnersReportingTable(partnerProjectId))";
	public static final String FORM_RATING_TABLE = "create table #$DataBaseName#$.dbo.FormRatingTable( id varchar(100) primary key, formId varchar(100), partnerProjectId varchar(100), formName varchar(150), tcName varchar(100), tcId varchar(100), centerAddress varchar(100),  district varchar(100),centerInchargeName varchar(100), centerInchargePhone varchar(10), centerLocation geography, maxMarks int, saScore int, faScore int, variance int, status bit, saDateAssigned varchar(20), saDateCompletion varchar(20), saStartTime varchar(20), saEndTime varchar(20), faName varchar(100), faPhone varchar(10), secondaryAuditorName varchar(100), faLocation geography, faDateAssigned varchar(20), faDateCompleted varchar(20), faStartTime varchar(20), faEndTime varchar(20), signoffTime varchar(20), otp varchar(20), CONSTRAINT FK_FR_PartnerProjectId FOREIGN KEY (partnerProjectId) REFERENCES #$DataBaseName#$.dbo.PartnersReportingTable(partnerProjectId))";
	// parameter Grading
	public static final String PARAMETER_GRADING_TABLE = "create table #$DataBaseName#$.dbo.ParameterGradingTable( formUUID varchar(100), sectionId varchar(100), parameterId varchar(100), maxMarks int, saScore int, faScore int, variance int, saRemark varchar(150),faRemark varchar(150),CONSTRAINT FK_FG_FormUUID FOREIGN KEY (formUUID) REFERENCES #$DataBaseName#$.dbo.FormGradingTable(id))";
	// parameter Rating
	public static final String PARAMETER_RATING_TABLE = "create table #$DataBaseName#$.dbo.ParameterRatingTable( formUUID varchar(100), sectionId varchar(100), parameterId varchar(100), maxMarks int, saScore int, faScore int, variance int, saRemark varchar(150),faRemark varchar(150), CONSTRAINT FK_FR_FormUUID FOREIGN KEY (formUUID) REFERENCES #$DataBaseName#$.dbo.FormRatingTable(id))";

	public static final String JOB = "create table #$DataBaseName#$.dbo.Job(id varchar(100) primary key,operationType varchar(50), jsonObj text, createdTime varchar(50), startTime varchar(50),endTime varchar(50),status varchar(30),failureMessage text,retryCount int)";
}
