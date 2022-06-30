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
 * Delete a Project
 * This assigner is used to assign the job to delete the entire project data from the reporting tables.
 * @author Reactiveworks
 *
 */
public class DeleteJobAssigner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeleteJobAssigner.class);

//	@Inject
	private IJobService jobService;

	public DeleteJobAssigner() {
		jobService = new JobServiceImpl();
	}

	/**
	 * This method is used to assign the job to delete entire project data.
	 * @param databaseName - The database name in which the job is to be assigned.
	 * @param json - the json which contains the projectId.
	 * @throws AssignerException - if exception occurs when assigning the job (or) the JSONs passed are invalid.
	 */
	public void assignDeleteJobToDatabase(String databaseName, String json) throws AssignerException {
		LOGGER.debug(".inside assignDeleteJobToDatabase method of DeleteJobAssigner class.");
		try {
			boolean validGR = JsonValidationUtil.validateJsonAgainstSchema(json, getInputStreamOfJsonSchema());
			LOGGER.debug("### DELETE JSON IS VALID? : " + validGR);
			if (validGR) {
				Job job = new Job();
				job.setJobId(UUID.randomUUID().toString());
				job.setOperationType(JobConstant.DELETE);
				job.setStatus(JobConstant.STATUS_NEW);
				job.setJsonObj(json);
				LOGGER.debug("Created Job in DeleteJobAssigner class: " + job);
				jobService.create(databaseName, job);
//				ReportDatabaseCallJob reportDatabaseJob = new ReportDatabaseCallJob(databaseName);
//				reportDatabaseJob.setJob(job);
//				if (ExecutorServiceConfig.ENABLE_EXEC_SERVICE) //
//					ExecutorServiceConfig.getExecutorService().submit(reportDatabaseJob);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to assign "+JobConstant.DELETE+" job, " + e.getMessage(), e);
			throw new AssignerException("Unable to assign "+JobConstant.DELETE+" job, " + e.getMessage(), e);
		}

	}

	/**
	 * Getting the inputStream of <strong>DeleteJSONSchema.json</strong> file, to validate the provided JSONs.
	 * @return - the inputStream of the required file.
	 */
	private static InputStream getInputStreamOfJsonSchema() {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(DeleteJobAssigner.class.getClassLoader()
					.getResource(ReportingDBConstants.REPORTING_DB_FOLDER_NAME + JsonSchemaFileName.DELETE_JSON_SCHEMA)
					.getFile());
		} catch (FileNotFoundException e) {
			LOGGER.error("Schema file is not found.");
		}
		return inputStream;
	}

}
