package com.pwc.grading.reportingdb.job;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.JobConstant;
import com.pwc.grading.reportingdb.model.Job;
import com.pwc.grading.reportingdb.processor.ReportDBDeleteGradingProcessor;
import com.pwc.grading.reportingdb.processor.ReportDBDeleteRatingProcessor;
import com.pwc.grading.reportingdb.processor.ReportDBUpdateGradingCSSubmitProcessor;
import com.pwc.grading.reportingdb.processor.ReportDBUpdateGradingFADetailsProcessor;
import com.pwc.grading.reportingdb.processor.ReportDBUpdateGradingFASubmitProcessor;
import com.pwc.grading.reportingdb.processor.ReportDBUpdateRatingCICSubmitProcessor;
import com.pwc.grading.reportingdb.processor.ReportDBUpdateRatingFADetailsProcessor;
import com.pwc.grading.reportingdb.processor.ReportDBUpdateRatingFASubmitProcessor;
import com.pwc.grading.reportingdb.processor.ReportingDBAddGradingProcessor;
import com.pwc.grading.reportingdb.processor.ReportingDBAddRatingProcessor;
import com.pwc.grading.reportingdb.processor.ReportingDBDeleteProcessor;
import com.pwc.grading.reportingdb.processor.ReportingDBUpdateCICProcessor;
import com.pwc.grading.reportingdb.processor.ReportingDBUpdateCSProcessor;
import com.pwc.grading.reportingdb.processor.ReportingDBUpdateTCProcessor;
import com.pwc.grading.reportingdb.service.IJobService;
import com.pwc.grading.reportingdb.service.impl.JobServiceImpl;

/**
 * An implementation of the {@link Runnable} to run the 
 * reporting tables related taks asynchronously.
 *
 */
public class ReportDatabaseCallJob implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportDatabaseCallJob.class);

