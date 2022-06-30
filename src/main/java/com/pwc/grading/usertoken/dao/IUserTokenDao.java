package com.pwc.grading.usertoken.dao;

import java.sql.Connection;

import com.pwc.grading.usertoken.dao.exception.UserTokenDaoException;
import com.pwc.grading.usertoken.model.UserToken;
/**
 * An interface class which is used to perform all
 * User Token related database operations.
 *
 */
public interface IUserTokenDao {

	/**
	 * This method is used to get user token by device Id and userId.
	 * @param deviceId the id of the device of the user.
	 * @param userId the id of the user
	 * @param tenantId the database name.
	 * @return the user token details.
	 * @throws UserTokenDaoException if any exception occurs when performing the operation.
	 */
	public UserToken getUserTokenByDeviceIdAndUserId(String deviceId, String userId, String tenantId)
			throws UserTokenDaoException;

	/**
	 * This method is used to get user token by accessToken.
	 * @param accessToken the accessToken of the user.
	 * @param tenantId the database name.
	 * @return the user token details.
	 * @throws UserTokenDaoException if any exception occurs when performing the operation.
	 */
	public UserToken getUserTokenByAccessToken(String accessToken, String tenantId) throws UserTokenDaoException;

	/**
	 * This method is used to store user accessToken.
	 * @param userToken  the user token details.
	 * @param tenantId the database name.
	 * @throws UserTokenDaoException  if any exception occurs when performing the operation.
	 */
	public void storeUserToken(UserToken userToken, String tenantId) throws UserTokenDaoException;

	/**
	 * This method is used to update user accessToken.
	 * @param userToken the user token details.
	 * @param tenantId the database name.
	 * @throws UserTokenDaoException if any exception occurs when performing the operation.
	 */
	public void updateUserTokenByTokenId(UserToken userToken, String tenantId) throws UserTokenDaoException;

	/**
	 * This method is used to delete user accessToken.
	 * @param deviceId the id of the device of the user.
	 * @param userId  the id of the user
	 * @param tenantId the database name.
	 * @throws UserTokenDaoException if any exception occurs when performing the operation.
	 */
	public void deleteUserTokenByUserId(String deviceId, String userId, String tenantId) throws UserTokenDaoException;

	/**
	 * This method is used to delete user accessToken.
	 * @param connection if this operation to be performed in single transaction.
	 * @param userId the id of the user
	 * @param tenantId the database name.
	 * @throws UserTokenDaoException if any exception occurs when performing the operation.
	 */
	void deleteUserTokenByUserId(Connection connection, String userId, String tenantId) throws UserTokenDaoException;

}
