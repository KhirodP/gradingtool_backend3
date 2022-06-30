package com.pwc.grading.user.service.registration.impl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerDBConstants;
import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.mail.service.IMailService;
import com.pwc.grading.mail.service.exception.MailServiceException;
import com.pwc.grading.mail.session.MailSessionInstance;
import com.pwc.grading.masterdata.dao.exception.MasterDataManagmentDaoException;
import com.pwc.grading.partner.dao.IPartnerDao;
import com.pwc.grading.partner.dao.exception.PartnerDaoException;
import com.pwc.grading.partner.model.PartnerDetails;
import com.pwc.grading.scheduler.dao.ISchedulerDao;
import com.pwc.grading.scheduler.dao.exception.SchedulerDaoException;
import com.pwc.grading.scheduler.model.GradingType;
import com.pwc.grading.scheduler.model.RatingType;
import com.pwc.grading.sms.service.exception.SmsServiceException;
import com.pwc.grading.tracking.dao.ITrackingDao;
import com.pwc.grading.user.dao.IUserDao;
import com.pwc.grading.user.dao.exception.UserDaoException;
import com.pwc.grading.user.model.User;
import com.pwc.grading.user.service.registration.IUserRegistrationService;
import com.pwc.grading.user.service.registration.UserAccessManagementServiceConstants;
import com.pwc.grading.user.service.registration.exception.UserRegistrationServiceException;
import com.pwc.grading.user.verification.dao.IUserVerificationDAO;
import com.pwc.grading.user.verification.model.UserVerification;
import com.pwc.grading.usertoken.dao.IUserTokenDao;
import com.pwc.grading.util.JsonUtill;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.UserSecurityProtecter;
import com.pwc.grading.util.exception.JsonUtillException;

import io.micronaut.http.multipart.CompletedFileUpload;
/**
 * Implementation class for {@link IUserRegistrationService}
 *
 */
public class UserRegistrationService implements IUserRegistrationService {

	private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);
	private static final String SUCCESS = "success";
	private static final String STATUS = "status";
	private static final String MESSAGE = "msg";

	@Inject
	private IUserDao userDao;

