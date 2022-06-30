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
import com.pwc.grading.reportingdb.util.JsonValidationUtil;

/**
 * Update Grading Form FieldAuditor's Name and Phone.
 * This assigner is used to assign the job which performs the updation of Field-Auditor name and phone
 * details of a Grading form into the reporting tables.
 *
 */
public class UpdateGradingFieldAuditorDetailsJobAssigner {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateGradingFieldAuditorDetailsJobAssigner.class);

//	@Inject
	private IJobService jobService;

	public UpdateGradingFieldAuditorDetailsJobAssigner() {
		jobService = new JobServiceImpl();
	}

	/**
	 * This method is used to assign the job which performs the updation of Field-Auditor name and phone
	 * details of a Grading form into the reporting tables.
	 * @param databaseName - The database name in which the job is to be assigned.
	 * @param json - the json which contains the details of field-auditor details.
	 * @throws AssignerException - if exception occurs when assigning the job (or) the JSONs passed are invalid.
	 */
	public void assignUpdateFADetailsJobToDatabase(String databaseName, String json) throws AssignerException {
		LOGGER.debug(".inside assignUpdateFADetailsJobToDatabase method of UpdateGradingFieldAuditorDetailsJobAssigner class.");
		try {
			boolean validGR = JsonValidationUtil.validateJsonAgainstSchema(json, getInputStreamOfJsonSchema());
			LOGGER.debug("### Update FA JSON IS VALID? : " + validGR);
			if (validGR) {
				Job job = new Job();
				job.setJobId(UUID.randomUUID().toString());
				job.setOperationType(JobConstant.UPDATE_GRADING_FA_DETAILS);
				job.setStatus(JobConstant.STATUS_NEW);
				job.setJsonObj(json);
				LOGGER.debug("Created Job in UpdateGradingFieldAuditorDetailsJobAssigner class: " + job);
				jobService.create(databaseName, job);
//				ReportDatabaseCallJob reportDatabaseJob = new ReportDatabaseCallJob(databaseName);
//				reportDatabaseJob.setJob(job);
//				if (ExecutorServiceConfig.ENABLE_EXEC_SERVICE) //
//					ExecutorServiceConfig.getExecutorService().submit(reportDatabaseJob);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to assign "+JobConstant.UPDATE_GRADING_FA_DETAILS+" job, " + e.getMessage(), e);
			throw new AssignerException("Unable to assign "+JobConstant.UPDATE_GRADING_FA_DETAILS+" job, " + e.getMessage(), e);
		}

	}

	/**
	 * Getting the inputStream of <strong>UpdateGradingFADetailsJSONSchema.json</strong> file, to validate the provided JSONs.
	 * @return - the inputStream of the required file.
	 */
	private static InputStream getInputStreamOfJsonSchema() {
		
		
		InputStream inputStream = null;
		inputStream = AddGradingJobAssigner.class.getClassLoader().getResourceAsStream(
		ReportingDBConstants.REPORTING_DB_FOLDER_NAME + JsonSchemaFileName.UPDATE_GRADING_FA_DETAILS_JSON_SCHEMA);
		if (inputStream == null)
		LOGGER.error("Schema file is not found.");
		return inputStream;
	}
}
