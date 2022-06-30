package com.pwc.grading.user.dao;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.user.dao.exception.UserDaoException;
import com.pwc.grading.user.model.ForgotPasswordUserOtp;
import com.pwc.grading.user.model.User;
import com.pwc.grading.user.model.UserOtp;
/**
 * An interface class which is used to perform all
 * User related database operations.
 *
 */
public interface IUserDao {

	/**
	 * This method is used to add new user to database.
	 * @param databaseName the database name.
	 * @param user the user details
	 * @return id of the user created.
	 * @throws UserDaoException if any exception occurs.
	 */
	public String addUser(String databaseName, User user) throws UserDaoException;

	/**
	 * This method is used to add the users in batch.
	 * @param databaseName the database name.
	 * @param user the user details
	 * @throws UserDaoException if any exception occurs.
	 */
	public void uploadUserExcel(String databaseName, List<User> user) throws UserDaoException;

	/**
	 * Get all the users from the table.
	 * @param databaseName  the database name.
	 * @return the list of users.
	 * @throws UserDaoException if any exception occurs when fetching data.
	 */
	public List<User> getUsers(String databaseName) throws UserDaoException;

	/**
	 * Get the user for the given email-Id.
	 * @param databaseName  the database name.
	 * @param email the email whose details is to be fetched.
	 * @return the object which is having user details.
	 * @throws UserDaoException if any exception occurs when fetching data.
	 */
	public User getUserByEmail(String databaseName, String email) throws UserDaoException;

	/**
	 * Get user by user id. 
	 * @param databaseName  the database name.
	 * @param userId the userId whose details is to be fetched.
	 * @return the object which is having user details.
	 * @throws UserDaoException if any exception occurs when fetching data.
	 */
	public User getUserByUserId(String databaseName, String userId) throws UserDaoException;

	/**
	 * Get user by user id.
	 * @param connection used for transaction purposes.
	 * @param databaseName  the database name.
	 * @param userId the userId whose details is to be fetched.
	 * @return the object which is having user details.
	 * @throws UserDaoException if any exception occurs when fetching data.
	 */
	public User getUserByUserId(Connection connection,String databaseName, String userId) throws UserDaoException;

	/**
	 * Update the user's password by the userId.
	 * @param databaseName  the database name.
	 * @param userId the userId whose password is to be updated.
	 * @param password the password to be updated.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	public void updateUserPasswordByUserId(String databaseName, String userId, String password) throws UserDaoException;

	/**
	 * Update the user's centerId by the userId if the user is CenterInCharge.
	 * @param databaseName  the database name.
	 * @param userId the userId whose password is to be updated.
	 * @param centerId the tcId to be updated.
	 * @throws UserDaoException  if any exception occurs when performing this operation.
	 */
	public void updateCenterIdByUserId(String databaseName, String userId, String centerId) throws UserDaoException;

	/**
	 * Delete the user by userId.
	 * @param connection if transaction is required for this operation.
	 * @param databaseName  the database name.
	 * @param userId the userId to be deleted.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	public void deleteUserById(Connection connection,String databaseName, String userId) throws UserDaoException;

	/**
	 * Update the user's otp by the email.
	 * @param databaseName  the database name.
	 * @param email the email whose otp is to be updated.
	 * @param userOtp otp details of the user.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	public void updateUserOtpByEmail(String databaseName, String email, UserOtp userOtp) throws UserDaoException;

//	public void updateUserForgetPasswordOtpByEmail(String databaseName, String email, UserOtp userOtp)
//			throws UserDaoException;

	/**
	 * Get the user's otp by the email.
	 * @param databaseName the database name.
	 * @param email the email whose otp is to be updated.
	 * @return otp details of the user.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	public UserOtp getUserOtpByEmail(String databaseName, String email) throws UserDaoException;

	/**
	 * Get the User Forget Password Otp By Email
	 * @param databaseName the database name.
	 * @param email the email whose otp is to be fetched.
	 * @return forgot otp details of the user.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	public ForgotPasswordUserOtp getUserForgetPasswordOtpByEmail(String databaseName, String email) throws UserDaoException;

	/**
	 * Update the user status by the userId
	 * @param userId the userId whose status to be updated.
	 * @param tenantId the database name.
	 * @param requestBody status to be updated.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	public void updateUserStatus(String userId, String tenantId, String requestBody) throws UserDaoException;

	/**
	 * Update user status by userId.
	 * @param userId the userId whose status to be updated.
	 * @param tenantId the database name.
	 * @param requestBody the new userDetails
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	public void updateUser(String tenantId, String userId, User requestBody) throws UserDaoException;

	/**
	 * Check the userExist or not.
	 * @param databaseName the database name.
	 * @param email the email which is to be checked
	 * @return true if exists, false if not exists.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	public boolean isUserExist(String databaseName, String email) throws UserDaoException;

	/**
	 * This method is used to add new user to database.
	 * @param databaseName the database name.
	 * @param connection used for transaction purposes.
	 * @param user the user details
	 * @return id of the user created.
	 * @throws UserDaoException if any exception occurs.
	 */
	String addUser(Connection connection, String databaseName, User user) throws UserDaoException;