//	@Inject
//	private IMasterDataManagmentDao imasterDao;
//	@Inject
//	private ISmsService smsService;

	@Inject
	private IMailService mailService;
	@Inject
	private ISchedulerDao iSchedulerDao;
	@Inject
	private IUserTokenDao iUserTokenDao;
	@Inject
	private IPartnerDao iPartnerDao;
	@Inject
	private IUserVerificationDAO verificationDAO;
	@Inject
	private ITrackingDao iTrackingDao;

	/**
	 * Get all the users for the particular tenant.
	 * @param tenantId the database name.
	 * @return all the users in the JSON.
	 * @throws UserRegistrationServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getAllUsers(String tenantId) throws UserRegistrationServiceException {
		try {
			List<User> listOfUsers = userDao.getUsers(tenantId);
			JSONObject responseJSON = new JSONObject();
			JSONArray usersJSONData = getJSONFromUsersList(listOfUsers);
			responseJSON.put(UserAccessManagementServiceConstants.USERS, usersJSONData);
			return responseJSON.toString();
		} catch (UserDaoException e) {
			logger.error(" unable get all users  , " + e.getMessage(), e);
			throw new UserRegistrationServiceException(" unable to get all users " + e.getMessage(), e);
		}
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public String registerUser(String requestJson, String tenantId) throws UserRegistrationServiceException {
//		logger.debug(" inside registerUser of user registrtion service .... ");
//		try {
//			JSONObject requestJsonObject = (JSONObject) JSONValue.parse(requestJson);
//			User user = buildUserFromJson(requestJsonObject);
//			applyValidationOnUser(user, tenantId);
//			user.setUserId(UUID.randomUUID().toString());
//			String tempPass = generateTemporaryPassword(user);
//			String userId = userDao.addUser(tenantId, user);
////			sendTempPasswordToUser(user, tempPass);
//			JSONObject reposneJSON = new JSONObject();
//			reposneJSON.put(STATUS, SUCCESS);
//			reposneJSON.put(MESSAGE, ReadPropertiesFile.readResponseProperty("201"));
//			reposneJSON.put(UserAccessManagementServiceConstants.USER_ID, userId);
//			return reposneJSON.toString();
//
//		} catch (Exception e) {
//			logger.error(" unable register user  , " + e.getMessage(), e);
//			throw new UserRegistrationServiceException(" unable to register user " + e.getMessage(), e);
//		}
//	}

	/**
	 * This method is used to create a new user.
	 * When a user is registered, the user will receive Otps in email and sms. the user has to provide
	 * the EmailOtp and SmsOtp which is received at the first time login of this user.
	 * @param user the user details which are to be created.
	 * @param tenantId the database name.
	 * @return the success message if the user is created.
	 * @throws UserRegistrationServiceException  if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String registerUser(String requestJson, String tenantId) throws UserRegistrationServiceException {
		logger.debug(" inside registerUser of user registrtion service .... ");
		Connection connection = null;
		Transport transport = null;
		try {

			JSONObject requestJsonObject = (JSONObject) JSONValue.parse(requestJson);
			User user = buildUserFromJson(requestJsonObject);
			applyValidationOnUser(user, tenantId);
			String userId = UUID.randomUUID().toString();
			user.setUserId(userId);
			user.setStatus(UserAccessManagementServiceConstants.USER_UN_VERIFIED);

			// Generating user Verification Fields.
//			String tempPassword = "userGt";
			String tempPassword = generateRandomChars(UserAccessManagementServiceConstants.TEMP_PWD_LENGTH);
//			String emailOtp = "654321";
			String emailOtp = generateRandomChars(UserAccessManagementServiceConstants.EMAIL_OTP_LENGTH);
			String smsOtp = "654321";
//			String smsOtp = generateRandomChars(UserAccessManagementServiceConstants.SMS_OTP_LENGTH);	
			logger.debug("TempPassword for '" + user.getEmail() + "' : " + tempPassword);
			String encryptedTempPassword = UserSecurityProtecter.protectUserData(tempPassword);
			logger.debug("EncryptedTempPassword: " + encryptedTempPassword);

			UserVerification userVerify = new UserVerification();
			userVerify.setUserId(userId);
			userVerify.setTempPassword(encryptedTempPassword);
			userVerify.setEmailOtp(emailOtp);
			userVerify.setSmsOtp(smsOtp);

			// First email has to send successfully, then only creating user.
			Session session = MailSessionInstance.getMailSession();
			transport = session.getTransport();
			transport.connect();
			sendFirstTimeRegistrationEmailToUser(transport, user, tempPassword, userVerify);
			connection = MSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			userDao.addUser(connection, tenantId, user);
			verificationDAO.addUserVerification(connection, tenantId, userVerify); // Adding to UserVerification Table.
			connection.commit();
			JSONObject reposneJSON = new JSONObject();
			reposneJSON.put(STATUS, SUCCESS);
			reposneJSON.put(MESSAGE, ReadPropertiesFile.readResponseProperty("201"));
			reposneJSON.put(UserAccessManagementServiceConstants.USER_ID, userId);
			return reposneJSON.toString();

		} catch (Exception e) {
			logger.error(" unable register user  , " + e.getMessage(), e);
			throw new UserRegistrationServiceException(" unable to register user " + e.getMessage(), e);
		} finally {
			logger.debug("Closing connection opened for Creating user..");
			MSSqlServerUtill.close(null, connection);
			MailSessionInstance.closeTransport(transport);
		}
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public String registerUser(Connection connection, String requestJson, String tenantId)
//			throws UserRegistrationServiceException {
//		logger.debug(" inside registerUser of user registrtion service .... ");
//		try {
//			JSONObject requestJsonObject = (JSONObject) JSONValue.parse(requestJson);
//			User user = buildUserFromJson(requestJsonObject);
//			applyValidationOnUser(connection, user, tenantId);
//			user.setUserId(UUID.randomUUID().toString());
//			String tempPass = generateTemporaryPassword(user);
//			String userId = userDao.addUser(connection, tenantId, user);
////			sendTempPasswordToUser(user, tempPass);
//			JSONObject reposneJSON = new JSONObject();
//			reposneJSON.put(STATUS, SUCCESS);
//			reposneJSON.put(MESSAGE, ReadPropertiesFile.readResponseProperty("201"));
//			reposneJSON.put(UserAccessManagementServiceConstants.USER_ID, userId);
//			return reposneJSON.toString();
//
//		} catch (Exception e) {
//			logger.error(" unable register user  , " + e.getMessage(), e);
//			throw new UserRegistrationServiceException(" unable to register user " + e.getMessage(), e);
//		}
//	}
	
	/**
	 * This method is used to register the user in a transaction operation.
	 * @param connection the connection provided for transaction purpose.
	 * @param transport to send the email.
	 * @param requestJson the request json.
	 * @param tenantId the database name.
	 * @return the success message if the user is created.
	 * @throws UserRegistrationServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String registerUser(Connection connection, Transport transport, String requestJson, String tenantId)
			throws UserRegistrationServiceException {
		logger.debug(" inside registerUser of user registrtion service .... ");
		try {
			JSONObject requestJsonObject = (JSONObject) JSONValue.parse(requestJson);
			User user = buildUserFromJson(requestJsonObject);
			applyValidationOnUser(connection, user, tenantId);
			String userId = UUID.randomUUID().toString();
			user.setUserId(userId);
			user.setStatus(UserAccessManagementServiceConstants.USER_UN_VERIFIED);

			// Generating user Verification Fields.
//			String tempPassword = "userGt";
			String tempPassword = generateRandomChars(UserAccessManagementServiceConstants.TEMP_PWD_LENGTH);
			logger.debug("TempPassword for '" + user.getEmail() + "' : " + tempPassword);
			String encryptedTempPassword = UserSecurityProtecter.protectUserData(tempPassword);
//			String emailOtp = "654321";
			String emailOtp = generateRandomChars(UserAccessManagementServiceConstants.EMAIL_OTP_LENGTH);
			String smsOtp = "654321";
//			String smsOtp =  generateRandomChars(UserAccessManagementServiceConstants.SMS_OTP_LENGTH);
			UserVerification userVerify = new UserVerification();
			userVerify.setUserId(userId);
			userVerify.setTempPassword(encryptedTempPassword);
			userVerify.setEmailOtp(emailOtp);
			userVerify.setSmsOtp(smsOtp);
			// First email has to send successfully, then only creating user.
			sendFirstTimeRegistrationEmailToUser(transport, user, tempPassword, userVerify);
			userDao.addUser(connection, tenantId, user);
			verificationDAO.addUserVerification(connection, tenantId, userVerify); // Adding to UserVerification Table.
			// sendTempPasswordToUser(user, tempPassword, userVerify);
			JSONObject reposneJSON = new JSONObject();
			reposneJSON.put(STATUS, SUCCESS);
			reposneJSON.put(MESSAGE, ReadPropertiesFile.readResponseProperty("201"));
			reposneJSON.put(UserAccessManagementServiceConstants.USER_ID, userId);
			return reposneJSON.toString();

		} catch (Exception e) {
			logger.error(" unable register user  , " + e.getMessage(), e);
			throw new UserRegistrationServiceException(" unable to register user " + e.getMessage(), e);
		}
	}

	private void applyValidationOnUser(User user, String tenantId)
			throws UserRegistrationServiceException, UserDaoException {
		validateFields(user.getFirstName(), UserAccessManagementServiceConstants.USER_FIRST_NAME,
				UserAccessManagementServiceConstants.FIRST_NAME_LENGTH);
		String lastName = user.getLastName();
		if (!(lastName.isEmpty())) {
			if (lastName.toCharArray().length > UserAccessManagementServiceConstants.LAST_NAME_LENGTH) {
				throw new UserRegistrationServiceException("Last name column or field should be lessthan "
						+ UserAccessManagementServiceConstants.LAST_NAME_LENGTH + " characters ");
			}
		}
		validateFields(user.getEmail(), UserAccessManagementServiceConstants.USER_EMAIL,
				UserAccessManagementServiceConstants.USER_EMAIL_LENGTH);
		boolean userExist = userDao.isUserExist(tenantId, user.getEmail());
		if (userExist) {
			throw new UserRegistrationServiceException(user.getEmail() + " already exist");
		}
		validateEmail(user.getEmail());
		validateFields(user.getPhone(), UserAccessManagementServiceConstants.USER_PHONE, 10);
		validatePhoneNumber(user.getPhone());
		String userRole = user.getUserRole();
		validateFields(userRole, UserAccessManagementServiceConstants.USER_ROLE, 0);
		boolean verifyUserRole = validateUserRole(userRole);
		if (!verifyUserRole) {
			logger.debug(userRole + " user Role does not exist");
			throw new UserRegistrationServiceException(userRole + " user Role does not exist");
		}
	}

	private void applyValidationOnUser(Connection connection, User user, String tenantId)
			throws UserRegistrationServiceException, UserDaoException {
		validateFields(user.getFirstName(), UserAccessManagementServiceConstants.USER_FIRST_NAME,
				UserAccessManagementServiceConstants.FIRST_NAME_LENGTH);
		String lastName = user.getLastName();
		if (!(lastName.isEmpty())) {
			if (lastName.toCharArray().length > UserAccessManagementServiceConstants.LAST_NAME_LENGTH) {
				throw new UserRegistrationServiceException("Last name column or field should be lessthan "
						+ UserAccessManagementServiceConstants.LAST_NAME_LENGTH + " characters ");
			}
		}
		validateFields(user.getEmail(), UserAccessManagementServiceConstants.USER_EMAIL,
				UserAccessManagementServiceConstants.USER_EMAIL_LENGTH);
		validateEmail(user.getEmail());
		validateFields(user.getPhone(), UserAccessManagementServiceConstants.USER_PHONE, 10);
		validatePhoneNumber(user.getPhone());
		String userRole = user.getUserRole();
		validateFields(userRole, UserAccessManagementServiceConstants.USER_ROLE, 0);
		boolean verifyUserRole = validateUserRole(userRole);
		if (!verifyUserRole) {
			logger.debug(userRole + " user Role does not exist");
			throw new UserRegistrationServiceException(userRole + " user Role does not exist");
		}
	}

	/**
	 * This method is used to create the multiple user while uploading the excel.
	 * In the excel, only valid users are created. 
	 * @param tenantId  the database name.
	 * @param multipleUsers instance of the excel upload.
	 * @return  the response message regarding the users created and the users not created with its reason.
	 * @throws UserRegistrationServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String registerMultipleUsers(String tenantId, CompletedFileUpload multipleUsers)
			throws UserRegistrationServiceException {
		logger.debug("inside registerMultipleUsers of UserRegistrationService class.");
		JSONArray failureJSONArray = new JSONArray();
		Transport transport = null;
		try {
			if (multipleUsers == null) {
				throw new UserRegistrationServiceException("User Management data cant be null or empty");
			}

			InputStream inputStream = multipleUsers.getInputStream();
			List<User> userList = getListOfUsersFromExcelSheet(tenantId, inputStream, failureJSONArray);
			logger.debug("Number of users in the excel: " + userList.size());
			validateDuplicateEntries(userList);
			int i = 1;
			transport = MailSessionInstance.getMailSession().getTransport();
			transport.connect();
			for (User user : userList) {
				if (user != null) {
					String userId = UUID.randomUUID().toString();
					user.setUserId(userId);
					user.setStatus(UserAccessManagementServiceConstants.USER_UN_VERIFIED);
					// Generating user Verification Fields.
					String tempPassword = generateRandomChars(
							UserAccessManagementServiceConstants.TEMP_PWD_LENGTH);
//					String tempPassword = "userGt";
					logger.debug("TempPassword for '" + user.getEmail() + "' : " + tempPassword);
					String encryptedTempPassword = UserSecurityProtecter.protectUserData(tempPassword);
					String emailOtp = generateRandomChars(UserAccessManagementServiceConstants.EMAIL_OTP_LENGTH);
//					String smsOtp = generateRandomChars(UserAccessManagementServiceConstants.SMS_OTP_LENGTH);
//					String emailOtp = "654321";
					String smsOtp = "654321";
					UserVerification userVerify = new UserVerification();
					userVerify.setUserId(userId);
					userVerify.setTempPassword(encryptedTempPassword);
					userVerify.setEmailOtp(emailOtp);
					userVerify.setSmsOtp(smsOtp);
					Connection connection = null;
					try {
						sendFirstTimeRegistrationEmailToUser(transport, user, tempPassword, userVerify);
						connection = MSSqlServerUtill.getConnection();
						connection.setAutoCommit(false);
						userDao.addUser(connection, tenantId, user);
						// Adding to UserVerification Table.
						verificationDAO.addUserVerification(connection, tenantId, userVerify);
						connection.commit();

					} catch (UserDaoException | MailServiceException e) {
						if (connection != null) {
							connection.rollback();
						}
						JSONObject failureJsonObj = new JSONObject();
						failureJsonObj.put("rowNumber", i + 1);
						failureJsonObj.put("firstName", user.getFirstName());
						failureJsonObj.put("lastName", user.getLastName());
						failureJsonObj.put("email", user.getEmail());
						failureJsonObj.put("phoneNumber", user.getPhone());
						failureJsonObj.put("userRole", user.getUserRole());
						failureJsonObj.put("errorMsg", e.getMessage().toString());
						failureJSONArray.add(failureJsonObj);
					} finally {
						MSSqlServerUtill.close(null, connection);
					}
				}
				i++; // Will indicate row number.
			}
			JSONObject reposneJSON = new JSONObject();
			reposneJSON.put(MESSAGE, ReadPropertiesFile.readResponseProperty("615"));
			reposneJSON.put("failedRecords", failureJSONArray);
			reposneJSON.put("failedRecordCount", failureJSONArray.size());
			reposneJSON.put("totalRecordsProcessed", userList.size() - failureJSONArray.size());
			return reposneJSON.toString();
		} catch (Exception e) {
			logger.error("Unable to register multiple users  , " + e.getMessage(), e);
			throw new UserRegistrationServiceException(e.getMessage());
		} finally {
			MailSessionInstance.closeTransport(transport);
		}
	}

	private String generateRandomChars(int n) {
		String uuid = UUID.randomUUID().toString();
		String replacedUUID = uuid.replaceAll("-", "");
		String random = replacedUUID.substring(0, n);
		return random;
	}

	private void validateDuplicateEntries(List<User> userList) throws UserRegistrationServiceException {
//		Map<Integer, Long> elementCountMap = numbersList.stream()
//				.collect(Collectors.toMap(Function.identity(), v -> 1L, Long::sum));
		List<String> phoneList = new ArrayList<String>();
		List<String> mailList = new ArrayList<String>();
		for (User user : userList) {
			if (user != null) {
				mailList.add(user.getEmail());
				phoneList.add(user.getPhone());
			}
		}
		Map<String, Long> phoneMap = phoneList.stream()
				.collect(Collectors.toMap(Function.identity(), v -> 1L, Long::sum));
		logger.debug("phoneNumber map " + phoneMap);
		Set<Entry<String, Long>> entrySet = phoneMap.entrySet();
		for (Entry<String, Long> singleMap : entrySet) {
			if (singleMap.getValue() > 1) {
				throw new UserRegistrationServiceException(
						singleMap.getKey() + " already exist" + " found " + singleMap.getValue() + " times");
			}
		}
		Map<String, Long> mailMap = mailList.stream()
				.collect(Collectors.toMap(Function.identity(), v -> 1L, Long::sum));
		logger.debug("email map " + mailMap);
		Set<Entry<String, Long>> emailEntrySet = mailMap.entrySet();
		for (Entry<String, Long> singleMap : emailEntrySet) {
			if (singleMap.getValue() > 1) {
				throw new UserRegistrationServiceException(
						singleMap.getKey() + " already exist" + " found " + singleMap.getValue() + " times");
			}
		}
	}

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
	@SuppressWarnings("unchecked")
	@Override
	public String updateUserStatus(String userId, String requestBody, String tenantId, String tenantKey)
			throws UserRegistrationServiceException {
		logger.debug(".in updateUserStatus ");
		try {
			User user = userDao.getUserByUserId(tenantId, userId);
			if (user == null) {
				throw new UserRegistrationServiceException("invalid userId");
			}
			boolean isDefaultUser = checkWhetherUserIsDefault(tenantKey, user);
			String status = user.getStatus();
			if (status.equalsIgnoreCase(UserAccessManagementServiceConstants.USER_UN_VERIFIED)) {
				throw new UserRegistrationServiceException("cannot update the un-verified user");
			}
			if (isDefaultUser && status.equalsIgnoreCase(UserAccessManagementServiceConstants.USER_ACTIVE)) {
				throw new UserRegistrationServiceException("cannot update status of Default user");
			}
			JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(requestBody);
			String reqStatus = JsonUtill.getString(jsonObject, "status");
			userDao.updateUserStatus(userId, tenantId, reqStatus);
			JSONObject reposneJSON = new JSONObject();
			reposneJSON.put(STATUS, SUCCESS);
			reposneJSON.put(MESSAGE, ReadPropertiesFile.readResponseProperty("202"));
			return reposneJSON.toString();
		} catch (Exception e) {
			logger.error(" unable update user status  , " + e.getMessage(), e);
			throw new UserRegistrationServiceException(" unable to update user status ::  " + e.getMessage(), e);
		}
	}

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
	@SuppressWarnings("unchecked")
	@Override
	public String updateUser(String userId, String requestBody, String tenantId, String tenantKey)
			throws UserRegistrationServiceException {
		logger.debug(".in updateUser.. ");
		try {
			if (userId == null || userId.isEmpty()) {
				throw new UserRegistrationServiceException("user id can't be null or empty");
			}
			JSONObject requestJsonObject = (JSONObject) JSONValue.parse(requestBody);
			User user = buildUserFromJson(requestJsonObject);
			checkConditions(tenantId, userId, user, tenantKey);
			validateFields(user.getFirstName(), UserAccessManagementServiceConstants.USER_FIRST_NAME,
					UserAccessManagementServiceConstants.FIRST_NAME_LENGTH);
			String lastName = user.getLastName();
			if (!(lastName.isEmpty()) || lastName != null) {
				if (lastName.toCharArray().length > UserAccessManagementServiceConstants.LAST_NAME_LENGTH) {
					throw new UserRegistrationServiceException("Last name column or field should be lessthan "
							+ UserAccessManagementServiceConstants.LAST_NAME_LENGTH + " characters ");
				}
			}
			validateFields(user.getEmail(), UserAccessManagementServiceConstants.USER_EMAIL,
					UserAccessManagementServiceConstants.USER_EMAIL_LENGTH);
			validateEmail(user.getEmail());
			validateFields(user.getPhone(), UserAccessManagementServiceConstants.USER_PHONE, 10);
			validatePhoneNumber(user.getPhone());

			String userRole = user.getUserRole();
			boolean verifyUserRole = validateUserRole(userRole);
			if (!verifyUserRole) {
				logger.debug(userRole + " user Role does not exist");
				throw new UserRegistrationServiceException(userRole + " user Role does not exist");
			}
			boolean notChanged = checkEmailAndPhoneIsChanged(tenantId, userId, user);
			if (notChanged) {
				userDao.updateUser(tenantId, userId.trim(), user);
			}
			JSONObject reposneJSON = new JSONObject();
			reposneJSON.put(STATUS, SUCCESS);
			reposneJSON.put(MESSAGE, ReadPropertiesFile.readResponseProperty("202"));
			return reposneJSON.toString();
		} catch (Exception e) {
			logger.error(" unable update user by id , " + e.getMessage(), e);
			throw new UserRegistrationServiceException(" unable to update user by id " + e.getMessage(), e);
		}
	}

	private boolean checkEmailAndPhoneIsChanged(String tenantId, String userId, User user)
			throws UserDaoException, UserRegistrationServiceException, SQLException {
		logger.debug(".in checkEmailAndPhoneIsChanged.. ");
		User oldUser = userDao.getUserByUserId(tenantId, userId);
		if (oldUser == null) {
			throw new UserRegistrationServiceException("User does not exist.");
		}
		String oldEmail = oldUser.getEmail();
		String oldPhone = oldUser.getPhone();
		logger.debug("oldEmail: " + oldEmail + ", oldPhone: " + oldPhone);
		logger.debug("NewEmail: " + user.getEmail() + ", NewPhone: " + user.getPhone());

		if (oldEmail.equals(user.getEmail()) && oldPhone.equals(user.getPhone())) {
			logger.debug("User's Email and phone are NOT Changed.");
			return true;
		} else {
			logger.debug("User's Email and phone are changed.");
			user.setStatus(UserAccessManagementServiceConstants.USER_UN_VERIFIED);

			// Generating user Verification Fields.
//			String tempPassword = "userGt";
			String tempPassword = generateRandomChars(UserAccessManagementServiceConstants.TEMP_PWD_LENGTH);
//			String emailOtp = "654321";
			String emailOtp = generateRandomChars(UserAccessManagementServiceConstants.EMAIL_OTP_LENGTH);
			String smsOtp = "654321";
//			String smsOtp = generateRandomChars(UserAccessManagementServiceConstants.SMS_OTP_LENGTH);	
			logger.debug("TempPassword for '" + user.getEmail() + "' : " + tempPassword);
			String encryptedTempPassword = UserSecurityProtecter.protectUserData(tempPassword);
			logger.debug("EncryptedTempPassword: " + encryptedTempPassword);

			UserVerification userVerify = new UserVerification();
			userVerify.setUserId(userId);
			userVerify.setTempPassword(encryptedTempPassword);
			userVerify.setEmailOtp(emailOtp);
			userVerify.setSmsOtp(smsOtp);

			Connection connection = null;
			Transport transport = null;
			try {
				transport = MailSessionInstance.getMailSession().getTransport();
				transport.connect();
				sendFirstTimeRegistrationEmailToUser(transport, user, tempPassword, userVerify);
				logger.debug("user updation mail is sent.");
				connection = MSSqlServerUtill.getConnection();
				userDao.updateUserDetailsAndStatus(connection, tenantId, userId, user);
				logger.debug("user details updated.");
				verificationDAO.addUserVerification(connection, tenantId, userVerify);
				logger.debug("Verification details added.");
				iUserTokenDao.deleteUserTokenByUserId(connection, userId, tenantId);
				logger.debug("User Token deleted.");
			} catch (Exception e) {
				if (connection != null) {
					connection.rollback();
				}
				throw new UserRegistrationServiceException(e);
			} finally {
				logger.debug("Connection closed.");
				MSSqlServerUtill.close(null, connection);
				MailSessionInstance.closeTransport(transport);
			}
			return false;
		}
	}

	private void checkConditions(String tenantId, String userId, User requestUser, String tenantKey)
			throws UserRegistrationServiceException, UserDaoException, MasterDataManagmentDaoException,
			SchedulerDaoException, PartnerDaoException {
		User user = userDao.getUserByUserId(tenantId, userId);
		boolean checkWhetherUserIsDefault = checkWhetherUserIsDefault(tenantKey, user);
		if (checkWhetherUserIsDefault) {
			throw new UserRegistrationServiceException("cant update Default user");
		}
		requestUser.setCenterId(user.getCenterId());
		logger.debug("actual user is " + user);
		logger.debug("requested user is " + requestUser);
		if (!requestUser.getUserRole().equalsIgnoreCase(user.getUserRole())) {
			logger.debug("actual user role and requested user does not matches , so checking mappings");
			if (user.getUserRole().equalsIgnoreCase(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
				logger.debug("user is an center inchage");
				String centerId = user.getCenterId();
				if (centerId != null) {
					logger.debug("center id is not null " + centerId + " length is " + centerId.length());
					if (user.getCenterId().length() >= 1) {
						throw new UserRegistrationServiceException(
								" unable to update because he is center inchange to tcId: " + user.getCenterId());
					}
				}

			} else if (user.getUserRole().equalsIgnoreCase(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
				logger.debug("user is an field auditor");
				// if user is field auditor he should not have any mapping field audits
				List<GradingType> listOfGrading = iSchedulerDao.getAllGradingTypeDataByFieldAuditorId(tenantId, userId);
				for (GradingType gt : listOfGrading) {
					boolean auditStatus = gt.isAuditStatus();
					logger.debug("audit status is " + auditStatus);
					if (!auditStatus) {
						throw new UserRegistrationServiceException("cant update the user, he/she mapped with partner: "
								+ gt.getPartnerId() + " and auditDate is on " + gt.getAuditDate());
					}
				}
				List<RatingType> listOfRating = iSchedulerDao.getAllRatingTypeDataByFieldAuditorId(tenantId, userId);
				for (RatingType rt : listOfRating) {
					boolean auditStatus = rt.isAuditStatus();
					logger.debug("audit status is " + auditStatus);
					if (!auditStatus) {
						throw new UserRegistrationServiceException("cant update the user, he/she mapped with partner: "
								+ rt.getPartnerId() + " and auditDate is on " + rt.getAuditDate());
					}
				}
			} else if (user.getUserRole().equalsIgnoreCase(UserAccessManagementServiceConstants.CLIENT_SPONSOR)) {
				PartnerDetails partnerDetails = iPartnerDao.getPartnerByClientSponsorId(tenantId, userId);
				if (partnerDetails != null) {
					throw new UserRegistrationServiceException(
							"user is mapped with partner: " + partnerDetails.getPartnerName());
				}
			}
		}
	}

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
	@SuppressWarnings("unchecked")
	@Override
	public String deleteUserById(String userId, String tenantId, String tenantKey, String adminUserEmail)
			throws UserRegistrationServiceException {
		logger.debug(".in deleteUserById.. userId is " + userId);
		Connection connection = null;
		try {
			if (userId == null || userId.isEmpty()) {
				throw new UserRegistrationServiceException("user id can't be null or empty");
			}
			User user = userDao.getUserByUserId(tenantId, userId);
			if (user == null) {
				throw new UserRegistrationServiceException("invalid userId");
			}
			checkingUserPrivileges(tenantKey, adminUserEmail, tenantId, user);
			checkingUserMappings(tenantId, user, tenantKey);
			logger.debug("validation completed user id is" + userId);

//			iUserTokenDao.deleteUserTokenByUserId(userId, tenantId);
//			verificationDAO.deleteUserVerificationByUserId(tenantId, userId);
//			userDao.deleteUserById(tenantId, userId);
			connection = MSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			iTrackingDao.deleteLogTrackingByUserId(connection, tenantId, userId);
			iUserTokenDao.deleteUserTokenByUserId(connection, userId, tenantId);
			verificationDAO.deleteUserVerificationByUserId(connection, tenantId, userId);
			userDao.deleteUserById(connection, tenantId, userId);
			connection.commit();
			JSONObject reposneJSON = new JSONObject();
			reposneJSON.put(STATUS, SUCCESS);
			reposneJSON.put(MESSAGE, ReadPropertiesFile.readResponseProperty("224"));
			return reposneJSON.toString();
		} catch (Exception e) {
			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			logger.error(" unable delete User By Id   , " + e.getMessage(), e);
			throw new UserRegistrationServiceException(" unable to delete user by id " + e.getMessage(), e);
		} finally {
			MSSqlServerUtill.close(null, connection);
		}
	}

	private void checkingUserPrivileges(String tenantKey, String adminUserEmail, String tenantId, User userToDel)
			throws UserDaoException, UserRegistrationServiceException {
		// user should not be admin
		if (userToDel.getUserRole().equalsIgnoreCase(UserAccessManagementServiceConstants.ADMIN)) {
			// if userToDel is Admin then check the role of to user who is deleting
			boolean isUserDefault = checkWhetherUserIsDefault(tenantKey, userToDel);
			if (isUserDefault) {
				throw new UserRegistrationServiceException("Default admin account cant be deleted");
			}
			User user = userDao.getUserByEmail(tenantId, adminUserEmail);
			boolean isDefaultUser = checkWhetherUserIsDefault(tenantKey, user);
			logger.debug("current user is Default user: ? " + isDefaultUser);
			if (!isDefaultUser) {
				logger.error("admin account cant be deleted");
				throw new UserRegistrationServiceException("admin account cant be deleted by normal Admin ");
			}

		}
	}

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
	@SuppressWarnings("unchecked")
	@Override
	public String deleteMultipleUser(String requestBody, String tenantId, String tenantKey, String adminUserEmail)
			throws UserRegistrationServiceException {
		JSONArray success = new JSONArray();
		JSONArray failure = new JSONArray();
		logger.debug(".in deleteMultipleUser.. requestBody is " + requestBody);
		try {
			JSONObject requestJson = (JSONObject) JSONValue.parseWithException(requestBody);
			JSONArray userIdsArray = JsonUtill.getJsonArray(requestJson, UserAccessManagementServiceConstants.USER_IDS);
			Object[] userIds = userIdsArray.toArray();
			for (int i = 0; i < userIds.length; i++) {
				String userId = userIds[i].toString();
				Connection connection = null;
				try {
					User user = userDao.getUserByUserId(tenantId, userId);
					if (user == null) {
						throw new UserRegistrationServiceException("invalid userId");
					}
					checkingUserPrivileges(tenantKey, adminUserEmail, tenantId, user);
					checkingUserMappings(tenantId, user, tenantKey);
					logger.debug("validation completed user id is" + userId);
					connection = MSSqlServerUtill.getConnection();
					connection.setAutoCommit(false);
					iTrackingDao.deleteLogTrackingByUserId(connection, tenantId, userId);
					iUserTokenDao.deleteUserTokenByUserId(connection, userId, tenantId);
					verificationDAO.deleteUserVerificationByUserId(connection, tenantId, userId);
					userDao.deleteUserById(connection, tenantId, userId);
					connection.commit();
					JSONObject reposneJSON = new JSONObject();
					reposneJSON.put(UserAccessManagementServiceConstants.USER_ID, userId);
					reposneJSON.put(STATUS, SUCCESS);
					success.add(reposneJSON);

				} catch (Exception e) {
					JSONObject reposneJSON = new JSONObject();
					reposneJSON.put(UserAccessManagementServiceConstants.USER_ID, userId);
					reposneJSON.put(STATUS, "failure");
					reposneJSON.put(MESSAGE, e.getMessage());
					failure.add(reposneJSON);
				} finally {
					MSSqlServerUtill.close(null, connection);
				}

			}

			JSONObject reposneJSON = new JSONObject();
			reposneJSON.put(MESSAGE, "users deleted successfully");
			reposneJSON.put("success", success);
			reposneJSON.put("failure", failure);
			return reposneJSON.toString();
		} catch (Exception e) {
			logger.error(" unable delete User By Id   , " + e.getMessage(), e);
			throw new UserRegistrationServiceException(" unable to delete user by id " + e.getMessage(), e);
		}

	}

	private void checkingUserMappings(String tenantId, User user, String tenantKey)
			throws UserRegistrationServiceException, MasterDataManagmentDaoException, ParseException,
			SchedulerDaoException, PartnerDaoException {
		// user should not contain any mapping with training centers
		if (user.getUserRole().equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
			logger.debug("curret user is center in charge");
			if (user.getCenterId() != null) {
				if (user.getCenterId().length() >= 1) {
					throw new UserRegistrationServiceException("user is mapped with tcId: " + user.getCenterId());
				}
			}

		} else if (user.getUserRole().equalsIgnoreCase(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
			// if user is field auditor he should not have any mapping field audits
			logger.debug("curret user is FieldAuditor");
			String fieldAuditorId = user.getUserId();
			List<GradingType> listOfGradingData = iSchedulerDao.getAllGradingTypeDataByFieldAuditorId(tenantId,
					fieldAuditorId);
			List<RatingType> listOfRatingData = iSchedulerDao.getAllRatingTypeDataByFieldAuditorId(tenantId,
					fieldAuditorId);
			for (RatingType rtd : listOfRatingData) {
//				rtd.getAuditDate();
				boolean auditStatus = rtd.isAuditStatus();
//				boolean auditCancled = rtd.isAuditCancled();
				if (!auditStatus) {
					throw new UserRegistrationServiceException("user is mapped with partner: " + rtd.getPartnerId()
							+ " and auditDate is on " + rtd.getAuditDate());
				}
			}
			for (GradingType gtd : listOfGradingData) {
//				gtd.getAuditDate();
				boolean auditStatus = gtd.isAuditStatus();
//				boolean auditCancled = gtd.isAuditCancled();
				if (!auditStatus) {
					throw new UserRegistrationServiceException("user is mapped with partner: " + gtd.getPartnerId()
							+ " and auditDate is on " + gtd.getAuditDate());
				}
			}
		} else if (user.getUserRole().equals(UserAccessManagementServiceConstants.CLIENT_SPONSOR)) {
			PartnerDetails partnerDetails = iPartnerDao.getPartnerByClientSponsorId(tenantId, user.getUserId());
			if (partnerDetails != null) {
				throw new UserRegistrationServiceException(
						"user is mapped with partner: " + partnerDetails.getPartnerName());
			}
		}

	}

//	private boolean isAuditComplete(String auditDate) throws ParseException {
//		Date current = new Date();
//		SimpleDateFormat df = new SimpleDateFormat(UserAccessManagementServiceConstants.DATA_FORMAT);
//		Date givenDate = df.parse(auditDate);
//		Long givenDateLong = givenDate.getTime();
//		// create date object
//		Date next = new Date(givenDateLong);
//		// compare both dates
//		if (next.after(current) || (next.equals(current))) {
//			logger.debug("The date is future day");
//			return false;
//		} else {
//			logger.debug("The date is older than current day");
//			return true;
//		}
//
//	}

	private boolean checkWhetherUserIsDefault(String tenantKey, User user) {
		String defaultUserEmailProperty = tenantKey + MSSqlServerDBConstants.USER_ADMIN_EMAIL;
		String defaultUserMail = ReadPropertiesFile.getAdminUserForTenant(defaultUserEmailProperty);
		String defaultUserPhoneProperty = tenantKey + MSSqlServerDBConstants.USER_ADMIN_PHONE;
		String defaultUserPhone = ReadPropertiesFile.getAdminUserForTenant(defaultUserPhoneProperty);
		String email = user.getEmail();
		String phone = user.getPhone();
		if (defaultUserMail.equals(email) && defaultUserPhone.equals(phone)) {
			logger.debug("user is a default user");
			return true;
		}
		logger.debug("user is not a default user");
		return false;
	}

	/**
	 * Filtering the users based on firstName, user-role, and phone.
	 * @param firstName the firstname of the user.
	 * @param role the role of the user.
	 * @param phone the phone number of the user.
	 * @param tenantId the database name.
	 * @return the result of the filters applied in JSON.
	 * @throws UserRegistrationServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String filterUsers(String firstName, String role, String phone, String tenantId)
			throws UserRegistrationServiceException {
		try {
			validateFields(firstName, UserAccessManagementServiceConstants.USER_FIRST_NAME);
			validateFields(role, UserAccessManagementServiceConstants.USER_ROLE);
			validateFields(phone, UserAccessManagementServiceConstants.USER_PHONE);
			List<User> filterdUserList = null;
			List<User> listOfUsers = userDao.getUsers(tenantId);
			logger.debug("list of users " + listOfUsers);
			if (!firstName.isEmpty() && !role.isEmpty() && !phone.isEmpty()) {
				filterdUserList = listOfUsers.stream().filter(user -> {
					logger.debug(StringEscapeUtils.escapeJava(user.getUserRole()));
					return user.getUserRole().toLowerCase().startsWith(role.trim().toLowerCase());
				}).filter(user -> {
					logger.debug(StringEscapeUtils.escapeJava(user.getFirstName()));
					return user.getFirstName().toLowerCase().startsWith(firstName.trim().toLowerCase());
				}).filter(user -> {
					logger.debug(StringEscapeUtils.escapeJava(user.getPhone()));
					return user.getPhone().contains(phone.trim());
				}).collect(Collectors.toList());
			} else if (!firstName.isEmpty() && !role.isEmpty()) {
				filterdUserList = listOfUsers.stream()
						.filter(user -> user.getUserRole().toLowerCase().startsWith(role.trim().toLowerCase()))
						.filter(user -> user.getFirstName().toLowerCase().startsWith(firstName.trim().toLowerCase()))
						.collect(Collectors.toList());
			} else if (!firstName.isEmpty() && !phone.isEmpty()) {
				filterdUserList = listOfUsers.stream()
						.filter(user -> user.getFirstName().toLowerCase().startsWith(firstName.trim().toLowerCase()))
						.filter(user -> user.getPhone().contains(phone.trim())).collect(Collectors.toList());
			} else if (!role.isEmpty() && !phone.isEmpty()) {
				filterdUserList = listOfUsers.stream()
						.filter(user -> user.getUserRole().toLowerCase().startsWith(role.trim().toLowerCase()))
						.filter(user -> user.getPhone().contains(phone.trim())).collect(Collectors.toList());
			} else if (!role.isEmpty()) {
				filterdUserList = listOfUsers.stream()
						.filter(user -> user.getUserRole().toLowerCase().startsWith(role.trim().toLowerCase()))
						.collect(Collectors.toList());
			} else if (!phone.isEmpty()) {
				filterdUserList = listOfUsers.stream().filter(user -> user.getPhone().contains(phone.trim()))
						.collect(Collectors.toList());
			} else if (!firstName.isEmpty()) {
				filterdUserList = listOfUsers.stream()
						.filter(user -> user.getFirstName().toLowerCase().startsWith(firstName.trim().toLowerCase()))
						.collect(Collectors.toList());
			}
			logger.debug("filtered list is  ::" + filterdUserList.toString());
			JSONObject responseJSON = new JSONObject();
			JSONArray usersJSONData = getJSONFromUsersList(filterdUserList);
			responseJSON.put(UserAccessManagementServiceConstants.USERS, usersJSONData);
			return responseJSON.toString();
		} catch (Exception e) {
			logger.error(" unable filter all users  , " + e.getMessage(), e);
			throw new UserRegistrationServiceException(" unable to filter all users " + e.getMessage(), e);
		}
	}

	private void sendFirstTimeRegistrationEmailToUser(Transport transport, User user, String tempPass,
			UserVerification userVerification) throws MailServiceException, SmsServiceException {
		logger.debug("Sending Credentials to user...");
		logger.debug("User: " + user);
		logger.debug("UserVerification : " + userVerification);
		logger.debug("sending temp password to user ::");
		mailService.sendFirstTimeRegistrationMailToUser(transport, user.getEmail(), user.getFirstName(), tempPass,
				userVerification.getEmailOtp());
//			smsService.sendFirstTimeSmsToUser(user, userVerification.getSmsOtp());

	}

//	private String generateTemporaryPassword(User user) {
//		String tempPassword = "userGt";
////		String tempPassword = String.valueOf(UUID.randomUUID()).substring(0, 8);
//		String enPassword = UserSecurityProtecer.protectUserData(tempPassword);
//		logger.debug("encrypted password is " + enPassword);
//		user.setPassword(enPassword);
//		user.setStatus(UserAccessManagementServiceConstants.USER_UN_VERIFIED);
//		return tempPassword;
//	}

//	private List<User> getListOfUsersFromExcelSheet(String tenantId, InputStream inputStream)
//			throws IOException, UserRegistrationServiceException, UserDaoException {
//		List<User> userList = new ArrayList<User>();
//		XSSFWorkbook workBook = new XSSFWorkbook(inputStream);
//		Iterator<Sheet> iteratorSheets = workBook.iterator();
//		while (iteratorSheets.hasNext()) {
//			Sheet singleSheet = iteratorSheets.next();
//			int rowsCount = singleSheet.getLastRowNum();
//			logger.debug("row count " + rowsCount);
//			for (int i = 1; i <= rowsCount; i++) {
//				Row singleRow = singleSheet.getRow(i);
//				User user = getUserFromRow(tenantId, singleRow);
//				userList.add(user);
//			}
//		}
//		workBook.close();
//		return userList;
//	}
	private List<User> getListOfUsersFromExcelSheet(String tenantId, InputStream inputStream, JSONArray failureArray)
			throws IOException, UserRegistrationServiceException, UserDaoException {
		List<User> userList = new ArrayList<User>();
		XSSFWorkbook workBook = null;
		try {
			workBook = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = workBook.getSheetAt(0);
			// Iterator<Sheet> iteratorSheets = workBook.iterator();
//			while (iteratorSheets.hasNext()) {
//				Sheet singleSheet = iteratorSheets.next();
			int rowsCount = sheet.getLastRowNum();
			logger.debug("row count " + rowsCount);
			String maxRowString = ReadPropertiesFile
					.readRequestProperty(UserAccessManagementServiceConstants.UPLOAD_MAX_ROWS);
			int maxRows = Integer.parseInt(maxRowString);
			if (maxRows < rowsCount) {
				throw new UserRegistrationServiceException("rows exceed, only " + maxRows + " row's allowed");
			}
			for (int i = 1; i <= rowsCount; i++) {
				Row singleRow = sheet.getRow(i);
				if (isExcelRowBlank(singleRow)) {
					logger.debug("Current row is blank, row NO: is  " + (i));
					break;
				}
				User user = getUserFromRow(tenantId, singleRow, failureArray, i);
				userList.add(user);
			}
			return userList;
//			}
		} finally {
			if (workBook != null) {
				workBook.close();
			}
		}
	}

	private boolean isExcelRowBlank(Row singleRow) {
		if (singleRow != null) {
			Cell cell0 = singleRow.getCell(0);
			Cell cell1 = singleRow.getCell(1);
			Cell cell2 = singleRow.getCell(2);
			Cell cell3 = singleRow.getCell(3);
			Cell cell4 = singleRow.getCell(4);

			if ((cell0 == null || cell0.getCellType() == CellType.BLANK)
					&& (cell1 == null || cell1.getCellType() == CellType.BLANK)
					&& (cell2 == null || cell2.getCellType() == CellType.BLANK)
					&& (cell3 == null || cell3.getCellType() == CellType.BLANK)
					&& (cell4 == null || cell4.getCellType() == CellType.BLANK)) {
				return true;
			}
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private User getUserFromRow(String tenantId, Row singleRow, JSONArray failureArray, int rowNumber)
			throws UserDaoException, UserRegistrationServiceException {
		String firstName = null;
		String lastName = null;
		String email = null;
		String phoneNumber = null;
		String userRoleCode = null;
		String userRole = null;
		try {
			DataFormatter formatter = new DataFormatter();
			firstName = formatter.formatCellValue(singleRow.getCell(0)).trim();
			lastName = formatter.formatCellValue(singleRow.getCell(1)).trim();
			email = formatter.formatCellValue(singleRow.getCell(2)).trim();
			phoneNumber = formatter.formatCellValue(singleRow.getCell(3)).trim();
			userRoleCode = formatter.formatCellValue(singleRow.getCell(4)).trim();
			// validations

			validateFields(firstName, UserAccessManagementServiceConstants.USER_FIRST_NAME,
					UserAccessManagementServiceConstants.FIRST_NAME_LENGTH);
			if (lastName != null) {
				if (lastName.toCharArray().length > UserAccessManagementServiceConstants.LAST_NAME_LENGTH) {
					throw new UserRegistrationServiceException("Last name column or field should be lessthan "
							+ UserAccessManagementServiceConstants.LAST_NAME_LENGTH + " characters ");
				} else {
					lastName = lastName.trim();
				}
			}
			validateFields(email, UserAccessManagementServiceConstants.USER_EMAIL,
					UserAccessManagementServiceConstants.USER_EMAIL_LENGTH);
			validateEmail(email);
			validateFields(phoneNumber, UserAccessManagementServiceConstants.USER_PHONE, 10);
			validatePhoneNumber(phoneNumber);
			validateFields(userRoleCode, UserAccessManagementServiceConstants.USER_ROLE, 1);
			userRole = getUserRoleFromCode(userRoleCode);
			// creating user
			User user = new User();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);
			user.setPhone(phoneNumber);
			user.setUserRole(userRole);
			return user;
		} catch (UserRegistrationServiceException e) {
			JSONObject failureJsonObj = new JSONObject();
			failureJsonObj.put("rowNumber", rowNumber + 1);
			failureJsonObj.put("firstName", firstName);
			failureJsonObj.put("lastName", lastName);
			failureJsonObj.put("email", email);
			failureJsonObj.put("phoneNumber", phoneNumber);
//			failureJsonObj.put("userRoleCode", userRoleCode);
			failureJsonObj.put("userRole", getUserRoleFromCode(userRoleCode));
			failureJsonObj.put("errorMsg", e.getMessage().toString());
			failureArray.add(failureJsonObj);
		}
		return null;
	}

	private String getUserRoleFromCode(String userRoleCode) throws UserRegistrationServiceException {
		int code = Integer.valueOf(userRoleCode);
		logger.debug("code is " + code);
		switch (code) {
		case UserAccessManagementServiceConstants.ONE:
			logger.debug("user is admin");
			return UserAccessManagementServiceConstants.ADMIN;
		case UserAccessManagementServiceConstants.TWO:
			logger.debug("user is client sponsor");
			return UserAccessManagementServiceConstants.CLIENT_SPONSOR;
		case UserAccessManagementServiceConstants.THREE:
			logger.debug("user is field audior");
			return UserAccessManagementServiceConstants.FIELD_AUDITOR;
		case UserAccessManagementServiceConstants.FOUR:
			logger.debug("user is center inchagre");
			return UserAccessManagementServiceConstants.CENTER_IN_CHARGE;
		default:
			throw new UserRegistrationServiceException("invalid userRole code");
		}
	}

	private void validateFields(String fieldValue, String fieldName, int expectedLength)
			throws UserRegistrationServiceException {
		logger.debug("fieldName is :" + fieldName + " and its value is " + fieldValue);
		if (fieldValue == null || fieldValue.isEmpty()) {
			throw new UserRegistrationServiceException(fieldName + " field cant be null or empty");
		}
		if (expectedLength > 0 && fieldValue.toCharArray().length > expectedLength) {
			throw new UserRegistrationServiceException(
					fieldName + " field should be lessthan " + expectedLength + " characters");
		}
	}

	private void validateFields(String fieldValue, String fieldName) throws UserRegistrationServiceException {
		logger.debug("fieldName is :" + fieldName + " and its value is " + fieldValue);
		if (fieldValue == null) {
			throw new UserRegistrationServiceException(fieldName + " field cant be null or empty");
		}
	}

	private void validateEmail(String email) throws UserRegistrationServiceException, UserDaoException {
		// same as front-end
		String emailRegex = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
		Pattern pattern = Pattern.compile(emailRegex);
		boolean result = pattern.matcher(email).matches();
		if (!result) {
			throw new UserRegistrationServiceException(email + " is invalid email formate");
		}

	}

	private void validatePhoneNumber(String phoneNo) throws UserRegistrationServiceException {
		Pattern pattern = Pattern.compile("[7-9]{1}[0-9]{9}");
		boolean result = pattern.matcher(phoneNo).matches();
		if (!result) {
			throw new UserRegistrationServiceException("invalid phone number formate");
		}
	}

	private boolean validateUserRole(String userRole) {
		if (userRole.equals(UserAccessManagementServiceConstants.FIELD_AUDITOR)
				|| userRole.equals(UserAccessManagementServiceConstants.CLIENT_SPONSOR)
				|| userRole.equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)
				|| userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
			logger.debug("current userRole " + userRole);
			return true;
		} else
			return false;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getJSONFromUsersList(List<User> listOfUsers) {
		JSONArray jsonArray = new JSONArray();
		for (User user : listOfUsers) {
			JSONObject userJson = buildJsonFromUser(user);
			jsonArray.add(userJson);
		}
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonFromUser(User user) {
		JSONObject userJson = new JSONObject();
		userJson.put(UserAccessManagementServiceConstants.USER_ID, user.getUserId());
		userJson.put(UserAccessManagementServiceConstants.USER_FIRST_NAME, user.getFirstName());
		userJson.put(UserAccessManagementServiceConstants.USER_LAST_NAME, user.getLastName());
		userJson.put(UserAccessManagementServiceConstants.USER_EMAIL, user.getEmail());
		userJson.put(UserAccessManagementServiceConstants.USER_PHONE, user.getPhone());
		userJson.put(UserAccessManagementServiceConstants.USER_ROLE, user.getUserRole());
		userJson.put(UserAccessManagementServiceConstants.USER_STATUS, user.getStatus());
		userJson.put(UserAccessManagementServiceConstants.USER_CENTER_ID, user.getCenterId());
		return userJson;
	}

	private User buildUserFromJson(JSONObject requestJson) throws JsonUtillException {
		User user = new User();
		user.setFirstName(
				JsonUtill.getString(requestJson, UserAccessManagementServiceConstants.USER_FIRST_NAME).trim());
		user.setLastName(JsonUtill.getString(requestJson, UserAccessManagementServiceConstants.USER_LAST_NAME).trim());
		user.setEmail(JsonUtill.getString(requestJson, UserAccessManagementServiceConstants.USER_EMAIL).trim());
		user.setPhone(JsonUtill.getString(requestJson, UserAccessManagementServiceConstants.USER_PHONE).trim());
		user.setUserRole(JsonUtill.getString(requestJson, UserAccessManagementServiceConstants.USER_ROLE).trim());
		return user;
	}

	/**
	 * Get the user by emailId.
	 * @param tenantId the database name.
	 * @param userEmail the user's Email
	 * @return the user. 
	 * @throws UserRegistrationServiceException  if any exception occurs while performing this operation.
	 */
	@Override
	public String getUserByEmail(String tenantId, String userEmail) throws UserRegistrationServiceException {
		try {
			User user = userDao.getUserByEmail(tenantId, userEmail);
			JSONObject jsonResponse = buildJsonFromUser(user);
			return jsonResponse.toString();
		} catch (Exception e) {
			logger.error(" unable get user  , " + e.getMessage(), e);
			throw new UserRegistrationServiceException(" unable get users " + e.getMessage(), e);
		}
	}

}
