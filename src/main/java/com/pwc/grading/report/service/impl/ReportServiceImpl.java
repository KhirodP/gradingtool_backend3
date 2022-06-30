package com.pwc.grading.report.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
//import org.codehaus.jackson.JsonGenerationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.pwc.grading.masterdata.dao.IMasterDataManagmentDao;
import com.pwc.grading.masterdata.dao.exception.MasterDataManagmentDaoException;
import com.pwc.grading.masterdata.model.GradingEnable;
import com.pwc.grading.masterdata.model.ProjectMapping;
import com.pwc.grading.mediabucket.dao.IMediaDao;
import com.pwc.grading.mediabucket.model.Media;
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
import com.pwc.grading.report.model.Report;
import com.pwc.grading.report.partner.model.PartnerDetailsReport;
import com.pwc.grading.report.project.model.ProjectReport;
import com.pwc.grading.report.service.AttachmentConstants;
import com.pwc.grading.report.service.IReportService;
import com.pwc.grading.report.service.ReportServiceConstants;
import com.pwc.grading.report.service.exception.ReportServiceException;
import com.pwc.grading.report.tcDetails.model.TrainingCenterReport;
import com.pwc.grading.scheduler.dao.ISchedulerDao;
import com.pwc.grading.scheduler.dao.exception.SchedulerDaoException;
import com.pwc.grading.scheduler.model.GradingType;
import com.pwc.grading.scheduler.model.RatingType;
import com.pwc.grading.surveyresponse.dao.ISurveyResponseDao;
import com.pwc.grading.surveyresponse.dao.exception.SurveyResponseDaoException;
import com.pwc.grading.surveyresponse.model.SurveyResponse;
import com.pwc.grading.user.dao.IUserDao;
import com.pwc.grading.user.dao.exception.UserDaoException;
import com.pwc.grading.user.model.User;
import com.pwc.grading.util.JsonUtill;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.exception.JsonUtillException;

/**
 * Implementation class for {@link IReportService}
 *
 */
public class ReportServiceImpl implements IReportService {
	private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
	@Inject
	IProjectDao projectDao;
	@Inject
	ISurveyResponseDao surveyResponseDao;
	@Inject
	IMasterDataManagmentDao imasterDao;
	@Inject
	IUserDao userDao;
	@Inject
	private IPartnerDao ipartnerDao;
	@Inject
	private ISchedulerDao iSchedulerDao;
	@Inject
	private IMediaDao mediaDAO;

	/**
	 * This method is used to preview the report.
	 * @param tenantId the database name.
	 * @param projectId the id of the project.
	 * @param partnerId  the id of the partner.
	 * @param tcId the id of the training center.
	 * @param reportType  the type of the report.
	 * @return the JSON for previewing the report. 
	 * @throws ReportServiceException if any exception occurs while performing this operation.
	 */
	@Override
	public String getReport(String tenantId, String projectId, String partnerId, String tcId, String reportType)
			throws ReportServiceException {
		JSONObject jsonResponse = null;
		try {
			if (reportType.equalsIgnoreCase(ReportServiceConstants.PROJECT_REPORT)) {
				jsonResponse = buildProjectReportJson(tenantId, projectId);
			} else if (reportType.equalsIgnoreCase(ReportServiceConstants.PARTNER_REPORT)) {
				jsonResponse = buildPartnerReportJson(tenantId, projectId, partnerId);
			} else if (reportType.equalsIgnoreCase(ReportServiceConstants.TRAINING_CENTER_REPORT)) {
				jsonResponse = buildTcReportJson(tenantId, projectId, partnerId, tcId);
			} else {
				logger.error(reportType + " report type does not exist");
				throw new ReportServiceException(reportType + " report type does not exist");
			}
		} catch (ReportServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable to generate report :: " + e.getMessage(), e);
			throw new ReportServiceException("unable to generate report :: " + e.getMessage(), e);
		}
		logger.debug("response Json is::: " + jsonResponse);
		return jsonResponse.toJSONString();
	}

