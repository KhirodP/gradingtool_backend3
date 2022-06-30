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

/**
 * Update Rating form Field-Auditor's Submit.
 * This assigner is used to assign the job which performs the updation of Field-Auditor response
 * of a Rating form into the reporting tables.
 *
 */
public class UpdateRatingFAJobAssigner {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateRatingFAJobAssigner.class);

//	@Inject
	private IJobService jobService;

	public UpdateRatingFAJobAssigner() {
		jobService = new JobServiceImpl();
	}

	/**
	 * This method is used to assign the job which performs the updation of Field-Auditor response
	 * of a Rating form into the reporting tables.
	 * @param databaseName - The database name in which the job is to be assigned.
	 * @param json - the json which contains the details of field-auditor response.
	 * @throws AssignerException - if exception occurs when assigning the job (or) the JSONs passed are invalid.
	 */
	public void assignUpdateRatingFAJobToDatabase(String databaseName, String json) throws AssignerException {
		LOGGER.debug(".inside assignUpdateRatingFAJobToDatabase method of UpdateRatingFAJobAssigner class.");
		try {
			if (FormsUtil.isRatingFormJSON(json)) {
				LOGGER.debug("UPDATE RATING FA JOB ASSIGNER ----> RATING FORM");
				boolean validGR = JsonValidationUtil.validateJsonAgainstSchema(json,
						getInputStreamOfGradingJsonSchema());
				LOGGER.debug("### Update Rating FA JSON IS VALID? : " + validGR);
				if (validGR) {
					Job job = new Job();
					job.setJobId(UUID.randomUUID().toString());
					job.setOperationType(JobConstant.UPDATE_RATING_FA_SUBMIT);
					job.setStatus(JobConstant.STATUS_NEW);
					job.setJsonObj(json);
					LOGGER.debug("Created Job in UpdateRatingFAJobAssigner class: " + job);
					jobService.create(databaseName, job);
//					ReportDatabaseCallJob reportDatabaseJob = new ReportDatabaseCallJob(databaseName);
//					reportDatabaseJob.setJob(job);
//					if (ExecutorServiceConfig.ENABLE_EXEC_SERVICE) //
//						ExecutorServiceConfig.getExecutorService().submit(reportDatabaseJob);
				}
			} else {
				LOGGER.info("Given update Rating FA Job JSON is Not Rating form.");
				throw new Exception("Given update Rating FA Job JSON is Not Rating form.");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to assign "+JobConstant.UPDATE_RATING_FA_SUBMIT+" job, " + e.getMessage(), e);
			throw new AssignerException("Unable to assign "+JobConstant.UPDATE_RATING_FA_SUBMIT+" job, " + e.getMessage(), e);
		}

	}

	/**
	 * Getting the inputStream of <strong>UpdateRatingFieldAuditorJSONSchema.json</strong> file, to validate the provided JSONs.
	 * @return - the inputStream of the required file.
	 */
	private static InputStream getInputStreamOfGradingJsonSchema() {
		
		InputStream inputStream = null;
		inputStream = AddGradingJobAssigner.class.getClassLoader().getResourceAsStream(
		ReportingDBConstants.REPORTING_DB_FOLDER_NAME + JsonSchemaFileName.UPDATE_RATING_FA_JSON_SCHEMA);
		if (inputStream == null)
		LOGGER.error("Schema file is not found.");
		return inputStream;
		
		
	}

}
