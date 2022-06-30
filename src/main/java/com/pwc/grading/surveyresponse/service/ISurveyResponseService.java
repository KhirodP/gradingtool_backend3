package com.pwc.grading.surveyresponse.service;

import com.pwc.grading.surveyresponse.service.exception.SurveyResponseServiceException;
/**
 * An interface class which is used to perform all
 * Survey Response operations.
 *
 */
public interface ISurveyResponseService {

	/**
	 * This method is used to add the survey response data.
	 * @param tanantId the database name
	 * @param requestJSON the JSON containing the survey response details.
	 * @param email the email of the user.
	 * @return success response if deleted
	 * @throws SurveyResponseServiceException  if any exception occurs while performing this operation.
	 */
	public String addSurveyResponseData(String tanantId, String requestJSON, String email)
			throws SurveyResponseServiceException;

	/**
	 * This method is used to update the survey response data. 
	 * @param tanantId the database name
	 * @param surveyResponseId the id of the surveyResponse
	 * @param requestJSON the JSON containing the survey response details.
	 * @param email  the email of the user.
	 * @return response message if the action is success.
	 * @throws SurveyResponseServiceException
	 */
	public String updateSurveyResponseById(String tanantId, String surveyResponseId, String requestJSON, String email)
			throws SurveyResponseServiceException;

	/**
	 * This method is used to delete the survey response data. 
	 * @param tanantId the database name
	 * @param surveyResponseById the id of the surveyResponse
	 * @return response message if the action is success.
	 * @throws SurveyResponseServiceException
	 */
	public String deleteSurveyResponseById(String tanantId, String surveyResponseById)
			throws SurveyResponseServiceException;

	/**
	 * This method is used to get the survey response data for given surveyId. 
	 * @param tanantId the database name
	 * @param surveyId the id of the survey
	 * @return response containing the survey response data
	 * @throws SurveyResponseServiceException
	 */
	public String getSurveyResponsesBySurveyId(String tanantId, String surveyId) throws SurveyResponseServiceException;

	/**
	 * This method is used to get the survey response data for given surveyResponseId. 
	 * @param tanantId the database name
	 * @param surveyResponseId the id of the surveyResponse
	 * @return response containing the survey response data
	 * @throws SurveyResponseServiceException
	 */
	public String getSurveyResponseBySurveyResponseById(String tanantId, String surveyResponseId)
			throws SurveyResponseServiceException;

	/**
	 * This method is used to get the survey response data of the user and given surveyId.
	 * @param tanantId the database name
	 * @param surveyId the id of the survey
	 * @param email the email of the user.
	 * @param auditFor the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @return response containing the survey response data
	 * @throws SurveyResponseServiceException
	 */
	public String getSurveyResponsesOfUserBySurveyId(String tanantId, String surveyId, String email, String auditFor,
			String auditForId) throws SurveyResponseServiceException;

	/**
	 * This method is used to get the survey response data for userId.
	 * @param tanantId the database name
	 * @param userId the id of the user.
	 * @return response containing the survey response data
	 * @throws SurveyResponseServiceException
	 */
	public String getSurveyResponsesByUserId(String tanantId, String userId) throws SurveyResponseServiceException;

	/**
	 * This method is used to get the audit data.
	 * @param tenantId  the database name
	 * @param surveyId the id of the survey
	 * @param auditFor the audit for is training center or partner.
	 * @param tcId the id of the training center.
	 * @param partnerId  the id of the partner.
	 * @return response containing the audit data
	 * @throws SurveyResponseServiceException
	 */
	String getAuditData(String tenantId, String surveyId, String auditFor, String tcId, String partnerId)
			throws SurveyResponseServiceException;

	/**
	 * This method is used to send the otp to the user when submitting the survey.
	 * @param userEmail the email of the user.
	 * @param tenantId  the database name
	 * @param auditFor the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @return response if otp send is success.
	 * @throws SurveyResponseServiceException
	 */
	public String sendOtp(String userEmail, String tenantId, String auditFor, String auditForId)
			throws SurveyResponseServiceException;

	/**
	 * This method is used to verify the otp of the user when submitting the survey.
	 * @param userEmail the email of the user.
	 * @param otp otp entered by the user.
	 * @param tenantId  the database name
	 * @param auditFor the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @return  response if otp entered is correct.
	 * @throws SurveyResponseServiceException
	 */
	public String verifyOtp(String userEmail, String otp, String tenantId, String auditFor, String auditForId) throws SurveyResponseServiceException;

//	public String deleteSurveyResponseBySurveyId(String tenantId, String surveyId)
//			throws SurveyResponseServiceException;

}
