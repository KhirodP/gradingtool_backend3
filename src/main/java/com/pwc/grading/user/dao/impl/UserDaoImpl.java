package com.pwc.grading.user.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.user.dao.IUserDao;
import com.pwc.grading.user.dao.UserDaoConstant;
import com.pwc.grading.user.dao.UserQueryConstants;
import com.pwc.grading.user.dao.exception.UserDaoException;
import com.pwc.grading.user.model.ForgotPasswordUserOtp;
import com.pwc.grading.user.model.User;
import com.pwc.grading.user.model.UserOtp;

/**
 * Implementation class for {@link IUserDao} 
 *
 */
@Singleton
public class UserDaoImpl implements IUserDao {
	private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

	/**
	 * This method is used to add new user to database.
	 * @param databaseName the database name.
	 * @param user the user details
	 * @return id of the user created.
	 * @throws UserDaoException if any exception occurs.
	 */
	@Override
	public String addUser(String databaseName, User user) throws UserDaoException {
		logger.debug(".in add user, user is: " + StringEscapeUtils.escapeJava(user.toString()));
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(
					UserQueryConstants.INSERT_USER.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			String userId = user.getUserId();
			prepareStatement.setString(1, userId);
			prepareStatement.setString(2, user.getFirstName());
			prepareStatement.setString(3, user.getLastName());
			prepareStatement.setString(4, user.getPassword());
			prepareStatement.setString(5, user.getEmail());
			prepareStatement.setString(6, user.getPhone());
			prepareStatement.setString(7, user.getUserRole());
			prepareStatement.setString(8, user.getCenterId());
			prepareStatement.setString(9, user.getStatus());
			prepareStatement.setString(10, "");
			prepareStatement.setLong(11, 0);
			prepareStatement.setString(12, "");
			prepareStatement.setString(13, "");
			prepareStatement.setLong(14, 0);
			prepareStatement.executeUpdate();
			return userId;
		} catch (Exception e) {
			logger.error("unable to add user " + e.getMessage());
			throw new UserDaoException("unable to add user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

	}

	/**
	 * This method is used to add new user to database.
	 * @param databaseName the database name.
	 * @param connection used for transaction purposes.
	 * @param user the user details
	 * @return id of the user created.
	 * @throws UserDaoException if any exception occurs.
	 */
	@Override
	public String addUser(Connection connection, String databaseName, User user) throws UserDaoException {
		logger.debug(".in add user, user is: " + user);
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(
					UserQueryConstants.INSERT_USER.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			String userId = user.getUserId();
			prepareStatement.setString(1, userId);
			prepareStatement.setString(2, user.getFirstName());
			prepareStatement.setString(3, user.getLastName());
			prepareStatement.setString(4, user.getPassword());
			prepareStatement.setString(5, user.getEmail());
			prepareStatement.setString(6, user.getPhone());
			prepareStatement.setString(7, user.getUserRole());
			prepareStatement.setString(8, user.getCenterId());
			prepareStatement.setString(9, user.getStatus());
			prepareStatement.setString(10, "");
			prepareStatement.setLong(11, 0);
			prepareStatement.setString(12, "");
			prepareStatement.setString(13, "");
			prepareStatement.setLong(14, 0);
			prepareStatement.executeUpdate();
			return userId;
		} catch (Exception e) {
			logger.error("unable to add user " + e.getMessage());
			throw new UserDaoException("unable to add user " + e.getMessage());
		} finally {
			logger.debug("closing the preparedStatment");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

	/**
	 * Get all the users from the table.
	 * @param databaseName  the database name.
	 * @return the list of users.
	 * @throws UserDaoException if any exception occurs when fetching data.
	 */
	@Override
	public List<User> getUsers(String databaseName) throws UserDaoException {
		List<User> listOfUsers = new ArrayList<User>();
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.SELECT_ALL_USER
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				logger.debug("user exist");
				User user = buldUserFromResultSet(resultSet);
				listOfUsers.add(user);
			}
			return listOfUsers;
		} catch (Exception e) {
			logger.error("unable to get all  user " + e.getMessage());
			throw new UserDaoException("unable to get all user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
	}

	/**
	 * Get the user for the given email-Id.
	 * @param databaseName  the database name.
	 * @param email the email whose details is to be fetched.
	 * @return the object which is having user details.
	 * @throws UserDaoException if any exception occurs when fetching data.
	 */
	@Override
	public User getUserByEmail(String databaseName, String email) throws UserDaoException {
		logger.debug(".in getUserByEmail , email is ::" + email + " and database name is " + databaseName);
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.SELECT_USER_BY_EMAIL
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, email);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("user exist");
				User user = buldUserFromResultSet(resultSet);
				return user;
			}
		} catch (Exception e) {
			logger.error("unable to get user " + e.getMessage());
			throw new UserDaoException("unable to get user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

	/**
	 * Get the user for the given email-Id.
	 * @param databaseName  the database name.
	 * @param connection used for transaction purposes.
	 * @param email the email whose details is to be fetched.
	 * @return the object which is having user details.
	 * @throws UserDaoException if any exception occurs when fetching data.
	 */
	@Override
	public User getUserByEmail(Connection connection, String databaseName, String email) throws UserDaoException {
		logger.debug(".in getUserByEmail , email is ::" + email + " and database name is " + databaseName);
//		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(UserQueryConstants.SELECT_USER_BY_EMAIL
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, email);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("user exist");
				User user = buldUserFromResultSet(resultSet);
				return user;
			}
		} catch (Exception e) {
			logger.error("unable to get user " + e.getMessage());
			throw new UserDaoException("unable to get user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return null;
	}

	private User buldUserFromResultSet(ResultSet resultSet) throws SQLException {
		User user = new User();
		user.setUserId(resultSet.getString(UserDaoConstant.USER_ID));
		user.setFirstName(resultSet.getString(UserDaoConstant.FIRST_NAME));
		user.setLastName(resultSet.getString(UserDaoConstant.LAST_NAME));
		user.setPassword(resultSet.getString(UserDaoConstant.PWD));
		user.setEmail(resultSet.getString(UserDaoConstant.EMAIL));
		user.setPhone(resultSet.getString(UserDaoConstant.PHONE));
		user.setUserRole(resultSet.getString(UserDaoConstant.ROLE));
		user.setCenterId(resultSet.getString(UserDaoConstant.CENTER_ID));
		user.setStatus(resultSet.getString(UserDaoConstant.STATUS));
		return user;
	}

	/**
	 * Get user by user id. 
	 * @param databaseName  the database name.
	 * @param userId the userId whose details is to be fetched.
	 * @return the object which is having user details.
	 * @throws UserDaoException if any exception occurs when fetching data.
	 */
	@Override
	public User getUserByUserId(String databaseName, String userId) throws UserDaoException {
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.SELECT_USER_BY_USER_ID
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, userId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("user exist");
				User user = buldUserFromResultSet(resultSet);
				logger.debug("useir is " + user);
				return user;
			}
		} catch (Exception e) {
			logger.error("unable to get user " + e.getMessage());
			throw new UserDaoException("unable to get user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}
	

	/**
	 * Delete the user by userId.
	 * @param connection if transaction is required for this operation.
	 * @param databaseName  the database name.
	 * @param userId the userId to be deleted.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void deleteUserById(Connection connection, String databaseName, String userId) throws UserDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(UserQueryConstants.DELETE_USER_BY_USER_ID
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, userId);
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + " rows in user deleted successfully");
		} catch (Exception e) {
			logger.error("unable to update user status" + e.getMessage());
			throw new UserDaoException("unable to update user status" + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * Update the user's otp by the email.
	 * @param databaseName  the database name.
	 * @param email the email whose otp is to be updated.
	 * @param userOtp otp details of the user.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateUserOtpByEmail(String databaseName, String email, UserOtp userOtp) throws UserDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.UPDATE_USER_OTP_BY_USER_EMAIL
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, userOtp.getUserOtp());
			prepareStatement.setLong(2, userOtp.getExpiryDate());
			prepareStatement.setString(3, email);
			int executeUpdate = prepareStatement.executeUpdate();
			if (executeUpdate > 0) {
				logger.debug("otp updated successfully");
			}
		} catch (Exception e) {
			logger.error("unable to update user otp" + e.getMessage());
			throw new UserDaoException("unable to update user otp" + e.getMessage());
		} finally {
			logger.debug("closing the user connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

	}

	/**
	 * Get the user's otp by the email.
	 * @param databaseName the database name.
	 * @param email the email whose otp is to be updated.
	 * @return otp details of the user.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public UserOtp getUserOtpByEmail(String databaseName, String email) throws UserDaoException {
		logger.debug(".in getUserOtpByEmail , email is ::", email + " and database name is " + databaseName);
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.SELECT_USER_BY_EMAIL
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, email);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("user exist");
				UserOtp userOtp = buildUserOtpFromResultSet(resultSet);
				return userOtp;
			}
		} catch (Exception e) {
			logger.error("unable to get user " + e.getMessage());
			throw new UserDaoException("unable to get user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

	private UserOtp buildUserOtpFromResultSet(ResultSet resultSet) throws SQLException {
		UserOtp userOtp = new UserOtp();
		userOtp.setUserOtp(resultSet.getString(UserDaoConstant.OTP));
		userOtp.setExpiryDate(resultSet.getLong(UserDaoConstant.EXPIRY_DATE));
		return userOtp;
	}

	/**
	 * Update the user status by the userId
	 * @param userId the userId whose status to be updated.
	 * @param tenantId the database name.
	 * @param requestBody status to be updated.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateUserStatus(String userId, String databaseName, String status) throws UserDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.UPDATE_USER_STATUS_BY_USER_ID
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, status);
			prepareStatement.setString(2, userId);
			int executeUpdate = prepareStatement.executeUpdate();
			if (executeUpdate > 0) {
				logger.debug("status updated successfully");
			}
		} catch (Exception e) {
			logger.error("unable to update user status" + e.getMessage());
			throw new UserDaoException("unable to update user status" + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * Update user status by userId.
	 * @param userId the userId whose status to be updated.
	 * @param tenantId the database name.
	 * @param requestBody the new userDetails
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateUser(String databaseName, String userId, User user) throws UserDaoException {
		logger.debug(".in update user database name is ::" + databaseName + " userId is ::" + userId
				+ " request user is ::" + user);
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.UPDATE_USER_BY_ID
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, user.getFirstName());
			prepareStatement.setString(2, user.getLastName());
			prepareStatement.setString(3, user.getEmail());
			prepareStatement.setString(4, user.getPhone());
			prepareStatement.setString(5, user.getUserRole());
			prepareStatement.setString(6, user.getCenterId());
			prepareStatement.setString(7, userId);
			prepareStatement.executeUpdate();
		} catch (Exception e) {
			logger.error("unable to Update user " + e.getMessage());
			throw new UserDaoException("unable to Update user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

	}

	/**
	 * Check the userExist or not.
	 * @param databaseName the database name.
	 * @param email the email which is to be checked
	 * @return true if exists, false if not exists.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public boolean isUserExist(String databaseName, String email) throws UserDaoException {
		logger.debug(".in getUserByEmail , email is ::" + email + " and database name is " + databaseName);
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.SELECT_USER_BY_EMAIL
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, email);
			logger.debug("prepareStatement");
			resultSet = prepareStatement.executeQuery();
			logger.debug("resultSet");
			if (resultSet.next()) {
				logger.debug("user exist");
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("unable to find user " + e.getMessage());
			throw new UserDaoException("unable to find user " + e.getMessage());
		} finally {
			logger.debug("closing the user connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}

	}

	/**
	 * Check the userExist or not.
	 * @param databaseName the database name.
	 * @param connection used for transaction purposes.
	 * @param email the email which is to be checked
	 * @return true if exists, false if not exists.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public boolean isUserExist(Connection connection, String databaseName, String email) throws UserDaoException {
		logger.debug(".in getUserByEmail , email is ::" + email + " and database name is " + databaseName);
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(UserQueryConstants.SELECT_USER_BY_EMAIL
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, email);
			logger.debug("prepareStatement");
			resultSet = prepareStatement.executeQuery();
			logger.debug("resultSet");
			if (resultSet.next()) {
				logger.debug("user exist");
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("unable to find user " + e.getMessage());
			throw new UserDaoException("unable to find user " + e.getMessage());
		} finally {
			logger.debug("closing the user connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}

	}

	/**
	 * Update the user's password by the userId.
	 * @param databaseName  the database name.
	 * @param userId the userId whose password is to be updated.
	 * @param password the password to be updated.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateUserPasswordByUserId(String databaseName, String userId, String password)
			throws UserDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.UPDATE_USER_PASSWORD_BY_USER_ID
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, password);
			prepareStatement.setString(2, userId);
			int executeUpdate = prepareStatement.executeUpdate();
			if (executeUpdate > 0) {
				logger.debug("password updated successfully");
			}
		} catch (Exception e) {
			logger.error("unable to update user " + e.getMessage());
			throw new UserDaoException("unable to update user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * Update the user's password by the userId.
	 * @param databaseName  the database name.
	 * @param connection used for transaction purposes.
	 * @param userId the userId whose password is to be updated.
	 * @param password the password to be updated.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateUserPasswordByUserId(Connection connection, String databaseName, String userId, String password)
			throws UserDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(UserQueryConstants.UPDATE_USER_PASSWORD_BY_USER_ID
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, password);
			prepareStatement.setString(2, userId);
			int executeUpdate = prepareStatement.executeUpdate();
			if (executeUpdate > 0) {
				logger.debug("password updated successfully");
			}
		} catch (Exception e) {
			logger.error("unable to update user " + e.getMessage());
			throw new UserDaoException("unable to update user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * This method is used to add the users in batch.
	 * @param databaseName the database name.
	 * @param user the user details
	 * @throws UserDaoException if any exception occurs.
	 */
	@Override
	public void uploadUserExcel(String databaseName, List<User> users) throws UserDaoException {
		logger.debug(".in add user, database name is: " + databaseName);
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			prepareStatement = connection.prepareStatement(
					UserQueryConstants.INSERT_USER.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			for (User user : users) {
				prepareStatement.setString(1, UUID.randomUUID().toString());
				prepareStatement.setString(2, user.getFirstName());
				prepareStatement.setString(3, user.getLastName());
				prepareStatement.setString(4, user.getPassword());
				prepareStatement.setString(5, user.getEmail());
				prepareStatement.setString(6, user.getPhone());
				prepareStatement.setString(7, user.getUserRole());
				prepareStatement.setString(8, user.getCenterId());
				prepareStatement.setString(9, user.getStatus());

				prepareStatement.addBatch();
			}

			int total = prepareStatement.executeUpdate();
			connection.commit();
			logger.debug(total + " users inserted");

		} catch (Exception e) {
			logger.error("unable to add user " + e.getMessage());
			throw new UserDaoException("unable to add user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * Update the user's centerId by the userId if the user is CenterInCharge.
	 * @param databaseName  the database name.
	 * @param userId the userId whose password is to be updated.
	 * @param centerId the tcId to be updated.
	 * @throws UserDaoException  if any exception occurs when performing this operation.
	 */
	@Override
	public void updateCenterIdByUserId(String databaseName, String userId, String centerId) throws UserDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.UPDATE_USER_CENTER_ID_BY_USER_ID
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, centerId);
			prepareStatement.setString(2, userId);
			int executeUpdate = prepareStatement.executeUpdate();
			if (executeUpdate > 0) {
				logger.debug("user center updated successfully ");
			}
		} catch (Exception e) {
			logger.error("unable to update user center" + e.getMessage());
			throw new UserDaoException("unable to update user center" + e.getMessage());
		} finally {
			logger.debug("closing the user connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}

	}

	/**
	 * Update the user's centerId by the userId if the user is CenterInCharge.
	 * @param databaseName  the database name.
	 * @param connection used for transaction purposes.
	 * @param userId the userId whose password is to be updated.
	 * @param centerId the tcId to be updated.
	 * @throws UserDaoException  if any exception occurs when performing this operation.
	 */
	@Override
	public void updateCenterIdByUserId(Connection connection, String databaseName, String userId, String centerId)
			throws UserDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(UserQueryConstants.UPDATE_USER_CENTER_ID_BY_USER_ID
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, centerId);
			prepareStatement.setString(2, userId);
			prepareStatement.executeUpdate();
		} catch (Exception e) {
			logger.error("unable to update user center" + e.getMessage());
			throw new UserDaoException("unable to update user center" + e.getMessage());
		} finally {
			logger.debug("closing the user connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}

//	@Override
//	public void updateUserForgetPasswordOtpByEmail(String databaseName, String email, UserOtp userOtp)
//			throws UserDaoException {
//		Connection connection = null;
//		PreparedStatement prepareStatement = null;
//		try {
//			connection = MSSqlServerUtill.getConnection();
//			prepareStatement = connection
//					.prepareStatement(UserQueryConstants.UPDATE_USER_FORGET_PASSWORD_OTP_BY_USER_EMAIL
//							.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
//			prepareStatement.setString(1, userOtp.getUserOtp());
//			prepareStatement.setLong(2, userOtp.getExpiryDate());
//			prepareStatement.setString(3, email);
//			int executeUpdate = prepareStatement.executeUpdate();
//			logger.debug(executeUpdate + "row updated successfully");
//		} catch (Exception e) {
//			logger.error("unable to update user otp" + e.getMessage());
//			throw new UserDaoException("unable to update user otp" + e.getMessage());
//		} finally {
//			logger.debug("closing the user connections");
//			MSSqlServerUtill.close(prepareStatement, connection);
//		}
//
//	}

//	@Override
//	public UserOtp getUserForgetPasswordOtpByEmail(String databaseName, String email) throws UserDaoException {
//		logger.debug(".in getUserOtpByEmail , email is ::" + email + " and database name is " + databaseName);
//		Connection connection = null;
//		ResultSet resultSet = null;
//		PreparedStatement prepareStatement = null;
//		try {
//			connection = MSSqlServerUtill.getConnection();
//			prepareStatement = connection.prepareStatement(UserQueryConstants.SELECT_USER_BY_EMAIL
//					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
//			prepareStatement.setString(1, email);
//			resultSet = prepareStatement.executeQuery();
//			if (resultSet.next()) {
//				logger.debug("user exist");
//				UserOtp userOtp = buildUserForgetPasswordOtpFromResultSet(resultSet);
//				return userOtp;
//			}
//		} catch (Exception e) {
//			logger.error("unable to get user " + e.getMessage());
//			throw new UserDaoException("unable to get user " + e.getMessage());
//		} finally {
//			logger.debug("closing the connections");
//			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
//		}
//		return null;
//	}
	
	/**
	 * Get the User Forget Password Otp By Email
	 * @param databaseName the database name.
	 * @param email the email whose otp is to be fetched.
	 * @return forgot otp details of the user.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public ForgotPasswordUserOtp getUserForgetPasswordOtpByEmail(String databaseName, String email)
			throws UserDaoException {
		logger.debug(".in getUserOtpByEmail , email is ::" + email + " and database name is " + databaseName);
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.SELECT_USER_BY_EMAIL
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, email);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("user exist");
				ForgotPasswordUserOtp userOtp = buildUserForgetPasswordOtpsFromResultSet(resultSet);
				return userOtp;
			}
		} catch (Exception e) {
			logger.error("unable to get user " + e.getMessage());
			throw new UserDaoException("unable to get user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

//	private UserOtp buildUserForgetPasswordOtpFromResultSet(ResultSet resultSet) throws SQLException {
//		UserOtp userOtp = new UserOtp();
//		userOtp.setUserOtp(resultSet.getString(UserDaoConstant.FORGET_PASSWORD_OTP));
//		userOtp.setExpiryDate(resultSet.getLong(UserDaoConstant.FORGET_PASSWORD_EXPIRY_DATE));
//		return userOtp;
//	}

	/**
	 * Get user by user id.
	 * @param connection used for transaction purposes.
	 * @param databaseName  the database name.
	 * @param userId the userId whose details is to be fetched.
	 * @return the object which is having user details.
	 * @throws UserDaoException if any exception occurs when fetching data.
	 */
	@Override
	public User getUserByUserId(Connection connection, String databaseName, String userId) throws UserDaoException {
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(UserQueryConstants.SELECT_USER_BY_USER_ID
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, userId);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("user exist");
				User user = buldUserFromResultSet(resultSet);
				logger.debug("useir is " + user);
				return user;
			}
		} catch (Exception e) {
			logger.error("unable to get user " + e.getMessage());
			throw new UserDaoException("unable to get user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, null);
		}
		return null;
	}

	/**
	 * Update the user password and the user status by the userId
	 * @param databaseName  the database name.
	 * @param userId the userId whose password is to be updated.
	 * @param password the password of the user.
	 * @param status the status of the user.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateUserPasswordAndStatusByUserId(String databaseName, String userId, String password, String status)
			throws UserDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.UPDATE_PASSWORD_STATUS_BY_USER_ID
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, password);
			prepareStatement.setString(2, status);
			prepareStatement.setString(3, userId);
			int executeUpdate = prepareStatement.executeUpdate();
			if (executeUpdate > 0) {
				logger.debug("password, status updated successfully");
			}
		} catch (Exception e) {
			logger.error("unable to update user " + e.getMessage());
			throw new UserDaoException("unable to update user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * Update the user password and the user status by the userId
	 * @param databaseName  the database name.
	 * @param connection used for transaction purposes.
	 * @param userId the userId whose password is to be updated.
	 * @param password the password of the user.
	 * @param status the status of the user.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateUserPasswordAndStatusByUserId(Connection connection, String databaseName, String userId,
			String password, String status) throws UserDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(UserQueryConstants.UPDATE_PASSWORD_STATUS_BY_USER_ID
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, password);
			prepareStatement.setString(2, status);
			prepareStatement.setString(3, userId);
			int executeUpdate = prepareStatement.executeUpdate();
			if (executeUpdate > 0) {
				logger.debug("password, status updated successfully");
			}
		} catch (Exception e) {
			logger.error("unable to update user " + e.getMessage());
			throw new UserDaoException("unable to update user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * Update the user forget password otp by emailId
	 * @param tenantId the database name.
	 * @param connection used for transaction purposes.
	 * @param emailId email of the user.
	 * @param userOtp Forgot Password User Otp details
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateUserForgetPasswordOtpsByEmail(Connection connection, String databaseName, String emailId,
			ForgotPasswordUserOtp userOtp) throws UserDaoException {
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection
					.prepareStatement(UserQueryConstants.UPDATE_USER_FORGET_PASSWORD_OTPS_BY_USER_EMAIL
							.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, userOtp.getEmailOtp());
			prepareStatement.setString(2, userOtp.getSmsOtp());
			prepareStatement.setLong(3, userOtp.getExpiryTime());
			prepareStatement.setString(4, emailId);
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + "row updated successfully");
		} catch (Exception e) {
			logger.error("unable to update user email, sms otp" + e.getMessage());
			throw new UserDaoException("unable to update user email, sms otp" + e.getMessage());
		} finally {
			logger.debug("closing the user connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}
	}

	/**
	 * Get the User Forget Password Otp By Email
	 * @param databaseName the database name.
	 * @param email the email whose otp is to be fetched.
	 * @return forgot otp details of the user.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public ForgotPasswordUserOtp getUserForgetPasswordOtpByEmail1(String databaseName, String email)
			throws UserDaoException {
		logger.debug(".in getUserOtpByEmail , email is ::" + email + " and database name is " + databaseName);
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection.prepareStatement(UserQueryConstants.SELECT_USER_BY_EMAIL
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, email);
			resultSet = prepareStatement.executeQuery();
			if (resultSet.next()) {
				logger.debug("user exist");
				ForgotPasswordUserOtp userOtp = buildUserForgetPasswordOtpsFromResultSet(resultSet);
				return userOtp;
			}
		} catch (Exception e) {
			logger.error("unable to get user " + e.getMessage());
			throw new UserDaoException("unable to get user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(resultSet, prepareStatement, connection);
		}
		return null;
	}

	private ForgotPasswordUserOtp buildUserForgetPasswordOtpsFromResultSet(ResultSet resultSet) throws SQLException {
		ForgotPasswordUserOtp userOtp = new ForgotPasswordUserOtp();
		userOtp.setEmailOtp(resultSet.getString(UserDaoConstant.FORGET_PWD_EMAIL_OTP));
		userOtp.setSmsOtp(resultSet.getString(UserDaoConstant.FORGET_PWD_SMS_OTP));
		userOtp.setExpiryTime(resultSet.getLong(UserDaoConstant.FORGET_PWD_EXPIRY_DATE));
		return userOtp;
	}

	/**
	 * Update the user forget password otp by emailId
	 * @param tenantId the database name.
	 * @param emailId email of the user.
	 * @param userOtp Forgot Password User Otp details
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	@Override
	public void updateUserForgetPasswordOtpsByEmail(String databaseName, String emailId, ForgotPasswordUserOtp userOtp)
			throws UserDaoException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			prepareStatement = connection
					.prepareStatement(UserQueryConstants.UPDATE_USER_FORGET_PASSWORD_OTPS_BY_USER_EMAIL
							.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			prepareStatement.setString(1, userOtp.getEmailOtp());
			prepareStatement.setString(2, userOtp.getSmsOtp());
			prepareStatement.setLong(3, userOtp.getExpiryTime());
			prepareStatement.setString(4, emailId);
			int executeUpdate = prepareStatement.executeUpdate();
			logger.debug(executeUpdate + "row updated successfully");
		} catch (Exception e) {
			logger.error("unable to update user email, sms otp" + e.getMessage());
			throw new UserDaoException("unable to update user email, sms otp" + e.getMessage());
		} finally {
			logger.debug("closing the user connections");
			MSSqlServerUtill.close(prepareStatement, connection);
		}
	}

	/**
	 * Update the user details and user status
	 * @param tenantId the database name.
	 * @param connection used for transaction purposes.
	 * @param userId the userId whose details is to be updated.
	 * @param user the user details.
	 * @throws UserDaoException 
	 */
	@Override
	public void updateUserDetailsAndStatus(Connection connection, String tenantId, String userId, User user)
			throws UserDaoException {
		logger.debug(".in updateUserDetailsAndStatus :: User is :: " + user);
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(UserQueryConstants.UPDATE_USER_DETAILS_AND_STATUS_BY_ID
					.replace(UserQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			prepareStatement.setString(1, user.getFirstName());
			prepareStatement.setString(2, user.getLastName());
			prepareStatement.setString(3, user.getEmail());
			prepareStatement.setString(4, user.getPhone());
			prepareStatement.setString(5, user.getUserRole());
			prepareStatement.setString(6, user.getCenterId());
			prepareStatement.setString(7, user.getStatus());
			prepareStatement.setString(8, null); // Changing password to NULL.
			prepareStatement.setString(9, userId);
			prepareStatement.executeUpdate();
		} catch (Exception e) {
			logger.error("unable to Update user " + e.getMessage());
			throw new UserDaoException("unable to Update user " + e.getMessage());
		} finally {
			logger.debug("closing the connections");
			MSSqlServerUtill.close(prepareStatement, null);
		}

	}
}
