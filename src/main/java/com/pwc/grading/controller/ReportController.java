package com.pwc.grading.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.controller.exception.ProjectControllerException;
import com.pwc.grading.controller.exception.ReportControllerException;
import com.pwc.grading.mediabucket.model.Media;
import com.pwc.grading.report.model.Report;
import com.pwc.grading.report.service.IReportService;
import com.pwc.grading.user.service.registration.UserAccessManagementServiceConstants;
import com.pwc.grading.usertoken.model.TokenPayload;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.TokenValidator;
import com.pwc.grading.util.exception.TokenValidatorException;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.server.types.files.StreamedFile;

/**
 * ReportController This Class is Responsable for the Generating or Dowloading
 * the Reports
 * 
 * <pre>
 * Types Of Reports:
 * 1)Project Report
 * 2)Partner Report
 * 3)TrainingCenter Report
 * </pre>
 * 
 * see also:
 * {@link}{@link ProjectController}{@link}{@link MasterDataManagmentController}
 * 
 *
 */
@Controller("gradingTool/report")
public class ReportController {
	@Inject
	IReportService reportService;
	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

	/**
	 * This method is used to preview the report.
	 * @param reportType the type of the report.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param projectId the id of the project.
	 * @param partnerId  the id of the partner.
	 * @param tcId the id of the training center.
	 * @return the JSON for previewing the report. 
	 * @throws ReportControllerException if any exception occurs while performing this operation.
	 */
	@Get(produces = MediaType.APPLICATION_JSON)
	public HttpResponse<String> getReport(@QueryValue(value = "reportType") String reportType,
			@Header("jwtToken") String jwtToken, @Header("projectId") String projectId,
			@Header(name = "partnerId", defaultValue = "") String partnerId,
			@Header(name = "tcId", defaultValue = "") String tcId) throws ReportControllerException {
		try {
			logger.debug(" inside filterSchedulers method SchedulerController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ReportControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ReportControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			String response = reportService.getReport(tenantId, projectId, partnerId, tcId, reportType);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response);
		} catch (ReportControllerException e) {
			throw e;
		} catch (Exception e) {
			throw new ReportControllerException("unable generate report", e, 400, e.getMessage());
		}

	}

	/**
	 * This method is used to Download the report.
	 * @param reportType the type of the report.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param projectId the id of the project.
	 * @param partnerId  the id of the partner.
	 * @param tcId the id of the training center.
	 * @return the JSON for previewing the report. 
	 * @throws ReportControllerException if any exception occurs while performing this operation.
	 */
	@Get(uri = "/download", produces = MediaType.APPLICATION_OCTET_STREAM)
	public HttpResponse<StreamedFile> getReportDownoad(@QueryValue(value = "reportType") String reportType,
			@Header("jwtToken") String jwtToken, @Header("projectId") String projectId,
			@Header(name = "partnerId", defaultValue = "") String partnerId,
			@Header(name = "tcId", defaultValue = "") String tcId) throws ReportControllerException {
		try {
			logger.debug(" inside filterSchedulers method SchedulerController");
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ReportControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ReportControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			Report report = reportService.getReportDownload(tenantId, projectId, partnerId, tcId, reportType);
			StreamedFile streamedFile = new StreamedFile(report.getInputstream(),
					MediaType.APPLICATION_OCTET_STREAM_TYPE);
			return HttpResponse.ok().body(streamedFile).header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*").header(
					HttpHeaders.CONTENT_DISPOSITION,
					ControllerConstants.CONTENT_DISPOSITION_VALUE + report.getReportName());
		} catch (ReportControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unable generate report ::" + e.getMessage());
			throw new ReportControllerException("unable generate report", e, 400, e.getMessage());
		}

	}

	/**
	 * This method is used to download attachments for the training center.
	 * @param reportType the type of the report.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @param projectId the id of the project.
	 * @param partnerId  the id of the partner.
	 * @param tcId the id of the training center.
	 * @return the attachements gets downloaded in .zip format.
	 * @throws ReportControllerException if any exception occurs while performing this operation.
	 */
	@Get(uri = "/attachments/download")
	public HttpResponse<StreamedFile> getAttachmentsDownload(@QueryValue(value = "reportType") String reportType,
			@Header("jwtToken") String jwtToken, @Header("projectId") String projectId,
			@Header(name = "partnerId", defaultValue = "") String partnerId,
			@Header(name = "tcId", defaultValue = "") String tcId) throws ReportControllerException {
		logger.debug(".inside getAttachmentsDownload method SchedulerController");
		try {
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new ProjectControllerException(ReadPropertiesFile.readResponseProperty("101"),
							new Throwable(), 401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new ReportControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
						401, e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String userRole = tokenPayload.getUserRole();
			if (!userRole.equals(UserAccessManagementServiceConstants.ADMIN)) {
				logger.error("User is not allowed for the opertion ");
				throw new ReportControllerException(ReadPropertiesFile.readResponseProperty("400"), new Throwable(),
						401, ReadPropertiesFile.readResponseProperty("400"));
			}
			Media media = reportService.getAttachementsDownload(tenantId, projectId, partnerId, tcId, reportType);
			StreamedFile streamedFile = new StreamedFile(media.getMediaInputStream(),
					MediaType.APPLICATION_OCTET_STREAM_TYPE);
			return HttpResponse.ok().body(streamedFile)
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*").header(HttpHeaders.CONTENT_DISPOSITION,
							ControllerConstants.CONTENT_DISPOSITION_VALUE + media.getMediaName());

		} catch (ReportControllerException e) {
			throw e;
		} catch (Exception e) {
			HttpResponse.status(HttpStatus.BAD_REQUEST).header("errorMsg", e.getMessage());
			logger.error("unable to get Attachements ::" + e.getMessage());
			throw new ReportControllerException("Unable to get Attachements", e, 400, e.getMessage());
		}

	}
}
