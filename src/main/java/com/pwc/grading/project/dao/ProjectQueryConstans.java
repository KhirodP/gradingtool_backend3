package com.pwc.grading.project.dao;
/**
 * A class contains all the database queries related to the project dao operations
 *
 */
public class ProjectQueryConstans {
	public static final String DATA_BASE_PLACE_HOLDER = "#$DataBaseName#$";
	// ******************** Project queries ********************//
	public static final String INSERT_PROJECT = "INSERT INTO #$DataBaseName#$.DBO.PROJECTDATA VALUES(?,?,?,?,?,?)";
	public static final String SELECT_PROJECTS = "SELECT * FROM #$DataBaseName#$.DBO.PROJECTDATA";
	public static final String SELECT_PROJECT_ID = "SELECT * FROM #$DataBaseName#$.DBO.PROJECTDATA WHERE PROJECTID=?";
	public static final String UPDATE_PROJECT_BY_PROJECT_ID = "UPDATE #$DataBaseName#$.DBO.PROJECTDATA SET PROJECTNAME=?,PROJECTDESCRIPTION=?,STARTDATE=?,ENDDATE=?,SELFASSIGNMENTDEADLINE=? WHERE PROJECTID=?";
	public static final String DELETE_PROJECT_BY_PROJECT_ID = "DELETE #$DataBaseName#$.DBO.PROJECTDATA WHERE PROJECTID=?";

	// ******************** Form queries ********************//
	public static final String INSERT_FORM = "INSERT INTO #$DataBaseName#$.DBO.FORMDATA VALUES(?,?,?,?,?,?,?)";
	public static final String SELECT_FORM_BY_FORM_ID = "SELECT * FROM #$DataBaseName#$.DBO.FORMDATA WHERE FORMID=?";
	public static final String SELECT_FORM_BY_PROJECT_ID = "SELECT * FROM #$DataBaseName#$.DBO.FORMDATA WHERE PROJECTID=?";
	public static final String SELECT_FORM_BY_SURVEY_ID = "SELECT * FROM #$DataBaseName#$.DBO.FORMDATA WHERE SURVEYID=?";
	public static final String UPDATE_FORM_BY_FORM_ID = "UPDATE #$DataBaseName#$.DBO.FORMDATA SET PROJECTID=?,FORMNAME=?, USERROLESALLOWED=? WHERE FORMID=?";
	public static final String DELETE_FORM_BY_FORM_ID = "DELETE #$DataBaseName#$.DBO.FORMDATA WHERE FORMID=?";
	public static final String SELECT_FORMS = "SELECT * FROM #$DataBaseName#$.DBO.FORMDATA";
	public static final String PUBLISH_FORM_BY_FORM_ID = "UPDATE #$DataBaseName#$.DBO.FORMDATA SET PUBLISH=? WHERE FORMID=?";
	// ******************** Survey queries ********************//
	public static final String INSERT_SURVEY = "INSERT INTO #$DataBaseName#$.DBO.SURVEYDATA VALUES(?,?,?,?,?,?)";
	public static final String UPDATE_SURVEY_BY_SURVEY_ID = "UPDATE #$DataBaseName#$.DBO.SURVEYDATA SET LASTUPDATE=?,MAXSCORE=?,SURVEYDATA=? WHERE SURVEYID=?";
	public static final String SELECT_SURVEYS = "SELECT * FROM #$DataBaseName#$.DBO.SURVEYDATA";
	public static final String SELECT_SURVEY_BY_SURVEY_ID = "SELECT * FROM #$DataBaseName#$.DBO.SURVEYDATA WHERE SURVEYID=?";
	public static final String DELETE_SURVEY_BY_SURVEY_ID = "DELETE #$DataBaseName#$.DBO.SURVEYDATA WHERE SURVEYID=?";
	public static final String PUBLISH_SURVEY_BY_SURVEY_ID = "UPDATE #$DataBaseName#$.DBO.SURVEYDATA SET PUBLISH=? WHERE SURVEYID=?";
}
