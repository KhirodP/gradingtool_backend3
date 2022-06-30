package com.pwc.grading.user.verification.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.user.verification.dao.IUserVerificationDAO;
import com.pwc.grading.user.verification.dao.UserVerificationQueryConstants;
import com.pwc.grading.user.verification.dao.exception.UserVerificationDAOException;
import com.pwc.grading.user.verification.model.UserVerification;

/**
 * Implementation class for {@link IUserVerificationDAO} 
 *
 */
@Singleton
public class UserVerificationDAOImpl implements IUserVerificationDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserVerificationDAOImpl.class);

	/**
	 * Add the user verification detail for the user.
	 * @param databaseName the database name.
	 * @param userVerify details of the user verification.
	 * @throws UserVerificationDAOException if any exception occurs while performing this operation.
	 */
	@Override
	public void addUserVerification(String databaseName, UserVerification userVerify)
			throws UserVerificationDAOException {
		LOGGER.debug(".inside addUserVerification method of UserVerificationDAOImpl class");
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			pstmt = connection.prepareStatement(UserVerificationQueryConstants.INSERT_USER_VERIFICATION
					.replace(UserVerificationQueryConstants.DATABASE_PLACE_HOLDER , databaseName));
			pstmt.setString(1, userVerify.getUserId());
			pstmt.setString(2, userVerify.getTempPassword());
			pstmt.setString(3, userVerify.getOneTimeAccessToken());
			pstmt.setLong(4, userVerify.getExpiryTime());
			pstmt.setString(5, userVerify.getEmailOtp());
			pstmt.setString(6, userVerify.getSmsOtp());			
			int rows = pstmt.executeUpdate();
			LOGGER.debug(rows +" added into UserVerification succesfully.");
		}catch (Exception e) {
			LOGGER.error("Unable to add UserVerification data, "+e.getMessage());
			throw new UserVerificationDAOException("Unable to add UserVerification data, "+e.getMessage());
		}finally {
			LOGGER.debug("Closing the UserVerification prepareStatement, connection.");
			MSSqlServerUtill.close(pstmt, connection);
		}
		
	}

	/**
	 * Add the user verification detail for the user.
	 * @param connection if this operation is to be performed in single transaction.
	 * @param databaseName the database name.
	 * @param userVerify  details of the user verification.
	 * @throws UserVerificationDAOException if any exception occurs while performing this operation.
	 */
	@Override
	public void addUserVerification(Connection connection, String databaseName, UserVerification userVerify)
			throws UserVerificationDAOException {
		LOGGER.debug(".inside addUserVerification method of UserVerificationDAOImpl class");
		LOGGER.debug("Incoming DAO connection: "+connection);
		PreparedStatement pstmt = null;
		try {
			pstmt = connection.prepareStatement(UserVerificationQueryConstants.INSERT_USER_VERIFICATION
					.replace(UserVerificationQueryConstants.DATABASE_PLACE_HOLDER , databaseName));
			pstmt.setString(1, userVerify.getUserId());
			pstmt.setString(2, userVerify.getTempPassword());
			pstmt.setString(3, userVerify.getOneTimeAccessToken());
			pstmt.setLong(4, userVerify.getExpiryTime());
			pstmt.setString(5, userVerify.getEmailOtp());
			pstmt.setString(6, userVerify.getSmsOtp());			
			int rows = pstmt.executeUpdate();
			LOGGER.debug(rows +" added into UserVerification succesfully.");
		}catch (Exception e) {
			LOGGER.error("Unable to add UserVerification data, "+e.getMessage());
			throw new UserVerificationDAOException("Unable to add UserVerification data, "+e.getMessage());
		}finally {
			LOGGER.debug("Closing the UserVerification prepareStatement.");
			MSSqlServerUtill.close(pstmt, null);
		}
		
	}

	/**
	 * Get the user verification by the userId.
	 * @param databaseName the database name.
	 * @param userId the id of the user.
	 * @return details of the user verification.
	 * @throws UserVerificationDAOException if any exception occurs while performing this operation.
	 */
	@Override
	public UserVerification getUserVerificationByUserId(String databaseName, String userId)
			throws UserVerificationDAOException {
		LOGGER.debug(".inside getUserVerificationByUserId method of UserVerificationDAOImpl class");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet=null;
		try {
			connection = MSSqlServerUtill.getConnection();
			pstmt = connection.prepareStatement(UserVerificationQueryConstants.SELECT_BY_USERID
					.replace(UserVerificationQueryConstants.DATABASE_PLACE_HOLDER , databaseName));
			pstmt.setString(1, userId);			
			resultSet = pstmt.executeQuery();
			UserVerification obj = buildUserVerificationFromResultSet(resultSet);
			return obj;
		
		}catch (Exception e) {
			LOGGER.error("Unable to get UserVerification data, "+e.getMessage());
			throw new UserVerificationDAOException("Unable to get UserVerification data, "+e.getMessage());
		}finally {
			LOGGER.debug("Closing the UserVerification resultSet, prepareStatement, connection.");
			MSSqlServerUtill.close(resultSet,pstmt, connection);
		}
	}


	/**
	 * Get the user verification by the userId.
	 * @param connection if this operation is to be performed in single transaction.
	 * @param databaseName the database name.
	 * @param userId the id of the user.
	 * @return details of the user verification.
	 * @throws UserVerificationDAOException if this operation is to be performed in single transaction.
	 */
	@Override
	public UserVerification getUserVerificationByUserId(Connection connection, String databaseName, String userId)
			throws UserVerificationDAOException {
		LOGGER.debug(".inside getUserVerificationByUserId method of UserVerificationDAOImpl class");
		LOGGER.debug("Incoming DAO connection: "+connection);
		PreparedStatement pstmt = null;
		ResultSet resultSet=null;
		try {
			pstmt = connection.prepareStatement(UserVerificationQueryConstants.SELECT_BY_USERID
					.replace(UserVerificationQueryConstants.DATABASE_PLACE_HOLDER , databaseName));
			pstmt.setString(1, userId);			
			resultSet = pstmt.executeQuery();
			UserVerification obj = buildUserVerificationFromResultSet(resultSet);
			return obj;
		
		}catch (Exception e) {
			LOGGER.error("Unable to get UserVerification data, "+e.getMessage());
			throw new UserVerificationDAOException("Unable to get UserVerification data, "+e.getMessage());
		}finally {
			LOGGER.debug("Closing the UserVerification resultSet, prepareStatement.");
			MSSqlServerUtill.close(resultSet,pstmt, null);
		}
	}

	/**
	 * Delete the user verification by the userId.
	 * @param connection if this operation is to be performed in single transaction.
	 * @param databaseName the database name.
	 * @param userId the id of the user.
	 * @throws UserVerificationDAOException if this operation is to be performed in single transaction.
	 */
	@Override
	public void deleteUserVerificationByUserId(String databaseName, String userId) throws UserVerificationDAOException {
		LOGGER.debug(".inside getUserVerificationByUserId method of UserVerificationDAOImpl class");
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			pstmt = connection.prepareStatement(UserVerificationQueryConstants.DELETE_BY_USERID
					.replace(UserVerificationQueryConstants.DATABASE_PLACE_HOLDER , databaseName));
			pstmt.setString(1, userId);			
			int rows = pstmt.executeUpdate();
			LOGGER.debug(rows +" deleted from UserVerification succesfully.");
		}catch (Exception e) {
			LOGGER.error("Unable to delete UserVerification data, "+e.getMessage());
			throw new UserVerificationDAOException("Unable to delete UserVerification data, "+e.getMessage());
		}finally {
			LOGGER.debug("Closing the UserVerification prepareStatement, connection");
			MSSqlServerUtill.close(pstmt, connection);
		}
		
	}
	
	/**
	 * Delete the user verification by the userId.
	 * @param databaseName the database name.
	 * @param userId the id of the user.
	 * @throws UserVerificationDAOException if this operation is to be performed in single transaction.
	 */
	@Override
	public void deleteUserVerificationByUserId(Connection connection,String databaseName, String userId) throws UserVerificationDAOException {
		LOGGER.debug(".inside getUserVerificationByUserId method of UserVerificationDAOImpl class");
		LOGGER.debug("Incoming DAO connection: "+connection);
		PreparedStatement pstmt = null;
		try {
			pstmt = connection.prepareStatement(UserVerificationQueryConstants.DELETE_BY_USERID
					.replace(UserVerificationQueryConstants.DATABASE_PLACE_HOLDER , databaseName));
			pstmt.setString(1, userId);			
			int rows = pstmt.executeUpdate();
			LOGGER.debug(rows +" deleted from UserVerification succesfully.");
		}catch (Exception e) {
			LOGGER.error("Unable to delete UserVerification data, "+e.getMessage());
			throw new UserVerificationDAOException("Unable to delete UserVerification data, "+e.getMessage());
		}finally {
			LOGGER.debug("Closing the UserVerification resultSet, prepareStatement.");
			MSSqlServerUtill.close(pstmt, null);
		}
		
	}
	
	/**
	 * Update the access token and its expiry for the user-verification.
	 * @param connection if this operation is to be performed in single transaction.
	 * @param databaseName the database name.
	 * @param userVerify  details of the user verification.
	 * @throws UserVerificationDAOException if any exception occurs while performing this operation.
	 */
	@Override
	public void updateAccessTokenAndExpiry(Connection connection, String databaseName, UserVerification userVerify)
			throws UserVerificationDAOException {
		LOGGER.debug(".inside updateAccessTokenAndExpiry method of UserVerificationDAOImpl class");
		LOGGER.debug("Incoming DAO connection: "+connection);
		PreparedStatement pstmt = null;
		try {
			pstmt = connection.prepareStatement(UserVerificationQueryConstants.UPDATE_USER_VERIFICATION
					.replace(UserVerificationQueryConstants.DATABASE_PLACE_HOLDER , databaseName));
			pstmt.setString(1, userVerify.getOneTimeAccessToken());		
			pstmt.setLong(2, userVerify.getExpiryTime());		
			pstmt.setString(3, userVerify.getUserId());		
			int rows = pstmt.executeUpdate();
			LOGGER.debug(rows +" updated to UserVerification succesfully.");
		}catch (Exception e) {
			LOGGER.error("Unable to Update UserVerification data, "+e.getMessage());
			throw new UserVerificationDAOException("Unable to Update UserVerification data, "+e.getMessage());
		}finally {
			LOGGER.debug("Closing the UserVerification prepareStatement.");
			MSSqlServerUtill.close(pstmt, null);
		}
		
	}

	/**
	 * Update the access token and its expiry for the user-verification.
	 * @param databaseName the database name.
	 * @param userVerify  details of the user verification.
	 * @throws UserVerificationDAOException if any exception occurs while performing this operation.
	 */
	@Override
	public void updateAccessTokenAndExpiry(String databaseName, UserVerification userVerify)
			throws UserVerificationDAOException {
		LOGGER.debug(".inside updateAccessTokenAndExpiry method of UserVerificationDAOImpl class");
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			pstmt = connection.prepareStatement(UserVerificationQueryConstants.UPDATE_USER_VERIFICATION
					.replace(UserVerificationQueryConstants.DATABASE_PLACE_HOLDER , databaseName));
			pstmt.setString(1, userVerify.getOneTimeAccessToken());		
			pstmt.setLong(2, userVerify.getExpiryTime());		
			pstmt.setString(3, userVerify.getUserId());		
			int rows = pstmt.executeUpdate();
			LOGGER.debug(rows +" updated to UserVerification succesfully.");
		}catch (Exception e) {
			LOGGER.error("Unable to Update UserVerification data, "+e.getMessage());
			throw new UserVerificationDAOException("Unable to Update UserVerification data, "+e.getMessage());
		}finally {
			LOGGER.debug("Closing the UserVerification prepareStatement,connection.");
			MSSqlServerUtill.close(pstmt, connection);
		}
		
	}
	
	private UserVerification buildUserVerificationFromResultSet(ResultSet resultSet) throws SQLException {
		UserVerification obj = null;
		while(resultSet.next()) {
			obj = new UserVerification();
			obj.setUserId(resultSet.getString(UserVerificationQueryConstants.USER_ID));
			obj.setTempPassword(resultSet.getString(UserVerificationQueryConstants.TEMP_PWD));
			obj.setOneTimeAccessToken(resultSet.getString(UserVerificationQueryConstants.ONETIME_ACCESS_TOKEN));
			obj.setExpiryTime(resultSet.getLong(UserVerificationQueryConstants.EXPIRY_TIME));
			obj.setEmailOtp(resultSet.getString(UserVerificationQueryConstants.EMAIL_OTP));
			obj.setSmsOtp(resultSet.getString(UserVerificationQueryConstants.SMS_OTP));
		}
		return obj;
	}


}
