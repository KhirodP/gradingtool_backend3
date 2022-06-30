package com.pwc.grading.reportingdb.dao;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.model.Job;
/**
 * An interface class which is used to perform all
 * JOB related database operations.
 *
 */
public interface IJobDAO {

	/**
	 * This method is used to create the job entry in the Job table.
	 * @param databaseName the database name which is having the Job table.
	 * @param job the job details which is to be created.
	 * @throws ReportingDbDAOException if any exception occurs when creating the job.
	 */
	void createJob(String databaseName, Job job) throws ReportingDbDAOException;
	
	/**
	 * This method is used to update the existing job entry in the Job table.
	 * @param databaseName the database name which is having the Job table.
	 * @param id the job id whose details are to be updated.
	 * @param job the job details which is to be created.
	 * @throws ReportingDbDAOException if any exception occurs when updating the job.
	 */
	void updateJob(String databaseName, String id, Job job) throws ReportingDbDAOException;
	
	/**
	 * This method is used to get all the job entries in the Job table.
	 * @param databaseName the database name which is having the Job table.
	 * @throws ReportingDbDAOException if any exception occurs when getting all the jobs.
	 */
	List<Job> getAll(String databaseName) throws ReportingDbDAOException;
	
	/**
	 * This method is used to get the job entry for the given JobId
	 * @param databaseName the database name which is having the Job table.
	 * @param id the job id whose details are to be fetched.
	 * @throws ReportingDbDAOException if any exception occurs when updating the job.
	 */
	Job getJobById(String databaseName,String id) throws ReportingDbDAOException;
	
	/**
	 * This method is used to fetch first 200 the failed jobs from the Job table
	 * sorted by the endTime in ascending order.
	 * The jobs which are failed first, will come first.
	 * @param database the database name which is having the Job table.
	 * @return the {@link List} of {@link Job} entries which are failed.
	 * @throws ReportingDbDAOException if any exception occurs when fetching failed jobs.
	 */
	List<Job> getAllFailedJobs(String database) throws ReportingDbDAOException;
	
	/**
	 * This method is used to fetch the first 200 newly created jobs from the Job table
	 * sorted by the createdTime in ascending order.
	 * @param databaseName the database name which is having the Job table.
	 * @return the {@link List} of {@link Job} entries which are new.
	 * @throws ReportingDbDAOException if any exception occurs when fetching new jobs.
	 */
	List<Job> getNewJobsSortedbyCreatedTime(String databaseName)  throws ReportingDbDAOException;
	
	/**
	 * This method is used to fetch first 200 the failed jobs from the Job table
	 * sorted by the endTime in ascending order.
	 * @param connection connection used to perform the database operations.
	 * @param database the database name which is having the Job table.
	 * @return the {@link List} of {@link Job} entries which are failed.
	 * @throws ReportingDbDAOException if any exception occurs when fetching failed jobs.
	 */
	List<Job> getAllFailedJobs(Connection connection ,String database) throws ReportingDbDAOException;
	
	/**
	 * This method is used to fetch the first 200 newly created jobs from the Job table
	 * sorted by the createdTime in ascending order.
	 * @param connection connection used to perform the database operations.
	 * @param databaseName the database name which is having the Job table.
	 * @return the {@link List} of {@link Job} entries which are failed.
	 * @throws ReportingDbDAOException if any exception occurs when fetching new jobs.
	 */
	List<Job> getNewJobsSortedbyCreatedTime(Connection connection, String databaseName)  throws ReportingDbDAOException;
	
	/**
	 * This method is used to delete the job entry for the given job id.
	 * @param databaseName the database name which is having the Job table.
	 * @param id the job id whose details are to be deleted.
	 * @throws ReportingDbDAOException if any exception occurs when fetching new jobs.
	 */
	void deleteJobById(String databaseName,String id) throws ReportingDbDAOException;

	/**
	 * This method is used to insert the list of jobs as a batch operation to the database.
	 * @param databaseName the database name which is having the Job table.
	 * @param jobList the list of jobs which are to be created.
	 * @throws ReportingDbDAOException if any exception occurs when creating new jobs.
	 */
	void insertJobList(String databaseName, List<Job> jobList) throws ReportingDbDAOException;
}
