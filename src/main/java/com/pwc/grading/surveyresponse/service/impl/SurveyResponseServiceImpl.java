package com.pwc.grading.surveyresponse.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.geolocation.dao.IGeoLocationDao;
import com.pwc.grading.geolocation.dao.exception.GeoLocationDAOException;
import com.pwc.grading.mail.service.IMailService;
import com.pwc.grading.masterdata.dao.exception.MasterDataManagmentDaoException;
import com.pwc.grading.partner.dao.IPartnerDao;
import com.pwc.grading.partner.dao.exception.PartnerDaoException;
import com.pwc.grading.partner.model.PartnerDetails;
import com.pwc.grading.partner.model.TrainingCenterDetails;
import com.pwc.grading.project.dao.IProjectDao;
import com.pwc.grading.project.dao.exception.ProjectDaoException;
import com.pwc.grading.project.model.FormData;
import com.pwc.grading.project.model.ProjectData;
import com.pwc.grading.project.model.Survey;
import com.pwc.grading.project.service.ProjectServiceConstant;
import com.pwc.grading.project.service.exception.ProjectServiceException;
import com.pwc.grading.report.service.ReportServiceConstants;
import com.pwc.grading.reportingdb.ReportingDBService;
import com.pwc.grading.reportingdb.assigner.UpdateGradingCSJobAssigner;
import com.pwc.grading.reportingdb.assigner.UpdateGradingFAJobAssigner;
import com.pwc.grading.reportingdb.assigner.UpdateRatingCICJobAssigner;
import com.pwc.grading.reportingdb.assigner.UpdateRatingFAJobAssigner;
import com.pwc.grading.reportingdb.assigner.exception.AssignerException;
import com.pwc.grading.reportingdb.constant.ReportingDbJSONConstant;
import com.pwc.grading.reportingdb.db.constant.ReportingDatabaseQueryConstants;
import com.pwc.grading.scheduler.dao.ISchedulerDao;
import com.pwc.grading.scheduler.dao.exception.SchedulerDaoException;
import com.pwc.grading.scheduler.model.GradingType;
import com.pwc.grading.scheduler.model.RatingType;
import com.pwc.grading.surveyresponse.dao.ISurveyResponseDao;
import com.pwc.grading.surveyresponse.dao.exception.SurveyResponseDaoException;
import com.pwc.grading.surveyresponse.model.SurveyResponse;
import com.pwc.grading.surveyresponse.service.ISurveyResponseService;
import com.pwc.grading.surveyresponse.service.SurveyResponseServiceConstant;
import com.pwc.grading.surveyresponse.service.exception.SurveyResponseServiceException;
import com.pwc.grading.tracking.dao.ITrackingDao;
import com.pwc.grading.tracking.model.UserSurveyResponseTracking;
import com.pwc.grading.user.dao.IUserDao;
import com.pwc.grading.user.dao.exception.UserDaoException;
import com.pwc.grading.user.model.User;
import com.pwc.grading.user.model.UserOtp;
import com.pwc.grading.user.service.login.UserLoginServiceConstant;
import com.pwc.grading.user.service.login.exception.UserLoginServiceException;
import com.pwc.grading.user.service.registration.UserAccessManagementServiceConstants;
import com.pwc.grading.user.util.UserUtil;
import com.pwc.grading.usertoken.model.ResponseSubmission;
import com.pwc.grading.util.JsonUtill;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.TokenBuilder;
import com.pwc.grading.util.TokenValidator;
import com.pwc.grading.util.exception.JsonUtillException;

/**
 * Implementation class for {@link ISurveyResponseService}
 *
 */
@Singleton
public class SurveyResponseServiceImpl implements ISurveyResponseService {

	private static final Logger logger = LoggerFactory.getLogger(SurveyResponseServiceImpl.class);
	@Inject
	private ISurveyResponseDao surveyResponseDao;
	@Inject
	private IProjectDao surveydao;
	@Inject
	private IUserDao iuserDao;
	@Inject
	private ITrackingDao iTrackingDao;
	@Inject
	private IGeoLocationDao iGeoLocationDao;
	@Inject
	private ISchedulerDao iSchedulerDao;
	@Inject
	private IPartnerDao ipartnerDao;
	@Inject
	private IMailService mailService;
//	@Inject
//	private ISmsService smsService;
	@Inject
	private TokenBuilder tokenBuilder;

