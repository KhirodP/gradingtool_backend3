package com.pwc.grading.user.verification.dao;

import java.sql.Connection;

import com.pwc.grading.user.verification.dao.exception.UserVerificationDAOException;
import com.pwc.grading.user.verification.model.UserVerification;
/**
 * An interface class which is used to perform all
 * User Verification related database operations.
 *
 */
public interface IUserVerificationDAO {

	/**
	 * Add the user verification detail for the user.
	 * @param databaseName the database name.
	 * @param userVerify details of the user verification.
	 * @throws UserVerificationDAOException if any exception occurs while performing this operation.
	 */
	void addUserVerification(String databaseName,UserVerification userVerify) throws UserVerificationDAOException;
	
	/**
	 * Add the user verification detail for the user.
	 * @param connection if this operation is to be performed in single transaction.
	 * @param databaseName the database name.
	 * @param userVerify  details of the user verification.
	 * @throws UserVerificationDAOException if any exception occurs while performing this operation.
	 */
	void addUserVerification(Connection connection, String databaseName,UserVerification userVerify) throws UserVerificationDAOException;
	
	/**
	 * Update the access token and its expiry for the user-verification.
	 * @param connection if this operation is to be performed in single transaction.
	 * @param databaseName the database name.
	 * @param userVerify  details of the user verification.
	 * @throws UserVerificationDAOException if any exception occurs while performing this operation.
	 */
	void updateAccessTokenAndExpiry(Connection connection, String databaseName,UserVerification userVerify) throws UserVerificationDAOException;
	
	/**
	 * Update the access token and its expiry for the user-verification.
	 * @param databaseName the database name.
	 * @param userVerify  details of the user verification.
	 * @throws UserVerificationDAOException if any exception occurs while performing this operation.
	 */
	void updateAccessTokenAndExpiry(String databaseName,UserVerification userVerify) throws UserVerificationDAOException;
	
	/**
	 * Get the user verification by the userId.
	 * @param databaseName the database name.
	 * @param userId the id of the user.
	 * @return details of the user verification.
	 * @throws UserVerificationDAOException if any exception occurs while performing this operation.
	 */
	UserVerification getUserVerificationByUserId(String databaseName,String userId) throws UserVerificationDAOException;
	
	/**
	 * Get the user verification by the userId.
	 * @param connection if this operation is to be performed in single transaction.
	 * @param databaseName the database name.
	 * @param userId the id of the user.
	 * @return details of the user verification.
	 * @throws UserVerificationDAOException if this operation is to be performed in single transaction.
	 */
	UserVerification getUserVerificationByUserId(Connection connection,String databaseName,String userId) throws UserVerificationDAOException;
	
	/**
	 * Delete the user verification by the userId.
	 * @param connection if this operation is to be performed in single transaction.
	 * @param databaseName the database name.
	 * @param userId the id of the user.
	 * @throws UserVerificationDAOException if this operation is to be performed in single transaction.
	 */
	void deleteUserVerificationByUserId(String databaseName,String userId) throws UserVerificationDAOException;
	
	/**
	 * Delete the user verification by the userId.
	 * @param databaseName the database name.
	 * @param userId the id of the user.
	 * @throws UserVerificationDAOException if this operation is to be performed in single transaction.
	 */
	void deleteUserVerificationByUserId(Connection connection,String databaseName,String userId) throws UserVerificationDAOException;
	
}
