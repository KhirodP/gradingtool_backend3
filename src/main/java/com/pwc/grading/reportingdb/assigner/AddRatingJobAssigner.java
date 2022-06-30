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
import com.pwc.grading.reportingdb.util.FormsUtil;
import com.pwc.grading.reportingdb.util.JsonValidationUtil;

/**
 * Add Rating Forms Job Assigner.
 * This class is used to assign the rating forms (in the form of JSON) which are to be inserted in the reporting tables.
 * @author Reactiveworks
 *
 */
public class AddRatingJobAssigner {

	private static final Logger LOGGER = LoggerFactory.getLogger(AddRatingJobAssigner.class);

//	@Inject
	private IJobService jobService;

	public AddRatingJobAssigner() {
		jobService = new JobServiceImpl();
	}

	/**
	 * This method is used to assign the ADD_RATING job to the JOB table.
	 * 
	 * @param databaseName - The database name in which the job is to be assigned.
	 * @param jsonList - all the rating forms in the form of JSONs.
	 * @throws AssignerException if exception occurs when assigning the job (or) the JSONs passed are invalid.
	 */
	public void assignAddRatingJobToDatabase(String databaseName, List<String> jsonList) throws AssignerException {
		LOGGER.debug(".inside assignAddRatingJobToDatabase method of AddRatingJobAssigner class.");
		List<Job> allAddRatingJobs = new ArrayList<Job>();
		List<String> invalidJSONS = new ArrayList<String>();
		try {
			LOGGER.debug("Incoming AddRating JSON List size : " + jsonList.size());
			for (int i=0;i<jsonList.size();i++) {
				String json = jsonList.get(i);
				if (FormsUtil.isRatingFormJSON(json)) {
					boolean validGR = JsonValidationUtil.validateJsonAgainstSchema(json,getInputStreamOfRatingJsonSchema());
					if (validGR) {
						Job job = new Job();
						job.setJobId(UUID.randomUUID().toString());
						job.setOperationType(JobConstant.ADD_RATING);
						job.setStatus(JobConstant.STATUS_NEW);
						job.setJsonObj(json);
						job.setCreatedTime(LocalDateTime.now().toString());
//						LOGGER.debug("Created Job in AddGradingJobAssigner class: " + job);
						allAddRatingJobs.add(job);
//						jobService.create(databaseName, job);
//						ReportDatabaseCallJob reportDatabaseJob = new ReportDatabaseCallJob(databaseName);
//						reportDatabaseJob.setJob(job);
//						if (ExecutorServiceConfig.ENABLE_EXEC_SERVICE) //
//							ExecutorServiceConfig.getExecutorService().submit(reportDatabaseJob);
					} else {
						LOGGER.warn("Invalid JSON encountered in Add Rating JSON list. ");
						invalidJSONS.add(json);
					}
				} else {
					LOGGER.error("Given add Job JSON is Not a Rating form, Index : "+i);
					throw new Exception("Given add Job JSON is Not a Rating form, Index : "+i);
				}
			}
			LOGGER.debug("AddRating, Number of Invalid JSONs in the incoming JSON List: "+invalidJSONS.size());
			LOGGER.debug("AddRating, Number of Invalid JSONs List: "+invalidJSONS);
			if(invalidJSONS.size()==0) {
				jobService.insertJobList(databaseName,allAddRatingJobs);
			}else {
				throw new Exception(invalidJSONS.size()+" Invalid JSON encountered in Add Rating JSON list. ");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to assign ADD_RATING job, " + e.getMessage(), e);
			throw new AssignerException("Unable to assign ADD_RATING job, " + e.getMessage(), e);
		}

	}

	/**
	 * Getting the inputStream of <strong>AddRatingJSONSchema.json</strong> file, to validate the provided JSONs.
	 * @return - the inputStream of the required file.
	 */
	private static InputStream getInputStreamOfRatingJsonSchema() {
		
		InputStream inputStream = null;
		inputStream = AddGradingJobAssigner.class.getClassLoader().getResourceAsStream(
		ReportingDBConstants.REPORTING_DB_FOLDER_NAME + JsonSchemaFileName.ADD_RATING_JSON_SCHEMA);
		if (inputStream == null)
		LOGGER.error("Schema file is not found.");
		return inputStream;
	}
}