	/**
	 * This method is used to add the survey response data.
	 * 
	 * @param tanantId    the database name
	 * @param requestJSON the JSON containing the survey response details.
	 * @param email       the email of the user.
	 * @return success response if deleted
	 * @throws SurveyResponseServiceException if any exception occurs while
	 *                                        performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String addSurveyResponseData(String tanantId, String requestJSON, String email)
			throws SurveyResponseServiceException {
		try {
			logger.debug("inside addSurveyResponseData method of SurveyResponseServiceImpl ");
			logger.debug("requestJSON :: " + requestJSON);
			User user = iuserDao.getUserByEmail(tanantId, email);
			if (user == null) {
				throw new SurveyResponseServiceException("user doesnt exist");
			}
			String userId = user.getUserId();
			JSONObject requestJSONObject = (JSONObject) JSONValue.parse(requestJSON);
			SurveyResponse surveyResponse = getSurveyResponseFromRequestJSON(requestJSONObject);
			if (!(user.getUserRole().equals(surveyResponse.getResponderType()))) {
				throw new SurveyResponseServiceException("Loged In user and Respondertype doesnot Match");
			}
			surveyResponse.setUserId(userId);
			String surveyId = surveyResponse.getSurveyId();
			Survey survey = surveydao.getSurveyById(tanantId, surveyId);
			if (survey == null) {
				throw new SurveyResponseServiceException("Invalid SurveyId");
			}
			String surveyResponseId = surveyResponseDao.addSurveyResponseData(tanantId, surveyResponse);
			JSONObject geoLocationJson = JsonUtill.getJsonObject(requestJSONObject,
					SurveyResponseServiceConstant.GEO_LOCATION);
			addSurveyResponseTracking(tanantId, surveyResponse, geoLocationJson, user);

			JSONObject responseJSON = new JSONObject();
			responseJSON.put(SurveyResponseServiceConstant.SURVEY_RESPONSE_ID, surveyResponseId);
			responseJSON.put("msg", ReadPropertiesFile.readResponseProperty("206"));
			return responseJSON.toString();
		} catch (Exception e) {
			logger.error(" unable to store Survey Response " + e.getMessage(), e);
			throw new SurveyResponseServiceException(" unable to store Survey Response " + e.getMessage(), e);
		}
	}

	/**
	 * Add the survey response tracking
	 */
	private void addSurveyResponseTracking(String databaseName, SurveyResponse surveyResponse,
			JSONObject geoLocationJson, User user)
			throws ProjectDaoException, GeoLocationDAOException, JsonUtillException {
		String responderType = surveyResponse.getResponderType();
		DateTimeFormatter dateFormate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeFormate = DateTimeFormatter.ofPattern("hh:mm:ss");
		double latitude = JsonUtill.getDouble(geoLocationJson, SurveyResponseServiceConstant.LATITUDE);
		double longitude = JsonUtill.getDouble(geoLocationJson, SurveyResponseServiceConstant.LONGITUDE);
		String surveyId = surveyResponse.getSurveyId();
		FormData form = surveydao.getFormsBySurveyId(databaseName, surveyId);
		String projectId = form.getProjectId();
		double variance = iGeoLocationDao.calculateDistance(databaseName, user.getCenterId(), latitude, longitude);
		UserSurveyResponseTracking userSurveyResponseTracking = new UserSurveyResponseTracking(projectId, surveyId,
				variance, LocalDate.now().format(dateFormate), LocalTime.now().format(timeFormate), null, null);
		userSurveyResponseTracking.setLatitude(latitude);
		userSurveyResponseTracking.setLongitude(longitude);

		if (responderType.equals(UserAccessManagementServiceConstants.CLIENT_SPONSOR)) {
			userSurveyResponseTracking.setPartnerId(surveyResponse.getPartnerId());
		} else if (responderType.equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
			userSurveyResponseTracking.setCenterId(surveyResponse.getCenterId());
		} else if (responderType.equals(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
			userSurveyResponseTracking.setAuditFor(surveyResponse.getAuditFor());
			userSurveyResponseTracking.setAuditId(surveyResponse.getAuditForId());
		}
		iTrackingDao.addUserSurveryResponseTracking(databaseName, userSurveyResponseTracking);
	}

	/**
	 * This method is used to update the survey response data.
	 * 
	 * @param tanantId         the database name
	 * @param surveyResponseId the id of the surveyResponse
	 * @param requestJSON      the JSON containing the survey response details.
	 * @param email            the email of the user.
	 * @return response message if the action is success.
	 * @throws SurveyResponseServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String updateSurveyResponseById(String tanantId, String surveyResponseId, String requestJSON, String email)
			throws SurveyResponseServiceException {
		Connection connection = null;
		try {
			logger.debug("inside updateSurveyResponseById method of SurveyResponseServiceImpl ");
			validateId(surveyResponseId, SurveyResponseServiceConstant.SURVEY_RESPONSE_ID);
			logger.debug("requestJSON :: " + requestJSON);
			User user = iuserDao.getUserByEmail(tanantId, email);
			if (user == null) {
				throw new SurveyResponseServiceException("unable to update Survey Response, user not found");
			}
			// User status Check
			boolean userActive = UserUtil.isUserActive(user);
			if (!userActive) {
				throw new SurveyResponseServiceException("unable to update Survey Response, User is disabled.");
			} // End User status Check
			String userId = user.getUserId();
			JSONObject requestJSONObject = (JSONObject) JSONValue.parse(requestJSON);
			SurveyResponse surveyResponse = getSurveyResponseFromRequestJSON(requestJSONObject);
			surveyResponse.setUserId(userId);
			if (!(user.getUserRole().equals(surveyResponse.getResponderType()))) {
				throw new SurveyResponseServiceException("Loged In user and Respondertype doesnot Match");
			}
			surveyResponse.setSurveyResponseId(surveyResponseId);
			logger.debug("surveyResponse ::" + surveyResponse.toString());
			long start = System.currentTimeMillis();
			evaluate(tanantId, surveyResponse);
			long end = System.currentTimeMillis();
			logger.debug("total time to evaluate sectionScore and total score is " + (end - start));
			String surveyId = surveyResponse.getSurveyId();
			FormData formData = surveydao.getFormsBySurveyId(tanantId, surveyId);
			validation(tanantId, formData, surveyResponse, user);
			logger.debug("**********************************************************");
			logger.debug("						[Tranaction Start]					");
			logger.debug("**********************************************************");
			connection = MSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			if (surveyResponse.isSubmited()) {
				String jwtToken = surveyResponse.getJwtToken();
				if (jwtToken == null || jwtToken.isEmpty()) {
					throw new SurveyResponseServiceException("Invalid jwtToken");
				}
				updateSchedulerTablesIfSubmitting(connection, tanantId, formData, surveyResponse, user);
				surveyResponse.setSubmitTime(new Date().getTime());
			}
			surveyResponse.setSaveTime(new Date().getTime());
			logger.debug("SurveyResponse is :: " + surveyResponse);
			surveyResponseDao.updateSurveyResponseById(connection, tanantId, surveyResponse);
			connection.commit();
			logger.debug("**********************************************************");
			logger.debug("						[Tranaction Ends]			    		");
			logger.debug("**********************************************************");
//			updateSurveyResponseTracking(tanantId, surveyResponse);
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("msg", ReadPropertiesFile.readResponseProperty("207"));
			return responseJSON.toString();
		} catch (Exception e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			logger.error(" unable to update Survey Response " + e.getMessage(), e);
			throw new SurveyResponseServiceException(" unable to update Survey Response " + e.getMessage(), e);
		} finally {
			MSSqlServerUtill.close(null, connection);
		}
	}

	/**
	 * Validations for the survey response.
	 */
	private void validation(String tanantId, FormData formData, SurveyResponse surveyResponse, User user)
			throws SurveyResponseServiceException, PartnerDaoException, SchedulerDaoException, ProjectDaoException {
		if (surveyResponse.getResponderType().equals(UserAccessManagementServiceConstants.CLIENT_SPONSOR)) {
			surveyResponse.setAuditFor(null);
			surveyResponse.setAuditForId(null);
			surveyResponse.setCenterId(null);
			String partnerId = surveyResponse.getPartnerId();
			PartnerDetails partnerDetails = ipartnerDao.getPartnerById(tanantId, partnerId);
			if (partnerDetails == null) {
				throw new SurveyResponseServiceException("user is not assigned to any partner or invalid Partenr Id");
			}
			String projectId = formData.getProjectId();
//			String formId = formData.getFormId();
			ProjectData project = surveydao.getProjectById(tanantId, projectId);
//			long selfAssignmentDeadline = project.getSelfAssignmentDeadline();
//			GradingType gradingType = iSchedulerDao.getGradingTypeDataByPartnerIdProjectIdAndFormId(tanantId, partnerId,
//					projectId, formId);
			long selfAssessmentDeadLine = project.getSelfAssignmentDeadline();
			Date date = new Date();
			long currentTime = date.getTime();
			if (selfAssessmentDeadLine < currentTime && selfAssessmentDeadLine != 0) {
				throw new SurveyResponseServiceException("Can't submit the Form, The final deadline for submissions is "
						+ getStringFormateTimeFromMiliSecond(selfAssessmentDeadLine));
			}
		} else if (surveyResponse.getResponderType().equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
			String centerId = surveyResponse.getCenterId();
			surveyResponse.setAuditFor(null);
			surveyResponse.setAuditForId(null);
			surveyResponse.setPartnerId(null);
			TrainingCenterDetails tcDetails = ipartnerDao.getTrainingCenterDetailsByTcId(tanantId, centerId);
			if (tcDetails == null) {
				throw new SurveyResponseServiceException("invalid tcId");
			}
//			String partnerId = tcDetails.getPartnerId();
			String projectId = formData.getProjectId();
//			String formId = formData.getFormId();
//			RatingType ratingType = iSchedulerDao.getRatingTypeDataByProjectIdParterIdFormIdAndTcId(tanantId, partnerId,
//					projectId, formId, centerId);
			ProjectData project = surveydao.getProjectById(tanantId, projectId);
			long selfAssessmentDeadLine = project.getSelfAssignmentDeadline();
			Date date = new Date();
			long currentTime = date.getTime();
			if (selfAssessmentDeadLine < currentTime && selfAssessmentDeadLine != 0) {
				throw new SurveyResponseServiceException("Can't submit the Form, The final deadline for submissions is "
						+ getStringFormateTimeFromMiliSecond(selfAssessmentDeadLine));
			}
		} else if (surveyResponse.getResponderType().equals(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
			String auditFor = surveyResponse.getAuditFor();
			String auditForId = surveyResponse.getAuditForId();
			surveyResponse.setCenterId(null);
			surveyResponse.setPartnerId(null);
			String projectId = formData.getProjectId();
//			ProjectData project = surveydao.getProjectById(tanantId, projectId);
			logger.debug("auditForId ::" + auditForId);
			String formId = formData.getFormId();
			boolean isGradingForm = isFromGradingForm(formData);
			boolean isRatingForm = isFromRatingForm(formData);
			if (isGradingForm && auditFor.equals(ProjectServiceConstant.PARTNER)) {
				PartnerDetails partnerDetails = ipartnerDao.getPartnerById(tanantId, auditForId);
				if (partnerDetails == null) {
					throw new SurveyResponseServiceException("In valid partnerId");
				}
				String partnerId = partnerDetails.getPartnerId();
				GradingType gradingType = iSchedulerDao.getGradingTypeDataByPartnerIdProjectIdAndFormId(tanantId,
						partnerId, projectId, formId);
				long selfAssessmentDeadLine = gradingType.getAuditDate();
				Date date = new Date();
				long currentTime = date.getTime();
				if (selfAssessmentDeadLine < currentTime && selfAssessmentDeadLine != 0) {
					throw new SurveyResponseServiceException(
							"Can't submit the Form, The final deadline for submissions is "
									+ getStringFormateTimeFromMiliSecond(selfAssessmentDeadLine));
				}
			} else if (isRatingForm && auditFor.equals(ProjectServiceConstant.TRAINING_CENTER)) {
				TrainingCenterDetails tcDetails = ipartnerDao.getTrainingCenterDetailsByTcId(tanantId, auditForId);
				if (tcDetails == null) {
					throw new SurveyResponseServiceException("invalid tcId");
				}
				RatingType ratingType = iSchedulerDao.getRatingTypeDataByProjectIdParterIdFormIdAndTcId(tanantId,
						tcDetails.getPartnerId(), projectId, formId, tcDetails.getTcId());
				long selfAssessmentDeadLine = ratingType.getAuditDate();
				Date date = new Date();
				long currentTime = date.getTime();
				if (selfAssessmentDeadLine < currentTime && selfAssessmentDeadLine != 0) {
					throw new SurveyResponseServiceException(
							"Can't submit the Form, The final deadline for submissions is "
									+ getStringFormateTimeFromMiliSecond(selfAssessmentDeadLine));
				}
			} else {
				throw new SurveyResponseServiceException(auditFor + " is an Invalid AuditorFor ");
			}

		}
	}

	/**
	 * Updating the scheduler tables if response is submitted.
	 */
	private void updateSchedulerTablesIfSubmitting(Connection connection, String tanantId, FormData formData,
			SurveyResponse surveyResponse, User user)
			throws PartnerDaoException, SchedulerDaoException, SurveyResponseServiceException, UserDaoException,
			ProjectDaoException, SurveyResponseDaoException, MasterDataManagmentDaoException, JsonUtillException,
			ProjectServiceException, AssignerException, org.json.simple.parser.ParseException, SQLServerException {
		DateTimeFormatter dateFormate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeFormate = DateTimeFormatter.ofPattern("hh:mm:ss");
		String formId = formData.getFormId();
		ResponseSubmission responseSubmission = TokenValidator
				.validateSurveySubmissionJWTToken(surveyResponse.getJwtToken());
		boolean isSubmited = surveyResponse.isSubmited();
		if (surveyResponse.getResponderType().equals(UserAccessManagementServiceConstants.CLIENT_SPONSOR)) {
			String partnerId = surveyResponse.getPartnerId();
			String projectId = formData.getProjectId();
			iSchedulerDao.updateSelfAssigInGradingTypeByProjectIdPartnerIdAndFormId(connection, tanantId, isSubmited,
					partnerId, projectId, formId);
			iTrackingDao.updateUserSurveryResponseTrackingForPartner(tanantId, LocalDate.now().format(dateFormate),
					LocalTime.now().format(timeFormate), surveyResponse.getSurveyId(), surveyResponse.getPartnerId());
			// UpdateGradingCS
			if (ReportingDBService.ENABLED) {
				logger.debug("UpdateGradingCS");
				JSONObject jsonObject = buildUpdateGradingClientSponsor(connection, tanantId, partnerId, formData,
						surveyResponse);
				logger.debug("UpdateGradingCS jsonObject:: " + jsonObject);
				UpdateGradingCSJobAssigner cs = new UpdateGradingCSJobAssigner();
				cs.assignUpdateGradingCSJobToDatabase(tanantId, jsonObject.toJSONString());
			}
		} else if (surveyResponse.getResponderType().equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
			String centerId = surveyResponse.getCenterId();
			TrainingCenterDetails tcDetails = ipartnerDao.getTrainingCenterDetailsByTcId(connection, tanantId,
					centerId);
			iSchedulerDao.updateSelfAssigInRatingTypeByProjectIdPartnerIdFormIdAndTcId(connection, tanantId, isSubmited,
					tcDetails.getPartnerId(), formData.getProjectId(), formId, centerId);
			iTrackingDao.updateUserSurveryResponseTrackingForTrainingCenter(tanantId,
					LocalDate.now().format(dateFormate), LocalTime.now().format(timeFormate),
					surveyResponse.getSurveyId(), surveyResponse.getCenterId());
			// UpdateRatingCenterIncharge
			if (ReportingDBService.ENABLED) {
				logger.debug("UpdateRatingCenterIncharge");
				JSONObject jsonObject = buildUpdateRatingCenterInCharge(connection, tanantId, tcDetails, formData,
						surveyResponse);
				logger.debug("UpdateRatingCenterIncharge jsonObject:: " + jsonObject);
				UpdateRatingCICJobAssigner cic = new UpdateRatingCICJobAssigner();
				cic.assignUpdateRatingCICJobToDatabase(tanantId, jsonObject.toJSONString());
			}
		} else if (surveyResponse.getResponderType().equals(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
			String otp = responseSubmission.getOtp();
			surveyResponse.setOtp(otp);
			boolean isGradingForm = isFromGradingForm(formData);
			boolean isRatingForm = isFromRatingForm(formData);
			String auditFor = surveyResponse.getAuditFor();
			String secondaryFieldAuditorName = surveyResponse.getSecondaryFieldAuditor();
			if (secondaryFieldAuditorName == null || secondaryFieldAuditorName.isEmpty()) {
				throw new SurveyResponseServiceException(" invalid request secondaryFieldAuditorName required");
			}

			if (isGradingForm && auditFor.equals(ProjectServiceConstant.PARTNER)) {
				String auditForId = surveyResponse.getAuditForId();
				PartnerDetails partnerDetails = ipartnerDao.getPartnerById(connection, tanantId, auditForId);
				iSchedulerDao.updateAuditStatusInGradingTypeByProjectIdPartnerIdAndFormId(connection, tanantId,
						isSubmited, secondaryFieldAuditorName, partnerDetails.getPartnerId(), formData.getProjectId(),
						formId);
				iTrackingDao.updateUserSurveryResponseTrackingForFieldAuditor(tanantId,
						LocalDate.now().format(dateFormate), LocalTime.now().format(timeFormate),
						surveyResponse.getSurveyId(), surveyResponse.getAuditFor(), surveyResponse.getAuditForId());
				// UpdateGradingFielAuditor
				if (ReportingDBService.ENABLED) {
					logger.debug("UpdateGradingFielAuditor ");
					JSONObject jsonObject = buildUpdateGradingFieldAuditor(connection, tanantId, partnerDetails,
							formData, surveyResponse);
					logger.debug("UpdateGradingFielAuditor jsonObject:: " + jsonObject);
					UpdateGradingFAJobAssigner gfa = new UpdateGradingFAJobAssigner();
					gfa.assignUpdateGradingFAJobToDatabase(tanantId, jsonObject.toJSONString());
				}
			} else if (isRatingForm && auditFor.equals(ProjectServiceConstant.TRAINING_CENTER)) {
				String auditForId = surveyResponse.getAuditForId();
				TrainingCenterDetails tcDetails = ipartnerDao.getTrainingCenterDetailsByTcId(connection, tanantId,
						auditForId);
				iSchedulerDao.updateAuditStatusInRatingTypeByProjectIdPartnerIdFormIdAndTcId(connection, tanantId,
						isSubmited, secondaryFieldAuditorName, tcDetails.getPartnerId(), formData.getProjectId(),
						formId, tcDetails.getTcId());
				iTrackingDao.updateUserSurveryResponseTrackingForFieldAuditor(tanantId,
						LocalDate.now().format(dateFormate), LocalTime.now().format(timeFormate),
						surveyResponse.getSurveyId(), surveyResponse.getAuditFor(), surveyResponse.getAuditForId());
				// UpdateRatingFielAuditor
				if (ReportingDBService.ENABLED) {
					logger.debug("UpdateRatingFielAuditor ");
					JSONObject jsonObject = buildUpdateRatingFieldAuditor(connection, tanantId, tcDetails, formData,
							surveyResponse);
					logger.debug("UpdateRatingFielAuditor jsonObject:: " + jsonObject);
					UpdateRatingFAJobAssigner rfa = new UpdateRatingFAJobAssigner();
					rfa.assignUpdateRatingFAJobToDatabase(tanantId, jsonObject.toJSONString());
				}
			} else {
				throw new SurveyResponseServiceException(" invalid Auditor For ");
			}
		} else {
			throw new SurveyResponseServiceException(" invalid responder Type ");
		}

	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildUpdateGradingClientSponsor(Connection connection, String tanantId, String partnerId,
			FormData formData, SurveyResponse surveyResponse) throws SchedulerDaoException, ProjectDaoException,
			SurveyResponseDaoException, MasterDataManagmentDaoException, PartnerDaoException, UserDaoException,
			JsonUtillException, ProjectServiceException {
		PartnerDetails partnerDetails = ipartnerDao.getPartnerById(connection, tanantId, partnerId);
		// partner
		JSONObject response = buildPartnerJsonForReportDb(connection, tanantId, partnerDetails);
		// score
		JSONObject scoreJson = buildScorJson(connection, tanantId, partnerDetails, formData);
		response.put(ReportingDbJSONConstant.SCORE, scoreJson);
		// project
		JSONObject projectJsonObject = buildProjectJsonForReportDb(connection, tanantId, formData);
		// form
		JSONObject formJson = buildFormForCIcAndCS(connection, tanantId, formData, surveyResponse);
		JSONObject timeJsonObject = buildTimingJsonForClientSponsor(connection, tanantId, surveyResponse);
		formJson.put(ReportingDbJSONConstant.TIMING, timeJsonObject);
		projectJsonObject.put(ReportingDbJSONConstant.FORM, formJson);
		response.put(ReportingDbJSONConstant.PROJECT, projectJsonObject);
		return response;
	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildUpdateRatingCenterInCharge(Connection connection, String tanantId,
			TrainingCenterDetails tcDetails, FormData formData, SurveyResponse surveyResponse)
			throws UserDaoException, PartnerDaoException, SchedulerDaoException, ProjectDaoException,
			SurveyResponseDaoException, MasterDataManagmentDaoException, JsonUtillException, ProjectServiceException {
		PartnerDetails partnerDetails = ipartnerDao.getPartnerById(connection, tanantId, tcDetails.getPartnerId());
		// partner
		JSONObject response = buildPartnerJsonForReportDb(connection, tanantId, partnerDetails);
		// score
		JSONObject scoreJson = buildScorJson(connection, tanantId, partnerDetails, formData);
		response.put(ReportingDbJSONConstant.SCORE, scoreJson);
		// trainingCenter
		JSONObject trainingCenterJson = buildtrainingCenterJsonForReportDb(connection, tanantId, tcDetails);
		response.put(ReportingDbJSONConstant.TRAINING_CENTER, trainingCenterJson);
		// project
		JSONObject projectJsonObject = buildProjectJsonForReportDb(connection, tanantId, formData);
		// form
		JSONObject formJson = buildFormForCIcAndCS(connection, tanantId, formData, surveyResponse);
		JSONObject timeJsonObject = buildTimingJsonForCenterInCharge(connection, tanantId, surveyResponse);
		formJson.put(ReportingDbJSONConstant.TIMING, timeJsonObject);
		projectJsonObject.put(ReportingDbJSONConstant.FORM, formJson);
		response.put(ReportingDbJSONConstant.PROJECT, projectJsonObject);
		return response;
	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildUpdateRatingFieldAuditor(Connection connection, String tanantId,
			TrainingCenterDetails tcDetails, FormData formData, SurveyResponse surveyResponse)
			throws SchedulerDaoException, ProjectDaoException, SurveyResponseDaoException,
			MasterDataManagmentDaoException, PartnerDaoException, UserDaoException, JsonUtillException,
			ProjectServiceException, SQLServerException {
		PartnerDetails partnerDetails = ipartnerDao.getPartnerById(connection, tanantId, tcDetails.getPartnerId());
		JSONObject response = buildPartnerJsonForReportDb(connection, tanantId, partnerDetails);
		JSONObject scoreJson = buildScorJson(connection, tanantId, partnerDetails, formData);
		response.put(ReportingDbJSONConstant.SCORE, scoreJson);
		JSONObject trainingCenterJson = buildtrainingCenterJsonForReportDb(connection, tanantId, tcDetails);
		response.put(ReportingDbJSONConstant.TRAINING_CENTER, trainingCenterJson);
		JSONObject projectJsonObject = buildProjectJsonForReportDb(connection, tanantId, formData);
		JSONObject formJson = buildFormForFieldAuditor(connection, tanantId, formData, surveyResponse);
		projectJsonObject.put(ReportingDbJSONConstant.FORM, formJson);
		response.put(ReportingDbJSONConstant.PROJECT, projectJsonObject);
		return response;

	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildtrainingCenterJsonForReportDb(Connection connection, String databaseName,
			TrainingCenterDetails tcDetails) throws UserDaoException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ReportingDbJSONConstant.TC_ID, tcDetails.getTcId());
		jsonObject.put(ReportingDbJSONConstant.TC_NAME, tcDetails.getTcName());
		jsonObject.put(ReportingDbJSONConstant.CENTER_ADDRESS, tcDetails.getCenterAddress());
		jsonObject.put(ReportingDbJSONConstant.LATITUDE, Double.parseDouble(tcDetails.getLatitude()));
		jsonObject.put(ReportingDbJSONConstant.LONGITUDE, Double.parseDouble(tcDetails.getLongitude()));
		jsonObject.put(ReportingDbJSONConstant.C_DISTRICT, tcDetails.getDistrict());
		String centerInchargeId = tcDetails.getCenterInchargeId();
		User user = iuserDao.getUserByUserId(connection, databaseName, centerInchargeId);
		jsonObject.put(ReportingDbJSONConstant.CIC_NAME, user.getFirstName());
		jsonObject.put(ReportingDbJSONConstant.CIC_PHONE, user.getPhone());
		return jsonObject;

	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildUpdateGradingFieldAuditor(Connection connection, String tanantId,
			PartnerDetails partnerDetails, FormData formData, SurveyResponse surveyResponse) throws UserDaoException,
			SchedulerDaoException, ProjectDaoException, SurveyResponseDaoException, MasterDataManagmentDaoException,
			PartnerDaoException, JsonUtillException, ProjectServiceException, SQLServerException {
		JSONObject response = buildPartnerJsonForReportDb(connection, tanantId, partnerDetails);
		JSONObject scoreJson = buildScorJson(connection, tanantId, partnerDetails, formData);
		response.put(ReportingDbJSONConstant.SCORE, scoreJson);
		JSONObject projectJsonObject = buildProjectJsonForReportDb(connection, tanantId, formData);
		JSONObject formJson = buildFormForFieldAuditor(connection, tanantId, formData, surveyResponse);
		projectJsonObject.put(ReportingDbJSONConstant.FORM, formJson);

		response.put(ReportingDbJSONConstant.PROJECT, projectJsonObject);
		return response;

	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildProjectJsonForReportDb(Connection connection, String databaseName, FormData formData)
			throws ProjectDaoException, JsonUtillException, ProjectServiceException {
		JSONObject jsonObject = new JSONObject();
		String projectId = formData.getProjectId();
		ProjectData project = surveydao.getProjectById(connection, databaseName, projectId);
		jsonObject.put(ReportingDbJSONConstant.PROJECT_NAME, project.getProjectName());
		jsonObject.put(ReportingDbJSONConstant.PROJECT_ID, project.getProjectId());
		return jsonObject;
	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildFormForCIcAndCS(Connection connection, String databaseName, FormData formData,
			SurveyResponse surveyResponse)
			throws ProjectDaoException, UserDaoException, JsonUtillException, ProjectServiceException {
		JSONObject jsonObject = new JSONObject();
		String surveyId = formData.getSurveyId();
		String type = isFromGradingForm(formData) ? ProjectServiceConstant.GRADING_TYPE
				: isFromRatingForm(formData) ? ProjectServiceConstant.RATING_TYPE : null;
		if (type == null) {
			return new JSONObject();
		}
		Survey survey = surveydao.getSurveyById(connection, databaseName, surveyId);
		jsonObject.put(ProjectServiceConstant.FORM_NAME, formData.getFormName());
		jsonObject.put(ReportingDbJSONConstant.FORM_ID, formData.getFormId());
		jsonObject.put(ReportingDbJSONConstant.FORM_TYPE, type);
		jsonObject.put(ReportingDbJSONConstant.FORM_STATUS, false);
		jsonObject.put(ReportingDbJSONConstant.MAX_MARKS, survey.getMaxScore());
		jsonObject.put(ReportingDbJSONConstant.SA_SCORE, surveyResponse.getTotalScore());

//		JSONObject fieldAuditorJsonObject = buildFieldAuditorForReportDb(connection, databaseName, surveyResponse);
//		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR, fieldAuditorJsonObject);
		JSONArray sectionsJsonObject = buildSectionJsonForCISAndCS(survey, surveyResponse);
		jsonObject.put(ReportingDbJSONConstant.SECTIONS, sectionsJsonObject);
		return jsonObject;
	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildFormForFieldAuditor(Connection connection, String databaseName, FormData formData,
			SurveyResponse surveyResponse) throws ProjectDaoException, JsonUtillException, ProjectServiceException,
			UserDaoException, SQLServerException {
		JSONObject jsonObject = new JSONObject();
		String surveyId = formData.getSurveyId();
		String type = isFromGradingForm(formData) ? ProjectServiceConstant.GRADING_TYPE
				: isFromRatingForm(formData) ? ProjectServiceConstant.RATING_TYPE : null;
		if (type == null) {
			return new JSONObject();
		}
		Survey survey = surveydao.getSurveyById(connection, databaseName, surveyId);
		jsonObject.put(ProjectServiceConstant.FORM_NAME, formData.getFormName());
		jsonObject.put(ReportingDbJSONConstant.FORM_ID, formData.getFormId());
		jsonObject.put(ReportingDbJSONConstant.FORM_TYPE, type);
		jsonObject.put(ReportingDbJSONConstant.FORM_STATUS, true);
		jsonObject.put(ReportingDbJSONConstant.MAX_MARKS, survey.getMaxScore());
//		jsonObject.put(ReportingDbJSONConstant.SA_SCORE, 0);
		jsonObject.put(ReportingDbJSONConstant.FA_SCORE, surveyResponse.getTotalScore());
//		JSONObject timeJsonObject = buildTimingJsonForReportDb(connection, databaseName, surveyResponse);
//		jsonObject.put(ReportingDbJSONConstant.TIMING, timeJsonObject);
		JSONObject fieldAuditorJsonObject = buildFieldAuditorForReportDb(connection, databaseName, surveyResponse);
		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR, fieldAuditorJsonObject);
		JSONArray sectionsJsonObject = buildSectionJsonForFieldAuditor(survey, surveyResponse);
		jsonObject.put(ReportingDbJSONConstant.SECTIONS, sectionsJsonObject);
		return jsonObject;
	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONArray buildSectionJsonForCISAndCS(Survey survey, SurveyResponse surveyResponse)
			throws JsonUtillException, ProjectServiceException {
		String surveyData = survey.getSurveyData();
		JSONArray jsonArrayResponse = new JSONArray();
		JSONObject surveyJson = (JSONObject) JSONValue.parse(surveyData);
		JSONArray surveyResponseData = (JSONArray) JSONValue.parse(surveyResponse.getSurveyResponseData());
		JSONArray sectionsArray = (JSONArray) surveyJson.get(ProjectServiceConstant.SECTIONS);
		for (int i = 0; i < sectionsArray.size(); i++) {
			JSONArray parameterArray = new JSONArray();
			JSONObject singleSection = new JSONObject();
			JSONObject jsonObject = (JSONObject) sectionsArray.get(i);
			String sectionId = jsonObject.get(ProjectServiceConstant.SECTION_ID).toString();
			JSONObject singleSurveyResponseSection = getSurveyResponseSection(surveyResponseData, sectionId);
			JSONArray sectionQuesArray = (JSONArray) jsonObject.get(ProjectServiceConstant.SECTION_QUESTIONS);
			for (int j = 0; j < sectionQuesArray.size(); j++) {
				JSONObject singleQuesJson = new JSONObject();
				JSONObject singleQues = (JSONObject) sectionQuesArray.get(j);
				String questionId = singleQues.get(ProjectServiceConstant.QUES_ID).toString();
				JSONObject singleSRQues = getSingleQuestion(singleSurveyResponseSection, questionId);
				JSONObject responseData = (JSONObject) singleSRQues.get(ProjectServiceConstant.RESPONSE_DATA);
				logger.debug("singleSRQues :: " + singleSRQues);
				int faScore = responseData.containsKey(ProjectServiceConstant.OPTION_SCORE)
						? Integer.parseInt(responseData.get(ProjectServiceConstant.OPTION_SCORE).toString())
						: responseData.containsKey(ProjectServiceConstant.SCORE)
								? Integer.parseInt(responseData.get(ProjectServiceConstant.SCORE).toString())
								: 0;
				String parameter = singleQues.get(ProjectServiceConstant.QUES_ID).toString();
				singleQuesJson.put(ReportingDbJSONConstant.PARAMETER_ID, parameter);
				int maxScore = Integer.parseInt(singleQues.get(ProjectServiceConstant.SCORE) + "");

				String saRemark = getRemarkInResponse(singleSRQues);
				logger.debug("saRemark:: " + saRemark);
				singleQuesJson.put(ReportingDbJSONConstant.MAX_MARKS, maxScore);
				singleQuesJson.put(ReportingDbJSONConstant.SA_REMARK, saRemark);
				singleQuesJson.put(ReportingDbJSONConstant.SA_SCORE, faScore);
				parameterArray.add(singleQuesJson);
			}
			singleSection.put(ReportingDbJSONConstant.SECTION_ID, sectionId);
			singleSection.put(ReportingDbJSONConstant.PARAMETERS, parameterArray);
			jsonArrayResponse.add(singleSection);
		}
		return jsonArrayResponse;
	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONArray buildSectionJsonForFieldAuditor(Survey survey, SurveyResponse surveyResponse)
			throws JsonUtillException, ProjectServiceException {
		String surveyData = survey.getSurveyData();
		JSONArray jsonArrayResponse = new JSONArray();
		JSONObject surveyJson = (JSONObject) JSONValue.parse(surveyData);
		JSONArray surveyResponseData = (JSONArray) JSONValue.parse(surveyResponse.getSurveyResponseData());
		JSONArray sectionsArray = (JSONArray) surveyJson.get(ProjectServiceConstant.SECTIONS);
		for (int i = 0; i < sectionsArray.size(); i++) {
			JSONArray parameterArray = new JSONArray();
			JSONObject singleSection = new JSONObject();
			JSONObject jsonObject = (JSONObject) sectionsArray.get(i);
			String sectionId = jsonObject.get(ProjectServiceConstant.SECTION_ID).toString();
			JSONObject singleSurveyResponseSection = getSurveyResponseSection(surveyResponseData, sectionId);
			JSONArray sectionQuesArray = (JSONArray) jsonObject.get(ProjectServiceConstant.SECTION_QUESTIONS);
			for (int j = 0; j < sectionQuesArray.size(); j++) {
				JSONObject singleQuesJson = new JSONObject();
				JSONObject singleQues = (JSONObject) sectionQuesArray.get(j);
				String questionId = singleQues.get(ProjectServiceConstant.QUES_ID).toString();
				JSONObject singleSRQues = getSingleQuestion(singleSurveyResponseSection, questionId);
				JSONObject responseData = (JSONObject) singleSRQues.get(ProjectServiceConstant.RESPONSE_DATA);
				logger.debug("singleSRQues :: " + singleSRQues);
				int faScore = responseData.containsKey(ProjectServiceConstant.OPTION_SCORE)
						? Integer.parseInt(responseData.get(ProjectServiceConstant.OPTION_SCORE).toString())
						: responseData.containsKey(ProjectServiceConstant.SCORE)
								? Integer.parseInt(responseData.get(ProjectServiceConstant.SCORE).toString())
								: 0;
				String parameter = singleQues.get(ProjectServiceConstant.QUES_ID).toString();
				singleQuesJson.put(ReportingDbJSONConstant.PARAMETER_ID, parameter);
//				Integer maxScore = calculateSingleQuesMaxMark(singleQues);
				int maxScore = Integer.parseInt(singleQues.get(ProjectServiceConstant.SCORE) + "");
				String faRemark = getRemarkInResponse(singleSRQues);
				singleQuesJson.put(ReportingDbJSONConstant.MAX_MARKS, maxScore);
				singleQuesJson.put(ReportingDbJSONConstant.FA_REMARK, faRemark);
				singleQuesJson.put(ReportingDbJSONConstant.FA_SCORE, faScore);
				parameterArray.add(singleQuesJson);
			}
			singleSection.put(ReportingDbJSONConstant.SECTION_ID, sectionId);
			singleSection.put(ReportingDbJSONConstant.PARAMETERS, parameterArray);
			jsonArrayResponse.add(singleSection);
		}
		return jsonArrayResponse;
	}

	private String getRemarkInResponse(JSONObject singleQues) {
		String remark = null;
		try {
			logger.debug("singleQues :: " + singleQues);
			remark = JsonUtill.getString(singleQues, SurveyResponseServiceConstant.REMARK);
		} catch (JsonUtillException e) {
			e.printStackTrace();
		}
//		Object remark = singleQues.get(SurveyResponseServiceConstant.REMARK);
		logger.debug("getRemarkInResponse :: " + remark);
		if (remark == null) {
			return "";
		}
		return remark;
	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	private JSONObject getSingleQuestion(JSONObject singleSurveyResponseSection, String questionId) {
		logger.debug(".in getSingleQuestion :: QuestionNo is :" + questionId);
		JSONArray questions = (JSONArray) singleSurveyResponseSection.get(ProjectServiceConstant.SECTION_RESPONSE_DATA);
		for (int i = 0; i < questions.size(); i++) {
			JSONObject jsonObject = (JSONObject) questions.get(i);
			if (jsonObject.get(ProjectServiceConstant.QUES_ID).equals(questionId)) {
				return jsonObject;
			}
		}
		return new JSONObject();
	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	private JSONObject getSurveyResponseSection(JSONArray surveyResponseData, String sectionId) {
		for (int i = 0; i < surveyResponseData.size(); i++) {
			JSONObject singleSection = (JSONObject) surveyResponseData.get(i);
			if (singleSection.get(ProjectServiceConstant.SECTION_ID).equals(sectionId)) {
				return singleSection;
			}
		}
		return new JSONObject();
	}

	/**
	 * Preparing JSON for the reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildFieldAuditorForReportDb(Connection connection, String databaseName,
			SurveyResponse surveyResponse) throws UserDaoException, SQLServerException {
		JSONObject jsonObject = new JSONObject();
		UserSurveyResponseTracking userSurveyResponseTracking = iTrackingDao
				.getSurveyResponseTrackingWithSurveyIdandAuditForId(connection, databaseName,
						surveyResponse.getSurveyId(), surveyResponse.getAuditFor(), surveyResponse.getAuditForId());
		String fieldAuditorId = surveyResponse.getUserId();
		User user = iuserDao.getUserByUserId(connection, databaseName, fieldAuditorId);
		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR_NAME, user.getFirstName());
		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR_PHONE, user.getPhone());
		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR_LOCATION, getCenterLocationInGeography(
				userSurveyResponseTracking.getLatitude(), userSurveyResponseTracking.getLongitude()));
		jsonObject.put(ReportingDbJSONConstant.SEC_FIELD_AUDITOR_NAME, surveyResponse.getSecondaryFieldAuditor());
		jsonObject.put(ReportingDbJSONConstant.FA_START_TIME, userSurveyResponseTracking.getStartTime());
		jsonObject.put(ReportingDbJSONConstant.FA_END_TIME, userSurveyResponseTracking.getEndTime());
		jsonObject.put(ReportingDbJSONConstant.FA_START_DATE, userSurveyResponseTracking.getStartDate());
		jsonObject.put(ReportingDbJSONConstant.FA_END_DATE, userSurveyResponseTracking.getEndDate());
		jsonObject.put(ReportingDbJSONConstant.SIGN_OFF_TIME, userSurveyResponseTracking.getEndTime());
		jsonObject.put(ReportingDbJSONConstant.OTP, surveyResponse.getOtp());
		return jsonObject;
	}

	private String getCenterLocationInGeography(double latitude, double longitude) throws SQLServerException {
		Geography geography = Geography.point(latitude, longitude, ReportingDatabaseQueryConstants.SRID_FOR_GEOGRAPHY);
		logger.debug("Geography in string FA Response : " + geography.toString());
		return geography.toString();

	}

	/**
	 * Preparing timing JSON for the CenterInCharge
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildTimingJsonForCenterInCharge(Connection connection, String databaseName,
			SurveyResponse surveyResponse) {
		UserSurveyResponseTracking userSurveyResponseTracking = iTrackingDao
				.getSurveyResponseTrackingWithSurveyIdandCenterId(connection, databaseName,
						surveyResponse.getSurveyId(), surveyResponse.getCenterId());

		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ReportingDbJSONConstant.START_TIME, userSurveyResponseTracking.getStartTime());
		jsonObject.put(ReportingDbJSONConstant.END_TIME, userSurveyResponseTracking.getEndTime());
		jsonObject.put(ReportingDbJSONConstant.START_DATE, userSurveyResponseTracking.getStartDate());
		jsonObject.put(ReportingDbJSONConstant.END_DATE, userSurveyResponseTracking.getEndDate());
		return jsonObject;
	}

	/**
	 * Preparing timing JSON for the ClientSponsor
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildTimingJsonForClientSponsor(Connection connection, String databaseName,
			SurveyResponse surveyResponse) {
		UserSurveyResponseTracking userSurveyResponseTracking = iTrackingDao
				.getSurveyResponseTrackingWithSurveyIdandPartnerId(connection, databaseName,
						surveyResponse.getSurveyId(), surveyResponse.getPartnerId());

		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ReportingDbJSONConstant.START_TIME, userSurveyResponseTracking.getStartTime());
		jsonObject.put(ReportingDbJSONConstant.END_TIME, userSurveyResponseTracking.getEndTime());
		jsonObject.put(ReportingDbJSONConstant.START_DATE, userSurveyResponseTracking.getStartDate());
		jsonObject.put(ReportingDbJSONConstant.END_DATE, userSurveyResponseTracking.getEndDate());
		return jsonObject;
	}

	/**
	 * Preparing score JSON for the reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildScorJson(Connection connection, String tenantId, PartnerDetails partnerDetails,
			FormData formData) throws SchedulerDaoException, ProjectDaoException, SurveyResponseDaoException,
			MasterDataManagmentDaoException, PartnerDaoException {
		String partnerId = partnerDetails.getPartnerId();
		String projectId = formData.getProjectId();
		List<RatingType> ratingTypeList = iSchedulerDao.getRatingTypeDataByProjectIdAndParterId(connection, tenantId,
				partnerId, projectId);
		double centerRating = calculateCenterRating(connection, tenantId, ratingTypeList, projectId);
		List<GradingType> gradingTypeList = iSchedulerDao.getGradingTypeDataByProjectIdAndPartnerId(connection,
				tenantId, partnerId, projectId);
		double projectGrading = calculateProjectGrading(connection, tenantId, gradingTypeList, projectId);
		double finalAvg = (double) ((centerRating + projectGrading) / 2);
		logger.debug("finalAvg :: " + finalAvg);
		String grade = "";
		if (finalAvg >= 80.0) {
			grade = "A+";
		} else if (finalAvg >= 70.0 && finalAvg <= 79.9) {
			grade = "A";
		} else if (finalAvg >= 50.0 && finalAvg <= 69.9) {
			grade = "B";
		} else {
			grade = "C";
		}
		JSONObject scoreJson = new JSONObject();
		scoreJson.put(ReportingDbJSONConstant.CENTER_RATING, centerRating);
		scoreJson.put(ReportingDbJSONConstant.FINAL_AVG, finalAvg);
		scoreJson.put(ReportingDbJSONConstant.PROJECT_GRADING, projectGrading);
		scoreJson.put(ReportingDbJSONConstant.GRADE, grade);
		return scoreJson;
	}

	/**
	 * Calculating project grading.
	 */
	private double calculateProjectGrading(Connection connection, String tenantId, List<GradingType> projectMappingList,
			String projectId) throws ProjectDaoException, SurveyResponseDaoException, MasterDataManagmentDaoException,
			PartnerDaoException {
		double totalScore = 0;
		double projectGrading = 0;
		for (GradingType gradingType : projectMappingList) {
			String formId = gradingType.getFormId();
			FormData form = surveydao.getFormById(connection, tenantId, formId);
			if (!(form.isPublish() && gradingType.isSelfAssignmentStatus() && gradingType.isAuditStatus())) {
				continue;
			}
			String surveyId = form.getSurveyId();
			Survey survey = surveydao.getSurveyById(connection, tenantId, surveyId);
			totalScore += survey.getMaxScore();
			SurveyResponse FASurveyResponse = surveyResponseDao.getSurveyResponsesBySurveyIdAndAuditForId(connection,
					tenantId, surveyId, ReportServiceConstants.PARTNER, gradingType.getPartnerId());
			projectGrading += FASurveyResponse.getTotalScore();
		}
		if (totalScore == 0.0) {
			return 0.0;
		}
		double projectGradingPercentage = (projectGrading / totalScore) * 100.0;
		return projectGradingPercentage;

	}

	/**
	 * Calculating Center Rating
	 */
	private double calculateCenterRating(Connection connection, String tenantId, List<RatingType> projectMappingList,
			String projectId) throws ProjectDaoException, SurveyResponseDaoException, MasterDataManagmentDaoException,
			PartnerDaoException {
		logger.debug(". in calculateCenterRating ProjectMapping size is  ::" + projectMappingList.size());
		double totalScore = 0;
		double centerRatingScore = 0;
		for (RatingType ratingType : projectMappingList) {
			String formId = ratingType.getFormId();
			FormData form = surveydao.getFormById(connection, tenantId, formId);
			if (!(form.isPublish() && ratingType.isSelfAssignmentStatus() && ratingType.isAuditStatus())) {
				continue;
			}
			String surveyId = form.getSurveyId();
			Survey survey = surveydao.getSurveyById(connection, tenantId, surveyId);
			totalScore += survey.getMaxScore();
			SurveyResponse FASurveyResponse = surveyResponseDao.getSurveyResponsesBySurveyIdAndAuditForId(connection,
					tenantId, surveyId, ReportServiceConstants.TRAINING_CENTER, ratingType.getTcId());
			centerRatingScore += FASurveyResponse.getTotalScore();
		}
		logger.debug("totalScore is " + totalScore + " and centerRatingScore is " + centerRatingScore);
		if (totalScore == 0.0) {
			return 0.0;
		}
		double centerRatingPercentage = (double) ((centerRatingScore / totalScore) * 100.0);
		logger.debug("centerRatingPercentage is ::" + centerRatingPercentage);
		return centerRatingPercentage;
	}

	/**
	 * Preparing partner JSON for reporting tables.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildPartnerJsonForReportDb(Connection connection, String databaseName,
			PartnerDetails partnerDetails) throws UserDaoException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ReportingDbJSONConstant.PIA_NAME, partnerDetails.getPartnerName());
		jsonObject.put(ReportingDbJSONConstant.PARTNER_ID, partnerDetails.getPartnerId());
//		jsonObject.put(ReportingDbJSONConstant.DISTRICT, "");
		String clientSponsorId = partnerDetails.getClientSponsorId();
		User user = iuserDao.getUserByUserId(connection, databaseName, clientSponsorId);
		jsonObject.put(ReportingDbJSONConstant.HEAD_PERSONNEL, user.getFirstName());
		jsonObject.put(ReportingDbJSONConstant.HEAD_PERSON_EMAIL, user.getEmail());
		jsonObject.put(ReportingDbJSONConstant.CONTACT, user.getPhone());
		jsonObject.put(ReportingDbJSONConstant.STATUS, true);
		return jsonObject;
	}

	private boolean isFromGradingForm(FormData formData) {
		List<String> usersRolesList = new ArrayList<String>();
		usersRolesList.add(UserAccessManagementServiceConstants.CLIENT_SPONSOR);
		usersRolesList.add(UserAccessManagementServiceConstants.FIELD_AUDITOR);
		List<String> usersRolesAllowed = formData.getUsersRolesAllowed();
		if (usersRolesAllowed.size() == 2 && usersRolesAllowed.containsAll(usersRolesList)) {
			return true;
		}
		return false;
	}

	private boolean isFromRatingForm(FormData formData) {
		List<String> usersRolesList = new ArrayList<String>();
		usersRolesList.add(UserAccessManagementServiceConstants.CENTER_IN_CHARGE);
		usersRolesList.add(UserAccessManagementServiceConstants.FIELD_AUDITOR);
		List<String> usersRolesAllowed = formData.getUsersRolesAllowed();
		if (usersRolesAllowed.size() == 2 && usersRolesAllowed.containsAll(usersRolesList)) {
			return true;
		}
		return false;
	}

	/**
	 * This method is used to delete the survey response data.
	 * 
	 * @param tanantId           the database name
	 * @param surveyResponseById the id of the surveyResponse
	 * @return response message if the action is success.
	 * @throws SurveyResponseServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deleteSurveyResponseById(String tanantId, String surveyResponseId)
			throws SurveyResponseServiceException {
		try {
			logger.debug("inside deleteSurveyResponseById method of SurveyResponseServiceImpl ");
			validateId(surveyResponseId, SurveyResponseServiceConstant.SURVEY_RESPONSE_ID);
			logger.debug("surveyResponseId ::" + surveyResponseId);
			surveyResponseDao.deleteSurveyResponseById(tanantId, surveyResponseId.trim());
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("msg", ReadPropertiesFile.readResponseProperty("208"));
			return responseJSON.toString();
		} catch (SurveyResponseServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" unable to delete Survey Response " + e.getMessage(), e);
			throw new SurveyResponseServiceException(" unable to delete Survey Response " + e.getMessage(), e);
		}

	}

	/**
	 * This method is used to get the audit data.
	 * 
	 * @param tenantId  the database name
	 * @param surveyId  the id of the survey
	 * @param auditFor  the audit for is training center or partner.
	 * @param tcId      the id of the training center.
	 * @param partnerId the id of the partner.
	 * @return response containing the audit data
	 * @throws SurveyResponseServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getAuditData(String tenantId, String surveyId, String auditFor, String tcId, String partnerId)
			throws SurveyResponseServiceException {
		logger.debug("inside getAuditData method of SurveyResponseServiceImpl ");
		try {
			validateId(surveyId, SurveyResponseServiceConstant.SURVEY_ID);
			validateId(auditFor, SurveyResponseServiceConstant.AUDIT_FOR);
			if (auditFor.equals(SurveyResponseServiceConstant.TRAINING_CENTER)) {
				logger.debug("audit for Training center");
				validateId(tcId, SurveyResponseServiceConstant.TC_ID);
				SurveyResponse surveyResponse = surveyResponseDao.getSurveyResponsesByCenterIdAndSurveyId(tenantId,
						tcId, surveyId);
				if (surveyResponse == null) {
					throw new SurveyResponseServiceException("Survey response not found.");
				}
				int totalScore = surveyResponse.getTotalScore();
				String surveyResponseData = surveyResponse.getSurveyResponseData();
				JSONArray jsonArray = processSurveyResponseData(surveyResponseData);
				JSONObject responseJSON = new JSONObject();
				responseJSON.put(SurveyResponseServiceConstant.TOTAL_SCORE, totalScore);
				responseJSON.put(SurveyResponseServiceConstant.SECTIONS, jsonArray);
				return responseJSON.toString();
			} else if (auditFor.equals(SurveyResponseServiceConstant.PARTNER)) {
				logger.debug("audit for partner");
				validateId(partnerId, SurveyResponseServiceConstant.PARTNER_ID);
				SurveyResponse surveyResponse = surveyResponseDao.getSurveyResponsesByPartnerIdAndSurveyId(tenantId,
						surveyId, partnerId);
				if (surveyResponse == null) {
					throw new SurveyResponseServiceException("Survey response not found.");
				}
				int totalScore = surveyResponse.getTotalScore();
				String surveyResponseData = surveyResponse.getSurveyResponseData();
				JSONArray jsonArray = processSurveyResponseData(surveyResponseData);
				JSONObject responseJSON = new JSONObject();
				responseJSON.put(SurveyResponseServiceConstant.TOTAL_SCORE, totalScore);
				responseJSON.put(SurveyResponseServiceConstant.SECTIONS, jsonArray);
				return responseJSON.toString();
			} else {
				logger.debug("AuditFor '" + auditFor + "' is not a valid type.");
				throw new SurveyResponseServiceException("AuditFor '" + auditFor + "' is not a valid type.");
			}
		} catch (Exception e) {
			logger.error("Unable to get Audit Data, " + e.getMessage(), e);
			throw new SurveyResponseServiceException("Unable to get Audit Data, " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private JSONArray processSurveyResponseData(String surveyResponseData) throws SurveyResponseServiceException {
		JSONArray array = new JSONArray();
		JSONArray surveyResponseObj = (JSONArray) JSONValue.parse(surveyResponseData);
		try {
//			JSONArray jsonArray = JsonUtill.getJsonArray(surveyResponseObj, SurveyResponseServiceConstant.SURVEY_RESPONSE_DATA);
			for (int i = 0; i < surveyResponseObj.size(); i++) {
				JSONObject eachSection = (JSONObject) surveyResponseObj.get(i);
				String sectionId = JsonUtill.getString(eachSection, SurveyResponseServiceConstant.SECTION_ID);
				int sectionScore = JsonUtill.getInt(eachSection, SurveyResponseServiceConstant.SECTION_SCORE);
				JSONObject obj = new JSONObject();
				obj.put(SurveyResponseServiceConstant.SECTION_ID, sectionId);
				obj.put(SurveyResponseServiceConstant.SECTION_SCORE, sectionScore);
				array.add(obj);
			}
		} catch (JsonUtillException e) {
			throw new SurveyResponseServiceException(e);
		}
		return array;
	}

	/**
	 * This method is used to get the survey response data for given surveyId.
	 * 
	 * @param tanantId the database name
	 * @param surveyId the id of the survey
	 * @return response containing the survey response data
	 * @throws SurveyResponseServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getSurveyResponsesBySurveyId(String tanantId, String surveyId) throws SurveyResponseServiceException {
		try {
			logger.debug("inside getSurveyResponsesBySurveyId method of SurveyResponseServiceImpl ");
			validateId(surveyId, SurveyResponseServiceConstant.SURVEY_ID);
			logger.debug("surveyId ::" + surveyId);
			List<SurveyResponse> surveyRsponseList = surveyResponseDao.getSurveyResponsesBySurveyId(tanantId,
					surveyId.trim());
			JSONArray surveyRsponseJSONArray = getJSONFromSurvey(surveyRsponseList);
			JSONObject responseJSON = new JSONObject();
			responseJSON.put(SurveyResponseServiceConstant.SURVEY_RESPONSE, surveyRsponseJSONArray);
			logger.debug("response :: " + responseJSON.toString());
			return responseJSON.toString();
		} catch (Exception e) {
			logger.error(" unable to fetech Survey Response " + e.getMessage(), e);
			throw new SurveyResponseServiceException(" unable to fetech Survey Response " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get the survey response data for given
	 * surveyResponseId.
	 * 
	 * @param tanantId         the database name
	 * @param surveyResponseId the id of the surveyResponse
	 * @return response containing the survey response data
	 * @throws SurveyResponseServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getSurveyResponseBySurveyResponseById(String tanantId, String surveyResponseId)
			throws SurveyResponseServiceException {
		try {
			logger.debug("inside getSurveyResponseBySurveyResponseById method of SurveyResponseServiceImpl ");
			validateId(surveyResponseId, SurveyResponseServiceConstant.SURVEY_RESPONSE_ID);
			logger.debug("surveyId ::" + surveyResponseId);
			SurveyResponse surveyRsponse = surveyResponseDao.getSurveyResponseBySurveyResponseId(tanantId,
					surveyResponseId);
			if (surveyRsponse == null) {
				logger.error("surveyResponse Id doesnt exist");
				throw new SurveyResponseServiceException(" surveyResponse Id doesnt exist ");
			}
			JSONObject surveyRsponseJSONObject = getJSONFromSurvey(surveyRsponse);
			JSONObject responseJSON = new JSONObject();
			responseJSON.put(SurveyResponseServiceConstant.SURVEY_RESPONSE, surveyRsponseJSONObject);
			logger.debug("response :: " + responseJSON.toString());
			return responseJSON.toString();
		} catch (Exception e) {
			logger.error(" unable to fetech Survey Response " + e.getMessage(), e);
			throw new SurveyResponseServiceException(" unable to fetech Survey Response " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get the survey response data of the user and given
	 * surveyId.
	 * 
	 * @param tanantId   the database name
	 * @param surveyId   the id of the survey
	 * @param email      the email of the user.
	 * @param auditFor   the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @return response containing the survey response data
	 * @throws SurveyResponseServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getSurveyResponsesOfUserBySurveyId(String tanantId, String surveyId, String email, String auditFor,
			String auditForId) throws SurveyResponseServiceException {
		try {
			logger.debug("inside getSurveyResponsesOfUserBySurveyId method of SurveyResponseServiceImpl ");
			validateId(surveyId, SurveyResponseServiceConstant.SURVEY_ID);
			User user = iuserDao.getUserByEmail(tanantId, email);
			String userRole = user.getUserRole();
			logger.debug("surveyId ::" + surveyId + " : userRole " + userRole);
			SurveyResponse surveyRsponse = null;
			if (userRole.equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
				surveyRsponse = surveyResponseDao.getSurveyResponsesByCenterIdAndSurveyId(tanantId, user.getCenterId(),
						surveyId.trim());
			} else if (userRole.equals(UserAccessManagementServiceConstants.CLIENT_SPONSOR)) {
				PartnerDetails partnerDetails = ipartnerDao.getPartnerByClientSponsorId(tanantId, user.getUserId());
				surveyRsponse = surveyResponseDao.getSurveyResponsesByPartnerIdAndSurveyId(tanantId, surveyId.trim(),
						partnerDetails.getPartnerId());
			} else if (userRole.equals(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
				if (auditFor.isEmpty() || auditForId.isEmpty()) {
					throw new SurveyResponseServiceException(
							"Invalid Request, [auditFor] and [auditForId] params not fount");
				}
				surveyRsponse = surveyResponseDao.getSurveyResponsesBySurveyIdAndAuditForId(tanantId, surveyId.trim(),
						auditFor, auditForId);
				logger.debug("for field auditor :: " + surveyRsponse);
			}
			if (surveyRsponse == null) {
				logger.error(" surveyRsponse does not exist ");
				throw new SurveyResponseServiceException("surveyRsponse does not exist");
			}
			logger.debug("surveyRsponse ::" + surveyRsponse);
			JSONObject surveyRsponseJSONObject = getJSONFromSurvey(surveyRsponse);
			JSONObject responseJSON = new JSONObject();
			responseJSON.put(SurveyResponseServiceConstant.SURVEY_RESPONSE, surveyRsponseJSONObject);
			logger.debug("response :: " + responseJSON.toString());
			return responseJSON.toString();
		} catch (Exception e) {
			logger.error(" unable to fetech Survey Response " + e.getMessage(), e);
			throw new SurveyResponseServiceException(" unable to fetech Survey Response " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get the survey response data for userId.
	 * 
	 * @param tanantId the database name
	 * @param userId   the id of the user.
	 * @return response containing the survey response data
	 * @throws SurveyResponseServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getSurveyResponsesByUserId(String tanantId, String userId) throws SurveyResponseServiceException {
		try {
			logger.debug("inside getSurveyResponsesByUserId method of SurveyResponseServiceImpl ");
			validateId(userId, SurveyResponseServiceConstant.USER_ID);
			logger.debug("surveyId ::" + userId);
			List<SurveyResponse> surveyRsponseList = surveyResponseDao.getSurveyResponsesByUserId(tanantId, userId);
			JSONArray surveyRsponseJSONArray = getJSONFromSurvey(surveyRsponseList);
			JSONObject responseJSON = new JSONObject();
			responseJSON.put(SurveyResponseServiceConstant.SURVEY_RESPONSE, surveyRsponseJSONArray);
			logger.debug("response :: " + responseJSON.toString());
			return responseJSON.toString();
		} catch (Exception e) {
			logger.error(" unable to fetech Survey Response " + e.getMessage(), e);
			throw new SurveyResponseServiceException(" unable to fetech Survey Response " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private SurveyResponse evaluate(String tanantId, SurveyResponse surveyResponse)
			throws SurveyResponseServiceException, SurveyResponseDaoException, ProjectDaoException, JsonUtillException,
			org.json.simple.parser.ParseException {
		List<Integer> totalScore = new ArrayList<Integer>();
		String surveyResponseId = surveyResponse.getSurveyResponseId();
		logger.debug("surveyResponseId is ::" + surveyResponseId);
		if (surveyResponseId != null && !(surveyResponseId.isEmpty())) {
			SurveyResponse surveyResponseBySurveyResponseById = surveyResponseDao
					.getSurveyResponseBySurveyResponseId(tanantId, surveyResponseId.trim());
			if (surveyResponseBySurveyResponseById == null)
				throw new SurveyResponseServiceException("invalid surveyResponseId");
			if (surveyResponseBySurveyResponseById.isSubmited()) {
				throw new SurveyResponseServiceException(" update operation not allowed once you submitted");
			}
		}

		String surveyId = surveyResponse.getSurveyId();
		Survey survey = surveydao.getSurveyById(tanantId, surveyId);
		if (survey == null) {
			throw new SurveyResponseServiceException("unable to find SurveyId");
		}
		surveyResponse.setMaxMarks(survey.getMaxScore());
		JSONObject surveyJson = (JSONObject) JSONValue.parseWithException(survey.getSurveyData());
		logger.debug("fdfdsfdf " + surveyJson);
		JSONArray surveyJsonArray = JsonUtill.getJsonArray(surveyJson, ProjectServiceConstant.SECTIONS);
		JSONArray surveyResponseDataJsonArray = (JSONArray) JSONValue
				.parseWithException(surveyResponse.getSurveyResponseData());
		// section Level loop
		for (int i = 0; i < surveyResponseDataJsonArray.size(); i++) {
			// response single section
			List<Integer> listOfOptionWeightage = new ArrayList<Integer>();
			JSONObject section = (JSONObject) surveyResponseDataJsonArray.get(i);
			JSONArray sectionQuestions = JsonUtill.getJsonArray(section, ProjectServiceConstant.SECTION_RESPONSE_DATA);
			String responseSectionId = JsonUtill.getString(section, ProjectServiceConstant.SECTION_ID);
			JSONObject surveySingleSection = getQuestionForSectionId(responseSectionId, surveyJsonArray);
			logger.debug("current survey  section is ::" + surveySingleSection);
			for (int j = 0; j < sectionQuestions.size(); j++) {
				// iterate over questionResponses
				JSONObject singleResponsequestion = (JSONObject) sectionQuestions.get(j);
				int singleQueScore = calculateQueScore(singleResponsequestion, surveySingleSection);
				logger.debug("single ques score is " + singleQueScore);
				listOfOptionWeightage.add(singleQueScore);
			}
			// caluclate section score
			int sectionScore = listOfOptionWeightage.stream().mapToInt(Integer::intValue).sum();
			section.put(ProjectServiceConstant.SECTION_SCORE, sectionScore);
			totalScore.add(sectionScore);
		}
		int sumOfScectionScore = totalScore.stream().mapToInt(Integer::intValue).sum();
		logger.debug("total score is " + sumOfScectionScore);
		surveyResponse.setTotalScore(sumOfScectionScore);
		surveyResponse.setSurveyResponseData(surveyResponseDataJsonArray.toJSONString());
		return surveyResponse;
	}

	private void updateSurveyResponseTracking(String tanantId, SurveyResponse surveyResponse) {
		DateTimeFormatter dateFormate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeFormate = DateTimeFormatter.ofPattern("hh:mm:ss");
		String responderType = surveyResponse.getResponderType();
		if (responderType.equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
			iTrackingDao.updateUserSurveryResponseTrackingForTrainingCenter(tanantId,
					LocalDate.now().format(dateFormate), LocalTime.now().format(timeFormate),
					surveyResponse.getSurveyId(), surveyResponse.getCenterId());
		} else if (responderType.equals(UserAccessManagementServiceConstants.CLIENT_SPONSOR)) {
			iTrackingDao.updateUserSurveryResponseTrackingForPartner(tanantId, LocalDate.now().format(dateFormate),
					LocalTime.now().format(timeFormate), surveyResponse.getSurveyId(), surveyResponse.getPartnerId());
		} else if (responderType.equals(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
			iTrackingDao.updateUserSurveryResponseTrackingForFieldAuditor(tanantId, LocalDate.now().format(dateFormate),
					LocalTime.now().format(timeFormate), surveyResponse.getSurveyId(), surveyResponse.getAuditFor(),
					surveyResponse.getAuditForId());
		}

	}

	private JSONObject getQuestionForSectionId(String responseSectionId, JSONArray surveyJsonArray)
			throws SurveyResponseServiceException, JsonUtillException {
		for (int i = 0; i < surveyJsonArray.size(); i++) {
			JSONObject singleSection = (JSONObject) surveyJsonArray.get(i);
			String sectionId = JsonUtill.getString(singleSection, ProjectServiceConstant.SECTION_ID);
			if (sectionId.equalsIgnoreCase(responseSectionId)) {
				return singleSection;
			}
		}
		throw new SurveyResponseServiceException("invalid sectionId");
	}

	private int calculateQueScore(JSONObject singleResponsequestion, JSONObject singleSurveySection)
			throws JsonUtillException, SurveyResponseServiceException {
		JSONArray surveyQuestions = JsonUtill.getJsonArray(singleSurveySection,
				ProjectServiceConstant.SECTION_QUESTIONS);
		for (int j = 0; j < surveyQuestions.size(); j++) {
			// iterate over questions
			JSONObject surveySingleQues = (JSONObject) surveyQuestions.get(j);
			String responseQueId = JsonUtill.getString(singleResponsequestion, ProjectServiceConstant.QUES_ID);
			String surveyQueId = JsonUtill.getString(surveySingleQues, ProjectServiceConstant.QUES_ID);
			String isResponded = JsonUtill.getString(singleResponsequestion,
					SurveyResponseServiceConstant.IS_RESPONDED);
			if (responseQueId.equals(surveyQueId) && isResponded.equalsIgnoreCase(SurveyResponseServiceConstant.YES)) {
				logger.debug("question Id's Matches" + responseQueId);
				JSONObject queMetaDataJson = JsonUtill.getJsonObject(surveySingleQues,
						ProjectServiceConstant.QUES_META_DATA);
				String quesType = JsonUtill.getString(queMetaDataJson, ProjectServiceConstant.QUESTION_TYPE);
				logger.debug("quesType is:: " + quesType);
				switch (quesType) {
				case ProjectServiceConstant.MULTIPLE_CHOICE:
					int optionBasedQues = optionBasedQues(surveySingleQues, singleResponsequestion);
					return optionBasedQues;
				case ProjectServiceConstant.OPEN_ENDED:
					int openEnded = openEnded(surveySingleQues, singleResponsequestion);
					return openEnded;
				case ProjectServiceConstant.DROP_DOWN:
					int optionBasedQuesScore = optionBasedQues(surveySingleQues, singleResponsequestion);
					return optionBasedQuesScore;
				case ProjectServiceConstant.CHECK_BOX:
					int checkBox = checkBox(surveySingleQues, singleResponsequestion);
					return checkBox;
				case ProjectServiceConstant.DEFAULT:
					logger.error("no such question type");
					throw new SurveyResponseServiceException("invalid Question Type");
				}
			}
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	private int checkBox(JSONObject surveySingleQuestions, JSONObject surveyResponseQues) throws JsonUtillException {
		int toSumTotalOptions = 0;
		JSONObject responseDataJsonObject = JsonUtill.getJsonObject(surveyResponseQues,
				SurveyResponseServiceConstant.RESPONSE_DATA);
		JSONArray surveyResponseOptionId = JsonUtill.getJsonArray(responseDataJsonObject,
				SurveyResponseServiceConstant.OPTION_ID);
		JSONObject surveyQuestionData = JsonUtill.getJsonObject(surveySingleQuestions,
				ProjectServiceConstant.QUES_DATA);
		JSONArray surveyQuestionOptions = JsonUtill.getJsonArray(surveyQuestionData, ProjectServiceConstant.OPTIONS);
		int optionWeightage = 0;
		// this loops iterates through options he/she selected in checkBox
		for (int i = 0; i < surveyResponseOptionId.size(); i++) {
			String optionId = (String) surveyResponseOptionId.get(i);
			logger.debug("option id is " + optionId);
			// this loop iterate over survey Question options
			for (int j = 0; j < surveyQuestionOptions.size(); j++) {
				JSONObject singleOption = (JSONObject) surveyQuestionOptions.get(j);
				String surveyOptionId = JsonUtill.getString(singleOption, ProjectServiceConstant.OPTION_ID);
				if (surveyOptionId.equals(optionId)) {
					logger.debug("option Id's are same same  " + surveyOptionId);
					optionWeightage = JsonUtill.getInt(singleOption, ProjectServiceConstant.OPTION_WEIGHTAGE);
					logger.debug("and option weightage for the option " + surveyOptionId + " is " + optionWeightage);
					toSumTotalOptions += optionWeightage;
				}
			}
		}
		logger.debug("total of the option selected is " + toSumTotalOptions);
		responseDataJsonObject.put(SurveyResponseServiceConstant.OPTION_SCORE, toSumTotalOptions);
		return toSumTotalOptions;
	}

	@SuppressWarnings("unchecked")
	private int openEnded(JSONObject surveySingleQuestions, JSONObject surveyResponseQues) throws JsonUtillException {
		JSONObject surveyQuestionData = JsonUtill.getJsonObject(surveySingleQuestions,
				ProjectServiceConstant.QUES_DATA);
		int weightage = JsonUtill.getInt(surveyQuestionData, ProjectServiceConstant.WEIGHTAGE);
		JSONObject responseData = JsonUtill.getJsonObject(surveyResponseQues,
				SurveyResponseServiceConstant.RESPONSE_DATA);
		responseData.put(ProjectServiceConstant.SCORE, weightage);
		return weightage;
	}

	@SuppressWarnings("unchecked")
	private int optionBasedQues(JSONObject surveySingleQuestions, JSONObject surveyResponseQues)
			throws JsonUtillException {
		logger.debug("in option base ques");
		JSONObject responseDataJsonObject = JsonUtill.getJsonObject(surveyResponseQues,
				SurveyResponseServiceConstant.RESPONSE_DATA);
		String surveyResponseOptionId = JsonUtill.getString(responseDataJsonObject,
				SurveyResponseServiceConstant.OPTION_ID);
		JSONObject surveyQuestionData = JsonUtill.getJsonObject(surveySingleQuestions,
				ProjectServiceConstant.QUES_DATA);
		JSONArray surveyQuestionOptions = JsonUtill.getJsonArray(surveyQuestionData, ProjectServiceConstant.OPTIONS);
		int optionWeightage = 0;
		// this loop is to find correct option and get optionWeightage
		for (int j = 0; j < surveyQuestionOptions.size(); j++) {
			JSONObject singleOption = (JSONObject) surveyQuestionOptions.get(j);
			String surveyOptionId = JsonUtill.getString(singleOption, ProjectServiceConstant.OPTION_ID);
			if (surveyOptionId.equals(surveyResponseOptionId)) {
				logger.debug("option Id's are same same  " + surveyOptionId);
				optionWeightage = JsonUtill.getInt(singleOption, ProjectServiceConstant.OPTION_WEIGHTAGE);
				logger.debug("and option weightage for the option " + surveyOptionId + " is " + optionWeightage);
				responseDataJsonObject.put(SurveyResponseServiceConstant.OPTION_SCORE, optionWeightage);
				return optionWeightage;
			}
		}
		return optionWeightage;
	}

	private SurveyResponse getSurveyResponseFromRequestJSON(JSONObject requestJSON)
			throws SurveyResponseServiceException {
		try {
			SurveyResponse surveyResponse = new SurveyResponse();
			String responderType = JsonUtill.getString(requestJSON, SurveyResponseServiceConstant.RESPONDER_TYPE)
					.trim();

//			String saveTime = JsonUtill.getString(requestJSON, SurveyResponseServiceConstant.SAVE_TIME).trim();
//			String submitTime = JsonUtill.getString(requestJSON, SurveyResponseServiceConstant.SUBMIT_TIME).trim();
			String surveyId = JsonUtill.getString(requestJSON, SurveyResponseServiceConstant.SURVEY_ID).trim();
			boolean isSubmited = JsonUtill.getBoolean(requestJSON, SurveyResponseServiceConstant.IS_SUBMITED);
			JSONArray surveyResponseData = JsonUtill.getJsonArray(requestJSON,
					SurveyResponseServiceConstant.SURVEY_RESPONSE_DATA);

			String secondaryFieldAuditor = null;
			String partnerId = null;
			String centerId = null;
			String auditFor = null;
			String auditForId = null;

			if (responderType.equals(UserAccessManagementServiceConstants.CLIENT_SPONSOR)) {
				partnerId = JsonUtill.getString(requestJSON, SurveyResponseServiceConstant.PARTNER_ID).trim();
			} else if (responderType.equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
				centerId = JsonUtill.getString(requestJSON, SurveyResponseServiceConstant.CENTER_ID).trim();
			} else if (responderType.equals(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
				auditFor = JsonUtill.getString(requestJSON, SurveyResponseServiceConstant.AUDIT_FOR).trim();
				auditForId = JsonUtill.getString(requestJSON, SurveyResponseServiceConstant.AUDIT_FOR_ID).trim();
				secondaryFieldAuditor = requestJSON
						.containsKey(SurveyResponseServiceConstant.SECONDARY_FIELD_AUDITOR_NAME)
								? requestJSON.get(SurveyResponseServiceConstant.SECONDARY_FIELD_AUDITOR_NAME).toString()
								: null;
			} else {
				throw new SurveyResponseServiceException("Invalid responseType");
			}
			if (requestJSON.containsKey(SurveyResponseServiceConstant.JWT_TOKEN)) {
				String jwtToken = JsonUtill.getString(requestJSON, SurveyResponseServiceConstant.JWT_TOKEN).trim();
				surveyResponse.setJwtToken(jwtToken);
			}
			surveyResponse.setAuditFor(auditFor);
			surveyResponse.setAuditForId(auditForId);
			surveyResponse.setSecondaryFieldAuditor(secondaryFieldAuditor);
			surveyResponse.setCenterId(centerId);
			surveyResponse.setPartnerId(partnerId);

			surveyResponse.setResponderType(responderType);
//			surveyResponse.setSaveTime(getTimeMiliSecond(saveTime));
//			surveyResponse.setSubmitTime(getTimeMiliSecond(submitTime));
			surveyResponse.setSurveyId(surveyId);
			surveyResponse.setSurveyResponseData(surveyResponseData.toJSONString());
			surveyResponse.setSubmited(isSubmited);
			return surveyResponse;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new SurveyResponseServiceException("invalid request formate ::" + e.getMessage(), e);
		}
	}

	private long getTimeMiliSecond(String time) throws ParseException {
		if (time == null || time.isEmpty()) {
			return 0;
		}
		SimpleDateFormat format = new SimpleDateFormat(SurveyResponseServiceConstant.TIME_FORMATE);
		Date date = format.parse(time);
		return date.getTime();
	}

	private String getStringFormateTimeFromMiliSecond(long time) {
		if (time == 0) {
			return "";
		}
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat(SurveyResponseServiceConstant.TIME_FORMATE);
		return format.format(date);
	}

	@SuppressWarnings("unchecked")
	private JSONArray getJSONFromSurvey(List<SurveyResponse> surveyResponses)
			throws org.json.simple.parser.ParseException {
		JSONArray jsonArray = new JSONArray();
		for (SurveyResponse surveyResponse : surveyResponses) {
			JSONObject surveyResponseJSON = getJSONFromSurvey(surveyResponse);
			jsonArray.add(surveyResponseJSON);
		}

		return jsonArray;

	}

	@SuppressWarnings("unchecked")
	private JSONObject getJSONFromSurvey(SurveyResponse surveyResponse) throws org.json.simple.parser.ParseException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(SurveyResponseServiceConstant.RESPONDER_TYPE, surveyResponse.getResponderType());
		jsonObject.put(SurveyResponseServiceConstant.SAVE_TIME,
				getStringFormateTimeFromMiliSecond(surveyResponse.getSaveTime()));
		jsonObject.put(SurveyResponseServiceConstant.SUBMIT_TIME,
				getStringFormateTimeFromMiliSecond(surveyResponse.getSubmitTime()));
		jsonObject.put(SurveyResponseServiceConstant.SURVEY_RESPONSE_ID, surveyResponse.getSurveyResponseId());
		jsonObject.put(SurveyResponseServiceConstant.USER_ID, surveyResponse.getUserId());

		jsonObject.put(SurveyResponseServiceConstant.SURVEY_ID, surveyResponse.getSurveyId());
		jsonObject.put(SurveyResponseServiceConstant.TOTAL_SCORE, surveyResponse.getTotalScore());
		jsonObject.put(SurveyResponseServiceConstant.IS_SUBMITED, surveyResponse.isSubmited());

		jsonObject.put(SurveyResponseServiceConstant.AUDIT_FOR, surveyResponse.getAuditFor());
		jsonObject.put(SurveyResponseServiceConstant.AUDIT_FOR_ID, surveyResponse.getAuditForId());
		jsonObject.put(SurveyResponseServiceConstant.CENTER_ID, surveyResponse.getCenterId());
		jsonObject.put(SurveyResponseServiceConstant.PARTNER_ID, surveyResponse.getPartnerId());

		JSONArray jsonArray = (JSONArray) JSONValue.parseWithException(surveyResponse.getSurveyResponseData());
		jsonObject.put(SurveyResponseServiceConstant.SURVEY_RESPONSE_DATA, jsonArray);
		return jsonObject;
	}

	private void validateId(String id, String keyName) throws SurveyResponseServiceException {
		if (id == null || id.isEmpty()) {
			throw new SurveyResponseServiceException(" invalid/empty " + keyName);
		}
	}

	/**
	 * This method is used to send the otp to the user when submitting the survey.
	 * 
	 * @param userEmail  the email of the user.
	 * @param tenantId   the database name
	 * @param auditFor   the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @return response if otp send is success.
	 * @throws SurveyResponseServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String sendOtp(String userEmail, String tenantId, String auditFor, String auditForId)
			throws SurveyResponseServiceException {
		logger.debug(" inside sendOtp of loginService ");
		try {
			logger.debug(" userEmail :: " + userEmail);
			User user = iuserDao.getUserByEmail(tenantId, userEmail);
			if (user == null) {
				throw new UserLoginServiceException("unable to find user data");
			}
			logger.debug("user ::" + user.toString());
//			String otp = String.valueOf(UUID.randomUUID()).substring(0, 6);
			String otp = "654321";
			logger.debug(" user Otp :: " + otp);
			Integer expirty = getExpirty();
			logger.debug("expirty :: " + expirty);
			long expirtyTime = getExpritionTime(expirty);
			UserOtp userOtp = new UserOtp(otp, expirtyTime);
			if (user.getUserRole().equals(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
				validateId(auditFor, SurveyResponseServiceConstant.AUDIT_FOR);
				validateId(auditForId, SurveyResponseServiceConstant.AUDIT_FOR_ID);
				if (auditFor.equals(SurveyResponseServiceConstant.TRAINING_CENTER)) {
					TrainingCenterDetails tcDetails = ipartnerDao.getTrainingCenterDetailsByTcId(tenantId, auditForId);
					if (tcDetails == null) {
						throw new SurveyResponseServiceException(" Invalid auditForId");
					}
					User centerInchargeUser = iuserDao.getUserByUserId(tenantId, tcDetails.getCenterInchargeId());
					iuserDao.updateUserOtpByEmail(tenantId, centerInchargeUser.getEmail(), userOtp);
//					smsService.sendOtpFieldAuditorSubmission(centerInchargeUser, userOtp.getUserOtp());

				} else if (auditFor.equals(SurveyResponseServiceConstant.PARTNER)) {
					PartnerDetails partnerDetails = ipartnerDao.getPartnerById(tenantId, auditForId);
					if (partnerDetails == null) {
						throw new SurveyResponseServiceException(" Invalid auditForId");
					}
					String clientSponsorId = partnerDetails.getClientSponsorId();
					User clientSponsorUser = iuserDao.getUserByUserId(tenantId, clientSponsorId);
					iuserDao.updateUserOtpByEmail(tenantId, clientSponsorUser.getEmail(), userOtp);
//					smsService.sendOtpFieldAuditorSubmission(clientSponsorUser, userOtp.getUserOtp());

				} else {
					throw new SurveyResponseServiceException(" Invalid auditFor");
				}
			}
			if (user.getUserRole().equals(UserAccessManagementServiceConstants.CLIENT_SPONSOR)
					|| user.getUserRole().equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
				iuserDao.updateUserOtpByEmail(tenantId, userEmail, userOtp);
//				smsService.sendOtpSelfAssesmentSubmission(user, otp);
			}
			JSONObject reponseMessgae = new JSONObject();
			reponseMessgae.put(UserLoginServiceConstant.MESSGAE, ReadPropertiesFile.readResponseProperty("220"));
			JSONObject response = new JSONObject();
			response.put(UserLoginServiceConstant.RESPONSE, reponseMessgae);
			return response.toString();
		} catch (Exception e) {
			logger.error(" unable to send otp to user : " + e.getMessage(), e);
			throw new SurveyResponseServiceException(e.getMessage(), e);
		}

	}

	private int getExpirty() {
		Integer minutes = 4;
		try {
			String expirty = ReadPropertiesFile.readRequestProperty(UserLoginServiceConstant.OTP_EXPIRY);
			minutes = Integer.parseInt(expirty);
		} catch (Exception e) {
		}
		return minutes;
	}

	private long getExpritionTime(Integer minutes) {
		logger.debug(".inside getDaysFromString ");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minutes);
		long expTime = calendar.getTimeInMillis();
		return expTime;
	}

	/**
	 * This method is used to verify the otp of the user when submitting the survey.
	 * 
	 * @param userEmail  the email of the user.
	 * @param otp        otp entered by the user.
	 * @param tenantId   the database name
	 * @param auditFor   the audit for is training center or partner.
	 * @param auditForId the audit for Id is training center id or partner id.
	 * @return response if otp entered is correct.
	 * @throws SurveyResponseServiceException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String verifyOtp(String userEmail, String otp, String tenantId, String auditFor, String auditForId)
			throws SurveyResponseServiceException {
		try {
			validateId(otp, SurveyResponseServiceConstant.OTP);
			logger.debug(" inside verifyOtp ..");
			User user = iuserDao.getUserByEmail(tenantId, userEmail);
			if (user.getUserRole().equals(UserAccessManagementServiceConstants.FIELD_AUDITOR)) {
				validateId(auditFor, SurveyResponseServiceConstant.AUDIT_FOR);
				validateId(auditForId, SurveyResponseServiceConstant.AUDIT_FOR_ID);
				if (auditFor.equals(SurveyResponseServiceConstant.TRAINING_CENTER)) {
					TrainingCenterDetails tcDetails = ipartnerDao.getTrainingCenterDetailsByTcId(tenantId, auditForId);
					if (tcDetails == null) {
						throw new SurveyResponseServiceException(" Invalid auditForId");
					}
					User centerInchargeUser = iuserDao.getUserByUserId(tenantId, tcDetails.getCenterInchargeId());
					userEmail = centerInchargeUser.getEmail();
				} else if (auditFor.equals(SurveyResponseServiceConstant.PARTNER)) {
					PartnerDetails partnerDetails = ipartnerDao.getPartnerById(tenantId, auditForId);
					if (partnerDetails == null) {
						throw new SurveyResponseServiceException(" Invalid auditForId");
					}
					String clientSponsorId = partnerDetails.getClientSponsorId();
					User clientSponsorUser = iuserDao.getUserByUserId(tenantId, clientSponsorId);
					userEmail = clientSponsorUser.getEmail();
				} else {
					throw new SurveyResponseServiceException(" Invalid auditFor");
				}
			}
			boolean isOtpValid = false;
			UserOtp userOtp = iuserDao.getUserOtpByEmail(tenantId, userEmail);
			String validOtp = userOtp.getUserOtp();
			if (validOtp != null) {
				if (validOtp.equals(otp)) {
					logger.info(" user otp is valid checking expiry ");
					long expiry = userOtp.getExpiryDate();
					long currentTime = new Date().getTime();
					if (currentTime <= expiry) {
						logger.info(" otp is valid successfully...");
						iuserDao.updateUserOtpByEmail(tenantId, userEmail, new UserOtp());
						isOtpValid = true;
					}
				}
			}
			if (!isOtpValid) {
				throw new SurveyResponseServiceException(" invalid or otp expire");
			}
//			tokenBuilder.buildOneTimeAccessToken(user, tenantId);
			String jwtToken = tokenBuilder.buildJwtTokenForSubmissionOfSurvey(user, otp);
			JSONObject response = new JSONObject();
			response.put(UserLoginServiceConstant.RESPONSE, "verification success");
			response.put(UserLoginServiceConstant.JWT_TOKEN, jwtToken);
			return response.toString();
		} catch (Exception e) {
			logger.error(" unable to verify otp : " + e.getMessage(), e);
			throw new SurveyResponseServiceException(e.getMessage(), e);
		}

	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public String deleteSurveyResponseBySurveyId(String tenantId, String surveyId)
//			throws SurveyResponseServiceException {
//		try {
//			logger.debug("inside deleteSurveyResponseById method of SurveyResponseServiceImpl ");
//			validateId(surveyId, SurveyResponseServiceConstant.SURVEY_ID);
//			logger.debug("surveyResponseId ::" + surveyId);
//			surveyResponseDao.deleteSurveyResponseBySurveyId(tenantId, surveyId.trim());
//			JSONObject responseJSON = new JSONObject();
//			responseJSON.put("msg", ReadPropertiesFile.readResponseProperty("208"));
//			return responseJSON.toString();
//		} catch (SurveyResponseServiceException e) {
//			throw e;
//		} catch (Exception e) {
//			logger.error(" unable to delete Survey Response " + e.getMessage(), e);
//			throw new SurveyResponseServiceException(" unable to delete Survey Response " + e.getMessage(), e);
//		}
//	}

}