//	private static long totalMs = 0;

	private Job job;

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	private String databaseName;

	private IJobService jobService;

	public ReportDatabaseCallJob(String databaseName) {
		jobService = new JobServiceImpl();
		this.databaseName = databaseName;
	}

	@Override
	public void run() {

		if (job != null) {
//			LOGGER.debug("Thread " + Thread.currentThread().getName() + "is picking job: " + job);
//			long startTime = System.currentTimeMillis();
			String jobStatus = job.getStatus();
			if (jobStatus.equalsIgnoreCase(JobConstant.RETRY_IN_PROGRESS)) {
				retryJobExecution(job);
			} else if (jobStatus.equalsIgnoreCase(JobConstant.STATUS_NEW)
					|| jobStatus.equalsIgnoreCase(JobConstant.STATUS_IN_PROGRESS)) {
				freshJobExecution(job);
			}
//			long endTime = System.currentTimeMillis();
//			long diff = endTime - startTime;
//			LOGGER.debug("Time taken for Job '" + job.getJobId() + "' is: " + diff);
//			totalMs = totalMs + diff;
//			LOGGER.debug("Total MS:" + totalMs);
		}
	}

	/**
	 * This method is called if it is a fresh Job (the job's status is NEW (or) IN PROGRESS)
	 * Its working is, it gets the operationType from the Job Object and based on the operationType
	 * it creates an instance of the processor and passes the JSON from the Job Object to the respective processor instance.
	 * @param job the object containing the operationType and the required JSON for the operation.
	 */
	private void freshJobExecution(Job job) {
		LOGGER.debug("freshJobExecution, job is : " + StringEscapeUtils.escapeJava(job.toString()));
		String jobId = null;
		String errorMsg = null;
		boolean jobSuccess = false;
		try {
			jobId = job.getJobId();
			job.setStartTime(LocalDateTime.now().toString());
			job.setStatus(JobConstant.STATUS_IN_PROGRESS);
			LOGGER.debug("Updating status to InProgress to Job.");
			jobService.update(databaseName, jobId, job);
			try {
				String operationType = job.getOperationType();
				if (operationType.equalsIgnoreCase(JobConstant.ADD_GRADING)) {
					ReportingDBAddGradingProcessor addProcessor = new ReportingDBAddGradingProcessor();
					addProcessor.insertGradingDataIntoReportDB(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.ADD_RATING)) {
					ReportingDBAddRatingProcessor addProcessor = new ReportingDBAddRatingProcessor();
					addProcessor.insertRatingDataIntoReportDB(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_GRADING_FA_SUBMIT)) {
					ReportDBUpdateGradingFASubmitProcessor updateGradingFAProcessor = new ReportDBUpdateGradingFASubmitProcessor();
					updateGradingFAProcessor.updateGradingFASubmitIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_GRADING_CS_SUBMIT)) {
					ReportDBUpdateGradingCSSubmitProcessor updateGradingCSProcessor = new ReportDBUpdateGradingCSSubmitProcessor();
					updateGradingCSProcessor.updateGradingCSSubmitIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_RATING_FA_SUBMIT)) {
					ReportDBUpdateRatingFASubmitProcessor updateRatingFAProcessor = new ReportDBUpdateRatingFASubmitProcessor();
					updateRatingFAProcessor.updateRatingFASubmitIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_RATING_CIC_SUBMIT)) {
					ReportDBUpdateRatingCICSubmitProcessor updateRatingCICProcessor = new ReportDBUpdateRatingCICSubmitProcessor();
					updateRatingCICProcessor.updateRatingCICSubmitIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.DELETE)) {
					ReportingDBDeleteProcessor deleteProcessor = new ReportingDBDeleteProcessor();
					deleteProcessor.deleteDataFromReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_TC)) {
					ReportingDBUpdateTCProcessor updateTCProcessor = new ReportingDBUpdateTCProcessor();
					updateTCProcessor.updateTrainingCenterDataIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_CIC)) {
					ReportingDBUpdateCICProcessor updateCICProcessor = new ReportingDBUpdateCICProcessor();
					updateCICProcessor.updateCenterInchargeDataIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_CS)) {
					ReportingDBUpdateCSProcessor updateCSProcessor = new ReportingDBUpdateCSProcessor();
					updateCSProcessor.updateClientSponsorDataIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_GRADING_FA_DETAILS)) {
					ReportDBUpdateGradingFADetailsProcessor updategrFAProcessor = new ReportDBUpdateGradingFADetailsProcessor();
					updategrFAProcessor.updateGradingFieldAuditorDetailsIntoReportingDatabase(databaseName,
							job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_RATING_FA_DETAILS)) {
					ReportDBUpdateRatingFADetailsProcessor updategrFAProcessor = new ReportDBUpdateRatingFADetailsProcessor();
					updategrFAProcessor.updateRatingFieldAuditorDataIntoReportingDatabase(databaseName,
							job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.DELETE_GRADING)) {
					ReportDBDeleteGradingProcessor delGraProceesor = new ReportDBDeleteGradingProcessor();
					delGraProceesor.deleteGradingDataFromReportDB(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.DELETE_RATING)) {
					ReportDBDeleteRatingProcessor delProceesor = new ReportDBDeleteRatingProcessor();
					delProceesor.deleteRatingDataFromReportDB(databaseName, job.getJsonObj());
					jobSuccess = true;
				}
			} catch (Exception e) {
				LOGGER.error("Job execution failed because of this reason : " + e.getMessage(), e);
				jobSuccess = false;
				errorMsg = e.getMessage();
			}
			if (jobSuccess) {
				LOGGER.debug("Job '" + StringEscapeUtils.escapeJava(jobId) + "' executed successfully, Updating status to SUCCESS");
				job.setStatus(JobConstant.STATUS_SUCCESS);
				job.setEndTime(LocalDateTime.now().toString());
				jobService.update(databaseName, jobId, job);
 		//	    jobService.delete(databaseName, jobId); //After job is success, deleting the job entry.
			} else {
				job.setStatus(JobConstant.STATUS_FAILED);
				job.setFailureMessage(errorMsg);
				job.setEndTime(LocalDateTime.now().toString());
				jobService.update(databaseName, jobId, job);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to make changes to JOB table: " + e.getMessage(), e);
		}

	}

	/**
	 * This method is called if it is a retrying Job (the job's status is FAILED (or) RETRY FAILED)
	 * Its working is, it gets the operationType from the Job Object and based on the operationType
	 * it creates an instance of the processor and passes the JSON from the Job Object to the respective processor instance.
	 * @param job the object containing the operationType and the required JSON for the operation.
	 */
	private void retryJobExecution(Job job) {
		LOGGER.debug("retryJobExecution, job is : " + StringEscapeUtils.escapeJava(job.toString()));
		String jobId = null;
		String errorMsg = null;
		boolean jobSuccess = false;
		try {
			jobId = job.getJobId();
			LOGGER.debug("Retrying Job with Id '" + StringEscapeUtils.escapeJava(jobId) + "' of '" + databaseName + "' ...");
			// job.setStartTime(LocalDateTime.now().toString());
			job.setStatus(JobConstant.RETRY_IN_PROGRESS);
			jobService.update(databaseName, jobId, job);
			try {
				String operationType = job.getOperationType();
				LOGGER.debug("Operation type of '" + StringEscapeUtils.escapeJava(jobId) + "' : " + StringEscapeUtils.escapeJava(operationType));
				if (operationType.equalsIgnoreCase(JobConstant.ADD_GRADING)) {
					ReportingDBAddGradingProcessor addGradingProcessor = new ReportingDBAddGradingProcessor();
					addGradingProcessor.insertGradingDataIntoReportDB(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.ADD_RATING)) {
					ReportingDBAddRatingProcessor addRatingProcessor = new ReportingDBAddRatingProcessor();
					addRatingProcessor.insertRatingDataIntoReportDB(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_GRADING_FA_SUBMIT)) {
					ReportDBUpdateGradingFASubmitProcessor updateGradingFAProcessor = new ReportDBUpdateGradingFASubmitProcessor();
					updateGradingFAProcessor.updateGradingFASubmitIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_GRADING_CS_SUBMIT)) {
					ReportDBUpdateGradingCSSubmitProcessor updateGradingCSProcessor = new ReportDBUpdateGradingCSSubmitProcessor();
					updateGradingCSProcessor.updateGradingCSSubmitIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_RATING_FA_SUBMIT)) {
					ReportDBUpdateRatingFASubmitProcessor updateRatingFAProcessor = new ReportDBUpdateRatingFASubmitProcessor();
					updateRatingFAProcessor.updateRatingFASubmitIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_RATING_CIC_SUBMIT)) {
					ReportDBUpdateRatingCICSubmitProcessor updateRatingCICProcessor = new ReportDBUpdateRatingCICSubmitProcessor();
					updateRatingCICProcessor.updateRatingCICSubmitIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.DELETE)) {
					ReportingDBDeleteProcessor deleteProcessor = new ReportingDBDeleteProcessor();
					deleteProcessor.deleteDataFromReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_TC)) {
					ReportingDBUpdateTCProcessor updateTCProcessor = new ReportingDBUpdateTCProcessor();
					updateTCProcessor.updateTrainingCenterDataIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_CIC)) {
					ReportingDBUpdateCICProcessor updateCICProcessor = new ReportingDBUpdateCICProcessor();
					updateCICProcessor.updateCenterInchargeDataIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_CS)) {
					ReportingDBUpdateCSProcessor updateCSProcessor = new ReportingDBUpdateCSProcessor();
					updateCSProcessor.updateClientSponsorDataIntoReportingDatabase(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_GRADING_FA_DETAILS)) {
					ReportDBUpdateGradingFADetailsProcessor updategrFAProcessor = new ReportDBUpdateGradingFADetailsProcessor();
					updategrFAProcessor.updateGradingFieldAuditorDetailsIntoReportingDatabase(databaseName,
							job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.UPDATE_RATING_FA_DETAILS)) {
					ReportDBUpdateRatingFADetailsProcessor updategrFAProcessor = new ReportDBUpdateRatingFADetailsProcessor();
					updategrFAProcessor.updateRatingFieldAuditorDataIntoReportingDatabase(databaseName,
							job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.DELETE_GRADING)) {
					ReportDBDeleteGradingProcessor delGraProceesor = new ReportDBDeleteGradingProcessor();
					delGraProceesor.deleteGradingDataFromReportDB(databaseName, job.getJsonObj());
					jobSuccess = true;
				} else if (operationType.equalsIgnoreCase(JobConstant.DELETE_RATING)) {
					ReportDBDeleteRatingProcessor delProceesor = new ReportDBDeleteRatingProcessor();
					delProceesor.deleteRatingDataFromReportDB(databaseName, job.getJsonObj());
					jobSuccess = true;
				}

			} catch (Exception e) {
				LOGGER.error("Retry Job execution failed because of this reason : " + e.getMessage(), e);
				jobSuccess = false;
				errorMsg = e.getMessage();
			}
			if (jobSuccess) {
				LOGGER.debug("Retry Job executed successfully..Now updating status");
				job.setStatus(JobConstant.STATUS_SUCCESS);
				job.setFailureMessage("");
				jobService.update(databaseName, jobId, job);
		//		jobService.delete(databaseName, jobId);    //After job is success, deleting the job entry.
			} else {
				job.setStatus(JobConstant.STATUS_RETRY_FAILED);
				job.setFailureMessage(errorMsg);
				int retryCount = job.getRetryCount();
				job.setRetryCount(retryCount + 1);
				jobService.update(databaseName, jobId, job);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to make changes to JOB table: " + e.getMessage(), e);
		}

	}

}
