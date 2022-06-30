package com.pwc.grading.user.service.registration;

import java.sql.Connection;

import javax.mail.Transport;

import com.pwc.grading.user.service.registration.exception.UserRegistrationServiceException;

import io.micronaut.http.multipart.CompletedFileUpload;
/**
 * An interface class which is used to perform all
 * User registration related operations.
 *
 */
public interface IUserRegistrationService {

	/**
	 * This method is used to create a new user.
	 * When a user is registered, the user will receive Otps in email and sms. the user has to provide
	 * the EmailOtp and SmsOtp which is received at the first time login of this user.
	 * @param user the user details which are to be created.
	 * @param tenantId the database name.
	 * @return the success message if the user is created.
	 * @throws UserRegistrationServiceException  if any exception occurs while performing this operation.
	 */
	public String registerUser(String user, String tenantId) throws UserRegistrationServiceException;

	/**
	 * This method is used to create the multiple user while uploading the excel.
	 * In the excel, only valid users are created. 
	 * @param tenantId  the database name.
	 * @param multipleUsers instance of the excel upload.
	 * @return  the response message regarding the users created and the users not created with its reason.
	 * @throws UserRegistrationServiceException if any exception occurs while performing this operation.
	 */
	public String registerMultipleUsers(String tenantId, CompletedFileUpload multipleUsers)
			throws UserRegistrationServiceException;

	/**
	 * This method is used to update the status of the user.
	 * Note that, the tenant's default user status cannot be updated.
	 * @param userId the userId whose status is to be updated.
	 * @param requestBody containing the status
	 * @param tenantId the database name.
	 * @param tenantKey the tenant key to check for default user.
	 * @return the success message if the user is updated.
	 * @throws UserRegistrationServiceException if any exception occurs while performing this operation.
	 */
	public String updateUserStatus(String userId, String requestBody, String tenantId, String tenantKey)
			throws UserRegistrationServiceException;

	/**
	 * This method is used to update the user.
	 * Note that, the tenant's default user cannot be updated.
	 * @param userId the userId to be updated.
	 * @param requestBody containing the status
	 * @param tenantId the database name.
	 * @param tenantKey the tenant key to check for default user.
	 * @return the success message if the user is updated.
	 * @throws UserRegistrationServiceException if any exception occurs while performing this operation.
	 */
	public String updateUser(String userId, String requestBody, String tenantId, String tenantKey)
			throws UserRegistrationServiceException;

	/**
	 * This method is used to delete the user.
	 * Note that, the tenant's default user cannot be deleted.
	 * @param userId userId whose status is to be deleteed.
	 * @param tenantId the database name.
	 * @param tenantKey the tenant key to check for default user.
	 * @param adminUserEmail the admin's user email.
	 * @return  the success message if the user is deleted.
	 * @throws UserRegistrationServiceException if any exception occurs while performing this operation.
	 */
	public String deleteUserById(String userId, String tenantId, String tenantKey, String adminUserEmail)
			throws UserRegistrationServiceException;

	/**
	 * Get all the users for the particular tenant.
	 * @param tenantId the database name.
	 * @return all the users in the JSON.
	 * @throws UserRegistrationServiceException if any exception occurs while performing this operation.
	 */
	public String getAllUsers(String tenantId) throws UserRegistrationServiceException;

	/**
	 * Filtering the users based on firstName, user-role, and phone.
	 * @param firstName the firstname of the user.
	 * @param role the role of the user.
	 * @param phone the phone number of the user.
	 * @param tenantId the database name.
	 * @return the result of the filters applied in JSON.
	 * @throws UserRegistrationServiceException if any exception occurs while performing this operation.
	 */
	public String filterUsers(String firstName, String role, String phone, String tenantId)
			throws UserRegistrationServiceException;

	/**
	 * This method is used to register the user in a transaction operation.
	 * @param connection the connection provided for transaction purpose.
	 * @param transport to send the email.
	 * @param requestJson the request json.
	 * @param tenantId the database name.
	 * @return the success message if the user is created.
	 * @throws UserRegistrationServiceException if any exception occurs while performing this operation.
	 */
	String registerUser(Connection connection, Transport transport, String requestJson, String tenantId)
			throws UserRegistrationServiceException;

	/**
	 * Get the user by emailId.
	 * @param tenantId the database name.
	 * @param userEmail the user's Email
	 * @return the user. 
	 * @throws UserRegistrationServiceException  if any exception occurs while performing this operation.
	 */
	public String getUserByEmail(String tenantId, String userEmail) throws UserRegistrationServiceException;

	/**
	 * This method is used to delete muliple users.
	 * Note that, tenant's default user cannot be deleted.
	 * @param requestBody the userIds to delete 
	 * @param tenantId the database name.
	 * @param tenantKey tenant key to check for default user.
	 * @param adminUserMail email of default admin
	 * @return the success message if the user is deleted.
	 * @throws UserRegistrationServiceException if any exception occurs while performing this operation.
	 */
	public String deleteMultipleUser(String requestBody, String tenantId, String tenantKey, String adminUserMail)
			throws UserRegistrationServiceException;

}
