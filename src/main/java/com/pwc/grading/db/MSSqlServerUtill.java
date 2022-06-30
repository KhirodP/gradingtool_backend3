package com.pwc.grading.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.exception.MSSqlServerException;
import com.pwc.grading.util.PropertiesUtil;

/**
 * MSSqlServerUtill class used to connect to MS Sql Server Database using
 * BasicDatasouce Connection pool
 * 
 *
 */
public class MSSqlServerUtill {
	private static final Logger logger = LoggerFactory.getLogger(MSSqlServerUtill.class);
	private static final String URL = "url";
	private static final String USER = "user";
	private static final String PASSWORD = "password";
	private static final String DRIVER = "driver";
	private static BasicDataSource basicdatasource = null;

	private MSSqlServerUtill() {
	}

	/**
	 * get connection will establish the connection from the BasicDataSource
	 * 
	 * @return connection objectc
	 * @throws MSSqlServerException
	 * @throws DataAccessException
	 * @throws DataBaseException    unable to connect to database
	 */
	public static Connection getConnection() throws MSSqlServerException {
		try {
			// Double check lock
			if (basicdatasource == null) {
				synchronized (MSSqlServerUtill.class) {
					if (basicdatasource == null) {
						logger.debug("getting new connection from database");
						basicdatasource = new BasicDataSource();
						Properties propertie = PropertiesUtil
								.getPropertiesFromResoures(MSSqlServerDBConstants.DB_PROPERTIES_FILE_NAME);
						basicdatasource.setDriverClassName(propertie.getProperty(DRIVER));
						basicdatasource.setUrl(propertie.getProperty(URL));
						basicdatasource.setUsername(propertie.getProperty(USER));
						basicdatasource.setPassword(propertie.getProperty(PASSWORD));
//						basicdatasource.setMaxActive(15);
						basicdatasource.setInitialSize(40);
//						basicdatasource.setMinIdle(5);
//						basicdatasource.setMaxIdle(10);
						basicdatasource.setPoolPreparedStatements(true);
//						basicdatasource.setMinIdle(5);
//						basicdatasource.setValidationQueryTimeout(5);
						logger.debug("Datasource initilized..");
					}
				}
			}
			Connection connection = basicdatasource.getConnection();
			return connection;
		} catch (Exception e) {
			logger.debug("unable to connect to database ::" + e.getMessage());
			throw new MSSqlServerException("unable to connect to database ", e);
		}

	}// ...end of getConnection() constructor

	/**
	 * This close() is used to close the connection of resultset, statement,
	 * connection
	 */
	public static void close(ResultSet resultset, PreparedStatement statment, Connection connectionclose) {
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
	 */
	public static void close(Statement statment, Connection connection) {
		if (statment != null) {
			try {
				statment.close();
			} catch (SQLException e) {
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
			}
		}

	}

}
