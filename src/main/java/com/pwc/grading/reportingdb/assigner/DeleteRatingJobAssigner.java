package com.pwc.grading.reportingdb.assigner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
 * Delete Rating Form data from Reporting tables.
 * This Assigner is used to assign the job which performs the deletion of Rating forms data from the reporting tables.
 *
 */
public class DeleteRatingJobAssigner {
	private static final Logger LOGGER = LoggerFactory.getLogger(DeleteGradingJobAssigner.class);

	private IJobService jobService;

	public DeleteRatingJobAssigner() {
		jobService = new JobServiceImpl();
	}

	/**
	 * This method is used to assign the job to delete rating forms data.
	 * @param databaseName - The database name in which the job is to be assigned.
	 * @param json - the json which contains the rating form deletion details.
	 * @throws AssignerException - if exception occurs when assigning the job (or) the JSONs passed are invalid.
	 */
	public void addDeleteRatingJobToDatabase(String databaseName, List<String> delRatingJsonList)
			throws AssignerException {
		LOGGER.debug(".inside addDeleteRatingJobToDatabase method of DeleteRatingFormAssigner class.");
		List<Job> allDeleteRatingJobs = new ArrayList<Job>();
		List<String> invalidJSONS = new ArrayList<String>();
		try {
			LOGGER.debug("Incoming Delete Rating JSON List size : " + delRatingJsonList.size());
			for (int i = 0; i < delRatingJsonList.size(); i++) {
				String json = delRatingJsonList.get(i);
				boolean validGR = JsonValidationUtil.validateJsonAgainstSchema(json,
						getInputStreamOfDeleteRatingJsonSchema());
				if (validGR) {
					Job job = new Job();
					job.setJobId(UUID.randomUUID().toString());
					job.setOperationType(JobConstant.DELETE_RATING);
					job.setStatus(JobConstant.STATUS_NEW);
					job.setJsonObj(json);
					job.setCreatedTime(LocalDateTime.now().toString());
					allDeleteRatingJobs.add(job);
				} else {
					LOGGER.warn("Invalid JSON encountered in Delete Rating JSON list. ");
					invalidJSONS.add(json);
				}
			}
			LOGGER.debug("DeleteRating, Number of Invalid JSONs in the incoming JSON List: " + invalidJSONS.size());
			LOGGER.debug("DeleteRating, Number of Invalid JSONs List: " + invalidJSONS);
			if (invalidJSONS.size() == 0) {
				jobService.insertJobList(databaseName, allDeleteRatingJobs);
			} else {
				throw new Exception(invalidJSONS.size() + " Invalid JSON encountered in Delete Grading JSON list. ");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to assign DeleteRating job, " + e.getMessage(), e);
			throw new AssignerException("Unable to assign DeleteRating job, " + e.getMessage(), e);
		}
	}

	/**
	 * Getting the inputStream of <strong>DeleteRatingJsonSchema.json</strong> file, to validate the provided JSONs.
	 * @return - the inputStream of the required file.
	 */
	private InputStream getInputStreamOfDeleteRatingJsonSchema() {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(DeleteRatingJobAssigner.class.getClassLoader().getResource(
					ReportingDBConstants.REPORTING_DB_FOLDER_NAME + JsonSchemaFileName.DELETE_RATING_JSON_SCHEMA)
					.getFile());
		} catch (FileNotFoundException e) {
			LOGGER.error("Schema file is not found.");
		}
		return inputStream;
	}

}
