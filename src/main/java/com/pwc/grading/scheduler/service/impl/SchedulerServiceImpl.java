package com.pwc.grading.scheduler.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.masterdata.dao.exception.MasterDataManagmentDaoException;
import com.pwc.grading.partner.dao.IPartnerDao;
import com.pwc.grading.partner.dao.exception.PartnerDaoException;
import com.pwc.grading.partner.model.PartnerDetails;
import com.pwc.grading.partner.model.TrainingCenterDetails;
import com.pwc.grading.project.dao.IProjectDao;
import com.pwc.grading.project.dao.exception.ProjectDaoException;
import com.pwc.grading.project.model.FormData;
import com.pwc.grading.project.model.ProjectData;
import com.pwc.grading.project.service.ProjectServiceConstant;
import com.pwc.grading.reportingdb.ReportingDBService;
import com.pwc.grading.reportingdb.assigner.UpdateGradingFieldAuditorDetailsJobAssigner;
import com.pwc.grading.reportingdb.assigner.UpdateRatingFieldAuditorDetailsJobAssigner;
import com.pwc.grading.reportingdb.constant.ReportingDbJSONConstant;
import com.pwc.grading.scheduler.dao.ISchedulerDao;
import com.pwc.grading.scheduler.dao.exception.SchedulerDaoException;
import com.pwc.grading.scheduler.model.GradingType;
import com.pwc.grading.scheduler.model.RatingType;
import com.pwc.grading.scheduler.model.SchedulerMapping;
import com.pwc.grading.scheduler.service.ISchedulerService;
import com.pwc.grading.scheduler.service.SchedulerServiceConstants;
import com.pwc.grading.scheduler.service.exception.SchedulerServiceException;
import com.pwc.grading.surveyresponse.dao.ISurveyResponseDao;
import com.pwc.grading.surveyresponse.dao.exception.SurveyResponseDaoException;
import com.pwc.grading.surveyresponse.model.SurveyResponse;
import com.pwc.grading.user.dao.IUserDao;
import com.pwc.grading.user.dao.exception.UserDaoException;
import com.pwc.grading.user.model.User;
import com.pwc.grading.user.service.registration.UserAccessManagementServiceConstants;
import com.pwc.grading.util.JsonUtill;
import com.pwc.grading.util.exception.JsonUtillException;

/**
 * SchedulerServiceImpl class is an service class used to assign audits for
 * field auditor to rating and grading Forms
 * 
 * @author Reactiveworks-21
 *
 */
