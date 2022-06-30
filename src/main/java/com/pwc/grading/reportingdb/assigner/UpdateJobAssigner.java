package com.pwc.grading.reportingdb.assigner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.ReportingDBConstants;
import com.pwc.grading.reportingdb.assigner.exception.AssignerException;
import com.pwc.grading.reportingdb.constant.JobConstant;
import com.pwc.grading.reportingdb.model.Job;
import com.pwc.grading.reportingdb.processor.constant.JsonSchemaFileName;
import com.pwc.grading.reportingdb.service.IJobService;
import com.pwc.grading.reportingdb.service.impl.JobServiceImpl;
import com.pwc.grading.reportingdb.util.FormsUtil;
import com.pwc.grading.reportingdb.util.JsonValidationUtil;

public class UpdateJobAssigner {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateJobAssigner.class);

//	@Inject
	private IJobService jobService;
	
	public UpdateJobAssigner() {
		jobService = new JobServiceImpl();
	}
	
	public void assignUpdateJobToDatabase(String databaseName, String json) throws AssignerException{
		LOGGER.debug(".inside assignUpdateJobToDatabase method of UpdateJobAssigner class.");
		try {
			if(FormsUtil.isGradingFormJSON(json)) {
				LOGGER.debug("UPDATE JOB ASSIGNER ----> GRADING FORM");
				boolean validGR = JsonValidationUtil.validateJsonAgainstSchema(json, getInputStreamOfGradingJsonSchema());
				LOGGER.debug("### UPDATE GRADING JSON IS VALID? : "+validGR);
				if(validGR) {
					Job job = new Job();
					job.setJobId(UUID.randomUUID().toString());
					job.setOperationType(JobConstant.UPDATE);
					job.setStatus(JobConstant.STATUS_NEW);
					job.setJsonObj(json);
					LOGGER.debug("Created Job in UpdateJobAssigner class: "+job);
					jobService.create(databaseName, job);
//					ReportDatabaseCallJob reportDatabaseJob = new ReportDatabaseCallJob(databaseName);
//					reportDatabaseJob.setJob(job);
//					if(ExecutorServiceConfig.ENABLE_EXEC_SERVICE) //
//					ExecutorServiceConfig.getExecutorService().submit(reportDatabaseJob);
				}
			}else if (FormsUtil.isRatingFormJSON(json)){
				LOGGER.debug("UPDATE JOB ASSIGNER ----> RATING FORM");
				boolean validRA = JsonValidationUtil.validateJsonAgainstSchema(json, getInputStreamOfRatingJsonSchema());
				LOGGER.debug("### UPDATE RATING JSON IS VALID? : "+validRA);
				if(validRA) {
					Job job = new Job();
					job.setJobId(UUID.randomUUID().toString());
					job.setOperationType(JobConstant.UPDATE);
					job.setStatus(JobConstant.STATUS_NEW);
					job.setJsonObj(json);
					LOGGER.debug("Created Job in UpdateJobAssigner class: "+job);
					jobService.create(databaseName, job);
//					ReportDatabaseCallJob reportDatabaseJob = new ReportDatabaseCallJob(databaseName);
//					reportDatabaseJob.setJob(job);
//					if(ExecutorServiceConfig.ENABLE_EXEC_SERVICE) //
//					ExecutorServiceConfig.getExecutorService().submit(reportDatabaseJob);
				}
			} else {
				LOGGER.error("Given update Job JSON is Neither Rating form Nor Grading form.");
				throw new Exception("Given update Job JSON is Neither Rating form Nor Grading form.");
			}
		}catch (Exception e) {
			LOGGER.error("Unable to assign update job, "+e.getMessage(),e);
			throw new AssignerException("Unable to assign update job, "+e.getMessage(),e);
		}
		
	}

	private static InputStream getInputStreamOfGradingJsonSchema() {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(
					UpdateJobAssigner.class.getClassLoader().getResource(ReportingDBConstants.REPORTING_DB_FOLDER_NAME +JsonSchemaFileName.UPDATE_GRADING_JSON_SCHEMA).getFile());
		} catch (FileNotFoundException e) {
			LOGGER.error("Schema file is not found.");
		}
		return inputStream;
	}
	
	private static InputStream getInputStreamOfRatingJsonSchema() {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(
					UpdateJobAssigner.class.getClassLoader().getResource(ReportingDBConstants.REPORTING_DB_FOLDER_NAME +JsonSchemaFileName.UPDATE_RATING_JSON_SCHEMA).getFile());
		} catch (FileNotFoundException e) {
			LOGGER.error("Schema file is not found.");
		}
		return inputStream;
	}
}
