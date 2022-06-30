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
 * Delete Grading Forms Job Assigner.
 * This class is used to delete the grading forms from the reporting tables.
 * 
 * @author Reactiveworks
 *
 */
public class DeleteGradingJobAssigner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeleteGradingJobAssigner.class);
	
	private IJobService jobService;

	public DeleteGradingJobAssigner() {
		jobService = new JobServiceImpl();
	}
	
	/**
	 * This method is used to assign the DELETE_GRADING job to the JOB table.
	 * 
	 * @param databaseName - The database name in which the job is to be assigned.
	 * @param jsonList - all the JSONS which is used for performing delete operation.
	 * @throws AssignerException if exception occurs when assigning the job (or) the JSONs passed are invalid.
	 */
	public void addDeleteGradingJobToDatabase(String databaseName, List<String> delGradingJsonList) throws AssignerException {
		LOGGER.debug(".inside addDeleteGradingJobToDatabase method of DeleteGradingJobAssigner class.");
		List<Job> allDeleteGradingJobs = new ArrayList<Job>();
		List<String> invalidJSONS = new ArrayList<String>();
		try {
			LOGGER.debug("Incoming Delete Grading JSON List size : " + delGradingJsonList.size());
			for (int i=0;i<delGradingJsonList.size();i++) {
				String json = delGradingJsonList.get(i);
				boolean validGR = JsonValidationUtil.validateJsonAgainstSchema(json,getInputStreamOfDeleteGradingJsonSchema());
				if (validGR) {
					Job job = new Job();
					job.setJobId(UUID.randomUUID().toString());
					job.setOperationType(JobConstant.DELETE_GRADING);
					job.setStatus(JobConstant.STATUS_NEW);
					job.setJsonObj(json);
					job.setCreatedTime(LocalDateTime.now().toString());
					allDeleteGradingJobs.add(job);
				} else {
					LOGGER.warn("Invalid JSON encountered in Delete Grading JSON list. ");
					invalidJSONS.add(json);
				}
		  }
			LOGGER.debug("DeleteGrading, Number of Invalid JSONs in the incoming JSON List: "+invalidJSONS.size());
			LOGGER.debug("DeleteGrading, Number of Invalid JSONs List: "+invalidJSONS);
			if(invalidJSONS.size()==0) {
				jobService.insertJobList(databaseName,allDeleteGradingJobs);
			}else {
				throw new Exception(invalidJSONS.size()+" Invalid JSON encountered in Delete Grading JSON list. ");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to assign DELETE_GRADING job, " + e.getMessage(), e);
			throw new AssignerException("Unable to assign DELETE_GRADING job, " + e.getMessage(), e);
		} 
	}

	/**
	 * Getting the inputStream of <strong>DeleteGradingJSONSchema.json</strong> file, to validate the provided JSONs.
	 * @return - the inputStream of the required file.
	 */
	private InputStream getInputStreamOfDeleteGradingJsonSchema() {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(DeleteGradingJobAssigner.class.getClassLoader()
					.getResource(
							ReportingDBConstants.REPORTING_DB_FOLDER_NAME + JsonSchemaFileName.DELETE_GRADING_JSON_SCHEMA)
					.getFile());
		} catch (FileNotFoundException e) {
			LOGGER.error("Schema file is not found.");
		}
		return inputStream;
	}
}
