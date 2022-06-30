package com.pwc.grading.masterdata.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.Transport;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.mail.session.MailSessionInstance;
import com.pwc.grading.masterdata.dao.IMasterDataManagmentDao;
import com.pwc.grading.masterdata.dao.exception.MasterDataManagmentDaoException;
import com.pwc.grading.masterdata.model.ClientMasterData;
import com.pwc.grading.masterdata.model.GradingEnable;
import com.pwc.grading.masterdata.model.ProjectMapping;
import com.pwc.grading.masterdata.service.IMasterDataManagmentService;
import com.pwc.grading.masterdata.service.MasterDataManagmentServiceConstants;
import com.pwc.grading.masterdata.service.exception.MasterDataManagmentServiceException;
import com.pwc.grading.mediabucket.dao.IMediaDao;
import com.pwc.grading.mediabucket.dao.exception.MediaDaoException;
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
import com.pwc.grading.reportingdb.ReportingDBService;
import com.pwc.grading.reportingdb.assigner.AddGradingJobAssigner;
import com.pwc.grading.reportingdb.assigner.AddRatingJobAssigner;
import com.pwc.grading.reportingdb.assigner.DeleteGradingJobAssigner;
import com.pwc.grading.reportingdb.assigner.DeleteRatingJobAssigner;
import com.pwc.grading.reportingdb.assigner.UpdateCenterInchargeJobAssigner;
import com.pwc.grading.reportingdb.assigner.UpdateClientSponserJobAssigner;
import com.pwc.grading.reportingdb.assigner.UpdateTrainingCenterJobAssigner;
import com.pwc.grading.reportingdb.assigner.exception.AssignerException;
import com.pwc.grading.reportingdb.constant.ReportingDbJSONConstant;
import com.pwc.grading.scheduler.dao.ISchedulerDao;
import com.pwc.grading.scheduler.dao.exception.SchedulerDaoException;
import com.pwc.grading.scheduler.model.GradingType;
import com.pwc.grading.scheduler.model.RatingType;
import com.pwc.grading.scheduler.model.SchedulerMapping;
import com.pwc.grading.surveyresponse.dao.ISurveyResponseDao;
import com.pwc.grading.surveyresponse.dao.exception.SurveyResponseDaoException;
import com.pwc.grading.surveyresponse.model.SurveyResponse;
import com.pwc.grading.surveyresponse.service.SurveyResponseServiceConstant;
import com.pwc.grading.surveyresponse.service.exception.SurveyResponseServiceException;
import com.pwc.grading.tracking.dao.ITrackingDao;
import com.pwc.grading.user.dao.IUserDao;
import com.pwc.grading.user.dao.exception.UserDaoException;
import com.pwc.grading.user.model.User;
import com.pwc.grading.user.service.registration.IUserRegistrationService;
import com.pwc.grading.user.service.registration.UserAccessManagementServiceConstants;
import com.pwc.grading.user.service.registration.exception.UserRegistrationServiceException;
import com.pwc.grading.util.JsonUtill;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.exception.JsonUtillException;

import io.micronaut.http.multipart.CompletedFileUpload;

/**
 * This MasterDataManagmentServiceImpl Class used for mapping the partner,
 * project and Training center
 * 
 *
 */
@Singleton
public class MasterDataManagmentServiceImpl implements IMasterDataManagmentService {
	private static final Logger logger = LoggerFactory.getLogger(MasterDataManagmentServiceImpl.class);
	@Inject
	private IMasterDataManagmentDao iMasterDataManagmentDao;
	@Inject
	private IUserDao userDao;
	@Inject
	private IProjectDao surveyDao;
	@Inject
	private IUserRegistrationService userService;
	@Inject
	private IPartnerDao ipartnerDao;
	@Inject
	private ISchedulerDao iSchedulerDao;
	@Inject
	private ISurveyResponseDao isurveyResponse;
	@Inject
	private ITrackingDao itrackingDao;
	
	@Inject
	private IMediaDao mediaDao;

