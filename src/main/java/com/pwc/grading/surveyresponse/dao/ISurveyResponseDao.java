package com.pwc.grading.surveyresponse.dao;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.surveyresponse.dao.exception.SurveyResponseDaoException;
import com.pwc.grading.surveyresponse.model.SurveyResponse;

/**
 * An interface class which is used to perform all
 * Survey Response related database operations.
 *
 */
public interface ISurveyResponseDao {

	/**
	 * This method is used to add the survey response data. 
	 * @param databaseName the database name
	 * @param surveyResponse details of survey response.
	 * @return the id of the survey response.
	 * @throws SurveyResponseDaoException  if any exception occurs while performing this operation.
	 */
	public String addSurveyResponseData(String databaseName, SurveyResponse surveyResponse)
			throws SurveyResponseDaoException;

	/**
	 * This method is used to update the survey response data. 
	 * @param connection if this operation is to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param surveyResponse details of survey response.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	public void updateSurveyResponseById(Connection connection, String databaseName, SurveyResponse surveyResponse)
			throws SurveyResponseDaoException;

	/**
	 * This method is used to delete the survey response data. 
	 * @param databaseName the database name
	 * @param surveyResponseById  the id of the survey response.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	public void deleteSurveyResponseById(String databaseName, String surveyResponseById)
			throws SurveyResponseDaoException;

	/**
	 * This method is used to get the survey response data for the given surveyId.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @return all the surveyResponse details for given surveyId.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	public List<SurveyResponse> getSurveyResponsesBySurveyId(String databaseName, String surveyId)
			throws SurveyResponseDaoException;
	
	/**
	 * This method is used to get the survey response data for the given surveyId.
	 * @param databaseName the database name
	 * @param connection if this operation is to be performed in a single transaction.
	 * @param surveyId the id of the survey 
	 * @return all the surveyResponse details for given surveyId.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	public List<SurveyResponse> getSurveyResponsesBySurveyId(Connection connection, String databaseName, String surveyId)
			throws SurveyResponseDaoException;


	/**
	 * This method is used to get the survey response data for the given surveyResponseId.
	 * @param databaseName the database name
	 * @param surveyResponseId the id of the survey response.
	 * @return the surveyResponse details for given surveyResponseId.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	public SurveyResponse getSurveyResponseBySurveyResponseId(String databaseName, String surveyResponseId)
			throws SurveyResponseDaoException;

	/**
	 * This method is used to get the survey response data for the given userId.
	 * @param databaseName the database name
	 * @param userId the id of the user.
	 * @return the surveyResponse details for given userId.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	public List<SurveyResponse> getSurveyResponsesByUserId(String databaseName, String userId)
			throws SurveyResponseDaoException;

	/**
	 * This method is used to delete all the survey response data for the given surveyId.
	 * @param connection if this operation is to be performed in a single transaction.
	 * @param tenantId the database name
	 * @param surveyId the id of the survey 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	public void deleteSurveyResponseBySurveyId(Connection connection, String tenantId, String surveyId)
			throws SurveyResponseDaoException;

	/**
	 * This method is used to get the survey response data for the given training center Id
	 * and the given surveyId.
	 * @param databaseName the database name
	 * @param tcId the id of the training center.
	 * @param surveyId the id of the survey 
	 * @return the surveyResponse details 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	public SurveyResponse getSurveyResponsesByCenterIdAndSurveyId(String databaseName, String tcId, String surveyId)
			throws SurveyResponseDaoException;

	/**
	 * This method is used to get the survey response data for the given training center Id
	 * and the given surveyId.
	 * @param databaseName the database name
	 * @param connection if this is to be performed in transaction.
	 * @param tcId the id of the training center.
	 * @param surveyId the id of the survey 
	 * @return the surveyResponse details 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	public SurveyResponse getSurveyResponsesByCenterIdAndSurveyId(Connection connection, String databaseName, String tcId, String surveyId)
			throws SurveyResponseDaoException;
	
	/**
	 * This method is used to get the survey response data for the given partnerId and the given surveyId.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @param partnerId the id of the partner.
	 * @return the surveyResponse details 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	public SurveyResponse getSurveyResponsesByPartnerIdAndSurveyId(String databaseName, String surveyId,
			String partnerId) throws SurveyResponseDaoException;
	
	/**
	 * This method is used to get the survey response data for the given partnerId and the given surveyId.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @param partnerId the id of the partner.
	 * @return the surveyResponse details 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	public SurveyResponse getSurveyResponsesByPartnerIdAndSurveyId(Connection connection, String databaseName, String surveyId,
			String partnerId) throws SurveyResponseDaoException;

	/**
	 * This method is used to get the survey response data for the given surveyId and auditForId.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @param auditFor the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @return the surveyResponse details 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	public SurveyResponse getSurveyResponsesBySurveyIdAndAuditForId(String databaseName, String surveyId,
			String auditFor, String auditForId) throws SurveyResponseDaoException;

	/**
	 * This method is used to get the survey response data for the given surveyId and auditForId.
	 * @param connection if this operation is to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @param auditFor the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @return the surveyResponse details 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	SurveyResponse getSurveyResponsesBySurveyIdAndAuditForId(Connection connection, String databaseName,
			String surveyId, String auditFor, String auditForId) throws SurveyResponseDaoException;

	/**
	 * This method is used to delete the survey response data for the given training center id and surveyId.
	 * @param connection  if this operation is to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param centerId the id of the training center.
	 * @param surveyId the id of the survey.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	void deleteSurveyResponsesByCenterIdAndSurveyId(Connection connection, String databaseName, String centerId,
			String surveyId) throws SurveyResponseDaoException;

	/**
	 * This method is used to delete the survey response data for the given audit for id and surveyId.
	 * @param connection  if this operation is to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @param auditFor the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	void deleteSurveyResponsesBySurveyIdAndAuditForId(Connection connection, String databaseName, String surveyId,
			String auditFor, String auditForId) throws SurveyResponseDaoException;

	/**
	 * This method is used to delete the survey response data for the given partnerId aand surveyId.
	 * @param connection if this operation is to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param surveyId the id of the survey 
	 * @param partnerId the id of the partner 
	 * @throws SurveyResponseDaoException if any exception occurs while performing this operation.
	 */
	void deleteSurveyResponsesByPartnerIdAndSurveyId(Connection connection, String databaseName, String surveyId,
			String partnerId) throws SurveyResponseDaoException;

	// remove this method
//	SurveyResponse getSurveyResponsesBySurveyIdAndUserId(String databaseName, String surveyId, String userId)
//			throws SurveyResponseDaoException;
}
