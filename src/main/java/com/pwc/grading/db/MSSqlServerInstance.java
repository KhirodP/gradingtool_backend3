package com.pwc.grading.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.exception.MSSqlServerException;
import com.pwc.grading.reportingdb.db.constant.ReportingDatabaseQueryConstants;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.UserSecurityProtecter;

/**
 * MSSqlServerInstance used to check Wheter Db exist or not if not it will
 * create the Database and Tables
 * 
 *
 */
public class MSSqlServerInstance extends MSSqlServerDBConstants {
	private static final Logger logger = LoggerFactory.getLogger(MSSqlServerInstance.class);
	private static final String DB_CREATE = "CREATE DATABASE ";

	/**
	 * Check if the database is exist or not.
	 */
	private static boolean isDBExist(String databaseName) throws SQLException, MSSqlServerException {
		Connection connection = null;
		ResultSet restuletSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			logger.debug("got the connection " + connection);
			restuletSet = connection.getMetaData().getCatalogs();
			while (restuletSet.next()) {
				String currentDBName = restuletSet.getString(1);
				if (currentDBName.contentEquals(databaseName)) {
					logger.debug(databaseName + " database found");
					return true;
				}
			}
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(restuletSet, null, connection);
		}
		return false;
	}

	/**
	 * This method is used to create the database if it is not exists.
	 * @param dataBaseName to be created
	 * @param tenantId the tenantId key.
	 * @throws MSSqlServerException if unable to create database.
	 */
	public static void createDataBases(String dataBaseName, String tenantId) throws MSSqlServerException {
		Connection connection = null;
		Statement statment = null;
		try {
			boolean isDbExist = isDBExist(dataBaseName);
			if (!isDbExist) {
				logger.info(dataBaseName + " database Not exits creating database ......");
				connection = MSSqlServerUtill.getConnection();
				statment = connection.createStatement();
				statment.executeUpdate(DB_CREATE + dataBaseName);
				createUserManagmentTables(statment, dataBaseName, tenantId);
				createProjectTables(statment, dataBaseName);
				createMasterManagmentTables(statment, dataBaseName);
				createSureyResponse(statment, dataBaseName);
				createMediaTable(statment, dataBaseName);
				createUserTrackingTables(statment, dataBaseName);
				createReportingTables(statment, dataBaseName);
				logger.info("** create [" + dataBaseName + "] database successfully... **");
			} else {
				logger.info(" ** [" + dataBaseName + "] database already exists .....");
			}
		} catch (Exception e) {
			logger.error("unable to create [" + dataBaseName + "] database , " + e.getMessage());
			throw new MSSqlServerException("unable to create [" + dataBaseName + "] database , " + e.getMessage());
		} finally {
			MSSqlServerUtill.close(statment, connection);
		}
	}


	/**
	 * Create the user tracking related tables.
	 */
	private static void createUserTrackingTables(Statement statment, String dataBaseName) throws SQLException {
		statment.executeUpdate(USER_LOG_TRACKING_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# USER_LOG_TRACKING_TABLE created");
		statment.executeUpdate(USER_SURVEY_RESPONSE_TRACKING_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# USER_SURVEY_RESPONSE_TRACKING_TABLE created");
	}

	/**
	 * Create the Media related tables.
	 */
	private static void createMediaTable(Statement statment, String dataBaseName) throws SQLException {
		statment.executeUpdate(MEDIA_DATA_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# MediaData Table created");
	}

	/**
	 * Create the Survey response related tables.
	 */
	private static void createSureyResponse(Statement statment, String dataBaseName) throws SQLException {
		statment.executeUpdate(SURVEY_RESPONSE_DATA_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
//		statment.execute(INDEX_SURVEY_RESPONSE_TRINING_CENTER.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
//		statment.execute(INDEX_SURVEY_RESPONSE_PARTNER.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
//		statment.execute(INDEX_SURVEY_RESPONSE_FIELD_AUDITOR.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# SurveyResponse Table created");

	}

	/**
	 * Create the user related tables.
	 */
	private static void createUserManagmentTables(Statement statment, String dataBaseName, String tenantId)
			throws SQLException, MSSqlServerException {
		statment.executeUpdate(USER_DETAILS_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		statment.execute(INDEX_USER_EMAIL.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		statment.execute(INDEX_USER_PHONE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# UserDetails Table created");
		statment.executeUpdate(USER_TOKEN_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# UserToken Table created ");
//		String protectAdminPassword = UserSecurityProtecer.protectUserData("admin");
//		statment.executeUpdate(INSERT_ADMIN.replace(DATA_BASE_PLACE_HOLDER, dataBaseName).replace(PASSWORD_PLACE_HOLDER,
//				protectAdminPassword));

		createAdminUser(statment, dataBaseName, tenantId);
		statment.executeUpdate(USER_VERIFICATION_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# UserVerification Table created..");

	}

	/**
	 * Create the admin user for the user table.
	 */
	private static void createAdminUser(Statement statment, String dataBaseName, String tenantId)
			throws SQLException, MSSqlServerException {
		String firstName = ReadPropertiesFile.getAdminUserForTenant(tenantId + USER_ADMIN_FIRST_NAME);
		String lastName = ReadPropertiesFile.getAdminUserForTenant(tenantId + USER_ADMIN_LAST_NAME);
		String phone = ReadPropertiesFile.getAdminUserForTenant(tenantId + USER_ADMIN_PHONE);
		String email = ReadPropertiesFile.getAdminUserForTenant(tenantId + USER_ADMIN_EMAIL);
		String password = ReadPropertiesFile.getAdminUserForTenant(tenantId + USER_ADMIN_PWD);
		String protectAdminPassword = UserSecurityProtecter.protectUserData(password);
		String insertAdmin = "INSERT INTO " + dataBaseName + ".DBO.UserDetails VALUES(NEWID(),'" + firstName + "','"
				+ lastName + "','" + protectAdminPassword + "','" + email + "','" + phone
				+ "','Admin','','active','','','','','')";
		statment.executeUpdate(insertAdmin);
		logger.debug("# Admin user created..");
	}

	/**
	 * Create the Project related tables.
	 */
	private static void createProjectTables(Statement statment, String dataBaseName) throws SQLException {
		statment.executeUpdate(PROJECT_DATA_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# project Table created");
		statment.executeUpdate(FORM_DATA_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# form data Table created");
		statment.executeUpdate(SURVEY_DATA_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# survey data Table created");
	}

	/**
	 * Create the master data management related tables.
	 */
	private static void createMasterManagmentTables(Statement statment, String dataBaseName) throws SQLException {
		statment.executeUpdate(PARTNER_DETAILS_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# partner Table created");
		statment.executeUpdate(TRAINING_CENTER_DETAILS_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# Training Center Details Table created");
		statment.executeUpdate(PROJECT_MAPPING_DATA_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		statment.executeUpdate(GRADING_TYPE_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		statment.executeUpdate(RATING_TYPE_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		statment.executeUpdate(GRADING_ENABLE_TABLE.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		statment.executeUpdate(INDEX_PROJECT_MAPPING_TCID_AND_PROJECTID.replace(DATA_BASE_PLACE_HOLDER, dataBaseName));
		logger.debug("# Project Mapping Details Table created");
	}

	/**
	 * Create the reporting related tables.
	 */
	private static void createReportingTables(Statement statement, String databaseName) throws SQLException {
		statement.executeUpdate(ReportingDatabaseQueryConstants.PARTNERS_REPORTING_TABLE
				.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
		logger.debug("# PartnersReporting Table Created");
		statement.executeUpdate(ReportingDatabaseQueryConstants.FORM_GRADING_TABLE
				.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
		logger.debug("# FormGrading Table Created");
		statement.executeUpdate(ReportingDatabaseQueryConstants.FORM_RATING_TABLE
				.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
		logger.debug("# FormRating Table Created");
		statement.executeUpdate(ReportingDatabaseQueryConstants.PARAMETER_GRADING_TABLE
				.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
		logger.debug("# ParameterGrading Table Created");
		statement.executeUpdate(ReportingDatabaseQueryConstants.PARAMETER_RATING_TABLE
				.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
		logger.debug("# ParameterRating Table Created");
		statement.executeUpdate(ReportingDatabaseQueryConstants.JOB
				.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
		logger.debug("# Job Table Created");
	}
}