@Singleton
public class SchedulerServiceImpl implements ISchedulerService {
	private static final Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);
	@Inject
	private ISurveyResponseDao surveyResponseDao;
	@Inject
	private IProjectDao iSurveyDao;
	@Inject
	private IUserDao iUserDao;
	@Inject
	private ISchedulerDao iSchedulerDao;
	@Inject
	private IPartnerDao ipartnerDao;

	/**
	 * This method is used to update the scheduler.
	 * @param tenantId the database name.
	 * @param requestBody the json response which has the scheduler data.
	 * @param schedulerType it can be either ratingForm or gradingForm
	 * @return the response message of this method.
	 * @throws SchedulerServiceException if any exception occurs when performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String updateScheduler(String databaseName, String requestBody, String schedulerType)
			throws SchedulerServiceException {
		try {
			logger.debug(".in updateScheduler");
			JSONObject requestJson = (JSONObject) JSONValue.parseWithException(requestBody);
			SchedulerMapping schdulerMappping = buildSchedulerMappingFromJson(databaseName, requestJson, schedulerType);
			checkingConditions(databaseName, schdulerMappping, schedulerType);
			logger.debug("conditions are checked");
			logger.debug("Scheduler type is ::" + schedulerType);
			if (schedulerType.equals(SchedulerServiceConstants.GRADING_TYPE)) {
				// trigger the update Grading FA
				GradingType gradingType = iSchedulerDao.getGradingTypeDataByPartnerIdProjectIdAndFormId(databaseName,
						schdulerMappping.getPartnerId(), schdulerMappping.getProjectId(), schdulerMappping.getFormId());
				String oldFA = gradingType.getFieldAuditorId();

				iSchedulerDao.updateGradingTypeData(databaseName, schdulerMappping);

				if (ReportingDBService.ENABLED && ((oldFA == null && schdulerMappping.getFieldAuditorId() != null)
						|| (oldFA != null && !(oldFA.equals(schdulerMappping.getFieldAuditorId()))))) {
					logger.debug("User is Changing the fieldAuditor For grading Type");
					String fieldAuditorId = schdulerMappping.getFieldAuditorId();
					User user = iUserDao.getUserByUserId(databaseName, fieldAuditorId);
					Map<String, String> updateGradingFA = new HashMap<String, String>();
					updateGradingFA.put(ReportingDbJSONConstant.PROJECT_ID, gradingType.getProjectId());
					updateGradingFA.put(ReportingDbJSONConstant.PARTNER_ID, gradingType.getPartnerId());
					updateGradingFA.put(ReportingDbJSONConstant.FORM_ID, gradingType.getFormId());
					JSONObject json = new JSONObject(updateGradingFA);
					Map<String, String> faDetails = new HashMap<String, String>();
					faDetails.put(ReportingDbJSONConstant.FIELD_AUDITOR_NAME,
							user.getFirstName() + " " + user.getLastName());
					faDetails.put(ReportingDbJSONConstant.FIELD_AUDITOR_PHONE, user.getPhone());
					json.put(ReportingDbJSONConstant.FIELD_AUDITOR, new JSONObject(faDetails));
					logger.debug("Trigger FaDetais:: " + json);
					UpdateGradingFieldAuditorDetailsJobAssigner assigner = new UpdateGradingFieldAuditorDetailsJobAssigner();
					assigner.assignUpdateFADetailsJobToDatabase(databaseName, json.toJSONString());
				}

			} else if (schedulerType.equals(SchedulerServiceConstants.RATING_TYPE)) {

				RatingType ratingType = iSchedulerDao.getRatingTypeDataByProjectIdParterIdFormIdAndTcId(databaseName,
						schdulerMappping.getPartnerId(), schdulerMappping.getProjectId(), schdulerMappping.getFormId(),
						schdulerMappping.getTcId());
				String oldFA = ratingType.getFieldAuditorId();

				iSchedulerDao.updateRatingTypeData(databaseName, schdulerMappping);

				if (ReportingDBService.ENABLED && ((oldFA == null && schdulerMappping.getFieldAuditorId() != null)
						|| (oldFA != null && !(oldFA.equals(schdulerMappping.getFieldAuditorId()))))) {
					logger.debug("User is Changing the fieldAuditor For rating Type");
					String fieldAuditorId = schdulerMappping.getFieldAuditorId();
					User user = iUserDao.getUserByUserId(databaseName, fieldAuditorId);
					Map<String, String> updateGradingFA = new HashMap<String, String>();
					updateGradingFA.put(ReportingDbJSONConstant.PROJECT_ID, ratingType.getProjectId());
					updateGradingFA.put(ReportingDbJSONConstant.PARTNER_ID, ratingType.getPartnerId());
					updateGradingFA.put(ReportingDbJSONConstant.FORM_ID, ratingType.getFormId());
					updateGradingFA.put(ReportingDbJSONConstant.TC_ID, ratingType.getTcId());
					JSONObject json = new JSONObject(updateGradingFA);
					Map<String, String> faDetails = new HashMap<String, String>();
					faDetails.put(ReportingDbJSONConstant.FIELD_AUDITOR_NAME,
							user.getFirstName() + " " + user.getLastName());
					faDetails.put(ReportingDbJSONConstant.FIELD_AUDITOR_PHONE, user.getPhone());
					json.put(ReportingDbJSONConstant.FIELD_AUDITOR, new JSONObject(faDetails));
					logger.debug("Trigger FaDetais:: " + json);
					UpdateRatingFieldAuditorDetailsJobAssigner assigner = new UpdateRatingFieldAuditorDetailsJobAssigner();
					assigner.assignUpdateFADetailsJobToDatabase(databaseName, json.toJSONString());
				}
			}
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put(SchedulerServiceConstants.RESPONSE_STATUS, "Scheduler update successfully");
			return jsonResponse.toString();
		} catch (Exception e) {
			logger.error("got exception unable to update scheduler");
			throw new SchedulerServiceException("unable to update scheduler ::" + e.getMessage());
		}

	}

//	private void validateDeadLine(String databaseName, SchedulerMapping schdulerMappping)
//			throws SchedulerServiceException {
//		logger.debug("### Validating self assessment deadline is not after project's end date...");
////		String projectId2 = schdulerMappping.getProjectId();
//		long selfAssessmentDeadLine = schdulerMappping.getSelfAssessmentDeadLine();
//		String projectId = schdulerMappping.getProjectId();
//		try {
//			ProjectData projectById = iSurveyDao.getProjectById(databaseName, projectId);
//			if (projectById == null) {
//				throw new SchedulerServiceException("Invalid projectId");
//			}
//			long endDate = projectById.getEndDate();
//			if (selfAssessmentDeadLine > endDate) {
//				throw new SchedulerServiceException(
//						"Self assessment dead line cannot be more than Project's dead line.");
//			}
//		} catch (ProjectDaoException e) {
//			throw new SchedulerServiceException(e);
//		}
//
//	}

	private void validateUser(String tenantId, String fieldAuditorId)
			throws SchedulerServiceException, UserDaoException {
		User user = iUserDao.getUserByUserId(tenantId, fieldAuditorId);
		if (user == null) {
			throw new SchedulerServiceException("user does not exist");
		} else if (!(user.getUserRole().equals(UserAccessManagementServiceConstants.FIELD_AUDITOR))) {
			throw new SchedulerServiceException("user is not field auditor");
		}
	}

	private void checkingConditions(String databaseName, SchedulerMapping scheduler, String schedulerType)
			throws SchedulerDaoException, MasterDataManagmentDaoException, ProjectDaoException,
			SurveyResponseDaoException, SchedulerServiceException, PartnerDaoException {
		// check whether the field autdit status and self assisment status is true or
		// not, if it is true we cant update the scheduler
		if (schedulerType.equals(SchedulerServiceConstants.GRADING_TYPE)) {
			GradingType gradingType = iSchedulerDao.getGradingTypeDataByPartnerIdProjectIdAndFormId(databaseName,
					scheduler.getPartnerId(), scheduler.getProjectId(), scheduler.getFormId());
			if (gradingType == null) {
				throw new SchedulerServiceException("invalid id ");
			}
//			if (!gradingType.isSelfAssignmentStatus()) {
//				logger.error("can't update if audit status");
//				throw new SchedulerServiceException("can't update if audit status and selfAssignmentStatus are true");
//			}
			if (gradingType.isAuditStatus() && gradingType.isSelfAssignmentStatus()) {
				logger.error("can't update if audit status");
				throw new SchedulerServiceException("can't update if audit status and selfAssignmentStatus are true");
			}

		} else if (schedulerType.equals(SchedulerServiceConstants.RATING_TYPE)) {
			RatingType ratingType = iSchedulerDao.getRatingTypeDataByProjectIdParterIdFormIdAndTcId(databaseName,
					scheduler.getPartnerId(), scheduler.getProjectId(), scheduler.getFormId(), scheduler.getTcId());
			if (ratingType == null) {
				throw new SchedulerServiceException("invalid id ");
			}
			if (ratingType.isAuditStatus() && ratingType.isSelfAssignmentStatus()) {
				logger.error("can't update if audit status");
				throw new SchedulerServiceException("can't update if audit status and selfAssignmentStatus are true");
			}
		} else {
			logger.error("invalid scheduler Type");
			throw new SchedulerServiceException("invalid scheduler Type");
		}
	}

	/**
	 * This method is used to get the scheduler.
	 * @param tenantId the database name.
	 * @param schedulerType it can be either ratingForm or gradingForm
	 * @return  the response message of this method.
	 * @throws SchedulerServiceException if any exception occurs when performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getScheduler(String databaseName, String schedulerType) throws SchedulerServiceException {
		try {
			JSONArray jsonArrayResponse = null;
			logger.debug("scheduler type is " + schedulerType);
			if (schedulerType.equals(SchedulerServiceConstants.GRADING_TYPE)) {
				List<GradingType> allGradingTypeData = iSchedulerDao.getAllGradingTypeData(databaseName);
				logger.debug("size of grdeing type ::" + allGradingTypeData.size());
				jsonArrayResponse = buildJsonFromGradingTypeList(databaseName, allGradingTypeData);
			} else if (schedulerType.equals(SchedulerServiceConstants.RATING_TYPE)) {
				List<RatingType> allRatingTypeData = iSchedulerDao.getAllRatingTypeData(databaseName);
				jsonArrayResponse = buildJsonFromRatingTypeList(databaseName, allRatingTypeData);
			} else {
				logger.error("invalid scheduler Type");
				throw new SchedulerServiceException("invalid scheduler Type");
			}
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put(SchedulerServiceConstants.SCHEDULERS, jsonArrayResponse);
			return jsonResponse.toString();
		} catch (Exception e) {
			logger.error("unable to get schedulers " + e.getMessage());
			throw new SchedulerServiceException("unable to get schedulers " + e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonFromRatingType(String databaseName, RatingType singleRT)
			throws UserDaoException, ProjectDaoException, PartnerDaoException, SurveyResponseDaoException {
		JSONObject jsonResponse = new JSONObject();
		String projectId = singleRT.getProjectId();
		String partnerId = singleRT.getPartnerId();
		String formId = singleRT.getFormId();
		String tcId = singleRT.getTcId();
		long auditDate = singleRT.getAuditDate();
		String fieldAuditorId = singleRT.getFieldAuditorId();
//		long selfAssessmentDeadLine = singleRT.getSelfAssessmentDeadLine();
		boolean auditCancled = singleRT.isAuditCancled();
		boolean auditStatus = singleRT.isAuditStatus();
		boolean selfAssignmentStatus = singleRT.isSelfAssignmentStatus();

		ProjectData project = iSurveyDao.getProjectById(databaseName, projectId);
		FormData form = iSurveyDao.getFormById(databaseName, formId);
		PartnerDetails partner = ipartnerDao.getPartnerById(databaseName, partnerId);
		TrainingCenterDetails tcDetails = ipartnerDao.getTrainingCenterDetailsByTcId(databaseName, tcId);
		long selfAssDate = 0;
		if (selfAssignmentStatus) {
			// error occured here change to tcDetais.getTcId()
			// remove this comment on 4 Feb 2021 11:00 Am
			SurveyResponse surveyResponse = surveyResponseDao.getSurveyResponsesByCenterIdAndSurveyId(databaseName,
					tcDetails.getTcId(), form.getSurveyId());
			logger.debug("surveyResponse " + surveyResponse);
			selfAssDate = surveyResponse.getSubmitTime();

		}
		jsonResponse.put(SchedulerServiceConstants.SELF_ASSIGNMENT_DATE,
				getDateStringFormateTimeFromMiliSecond(selfAssDate));
		jsonResponse.put(SchedulerServiceConstants.SELF_ASSIGNMENT_STATUS, selfAssignmentStatus);
		// Project
		jsonResponse.put(SchedulerServiceConstants.PROJECT_ID, projectId);
		jsonResponse.put(SchedulerServiceConstants.PROJECT_NAME, project.getProjectName());
		jsonResponse.put(SchedulerServiceConstants.FORM_ID, formId);
		jsonResponse.put(SchedulerServiceConstants.FORM_NAME, form.getFormName());

//		jsonResponse.put(SchedulerServiceConstants.SELF_ASSIGNMENT_DEAD_LINE,
//				getDateStringFormateTimeFromMiliSecond(selfAssessmentDeadLine));

		jsonResponse.put(SchedulerServiceConstants.AUDIT_DATE, getDateStringFormateTimeFromMiliSecond(auditDate));
		jsonResponse.put(SchedulerServiceConstants.IS_AUDIT_STATUS, auditStatus);
		jsonResponse.put(SchedulerServiceConstants.IS_AUDIT_CANCLED, auditCancled);

		// FieldAuditor
		jsonResponse.put(SchedulerServiceConstants.FIELD_AUDITOR_ID, fieldAuditorId);
		String fieldAuditorContact = "";
		String firstName = "";
		User fieldAuditor = iUserDao.getUserByUserId(databaseName, fieldAuditorId);
		if (fieldAuditor != null) {
			firstName = fieldAuditor.getFirstName();
			fieldAuditorContact = fieldAuditor.getPhone();
		}
		jsonResponse.put(SchedulerServiceConstants.FIELD_AUDITOR_NAME, firstName);
		jsonResponse.put(SchedulerServiceConstants.FIELD_AUDITOR_CONTACT, fieldAuditorContact);

		// Partner
		jsonResponse.put(SchedulerServiceConstants.PARTNER_ID, partnerId);
		jsonResponse.put(SchedulerServiceConstants.PARTNER_NAME, partner.getPartnerName());
		jsonResponse.put(SchedulerServiceConstants.CLIENT_SPONSOR_ID, partner.getClientSponsorId());
		jsonResponse.put(SchedulerServiceConstants.TC_ID, tcId);
		jsonResponse.put(SchedulerServiceConstants.TC_NAME, tcDetails.getTcName());
		String centerInchargeId = tcDetails.getCenterInchargeId();
		User centerIncharge = iUserDao.getUserByUserId(databaseName, centerInchargeId);
		jsonResponse.put(SchedulerServiceConstants.CENTER_INCHARGE_ID, centerIncharge.getUserId());
		jsonResponse.put(SchedulerServiceConstants.CENTER_INCHARGE_NAME, centerIncharge.getFirstName());
		jsonResponse.put(SchedulerServiceConstants.CENTER_INCHARGE_CONTACT, centerIncharge.getPhone());

		return jsonResponse;

	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonFromGradingType(String databaseName, GradingType singleGT)
			throws SurveyResponseDaoException, PartnerDaoException, ProjectDaoException, UserDaoException {
		JSONObject jsonResponse = new JSONObject();
		String projectId = singleGT.getProjectId();
		String partnerId = singleGT.getPartnerId();
		String formId = singleGT.getFormId();
		long auditDate = singleGT.getAuditDate();
		String fieldAuditorId = singleGT.getFieldAuditorId();
//		long selfAssessmentDeadLine = singleGT.getSelfAssessmentDeadLine();
		boolean auditCancled = singleGT.isAuditCancled();
		boolean auditStatus = singleGT.isAuditStatus();
		boolean selfAssignmentStatus = singleGT.isSelfAssignmentStatus();
		logger.debug("selfAssignmentStatus " + selfAssignmentStatus);
		ProjectData project = iSurveyDao.getProjectById(databaseName, projectId);
		FormData form = iSurveyDao.getFormById(databaseName, formId);
		PartnerDetails partner = ipartnerDao.getPartnerById(databaseName, partnerId);
		long selfAssDate = 0;
		if (selfAssignmentStatus) {
			SurveyResponse surveyResponse = surveyResponseDao.getSurveyResponsesByPartnerIdAndSurveyId(databaseName,
					form.getSurveyId(), partner.getPartnerId());
			selfAssDate = surveyResponse.getSubmitTime();
		}
		jsonResponse.put(SchedulerServiceConstants.SELF_ASSIGNMENT_DATE,
				getDateStringFormateTimeFromMiliSecond(selfAssDate));
		jsonResponse.put(SchedulerServiceConstants.SELF_ASSIGNMENT_STATUS, selfAssignmentStatus);
		// Project
		jsonResponse.put(SchedulerServiceConstants.PROJECT_ID, projectId);
		jsonResponse.put(SchedulerServiceConstants.PROJECT_NAME, project.getProjectName());
		jsonResponse.put(SchedulerServiceConstants.FORM_ID, formId);
		jsonResponse.put(SchedulerServiceConstants.FORM_NAME, form.getFormName());

//		jsonResponse.put(SchedulerServiceConstants.SELF_ASSIGNMENT_DEAD_LINE,
//				getDateStringFormateTimeFromMiliSecond(selfAssessmentDeadLine));
		jsonResponse.put(SchedulerServiceConstants.FIELD_AUDITOR_ID, fieldAuditorId);
//		User fieldAuditor = iUserDao.getUserByUserId(databaseName, fieldAuditorId);
//		if (fieldAuditor != null) {
//			jsonResponse.put(SchedulerServiceConstants.FIELD_AUDITOR_NAME, fieldAuditor.getFirstName());
//		} else {
//			jsonResponse.put(SchedulerServiceConstants.FIELD_AUDITOR_NAME, "");
//		}
		String fieldAuditorContact = "";
		String firstName = "";
		User fieldAuditor = iUserDao.getUserByUserId(databaseName, fieldAuditorId);
		if (fieldAuditor != null) {
			firstName = fieldAuditor.getFirstName();
			fieldAuditorContact = fieldAuditor.getPhone();
		}
		jsonResponse.put(SchedulerServiceConstants.FIELD_AUDITOR_NAME, firstName);
		jsonResponse.put(SchedulerServiceConstants.FIELD_AUDITOR_CONTACT, fieldAuditorContact);

		jsonResponse.put(SchedulerServiceConstants.AUDIT_DATE, getDateStringFormateTimeFromMiliSecond(auditDate));
		jsonResponse.put(SchedulerServiceConstants.IS_AUDIT_STATUS, auditStatus);
		jsonResponse.put(SchedulerServiceConstants.IS_AUDIT_CANCLED, auditCancled);
		logger.debug("adding partner json");
		// Partner
		jsonResponse.put(SchedulerServiceConstants.PARTNER_ID, partnerId);
		jsonResponse.put(SchedulerServiceConstants.PARTNER_NAME, partner.getPartnerName());
		String clientSponsorId = partner.getClientSponsorId();
		User clientSponsor = iUserDao.getUserByUserId(databaseName, clientSponsorId);
		jsonResponse.put(SchedulerServiceConstants.CLIENT_SPONSOR_ID, partner.getClientSponsorId());
		jsonResponse.put(SchedulerServiceConstants.CLIENT_SPONSOR_FIRST_NAME, clientSponsor.getFirstName());
		jsonResponse.put(SchedulerServiceConstants.CLIENT_SPONSOR_CONTACT, clientSponsor.getPhone());

		return jsonResponse;
	}

	/**
	 *  This method is used to filter the scheduler.
	 * @param tenantId  the database name.
	 * @param projectName the name of the project
	 * @param partnerName the name of the partner
	 * @param schedulerType it can be either ratingForm or gradingForm
	 * @return  the response message of this method.
	 * @throws SchedulerServiceException if any exception occurs when performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String filterSchedulers(String tenantId, String projectName, String partnerName, String schedulerType)
			throws SchedulerServiceException {
		try {
			projectName = projectName.trim();
			partnerName = partnerName.trim();
			JSONArray jsonArrayResponse = null;
			if (schedulerType.equals(SchedulerServiceConstants.GRADING_TYPE)) {
				List<GradingType> allGradingTypeData = iSchedulerDao.getAllGradingTypeData(tenantId);
				jsonArrayResponse = buildJsonFromGradingTypeList(tenantId, allGradingTypeData);
			} else if (schedulerType.equals(SchedulerServiceConstants.RATING_TYPE)) {
				List<RatingType> allRatingTypeData = iSchedulerDao.getAllRatingTypeData(tenantId);
				jsonArrayResponse = buildJsonFromRatingTypeList(tenantId, allRatingTypeData);
			} else {
				logger.error("invalid scheduler Type");
				throw new SchedulerServiceException("invalid scheduler Type");
			}
			JSONArray filterJsonArray = new JSONArray();
			logger.debug("total scheduler data is " + jsonArrayResponse.size());
			if (!(projectName.isEmpty()) && !(partnerName.isEmpty())) {
				logger.debug("both projectName and partner name are given");
				for (int i = 0; i < jsonArrayResponse.size(); i++) {
					JSONObject singleScheduler = (JSONObject) jsonArrayResponse.get(i);
					String responseProjectName = JsonUtill.getString(singleScheduler,
							SchedulerServiceConstants.PROJECT_NAME);
					if (responseProjectName.toLowerCase().startsWith(projectName.toLowerCase())) {
						String responsePartnerName = JsonUtill.getString(singleScheduler,
								SchedulerServiceConstants.PARTNER_NAME);
						if (responsePartnerName.toLowerCase().startsWith(partnerName.toLowerCase())) {
							filterJsonArray.add(singleScheduler);
						}
					}
				}
			} else if (!(projectName.isEmpty())) {
				logger.debug("only project name given and it is " + projectName);
				for (int i = 0; i < jsonArrayResponse.size(); i++) {
					JSONObject singleScheduler = (JSONObject) jsonArrayResponse.get(i);
					String responseProjectName = JsonUtill.getString(singleScheduler,
							SchedulerServiceConstants.PROJECT_NAME);
					if (responseProjectName.toLowerCase().startsWith(projectName.toLowerCase())) {
						filterJsonArray.add(singleScheduler);
					}
				}
			} else if (!(partnerName.isEmpty())) {
				logger.debug("only partnerName  given and it is " + partnerName);
				for (int i = 0; i < jsonArrayResponse.size(); i++) {
					JSONObject singleScheduler = (JSONObject) jsonArrayResponse.get(i);
					String responsePartnerName = JsonUtill.getString(singleScheduler,
							SchedulerServiceConstants.PARTNER_NAME);
					if (responsePartnerName.toLowerCase().startsWith(partnerName.toLowerCase())) {
						filterJsonArray.add(singleScheduler);
					}
				}
			}

			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put(SchedulerServiceConstants.SCHEDULERS, filterJsonArray);
			return jsonResponse.toString();

		} catch (Exception e) {
			logger.error("unable to filter schedulers ::" + e.getMessage());
			throw new SchedulerServiceException("unable to filter schedulers " + e.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	private JSONArray buildJsonFromRatingTypeList(String tenantId, List<RatingType> allRatingTypeData)
			throws UserDaoException, ProjectDaoException, PartnerDaoException, SurveyResponseDaoException {
		JSONArray ratingTypeJsonArray = new JSONArray();
		for (RatingType ratingType : allRatingTypeData) {
			JSONObject gradingTypeJson = buildJsonFromRatingType(tenantId, ratingType);
			ratingTypeJsonArray.add(gradingTypeJson);
		}
		return ratingTypeJsonArray;
	}

	@SuppressWarnings("unchecked")
	private JSONArray buildJsonFromGradingTypeList(String tenantId, List<GradingType> allGradingTypeData)
			throws SurveyResponseDaoException, PartnerDaoException, ProjectDaoException, UserDaoException {
		JSONArray gradingTypeJsonArray = new JSONArray();
		for (GradingType gradingType : allGradingTypeData) {
			JSONObject gradingTypeJson = buildJsonFromGradingType(tenantId, gradingType);
			gradingTypeJsonArray.add(gradingTypeJson);
		}
		return gradingTypeJsonArray;
	}

//	private Long getLastSubmittedFormDate(String tenantId, List<FormData> formDataList, String centerInchargeId)
//			throws SurveyResponseDaoException {
//		List<Long> lastSubmittedTime = new ArrayList<Long>();
//		for (FormData formData : formDataList) {
//			String surveyId = formData.getSurveyId();
//			SurveyResponse surveyResponse = surveyResponseDao.getSurveyResponsesBySurveyIdAndUserId(tenantId, surveyId,
//					centerInchargeId);
//			logger.debug("surveyResponse ::" + surveyResponse);
//			long submitedTime = surveyResponse.getSubmitTime();
//			lastSubmittedTime.add(submitedTime);
//
//		}
//		Long max = Collections.max(lastSubmittedTime);
//		return max;
//
//	}

//	private boolean getSurveyAssessmentStatus(String tenantId, List<FormData> formDataList,
//			String trainingCenterInchargeId) throws SurveyResponseDaoException {
//		if (formDataList.isEmpty()) {
//			return false;
//		}
//		for (FormData formData : formDataList) {
//			String surveyId = formData.getSurveyId();
//			SurveyResponse surveyResponse = surveyResponseDao.getSurveyResponsesBySurveyIdAndUserId(tenantId, surveyId,
//					trainingCenterInchargeId);
//			logger.debug("surveyResponse ::" + surveyResponse);
//
//			if (surveyResponse == null) {
//				return false;
//			} else {
//				boolean submited = surveyResponse.isSubmited();
//				if (!submited) {
//					return false;
//				}
//			}
//		}
//		return true;
//
//	}

//	private List<FormData> getFormsOfCenterIncharge(String tenantId, String projectId)
//			throws MasterDataManagmentDaoException, ProjectDaoException {
//		List<FormData> listOfFormsOfUser = new ArrayList<FormData>();
//		logger.debug("project id is " + projectId);
//		List<FormData> formDataList = iSurveyDao.getFormsByProjectId(tenantId, projectId);
//		logger.debug("formData for the projectId " + projectId + " is :: " + formDataList);
//		for (FormData formData : formDataList) {
//			List<String> usersRolesAllowed = formData.getUsersRolesAllowed();
//			for (String role : usersRolesAllowed) {
//				if (role.equals(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
//					logger.debug("this user has the form" + formData);
//					listOfFormsOfUser.add(formData);
//				}
//			}
//		}
//		return listOfFormsOfUser;
//	}

//	private String getStringFormateTimeFromMiliSecond(long time) {
//		Date date = new Date(time);
//		SimpleDateFormat format = new SimpleDateFormat(ProjectServiceConstant.SURVEY_TIME_FORMATE);
//		return format.format(date);
//	}

	private SchedulerMapping buildSchedulerMappingFromJson(String databaseName, JSONObject requestJsonBody,
			String schedulerType)
			throws JsonUtillException, ParseException, SchedulerServiceException, UserDaoException {
		SchedulerMapping schedulerMapping = new SchedulerMapping();

//		String assigDate = JsonUtill.getString(requestJsonBody, SchedulerServiceConstants.SELF_ASSIGNMENT_DEAD_LINE);
		String fieldAuditorId = JsonUtill.getString(requestJsonBody, SchedulerServiceConstants.FIELD_AUDITOR_ID).trim();
		if (fieldAuditorId.isEmpty()) {
			fieldAuditorId = null;
		} else {
			validateUser(databaseName, fieldAuditorId);
		}
		String auditDate = JsonUtill.getString(requestJsonBody, SchedulerServiceConstants.AUDIT_DATE);
//		boolean auditStatus = JsonUtill.getBoolean(requestJsonBody, SchedulerServiceConstants.IS_AUDIT_STATUS);
		boolean isAuditCancel = JsonUtill.getBoolean(requestJsonBody, SchedulerServiceConstants.IS_AUDIT_CANCLED);
		String formId = JsonUtill.getString(requestJsonBody, SchedulerServiceConstants.FORM_ID);
		String projectId = JsonUtill.getString(requestJsonBody, SchedulerServiceConstants.PROJECT_ID);
		String partnerId = JsonUtill.getString(requestJsonBody, SchedulerServiceConstants.PARTNER_ID);

		if (schedulerType.equals(SchedulerServiceConstants.RATING_TYPE)) {
			String tcId = JsonUtill.getString(requestJsonBody, SchedulerServiceConstants.TC_ID);
			schedulerMapping.setTcId(tcId);
		}
		schedulerMapping.setFormId(formId);
		schedulerMapping.setProjectId(projectId);
		schedulerMapping.setPartnerId(partnerId);
		schedulerMapping.setAuditCancled(isAuditCancel);
		// auditStatus cant be send from frontEnd
		// it should be calculate from Backend
		// it is noting but isSubmit of FieldAuditor
//		schedulerMapping.setAuditStatus(auditStatus);
		schedulerMapping.setAuditDate(getDateToMiliSecond(auditDate));
		schedulerMapping.setFieldAuditorId(fieldAuditorId);
//		schedulerMapping.setSelfAssessmentDeadLine(getDateToMiliSecond(assigDate));
		logger.debug("in buildScheduler Mapping data , Scheduler is ::" + schedulerMapping);
		return schedulerMapping;
	}

	private long getDateToMiliSecond(String time) throws ParseException {
		if (time == null || time.isEmpty()) {
			return 0;
		}
		SimpleDateFormat format = new SimpleDateFormat(ProjectServiceConstant.PROJECT_TIME_FORMATE);
		Date date = format.parse(time);
		return date.getTime();
	}

	private String getDateStringFormateTimeFromMiliSecond(long time) {
		if (time == 0) {
			return "";
		}
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat(ProjectServiceConstant.PROJECT_TIME_FORMATE);
		return format.format(date);
	}
}
