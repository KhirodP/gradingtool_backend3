package com.pwc.grading.usertoken.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.usertoken.dao.IUserTokenDao;
import com.pwc.grading.usertoken.dao.UserTokenDaoConstant;
import com.pwc.grading.usertoken.dao.UserTokenQueryConstans;
import com.pwc.grading.usertoken.dao.exception.UserTokenDaoException;
import com.pwc.grading.usertoken.model.UserToken;

/**
 * Implementation class for {@link IUserTokenDao} 
 *
 */
@Singleton
public class UserTokenDaoImpl implements IUserTokenDao {

	private static final Logger logger = LoggerFactory.getLogger(UserTokenDaoImpl.class);

	/**
	 * This method is used to get user token by device Id and userId.
	 * @param deviceId the id of the device of the user.
	 * @param userId the id of the user
	 * @param tenantId the database name.
	 * @return the user token details.
	 * @throws UserTokenDaoException if any exception occurs when performing the operation.
	 */
	@Override
	public UserToken getUserTokenByDeviceIdAndUserId(String deviceId, String userId, String tenantId)
			throws UserTokenDaoException {
		logger.debug("inside getUserTokenByDeviceIdAndUserId method of UserTokenDaoImpl ");
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(UserTokenQueryConstans.SELECT_USER_TOKEN_BY_USERID_AND_DEVICEID
							.replace(UserTokenQueryConstans.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, userId);
			prepareStatement.setString(2, deviceId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("userToken exist");
				UserToken userToken = buildUserTokenFromResultSet(resultSet);
				return userToken;
			}
		} catch (Exception e) {
			logger.error("unable to get User Token data " + e.getMessage(), e);
			throw new UserTokenDaoException("unable to get User Token data ", e);
		} finally {
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

	private UserToken buildUserTokenFromResultSet(ResultSet resultSet) throws SQLException {
		UserToken userToken = new UserToken();
		userToken.setTokenId(resultSet.getString(UserTokenDaoConstant.TOKEN_ID));
		userToken.setDeviceId(resultSet.getString(UserTokenDaoConstant.DEVICE_ID));
		userToken.setExpiryTime(resultSet.getLong(UserTokenDaoConstant.EXP_TIME));
		userToken.setUserId(resultSet.getString(UserTokenDaoConstant.USER_ID));
		userToken.setAccessToken(resultSet.getString(UserTokenDaoConstant.ACCESS_TOKEN));
		userToken.setPlatform(resultSet.getString(UserTokenDaoConstant.PLATFORM));
		return userToken;
	}

	/**
	 * This method is used to get user token by accessToken.
	 * @param accessToken the accessToken of the user.
	 * @param tenantId the database name.
	 * @return the user token details.
	 * @throws UserTokenDaoException if any exception occurs when performing the operation.
	 */
	@Override
	public UserToken getUserTokenByAccessToken(String accessToken, String tenantId) throws UserTokenDaoException {
		logger.debug(" inside getUserTokenByAccessToken of  UserTokenDaoImpl:: accessToken is ::" + accessToken);
		logger.debug("tenantId is :::" + tenantId);
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			logger.debug("got the connection " + connection);
			String query = UserTokenQueryConstans.SELECT_USER_TOKEN_BY_ACCESTOKEN
					.replace(UserTokenQueryConstans.DATA_BASE_PLACE_HOLDER, tenantId);
			logger.debug("Query is " + query);
			prepareStatement = connection.prepareStatement(query);
			prepareStatement.setString(1, accessToken);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("userToken exist");
				UserToken userToken = buildUserTokenFromResultSet(resultSet);
				return userToken;
			}
			logger.debug("userTokent not exist");
			return null;
		} catch (Exception e) {
			logger.error(" unable to get User Token ," + e.getMessage(), e);
			throw new UserTokenDaoException("unable to get User Token data:: " + e.getMessage(), e);
		} finally {
			logger.debug("closing the connection");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
	}

	/**
	 * This method is used to store user accessToken.
	 * @param userToken  the user token details.
	 * @param tenantId the database name.
	 * @throws UserTokenDaoException  if any exception occurs when performing the operation.
	 */
	@Override
	public void storeUserToken(UserToken userToken, String tenantId) throws UserTokenDaoException {
		logger.debug("inside storeUserToken method of UserTokenDaoImpl ");
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			logger.debug(" userToken :: " + userToken + " tenantId :" + tenantId);
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserTokenQueryConstans.INSERT_USER_TOKEN
					.replace(UserTokenQueryConstans.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, userToken.getTokenId());
			prepareStatement.setString(2, userToken.getDeviceId());
			prepareStatement.setLong(3, userToken.getExpiryTime());
			prepareStatement.setString(4, userToken.getUserId());
			prepareStatement.setString(5, userToken.getPlatform());
			prepareStatement.setString(6, userToken.getAccessToken());
			prepareStatement.executeUpdate();
			logger.debug(" added User Token ...");
		} catch (Exception e) {
			logger.error(" unable to store userToken " + e.getMessage(), e);
			throw new UserTokenDaoException("unable to store user token ", e);
		} finally {
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * This method is used to update user accessToken.
	 * @param userToken the user token details.
	 * @param tenantId the database name.
	 * @throws UserTokenDaoException if any exception occurs when performing the operation.
	 */
	@Override
	public void updateUserTokenByTokenId(UserToken userToken, String tenantId) throws UserTokenDaoException {
		logger.debug("inside updateUserToken method of UserTokenDaoImpl ");
		logger.debug(" userToken :: " + userToken + " tenantId :" + tenantId);
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			logger.debug(" userToken :: " + userToken + " tenantId :" + tenantId);
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserTokenQueryConstans.UPDATE_USER_TOKEN_BY_ID
					.replace(UserTokenQueryConstans.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, userToken.getDeviceId());
			prepareStatement.setLong(2, userToken.getExpiryTime());
			prepareStatement.setString(3, userToken.getUserId());
			prepareStatement.setString(4, userToken.getPlatform());
			prepareStatement.setString(5, userToken.getAccessToken());
			prepareStatement.setString(6, userToken.getTokenId());
			int executeUpdate = prepareStatement.executeUpdate();
			if (executeUpdate > 0) {
				logger.debug(" updated User Token ...");
			}
		} catch (Exception e) {
			logger.error(" unable to update UserToken " + e.getMessage(), e);
			throw new UserTokenDaoException("unable to update updateUserToken  ", e);
		} finally {
			logger.debug("closing the connection");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

	}

	/**
	 * This method is used to delete user accessToken.
	 * @param deviceId the id of the device of the user.
	 * @param userId  the id of the user
	 * @param tenantId the database name.
	 * @throws UserTokenDaoException if any exception occurs when performing the operation.
	 */
	@Override
	public void deleteUserTokenByUserId(String deviceId, String userId, String tenantId) throws UserTokenDaoException {
		logger.debug("inside deleteUserTokenByUserId method of UserTokenDaoImpl ");
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(UserTokenQueryConstans.DELETE_USER_TOKEN_BY_USERID_AND_DEVICEID
							.replace(UserTokenQueryConstans.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, userId);
			prepareStatement.setString(2, deviceId);
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + "row deleted from User Token ...");
		} catch (Exception e) {
			logger.error(" unable to delete UserToken " + e.getMessage(), e);
			throw new UserTokenDaoException("unable to update updateUserToken  ", e);
		} finally {
			logger.debug("closing the connection");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * This method is used to delete user accessToken.
	 * @param connection if this operation to be performed in single transaction.
	 * @param userId the id of the user
	 * @param tenantId the database name.
	 * @throws UserTokenDaoException if any exception occurs when performing the operation.
	 */
	@Override
	public void deleteUserTokenByUserId(Connection connection, String userId, String tenantId)
			throws UserTokenDaoException {
		logger.debug("inside deleteUserTokenByUserId method of UserTokenDaoImpl ");
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(UserTokenQueryConstans.DELETE_USER_TOKEN_BY_USERID
					.replace(UserTokenQueryConstans.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, userId);
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " row deleted from User Token ...");
		} catch (Exception e) {
			logger.error(" unable to delete UserToken " + e.getMessage(), e);
			throw new UserTokenDaoException("unable to update updateUserToken  ", e);
		} finally {
			logger.debug("closing the connection");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}
}