	/**
	 * This method is used to Download the report.
	 * @param tenantId the database name.
	 * @param projectId the id of the project.
	 * @param partnerId  the id of the partner.
	 * @param tcId the id of the training center.
	 * @param reportType  the type of the report.
	 * @return  the report object which contains the details of the report.
	 * @throws ReportServiceException if any exception occurs while performing this operation.
	 */
	@Override
	public Report getReportDownload(String tenantId, String projectId, String partnerId, String tcId, String reportType)
			throws ReportServiceException {
		JSONObject jsonResponse = null;
		Report report = null;
		try {
			if (reportType.equalsIgnoreCase(ReportServiceConstants.PROJECT_REPORT)) {
				jsonResponse = buildProjectReportJson(tenantId, projectId);
				report = buildProjectReportJson(jsonResponse);
			} else if (reportType.equalsIgnoreCase(ReportServiceConstants.PARTNER_REPORT)) {
				jsonResponse = buildPartnerReportJson(tenantId, projectId, partnerId);
				report = generatePartnerReportPdfFromJson(jsonResponse);
			} else if (reportType.equalsIgnoreCase(ReportServiceConstants.TRAINING_CENTER_REPORT)) {
				jsonResponse = buildTcReportJson(tenantId, projectId, partnerId, tcId);
				report = generateTCDetailsPdfFromJson(jsonResponse);
			} else {
				throw new ReportServiceException(reportType + " report type does not exist");
			}
			return report;
		} catch (ReportServiceException e) {
			logger.debug("unable to generate report :: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.debug("unable to generate report :: " + e.getMessage());
			throw new ReportServiceException("unable to generate report :: " + e.getMessage());
		}
	}

	/**
	 * This method is used to download attachments for the training center.
	 * @param tenantId the database name.
	 * @param projectId the id of the project.
	 * @param partnerId  the id of the partner.
	 * @param tcId the id of the training center.
	 * @param reportType  the type of the report.
	 * @return the media which contains the details of the attachment.
	 * @throws ReportServiceException if any exception occurs while performing this operation.
	 */
	@Override
	public Media getAttachementsDownload(String tenantId, String projectId, String partnerId, String tcId,
			String reportType) throws ReportServiceException {
		Media media = null;
		try {
			if (reportType.equalsIgnoreCase(ReportServiceConstants.TRAINING_CENTER_REPORT)) {
				media = getAttachementsForTrainingCenter(tenantId, projectId, partnerId, tcId);
//				media = TestgetAttachementsForTrainingCenter(tenantId, projectId, partnerId, tcId);
			} else {
				throw new ReportServiceException(reportType + " report type does not exist");
			}
		} catch (Exception e) {
			logger.error("Unable to get Attachments, " + e.getMessage());
			throw new ReportServiceException("Unable to get Attachments, " + e.getMessage());
		}
		return media;
	}

	/**
	 * This method is used to build the project report JSON
	 */
	private Report buildProjectReportJson(JSONObject jsonResponse) throws IOException, FOPException, TransformerException {
		Report report = new Report();
		ObjectMapper jsonMapper = new ObjectMapper();
		String string = jsonResponse.toString();
		logger.debug("json String is " + string);
		ProjectReport projectReport = jsonMapper.readValue(string, ProjectReport.class);
		XmlMapper xmlMapper = new XmlMapper();
		String projectReportXml = xmlMapper.writeValueAsString(projectReport);
		logger.debug("xml is :: " + projectReportXml);
//		InputStream inputStream = IOUtils.toInputStream(projectReportXml);  //Deprecated in latest IOUtils.
		InputStream inputStream = IOUtils.toInputStream(projectReportXml, Charset.defaultCharset());
//		String xsltLocation = getClass().getClassLoader()
//				.getResource(ReportServiceConstants.PROJECT_REPORT_XSLT_FILE_NAME).getFile();
		InputStream xsltfile = ReportServiceImpl.class.getClassLoader().getResourceAsStream(ReportServiceConstants.PROJECT_REPORT_XSLT_FILE_NAME);
//		File xsltfile = new File(xsltLocation);
		final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, byteOutput);

		TransformerFactory factory = TransformerFactory.newInstance();
		
		//Bug fix for XML external entity (XXE) attacks
//		logger.debug("Setting access_External_DTD property to TransformerFactory instance.");
//		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
//		logger.debug("Setting access_External_Stylesheet property to TransformerFactory instance.");
//		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
//		logger.debug("Two properties successfully set to TransformerFactory instance.");
		//end of bug fix.
		
		Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));
		transformer.setParameter(ReportServiceConstants.VERSION_PARAM, ReportServiceConstants.VERSION_NUMBER);
		Source src = new StreamSource(inputStream);
		Result res = new SAXResult(fop.getDefaultHandler());
		transformer.transform(src, res);

		InputStream projectReportPdf = new ByteArrayInputStream(byteOutput.toByteArray());
		report.setInputstream(projectReportPdf);
		report.setReportName(
				projectReport.getReportHeader().getProjectName() + "_" + ReportServiceConstants.PROJECT_REPORT_PDF);
		return report;
	}

	/**
	 * This method is used to generate the partner report from JSON
	 */
	private Report generatePartnerReportPdfFromJson(JSONObject jsonResponse) throws IOException, FOPException, TransformerException {
		ObjectMapper jsonMapper = new ObjectMapper();
		String string = jsonResponse.toString();
		logger.debug("json String is " + string);
		PartnerDetailsReport partnerReport = jsonMapper.readValue(string, PartnerDetailsReport.class);
		XmlMapper xmlMapper = new XmlMapper();
		String partnerReportXml = xmlMapper.writeValueAsString(partnerReport);
		logger.debug("xml is :: " + partnerReportXml);
//		InputStream inputStream = IOUtils.toInputStream(partnerReportXml);   //Deprecated in latest IOUtils.
		InputStream inputStream = IOUtils.toInputStream(partnerReportXml, Charset.defaultCharset());
//		String xsltLocation = getClass().getClassLoader()
//				.getResource(ReportServiceConstants.PARTNER_REPORT_XSLT_FILE_NAME).getFile();
		InputStream xsltfile = ReportServiceImpl.class.getClassLoader().getResourceAsStream(ReportServiceConstants.PARTNER_REPORT_XSLT_FILE_NAME);
		//File xsltfile = new File(xsltLocation);
		final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, byteOutput);

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));
		transformer.setParameter(ReportServiceConstants.VERSION_PARAM, ReportServiceConstants.VERSION_NUMBER);
		Source src = new StreamSource(inputStream);
		Result res = new SAXResult(fop.getDefaultHandler());
		transformer.transform(src, res);
		Report report = new Report();
		InputStream projectReportPdf = new ByteArrayInputStream(byteOutput.toByteArray());
		report.setInputstream(projectReportPdf);
		report.setReportName(
				partnerReport.getReportHeader().getPIA() + "_" + ReportServiceConstants.PARTNER_REPORT_PDF);
		return report;
	}

	/**
	 * This method is used to generate the training center report from JSON
	 */
	private Report generateTCDetailsPdfFromJson(JSONObject jsonResponse) throws IOException, FOPException, TransformerException {
		ObjectMapper jsonMapper = new ObjectMapper();
//		jsonMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
//		jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String string = jsonResponse.toString();
		logger.debug("json String is " + string);
		TrainingCenterReport tcReport = jsonMapper.readValue(string, TrainingCenterReport.class);
		logger.debug("tcReport :::" + tcReport);
		XmlMapper xmlMapper = new XmlMapper();
		String tcReportXml = xmlMapper.writeValueAsString(tcReport);
		logger.debug("xml is :: " + tcReportXml);
//		InputStream inputStream = IOUtils.toInputStream(tcReportXml);   //Deprecated in latest IOUtils.
		InputStream inputStream = IOUtils.toInputStream(tcReportXml, Charset.defaultCharset());
		String xsltLocation = "";
// not working code		
//		File xsltfile = null;
//		String xsltPath = ReadPropertiesFile.readRequestProperty(ReportServiceConstants.XSLT_LOCATION_TYPE);
//		if (xsltPath.equalsIgnoreCase(ReportServiceConstants.CLASSPATH)) {
//			logger.debug("serching in classpath");
//			xsltLocation = getClass().getClassLoader().getResource(ReportServiceConstants.TC_DETAILS_XSLT_FILE_NAME)
//					.getFile();
//			logger.debug("found file");
//			xsltfile = new File(xsltLocation);
//		} else {
//			xsltLocation = ReadPropertiesFile.readRequestProperty(ReportServiceConstants.XSLT_LOCATION);
//			xsltfile = new File(xsltLocation + ReportServiceConstants.TC_DETAILS_XSLT_FILE_NAME);
//		}
		
//	added after suggestion of sai kaushik
		
		InputStream xsltfile = null;
		String xsltPath = ReadPropertiesFile.readRequestProperty(ReportServiceConstants.XSLT_LOCATION_TYPE);
		if (xsltPath.equalsIgnoreCase(ReportServiceConstants.CLASSPATH)) {
		logger.debug("serching in classpath");
		xsltfile = ReportServiceImpl.class.getClassLoader()
		.getResourceAsStream(ReportServiceConstants.TC_DETAILS_XSLT_FILE_NAME);
		logger.debug("found file");
		} else {
		xsltLocation = ReadPropertiesFile.readRequestProperty(ReportServiceConstants.XSLT_LOCATION);
		xsltfile = new FileInputStream(new File(xsltLocation + ReportServiceConstants.TC_DETAILS_XSLT_FILE_NAME));
		}

		final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, byteOutput);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));
		transformer.setParameter(ReportServiceConstants.VERSION_PARAM, ReportServiceConstants.VERSION_NUMBER);
		Source src = new StreamSource(inputStream);
		Result res = new SAXResult(fop.getDefaultHandler());
		transformer.transform(src, res);
		Report report = new Report();
		InputStream tcReportPdf = new ByteArrayInputStream(byteOutput.toByteArray());
		report.setReportName(tcReport.getReportHeader().getPia() + "_" + tcReport.getReportHeader().getCenterId() + "_"
				+ ReportServiceConstants.TRAINING_CENTER_REPORT_PDF);
		report.setInputstream(tcReportPdf);
		return report;

	}

	/**
	 * This method is used to build the training center report JSON
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildTcReportJson(String tenantId, String projectId, String partnerId, String tcId)
			throws ProjectDaoException, MasterDataManagmentDaoException, UserDaoException, SurveyResponseDaoException,
			ParseException, JsonUtillException, ReportServiceException, PartnerDaoException, SchedulerDaoException {
		validateHeaders(projectId, ReportServiceConstants.PROJECT_ID);
		validateHeaders(partnerId, ReportServiceConstants.PARTNER_ID);
		validateHeaders(tcId, ReportServiceConstants.TC_ID);
		JSONObject jsonResponse = new JSONObject();
		ProjectData projectData = projectDao.getProjectById(tenantId, projectId);
		validateIds(projectData, ReportServiceConstants.PROJECT_ID);
		PartnerDetails partnerDetails = ipartnerDao.getPartnerById(tenantId, partnerId);
		validateIds(partnerDetails, ReportServiceConstants.PARTNER_ID);
		TrainingCenterDetails tcDetails = ipartnerDao.getTrainingCenterDetailsByTcId(tenantId, tcId);
		validateIds(tcDetails, ReportServiceConstants.TC_ID);
		ProjectMapping projectMapping = imasterDao.getProjectMappingByPartnerIdTcIdAndProjectId(tenantId, partnerId,
				tcId, projectId);
		if (projectMapping == null) {
			throw new ReportServiceException("projectMapping not found");
		}
		logger.debug("projectMapping is " + projectMapping);
		JSONObject reportHeaderJson = buildReporHeaderJsonForTcReport(tenantId, projectData, partnerDetails, tcDetails,
				projectMapping);
		List<RatingType> ratingTypeList = iSchedulerDao.getRatingTypeDataByProjectIdParterIdAndTcId(tenantId, partnerId,
				projectId, tcId);
		JSONArray formsArray = buildReportBodyForTCReport(tenantId, ratingTypeList, tcDetails.getCenterInchargeId(),
				reportHeaderJson, jsonResponse);

		// reportBody
		JSONObject formJson = new JSONObject();
		formJson.put(ReportServiceConstants.FORMS, formsArray);
		// finalJsonResponse
		jsonResponse.put(ReportServiceConstants.REPORT_BODY, formJson);
		jsonResponse.put(ReportServiceConstants.REPORT_HEADER, reportHeaderJson);
		logger.debug("responst is " + jsonResponse);
		return jsonResponse;
	}

	/**
	 * This method is used to build report header for training center report.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildReporHeaderJsonForTcReport(String databaseName, ProjectData projectData,
			PartnerDetails partnerDetails, TrainingCenterDetails tcDetails, ProjectMapping projectMapping)
			throws UserDaoException {
		logger.debug(".in buildReporHeaderJsonForTcReport");
		JSONObject reportHeaderJson = new JSONObject();
		reportHeaderJson.put(ReportServiceConstants.PROJECT_NAME, projectData.getProjectName());
		reportHeaderJson.put(ReportServiceConstants.PIA, partnerDetails.getPartnerName());
		reportHeaderJson.put(ReportServiceConstants.PARTNER_PROJECT_ID, projectMapping.getPartnerProjectId());
		User clientSponsor = userDao.getUserByUserId(databaseName, partnerDetails.getClientSponsorId());
		reportHeaderJson.put(ReportServiceConstants.PARTNER_SPOC_NAME, clientSponsor.getFirstName());
		reportHeaderJson.put(ReportServiceConstants.PARTNER_SPOC_PHONE, clientSponsor.getPhone());
		reportHeaderJson.put(ReportServiceConstants.PARTNER_SPOC_EMAIL_ID, clientSponsor.getEmail());
		reportHeaderJson.put(ReportServiceConstants.CENTER_ID, tcDetails.getTcId());
		User centerIncharge = userDao.getUserByUserId(databaseName, tcDetails.getCenterInchargeId());
		reportHeaderJson.put(ReportServiceConstants.CENTER_INCHARGE_NAME, centerIncharge.getFirstName());
		reportHeaderJson.put(ReportServiceConstants.CENTER_INCHARGE_EMAIL, centerIncharge.getEmail());
		reportHeaderJson.put(ReportServiceConstants.CENTER_INCHARGE_CONTACT, centerIncharge.getPhone());
		reportHeaderJson.put(ReportServiceConstants.TC_ADDRESS, tcDetails.getCenterAddress());
		return reportHeaderJson;
	}

	private String getStringFormateTimeFromMiliSecond(long time) {
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat(ProjectServiceConstant.PROJECT_TIME_FORMATE);
		return format.format(date);
	}

	/**
	 * This method is used to build the report body for training center report.
	 */
	@SuppressWarnings("unchecked")
	private JSONArray buildReportBodyForTCReport(String tenantId, List<RatingType> ratingTypeList,
			String centerInchargeId, JSONObject headerJson, JSONObject jsonResponse) throws SurveyResponseDaoException,
			ProjectDaoException, ParseException, JsonUtillException, ReportServiceException, UserDaoException {
		logger.debug(".in buildReportBody :: ");
		JSONArray jsoArrayResponse = new JSONArray();
		JSONArray auditsArray = new JSONArray();
//		boolean isAllFormsCompleted = true;
		int formsCount = ratingTypeList.size();
		logger.debug("there are " + formsCount + " forms");
		int formNotCompletedCount = 0;
		for (RatingType ratingType : ratingTypeList) {
			FormData formData = projectDao.getFormById(tenantId, ratingType.getFormId());
			if (!(formData.isPublish() && ratingType.isSelfAssignmentStatus() && ratingType.isAuditStatus())) {
//				isAllFormsCompleted = false;
				++formNotCompletedCount;
				continue;
			}
			logger.debug("current form is :" + formData);
			JSONObject singleFormJson = new JSONObject();
			String surveyId = formData.getSurveyId();
			Survey survey = projectDao.getSurveyById(tenantId, surveyId);
			String fieldAuditorId = ratingType.getFieldAuditorId();
			SurveyResponse fieldAuditorResponse = surveyResponseDao.getSurveyResponsesBySurveyIdAndAuditForId(tenantId,
					surveyId, ReportServiceConstants.TRAINING_CENTER, ratingType.getTcId());
			logger.debug("fieldAuditorResponse " + fieldAuditorResponse);
//			validateSurveyResponses(fieldAuditorResponse, ReportServiceConstants.FIELD_AUDITOR);
			SurveyResponse centerInchageResponse = surveyResponseDao.getSurveyResponsesByCenterIdAndSurveyId(tenantId,
					ratingType.getTcId(), surveyId);
//			validateSurveyResponses(centerInchageResponse, ReportServiceConstants.CENTER_IN_CHARGE);

			// working fine
			JSONArray sectionDetails = new JSONArray();
			String surveyData = survey.getSurveyData();
			JSONObject surveyDataJson = (JSONObject) JSONValue.parseWithException(surveyData);
			logger.debug("surveyDataJson:: " + surveyDataJson);
			String FASurveyResponseData = fieldAuditorResponse.getSurveyResponseData();
			JSONArray FASurveyResponseDataJson = (JSONArray) JSONValue.parseWithException(FASurveyResponseData);
			logger.debug("FASurveyResponseDataJson:: " + FASurveyResponseDataJson);
			String SASurveyResponseData = centerInchageResponse.getSurveyResponseData();
			JSONArray SASurveyResponseDataJson = (JSONArray) JSONValue.parseWithException(SASurveyResponseData);
			logger.debug("SASurveyResponseDataJson:: " + SASurveyResponseDataJson);
			JSONArray sectionArray = JsonUtill.getJsonArray(surveyDataJson, ReportServiceConstants.SECTIONS);
			logger.debug("sectionArray " + sectionArray);
			// surveySectionLevelLoop
			for (int i = 0; i < sectionArray.size(); i++) {
				JSONObject singleSection = (JSONObject) sectionArray.get(i);
				String sectionId = JsonUtill.getString(singleSection, ReportServiceConstants.SECTION_ID);
				String sectionName = JsonUtill.getString(singleSection, ReportServiceConstants.SECTION_NAME);
				JSONObject faSingleSection = getSuveyResponseSection(FASurveyResponseDataJson, sectionId);
				JSONObject saSingleSection = getSuveyResponseSection(SASurveyResponseDataJson, sectionId);
				int maxSectionScore = JsonUtill.getInt(singleSection, "sectionScore");
				JSONArray containsQuestions = JsonUtill.getJsonArray(singleSection, "sectionQuestions");
				JSONArray scoreCardArray = new JSONArray();

				// questions level loop
				for (int j = 0; j < containsQuestions.size(); j++) {
					JSONObject singleQuestion = (JSONObject) containsQuestions.get(j);
					logger.debug("current question in question loop " + singleQuestion);
					String questionNo = JsonUtill.getString(singleQuestion, ReportServiceConstants.QUES_ID);
					JSONObject questionDataJson = JsonUtill.getJsonObject(singleQuestion,
							ReportServiceConstants.QUES_DATA);
					String question = JsonUtill.getString(questionDataJson, ReportServiceConstants.QUESTION);
					logger.debug("QuesNo: " + questionNo + " Ques is: " + question);
					int maxScoreOfCurrentQues = getMaxScoreOfSingleQuestion(singleQuestion);
//					JSONArray optionDetailsArray = getOptionsOfSingleQuesion(singleQuestion,);
					int currentQuesFAScore = getSurveyResponseScores(faSingleSection, questionNo);
					int currentQuesSAScore = getSurveyResponseScores(saSingleSection, questionNo);
					JSONArray optionDetailsArray = getOptionsOfSingleQuesion(singleQuestion, faSingleSection);
					JSONObject singleQuestionJson = new JSONObject();
					singleQuestionJson.put(ReportServiceConstants.PARAMETER, question);
					singleQuestionJson.put(ReportServiceConstants.MAX_MARKS, maxScoreOfCurrentQues);
					singleQuestionJson.put(ReportServiceConstants.OPTION_DETAILS, optionDetailsArray);
					singleQuestionJson.put(ReportServiceConstants.SA_SCORE, currentQuesSAScore);
					singleQuestionJson.put(ReportServiceConstants.FA_SCORE, currentQuesFAScore);
					scoreCardArray.add(singleQuestionJson);

				}

				int faScore = JsonUtill.getInt(faSingleSection, ReportServiceConstants.SECTION_SCORE);

				int saScore = JsonUtill.getInt(saSingleSection, ReportServiceConstants.SECTION_SCORE);

				double SaScorePercentage = (double) ((saScore * 100) / maxSectionScore);
				double FAScorePercentage = (double) ((faScore * 100) / maxSectionScore);
				JSONObject sectionDetailsJson = new JSONObject();
				sectionDetailsJson.put(ReportServiceConstants.SECTION_ID, sectionId);
				sectionDetailsJson.put(ReportServiceConstants.SECTION_NAME, sectionName);
				sectionDetailsJson.put(ReportServiceConstants.MAX_SCORE, maxSectionScore);
				sectionDetailsJson.put(ReportServiceConstants.FA_SCORE, FAScorePercentage);
				sectionDetailsJson.put(ReportServiceConstants.SA_SCORE, SaScorePercentage);
				sectionDetailsJson.put(ReportServiceConstants.SCORE_CARD, scoreCardArray);

				sectionDetails.add(sectionDetailsJson);
			}
			int SAScore = centerInchageResponse.getTotalScore();
			int FAScore = fieldAuditorResponse.getTotalScore();
			int maxScore = survey.getMaxScore();
			double SaScorePercentage = (double) ((SAScore * 100) / maxScore);
			double FAScorePercentage = (double) ((FAScore * 100) / maxScore);

			User fieldAuditor = userDao.getUserByUserId(tenantId, fieldAuditorId);
			JSONObject auditsJsonObjects = new JSONObject();
			auditsJsonObjects.put(ReportServiceConstants.FIELD_AUDITOR_NAME, fieldAuditor.getFirstName());
			auditsJsonObjects.put(ReportServiceConstants.FORM_NAME, formData.getFormName());
			auditsJsonObjects.put(ReportServiceConstants.AUDIT_DATE,
					getStringFormateTimeFromMiliSecond(fieldAuditorResponse.getSubmitTime()));
			auditsJsonObjects.put(ReportServiceConstants.SECONDARY_AUDITOR_NAME,
					ratingType.getSecondaryFieldAuditorName());
			auditsArray.add(auditsJsonObjects);

			singleFormJson.put(ReportServiceConstants.FORM_NAME, formData.getFormName());
			singleFormJson.put(ReportServiceConstants.SA_SCORE, SaScorePercentage);
			singleFormJson.put(ReportServiceConstants.FA_SCORE, FAScorePercentage);
			singleFormJson.put(ReportServiceConstants.MAX_MARKS, maxScore);
			singleFormJson.put(ReportServiceConstants.SECTION_DETAILS, sectionDetails);

			// singleForm added
			jsoArrayResponse.add(singleFormJson);
		}
		if (formNotCompletedCount == formsCount) {
			throw new ReportServiceException("All " + formNotCompletedCount + " Forms Not Completed");
		}
		if (formNotCompletedCount != 0) {
			jsonResponse.put(ReportServiceConstants.WARNING,
					formNotCompletedCount + " forms not Completed, Showing only completed Forms");
		} else {
			jsonResponse.put(ReportServiceConstants.WARNING, "");
		}

		headerJson.put(ReportServiceConstants.AUDITS, auditsArray);
		return jsoArrayResponse;
	}

	/**
	 * This method is used to get the Suvey Response Section.
	 */
	private JSONObject getSuveyResponseSection(JSONArray surveyResponseDataJson, String sectionId)
			throws JsonUtillException, ReportServiceException {
		for (int i = 0; i < surveyResponseDataJson.size(); i++) {
			JSONObject singleSection = (JSONObject) surveyResponseDataJson.get(i);
			String currentSectionId = JsonUtill.getString(singleSection, "sectionId");
			if (currentSectionId.equals(sectionId)) {
				logger.debug("section matches in surveyResponse " + currentSectionId);
				return singleSection;
			}
		}
		throw new ReportServiceException(sectionId + " :: sectionId not found");
	}

	/**
	 * This method is used to get the scores of the Suvey Response Section.
	 */
	private int getSurveyResponseScores(JSONObject singleSurveyResponseSection, String questionId)
			throws JsonUtillException {
		logger.debug("singleSurveyResponseSection :: " + singleSurveyResponseSection + " questionId to serch is "
				+ questionId);
		JSONArray sectionResponseArray = JsonUtill.getJsonArray(singleSurveyResponseSection, "sectionResponseData");
		for (int i = 0; i < sectionResponseArray.size(); i++) {
			JSONObject singleQuesResponse = (JSONObject) sectionResponseArray.get(i);
			String currentQuestionId = JsonUtill.getString(singleQuesResponse, "questionId");
			if (currentQuestionId.equals(questionId)) {
				logger.debug("question matches " + currentQuestionId);
				JSONObject responseDataJson = JsonUtill.getJsonObject(singleQuesResponse, "responseData");
				logger.debug("responseDataJson " + responseDataJson);
				boolean containsKeyOptionScore = responseDataJson.containsKey("optionScore");
				if (!containsKeyOptionScore) {
					return JsonUtill.getInt(responseDataJson, "score");
				}
				return JsonUtill.getInt(responseDataJson, "optionScore");

			}
		}
		return 0;
	}

	/**
	 * This method is used to get the options of single Suvey Response question.
	 */
	private JSONArray getOptionsOfSingleQuesion(JSONObject surveySingleQues, JSONObject faSingleSection)
			throws JsonUtillException {
		JSONArray optionArray = null;
		String questionNo = JsonUtill.getString(surveySingleQues, ReportServiceConstants.QUES_ID);
		JSONObject queMetaDataJson = JsonUtill.getJsonObject(surveySingleQues, ProjectServiceConstant.QUES_META_DATA);
		String quesType = JsonUtill.getString(queMetaDataJson, ProjectServiceConstant.QUESTION_TYPE);
		logger.debug("quesType is:: " + quesType);
		switch (quesType) {
		case ProjectServiceConstant.MULTIPLE_CHOICE:
			optionArray = getOptionArrayForOptionBasedQues(surveySingleQues);
			break;
		case ProjectServiceConstant.OPEN_ENDED:
			optionArray = getOptionArrayForOpenEndedQues(surveySingleQues, faSingleSection, questionNo);
			break;
		case ProjectServiceConstant.DROP_DOWN:
			optionArray = getOptionArrayForOptionBasedQues(surveySingleQues);
			break;
		case ProjectServiceConstant.CHECK_BOX:
			optionArray = getOptionArrayForOptionBasedQues(surveySingleQues);
			break;
		case ProjectServiceConstant.DEFAULT:
		}
		return optionArray;
	}

	/**
	 * This method is used to get the options array of open ended question.
	 */
	@SuppressWarnings("unchecked")
	private JSONArray getOptionArrayForOpenEndedQues(JSONObject surveySingleQues, JSONObject faSingleSection,
			String questionNo) throws JsonUtillException {
		JSONArray jsonArray = new JSONArray();
		JSONObject singleQuestionResponse = getSingleQuestionResponse(faSingleSection, questionNo);
		JSONObject responseData = JsonUtill.getJsonObject(singleQuestionResponse, "responseData");
		String value = JsonUtill.getString(responseData, "inputFieldResponse");
		JSONObject questionDataJson = JsonUtill.getJsonObject(surveySingleQues, "questionData");
		int weightage = JsonUtill.getInt(questionDataJson, "weightage");
		JSONObject responseOptionJson = new JSONObject();
		responseOptionJson.put("optionValue", value);
		responseOptionJson.put("optionWeightage", weightage);
		jsonArray.add(responseOptionJson);
		return jsonArray;
	}

	/**
	 * This method is used to get the response of single question.
	 */
	private JSONObject getSingleQuestionResponse(JSONObject singleSurveyResponseSection, String questionNo)
			throws JsonUtillException {
		logger.debug("getSingleQuestionResponse, question No: " + questionNo);
		JSONArray sectionResponseArray = JsonUtill.getJsonArray(singleSurveyResponseSection, "sectionResponseData");
		for (int i = 0; i < sectionResponseArray.size(); i++) {
			JSONObject singleQuesResponse = (JSONObject) sectionResponseArray.get(i);
			String currentQuestionId = JsonUtill.getString(singleQuesResponse, "questionId");
			if (currentQuestionId.equals(questionNo)) {
				logger.debug("QuestionID  matches :: " + singleQuesResponse);
				return singleQuesResponse;
			}
		}
		logger.debug("Returning null for single question response..");
		return null;
	}

	/**
	 * This method is used to get the options array of option based question.
	 */
	@SuppressWarnings("unchecked")
	private JSONArray getOptionArrayForOptionBasedQues(JSONObject surveySingleQues) throws JsonUtillException {
		logger.debug("getOptionArrayForOptionBasedQues " + surveySingleQues);
		JSONObject questionDataJson = JsonUtill.getJsonObject(surveySingleQues, "questionData");
		JSONArray responseOptionArray = new JSONArray();
		JSONArray optionArray = JsonUtill.getJsonArray(questionDataJson, "options");
		for (int i = 0; i < optionArray.size(); i++) {
			JSONObject signleOption = (JSONObject) optionArray.get(i);
			String optionData = JsonUtill.getString(signleOption, "optionData");
			int optionWeightage = JsonUtill.getInt(signleOption, "optionweightage");

			JSONObject responseOptionJson = new JSONObject();
			responseOptionJson.put("optionValue", optionData);
			responseOptionJson.put("optionWeightage", optionWeightage);
			responseOptionArray.add(responseOptionJson);
		}
		return responseOptionArray;
	}

	/**
	 * This method is used to get the maximum score of single question.
	 */
	private int getMaxScoreOfSingleQuestion(JSONObject surveySingleQues) throws JsonUtillException {
		JSONObject queMetaDataJson = JsonUtill.getJsonObject(surveySingleQues, ProjectServiceConstant.QUES_META_DATA);
		String quesType = JsonUtill.getString(queMetaDataJson, ProjectServiceConstant.QUESTION_TYPE);
		logger.debug("quesType is:: " + quesType);
		switch (quesType) {
		case ProjectServiceConstant.MULTIPLE_CHOICE:
			int optionBasedQues = optionBasedQues(surveySingleQues);
			return optionBasedQues;
		case ProjectServiceConstant.OPEN_ENDED:
			int openEnded = openEnded(surveySingleQues);
			return openEnded;
		case ProjectServiceConstant.DROP_DOWN:
			int optionBasedQuesScore = optionBasedQues(surveySingleQues);
			return optionBasedQuesScore;
		case ProjectServiceConstant.CHECK_BOX:
			int checkBox = checkBox(surveySingleQues);
			return checkBox;
		case ProjectServiceConstant.DEFAULT:
			return 0;
		}
		return 0;
	}

	/**
	 * This method is used to get the weightage of checkBox question.
	 */
	private int checkBox(JSONObject surveySingleQuestions) throws JsonUtillException {
		JSONObject surveyQuestionData = JsonUtill.getJsonObject(surveySingleQuestions,
				ProjectServiceConstant.QUES_DATA);
		JSONArray surveyQuestionOptions = JsonUtill.getJsonArray(surveyQuestionData, ProjectServiceConstant.OPTIONS);
		int totalCout = 0;
		for (int i = 0; i < surveyQuestionOptions.size(); i++) {
			JSONObject singleOptionJson = (JSONObject) surveyQuestionOptions.get(i);
			int optionWeightage = JsonUtill.getInt(singleOptionJson, "optionweightage");
			totalCout += optionWeightage;
		}
		return totalCout;
	}

	/**
	 * This method is used to get the weightage of openEnded question.
	 */
	private int openEnded(JSONObject surveySingleQuestions) throws JsonUtillException {
		JSONObject surveyQuestionData = JsonUtill.getJsonObject(surveySingleQuestions,
				ProjectServiceConstant.QUES_DATA);
		int weightage = JsonUtill.getInt(surveyQuestionData, ProjectServiceConstant.WEIGHTAGE);
		return weightage;
	}

	/**
	 * This method is used to get the weightage of option Based question.
	 */
	private int optionBasedQues(JSONObject surveySingleQuestions) throws JsonUtillException {
		logger.debug(".in optionBasedQues" + surveySingleQuestions);
		JSONObject surveyQuestionData = JsonUtill.getJsonObject(surveySingleQuestions,
				ProjectServiceConstant.QUES_DATA);
		JSONArray surveyQuestionOptions = JsonUtill.getJsonArray(surveyQuestionData, ProjectServiceConstant.OPTIONS);
		List<Integer> maxMarkInList = new ArrayList<Integer>();
		for (int i = 0; i < surveyQuestionOptions.size(); i++) {
			JSONObject singleOptionJson = (JSONObject) surveyQuestionOptions.get(i);
			int optionWeightage = JsonUtill.getInt(singleOptionJson, "optionweightage");
			maxMarkInList.add(optionWeightage);
		}
		if (!maxMarkInList.isEmpty()) {
			Integer maxElement = Collections.max(maxMarkInList);
			return maxElement;
		}
		return 0;
	}

	/**
	 * This method is used to build the partner report JSON.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildPartnerReportJson(String tenantId, String projectId, String partnerId)
			throws MasterDataManagmentDaoException, ProjectDaoException, ProjectServiceException, UserDaoException,
			SurveyResponseDaoException, ReportServiceException, PartnerDaoException, SchedulerDaoException,
			JsonUtillException {
		validateHeaders(projectId, ReportServiceConstants.PROJECT_ID);
		validateHeaders(partnerId, ReportServiceConstants.PARTNER_ID);
		JSONObject jsonResponse = new JSONObject();
		ProjectData projectData = projectDao.getProjectById(tenantId, projectId);
		validateIds(projectData, ReportServiceConstants.PROJECT_ID);
		PartnerDetails partnerDetails = ipartnerDao.getPartnerById(tenantId, partnerId);
		validateIds(partnerDetails, ReportServiceConstants.PARTNER_ID);
		List<ProjectMapping> projectMapping = imasterDao.getProjectMappingByProjectIdAndPartnerId(tenantId, projectId,
				partnerId);
		if (projectMapping.size() == 0) {
			throw new ReportServiceException("no mapping found");
		}
		String partnerProjectId = projectMapping.get(0).getPartnerProjectId();
		JSONObject reportHeaderJson = new JSONObject();

		JSONObject reportBodyJson = new JSONObject();
		JSONObject summaryJson = buildSummaryReportJson(tenantId, projectId, partnerId);
		reportBodyJson.put("summaryReport", summaryJson);
		JSONArray centerRatingJson = buildCenterRatingSummaryJson(tenantId, partnerId, projectId, reportHeaderJson,
				jsonResponse);
		reportBodyJson.put("centerRatingSummary", centerRatingJson);
		JSONObject projectGradingSummary = buildProjectGradingSummaryJson(tenantId, projectId, partnerId,
				reportHeaderJson);
		reportBodyJson.put("partnerGradingSummary", projectGradingSummary);

		reportHeaderJson.put("projectName", projectData.getProjectName());
		reportHeaderJson.put("PIA", partnerDetails.getPartnerName());
		String clientSponsorId = partnerDetails.getClientSponsorId();
		User clientSponsorUser = userDao.getUserByUserId(tenantId, clientSponsorId);
		reportHeaderJson.put("partnerSPOCName", clientSponsorUser.getFirstName());
		reportHeaderJson.put("partnerSPOCPhone", clientSponsorUser.getPhone());
		reportHeaderJson.put("partnerProjectId", partnerProjectId);
		reportHeaderJson.put("partnerSPOCEmailId", clientSponsorUser.getEmail());
		jsonResponse.put("reportHeader", reportHeaderJson);
		jsonResponse.put("reportBody", reportBodyJson);

		return jsonResponse;
	}

	private void validateIds(Object object, String id) throws ReportServiceException {
		if (object == null) {
			throw new ReportServiceException("Invalid " + id);
		}
	}

	/**
	 * This method is used to build the Project Grading Summary JSON.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildProjectGradingSummaryJson(String tenantId, String projectId, String partnerId,
			JSONObject headerJson)
			throws MasterDataManagmentDaoException, ProjectDaoException, SurveyResponseDaoException,
			ReportServiceException, PartnerDaoException, SchedulerDaoException, JsonUtillException, UserDaoException {
		JSONObject projectGradingSummaryJson = new JSONObject();
		JSONArray auditsArray = JsonUtill.getJsonArray(headerJson, ReportServiceConstants.AUDITS);
		List<GradingType> listOfGrading = iSchedulerDao.getGradingTypeDataByProjectIdAndPartnerId(tenantId, partnerId,
				projectId);
		int SAScore = 0;
		int FAScore = 0;
		int maxMarks = 0;
		for (GradingType gradingType : listOfGrading) {
			String formId = gradingType.getFormId();
			FormData form = projectDao.getFormById(tenantId, formId);
			if (!(form.isPublish() && gradingType.isSelfAssignmentStatus() && gradingType.isAuditStatus())) {
				continue;
			}
			String surveyId = form.getSurveyId();
			String gradingPartnerId = gradingType.getPartnerId();
			SurveyResponse FASurveyResponse = surveyResponseDao.getSurveyResponsesBySurveyIdAndAuditForId(tenantId,
					surveyId, ReportServiceConstants.PARTNER, gradingPartnerId);
			FAScore += FASurveyResponse.getTotalScore();
			SurveyResponse saSurveyResopnse = surveyResponseDao.getSurveyResponsesByPartnerIdAndSurveyId(tenantId,
					surveyId, gradingPartnerId);
			SAScore += saSurveyResopnse.getTotalScore();
			maxMarks += FASurveyResponse.getMaxMarks();

			User fieldAuditor = userDao.getUserByUserId(tenantId, gradingType.getFieldAuditorId());
			JSONObject auditsJsonObjects = new JSONObject();
			auditsJsonObjects.put(ReportServiceConstants.FIELD_AUDITOR_NAME, fieldAuditor.getFirstName());
			auditsJsonObjects.put(ReportServiceConstants.FORM_NAME, form.getFormName());
			auditsJsonObjects.put(ReportServiceConstants.AUDIT_DATE,
					getStringFormateTimeFromMiliSecond(FASurveyResponse.getSubmitTime()));
			auditsJsonObjects.put(ReportServiceConstants.SECONDARY_AUDITOR_NAME,
					gradingType.getSecondaryFieldAuditorName());
			auditsJsonObjects.put(ReportServiceConstants.TC_NAME, "Grading");
			auditsArray.add(auditsJsonObjects);
		}

		projectGradingSummaryJson.put("SAScore", SAScore);
		projectGradingSummaryJson.put("FAScore", FAScore);
		projectGradingSummaryJson.put("maxMarks", maxMarks);
		return projectGradingSummaryJson;
	}

	/**
	 * This method is used to build the Summary Report JSON.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildSummaryReportJson(String tenantId, String projectId, String partnerId)
			throws MasterDataManagmentDaoException, ProjectDaoException, SurveyResponseDaoException,
			PartnerDaoException, SchedulerDaoException {
		logger.debug("In build summary Report Json");
		DecimalFormat formatter = new DecimalFormat("##.##");
		JSONObject summaryJson = new JSONObject();
//		List<ProjectMapping> projectMapping = imasterDao.getProjectMappingByProjectIdAndPartnerId(tenantId, projectId,
//				partnerId);
		List<RatingType> ratingtypeList = iSchedulerDao.getRatingTypeDataByProjectIdAndParterId(tenantId, partnerId,
				projectId);
		double centerRating = calculateCenterRating(tenantId, ratingtypeList, projectId);
		List<GradingType> gradingTypeList = iSchedulerDao.getGradingTypeDataByProjectIdAndPartnerId(tenantId, partnerId,
				projectId);
		double projectGrading = calculateProjectGrading(tenantId, gradingTypeList, projectId);
		double finalAvg = (double) ((centerRating + projectGrading) / 2);
		logger.debug("finalAvg :: " + finalAvg);
		String grade = null;
		if (finalAvg >= 80.0) {
			grade = "A+";
		} else if (finalAvg >= 70.0 && finalAvg <= 79.9) {
			grade = "A";
		} else if (finalAvg >= 50.0 && finalAvg <= 69.9) {
			grade = "B";
		} else {
			grade = "C";
		}
//		if (finalAvg == 0.0) {
//			jsonResponse.put("warning", "not all forms completed");
//		} else {
//			jsonResponse.put("warning", "");
//		}
		summaryJson.put("centerRating", formatter.format(centerRating));
		summaryJson.put("projectGrading", formatter.format(projectGrading));
		summaryJson.put("finalAvg", formatter.format(finalAvg));
		summaryJson.put("grade", grade);
		return summaryJson;
	}

	/**
	 * This method is used to calculate  the Center Rating
	 */
	private double calculateCenterRating(String tenantId, List<RatingType> projectMappingList, String projectId)
			throws ProjectDaoException, SurveyResponseDaoException, MasterDataManagmentDaoException,
			PartnerDaoException {
		logger.debug(". in calculateCenterRating ProjectMapping size is  ::" + projectMappingList.size());
		double totalScore = 0;
		double centerRatingScore = 0;
		for (RatingType ratingType : projectMappingList) {
			String formId = ratingType.getFormId();
			FormData form = projectDao.getFormById(tenantId, formId);
			if (!(form.isPublish() && ratingType.isSelfAssignmentStatus() && ratingType.isAuditStatus())) {
				continue;
			}
			String surveyId = form.getSurveyId();
			Survey survey = projectDao.getSurveyById(tenantId, surveyId);
			totalScore += survey.getMaxScore();
			SurveyResponse FASurveyResponse = surveyResponseDao.getSurveyResponsesBySurveyIdAndAuditForId(tenantId,
					surveyId, ReportServiceConstants.TRAINING_CENTER, ratingType.getTcId());
			centerRatingScore += FASurveyResponse.getTotalScore();
		}
		logger.debug("totalScore is " + totalScore + " and centerRatingScore is " + centerRatingScore);
		if (totalScore == 0) {
			return 0.0;
		}
		double centerRatingPercentage = (double) ((centerRatingScore / totalScore) * 100.0);
		logger.debug("centerRatingPercentage is ::" + centerRatingPercentage);
		return centerRatingPercentage;
	}

	/**
	 * This method is used to calculate the Project Grading
	 */
	private double calculateProjectGrading(String tenantId, List<GradingType> projectMappingList, String projectId)
			throws ProjectDaoException, SurveyResponseDaoException, MasterDataManagmentDaoException,
			PartnerDaoException {
		double totalScore = 0;
		double projectGrading = 0;
		for (GradingType gradingType : projectMappingList) {
			String formId = gradingType.getFormId();
			FormData form = projectDao.getFormById(tenantId, formId);
			if (!(form.isPublish() && gradingType.isSelfAssignmentStatus() && gradingType.isAuditStatus())) {
				continue;
			}
			String surveyId = form.getSurveyId();
			Survey survey = projectDao.getSurveyById(tenantId, surveyId);
			totalScore += survey.getMaxScore();
			SurveyResponse FASurveyResponse = surveyResponseDao.getSurveyResponsesBySurveyIdAndAuditForId(tenantId,
					surveyId, ReportServiceConstants.PARTNER, gradingType.getPartnerId());
			projectGrading += FASurveyResponse.getTotalScore();
		}
		if (totalScore == 0) {
			return 0.0;
		}
		double projectGradingPercentage = (double) ((projectGrading / totalScore) * 100.0);
		return projectGradingPercentage;

	}
	/**
	 * This method is used to build the Center Rating Summary JSON.
	 */
	@SuppressWarnings("unchecked")
	private JSONArray buildCenterRatingSummaryJson(String tenantId, String partnerId, String projectId,
			JSONObject headerJson, JSONObject responseJson)
			throws ProjectDaoException, ProjectServiceException, UserDaoException, SurveyResponseDaoException,
			MasterDataManagmentDaoException, ReportServiceException, SchedulerDaoException, PartnerDaoException {
		JSONArray centerArray = new JSONArray();
		JSONArray auditsArray = new JSONArray();
		List<TrainingCenterDetails> tcDetailsList = ipartnerDao.getAllTrainingCenterDetailsByPartnerId(tenantId,
				partnerId);
		boolean isAllTcCompleted = true;
		for (TrainingCenterDetails tcDetails : tcDetailsList) {
			JSONObject tcJson = new JSONObject();
			int maxScore = 0;
			int FAScore = 0;
			int SAScore = 0;
			String tcId = tcDetails.getTcId();
			String centerInchargeId = tcDetails.getCenterInchargeId();
			User user = userDao.getUserByUserId(tenantId, centerInchargeId);
			List<RatingType> ratingList = iSchedulerDao.getRatingTypeDataByProjectIdParterIdAndTcId(tenantId, partnerId,
					projectId, tcId);
			for (RatingType ratingType : ratingList) {
				String formId = ratingType.getFormId();
				FormData form = projectDao.getFormById(tenantId, formId);
				if (!(form.isPublish() && ratingType.isSelfAssignmentStatus() && ratingType.isAuditStatus())) {
					continue;
				}
				String surveyId = form.getSurveyId();
				SurveyResponse faSurveyResponse = surveyResponseDao.getSurveyResponsesBySurveyIdAndAuditForId(tenantId,
						surveyId, ReportServiceConstants.TRAINING_CENTER, tcId);
				SurveyResponse saSurveyResponse = surveyResponseDao.getSurveyResponsesByCenterIdAndSurveyId(tenantId,
						tcId, surveyId);
				FAScore += faSurveyResponse.getTotalScore();
				// calculate Self-Assig(CenterIncharge) Score for each form
				SAScore += saSurveyResponse.getTotalScore();

				maxScore += saSurveyResponse.getMaxMarks();

				User fieldAuditor = userDao.getUserByUserId(tenantId, ratingType.getFieldAuditorId());
				JSONObject auditsJsonObjects = new JSONObject();
				auditsJsonObjects.put(ReportServiceConstants.FIELD_AUDITOR_NAME, fieldAuditor.getFirstName());
				auditsJsonObjects.put(ReportServiceConstants.FORM_NAME, form.getFormName());
				auditsJsonObjects.put(ReportServiceConstants.AUDIT_DATE,
						getStringFormateTimeFromMiliSecond(faSurveyResponse.getSubmitTime()));
				auditsJsonObjects.put(ReportServiceConstants.SECONDARY_AUDITOR_NAME,
						ratingType.getSecondaryFieldAuditorName());
				auditsJsonObjects.put(ReportServiceConstants.TC_NAME, tcDetails.getTcName());
				auditsArray.add(auditsJsonObjects);
			}
			if (maxScore != 0.0 && SAScore != 0.0 && FAScore != 0.0) {
				tcJson.put("TCAddress", tcDetails.getCenterAddress());
				tcJson.put("TCSPOCName", user.getFirstName());
				tcJson.put("SAScore", SAScore);
				tcJson.put("FAScore", FAScore);
				tcJson.put("maxMarks", maxScore);
				tcJson.put("trainingCenter", tcId);
				centerArray.add(tcJson);
			} else {
				isAllTcCompleted = false;
			}
			logger.debug("single calculatifndskfdjfkl: " + tcJson.toJSONString());

		}
		if (!isAllTcCompleted) {
			responseJson.put("warning", "all training center not completed");
		} else {
			logger.info("all training center completed");
			responseJson.put("warning", "");
		}
		headerJson.put(ReportServiceConstants.AUDITS, auditsArray);

		return centerArray;
	}

	/**
	 * This method is used to build the Project Report JSON.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildProjectReportJson(String tenantId, String projectId)
			throws ProjectDaoException, MasterDataManagmentDaoException, SurveyResponseDaoException,
			ReportServiceException, PartnerDaoException, SchedulerDaoException {
		validateHeaders(projectId, ReportServiceConstants.PROJECT_ID);
		ProjectData projectData = projectDao.getProjectById(tenantId, projectId);
		validateIds(projectData, ReportServiceConstants.PROJECT_ID);
		JSONObject jsonResponse = new JSONObject();
		List<ProjectMapping> listOfMapping = imasterDao.getProjectMappingByProjectId(tenantId, projectId);
		int tcIcluded = listOfMapping.size();
		List<GradingEnable> gradingEnablelList = imasterDao.getGradingEnableByProjectId(tenantId, projectId);// imasterDao.getPartnerCountByProjectId(tenantId,
		long partnerCount = gradingEnablelList.size();
		JSONArray jssonArray = new JSONArray();
		DecimalFormat formatter = new DecimalFormat("##.##");
		boolean isAllPartnersCompleted = true;
		for (GradingEnable gradingEnable : gradingEnablelList) {
			String partnerId = gradingEnable.getPartnerId();
			JSONObject singlePartnerJson = new JSONObject();
			PartnerDetails partner = ipartnerDao.getPartnerById(tenantId, partnerId);
			List<RatingType> ratingTypeList = iSchedulerDao.getRatingTypeDataByProjectIdAndParterId(tenantId, partnerId,
					projectId);
			double centerRating = calculateCenterRating(tenantId, ratingTypeList, projectId);
			List<GradingType> gradingTypeList = iSchedulerDao.getGradingTypeDataByProjectIdAndPartnerId(tenantId,
					partnerId, projectId);
			double projectGrading = calculateProjectGrading(tenantId, gradingTypeList, projectId);
			double finalAvg = (double) ((centerRating + projectGrading) / 2);
			String grade = null;
			if (finalAvg >= 80.0) {
				grade = "A+";
			} else if (finalAvg >= 70.0 && finalAvg <= 79.9) {
				grade = "A";
			} else if (finalAvg >= 50.0 && finalAvg <= 69.9) {
				grade = "B";
			} else {
				grade = "C";
			}
			if (centerRating != 0.0 && projectGrading != 0.0 && finalAvg != 0.0) {
				singlePartnerJson.put("centerRating", formatter.format(centerRating));
				singlePartnerJson.put("projectGrading", formatter.format(projectGrading));
				singlePartnerJson.put("finalAvg", formatter.format(finalAvg));
				singlePartnerJson.put("grade", grade);
				singlePartnerJson.put("PIA", partner.getPartnerName());
				jssonArray.add(singlePartnerJson);
			} else {
				isAllPartnersCompleted = false;
			}

		}
		if (isAllPartnersCompleted) {
			jsonResponse.put("warning", "");
		} else {
			jsonResponse.put("warning", "all partners not completed the project");
		}
		// reportHeaders
		JSONObject reportHeaderJson = new JSONObject();
		reportHeaderJson.put(ReportServiceConstants.PROJECT_NAME, projectData.getProjectName());
		reportHeaderJson.put(ReportServiceConstants.DATE,
				getStringFormateTimeFromMiliSecond(projectData.getStartDate()));
		reportHeaderJson.put(ReportServiceConstants.PARTNERS_INCLUDED, partnerCount);
		reportHeaderJson.put(ReportServiceConstants.TRAINING_CENTER_INCLUDED, tcIcluded);
		JSONObject projectReprtSummary = new JSONObject();
		projectReprtSummary.put("partnersSummary", jssonArray);
		// final Response
		jsonResponse.put(ReportServiceConstants.REPORT_HEADER, reportHeaderJson);
		jsonResponse.put(ReportServiceConstants.REPORT_BODY, projectReprtSummary);
		return jsonResponse;
	}

	private void validateHeaders(String id, String string) throws ReportServiceException {
		if (id == null || id.isEmpty()) {
			throw new ReportServiceException("invalid request [" + string + "] header not found");
		}

	}

	/**
	 * This method is used to get the attachements for training center.
	 */
	private Media getAttachementsForTrainingCenter(String tenantId, String projectId, String partnerId, String tcId)
			throws Exception {
		logger.debug("inside getAttachementsForTrainingCenter method of ReportServiceImpl.");
		validateHeaders(projectId, ReportServiceConstants.PROJECT_ID);
		validateHeaders(partnerId, ReportServiceConstants.PARTNER_ID);
		validateHeaders(tcId, ReportServiceConstants.TC_ID);
		ProjectData projectData = projectDao.getProjectById(tenantId, projectId);
		validateIds(projectData, ReportServiceConstants.PROJECT_ID);
//		PartnerDetails partnerDetails = ipartnerDao.getPartnerById(tenantId, partnerId);
		TrainingCenterDetails tcDetails = ipartnerDao.getTrainingCenterDetailsByTcId(tenantId, tcId);
		validateIds(tcDetails, ReportServiceConstants.TC_ID);
		String centerInchargeId = tcDetails.getCenterInchargeId();
		ProjectMapping projectMapping = imasterDao.getProjectMappingByPartnerIdTcIdAndProjectId(tenantId, partnerId,
				tcId, projectId);
		if (projectMapping == null) {
			throw new ReportServiceException("Invalid request Mapping");
		}
		logger.debug("projectMapping is " + projectMapping);

		List<RatingType> ratingTypeList = iSchedulerDao.getRatingTypeDataByProjectIdParterIdAndTcId(tenantId, partnerId,
				projectId, tcId);
		String projectName = projectData.getProjectName();
		File projectDirectory = new File(projectName);
		if (projectDirectory.exists()) {
			logger.debug("Encountered Existing directory.. Deleting it..");
			FileUtils.deleteDirectory(projectDirectory);
			logger.debug("Existing directory deleted..Creating a new directory..");
			if (projectDirectory.mkdir()) {
				logger.debug(" *** Project Directory with name '" + projectName + "' created.");
			}
		} else {
			if (projectDirectory.mkdir()) {
				logger.debug(" ----> Project Directory with name '" + projectName + "' created.");
			}
		}
		for (RatingType ratingType : ratingTypeList) {
			FormData formData = projectDao.getFormById(tenantId, ratingType.getFormId());
			if (!(formData.isPublish() && ratingType.isSelfAssignmentStatus() && ratingType.isAuditStatus())) {
				logger.debug("this form not completed " + formData.getFormName());
				continue;
			}
			logger.debug("current form is :" + formData);
			String formName = formData.getFormName();
			File formDirectory = new File(projectDirectory.getAbsolutePath() + File.separator + formName);
			if (formDirectory.mkdir()) {
				logger.debug("Form Directory with name '" + formName + "' created.");
			}
			String surveyId = formData.getSurveyId();
			String fieldAuditorId = ratingType.getFieldAuditorId();
			SurveyResponse fieldAuditorResponse = surveyResponseDao.getSurveyResponsesBySurveyIdAndAuditForId(tenantId,
					surveyId, ReportServiceConstants.TRAINING_CENTER, ratingType.getTcId());
			logger.debug("fieldAuditorResponse " + fieldAuditorResponse);
			SurveyResponse centerInchageResponse = surveyResponseDao.getSurveyResponsesByCenterIdAndSurveyId(tenantId,
					ratingType.getTcId(), surveyId);
			logger.debug("centerInchageResponse " + centerInchageResponse);

			JSONArray FASurveyResponseDataJson;
			if (fieldAuditorResponse == null) {
				FASurveyResponseDataJson = new JSONArray();
			} else {
				String FASurveyResponseData = fieldAuditorResponse.getSurveyResponseData();
				FASurveyResponseDataJson = (JSONArray) JSONValue.parseWithException(FASurveyResponseData);
			}

			logger.debug("FASurveyResponseDataJson:: " + FASurveyResponseDataJson);

			JSONArray SASurveyResponseDataJson;
			if (centerInchageResponse == null) {
				SASurveyResponseDataJson = new JSONArray();
			} else {
				String SASurveyResponseData = centerInchageResponse.getSurveyResponseData();
				SASurveyResponseDataJson = (JSONArray) JSONValue.parseWithException(SASurveyResponseData);
			}

			logger.debug("SASurveyResponseDataJson:: " + SASurveyResponseDataJson);
//			JSONArray sectionArray = JsonUtill.getJsonArray(surveyDataJson, ReportServiceConstants.SECTIONS);
//			logger.debug("sectionArray " + sectionArray);

			// Processing Field-Auditor SurveyResponses.
			Map<String, String> FAmediaIdNameMap = new HashMap<String, String>();
			for (int i = 0; i < FASurveyResponseDataJson.size(); i++) {
				JSONObject singleSection = (JSONObject) FASurveyResponseDataJson.get(i);
				String sectionId = JsonUtill.getString(singleSection, AttachmentConstants.SECTION_ID);
				JSONArray sectionResponseArray = JsonUtill.getJsonArray(singleSection,
						AttachmentConstants.SECTION_RESPONSE_DATA);
				for (int j = 0; j < sectionResponseArray.size(); j++) {
					JSONObject singleQuestion = (JSONObject) sectionResponseArray.get(j);
					String questionId = JsonUtill.getString(singleQuestion, AttachmentConstants.QUESTION_ID);
					JSONObject responseData = JsonUtill.getJsonObject(singleQuestion,
							AttachmentConstants.RESPONSE_DATA);
					JSONObject mediaResponseJsonObject = JsonUtill.getJsonObject(responseData,
							AttachmentConstants.MEDIA_RESPONSE);
					JSONArray imageJsonArray = (JSONArray) mediaResponseJsonObject.get(AttachmentConstants.IMAGE);
					for (int k = 0; k < imageJsonArray.size(); k++) {
						JSONObject singleMedia = (JSONObject) imageJsonArray.get(k);
						String mediaId = JsonUtill.getString(singleMedia, AttachmentConstants.MEDIA_ID);
						String mediaName = sectionId + AttachmentConstants.PARAMETER + questionId + AttachmentConstants._IMAGE + (k + 1);
						FAmediaIdNameMap.put(mediaId, mediaName);
					}
					JSONArray docJsonArray = (JSONArray) mediaResponseJsonObject.get(AttachmentConstants.DOCS);
					for (int k = 0; k < docJsonArray.size(); k++) {
						JSONObject singleMedia = (JSONObject) docJsonArray.get(k);
						String mediaId = JsonUtill.getString(singleMedia, AttachmentConstants.MEDIA_ID);
						String mediaName = sectionId + AttachmentConstants.PARAMETER + questionId + AttachmentConstants._DOC + (k + 1);
						FAmediaIdNameMap.put(mediaId, mediaName);
					}
				}
			}
			logger.debug("FAmediaIdNameMap: " + FAmediaIdNameMap);

			// Creating Field-Auditor Media Files
			logger.info("Field-Auditor Files creation is STARTED..");
			User fieldAuditor = userDao.getUserByUserId(tenantId, fieldAuditorId);
			String fieldAuditorName = fieldAuditor.getFirstName() + " " + fieldAuditor.getLastName();
			File faDirectory = new File(formDirectory.getAbsolutePath() + File.separator + fieldAuditorName
					+ AttachmentConstants.FIELD_AUDITOR);
			if (faDirectory.mkdir()) {
				logger.debug("Field-Auditor attachment directory is created. ");
			}
			Set<String> mediaIdkeySet = FAmediaIdNameMap.keySet();
			List<Media> mediaList = mediaDAO.getMediaList(mediaIdkeySet, tenantId);
			createFilesFromInputStream(mediaList, faDirectory, FAmediaIdNameMap);
			logger.info("Field-Auditor Files creation is COMPLETED..");

			Map<String, String> userMediaIdNameMap = new HashMap<String, String>();
			for (int i = 0; i < SASurveyResponseDataJson.size(); i++) {
				JSONObject singleSection = (JSONObject) SASurveyResponseDataJson.get(i);
				String sectionId = JsonUtill.getString(singleSection, AttachmentConstants.SECTION_ID);
				JSONArray sectionResponseArray = JsonUtill.getJsonArray(singleSection,
						AttachmentConstants.SECTION_RESPONSE_DATA);
				for (int j = 0; j < sectionResponseArray.size(); j++) {
					JSONObject singleQuestion = (JSONObject) sectionResponseArray.get(j);
					String questionId = JsonUtill.getString(singleQuestion, AttachmentConstants.QUESTION_ID);
					JSONObject responseData = JsonUtill.getJsonObject(singleQuestion,
							AttachmentConstants.RESPONSE_DATA);
					JSONObject mediaResponseJsonObject = JsonUtill.getJsonObject(responseData,
							AttachmentConstants.MEDIA_RESPONSE);
					JSONArray imageJsonArray = (JSONArray) mediaResponseJsonObject.get(AttachmentConstants.IMAGE);
					for (int k = 0; k < imageJsonArray.size(); k++) {
						JSONObject singleMedia = (JSONObject) imageJsonArray.get(k);
						String mediaId = JsonUtill.getString(singleMedia, AttachmentConstants.MEDIA_ID);
						String mediaName = sectionId + AttachmentConstants.PARAMETER + questionId + AttachmentConstants._IMAGE + (k + 1);
						userMediaIdNameMap.put(mediaId, mediaName);
					}
					JSONArray docJsonArray = (JSONArray) mediaResponseJsonObject.get(AttachmentConstants.DOCS);
					for (int k = 0; k < docJsonArray.size(); k++) {
						JSONObject singleMedia = (JSONObject) docJsonArray.get(k);
						String mediaId = JsonUtill.getString(singleMedia, AttachmentConstants.MEDIA_ID);
						String mediaName = sectionId + AttachmentConstants.PARAMETER + questionId + AttachmentConstants._DOC + (k + 1);
						userMediaIdNameMap.put(mediaId, mediaName);
					}

				}
			}
			logger.debug("userMediaIdNameMap: " + userMediaIdNameMap);

			// Creating CIC Media Files
			logger.info("Center_In-Charge Files creation is STARTED..");
			User centerInCharge = userDao.getUserByUserId(tenantId, centerInchargeId);
			String cicName = centerInCharge.getFirstName() + " " + centerInCharge.getLastName();
			File cicDirectory = new File(
					formDirectory.getAbsolutePath() + File.separator + cicName + AttachmentConstants.CENTER_IN_CHARGE);
			if (cicDirectory.mkdir()) {
				logger.debug("Center_Incharge attachment directory is created. ");
			}
			Set<String> cicMediaIdkeySet = userMediaIdNameMap.keySet();
			if (cicMediaIdkeySet.size() > 0) {
				List<Media> cicMediaList = mediaDAO.getMediaList(cicMediaIdkeySet, tenantId);
				createFilesFromInputStream(cicMediaList, cicDirectory, userMediaIdNameMap);
			}
			logger.info("Center_In-Charge Files creation is COMPLETED..");
		}

		// projectDirectory - Creating zip
		Media entireZip = createZipOfEntireDirectory(projectDirectory);
		logger.debug("Got zip Media : " + entireZip);
		FileUtils.deleteDirectory(projectDirectory);
		logger.debug(" $$$$ Project Folder deleted successfully.");
		return entireZip;
	}

	/**
	 * This method is used to create the files from the input stream.
	 */
	private void createFilesFromInputStream(List<Media> mediaList, File directory, Map<String, String> mediaIdMap)
			throws Exception {
		logger.debug(".inside createFilesFromInputStream method of ReportServiceImpl.");
		logger.debug("mediaList is " + mediaList.size());
		for (Media media : mediaList) {
			String mediaName = media.getMediaName();
			String mediaId = media.getMediaId().toLowerCase();
			String extension = FilenameUtils.getExtension(mediaName);
			String newFileName = mediaIdMap.get(mediaId);
			String newFileNameWithExt = newFileName + "." + extension;
			logger.debug("MediaId: " + mediaId + ", OldFileName: " + mediaName + "------> New File Name: "
					+ newFileNameWithExt);
			File withFile = new File(directory.getAbsolutePath() + File.separator + newFileNameWithExt);
			logger.debug("Creating file: " + withFile);
			FileOutputStream fos = new FileOutputStream(withFile);
			IOUtils.copy(media.getMediaInputStream(), fos);
			fos.close();
		}
	}

	/*
	 * private Media createZipOfEntireDirectory1(File directory) throws Exception {
	 * if(directory.isDirectory()) {
	 * logger.debug("Given file obj is pointing to directory"); // File[] listFiles
	 * = directory.listFiles(); //
	 * logger.debug("Total Files present in directory: "+listFiles.length);
	 * FileOutputStream fos = new
	 * FileOutputStream("Attachments_"+directory.getName()+".zip"); ZipOutputStream
	 * zipOut = new ZipOutputStream(fos); logger.debug("File Obj : "+directory);
	 * logger.debug("File getName() : "+directory.getName()); zipFile(directory,
	 * directory.getName(), zipOut); zipOut.close(); fos.close(); } File file = new
	 * File("Attachments_"+directory.getName()+".zip");
	 * logger.debug(file+", exists: "+file.exists()); FileInputStream fis = new
	 * FileInputStream(file); logger.debug("FileInputStream : "+fis); Media media =
	 * new Media(); media.setMediaName("Attachments_"+directory.getName()+".zip");
	 * media.setMediaInputStream(fis); // IOUtils. // fis.close(); // file.delete();
	 * // FileUtils.forceDelete(file); //
	 * logger.debug("zip file is deleted : "+zipDelete); return media; }
	 */

	/**
	 * This method is used to zip all the files in the directory
	 * @param directory the directory to be zipped.
	 */
	private Media createZipOfEntireDirectory(File directory) throws Exception {
		String name = null;
		File zipFile = null;
		if (directory.isDirectory()) {
			logger.debug("Given file obj is pointing to directory");
			name = "Attachments_" + directory.getName() + ".zip";
//			File[] listFiles = directory.listFiles();
//			logger.debug("Total Files present in directory: "+listFiles.length);
			zipFile = new File(name);
			boolean createNewFile = zipFile.createNewFile();
			logger.debug(zipFile + ", Created : " + createNewFile);
			FileOutputStream fos = new FileOutputStream(zipFile);
			logger.debug("FOS created: " + fos);
			ZipOutputStream zipOut = new ZipOutputStream(fos);
			logger.debug("File Obj : " + directory);
			logger.debug("File getName() : " + directory.getName());
			zipFile(directory, directory.getName(), zipOut);
			zipOut.close();
			fos.close();
		}
//		File file = new File(zipFile);
//		logger.debug(file+", exists: "+file.exists());
		FileInputStream fis = null;
		Media media = null;
		fis = new FileInputStream(zipFile);
		logger.debug("FileInputStream : " + fis);
		byte[] byteArrayOfZip = IOUtils.toByteArray(fis);
		fis.close(); // Closing the fileinputStream to delete the ZIP File.
		media = new Media();
		media.setMediaName(name);
		media.setMediaInputStream(new ByteArrayInputStream(byteArrayOfZip));
		logger.debug("After setting InputStream to media, Attempting to delete zip file.");
		boolean delete = zipFile.delete();
		logger.debug("$$$$ Zip file is deleted : " + delete);
		return media;
	}

	/**
	 * This method is used to add a single file into the zip.
	 */
	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}

}
