package com.pwc.grading.reportingdb.service.impl;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.dao.IJobDAO;
import com.pwc.grading.reportingdb.dao.impl.JobDAOImpl;
import com.pwc.grading.reportingdb.model.Job;
import com.pwc.grading.reportingdb.service.IJobService;
import com.pwc.grading.reportingdb.service.exp.ReportingDbServiceException;

/**
 * Implementation class for {@link IJobService}
 *
 */
//@Singleton
public class JobServiceImpl implements IJobService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

	private IJobDAO dao;

//	@Inject
	public JobServiceImpl() {
		dao = new JobDAOImpl();
	}

	/**
	 * This method is used to create the job.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param job the job details which are to be created.
	 * @throws ReportingDbServiceException
	 */
	@Override
	public void create(String databaseName, Job job) throws ReportingDbServiceException {
//		LOGGER.debug(".inside create method method of JobServiceImpl");
		try {
			String createdTime = LocalDateTime.now().toString();
			job.setCreatedTime(createdTime);
			dao.createJob(databaseName, job);
		} catch (Exception e) {
			LOGGER.error("Unable to create Job.", e);
			throw new ReportingDbServiceException("Unable to create Job, " + e.getMessage(), e);
		}
//		LOGGER.debug("Insert to JOB database is success.");
	}

	/**
	 * This method is used to update the job. The Job entry have to exist.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param id the job id.
	 * @param job the job details which are to be updated.
	 * @throws ReportingDbServiceException
	 */
	@Override
	public void update(String databaseName, String id, Job job) throws ReportingDbServiceException {
		LOGGER.debug(".inside update method method of JobServiceImpl");
		Job oldJob;
		try {
			oldJob = dao.getJobById(databaseName, id);
			if (oldJob == null) {
				throw new Exception("No data found for JOB with Id to update: " + id);
			} else {
				LOGGER.debug("JOB exists, updating job.");
				dao.updateJob(databaseName, id, job);
			}
		} catch (Exception e) {
			throw new ReportingDbServiceException("Unable to update Job data: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get all the jobs.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @return list of all the jobs.
	 * @throws ReportingDbServiceException
	 */
	@Override
	public List<Job> getAll(String databaseName) throws ReportingDbServiceException {
		LOGGER.debug(".inside getAll method method of JobServiceImpl");
		try {
			List<Job> allObjs = dao.getAll(databaseName);
			return allObjs;
		} catch (Exception e) {
			LOGGER.error("Unable to get all the jobs.", e);
			throw new ReportingDbServiceException("Unable to get all the jobs." + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get a job by its id.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param id the job id.
	 * @return a Job entry.
	 * @throws ReportingDbServiceException
	 */
	@Override
	public Job getById(String databaseName, String id) throws ReportingDbServiceException {
		LOGGER.debug(".inside getById method method of JobServiceImpl");
		try {
			Job byId = dao.getJobById(databaseName, id);
			return byId;
		} catch (Exception e) {
			LOGGER.error("Unable to get all the job.", e);
			throw new ReportingDbServiceException("Unable to get Job for id '" + id + "' , " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to delete the Job by given id.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param id the job id to delete.
	 * @throws ReportingDbServiceException
	 */
	@Override
	public void delete(String databaseName, String id) throws ReportingDbServiceException {
		LOGGER.debug(".inside delete method method of JobServiceImpl");
		try {
			dao.deleteJobById(databaseName, id);
		} catch (Exception e) {
			throw new ReportingDbServiceException("Unable to delete data: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get all the failed jobs.
	 * Failed Jobs status are FAILED and RETRY FAILED.
	 * @param database the database name indicating which database is used for this operation.
	 * @return  list of all the failed jobs.
	 * @throws ReportingDbServiceException
	 */
	@Override
	public List<Job> getAllFailedJobs(String database) throws ReportingDbServiceException {
		LOGGER.debug(".inside getAllFailedJobs method method of JobServiceImpl");
		try {
			List<Job> allObjs = dao.getAllFailedJobs(database);
			return allObjs;
		} catch (Exception e) {
			LOGGER.error("Unable to get the failed jobs, ", e);
			throw new ReportingDbServiceException("Unable to get the failed jobs, " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get all the new Jobs.
	 * New Jobs status are NEW and IN PROGRESS.
	 * @param database the database name indicating which database is used for this operation.
	 * @return  list of all the new jobs.
	 * @throws ReportingDbServiceException
	 */
	@Override
	public List<Job> getAllNewJobs(String database) throws ReportingDbServiceException {
		LOGGER.debug(".inside getAllNewJobs method of JobServiceImpl");
		try {
			List<Job> allObjs = dao.getNewJobsSortedbyCreatedTime(database);
			return allObjs;
		} catch (Exception e) {
			LOGGER.error("Unable to get the new jobs, ", e);
			throw new ReportingDbServiceException("Unable to get the new jobs, " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to insert the given {@link List} of Jobs.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param allJobs List of Jobs to be inserted.
	 * @throws ReportingDbServiceException
	 */
	@Override
	public void insertJobList(String databaseName, List<Job> allAddGradingJobs) throws ReportingDbServiceException {
		LOGGER.debug(".inside insertJobList method of JobServiceImpl");
		try {
			long start = System.currentTimeMillis();
			dao.insertJobList(databaseName,allAddGradingJobs);
			long end = System.currentTimeMillis();
			LOGGER.debug("Time taken for "+allAddGradingJobs.size()+" entries Batch insertion : "+(end-start));
		} catch (Exception e) {
			LOGGER.error("Unable to get the new jobs, ", e);
			throw new ReportingDbServiceException("Unable to get the new jobs, " + e.getMessage(), e);
		}

		
	}

	/**
	 * This method is used to get all the failed jobs.
	 * Failed Jobs status are FAILED and RETRY FAILED.
	 * @param connection  used to perform the database operations in a single transaction.
	 * @param database the database name indicating which database is used for this operation.
	 * @return list of all the failed jobs.
	 * @throws ReportingDbServiceException
	 */
	@Override
	public List<Job> getAllFailedJobs(Connection connection, String database) throws ReportingDbServiceException {
		LOGGER.debug(".inside getAllFailedJobs(con) method method of JobServiceImpl");
//		LOGGER.debug("Connection is there.");
		try {
			List<Job> allObjs = dao.getAllFailedJobs(connection,database);
			return allObjs;
		} catch (Exception e) {
			LOGGER.error("Unable to get the failed jobs, ", e);
			throw new ReportingDbServiceException("Unable to get the failed jobs, " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get all the new Jobs.
	 * New Jobs status are NEW and IN PROGRESS. 
	 * @param connection  used to perform the database operations in a single transaction.
	 * @param database the database name indicating which database is used for this operation.
	 * @return list of all the new jobs.
	 * @throws ReportingDbServiceException
	 */
	@Override
	public List<Job> getAllNewJobs(Connection connection, String database) throws ReportingDbServiceException {
		LOGGER.debug(".inside getAllNewJobs(con) method of JobServiceImpl");
//		LOGGER.debug("Connection is there.");
		try {
			List<Job> allObjs = dao.getNewJobsSortedbyCreatedTime(connection,database);
			return allObjs;
		} catch (Exception e) {
			LOGGER.error("Unable to get the new jobs, ", e);
			throw new ReportingDbServiceException("Unable to get the new jobs, " + e.getMessage(), e);
		}
	}

}
