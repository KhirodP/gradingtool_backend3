package com.pwc.grading.tracking.dao;

import java.sql.Connection;

import com.pwc.grading.surveyresponse.dao.exception.SurveyResponseDaoException;
import com.pwc.grading.tracking.model.UserSurveyResponseTracking;
import com.pwc.grading.usertoken.model.UserLogTracking;
/**
 * An interface class which is used to perform all
 * tracking related database operations.
 *
 */
public interface ITrackingDao {

	/**
	 * This method is used to add log tracking of the user.
	 * @param tenantId the database name.
	 * @param userTracking the user tracking details.
	 */
	public void addLogTrackingOfUser(String tenantId, UserLogTracking userTracking);

	/**
	 * This method is used to delete log tracking of the user.
	 * @param connection if this operation to be performed in single transaction.
	 * @param tenantId the database name
	 * @param userId the id of the user.
	 */
	public void deleteLogTrackingByUserId(Connection connection, String tenantId, String userId);

//	public void deleteSurveyTrackingByUserId(String tenantId, String userId);

	/**
	 * This method is used to update log tracking of the user.
	 * @param tenanId the database name
	 * @param sessionId the session Id
	 * @param time the time to be updated.
	 */
	public void updateLogTrackingBySessionId(String tenanId, String sessionId, long time);

	/**
	 * This method is used to add survey response tracking of the user.
	 * @param tenantId the database name
	 * @param userSurveyResponse
	 */
	public void addUserSurveryResponseTracking(String tenantId, UserSurveyResponseTracking userSurveyResponse);

//	public void updateUserSurveryResponseTracking(String tenantId, String endDate, String endTime, String userId);

	/**
	 * This method is used to get SurveyResponse Tracking With SurveyId and AuditFor Id.
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName  the database name
	 * @param surveyId the id of the survey
	 * @param auditFor the audit for training center or partner.
	 * @param auditForId the audit for training center Id or partnerId.
	 * @return the User Survey Response Tracking details
	 */
	public UserSurveyResponseTracking getSurveyResponseTrackingWithSurveyIdandAuditForId(Connection connection,
			String databaseName, String surveyId, String auditFor, String auditForId);

	/**
	 * This method is used to get SurveyResponse Tracking With SurveyId and CenterId.
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName  the database name
	 * @param surveyId the id of the survey
	 * @param centerId training center Id 
	 * @return the User Survey Response Tracking details
	 */
	public UserSurveyResponseTracking getSurveyResponseTrackingWithSurveyIdandCenterId(Connection connection,
			String databaseName, String surveyId, String centerId);

	/**
	 * This method is used to get SurveyResponse Tracking With SurveyId and PartnerId.
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName  the database name
	 * @param surveyId the id of the survey
	 * @param partnerId  the id of the partner
	 * @return the User Survey Response Tracking details
	 */
	public UserSurveyResponseTracking getSurveyResponseTrackingWithSurveyIdandPartnerId(Connection connection,
			String databaseName, String surveyId, String partnerId);

	/**
	 * This method is used to get SurveyResponse Tracking for training center.
	 * @param tenantId the database name
	 * @param endDate the end date to be updated
	 * @param endTime the endTime to be updated
	 * @param surveyId the id of the survey
	 * @param centerId  training center Id 
	 */
	void updateUserSurveryResponseTrackingForTrainingCenter(String tenantId, String endDate, String endTime,
			String surveyId, String centerId);

	/**
	 * This method is used to get SurveyResponse Tracking for Partner.
	 * @param tenantId the database name
	 * @param endDate the end date to be updated
	 * @param endTime the endTime to be updated
	 * @param surveyId the id of the survey
	 * @param partnerId the id of the partner
	 */
	void updateUserSurveryResponseTrackingForPartner(String tenantId, String endDate, String endTime, String surveyId,
			String partnerId);

	/**
	 *  This method is used to get SurveyResponse Tracking for FieldAuditor.
	 * @param tenantId the database name
	 * @param endDate the end date to be updated
	 * @param endTime the endTime to be updated
	 * @param surveyId the id of the survey
	 * @param auditFor the audit for training center or partner.
	 * @param auditForId the audit for training center Id or partnerId.
	 */
	void updateUserSurveryResponseTrackingForFieldAuditor(String tenantId, String endDate, String endTime,
			String surveyId, String auditFor, String auditForId);

	/**
	 * This method is used to delete SurveyResponse Tracking With SurveyId and CenterId. 
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName  the database name
	 * @param centerId training center Id 
	 * @param surveyId the id of the survey
	 * @throws SurveyResponseDaoException if any exception occurs.
	 */
	void deleteSurveyResponsesTrackingByCenterIdAndSurveyId(Connection connection, String databaseName, String centerId,
			String surveyId) throws SurveyResponseDaoException;

	/**
	 * This method is used to get SurveyResponse Tracking With SurveyId and AuditForId.
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey
	 * @param auditFor the audit for training center or partner.
	 * @param auditForId the audit for training center Id or partnerId.
	 * @throws SurveyResponseDaoException if any exception occurs.
	 */
	void deleteSurveyResponsesTrackingBySurveyIdAndAuditForId(Connection connection, String databaseName, String surveyId,
			String auditFor, String auditForId) throws SurveyResponseDaoException;

	/**
	 * This method is used to delete SurveyResponse Tracking With SurveyId and PartnerId.
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey
	 * @param partnerId the id of the partner
	 * @throws SurveyResponseDaoException if any exception occurs.
	 */
	void deleteSurveyResponsesTrackingByPartnerIdAndSurveyId(Connection connection, String databaseName, String surveyId,
			String partnerId) throws SurveyResponseDaoException;

	/**
	 * This method is used to delete SurveyResponse Tracking With SurveyId.
	 * @param connection if this operation to be performed in single transaction.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey
	 * @param partnerId the id of the partner
	 * @throws SurveyResponseDaoException if any exception occurs.
	 */
	void deleteSurveyResponsesTrackingBySurveyId(Connection connection, String databaseName, String surveyId)
			throws SurveyResponseDaoException;

}
