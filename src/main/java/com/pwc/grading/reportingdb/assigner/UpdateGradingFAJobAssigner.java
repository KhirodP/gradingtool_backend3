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
 * Update Grading Field-Auditor's submit.
 * This assigner is used to assign the job which performs the updation of Field-Auditor response
 * of a Grading form into the reporting tables.
 *
 */
public class UpdateGradingFAJobAssigner {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateGradingFAJobAssigner.class);

//	@Inject
	private IJobService jobService;

	public UpdateGradingFAJobAssigner() {
		jobService = new JobServiceImpl();
	}

	/**
	 * This method is used to assign the job which performs the updation of Field-Auditor response
	 * of a Grading form into the reporting tables.
	 * @param databaseName - The database name in which the job is to be assigned.
	 * @param json - the json which contains the details of field-auditor response.
	 * @throws AssignerException - if exception occurs when assigning the job (or) the JSONs passed are invalid.
	 */
	public void assignUpdateGradingFAJobToDatabase(String databaseName, String json) throws AssignerException {
		LOGGER.debug(".inside assignUpdateJobToDatabase method of UpdateGradingFAJobAssigner class.");
		try {
			if (FormsUtil.isGradingFormJSON(json)) {
				LOGGER.debug("UPDATE GRADING FA JOB ASSIGNER ----> GRADING FORM");
				boolean validGR = JsonValidationUtil.validateJsonAgainstSchema(json,
						getInputStreamOfGradingJsonSchema());
				LOGGER.debug("### Update Grading FA JSON IS VALID? : " + validGR);
				if (validGR) {
					Job job = new Job();
					job.setJobId(UUID.randomUUID().toString());
					job.setOperationType(JobConstant.UPDATE_GRADING_FA_SUBMIT);
					job.setStatus(JobConstant.STATUS_NEW);
					job.setJsonObj(json);
					LOGGER.debug("Created Job in UpdateGradingFAJobAssigner class: " + job);
					jobService.create(databaseName, job);
//					ReportDatabaseCallJob reportDatabaseJob = new ReportDatabaseCallJob(databaseName);
//					reportDatabaseJob.setJob(job);
//					if (ExecutorServiceConfig.ENABLE_EXEC_SERVICE) //
//						ExecutorServiceConfig.getExecutorService().submit(reportDatabaseJob);
				}
			} else {
				LOGGER.error("Given update Grading FA Job JSON is Not Grading form.");
				throw new Exception("Given update Grading FA Job JSON is Not Grading form.");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to assign "+JobConstant.UPDATE_GRADING_FA_SUBMIT+" job, " + e.getMessage(), e);
			throw new AssignerException("Unable to assign "+JobConstant.UPDATE_GRADING_FA_SUBMIT+" job, " + e.getMessage(), e);
		}

	}

	/**
	 * Getting the inputStream of <strong>UpdateGradingFieldAuditorJSONSchema.json</strong> file, to validate the provided JSONs.
	 * @return - the inputStream of the required file.
	 */
	private static InputStream getInputStreamOfGradingJsonSchema() {
		
		InputStream inputStream = null;
		inputStream = AddGradingJobAssigner.class.getClassLoader().getResourceAsStream(
		ReportingDBConstants.REPORTING_DB_FOLDER_NAME + JsonSchemaFileName.UPDATE_GRADING_FA_JSON_SCHEMA);
		if (inputStream == null)
		LOGGER.error("Schema file is not found.");
		return inputStream;
	}

}