	/**
	 * Update the user's centerId by the userId if the user is CenterInCharge.
	 * @param databaseName  the database name.
	 * @param connection used for transaction purposes.
	 * @param userId the userId whose password is to be updated.
	 * @param centerId the tcId to be updated.
	 * @throws UserDaoException  if any exception occurs when performing this operation.
	 */
	void updateCenterIdByUserId(Connection connection, String databaseName, String userId, String centerId)
			throws UserDaoException;

	/**
	 * Get the user for the given email-Id.
	 * @param databaseName  the database name.
	 * @param connection used for transaction purposes.
	 * @param email the email whose details is to be fetched.
	 * @return the object which is having user details.
	 * @throws UserDaoException if any exception occurs when fetching data.
	 */
	User getUserByEmail(Connection connection, String databaseName, String email) throws UserDaoException;

	/**
	 * Check the userExist or not.
	 * @param databaseName the database name.
	 * @param connection used for transaction purposes.
	 * @param email the email which is to be checked
	 * @return true if exists, false if not exists.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	boolean isUserExist(Connection connection, String databaseName, String email) throws UserDaoException;

	/**
	 * Update the user password and the user status by the userId
	 * @param databaseName  the database name.
	 * @param userId the userId whose password is to be updated.
	 * @param password the password of the user.
	 * @param status the status of the user.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	void updateUserPasswordAndStatusByUserId(String databaseName, String userId, String password, String status)
			throws UserDaoException;

	/**
	 * Update the user password and the user status by the userId
	 * @param databaseName  the database name.
	 * @param connection used for transaction purposes.
	 * @param userId the userId whose password is to be updated.
	 * @param password the password of the user.
	 * @param status the status of the user.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	void updateUserPasswordAndStatusByUserId(Connection connection, String databaseName, String userId, String password,
			String status) throws UserDaoException;

	/**
	 * Update the user forget password otp by emailId
	 * @param tenantId the database name.
	 * @param emailId email of the user.
	 * @param userOtp Forgot Password User Otp details
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	public void updateUserForgetPasswordOtpsByEmail(String tenantId, String emailId, ForgotPasswordUserOtp userOtp) throws UserDaoException;

	/**
	 * Get the User Forget Password Otp By Email
	 * @param databaseName the database name.
	 * @param email the email whose otp is to be fetched.
	 * @return forgot otp details of the user.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	ForgotPasswordUserOtp getUserForgetPasswordOtpByEmail1(String databaseName, String email) throws UserDaoException;

	/**
	 * Update the user's password by the userId.
	 * @param databaseName  the database name.
	 * @param connection used for transaction purposes.
	 * @param userId the userId whose password is to be updated.
	 * @param password the password to be updated.
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	void updateUserPasswordByUserId(Connection connection, String databaseName, String userId, String password)
			throws UserDaoException;

	/**
	 * Update the user forget password otp by emailId
	 * @param tenantId the database name.
	 * @param connection used for transaction purposes.
	 * @param emailId email of the user.
	 * @param userOtp Forgot Password User Otp details
	 * @throws UserDaoException if any exception occurs when performing this operation.
	 */
	void updateUserForgetPasswordOtpsByEmail(Connection connection, String databaseName, String emailId,
			ForgotPasswordUserOtp userOtp) throws UserDaoException;

	/**
	 * Update the user details and user status
	 * @param tenantId the database name.
	 * @param connection used for transaction purposes.
	 * @param userId the userId whose details is to be updated.
	 * @param user the user details.
	 * @throws UserDaoException 
	 */
	public void updateUserDetailsAndStatus(Connection connection, String tenantId, String userId, User user)
			throws UserDaoException;

}
