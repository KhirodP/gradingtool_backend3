package com.pwc.grading.report.service;

import com.pwc.grading.mediabucket.model.Media;
import com.pwc.grading.report.model.Report;
import com.pwc.grading.report.service.exception.ReportServiceException;

/**
 * An interface class which is used to perform all
 * Report related operations.
 *
 */
public interface IReportService {
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
	public String getReport(String tenantId, String projectId, String partnerId, String tcId, String reportType)
			throws ReportServiceException;

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
	Report getReportDownload(String tenantId, String projectId, String partnerId, String tcId, String reportType)
			throws ReportServiceException;

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
	public Media getAttachementsDownload(String tenantId, String projectId, String partnerId, String tcId,
			String reportType) throws ReportServiceException;

}
