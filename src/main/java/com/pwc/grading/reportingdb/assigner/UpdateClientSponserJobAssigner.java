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
 * Update Client Sponsor details.
 * This Assigner is used to assign the job which performs the updation of Client Sponsor details.
 *
 */
public class UpdateClientSponserJobAssigner {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateClientSponserJobAssigner.class);
	
//	@Inject
	private IJobService jobService;

	public UpdateClientSponserJobAssigner() {
		jobService = new JobServiceImpl();
	}

	/**
	 * This method is used to assign the job of updating Client Sponsor details in the 
	 * reporting tables.
	 * @param databaseName - The database name in which the job is to be assigned.
	 * @param json - the json which contains the details to update Client Sponsor
	 * @throws AssignerException - if exception occurs when assigning the job (or) the JSONs passed are invalid.
	 */
	public void assignUpdateCSJobToDatabase(String databaseName, String json) throws AssignerException {
		LOGGER.debug(".inside assignUpdateCSJobToDatabase method of UpdateClientSponserJobAssigner class.");
		try {
			boolean validGR = JsonValidationUtil.validateJsonAgainstSchema(json, getInputStreamOfJsonSchema());
			LOGGER.debug("### Update CS JSON IS VALID? : " + validGR);
			if (validGR) {
				Job job = new Job();
				job.setJobId(UUID.randomUUID().toString());
				job.setOperationType(JobConstant.UPDATE_CS);
				job.setStatus(JobConstant.STATUS_NEW);
				job.setJsonObj(json);
				LOGGER.debug("Created Job in UpdateClientSponserJobAssigner class: " + job);
				jobService.create(databaseName, job);
//				ReportDatabaseCallJob reportDatabaseJob = new ReportDatabaseCallJob(databaseName);
//				reportDatabaseJob.setJob(job);
//				if (ExecutorServiceConfig.ENABLE_EXEC_SERVICE) //
//					ExecutorServiceConfig.getExecutorService().submit(reportDatabaseJob);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to assign UPDATE_CS job, " + e.getMessage(), e);
			throw new AssignerException("Unable to assign UPDATE_CS job, " + e.getMessage(), e);
		}

	}

	/**
	 * Getting the inputStream of <strong>UpdateCSJSONSchema.json</strong> file, to validate the provided JSONs.
	 * @return - the inputStream of the required file.
	 */
	private static InputStream getInputStreamOfJsonSchema() {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(UpdateClientSponserJobAssigner.class.getClassLoader()
					.getResource(
							ReportingDBConstants.REPORTING_DB_FOLDER_NAME + JsonSchemaFileName.UPDATE_CS_JSON_SCHEMA)
					.getFile());
		} catch (FileNotFoundException e) {
			LOGGER.error("Schema file is not found.");
		}
		return inputStream;
	}
}
