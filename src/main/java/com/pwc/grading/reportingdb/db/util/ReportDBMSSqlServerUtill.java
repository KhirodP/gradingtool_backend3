package com.pwc.grading.reportingdb.db.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerDBConstants;
import com.pwc.grading.db.MSSqlServerInstance;
import com.pwc.grading.reportingdb.db.exception.ReportDBMSSqlServerException;
import com.pwc.grading.util.PropertiesUtil;

/**
 * An utility class used to get the connection to perform the reporting tables 
 * related operations and closing its resources
 * 
 *
 */
public class ReportDBMSSqlServerUtill {
	private static final Logger logger = LoggerFactory.getLogger(ReportDBMSSqlServerUtill.class);
	private static final String URL = "url";
	private static final String USER = "user";
	private static final String PASSWORD = "password";
	private static final String DRIVER = "driver";
	private static BasicDataSource reportDBdatasource = null;

	private ReportDBMSSqlServerUtill() {
	}

	/**
	 * get connection will establish the connection from the BasicDataSource
	 * 
	 * @return connection object
	 * @throws ReportDBMSSqlServerException unable to connect to database 
	 */
	public static Connection getConnection() throws ReportDBMSSqlServerException {
		try {// Double check lock
			if (reportDBdatasource == null) {
				synchronized (MSSqlServerInstance.class) {
					if (reportDBdatasource == null) {
						logger.debug("Report DB ::: getting new connection from database");
						reportDBdatasource = new BasicDataSource();
						Properties propertie = PropertiesUtil
								.getPropertiesFromResoures(MSSqlServerDBConstants.DB_PROPERTIES_FILE_NAME);
						reportDBdatasource.setDriverClassName(propertie.getProperty(DRIVER));
						reportDBdatasource.setUrl(propertie.getProperty(URL));
						reportDBdatasource.setUsername(propertie.getProperty(USER));
						reportDBdatasource.setPassword(propertie.getProperty(PASSWORD));
						reportDBdatasource.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
//						basicdatasource.setMaxActive(200);
						logger.debug("Report DB Datasource initilized..");
					}
				}
			}
			logger.debug("###^ Report DB Num Active: "+reportDBdatasource.getNumActive());
			logger.debug("###^ Report DB Num Idle: "+reportDBdatasource.getNumIdle());
			logger.debug("###^ Report DB Max Active: "+reportDBdatasource.getMaxActive());
			Connection connection = reportDBdatasource.getConnection();
			logger.debug("###^ Got Connection, Returning connection...");
			return connection;
		} catch (Exception e) {
			logger.debug("unable to connect to database ::" + e.getMessage());
			throw new ReportDBMSSqlServerException("unable to connect to database ", e);
		}

	}// ...end of getConnection() constructor

	/**
	 * This close() is used to close the connection of resultset, statement,
	 * connection
	 */
	public static void close(ResultSet resultset, Statement statment, Connection connectionclose) {
		if (resultset != null) {
			try {
				resultset.close();
			} catch (SQLException e) {
				logger.error("unable to close the result set  " + e.getMessage());
			}
		}
		if (statment != null) {
			try {
				statment.close();
			} catch (SQLException e) {
				logger.error("unable to close the statment " + e.getMessage());
			}
		}
		if (connectionclose != null) {
			try {
				connectionclose.close();
			} catch (SQLException e) {
				logger.error("unable to close the connection " + e.getMessage());
			}
		}
	}

	/**
	 * This close() is used to close the connections of preparedstatment and
	 * connection
	 *
	 */
	public static void close(Statement statment, Connection connection) {
		if (statment != null) {
			try {
				statment.close();
			} catch (SQLException e) {
				logger.debug("Cannot close the ReportDB Statement.");
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.debug("Cannot close the ReportDB connection.");
			}
		}

	}

}
