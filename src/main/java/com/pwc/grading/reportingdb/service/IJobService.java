package com.pwc.grading.reportingdb.service;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.reportingdb.model.Job;
import com.pwc.grading.reportingdb.service.exp.ReportingDbServiceException;

/**
 * An interface class which is used to perform all
 * Job related operations.
 *
 */
public interface IJobService {

	/**
	 * This method is used to create the job.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param job the job details which are to be created.
	 * @throws ReportingDbServiceException
	 */
	void create(String databaseName, Job job) throws ReportingDbServiceException;
	
	/**
	 * This method is used to update the job. The Job entry have to exist.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param id the job id.
	 * @param job the job details which are to be updated.
	 * @throws ReportingDbServiceException
	 */
	void update(String databaseName, String id, Job job) throws ReportingDbServiceException;
	
	/**
	 * This method is used to get all the jobs.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @return list of all the jobs.
	 * @throws ReportingDbServiceException
	 */
	List<Job> getAll(String databaseName) throws ReportingDbServiceException;
	
	/**
	 * This method is used to get a job by its id.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param id the job id.
	 * @return a Job entry.
	 * @throws ReportingDbServiceException
	 */
	Job getById(String databaseName,String id) throws ReportingDbServiceException;
	
	/**
	 * This method is used to get all the failed jobs.
	 * Failed Jobs status are FAILED and RETRY FAILED.
	 * @param database the database name indicating which database is used for this operation.
	 * @return  list of all the failed jobs.
	 * @throws ReportingDbServiceException
	 */
	List<Job> getAllFailedJobs(String database) throws ReportingDbServiceException;
	
	/**
	 * This method is used to get all the new Jobs.
	 * New Jobs status are NEW and IN PROGRESS.
	 * @param database the database name indicating which database is used for this operation.
	 * @return  list of all the new jobs.
	 * @throws ReportingDbServiceException
	 */
	List<Job> getAllNewJobs(String database) throws ReportingDbServiceException;
	
	/**
	 * This method is used to get all the failed jobs.
	 * Failed Jobs status are FAILED and RETRY FAILED.
	 * @param connection  used to perform the database operations in a single transaction.
	 * @param database the database name indicating which database is used for this operation.
	 * @return list of all the failed jobs.
	 * @throws ReportingDbServiceException
	 */
	List<Job> getAllFailedJobs(Connection connection, String database) throws ReportingDbServiceException;
	
	/**
	 * This method is used to get all the new Jobs.
	 * New Jobs status are NEW and IN PROGRESS. 
	 * @param connection  used to perform the database operations in a single transaction.
	 * @param database the database name indicating which database is used for this operation.
	 * @return list of all the new jobs.
	 * @throws ReportingDbServiceException
	 */
	List<Job> getAllNewJobs(Connection connection,String database) throws ReportingDbServiceException;
	
	/**
	 * This method is used to delete the Job by given id.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param id the job id to delete.
	 * @throws ReportingDbServiceException
	 */
	void delete(String databaseName,String id) throws ReportingDbServiceException;

	/**
	 * This method is used to insert the given {@link List} of Jobs.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param allJobs List of Jobs to be inserted.
	 * @throws ReportingDbServiceException
	 */
	void insertJobList(String databaseName, List<Job> allJobs) throws ReportingDbServiceException;;
}