	/**
	 * This method is used to add mapping between the partner and project or
	 * training center and project
	 * 
	 * @param tenantId the database name.
	 * @param partner  the partner details
	 * @return the response message of this method.
	 * @throws MasterDataManagmentServiceException if any exception occurs when
	 *                                             performing this operation
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String addProjectMasterData(String tenantId, String requestBody) throws MasterDataManagmentServiceException {
		Connection connection = null;
		Transport transport = null;
		try {
			logger.debug("inside addProjectMasterData requestBody:: " + requestBody);
			connection = MSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);

			JSONObject masterJson = (JSONObject) JSONValue.parseWithException(requestBody);
			JSONObject projectDetailsJson = JsonUtill.getJsonObject(masterJson,
					MasterDataManagmentServiceConstants.PROJECT_DETAILS);
			String projectId = JsonUtill.getString(projectDetailsJson, MasterDataManagmentServiceConstants.PROJECT_ID);
			validateProjectId(tenantId, projectId);
			JSONObject partnerDetailsJson = JsonUtill.getJsonObject(masterJson,
					MasterDataManagmentServiceConstants.PARTNER_DETAILS);
			boolean isGradingEnable = JsonUtill.getBoolean(partnerDetailsJson,
					MasterDataManagmentServiceConstants.IS_GRADING_ENABLED);
			transport = MailSessionInstance.getMailSession().getTransport();
			transport.connect();
			PartnerDetails partnerDetails = buildPartnerDetailsFromJson(connection, transport, tenantId,
					partnerDetailsJson);
			TrainingCenterDetails tcDetails = buildTrainingCenterDetailsFromJson(connection, transport, tenantId,
					partnerDetailsJson);
			String partnerProjectId = JsonUtill.getString(partnerDetailsJson,
					MasterDataManagmentServiceConstants.PARTNER_PROJECT_ID);
			boolean isPartnerProjectExist = verifyParterProjectId(connection, tenantId, partnerProjectId);
			String partnerId = partnerDetails.getPartnerId();
			if (!isPartnerProjectExist && partnerId != null) {
				isPartnerProjectExist = true;
			}
			logger.debug("is PartnerProject Exist ?? " + isPartnerProjectExist);
			if (isPartnerProjectExist) {
				logger.debug("Partner exist updating Tc");
				tcDetails.setPartnerId(partnerId);

				updateMasterDataManagment(connection, tenantId, tcDetails, projectId, partnerProjectId, partnerDetails);
				logger.debug("after the Updat ");
				GradingEnable gradingEnable = iMasterDataManagmentDao.getGradingEnableByProjectIdAndPartnerId(
						connection, tenantId, projectId, tcDetails.getPartnerId());
				logger.debug("gradingEnable " + gradingEnable);
				if (gradingEnable == null) {
					logger.debug("in if");
					GradingEnable newGradingEnable = new GradingEnable(projectId, partnerId, isGradingEnable);
					iMasterDataManagmentDao.addGradingEnable(connection, tenantId, newGradingEnable);
				} else {
					boolean oldGradingEnable = gradingEnable.isGradingEnable();
					if (oldGradingEnable == true && isGradingEnable == false) {
						logger.debug("old Entery is true and new Entery is false");
						deleteResponsesForPartner(connection, tenantId, partnerId, projectId);   //NEWLY ADDED
						updateGradingTypeDataWhileMakingIsGradingEnableFalse(connection, tenantId, partnerId, projectId,
								isGradingEnable);
					} else if (oldGradingEnable == false && isGradingEnable == true) {
						logger.debug("oldEntyr is false and new Entery is True");
						iMasterDataManagmentDao.updateGradingEnable(connection, tenantId, isGradingEnable, partnerId,
								projectId);
					}
				}
			} else {
				logger.debug("Adding new partner");
				partnerId = addMasterDataManagment(connection, tenantId, partnerDetails, tcDetails, projectId,
						partnerProjectId);
				GradingEnable gradingEnable = new GradingEnable(projectId, partnerId, isGradingEnable);
				iMasterDataManagmentDao.addGradingEnable(connection, tenantId, gradingEnable);
				partnerDetails.setPartnerId(partnerId);
				String centerInchargeId = tcDetails.getCenterInchargeId();
				userDao.updateCenterIdByUserId(connection, tenantId, centerInchargeId, tcDetails.getTcId());
			}
			logger.debug("------------------- adding forms to scheduler -------------------");
			boolean isGradingRequired = false;
			if (isGradingEnable) {
				List<GradingType> gradingType = iSchedulerDao.getGradingTypeDataByPartnerIdAndProjectId(connection,
						tenantId, partnerDetails.getPartnerId(), projectId);
				if (gradingType.isEmpty()) {
					isGradingRequired = true;
				}
			}
			List<FormData> formsForScheduler = surveyDao.getFormsByProjectId(connection, tenantId, projectId);
			logger.debug("total no. of form are: " + formsForScheduler.size());
			List<String> ratingJSONS = new ArrayList<String>();
			List<String> gradingJSONS = new ArrayList<String>();
			for (FormData form : formsForScheduler) {
				addSchedulerMapping(connection, tenantId, form, partnerDetails, tcDetails, isGradingRequired,
						ratingJSONS, gradingJSONS);
			}
			logger.debug("Rating JSON size: " + ratingJSONS.size());
			logger.debug("Grading JSON size: " + gradingJSONS.size());
			if (ReportingDBService.ENABLED) {
				if (ratingJSONS.size() > 0) {
					AddRatingJobAssigner assigner = new AddRatingJobAssigner();
					assigner.assignAddRatingJobToDatabase(tenantId, ratingJSONS);
					logger.debug("Rating forms assigned.");
				}
				if (gradingJSONS.size() > 0) {
					AddGradingJobAssigner assigner = new AddGradingJobAssigner();
					assigner.assignAddGradingJobToDatabase(tenantId, gradingJSONS);
					logger.debug("Grading forms assigned.");
				}
			}
			// InR
			// InG
			connection.commit();
		} catch (Exception e) {
			try {
				logger.debug("** rolling Back **");
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			logger.error("unable to add project master data managment: " + e.getMessage());
			throw new MasterDataManagmentServiceException(
					"unable to add project master data managmen ::" + e.getMessage());
		} finally {
			logger.debug("closing the connection in service layer for adding the single masterData");
			MSSqlServerUtill.close(null, connection);
			MailSessionInstance.closeTransport(transport);
		}
		JSONObject responseJson = new JSONObject();
		responseJson.put("msg", "project master data");
		responseJson.put(MasterDataManagmentServiceConstants.STATUS, "success");
		return responseJson.toString();
	}

	private void updateGradingTypeDataWhileMakingIsGradingEnableFalse(Connection connection, String tenantId,
			String partnerId, String projectId, boolean isGradingEnable)
			throws SchedulerDaoException, MasterDataManagmentServiceException, MasterDataManagmentDaoException {
		List<GradingType> gradingTypeDataList = iSchedulerDao.getGradingTypeDataByPartnerIdAndProjectId(connection,
				tenantId, partnerId, projectId);
		for (GradingType gt : gradingTypeDataList) {
			// && !gt.isAuditStatus()
			if (gt.getAuditDate() != 0 && gt.isAuditCancled()) {
				throw new MasterDataManagmentServiceException("cant change the GradingEnable when there is audit");
			}
		}
		iMasterDataManagmentDao.updateGradingEnable(connection, tenantId, isGradingEnable, partnerId, projectId);
		iSchedulerDao.deleteGradingTypeData(connection, tenantId, partnerId, projectId);

	}

	private void addSchedulerMapping(Connection connection, String tenantId, FormData form,
			PartnerDetails partnerDetails, TrainingCenterDetails tcDetails, boolean isGradingEnable,
			List<String> ratingJSONS, List<String> gradingJSONS) throws SchedulerDaoException, PartnerDaoException,
			UserDaoException, ProjectDaoException, JsonUtillException, ProjectServiceException, AssignerException {
		logger.info(" in adding Scheduler Forms");
		if (form.isPublish()) {
			SchedulerMapping scheduler = new SchedulerMapping();
			scheduler.setFormId(form.getFormId());
			scheduler.setProjectId(form.getProjectId());
			scheduler.setPartnerId(partnerDetails.getPartnerId());
			boolean isGradingForm = isFromGradingForm(form);
			boolean isRatingForm = isFromRatingForm(form);
			if (isGradingForm && isGradingEnable) {
				logger.info("current form is GradingForm");
				iSchedulerDao.addGradingTypeData(connection, tenantId, scheduler);
				if (ReportingDBService.ENABLED) {
					JSONObject jsonObject = buildJsonForAddingGradingFormToReportDb(connection, tenantId,
							partnerDetails, form);
					logger.debug("GradingForm:: " + jsonObject);
					gradingJSONS.add(jsonObject.toString());
				}
			} else if (isRatingForm) {
				logger.info("current form is RatingForm");
				scheduler.setTcId(tcDetails.getTcId());
				iSchedulerDao.addRatingTypeData(connection, tenantId, scheduler);
				if (ReportingDBService.ENABLED) {
					JSONObject jsonObject = buildJsonForAddingRatingFormToReportDb(connection, tenantId, partnerDetails,
							tcDetails, form);
//					list.add()
					logger.debug("RatingForm:: " + jsonObject);
					ratingJSONS.add(jsonObject.toString());
				}
			} else {
				logger.error("in valid form standards");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonForAddingGradingFormToReportDb(Connection connection, String databaseName,
			PartnerDetails partnerDetails, FormData formData) throws PartnerDaoException, UserDaoException,
			ProjectDaoException, JsonUtillException, ProjectServiceException {
		logger.debug(".in buildJsonForAddingGradingFormToReportDb");
//		JSONObject jsonObject = new JSONObject();
//		String tcId = projectMapping.getTcId();
//		String partnerId = projectMapping.getPartnerId();
//		partnerDetails = ipartnerDao.getPartnerById(connection, databaseName, partnerDetails.getPartnerId());
//		TrainingCenterDetails tcDetails = iPartnerDao.getTrainingCenterDetailsByTcId(connection, databaseName, tcId);
		JSONObject ratingFormJson = buildPartnerJsonForReportDb(connection, databaseName, partnerDetails);
		logger.debug("ratingFormJson success");
		JSONObject projectJsonObject = buildProjectJsonForReportDb(connection, databaseName, formData);
		logger.debug("projectJsonObject success 1 ");
		// JSONObject trainingCenterJson =
		// buildtrainingCenterJsonForReportDb(connection, databaseName, tcDetails);
		ratingFormJson.put(ReportingDbJSONConstant.PROJECT, projectJsonObject);
		logger.debug("projectJsonObject success 2");
		return ratingFormJson;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonForAddingRatingFormToReportDb(Connection connection, String databaseName,
			PartnerDetails partnerDetails, TrainingCenterDetails tcDetails, FormData formData)
			throws PartnerDaoException, UserDaoException, ProjectDaoException, JsonUtillException,
			ProjectServiceException {
		logger.debug(".in buildJsonForAddingRatingFormToReportDb ");
		JSONObject ratingFormJson = buildPartnerJsonForReportDb(connection, databaseName, partnerDetails);
		JSONObject projectJsonObject = buildProjectJsonForReportDb(connection, databaseName, formData);
		JSONObject trainingCenterJson = buildtrainingCenterJsonForReportDb(connection, databaseName, tcDetails);
		ratingFormJson.put(ReportingDbJSONConstant.TRAINING_CENTER, trainingCenterJson);
		ratingFormJson.put(ReportingDbJSONConstant.PROJECT, projectJsonObject);
		return ratingFormJson;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildPartnerJsonForReportDb(Connection connection, String databaseName,
			PartnerDetails partnerDetails) throws UserDaoException {
		logger.debug(".buildPartnerJsonForReportDb");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ReportingDbJSONConstant.PIA_NAME, partnerDetails.getPartnerName());
		jsonObject.put(ReportingDbJSONConstant.PARTNER_ID, partnerDetails.getPartnerId());
//		jsonObject.put(ReportingDbJSONConstant.DISTRICT, "");
		String clientSponsorId = partnerDetails.getClientSponsorId();
		User user = userDao.getUserByUserId(connection, databaseName, clientSponsorId);
		String csName = user.getFirstName() + " " + user.getLastName();
		jsonObject.put(ReportingDbJSONConstant.HEAD_PERSONNEL, csName);  //Previously, first name only.
		jsonObject.put(ReportingDbJSONConstant.HEAD_PERSON_EMAIL, user.getEmail());
		jsonObject.put(ReportingDbJSONConstant.CONTACT, user.getPhone());
		jsonObject.put(ReportingDbJSONConstant.STATUS, false);
		JSONObject scoreJson = new JSONObject();
		scoreJson.put(ReportingDbJSONConstant.CENTER_RATING, 0.0);
		scoreJson.put(ReportingDbJSONConstant.FINAL_AVG, 0.0);
		scoreJson.put(ReportingDbJSONConstant.PROJECT_GRADING, 0.0);
		scoreJson.put(ReportingDbJSONConstant.GRADE, "");
		jsonObject.put(ReportingDbJSONConstant.SCORE, scoreJson);
		return jsonObject;
	}

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
		User user = userDao.getUserByUserId(connection, databaseName, centerInchargeId);
		String name = user.getFirstName()+" "+user.getLastName();
		jsonObject.put(ReportingDbJSONConstant.CIC_NAME, name);    //Previously it was First name only.
		jsonObject.put(ReportingDbJSONConstant.CIC_PHONE, user.getPhone());
		return jsonObject;

	}

	@SuppressWarnings("unchecked")
	private JSONObject buildProjectJsonForReportDb(Connection connection, String databaseName, FormData formData)
			throws ProjectDaoException, JsonUtillException, ProjectServiceException {
		logger.debug(".in buildProjectJsonForReportDb");
		JSONObject jsonObject = new JSONObject();
		String projectId = formData.getProjectId();
		ProjectData project = surveyDao.getProjectById(connection, databaseName, projectId);
		jsonObject.put(ReportingDbJSONConstant.PROJECT_NAME, project.getProjectName());
		jsonObject.put(ReportingDbJSONConstant.PROJECT_ID, project.getProjectId());
		JSONObject formJson = buildFormForAddingRating(connection, databaseName, formData);
		jsonObject.put(ReportingDbJSONConstant.FORM, formJson);
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildFormForAddingRating(Connection connection, String databaseName, FormData formData)
			throws ProjectDaoException, JsonUtillException, ProjectServiceException {
		JSONObject jsonObject = new JSONObject();
		String surveyId = formData.getSurveyId();
		String type = isFromGradingForm(formData) ? ProjectServiceConstant.GRADING_TYPE
				: isFromRatingForm(formData) ? ProjectServiceConstant.RATING_TYPE : null;
		if (type == null) {
			return new JSONObject();
		}
		Survey survey = surveyDao.getSurveyById(connection, databaseName, surveyId);
		jsonObject.put(ProjectServiceConstant.FORM_NAME, formData.getFormName());
		jsonObject.put(ReportingDbJSONConstant.FORM_ID, formData.getFormId());
		jsonObject.put(ReportingDbJSONConstant.FORM_TYPE, type);
		jsonObject.put(ReportingDbJSONConstant.FORM_STATUS, false);
		jsonObject.put(ReportingDbJSONConstant.MAX_MARKS, survey.getMaxScore());
		jsonObject.put(ReportingDbJSONConstant.SA_SCORE, 0);
		jsonObject.put(ReportingDbJSONConstant.FA_SCORE, 0);
		JSONObject timeJsonObject = buildTimingJsonForReportDb();
		logger.debug("time is fine");
		jsonObject.put(ReportingDbJSONConstant.TIMING, timeJsonObject);
		JSONObject fieldAuditorJsonObject = buildFieldAuditorForReportDb();
		logger.debug("fieldAuditorJsonObject is fine");
		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR, fieldAuditorJsonObject);

		JSONArray sectionsJsonObject = buildSectionJsonForReportingDb(survey);
		logger.debug("sectionsJsonObject is fine");
		jsonObject.put(ReportingDbJSONConstant.SECTIONS, sectionsJsonObject);
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	private JSONArray buildSectionJsonForReportingDb(Survey survey) throws JsonUtillException, ProjectServiceException {
		logger.debug("in buildSectionJsonForReportingDb ");
		String surveyData = survey.getSurveyData();
		JSONArray jsonArrayResponse = new JSONArray();
		JSONObject surveyJson = (JSONObject) JSONValue.parse(surveyData);
		JSONArray sectionsArray = (JSONArray) surveyJson.get(ProjectServiceConstant.SECTIONS);

		for (int i = 0; i < sectionsArray.size(); i++) {
			JSONArray parameterArray = new JSONArray();
			JSONObject singleSection = new JSONObject();
			JSONObject jsonObject = (JSONObject) sectionsArray.get(i);
			String sectionId = jsonObject.get(ProjectServiceConstant.SECTION_ID).toString();
			JSONArray sectionQuesArray = (JSONArray) jsonObject.get(ProjectServiceConstant.SECTION_QUESTIONS);
			for (int j = 0; j < sectionQuesArray.size(); j++) {
				JSONObject singleQuesJson = new JSONObject();
				JSONObject singleQues = (JSONObject) sectionQuesArray.get(j);
				String parameter = singleQues.get(ProjectServiceConstant.QUES_ID).toString();
				singleQuesJson.put(ReportingDbJSONConstant.PARAMETER_ID, parameter);
//				Integer maxScore = calculateSingleQuesMaxMark(singleQues);
				int maxScore = Integer.parseInt(singleQues.get(ProjectServiceConstant.SCORE) + "");
				singleQuesJson.put(ReportingDbJSONConstant.MAX_MARKS, maxScore);
				singleQuesJson.put(ReportingDbJSONConstant.SA_SCORE, 0);
				singleQuesJson.put(ReportingDbJSONConstant.FA_SCORE, 0);
				singleQuesJson.put(ReportingDbJSONConstant.SA_REMARK, "");
				singleQuesJson.put(ReportingDbJSONConstant.FA_REMARK, "");
				parameterArray.add(singleQuesJson);
			}
			singleSection.put(ReportingDbJSONConstant.SECTION_ID, sectionId);
			singleSection.put(ReportingDbJSONConstant.PARAMETERS, parameterArray);
			jsonArrayResponse.add(singleSection);
		}
		return jsonArrayResponse;
	}

	private Integer calculateSingleQuesMaxMark(JSONObject currentQues)
			throws JsonUtillException, ProjectServiceException {
		JSONObject jsonObject = (JSONObject) currentQues.get(ProjectServiceConstant.QUES_DATA);
		JSONArray jsonArray = (JSONArray) jsonObject.get(ProjectServiceConstant.OPTIONS);
		List<Integer> list = new ArrayList<>();
		String questionType = (String) ((JSONObject) currentQues.get(ProjectServiceConstant.QUES_META_DATA))
				.get(ProjectServiceConstant.QUESTION_TYPE);
		switch (questionType) {
		case ProjectServiceConstant.OPEN_ENDED:
			logger.debug("current is====>>  " + questionType);
			getOptionScoreForOpenEnd(jsonObject, list);
			break;
		case ProjectServiceConstant.MULTIPLE_CHOICE:
			logger.debug("current is====>>  " + questionType);
			getOptionScoreForChoiceBasedQues(jsonArray, list);
			break;
		case ProjectServiceConstant.DROP_DOWN:
			logger.debug("current is====>>  " + questionType);
			getOptionScoreForChoiceBasedQues(jsonArray, list);
			break;
		case ProjectServiceConstant.CHECK_BOX:
			logger.debug("current is====>>  " + questionType);
			int checkBoxQues = getOptionScoreForCheckBoxQues(jsonArray, list);
			return checkBoxQues;
		default:
			throw new ProjectServiceException("In-valid Question Type : " + questionType);
		}
		Integer maxScore = Collections.max(list);
		return maxScore;
	}

	private int getOptionScoreForCheckBoxQues(JSONArray jsonArray, List<Integer> list) throws JsonUtillException {
		for (int i = 0; i < jsonArray.size(); i++) {
			int optionScore = 0;
			JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
			optionScore = JsonUtill.getInt(jsonObject2, ProjectServiceConstant.OPTION_WEIGHTAGE);
			list.add(optionScore);
			logger.debug("current option  " + jsonObject2);
		}
		return list.stream().mapToInt(Integer::intValue).sum();

	}

	private void getOptionScoreForOpenEnd(JSONObject jsonObject, List<Integer> list) throws JsonUtillException {
		int optionScore = 0;
		optionScore = JsonUtill.getInt(jsonObject, ProjectServiceConstant.WEIGHTAGE);
		logger.debug("optionScore " + optionScore);
		list.add(optionScore);
	}

	private void getOptionScoreForChoiceBasedQues(JSONArray jsonArray, List<Integer> list) throws JsonUtillException {
		for (int i = 0; i < jsonArray.size(); i++) {
			int optionScore = 0;
			JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
			optionScore = JsonUtill.getInt(jsonObject2, ProjectServiceConstant.OPTION_WEIGHTAGE);
			list.add(optionScore);
			logger.debug("current option  " + jsonObject2);
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildFieldAuditorForReportDb() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR_NAME, "");
		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR_PHONE, "");
		jsonObject.put(ReportingDbJSONConstant.FIELD_AUDITOR_LOCATION, "");
		jsonObject.put(ReportingDbJSONConstant.SEC_FIELD_AUDITOR_NAME, "");
		jsonObject.put(ReportingDbJSONConstant.FA_START_TIME, "");
		jsonObject.put(ReportingDbJSONConstant.FA_END_TIME, "");
		jsonObject.put(ReportingDbJSONConstant.FA_START_DATE, "");
		jsonObject.put(ReportingDbJSONConstant.FA_END_DATE, "");
		jsonObject.put(ReportingDbJSONConstant.SIGN_OFF_TIME, "");
		jsonObject.put(ReportingDbJSONConstant.OTP, "");
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildTimingJsonForReportDb() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ReportingDbJSONConstant.START_TIME, "");
		jsonObject.put(ReportingDbJSONConstant.END_TIME, "");
		jsonObject.put(ReportingDbJSONConstant.START_DATE, "");
		jsonObject.put(ReportingDbJSONConstant.END_DATE, "");
		return jsonObject;
	}

	private void addGradingForms(Connection connection, String tenantId, FormData form, PartnerDetails partnerDetails,
			List<String> gradingJSONS) throws SchedulerDaoException, PartnerDaoException, UserDaoException,
			ProjectDaoException, JsonUtillException, ProjectServiceException, AssignerException {
		logger.info(" in adding Scheduler Forms");
		if (form.isPublish()) {
			partnerDetails = ipartnerDao.getPartnerById(connection, tenantId, partnerDetails.getPartnerId());
			SchedulerMapping scheduler = new SchedulerMapping();
			scheduler.setFormId(form.getFormId());
			scheduler.setProjectId(form.getProjectId());
			scheduler.setPartnerId(partnerDetails.getPartnerId());
			boolean isGradingForm = isFromGradingForm(form);
			if (isGradingForm) {
				logger.info("current form is GradingForm");
				iSchedulerDao.addGradingTypeData(connection, tenantId, scheduler);
				if (ReportingDBService.ENABLED) {
					JSONObject jsonObject = buildJsonForAddingGradingFormToReportDb(connection, tenantId,
							partnerDetails, form);
					logger.debug("GradingForm:: " + jsonObject);
					gradingJSONS.add(jsonObject.toJSONString());
				}
			}
		}
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

//	private List<FormData> getFormsForScheduler(Connection connection, String tenantId, String projectId,
//			boolean isGradingEnabled) throws ProjectDaoException {
//		List<FormData> forms = surveyDao.getFormsByProjectId(connection, tenantId, projectId);
//		logger.debug("total no. of form are: " + forms.size());
//		if (!isGradingEnabled) {
//			List<FormData> ratingForms = getRatingForms(forms);
//			logger.debug("there are {" + ratingForms.size() + "} rating Forms");
//			return ratingForms;
//		}
//		return forms;
//	}

//	private List<FormData> getRatingForms(List<FormData> formDataList) {
//		List<String> usersRolesList = new ArrayList<String>();
//		usersRolesList.add(UserAccessManagementServiceConstants.CENTER_IN_CHARGE);
//		usersRolesList.add(UserAccessManagementServiceConstants.FIELD_AUDITOR);
//		List<FormData> listOfForms = new ArrayList<FormData>();
//		for (FormData formData : formDataList) {
//			List<String> usersRolesAllowed = formData.getUsersRolesAllowed();
//			if (usersRolesAllowed.size() == 2 && usersRolesAllowed.containsAll(usersRolesList)) {
//				listOfForms.add(formData);
//			}
//		}
//		return listOfForms;
//	}

//	private List<FormData> getProjectGradingForms(List<FormData> formDataList) {
//		List<String> usersRolesList = new ArrayList<String>();
//		usersRolesList.add(UserAccessManagementServiceConstants.CLIENT_SPONSOR);
//		usersRolesList.add(UserAccessManagementServiceConstants.FIELD_AUDITOR);
//		List<FormData> listOfForms = new ArrayList<FormData>();
//		for (FormData formData : formDataList) {
//			List<String> usersRolesAllowed = formData.getUsersRolesAllowed();
//			if (usersRolesAllowed.size() == 2 && usersRolesAllowed.containsAll(usersRolesList)) {
//				listOfForms.add(formData);
//			}
//		}
//		return listOfForms;
//	}

	private void validateUserId(Connection connection, String tenantId, String centerInchargeId)
			throws MasterDataManagmentServiceException, UserDaoException {
		User user = userDao.getUserByUserId(connection, tenantId, centerInchargeId);
		logger.debug("Training Center User is ::" + user);
		if (user == null) {
			logger.error("user does not exist");
			throw new MasterDataManagmentServiceException(" user does not exist ");
		}
		if (!(user.getUserRole().equalsIgnoreCase(UserAccessManagementServiceConstants.CENTER_IN_CHARGE))) {
			logger.error("user exist but not user role is not Center-In-Charge");
			throw new MasterDataManagmentServiceException("user exist, but user role is not Center-In-Charge");
		}
//		if (user.getCenterId() != null || !(user.getCenterId().isEmpty())) {
//			logger.error("user is assigned to [" + user.getCenterId() + "] Training Center");
//			throw new MasterDataManagmentServiceException(
//					"user is assigned to [" + user.getCenterId() + "] Training Center");
//		}
//		String centerId = user.getCenterId();
//		if (centerId != null) {
//			if (!centerId.isEmpty()) {
//				throw new MasterDataManagmentServiceException(
//						user.getEmail() + " :: already assign (or) mapped to " + centerId + " training center");
//			}
//		}

	}

	private PartnerDetails buildPartnerDetailsFromJson(Connection connection, Transport transport, String databaseName,
			JSONObject partnerDetailsJson)
			throws JsonUtillException, UserDaoException, MasterDataManagmentServiceException,
			UserRegistrationServiceException, MasterDataManagmentDaoException, PartnerDaoException {
		PartnerDetails partnerDetails = new PartnerDetails();

		String clientSponsorId = getClientSponsorIdFromRequest(connection, transport, databaseName, partnerDetailsJson);
		partnerDetails.setClientSponsorId(clientSponsorId);

		boolean containsPartnerIdKey = partnerDetailsJson.containsKey(MasterDataManagmentServiceConstants.PARTNER_ID);
		if (containsPartnerIdKey) {
			String partnerId = (String) partnerDetailsJson.get(MasterDataManagmentServiceConstants.PARTNER_ID);
			PartnerDetails partner = ipartnerDao.getPartnerById(connection, databaseName, partnerId);
			if (partner == null) {
				throw new MasterDataManagmentServiceException("partner does not exist");
			}
			partnerDetails.setPartnerId(partnerId);
			partnerDetails.setPartnerName(partner.getPartnerName());
			return partnerDetails;
		}
		String partnerName = JsonUtill.getString(partnerDetailsJson, MasterDataManagmentServiceConstants.PARTNER_NAME)
				.trim();
		partnerDetails.setPartnerName(partnerName);
		return partnerDetails;
	}

	@SuppressWarnings("unchecked")
	private String getClientSponsorIdFromRequest(Connection connection, Transport transport, String databaseName,
			JSONObject partnerDetailsJson) throws MasterDataManagmentServiceException, UserDaoException,
			UserRegistrationServiceException, JsonUtillException {
		String clientSponsorId;
		if (partnerDetailsJson.containsKey(MasterDataManagmentServiceConstants.CLIENT_SPONSOR_ID)) {
			clientSponsorId = (String) partnerDetailsJson.get(MasterDataManagmentServiceConstants.CLIENT_SPONSOR_ID);
			User user = userDao.getUserByUserId(connection, databaseName, clientSponsorId);
			if (user == null) {
				logger.error("user does not exist");
				throw new MasterDataManagmentServiceException("user does not exist");
			}
			if (!(user.getUserRole().equalsIgnoreCase(UserAccessManagementServiceConstants.CLIENT_SPONSOR))) {
				logger.error("user exist but user is not Client sponsor");
				throw new MasterDataManagmentServiceException("user exist but user is not Client sponsor");
			}

		} else if (partnerDetailsJson.containsKey(MasterDataManagmentServiceConstants.CLIENT_SPONSOR)) {
			logger.debug("adding new ClientSponsor");
			JSONObject clientSponsorDetails = (JSONObject) partnerDetailsJson
					.get(MasterDataManagmentServiceConstants.CLIENT_SPONSOR);
			clientSponsorDetails.put(UserAccessManagementServiceConstants.USER_ROLE,
					UserAccessManagementServiceConstants.CLIENT_SPONSOR);
			String registerUser = userService.registerUser(connection, transport, clientSponsorDetails.toJSONString(),
					databaseName);
			JSONObject response = (JSONObject) JSONValue.parse(registerUser);
			clientSponsorId = JsonUtill.getString(response, UserAccessManagementServiceConstants.USER_ID);
		} else {
			logger.error("invalid request clientSponsorId or clientSponsor is expected");
			throw new MasterDataManagmentServiceException(
					"invalid request clientSponsorId or clientSponsor is expected");
		}
		return clientSponsorId;
	}

	private TrainingCenterDetails buildTrainingCenterDetailsFromJson(Connection connection, Transport transport,
			String dataBaseName, JSONObject partnerDetailsJson) throws JsonUtillException,
			MasterDataManagmentServiceException, UserRegistrationServiceException, UserDaoException {
		TrainingCenterDetails tcDetails = new TrainingCenterDetails();
		JSONObject tcDetailsJson = JsonUtill.getJsonObject(partnerDetailsJson,
				MasterDataManagmentServiceConstants.TRANING_CENTER_DETAILS);
		boolean containsTcName = tcDetailsJson.containsKey(MasterDataManagmentServiceConstants.TC_NAME);
		if (containsTcName) {
			String tcName = ((String) tcDetailsJson.get(MasterDataManagmentServiceConstants.TC_NAME)).trim();
			tcDetails.setTcName(tcName);
		}
		String tcId = JsonUtill.getString(tcDetailsJson, MasterDataManagmentServiceConstants.TC_ID).trim();
		String centerAddress = JsonUtill.getString(tcDetailsJson, MasterDataManagmentServiceConstants.CENTER_ADDRESS)
				.trim();
		String district = JsonUtill.getString(tcDetailsJson, MasterDataManagmentServiceConstants.DISTRICT).trim();
		String latitude = JsonUtill.getString(tcDetailsJson, MasterDataManagmentServiceConstants.LATITUDE).trim();
		String longitude = JsonUtill.getString(tcDetailsJson, MasterDataManagmentServiceConstants.LONGITUDE).trim();
		String centerInchargeId = getCenterInchargeIdFromRequest(connection, transport, dataBaseName, tcDetailsJson);

		tcDetails.setTcId(tcId);
		tcDetails.setCenterAddress(centerAddress);
		tcDetails.setDistrict(district);
		tcDetails.setCenterInchargeId(centerInchargeId);
		tcDetails.setLatitude(latitude);
		tcDetails.setLongitude(longitude);
		return tcDetails;
	}

	@SuppressWarnings("unchecked")
	private String getCenterInchargeIdFromRequest(Connection connection, Transport transport, String dataBaseName,
			JSONObject tcDetailsJson) throws MasterDataManagmentServiceException, UserRegistrationServiceException,
			JsonUtillException, UserDaoException {
		String centerInChargeId = null;
		if (tcDetailsJson.containsKey(MasterDataManagmentServiceConstants.CENTER_INCHARGE_ID)) {
			centerInChargeId = (String) tcDetailsJson.get(MasterDataManagmentServiceConstants.CENTER_INCHARGE_ID);
			// Validating the centerInchargeId
			validateUserId(connection, dataBaseName, centerInChargeId);

		} else if (tcDetailsJson.containsKey(MasterDataManagmentServiceConstants.CENTER_INCHARGE_KEY)) {
			JSONObject centerInchargeDeatils = (JSONObject) tcDetailsJson
					.get(MasterDataManagmentServiceConstants.CENTER_INCHARGE_KEY);
			centerInchargeDeatils.put(UserAccessManagementServiceConstants.USER_ROLE,
					UserAccessManagementServiceConstants.CENTER_IN_CHARGE);
			String registerUser = userService.registerUser(connection, transport, centerInchargeDeatils.toJSONString(),
					dataBaseName);
			JSONObject response = (JSONObject) JSONValue.parse(registerUser);
			centerInChargeId = JsonUtill.getString(response, UserAccessManagementServiceConstants.USER_ID);
		} else {
			logger.error("invalid request centerInChargeId or centerInCharge is expected");
			throw new MasterDataManagmentServiceException(
					"invalid request centerInchargeId or centerInCharge is expected");
		}

		return centerInChargeId;
	}

//	private void verifyPartnerId(String tenantId, String partnerId)
//			throws MasterDataManagmentDaoException, MasterDataManagmentServiceException, PartnerDaoException {
//		PartnerDetails partnerDetails = ipartnerDao.getPartnerById(tenantId, partnerId);
//		if (partnerDetails == null) {
//			throw new MasterDataManagmentServiceException("partnerId does not exist");
//		}
//	}
	/**
	 * This method is used to add mapping between the partner and project or
	 * training center and project using the Excel file
	 * 
	 * @param tenantId  the database name.
	 * @param multipart the excel upload instance which has to be processed.
	 * @param projectId it is a project which you are adding mapping
	 * @return the response message of this method.
	 * @throws MasterDataManagmentServiceException if any exception occurs when
	 *                                             performing this operation
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String uploadProjectMasterDataFromExcel(String tenantId, CompletedFileUpload clientMasterData,
			String projectId) throws MasterDataManagmentServiceException {
		try {
			if (clientMasterData == null) {
				throw new MasterDataManagmentServiceException("client maste cant be null or empty");
			}
			InputStream inputStream = clientMasterData.getInputStream();
			List<ClientMasterData> listOfClientMD = getListOfClientMasteData(inputStream);
			logger.info("list of Client Master data" + listOfClientMD);
			// checking the duplicates emails and emails exists or not if exists they are
			// center-In-charge or not
			validateExcel(tenantId, listOfClientMD);
			JSONObject reposneJSON = uploadMasterDataManagment(tenantId, listOfClientMD, projectId);
			reposneJSON.put("msg", ReadPropertiesFile.readResponseProperty("227"));
			return reposneJSON.toString();
		} catch (MasterDataManagmentServiceException e) {
			logger.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new MasterDataManagmentServiceException(e.getMessage());
		}
	}

	private void validateExcel(String tenantId, List<ClientMasterData> listOfClientMD)
			throws MasterDataManagmentServiceException, UserDaoException {
		List<String> emailList = listOfClientMD.stream().map(cd -> cd.getCenterInchargeEmail())
				.collect(Collectors.toList());
		Map<String, Long> mailMap = emailList.stream()
				.collect(Collectors.toMap(Function.identity(), v -> 1L, Long::sum));

		logger.debug("email map " + mailMap);

		Set<Entry<String, Long>> emailEntrySet = mailMap.entrySet();
		for (Entry<String, Long> singleMap : emailEntrySet) {
			if (singleMap.getValue() > 1) {
				throw new MasterDataManagmentServiceException(
						singleMap.getKey() + " already exist found " + singleMap.getValue() + " times");
			}
		}
//		for (String email : emailList) {
//			boolean userExist = userDao.isUserExist(tenantId, email);
//			if (userExist) {
//				User user = userDao.getUserByEmail(tenantId, email);
//				String userRole = user.getUserRole();
//				logger.debug("current user role is " + userRole + " is is matching with centerInchrge role "
//						+ userRole.equalsIgnoreCase(UserAccessManagementServiceConstants.CENTER_IN_CHARGE));
//				if (!userRole.equalsIgnoreCase(UserAccessManagementServiceConstants.CENTER_IN_CHARGE)) {
//					throw new MasterDataManagmentServiceException(
//							"user with email " + email + " is not an center-In-charge role");
//				}
//			} else {
//				throw new MasterDataManagmentServiceException("user with email " + email + " does not exist");
//			}
//		}
	}

	/**
	 * This method used to get all the mapping data
	 * 
	 * @param tenantId the database name.
	 * @return the json response which has the master data.
	 * @throws MasterDataManagmentServiceException if any exception occurs when
	 *                                             performing this operation
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getMasterMappingData(String tenantId) throws MasterDataManagmentServiceException {
		try {
			JSONArray jsonArrayResponse = new JSONArray();
			List<ProjectMapping> listOfMappingData = iMasterDataManagmentDao.getProjectMappingData(tenantId);
			logger.debug("list of mapping data:: " + listOfMappingData);
//			listOfMappingData.parallelStream().sorted(Comparator.comparing(ProjectMapping::getPartnerId));
//			Map<String, List<ProjectMapping>> collect = listOfMappingData.parallelStream()
//					.collect(Collectors.groupingBy(ProjectMapping::getPartnerId));
//			logger.debug("grouped List is " + collect);
			for (ProjectMapping mapping : listOfMappingData) {
				JSONObject jsonOtbjec = buildJsonFromProjectMapping(tenantId, mapping);
				jsonArrayResponse.add(jsonOtbjec);
			}
			logger.debug("list of mapping data => Json Array Completed");
			JSONObject responseJson = new JSONObject();
			responseJson.put(MasterDataManagmentServiceConstants.PROJECT_MASTER_DATA, jsonArrayResponse);
			return responseJson.toString();
		} catch (Exception e) {
			logger.error("unable to get project master data, ::" + e.getMessage());
			throw new MasterDataManagmentServiceException("unable to get project master data " + e.getMessage());
		}
	}

	/**
	 * This method is used to delete multiple Mapping by Id's
	 * 
	 * @param tenantId    the database name.
	 * @param requestBody - request Body contains the mapping Id's
	 * @return the response message of this method.
	 * @throws MasterDataManagmentServiceException if any exception occurs when
	 *                                             performing this operation
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deleteMultipleMappingDataById(String tenantId, String requestBody)
			throws MasterDataManagmentServiceException {
		logger.debug("in deleteMultipleMappingDataById requestBody:: " + requestBody);
		JSONArray successArray = new JSONArray();
		JSONArray failureArray = new JSONArray();
		try {

			JSONObject requestJson = (JSONObject) JSONValue.parse(requestBody);
			JSONArray projectMappingIdsJson = JsonUtill.getJsonArray(requestJson,
					MasterDataManagmentServiceConstants.PROJECT_MAPPING_ID_S);
			Object[] projectMappingIds = projectMappingIdsJson.toArray();
			List<String> delRatingJsonList = new ArrayList<String>();
			List<String> gradingList = new ArrayList<String>();
			for (int i = 0; i < projectMappingIds.length; i++) {
				String mappingId = projectMappingIds[i].toString();
				try {
					deleteMappingData(tenantId, mappingId, delRatingJsonList, gradingList);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(MasterDataManagmentServiceConstants.PROJECT_MAPPING_ID, mappingId);
					jsonObject.put(MasterDataManagmentServiceConstants.STATUS,
							MasterDataManagmentServiceConstants.SUCCESS);
					jsonObject.put(MasterDataManagmentServiceConstants.MSG, "successfully Deleted the Mapping");
					successArray.add(jsonObject);
				} catch (Exception e) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(MasterDataManagmentServiceConstants.PROJECT_MAPPING_ID, mappingId);
					jsonObject.put(MasterDataManagmentServiceConstants.STATUS,
							MasterDataManagmentServiceConstants.FAILURE);
					jsonObject.put(MasterDataManagmentServiceConstants.ERROR_MSG, e.getMessage());
					failureArray.add(jsonObject);
				}
			}
			DeleteGradingJobAssigner delGrading = new DeleteGradingJobAssigner();
			delGrading.addDeleteGradingJobToDatabase(tenantId, gradingList);
			logger.debug("Delete Grading Job Assigned to ReportDb.");
			DeleteRatingJobAssigner deleteRatingAss = new DeleteRatingJobAssigner();
			deleteRatingAss.addDeleteRatingJobToDatabase(tenantId, delRatingJsonList);
			logger.debug("Delete Rating Job Assigned to ReportDb.");

		} catch (Exception e) {
			logger.error(" unable to delete the mapping by id " + e.getMessage());
			throw new MasterDataManagmentServiceException(" unable to delete the mapping by id " + e.getMessage());
		}
		JSONObject reposneJSON = new JSONObject();
		reposneJSON.put("msg", "mapping deleted successfully");
		reposneJSON.put("success", successArray);
		reposneJSON.put("failure", failureArray);
		return reposneJSON.toString();

	}

	@SuppressWarnings("unchecked")
	private void deleteMappingData(String tenantId, String mappingId, List<String> delRatingJsonList,
			List<String> gradingList) throws Exception {
		Connection connection = null;
		try {
			validateId(mappingId, MasterDataManagmentServiceConstants.PROJECT_MAPPING_ID);
			logger.debug(" id :: " + mappingId);
			ProjectMapping projectMapping = iMasterDataManagmentDao.getProjectMappingById(tenantId, mappingId);
			if (projectMapping == null) {
				throw new MasterDataManagmentServiceException(" Invalid mappingId ");
			}
			String tcId = projectMapping.getTcId();
			String projectId = projectMapping.getProjectId();
			String partnerId = projectMapping.getPartnerId();
			List<ProjectMapping> projectMappingList = iMasterDataManagmentDao
					.getProjectMappingByProjectIdAndPartnerId(tenantId, projectId, partnerId);
			logger.debug("allTcByPartrnerId " + projectMappingList.size());
			boolean removeProjectToPartner = false;
			if (projectMappingList.size() == 1) {
				logger.debug("*** this is the final training centre ,So removing the mapping between partner and project");
				removeProjectToPartner = true;
			}
			connection = MSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			if (removeProjectToPartner) {
				// remove project to partner
				logger.debug("removing the mapping");
				iMasterDataManagmentDao.deleteGradingEnableByPartnerIdAndProjectId(connection, tenantId, partnerId,
						projectId);
				deleteResponsesForPartner(connection, tenantId, partnerId, projectId);
				iSchedulerDao.deleteGradingTypeData(connection, tenantId, partnerId, projectId);
				// delete formGrading in report Db
				// DeleteGrading Task has to trigger here.
				if (ReportingDBService.ENABLED) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put(ReportingDbJSONConstant.PROJECT_ID, projectId);
					jsonObj.put(ReportingDbJSONConstant.PARTNER_ID, partnerId);
					String json = jsonObj.toString();
					gradingList.add(json);

				}
			}
			deleteResponsesForTrainingCetnte(connection, tenantId, tcId, projectId, partnerId);
			JSONObject json = new JSONObject();
			json.put(MasterDataManagmentServiceConstants.PROJECT_ID, projectId);
			json.put(MasterDataManagmentServiceConstants.PARTNER_ID, partnerId);
			json.put(MasterDataManagmentServiceConstants.TC_ID, tcId);
			delRatingJsonList.add(json.toString());
			iSchedulerDao.deleteRatingTypeDateBytcIdProjectIdAndPartnerId(connection, tenantId, tcId, projectId,
					partnerId);
			iMasterDataManagmentDao.deleteMappingDataById(connection, tenantId, mappingId);
			connection.commit();
		} catch (Exception e) {
			if (connection != null) {
				connection.rollback();
			}
			throw e;
		} finally {
			MSSqlServerUtill.close(null, connection);
		}

	}

	private void deleteResponsesForPartner(Connection connection, String tenantId, String partnerId, String projectId)
			throws SchedulerDaoException, ProjectDaoException, SurveyResponseDaoException, MasterDataManagmentServiceException, MediaDaoException {
		List<GradingType> gradingList = iSchedulerDao.getGradingTypeDataByPartnerIdAndProjectId(connection, tenantId,
				partnerId, projectId);
		logger.debug("Grading type list size is : "+gradingList.size());
		List<String> mediaIdList = new ArrayList<String>();
		for (GradingType gt : gradingList) {
			FormData form = surveyDao.getFormById(connection, tenantId, gt.getFormId());
			String surveyId = form.getSurveyId();
			iSchedulerDao.updateSelfAssigInGradingTypeByProjectIdPartnerIdAndFormId(connection, tenantId, false,
					partnerId, projectId, gt.getFormId());
			
			SurveyResponse surveyResponse = isurveyResponse.getSurveyResponsesByPartnerIdAndSurveyId(connection,tenantId, surveyId, partnerId);
			List<String> mediaIds = getMediaIdsFromSurveyResponse(surveyResponse);
			if(mediaIds.size() > 0) {
				logger.debug(mediaIds.size()+" media files for Client Sponsor found for surveyResponseId '"+surveyResponse.getSurveyResponseId()+"'.");
				mediaIdList.addAll(mediaIds);
			}
			isurveyResponse.deleteSurveyResponsesByPartnerIdAndSurveyId(connection, tenantId, surveyId, partnerId);
			itrackingDao.deleteSurveyResponsesTrackingByPartnerIdAndSurveyId(connection, tenantId, surveyId, partnerId);
			if (gt.getFieldAuditorId() != null && !(gt.getFieldAuditorId().isEmpty())) {
				SurveyResponse surveyResponse2 = isurveyResponse.getSurveyResponsesBySurveyIdAndAuditForId(connection, tenantId, surveyId, SurveyResponseServiceConstant.PARTNER, partnerId);
				List<String> mediaIds2 = getMediaIdsFromSurveyResponse(surveyResponse2);
				if(mediaIds2.size() > 0) {
					logger.debug(mediaIds2.size()+" media files for Field Auditor (grading) found for surveyResponseId '"+surveyResponse2.getSurveyResponseId()+"'.");
					mediaIdList.addAll(mediaIds2);
				}

				isurveyResponse.deleteSurveyResponsesBySurveyIdAndAuditForId(connection, tenantId, surveyId,
						SurveyResponseServiceConstant.PARTNER, partnerId);
				itrackingDao.deleteSurveyResponsesTrackingBySurveyIdAndAuditForId(connection, tenantId, surveyId,
						SurveyResponseServiceConstant.PARTNER, partnerId);
				iSchedulerDao.updateAuditStatusInGradingTypeByProjectIdPartnerIdAndFormId(connection, tenantId, false, "", partnerId, projectId, gt.getFormId());
			}
		}
		
		logger.debug("Deleted partner responses, attempting to delete media's associated with it..");
		logger.debug("Complete mediaIds list size : "+mediaIdList.size());
		mediaDao.deleteMediaList(connection, tenantId, mediaIdList);
		logger.debug("Media files deleted for partner responses.");
	}

	private List<String> getMediaIdsFromSurveyResponse(SurveyResponse surveyResponse) throws MasterDataManagmentServiceException {
		List<String> mediaIdList = new ArrayList<String>();
		try {
			String surveyResponseData = surveyResponse.getSurveyResponseData();		
			if(surveyResponseData != null && !surveyResponseData.isEmpty()) {
				JSONArray surveyResponseDataJson = (JSONArray) JSONValue.parseWithException(surveyResponseData);
				for (int i = 0; i < surveyResponseDataJson.size(); i++) {
					JSONObject singleSection = (JSONObject) surveyResponseDataJson.get(i);
				//	String sectionId = JsonUtill.getString(singleSection, ProjectServiceConstant._SECTION_ID);
					JSONArray sectionResponseArray = JsonUtill.getJsonArray(singleSection,
							ProjectServiceConstant._SECTION_RESPONSE_DATA);
					for (int j = 0; j < sectionResponseArray.size(); j++) {
						JSONObject singleQuestion = (JSONObject) sectionResponseArray.get(j);
				//		String questionId = JsonUtill.getString(singleQuestion, ProjectServiceConstant._QUESTION_ID);
						JSONObject responseData = JsonUtill.getJsonObject(singleQuestion,
								ProjectServiceConstant._RESPONSE_DATA);
						JSONObject mediaResponseJsonObject = JsonUtill.getJsonObject(responseData,
								ProjectServiceConstant._MEDIA_RESPONSE);
						JSONArray imageJsonArray = (JSONArray) mediaResponseJsonObject.get(ProjectServiceConstant._IMAGE);
						for (int k = 0; k < imageJsonArray.size(); k++) {
							JSONObject singleMedia = (JSONObject) imageJsonArray.get(k);
							String mediaId = JsonUtill.getString(singleMedia, ProjectServiceConstant._MEDIA_ID);
							mediaIdList.add(mediaId);   //Image type media Ids
						}
						JSONArray docJsonArray = (JSONArray) mediaResponseJsonObject.get(ProjectServiceConstant._DOCS);
						for (int k = 0; k < docJsonArray.size(); k++) {
							JSONObject singleMedia = (JSONObject) docJsonArray.get(k);
							String mediaId = JsonUtill.getString(singleMedia, ProjectServiceConstant._MEDIA_ID);
							mediaIdList.add(mediaId);   //Document type media Ids.
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Unable to get mediaIds from the surveyResponse..",e);
			throw new MasterDataManagmentServiceException("Unable to get mediaIds from the surveyResponse, "+e.getMessage());
		}
		
		return mediaIdList;
	}

	private void deleteResponsesForTrainingCetnte(Connection connection, String tenantId, String tcId, String projectId,
			String partnerId) throws ProjectDaoException, SchedulerDaoException, SurveyResponseDaoException, MasterDataManagmentServiceException, MediaDaoException {
		List<RatingType> ratingTypeList = iSchedulerDao.getRatingTypeDataByProjectIdParterIdAndTcId(connection,
				tenantId, partnerId, projectId, tcId);
		logger.debug("Rating type size is : "+ratingTypeList.size());
		List<String> mediaIdList = new ArrayList<String>();
		for (RatingType rt : ratingTypeList) {
			FormData form = surveyDao.getFormById(connection, tenantId, rt.getFormId());
			String surveyId = form.getSurveyId();
			SurveyResponse surveyResponse1 = isurveyResponse.getSurveyResponsesByCenterIdAndSurveyId(connection,tenantId, tcId, surveyId); 
			List<String> mediaIds1 = getMediaIdsFromSurveyResponse(surveyResponse1);
			if(mediaIds1.size() > 0) {
				logger.debug(mediaIds1.size()+" media files for Center-In-Charge found for surveyResponseId '"+surveyResponse1.getSurveyResponseId()+"'.");
				mediaIdList.addAll(mediaIds1);
			}
			isurveyResponse.deleteSurveyResponsesByCenterIdAndSurveyId(connection, tenantId, tcId, surveyId);
			itrackingDao.deleteSurveyResponsesTrackingByCenterIdAndSurveyId(connection, tenantId, tcId, surveyId);
			iSchedulerDao.updateSelfAssigInRatingTypeByProjectIdPartnerIdFormIdAndTcId(connection, tenantId, false,
					partnerId, projectId, rt.getFormId(), rt.getTcId());
			if (rt.getFieldAuditorId() != null && !(rt.getFieldAuditorId().isEmpty())) {
				SurveyResponse surveyResponse2 = isurveyResponse.getSurveyResponsesBySurveyIdAndAuditForId(connection, tenantId, surveyId, SurveyResponseServiceConstant.TRAINING_CENTER, tcId);
				List<String> mediaIds2 = getMediaIdsFromSurveyResponse(surveyResponse2);
				if(mediaIds2.size() > 0) {
					logger.debug(mediaIds2.size()+" media files for Field Auditor (rating) found for surveyResponseId '"+surveyResponse2.getSurveyResponseId()+"'.");
					mediaIdList.addAll(mediaIds2);
				}
				isurveyResponse.deleteSurveyResponsesBySurveyIdAndAuditForId(connection, tenantId, surveyId,
						SurveyResponseServiceConstant.TRAINING_CENTER, tcId);
				itrackingDao.deleteSurveyResponsesTrackingBySurveyIdAndAuditForId(connection, tenantId, surveyId,
						SurveyResponseServiceConstant.TRAINING_CENTER, tcId); 
				iSchedulerDao.updateAuditStatusInRatingTypeByProjectIdPartnerIdFormIdAndTcId(connection, tenantId, false, "", partnerId, projectId, rt.getFormId(), tcId);
			}
		}
		logger.debug("Deleted Training center responses, attempting to delete media's associated with it..");
		logger.debug("Complete mediaIds list size : "+mediaIdList.size());
		mediaDao.deleteMediaList(connection, tenantId, mediaIdList);
		logger.debug("Media files deleted for Training center responses.");
	}

	/**
	 * This method is used to Update the mapping like tcName, tcAddress.. etc it is
	 * also used to change the Client-Sponsor for a partner or change the
	 * CenterIncharge for Training Cetner
	 * 
	 * @param tenantId   the database name.
	 * @param mappingId  the project mapping id.
	 * @param masterData the json which has the master data.
	 * @return the json response which has the master data.
	 * @throws MasterDataManagmentServiceException if any exception occurs when
	 *                                             performing this operation
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String updateProjectMasterData(String tenantId, String mappingId, String requestBody)
			throws MasterDataManagmentServiceException {
		Connection connection = null;
		Transport transport = null;
		try {
			logger.debug("inside updateProjectMasterData");
			ProjectMapping projectMapping = iMasterDataManagmentDao.getProjectMappingById(tenantId, mappingId);
			if (projectMapping == null) {
				logger.error("mapping id does not exist");
				throw new MasterDataManagmentServiceException(" Invalid Mapping Id");
			}
			JSONObject partnerJson = (JSONObject) JSONValue.parseWithException(requestBody);
			JSONObject projectDetailsJson = JsonUtill.getJsonObject(partnerJson,
					MasterDataManagmentServiceConstants.PROJECT_DETAILS);
			String projectId = JsonUtill.getString(projectDetailsJson, MasterDataManagmentServiceConstants.PROJECT_ID);
			validateProjectId(tenantId, projectId);
			connection = MSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			JSONObject partnerDetailsJson = JsonUtill.getJsonObject(partnerJson,
					MasterDataManagmentServiceConstants.PARTNER_DETAILS);
			boolean isGradingEnable = JsonUtill.getBoolean(partnerDetailsJson,
					MasterDataManagmentServiceConstants.IS_GRADING_ENABLED);
			transport = MailSessionInstance.getMailSession().getTransport();
			transport.connect();
			PartnerDetails partnerDetails = buildPartnerDetailsFromJson(connection, transport, tenantId,
					partnerDetailsJson);
			TrainingCenterDetails tcDetails = buildTrainingCenterDetailsFromJson(connection, transport, tenantId,
					partnerDetailsJson);
			String partnerProjectId = JsonUtill.getString(partnerDetailsJson,
					MasterDataManagmentServiceConstants.PARTNER_PROJECT_ID);
			boolean isPartnerProjectExist = verifyParterProjectId(connection, tenantId, partnerProjectId);
			String partnerId = partnerDetails.getPartnerId();
			if (!isPartnerProjectExist && partnerId != null) {
				isPartnerProjectExist = true;
			}
			// checking whether user is changing the projecte
			boolean isUserChangingProject = isUserChangingProject(projectMapping, projectId);
			if (isUserChangingProject) {
				GradingEnable gradingEnable = iMasterDataManagmentDao
						.getGradingEnableByProjectIdAndPartnerId(connection, tenantId, projectId, partnerId);
				if (gradingEnable == null) {
					logger.debug("this partner doesnot have mapping to  project ");
					GradingEnable newGradingEnable = new GradingEnable(projectId, partnerId, isGradingEnable);
					iMasterDataManagmentDao.addGradingEnable(connection, tenantId, newGradingEnable);
				}
				String tcId = projectMapping.getTcId();
				String oldProjectId = projectMapping.getProjectId();
				logger.debug("------------------- Delete forms to scheduler -------------------");
				iSchedulerDao.deleteRatingTypeDate(connection, tenantId, tcId, oldProjectId);
			}
			// *************** Start Integrate **********************
			if (ReportingDBService.ENABLED) {
				logger.debug("Updating TC data in ReportingTables,");
				JSONObject trainingCenter = new JSONObject();
				trainingCenter.put(ReportingDbJSONConstant.TC_ID, tcDetails.getTcId());
				trainingCenter.put(ReportingDbJSONConstant.TC_NAME, tcDetails.getTcName());
				trainingCenter.put(ReportingDbJSONConstant.CENTER_ADDRESS, tcDetails.getCenterAddress());
				trainingCenter.put(ReportingDbJSONConstant.C_DISTRICT, tcDetails.getDistrict());
				trainingCenter.put(ReportingDbJSONConstant.LATITUDE, Double.parseDouble(tcDetails.getLatitude()));
				trainingCenter.put(ReportingDbJSONConstant.LONGITUDE, Double.parseDouble(tcDetails.getLongitude()));  //changing latitude to longitude.
				JSONObject updateTCJson = new JSONObject();
				updateTCJson.put(ReportingDbJSONConstant.PROJECT_ID, projectId);
				updateTCJson.put(ReportingDbJSONConstant.PARTNER_ID, partnerId);
				updateTCJson.put(ReportingDbJSONConstant.TRAINING_CENTER, trainingCenter);
				String tcjsonStr = updateTCJson.toString();
				logger.debug("Update TC JSON : " + tcjsonStr);
				UpdateTrainingCenterJobAssigner tcAssigner = new UpdateTrainingCenterJobAssigner();
				tcAssigner.assignUpdateTCJobToDatabase(tenantId, tcjsonStr);
			}
			// *************** End Integrate **********************

			// *************** Start Integrate **********************
			String newClientSponsorId = partnerDetails.getClientSponsorId();
			logger.debug("newClientSponsorId " + newClientSponsorId);
			PartnerDetails oldPartnerDetails = ipartnerDao.getPartnerById(connection, tenantId, partnerId);
			logger.debug("partner sis " + oldPartnerDetails);
			String oldClientSponsorId = oldPartnerDetails.getClientSponsorId();
			logger.debug("oldClientSponsorId " + oldClientSponsorId);
			if (!oldClientSponsorId.equals(newClientSponsorId)) {
				deleteResponsesForPartner(connection, tenantId, partnerId, projectId);
				ipartnerDao.updatePartnerById(connection, tenantId, newClientSponsorId, partnerId);
				if (ReportingDBService.ENABLED) {
					logger.debug("Client sponsor changed..Updating CS data in ReportingTables,");
					User user = userDao.getUserByUserId(connection, tenantId, newClientSponsorId);
					String csName = user.getFirstName() + " " + user.getLastName();
					String csPhone = user.getPhone();
					String csEmail = user.getEmail();
					Map<String, String> clientSponsor = new HashMap<String, String>();
					clientSponsor.put(ReportingDbJSONConstant.CS_NAME, csName);
					clientSponsor.put(ReportingDbJSONConstant.CS_PHONE, csPhone);
					clientSponsor.put(ReportingDbJSONConstant.CS_EMAIL, csEmail);
					JSONObject updateCSJson = new JSONObject();
					updateCSJson.put(ReportingDbJSONConstant.PROJECT_ID, projectId);
					updateCSJson.put(ReportingDbJSONConstant.PARTNER_ID, partnerId);
					updateCSJson.put(ReportingDbJSONConstant.CLIENT_SPONSOR, clientSponsor);
					String jsonStr = updateCSJson.toString();
					logger.debug("Update CS JSON : " + jsonStr);
					UpdateClientSponserJobAssigner csAssigner = new UpdateClientSponserJobAssigner();
					csAssigner.assignUpdateCSJobToDatabase(tenantId, jsonStr);
				}

			}
			// *************** End Integrate **********************

			tcDetails.setPartnerId(partnerId);
			String tcId = tcDetails.getTcId();
			logger.debug("updating existing tcDetails, tcDetails::" + tcDetails);
			TrainingCenterDetails oldTcDetails = ipartnerDao.getTrainingCenterDetailsByTcId(connection, tenantId, tcId);
			if (oldTcDetails == null) {
				throw new MasterDataManagmentServiceException("Invalid tcId");
			}
			// *************** Start Integrate **********************
			String newCenterInchargeId = tcDetails.getCenterInchargeId();
			String oldCenterInchargeId = oldTcDetails.getCenterInchargeId();
			// check conditon and triger
			if (!oldCenterInchargeId.equals(newCenterInchargeId)) {
				// updating old center Incharge CenterId to NULl
				deleteResponsesForTrainingCetnte(connection, tenantId, tcId, projectId, partnerId);
				userDao.updateCenterIdByUserId(connection, tenantId, oldCenterInchargeId, null);
				userDao.updateCenterIdByUserId(connection, tenantId, newCenterInchargeId, tcId);
				if (ReportingDBService.ENABLED) {
					logger.debug("Center In charge changed..Updating CIC data in ReportingTables,");
					User user = userDao.getUserByUserId(connection, tenantId, newCenterInchargeId);
					String cicName = user.getFirstName() + " " + user.getLastName();
					String cicPhone = user.getPhone();
					Map<String, String> centerIncharge = new HashMap<String, String>();
					centerIncharge.put(ReportingDbJSONConstant.CIC_NAME, cicName);
					centerIncharge.put(ReportingDbJSONConstant.CIC_PHONE, cicPhone);
					JSONObject updateCICJson = new JSONObject();
					updateCICJson.put(ReportingDbJSONConstant.PROJECT_ID, projectId);
					updateCICJson.put(ReportingDbJSONConstant.PARTNER_ID, partnerId);
					updateCICJson.put(ReportingDbJSONConstant.TC_ID, tcId);
					updateCICJson.put(ReportingDbJSONConstant.CENTER_IN_CHARGE, centerIncharge);
					String jsonStr = updateCICJson.toString();
					logger.debug("Update CIC JSON : " + jsonStr);
					UpdateCenterInchargeJobAssigner assigner = new UpdateCenterInchargeJobAssigner();
					assigner.assignUpdateCICJobToDatabase(tenantId, jsonStr);
				}

			}
			// *************** End Integrate **********************

			ipartnerDao.updateTrainingCenterDetails(connection, tenantId, tcDetails);
			userDao.updateCenterIdByUserId(connection, tenantId, newCenterInchargeId, tcId);
			logger.debug("TcDetails ::" + tcDetails);
			ProjectMapping mapping = buildProjectMapping(projectId, tcDetails.getPartnerId(), partnerProjectId,
					partnerDetails.getClientSponsorId(), tcId);
			iMasterDataManagmentDao.updateProjectMapping(connection, tenantId, mappingId, mapping);
			GradingEnable gradingEnable = iMasterDataManagmentDao.getGradingEnableByProjectIdAndPartnerId(connection,
					tenantId, projectId, tcDetails.getPartnerId());
			boolean oldGradingEnable = false;
//			if (gradingEnable != null) {
			oldGradingEnable = gradingEnable.isGradingEnable();
//			}
			if (oldGradingEnable == true && isGradingEnable == false) {
				logger.debug("old Entery is true and new Entery is false");
				List<GradingType> gradingTypeDataList = iSchedulerDao
						.getGradingTypeDataByPartnerIdAndProjectId(connection, tenantId, partnerId, projectId);
				for (GradingType gt : gradingTypeDataList) {
					// && !gt.isAuditStatus()
					if (gt.getAuditDate() != 0 && gt.isAuditCancled()) {
						throw new MasterDataManagmentServiceException(
								"cant change the GradingEnable when there is audit");
					}
				}
				iMasterDataManagmentDao.updateGradingEnable(connection, tenantId, isGradingEnable, partnerId,
						projectId);
				deleteResponsesForPartner(connection, tenantId, partnerId, projectId);
				iSchedulerDao.deleteGradingTypeData(connection, tenantId, partnerId, projectId);

				// DeleteGrading Task has to trigger here.
				if (ReportingDBService.ENABLED) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put(ReportingDbJSONConstant.PROJECT_ID, projectId);
					jsonObj.put(ReportingDbJSONConstant.PARTNER_ID, partnerId);
					String json = jsonObj.toString();
					List<String> jsonList = new ArrayList<String>();
					jsonList.add(json);
					DeleteGradingJobAssigner delGrading = new DeleteGradingJobAssigner();
					delGrading.addDeleteGradingJobToDatabase(tenantId, jsonList);
					logger.debug("Delete Grading Job Assigned to ReportDb.");
				}

			} else if (oldGradingEnable == false && isGradingEnable == true) {
				logger.debug("oldEntyr is false and new Entery is True");
				iMasterDataManagmentDao.updateGradingEnable(connection, tenantId, isGradingEnable, partnerId,
						projectId);
				List<FormData> formsForScheduler = surveyDao.getFormsByProjectId(connection, tenantId, projectId);
				List<String> gradingJSONS = new ArrayList<String>();
				for (FormData form : formsForScheduler) {
					addGradingForms(connection, tenantId, form, partnerDetails, gradingJSONS);
				}
				logger.debug("Grading JSON size: " + gradingJSONS.size());
				if (ReportingDBService.ENABLED) {
					if (gradingJSONS.size() > 0) {
						AddGradingJobAssigner assigner = new AddGradingJobAssigner();
						assigner.assignAddGradingJobToDatabase(tenantId, gradingJSONS);
						logger.debug("Grading forms assigned.");

					}
				}

			}
			if (isUserChangingProject) {
				logger.debug("------------------- adding forms to scheduler -------------------");
				boolean isGradingRequired = false;
				if (isGradingEnable) {
					logger.debug("projectId and partenr Id is : " + partnerDetails.getPartnerId() + "  " + projectId);
					List<GradingType> listOfGrading = iSchedulerDao.getGradingTypeDataByPartnerIdAndProjectId(
							connection, tenantId, partnerDetails.getPartnerId(), projectId);
					logger.debug("listOfGrading " + listOfGrading);
					if (listOfGrading.isEmpty()) {
						isGradingRequired = true;
					}
				}
				List<FormData> formsForScheduler = surveyDao.getFormsByProjectId(connection, tenantId, projectId);
				List<String> ratingJSONS = new ArrayList<String>();
				List<String> gradingJSONS = new ArrayList<String>();
				for (FormData form : formsForScheduler) {
					addSchedulerMapping(connection, tenantId, form, partnerDetails, tcDetails, isGradingRequired,
							ratingJSONS, gradingJSONS);
				}
				logger.debug("Rating JSON size: " + ratingJSONS.size());
				logger.debug("Grading JSON size: " + gradingJSONS.size());
				if (ReportingDBService.ENABLED) {
					if (ratingJSONS.size() > 0) {
						AddRatingJobAssigner assigner = new AddRatingJobAssigner();
						assigner.assignAddRatingJobToDatabase(tenantId, ratingJSONS);
						logger.debug("Rating forms assigned.");
					}
					if (gradingJSONS.size() > 0) {
						AddGradingJobAssigner assigner = new AddGradingJobAssigner();
						assigner.assignAddGradingJobToDatabase(tenantId, gradingJSONS);
						logger.debug("Grading forms assigned.");
					}
				}
			}
			connection.commit();
			JSONObject responseJson = new JSONObject();
			responseJson.put(MasterDataManagmentServiceConstants.STATUS, "master data managment successfully updated");
			return responseJson.toString();
		} catch (Exception e) {
			logger.error("unable to update project master data " + e.getMessage());
			if (connection != null) {
				try {
					logger.debug("------------ rolling Back ------------");
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw new MasterDataManagmentServiceException("unable to update project master data " + e.getMessage());
		} finally {
			MSSqlServerUtill.close(null, connection);
			MailSessionInstance.closeTransport(transport);
		}
	}

//	private void validateProjectMappingId(String tenantId, String mappingId)
//			throws MasterDataManagmentServiceException, MasterDataManagmentDaoException {
//		ProjectMapping projectMapping = iMasterDataManagmentDao.getProjectMappingById(tenantId, mappingId);
//		if (projectMapping == null) {
//			logger.error("mapping id does not exist");
//			throw new MasterDataManagmentServiceException(" Invalid Mapping Id");
//		}
//	}

	private boolean isUserChangingProject(ProjectMapping projectMapping, String projectId) {
		if (projectMapping.getProjectId().equals(projectId)) {
			logger.debug("user is not changning project");
			return false;
		}
		logger.debug("user is changning project");
		return true;
	}

	/**
	 * This is used to filter the project Mapping data by projectName , partnerName
	 * and tcId
	 * 
	 * @param tenantId    the database name.
	 * @param projectName - filter by projectName
	 * @param partnerName - filter by partnerName
	 * @param tcId        - filter by tcId
	 * @return the json response which has the master data.
	 * @throws MasterDataManagmentServiceException if any exception occurs when
	 *                                             performing this operation
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String filterProjectMasterData(String tenantId, String projectName, String partnerName, String tcId)
			throws MasterDataManagmentServiceException {
		logger.debug(".in filterProjectMasterData, projectName " + projectName + " partnerName " + partnerName
				+ " tcId " + tcId);
		JSONArray jsonArrayResponse = new JSONArray();
		JSONArray masterJsonArray = new JSONArray();
		List<ProjectMapping> listOfMappingData;
		try {
			projectName = projectName.trim();
			partnerName = partnerName.trim();
			tcId = tcId.trim();

			listOfMappingData = iMasterDataManagmentDao.getProjectMappingData(tenantId);
			logger.debug("list of mapping data:: " + listOfMappingData);
			for (ProjectMapping mapping : listOfMappingData) {
				JSONObject jsonOtbjec = buildJsonFromProjectMapping(tenantId, mapping);
				masterJsonArray.add(jsonOtbjec);
			}
			logger.debug("masterJsonArray ::" + masterJsonArray);
			logger.debug("masterJsonArray size ::" + masterJsonArray.size());
			if (!projectName.isEmpty() && !partnerName.isEmpty() && !tcId.isEmpty()) {
				for (int i = 0; i < masterJsonArray.size(); i++) {
					JSONObject singleJson = (JSONObject) masterJsonArray.get(i);
					JSONObject jsonProjectDetails = JsonUtill.getJsonObject(singleJson,
							MasterDataManagmentServiceConstants.PROJECT_DETAILS);
					String jsonProjectName = JsonUtill.getString(jsonProjectDetails,
							MasterDataManagmentServiceConstants.PROJECT_NAME);
					if (jsonProjectName.toLowerCase().startsWith(projectName.toLowerCase())) {
						JSONObject parnterdetailsjson = JsonUtill.getJsonObject(singleJson,
								MasterDataManagmentServiceConstants.PARTNER_DETAILS);
						String jsonPartnerName = JsonUtill.getString(parnterdetailsjson,
								MasterDataManagmentServiceConstants.PARTNER_NAME);
						if (jsonPartnerName.toLowerCase().startsWith(partnerName.toLowerCase())) {
							JSONObject pd = JsonUtill.getJsonObject(singleJson,
									MasterDataManagmentServiceConstants.PARTNER_DETAILS);
							JSONObject tcd = JsonUtill.getJsonObject(pd,
									MasterDataManagmentServiceConstants.TRANING_CENTER_DETAILS);
							String jsontcId = JsonUtill.getString(tcd, MasterDataManagmentServiceConstants.TC_ID);
							if (jsontcId.toLowerCase().startsWith(tcId.toLowerCase())) {
								jsonArrayResponse.add(singleJson);
							}
						}
					}
				}
			} else if (!projectName.isEmpty() && !partnerName.isEmpty()) {
				for (int i = 0; i < masterJsonArray.size(); i++) {
					JSONObject singleJson = (JSONObject) masterJsonArray.get(i);
					JSONObject jsonProjectDetails = JsonUtill.getJsonObject(singleJson,
							MasterDataManagmentServiceConstants.PROJECT_DETAILS);
					String jsonProjectName = JsonUtill.getString(jsonProjectDetails,
							MasterDataManagmentServiceConstants.PROJECT_NAME);
					if (jsonProjectName.toLowerCase().startsWith(projectName.toLowerCase())) {
						JSONObject parnterdetailsjson = JsonUtill.getJsonObject(singleJson,
								MasterDataManagmentServiceConstants.PARTNER_DETAILS);
						String jsonPartnerName = JsonUtill.getString(parnterdetailsjson,
								MasterDataManagmentServiceConstants.PARTNER_NAME);
						if (jsonPartnerName.toLowerCase().startsWith(partnerName.toLowerCase())) {
							jsonArrayResponse.add(singleJson);
						}
					}
				}
			} else if (!projectName.isEmpty() && !tcId.isEmpty()) {
				for (int i = 0; i < masterJsonArray.size(); i++) {
					JSONObject singleJson = (JSONObject) masterJsonArray.get(i);
					JSONObject jsonProjectDetails = JsonUtill.getJsonObject(singleJson,
							MasterDataManagmentServiceConstants.PROJECT_DETAILS);
					String jsonProjectName = JsonUtill.getString(jsonProjectDetails,
							MasterDataManagmentServiceConstants.PROJECT_NAME);
					if (jsonProjectName.toLowerCase().startsWith(projectName.toLowerCase())) {
						JSONObject pd = JsonUtill.getJsonObject(singleJson,
								MasterDataManagmentServiceConstants.PARTNER_DETAILS);
						JSONObject tcd = JsonUtill.getJsonObject(pd,
								MasterDataManagmentServiceConstants.TRANING_CENTER_DETAILS);
						String jsonTcId = JsonUtill.getString(tcd, MasterDataManagmentServiceConstants.TC_ID);
						if (jsonTcId.startsWith(tcId.toLowerCase())) {
							jsonArrayResponse.add(singleJson);
						}
					}
				}
			} else if (!partnerName.isEmpty() && !tcId.isEmpty()) {
				for (int i = 0; i < masterJsonArray.size(); i++) {
					JSONObject singleJson = (JSONObject) masterJsonArray.get(i);
					JSONObject parnterdetailsjson = JsonUtill.getJsonObject(singleJson,
							MasterDataManagmentServiceConstants.PARTNER_DETAILS);
					String jsonPartnerName = JsonUtill.getString(parnterdetailsjson,
							MasterDataManagmentServiceConstants.PARTNER_NAME);
					if (jsonPartnerName.toLowerCase().startsWith(partnerName.toLowerCase())) {
						JSONObject pd = JsonUtill.getJsonObject(singleJson,
								MasterDataManagmentServiceConstants.PARTNER_DETAILS);
						JSONObject tcd = JsonUtill.getJsonObject(pd,
								MasterDataManagmentServiceConstants.TRANING_CENTER_DETAILS);
						String jsonTcId = JsonUtill.getString(tcd, MasterDataManagmentServiceConstants.TC_ID);
						if (jsonTcId.toLowerCase().startsWith(tcId.toLowerCase())) {
							jsonArrayResponse.add(singleJson);
						}
					}
				}
			} else if (!projectName.isEmpty()) {
				for (int i = 0; i < masterJsonArray.size(); i++) {
					JSONObject singleJson = (JSONObject) masterJsonArray.get(i);
					JSONObject jsonProjectDetails = JsonUtill.getJsonObject(singleJson,
							MasterDataManagmentServiceConstants.PROJECT_DETAILS);
					String jsonProjectName = JsonUtill.getString(jsonProjectDetails,
							MasterDataManagmentServiceConstants.PROJECT_NAME);
					if (jsonProjectName.toLowerCase().startsWith(projectName.toLowerCase())) {
						jsonArrayResponse.add(singleJson);
					}
				}
			} else if (!partnerName.isEmpty()) {
				for (int i = 0; i < masterJsonArray.size(); i++) {
					JSONObject singleJson = (JSONObject) masterJsonArray.get(i);
					JSONObject parnterdetailsjson = JsonUtill.getJsonObject(singleJson,
							MasterDataManagmentServiceConstants.PARTNER_DETAILS);
					String jsonPartnerName = JsonUtill.getString(parnterdetailsjson,
							MasterDataManagmentServiceConstants.PARTNER_NAME);
					if (jsonPartnerName.toLowerCase().startsWith(partnerName.toLowerCase())) {
						jsonArrayResponse.add(singleJson);
					}
				}
			} else if (!tcId.isEmpty()) {
				for (int i = 0; i < masterJsonArray.size(); i++) {
					JSONObject singleJson = (JSONObject) masterJsonArray.get(i);
					JSONObject pd = JsonUtill.getJsonObject(singleJson,
							MasterDataManagmentServiceConstants.PARTNER_DETAILS);
					JSONObject tcd = JsonUtill.getJsonObject(pd,
							MasterDataManagmentServiceConstants.TRANING_CENTER_DETAILS);
					String jsonTcId = JsonUtill.getString(tcd, MasterDataManagmentServiceConstants.TC_ID);
					if (jsonTcId.toLowerCase().startsWith(tcId.toLowerCase())) {
						jsonArrayResponse.add(singleJson);
					}
				}
			}
		} catch (Exception e) {
			throw new MasterDataManagmentServiceException("unable to filter partners " + e.getMessage());
		}
		JSONObject responseJson = new JSONObject();
		responseJson.put(MasterDataManagmentServiceConstants.PROJECT_MASTER_DATA, jsonArrayResponse);
		return responseJson.toString();
	}

	private void validateProjectId(String tenantId, String projectId)
			throws ProjectDaoException, MasterDataManagmentServiceException {
		boolean projectExist = surveyDao.isProjectExist(tenantId, projectId);
		if (!projectExist) {
			logger.error("project Does not exist");
			throw new MasterDataManagmentServiceException("project Does not exist");
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject uploadMasterDataManagment(String tenantId, List<ClientMasterData> listOfClientMD,
			String projectId) throws MasterDataManagmentServiceException {
		logger.debug(".in buildJsonArrayFromClientMasterData ");
		boolean isGradingEnable = false;
		int failureCount = 0;
		PartnerDetails partnerDetails = null;
		TrainingCenterDetails tcDetails = null;
		JSONArray failureJsonArray = new JSONArray();
		int totalNoOfRecords = listOfClientMD.size();
		Transport transport = null;
		try {
			transport = MailSessionInstance.getMailSession().getTransport();
			transport.connect();
			validateProjectId(tenantId, projectId);
			List<String> ratingJSONS = new ArrayList<String>();
			List<String> gradingJSONS = new ArrayList<String>();
			for (int i = 0; i < totalNoOfRecords; i++) {
				ClientMasterData clientMaster = listOfClientMD.get(i);
				Connection connection = null;
				try {
					connection = MSSqlServerUtill.getConnection();
					connection.setAutoCommit(false);
					logger.info("**************************Started connection*******************************");
					long start = System.currentTimeMillis();
					String partnerProjectId = clientMaster.getPartnerProjectId();
					boolean isPartnerProjectExist = verifyParterProjectId(connection, tenantId, partnerProjectId);
					PartnerDetails partner = iMasterDataManagmentDao.getPartnerByPartnerNameWithCaseInSensitive(
							connection, tenantId, clientMaster.getPartnerName());
					if (!isPartnerProjectExist && partner != null) {
						logger.debug("partner exist but mapping does not exist");
						isPartnerProjectExist = true;
					}
					logger.debug("PartnerProjectExist is present : " + isPartnerProjectExist);
					partnerDetails = buildPartenrDetailsFromClientMaster(connection, transport, tenantId, clientMaster);
					tcDetails = buildTriningCenterDetailsFromClientMaster(connection, transport, tenantId,
							clientMaster);
					if (isPartnerProjectExist) {
						String isGradingEnableString = clientMaster.getIsGradingEnable();
						if (!(isGradingEnableString.isEmpty())) {
							isGradingEnable = Boolean.parseBoolean(isGradingEnableString);
							logger.debug("making isGradingEnable :::" + isGradingEnable);
						}
						String partnerId = partner.getPartnerId();
						partnerDetails.setPartnerId(partnerId);
						tcDetails.setPartnerId(partnerId);
						updateMasterDataManagment(connection, tenantId, tcDetails, projectId, partnerProjectId,
								partnerDetails);
						GradingEnable gradingEnable = iMasterDataManagmentDao.getGradingEnableByProjectIdAndPartnerId(
								connection, tenantId, projectId, tcDetails.getPartnerId());
						if (gradingEnable == null) {
							isGradingEnable = false;
							if (!(isGradingEnableString.isEmpty())) {
								isGradingEnable = Boolean.parseBoolean(isGradingEnableString);
								logger.debug("making isGradingEnable :::" + isGradingEnable);
							}
							GradingEnable newGradingEnable = new GradingEnable(projectId, partnerId, isGradingEnable);
							iMasterDataManagmentDao.addGradingEnable(connection, tenantId, newGradingEnable);
						} else {
							boolean oldGradingEnable = gradingEnable.isGradingEnable();
							if (oldGradingEnable == true && isGradingEnable == false) {
								logger.info("Old Entry is true and new Entery is false");
								List<GradingType> gradingTypeDataList = iSchedulerDao
										.getGradingTypeDataByPartnerIdAndProjectId(connection, tenantId, partnerId,
												projectId);
								for (GradingType gt : gradingTypeDataList) {
									// && !gt.isAuditStatus()
									if (gt.getAuditDate() != 0 && gt.isAuditCancled()) {
										throw new MasterDataManagmentServiceException(
												"cant change the GradingEnable when there is audit");
									}
								}
								iMasterDataManagmentDao.updateGradingEnable(connection, tenantId, isGradingEnable,
										partnerId, projectId);
								iSchedulerDao.deleteGradingTypeData(connection, tenantId, partnerId, projectId);
							} else if (oldGradingEnable == false && isGradingEnable == true) {
								logger.debug("Old Entry is false and new Entery is True");
								iMasterDataManagmentDao.updateGradingEnable(connection, tenantId, isGradingEnable,
										partnerId, projectId);
							}
						}
					} else {
						logger.info("adding new partner");
						isGradingEnable = false;
						String isGradingEnableString = clientMaster.getIsGradingEnable();
						if (!(isGradingEnableString.isEmpty())) {
							isGradingEnable = Boolean.parseBoolean(isGradingEnableString);
						}
						String partnerId = addMasterDataManagment(connection, tenantId, partnerDetails, tcDetails,
								projectId, partnerProjectId);
						partnerDetails.setPartnerId(partnerId);
						GradingEnable gradingEnable = new GradingEnable(projectId, partnerId, isGradingEnable);
						iMasterDataManagmentDao.addGradingEnable(connection, tenantId, gradingEnable);
						userDao.updateCenterIdByUserId(connection, tenantId, tcDetails.getCenterInchargeId(),
								tcDetails.getTcId());
					}
					logger.debug("adding forms to scheduler");
					boolean isGradingRequired = false;
					if (isGradingEnable) {
						List<GradingType> gradingType = iSchedulerDao.getGradingTypeDataByPartnerIdAndProjectId(
								connection, tenantId, partnerDetails.getPartnerId(), projectId);
						if (gradingType.isEmpty()) {
							isGradingRequired = true;
						}
					}
					List<FormData> formsForScheduler = surveyDao.getFormsByProjectId(connection, tenantId, projectId);
					for (FormData form : formsForScheduler) {
						addSchedulerMapping(connection, tenantId, form, partnerDetails, tcDetails, isGradingRequired,
								ratingJSONS, gradingJSONS);
					}

					long end = System.currentTimeMillis();
					logger.debug("************************** Commiting connection *******************************");
					logger.debug("it took " + (end - start) + " ms to commit One Record");
					connection.commit();
				} catch (Exception e) {
					if (connection != null) {
						try {
							logger.error("!!!! Rolling Back !!!!");
							connection.rollback();
						} catch (SQLException e1) {
						}
					}
					failureCount++;
					JSONObject failureJson = new JSONObject();
					failureJson.put("rowNumber", (i + 1));
					failureJson.put("partnerName", clientMaster.getPartnerName());
					failureJson.put("trainingCenterId", clientMaster.getTcId());
					failureJson.put("errorMsg", e.getMessage().toString());
					failureJsonArray.add(failureJson);
				} finally {
					MSSqlServerUtill.close(null, connection);
				}
			}
			logger.debug("Rating JSON size: " + ratingJSONS.size());
			logger.debug("Grading JSON size: " + gradingJSONS.size());
			if (ReportingDBService.ENABLED) {
				if (ratingJSONS.size() > 0) {
					AddRatingJobAssigner assigner = new AddRatingJobAssigner();
					assigner.assignAddRatingJobToDatabase(tenantId, ratingJSONS);
					logger.debug("Rating forms assigned.");
				}
				if (gradingJSONS.size() > 0) {
					AddGradingJobAssigner assigner = new AddGradingJobAssigner();
					assigner.assignAddGradingJobToDatabase(tenantId, gradingJSONS);
					logger.debug("Grading forms assigned.");
				}
			}
		} catch (Exception e) {
			throw new MasterDataManagmentServiceException("unable to upload master Data ::" + e.getMessage());
		} finally {
			MailSessionInstance.closeTransport(transport);
		}
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("totalRecord", totalNoOfRecords);
		jsonResponse.put("failureRecords", failureJsonArray);
		jsonResponse.put("failuresRecordsCount", failureCount);
		return jsonResponse;
	}

	private void updateMasterDataManagment(Connection connection, String tenantId, TrainingCenterDetails tcDetails,
			String projectId, String partnerProjectId, PartnerDetails partnerDetails)
			throws MasterDataManagmentDaoException, UserDaoException, MasterDataManagmentServiceException,
			PartnerDaoException {
		logger.debug("in updateMasterDataManagment");
		String tcId = tcDetails.getTcId();
		boolean isTcIdExist = verifyTcIdExists(connection, tenantId, tcId);
		logger.debug(tcId + " :: is TcId exist ? " + isTcIdExist);
		if (!isTcIdExist) {
			logger.debug("updating existing partner with new tcDetails :: " + tcDetails);
			iMasterDataManagmentDao.addTrainingCenterDetails(connection, tenantId, tcDetails);
			userDao.updateCenterIdByUserId(connection, tenantId, tcDetails.getCenterInchargeId(), tcId);
		}
		ProjectMapping mapping = buildProjectMapping(projectId, tcDetails.getPartnerId(), partnerProjectId,
				partnerDetails.getClientSponsorId(), tcId);
		iMasterDataManagmentDao.addProjectMapping(connection, tenantId, mapping);
	}

	private String addMasterDataManagment(Connection connection, String tenantId, PartnerDetails partnerDetails,
			TrainingCenterDetails tcDetails, String projectId, String partnerProjectId)
			throws MasterDataManagmentDaoException, UserDaoException, MasterDataManagmentServiceException {
		String partnerId = iMasterDataManagmentDao.addPartner(connection, tenantId, partnerDetails);
		tcDetails.setPartnerId(partnerId);
		iMasterDataManagmentDao.addTrainingCenterDetails(connection, tenantId, tcDetails);
		ProjectMapping projectMapping = buildProjectMapping(projectId, partnerId, partnerProjectId,
				partnerDetails.getClientSponsorId(), tcDetails.getTcId());
		iMasterDataManagmentDao.addProjectMapping(connection, tenantId, projectMapping);
		return partnerId;
	}

	private ProjectMapping buildProjectMapping(String projectId, String partnerId, String partnerProjectId,
			String clientSponsorId, String tcId) {
		ProjectMapping projectMapping = new ProjectMapping();
		projectMapping.setPartnerProjectId(partnerProjectId);
		projectMapping.setTcId(tcId);
		projectMapping.setPartnerId(partnerId);
		projectMapping.setProjectId(projectId);
		return projectMapping;
	}

	@SuppressWarnings("unchecked")
	private PartnerDetails buildPartenrDetailsFromClientMaster(Connection connection, Transport transport,
			String databaseName, ClientMasterData clientMaster) throws UserDaoException,
			MasterDataManagmentServiceException, UserRegistrationServiceException, JsonUtillException {
		PartnerDetails partnerDetails = new PartnerDetails();
		String clientSponsorId = "";
		String clientSponsorEmail = clientMaster.getClientSponsorEmail();
		User user = userDao.getUserByEmail(connection, databaseName, clientSponsorEmail);
		if (user == null) {
			logger.debug("creating the new User");
			JSONObject json = new JSONObject();
			json.put(MasterDataManagmentServiceConstants.USER_FIRST_NAME, clientMaster.getClientSponsorFirstName());
			json.put(MasterDataManagmentServiceConstants.USER_LAST_NAME, clientMaster.getClientSponsorLastName());
			json.put(MasterDataManagmentServiceConstants.USER_PHONE, clientMaster.getClientSponsorContact());
			json.put(MasterDataManagmentServiceConstants.USER_EMAIL, clientMaster.getClientSponsorEmail());
			json.put(MasterDataManagmentServiceConstants.USER_ROLE,
					UserAccessManagementServiceConstants.CLIENT_SPONSOR);
			String registerUser = userService.registerUser(connection, transport, json.toString(), databaseName);
			JSONObject responseJson = (JSONObject) JSONValue.parse(registerUser);
			clientSponsorId = JsonUtill.getString(responseJson, UserAccessManagementServiceConstants.USER_ID);

		} else {
			if (!(user.getUserRole().equalsIgnoreCase(UserAccessManagementServiceConstants.CLIENT_SPONSOR))) {
				logger.error("user exist but user is not Client sponsor");
				throw new MasterDataManagmentServiceException("user exist but user is not Client sponsor");
			}
			clientSponsorId = user.getUserId();
		}

		partnerDetails.setPartnerName(clientMaster.getPartnerName());
		partnerDetails.setClientSponsorId(clientSponsorId);
		return partnerDetails;
	}

	@SuppressWarnings("unchecked")
	private TrainingCenterDetails buildTriningCenterDetailsFromClientMaster(Connection connection, Transport transport,
			String tenantId, ClientMasterData clientMaster) throws UserDaoException,
			MasterDataManagmentServiceException, UserRegistrationServiceException, JsonUtillException {
		TrainingCenterDetails tcDetails = new TrainingCenterDetails();
		String centerInchargeEmail = clientMaster.getCenterInchargeEmail();
		String centerInchargrId = "";
		// we have done validation on it while file is uploaded;
		User user = userDao.getUserByEmail(connection, tenantId, centerInchargeEmail);
		if (user == null) {
			logger.debug("creating the new User");
			JSONObject json = new JSONObject();
			json.put(MasterDataManagmentServiceConstants.USER_FIRST_NAME, clientMaster.getCenterInchargeFirstName());
			json.put(MasterDataManagmentServiceConstants.USER_LAST_NAME, clientMaster.getCenterInchargeLastName());
			json.put(MasterDataManagmentServiceConstants.USER_PHONE, clientMaster.getCenterInchargeContact());
			json.put(MasterDataManagmentServiceConstants.USER_EMAIL, clientMaster.getCenterInchargeEmail());
			json.put(MasterDataManagmentServiceConstants.USER_ROLE,
					UserAccessManagementServiceConstants.CENTER_IN_CHARGE);
			String registerUser = userService.registerUser(connection, transport, json.toString(), tenantId);
			JSONObject responseJson = (JSONObject) JSONValue.parse(registerUser);
			centerInchargrId = JsonUtill.getString(responseJson, UserAccessManagementServiceConstants.USER_ID);
		} else {
			centerInchargrId = user.getUserId();
			if (!(user.getUserRole().equalsIgnoreCase(UserAccessManagementServiceConstants.CENTER_IN_CHARGE))) {
				logger.error("user exist but not user role is not Center-In-Charge");
				throw new MasterDataManagmentServiceException("user exist, but user role is not Center-In-Charge");
			}
			String centerId = user.getCenterId();
		}
		tcDetails.setCenterInchargeId(centerInchargrId);
		tcDetails.setCenterAddress(clientMaster.getCenterAddress());
		tcDetails.setDistrict(clientMaster.getDistrict());
		tcDetails.setLatitude(clientMaster.getLatitude());
		tcDetails.setLongitude(clientMaster.getLongitude());
		tcDetails.setTcName(clientMaster.getTcName());
		tcDetails.setTcId(clientMaster.getTcId());
		return tcDetails;
	}

	private boolean verifyTcIdExists(Connection connection, String tenantId, String tcId) throws PartnerDaoException {
		logger.debug("checking whether the tcid present or not");
		TrainingCenterDetails tcDetails = ipartnerDao.getTrainingCenterDetailsByTcId(connection, tenantId, tcId);
		if (tcDetails == null)
			return false;
		return true;

	}

	private boolean verifyParterProjectId(Connection connection, String tenantId, String clientMaster)
			throws MasterDataManagmentDaoException {
		List<ProjectMapping> masterMappingData = iMasterDataManagmentDao.getProjectMappingData(connection, tenantId);
		logger.debug("project mapping " + masterMappingData);
		for (ProjectMapping singleMapping : masterMappingData) {
			if (singleMapping.getPartnerProjectId().equalsIgnoreCase(clientMaster)) {
				logger.debug("partenr project id is present");
				return true;
			}
		}
		logger.debug("partner project not present");
		return false;
	}

	private List<ClientMasterData> getListOfClientMasteData(InputStream inputStream)
			throws IOException, MasterDataManagmentServiceException {
		List<ClientMasterData> clientMasterList = new ArrayList<ClientMasterData>();
		XSSFWorkbook workBook = null;
		try {
			workBook = new XSSFWorkbook(inputStream);
//			Iterator<Sheet> iteratorSheets = workBook.iterator();
			XSSFSheet sheet = workBook.getSheetAt(0);
			// we are not iterating over sheets in excel file
			// while (iteratorSheets.hasNext()) {
			// Sheet singleSheet = iteratorSheets.next();
			int rowsCount = sheet.getLastRowNum();
			logger.debug("rowsCount :" + rowsCount);
			String maxRowString = ReadPropertiesFile
					.readRequestProperty(MasterDataManagmentServiceConstants.UPLOAD_MAX_ROWS);
			int maxRows = Integer.parseInt(maxRowString);
			if (maxRows < rowsCount) {
				throw new MasterDataManagmentServiceException("rows exceed, only " + maxRows + " row's allowed");
			}
			for (int i = 1; i <= rowsCount; i++) {
				Row singleRow = sheet.getRow(i);
				if (verifyRowIsEmpty(singleRow)) {
					break;
				}
				ClientMasterData masterData = buildClientMasterData(singleRow);
				clientMasterList.add(masterData);
			}
			return clientMasterList;
		} finally {
			if (workBook != null) {
				workBook.close();
			}
		}

	}

	private ClientMasterData buildClientMasterData(Row singleRow) throws MasterDataManagmentServiceException {
		DataFormatter formatter = new DataFormatter();
		ClientMasterData masterData = new ClientMasterData();
		int i = 1;
		// is GradingEnable or not
		String isGradingEnableString = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		// Partner Name
		String partnerName = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(partnerName, MasterDataManagmentServiceConstants.PARTNER_NAME,
				MasterDataManagmentServiceConstants.PARTNER_NAME_LENGTH);
		// clientSponsor first name
		String csFirstName = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(csFirstName, MasterDataManagmentServiceConstants.CLIENT_SPONSOR_FIRST_NAME,
				MasterDataManagmentServiceConstants.FIRST_NAME_LENGTH);

		// clientSponsor LastName
		String csLastName = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		if (!(csLastName.isEmpty())) {
			if (csLastName.toCharArray().length > MasterDataManagmentServiceConstants.LAST_NAME_LENGTH) {
				throw new MasterDataManagmentServiceException("Last name column or field should be lessthan "
						+ MasterDataManagmentServiceConstants.LAST_NAME_LENGTH + " characters ");
			}
		}
		// clientSponsor Email
		String clientSponsorEmail = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(clientSponsorEmail, MasterDataManagmentServiceConstants.CLIENT_SPONSOR_EMAIL,
				MasterDataManagmentServiceConstants.EMAIL_LENGTH);
		// clientSponsor Contact
		String clientSponsorContact = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(clientSponsorContact, MasterDataManagmentServiceConstants.CLIENT_SPONSOR_CONTACT,
				MasterDataManagmentServiceConstants.CONTACT_LENGTH);
		validatePhoneNumber(clientSponsorContact);
		// partner project Id
		String partnerProjectId = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(partnerProjectId, MasterDataManagmentServiceConstants.PARTNER_PROJECT_ID,
				MasterDataManagmentServiceConstants.PARTER_PROJECT_ID_LENGETH);
		// Tc id
		String tcId = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(tcId, MasterDataManagmentServiceConstants.TC_ID,
				MasterDataManagmentServiceConstants.TC_ID_LENGTH);

		String tcName = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		if (!(tcName.isEmpty())) {
			if (csLastName.toCharArray().length > MasterDataManagmentServiceConstants.TC_NAME_LENGTH) {
				throw new MasterDataManagmentServiceException("Last name column or field should be lessthan "
						+ MasterDataManagmentServiceConstants.TC_NAME_LENGTH + " characters ");
			}
		}
		// Training center address
		String centerAddress = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(centerAddress, MasterDataManagmentServiceConstants.CENTER_ADDRESS,
				MasterDataManagmentServiceConstants.CENTER_ADDRESS_LENGTH);
		// district
		String district = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(district, MasterDataManagmentServiceConstants.DISTRICT,
				MasterDataManagmentServiceConstants.DISTRICT_LENGTH);

		// centerIncharge first name
		String centerInchargeFirstName = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(centerInchargeFirstName, MasterDataManagmentServiceConstants.CENTER_INCHARGE_FIRST_NAME,
				MasterDataManagmentServiceConstants.FIRST_NAME_LENGTH);

		// centerIncharge LastName
		String ciLastName = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		if (!(ciLastName.isEmpty())) {
			if (csLastName.toCharArray().length > MasterDataManagmentServiceConstants.LAST_NAME_LENGTH) {
				throw new MasterDataManagmentServiceException("Last name column or field should be lessthan "
						+ MasterDataManagmentServiceConstants.LAST_NAME_LENGTH + " characters ");
			}
		}
		// centerIncharge email
		String centerInchargeEmail = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(centerInchargeEmail, MasterDataManagmentServiceConstants.CENTER_INCHARGE_EMAIL,
				MasterDataManagmentServiceConstants.EMAIL_LENGTH);
		validateEmail(centerInchargeEmail);
		// centerIncharge Contact
		String centerInchargeContact = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(centerInchargeContact, MasterDataManagmentServiceConstants.CENTER_INCHARGE_CONTACT,
				MasterDataManagmentServiceConstants.CONTACT_LENGTH);
		validatePhoneNumber(centerInchargeContact);
		// longitude
		String longitude = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(longitude, MasterDataManagmentServiceConstants.LONGITUDE, 0);
		// latitude
		String latitude = formatter.formatCellValue(singleRow.getCell(i++)).trim();
		validateFields(latitude, MasterDataManagmentServiceConstants.LATITUDE, 0);
		masterData.setIsGradingEnable(isGradingEnableString);
		masterData.setPartnerName(partnerName);
		masterData.setClientSponsorFirstName(csFirstName);
		masterData.setClientSponsorLastName(csLastName);
		masterData.setClientSponsorEmail(clientSponsorEmail);
		masterData.setClientSponsorContact(clientSponsorContact);
		masterData.setTcName(tcName);
		masterData.setCenterInchargeFirstName(centerInchargeFirstName);
		masterData.setCenterInchargeLastName(ciLastName);
		masterData.setCenterInchargeContact(centerInchargeContact);
		masterData.setPartnerProjectId(partnerProjectId);
		masterData.setTcId(tcId);
		masterData.setCenterAddress(centerAddress);
		masterData.setDistrict(district);
		masterData.setCenterInchargeEmail(centerInchargeEmail);
		masterData.setLongitude(longitude);
		masterData.setLatitude(latitude);
		return masterData;
	}

	private void validateEmail(String email) throws MasterDataManagmentServiceException {
		String emailRegex = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
		Pattern pattern = Pattern.compile(emailRegex);
		boolean result = pattern.matcher(email).matches();
		if (!result) {
			throw new MasterDataManagmentServiceException(email + " is invalid email formate");
		}
	}

	private void validatePhoneNumber(String phoneNo) throws MasterDataManagmentServiceException {
		Pattern pattern = Pattern.compile("[7-9][0-9]{9}");
		boolean result = pattern.matcher(phoneNo).matches();
		if (!result) {
			throw new MasterDataManagmentServiceException("invalid phone number formate");
		}
	}

	private boolean verifyRowIsEmpty(Row singleRow) {
		DataFormatter formatter = new DataFormatter();
		String sNo = formatter.formatCellValue(singleRow.getCell(0)).trim();
		String tcId = singleRow.getCell(8).getStringCellValue().trim();
		if (sNo == null || sNo.isEmpty() && tcId.isEmpty() || tcId == null) {
			return true;
		}
		return false;
	}

	private void validateFields(String fieldValue, String fieldName, int expectedLength)
			throws MasterDataManagmentServiceException {
		if (fieldValue == null || fieldValue.isEmpty()) {
			throw new MasterDataManagmentServiceException(fieldName + " field cant be null or empty");
		}
		if (expectedLength > 0 && fieldValue.toCharArray().length > expectedLength) {
			throw new MasterDataManagmentServiceException(
					fieldName + " field should be lessthan " + expectedLength + " characters");
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonFromProjectMapping(String tenantId, ProjectMapping mapping)
			throws SurveyResponseDaoException, MasterDataManagmentDaoException, ProjectDaoException, UserDaoException,
			PartnerDaoException {
		logger.debug(".in buildJsonFromProjectMapping , ProjectMapping ::" + mapping);

		JSONObject singleMasterDataJson = new JSONObject();
		String projectMappingId = mapping.getProjectMappingId();
		String tcId = mapping.getTcId();
		String partnerId = mapping.getPartnerId();
		String projectId = mapping.getProjectId();
		String partnerProjectId = mapping.getPartnerProjectId();

		GradingEnable gradingEnable = iMasterDataManagmentDao.getGradingEnableByProjectIdAndPartnerId(tenantId,
				projectId, partnerId);

		singleMasterDataJson.put(MasterDataManagmentServiceConstants.IS_GRADING_ENABLED,
				gradingEnable.isGradingEnable());

		singleMasterDataJson.put(MasterDataManagmentServiceConstants.PARTNER_PROJECT_ID, partnerProjectId);

		singleMasterDataJson.put(MasterDataManagmentServiceConstants.PROJECT_MAPPING_ID, projectMappingId);

		JSONObject partnersDetailsJson = buildJsonFromPartnerId(tenantId, partnerId, tcId);
		singleMasterDataJson.put(MasterDataManagmentServiceConstants.PARTNER_DETAILS, partnersDetailsJson);

		JSONObject projectDetailsJson = buildJsonFromProjectId(tenantId, projectId);
		singleMasterDataJson.put(MasterDataManagmentServiceConstants.PROJECT_DETAILS, projectDetailsJson);

		return singleMasterDataJson;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonFromProjectId(String tenantId, String projectId) throws ProjectDaoException {
		if (projectId == null || projectId.isEmpty()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(MasterDataManagmentServiceConstants.PROJECT_ID, "N/A");
			jsonObject.put(MasterDataManagmentServiceConstants.PROJECT_NAME, "N/A");
			return jsonObject;
		} else {
			ProjectData projectData = surveyDao.getProjectById(tenantId, projectId);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(MasterDataManagmentServiceConstants.PROJECT_ID, projectId);
			jsonObject.put(MasterDataManagmentServiceConstants.PROJECT_NAME, projectData.getProjectName());
			return jsonObject;
		}

	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonFromPartnerId(String tenantId, String partnerId, String tcId)
			throws MasterDataManagmentDaoException, UserDaoException, PartnerDaoException {
		PartnerDetails partnerDetails = ipartnerDao.getPartnerById(tenantId, partnerId);
		JSONObject partnerDetailsJson = buildJsonFromPartnerDetails(tenantId, partnerDetails);
		JSONObject trainingCenter = buildJsonFromTcId(tenantId, tcId);
		partnerDetailsJson.put(MasterDataManagmentServiceConstants.TRANING_CENTER_DETAILS, trainingCenter);
		return partnerDetailsJson;
	}

	private JSONObject buildJsonFromTcId(String databaseName, String tcId)
			throws MasterDataManagmentDaoException, UserDaoException, PartnerDaoException {
		TrainingCenterDetails tcDetails = ipartnerDao.getTrainingCenterDetailsByTcId(databaseName, tcId);
		JSONObject tcJson = buildJsonFromTrainingCenterDetails(databaseName, tcDetails);
		return tcJson;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonFromPartnerDetails(String tenantId, PartnerDetails partner) throws UserDaoException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(MasterDataManagmentServiceConstants.PARTNER_NAME, partner.getPartnerName());
		jsonObject.put(MasterDataManagmentServiceConstants.PARTNER_ID, partner.getPartnerId());
		String userId = partner.getClientSponsorId();
		User user = userDao.getUserByUserId(tenantId, userId);
		jsonObject.put(MasterDataManagmentServiceConstants.CLIENT_SPONSOR_FIRST_NAME, user.getFirstName());
		jsonObject.put(MasterDataManagmentServiceConstants.CLIENT_SPONSOR_LAST_NAME, user.getLastName());
		jsonObject.put(MasterDataManagmentServiceConstants.CLIENT_SPONSOR_CONTACT, user.getPhone());
		jsonObject.put(MasterDataManagmentServiceConstants.CLIENT_SPONSOR_EMAIL, user.getEmail());
		jsonObject.put(MasterDataManagmentServiceConstants.CLIENT_SPONSOR_ID, userId);
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildJsonFromTrainingCenterDetails(String tenantId,
			TrainingCenterDetails singleTrainingCenterDetails) throws UserDaoException {
		JSONObject trainingCenterJsonObj = new JSONObject();
		trainingCenterJsonObj.put(MasterDataManagmentServiceConstants.TC_ID, singleTrainingCenterDetails.getTcId());
		trainingCenterJsonObj.put(MasterDataManagmentServiceConstants.CENTER_ADDRESS,
				singleTrainingCenterDetails.getCenterAddress());
		trainingCenterJsonObj.put(MasterDataManagmentServiceConstants.DISTRICT,
				singleTrainingCenterDetails.getDistrict());
		trainingCenterJsonObj.put(MasterDataManagmentServiceConstants.TC_NAME, singleTrainingCenterDetails.getTcName());
		String centerInchargeId = singleTrainingCenterDetails.getCenterInchargeId();
		User user = userDao.getUserByUserId(tenantId, centerInchargeId);
		trainingCenterJsonObj.put(MasterDataManagmentServiceConstants.CENTER_INCHARGE_ID, centerInchargeId);
		trainingCenterJsonObj.put(MasterDataManagmentServiceConstants.CENTER_INCHARGE_FIRST_NAME, user.getFirstName());
		trainingCenterJsonObj.put(MasterDataManagmentServiceConstants.CENTER_INCHARGE_LAST_NAME, user.getLastName());
		trainingCenterJsonObj.put(MasterDataManagmentServiceConstants.CENTER_INCHARGE_EMAIL, user.getEmail());
		trainingCenterJsonObj.put(MasterDataManagmentServiceConstants.CENTER_INCHARGE_CONTACT, user.getPhone());
		trainingCenterJsonObj.put(MasterDataManagmentServiceConstants.LATITUDE,
				singleTrainingCenterDetails.getLatitude());
		trainingCenterJsonObj.put(MasterDataManagmentServiceConstants.LONGITUDE,
				singleTrainingCenterDetails.getLongitude());
		return trainingCenterJsonObj;
	}

	private void validateId(String id, String keyName) throws SurveyResponseServiceException {
		if (id == null || id.isEmpty()) {
			throw new SurveyResponseServiceException(" invalid/empty " + keyName);
		}
	}

}
