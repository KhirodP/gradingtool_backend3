package com.pwc.grading.reportingdb.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.ReportingDBTableConstant;
import com.pwc.grading.reportingdb.dao.IJobDAO;
import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.db.constant.ReportingDatabaseQueryConstants;
import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.model.Job;

/**
 * Implementation class for {@link IJobDAO} 
 *
 */
//@Singleton
public class JobDAOImpl implements IJobDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobDAOImpl.class);

	/**
	 * This method is used to create the job entry in the Job table.
	 * @param databaseName the database name which is having the Job table.
	 * @param job the job details which is to be created.
	 * @throws ReportingDbDAOException if any exception occurs when creating the job.
	 */
	@Override
	public void createJob(String databaseName, Job job) throws ReportingDbDAOException {
//		LOGGER.debug(".inside createJob method of JobDAOImpl");
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			String insertQuery = ReportingDatabaseQueryConstants.INSERT_JOB
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			// LOGGER.debug("INSERT QUERY : "+insertQuery);
			prepareStatement.setString(1, job.getJobId());
			prepareStatement.setString(2, job.getOperationType());
			prepareStatement.setString(3, job.getJsonObj());
			prepareStatement.setString(4, job.getCreatedTime());
			prepareStatement.setString(5, job.getStartTime());
			prepareStatement.setString(6, job.getEndTime());
			prepareStatement.setString(7, job.getStatus());
			prepareStatement.setString(8, job.getFailureMessage());
			prepareStatement.setInt(9, job.getRetryCount());
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " Job created successfully");
		} catch (Exception e) {
			LOGGER.error("Unable to create Job into database" + e.getMessage());
			throw new ReportingDbDAOException("Unable to create Job into database" + e.getMessage());
		} finally {
			LOGGER.debug("Closing the Job prepareStatement, connection");
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->createJob, Before Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			ReportDBMSSqlServerUtill.close(prepareStatement, connection);
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->createJob, After Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	/**
	 * This method is used to update the existing job entry in the Job table.
	 * @param databaseName the database name which is having the Job table.
	 * @param id the job id whose details are to be updated.
	 * @param job the job details which is to be created.
	 * @throws ReportingDbDAOException if any exception occurs when updating the job.
	 */
	@Override
	public void updateJob(String databaseName, String id, Job job) throws ReportingDbDAOException {
		LOGGER.debug(".inside updateJob method of JobDAOImpl");
//		LOGGER.debug("Job-Id is : "+id);
//		LOGGER.debug("Job is : "+job);

		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			String insertQuery = ReportingDatabaseQueryConstants.UPDATE_JOB
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			LOGGER.debug("Job Update Query :" + insertQuery);
			prepareStatement = connection.prepareStatement(insertQuery);
			prepareStatement.setString(1, job.getStartTime());
			prepareStatement.setString(2, job.getEndTime());
			prepareStatement.setString(3, job.getStatus());
			prepareStatement.setString(4, job.getFailureMessage());
			prepareStatement.setInt(5, job.getRetryCount());
			prepareStatement.setString(6, job.getJobId());
			int executeUpdate = prepareStatement.executeUpdate();
			LOGGER.debug(executeUpdate + " Job updated successfully");
		} catch (Exception e) {
			LOGGER.error("Unable to update Job into database." + e.getMessage());
			throw new ReportingDbDAOException("Unable to update Job into database." + e.getMessage());
		} finally {
//			LOGGER.debug("Closing the Job prepareStatement, connection");
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->updateJob, Before Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			ReportDBMSSqlServerUtill.close(prepareStatement, connection);
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->updateJob, After Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}

	}

	/**
	 * This method is used to get all the job entries in the Job table.
	 * @param databaseName the database name which is having the Job table.
	 * @throws ReportingDbDAOException if any exception occurs when getting all the jobs.
	 */
	@Override
	public List<Job> getAll(String databaseName) throws ReportingDbDAOException {
		LOGGER.debug(".inside getAll method of JobDAOImpl");
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet rs = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_ALL_JOB
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			LOGGER.debug("SELECT QUERY: " + selectQuery);
			prepareStatement = connection.prepareStatement(selectQuery);
			rs = prepareStatement.executeQuery();
			List<Job> objList = getJobListFromResultSet(rs);
			return objList;
		} catch (Exception e) {
			LOGGER.error("Unable to get all the jobs from database, " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get all the jobs from database, " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the Job rs, prepareStatement,connection");
			ReportDBMSSqlServerUtill.close(rs, prepareStatement, connection);
		}
	}

	/**
	 * This method is used to get the job entry for the given JobId
	 * @param databaseName the database name which is having the Job table.
	 * @param id the job id whose details are to be fetched.
	 * @throws ReportingDbDAOException if any exception occurs when updating the job.
	 */
	@Override
	public Job getJobById(String databaseName, String id) throws ReportingDbDAOException {
		LOGGER.debug(".inside getJobById method of JobDAOImpl");
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet rs = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_JOB_BY_ID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(selectQuery);
//			LOGGER.debug("SELECT QUERY FOR ID : " + selectQuery);
			prepareStatement.setString(1, id);
			rs = prepareStatement.executeQuery();
			Job job = getJobObjectFromResultSet(rs);
			return job;
		} catch (Exception e) {
			LOGGER.error("Exception occured when getting job by id: " + e.getMessage(), e);
			throw new ReportingDbDAOException("Unable to get job By id, " + e.getMessage(), e);
		} finally {
			LOGGER.debug("Closing the Job prepareStatement, connection");
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->getJobById, Before Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			ReportDBMSSqlServerUtill.close(rs,prepareStatement, connection);
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->getJobById, After Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	/**
	 * This method is used to delete the job entry for the given job id.
	 * @param databaseName the database name which is having the Job table.
	 * @param id the job id whose details are to be deleted.
	 * @throws ReportingDbDAOException if any exception occurs when fetching new jobs.
	 */
	@Override
	public void deleteJobById(String databaseName, String id) throws ReportingDbDAOException {
		LOGGER.debug(".inside deleteJobById method of JobDAOImpl");
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			String deleteQuery = ReportingDatabaseQueryConstants.DELETE_JOB_BY_ID
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
//			LOGGER.debug("DELETE QUERY: " + deleteQuery);
			prepareStatement = connection.prepareStatement(deleteQuery);
			prepareStatement.setString(1, id);
			prepareStatement.executeUpdate();
		} catch (Exception e) {
			LOGGER.error("Unable to delete the job with id '" + id + "' : " + e);
			throw new ReportingDbDAOException("Unable to delete the job with id '" + id + "' : " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the Job prepareStatement, connection");
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->deleteJobById, Before Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			ReportDBMSSqlServerUtill.close(prepareStatement, connection);
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->deleteJobById, After Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	/**
	 * This method is used to fetch first 200 the failed jobs from the Job table
	 * sorted by the endTime in ascending order.
	 * The jobs which are failed first, will come first.
	 * @param database the database name which is having the Job table.
	 * @return the {@link List} of {@link Job} entries which are failed.
	 * @throws ReportingDbDAOException if any exception occurs when fetching failed jobs.
	 */
	@Override
	public List<Job> getAllFailedJobs(String database) throws ReportingDbDAOException {
		LOGGER.debug(".inside getAllFailedJobs method of JobDAOImpl");
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet rs = null;
		LOGGER.debug("Getting the try block");
		try {
			LOGGER.debug("Inside the try block and getting connection.");
			connection = ReportDBMSSqlServerUtill.getConnection();
			LOGGER.debug("Got the connection...");
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_JOBS_FAILED
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, database);
			LOGGER.debug("SELECT QUERY: " + selectQuery);
			prepareStatement = connection.prepareStatement(selectQuery);
			LOGGER.debug("Executing query.....");
			rs = prepareStatement.executeQuery();
			List<Job> objList = getJobListFromResultSet(rs);
			LOGGER.debug("Got FAILED Jobs List Size: "+objList.size());
			return objList;
			
		} catch (Exception e) {
			LOGGER.error("Unable to get all the Failed/RetryFailed Jobs, " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get all the Failed/RetryFailed Jobs, " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the Job rs, prepareStatement, connection");
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->getAllFailedJobs, Before Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			ReportDBMSSqlServerUtill.close(rs, prepareStatement, connection);
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->getAllFailedJobs, After Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	private List<Job> getJobListFromResultSet(ResultSet rs) throws SQLException {
		List<Job> jobList = new ArrayList<Job>();
		while (rs.next()) {
			Job job = new Job();
			job.setJobId(rs.getString(ReportingDBTableConstant.JOB_ID));
			job.setOperationType(rs.getString(ReportingDBTableConstant.JOB_OPERATION_TYPE));
			job.setJsonObj(rs.getString(ReportingDBTableConstant.JOB_JSON_OBJ));
			job.setStartTime(rs.getString(ReportingDBTableConstant.JOB_START_TIME));
			job.setEndTime(rs.getString(ReportingDBTableConstant.JOB_END_TIME));
			job.setStatus(rs.getString(ReportingDBTableConstant.JOB_STATUS));
			job.setFailureMessage(rs.getString(ReportingDBTableConstant.JOB_FAILURE_MSG));
			job.setRetryCount(rs.getInt(ReportingDBTableConstant.JOB_RETRY_COUNT));
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobObjectFromResultSet(ResultSet rs) throws SQLException {
		if (rs != null) {
			Job job = new Job();
			while (rs.next()) {
				job.setJobId(rs.getString(ReportingDBTableConstant.JOB_ID));
				job.setOperationType(rs.getString(ReportingDBTableConstant.JOB_OPERATION_TYPE));
				job.setJsonObj(rs.getString(ReportingDBTableConstant.JOB_JSON_OBJ));
				job.setStartTime(rs.getString(ReportingDBTableConstant.JOB_START_TIME));
				job.setEndTime(rs.getString(ReportingDBTableConstant.JOB_END_TIME));
				job.setStatus(rs.getString(ReportingDBTableConstant.JOB_STATUS));
				job.setFailureMessage(rs.getString(ReportingDBTableConstant.JOB_FAILURE_MSG));
				job.setRetryCount(rs.getInt(ReportingDBTableConstant.JOB_RETRY_COUNT));
			}
			return job;
		}
		return null;
	}

	/**
	 * This method is used to fetch the first 200 newly created jobs from the Job table
	 * sorted by the createdTime in ascending order.
	 * @param databaseName the database name which is having the Job table.
	 * @return the {@link List} of {@link Job} entries which are new.
	 * @throws ReportingDbDAOException if any exception occurs when fetching new jobs.
	 */
	@Override
	public List<Job> getNewJobsSortedbyCreatedTime(String databaseName) throws ReportingDbDAOException {
		LOGGER.debug(".inside getAllFailedJobs method of JobDAOImpl");
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet rs = null;
		LOGGER.debug("Getting the try block");
		try {
			LOGGER.debug("Inside the try block and getting connection.");
			connection = ReportDBMSSqlServerUtill.getConnection();
			LOGGER.debug("Got the connection...");
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_JOBS_NEW
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			LOGGER.debug("SELECT QUERY: " + selectQuery);
			prepareStatement = connection.prepareStatement(selectQuery);
			LOGGER.debug("Executing query.....");
			rs = prepareStatement.executeQuery();
			List<Job> objList = getJobListFromResultSet(rs);
			LOGGER.debug("Got NEW Jobs List Size: "+objList.size());
			return objList;
		} catch (Exception e) {
			LOGGER.error("Unable to get all the Failed/RetryFailed Jobs, " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get all the Failed/RetryFailed Jobs, " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the Job rs, prepareStatement, connection");
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->getNewJobsSortedbyCreatedTime, Before Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			ReportDBMSSqlServerUtill.close(rs, prepareStatement, connection);
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->getNewJobsSortedbyCreatedTime, After Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	/**
	 * This method is used to insert the list of jobs as a batch operation to the database.
	 * @param databaseName the database name which is having the Job table.
	 * @param jobList the list of jobs which are to be created.
	 * @throws ReportingDbDAOException if any exception occurs when creating new jobs.
	 */
	@Override
	public void insertJobList(String databaseName, List<Job> allAddGradingJobs) throws ReportingDbDAOException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			String insertQuery = ReportingDatabaseQueryConstants.INSERT_JOB
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
			prepareStatement = connection.prepareStatement(insertQuery);
			
			connection.setAutoCommit(false);
//			int i=0;
			for(Job job : allAddGradingJobs) {
				prepareStatement.setString(1, job.getJobId());
				prepareStatement.setString(2, job.getOperationType());
				prepareStatement.setString(3, job.getJsonObj());
				prepareStatement.setString(4, job.getCreatedTime());
				prepareStatement.setString(5, job.getStartTime());
				prepareStatement.setString(6, job.getEndTime());
				prepareStatement.setString(7, job.getStatus());
				prepareStatement.setString(8, job.getFailureMessage());
				prepareStatement.setInt(9, job.getRetryCount());
				prepareStatement.addBatch();
//				i++;
			}
			prepareStatement.executeBatch();
			connection.commit();
			LOGGER.debug("Committed connection..");
			LOGGER.debug(allAddGradingJobs.size() + " Jobs inserted successfully in batch.");
		} catch (Exception e) {
			LOGGER.error("Unable to insert JobList into database" + e.getMessage());
			throw new ReportingDbDAOException("Unable to insert JobList into database" + e.getMessage());
		} finally {
			LOGGER.debug("Closing the Job prepareStatement, connection");
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->insertJobList, Before Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			ReportDBMSSqlServerUtill.close(prepareStatement, connection);
//			try {
//				LOGGER.debug("^^^ JobDAOImpl->insertJobList, After Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
	}

	/**
	 * This method is used to fetch first 200 the failed jobs from the Job table
	 * sorted by the endTime in ascending order.
	 * @param connection connection used to perform the database operations.
	 * @param database the database name which is having the Job table.
	 * @return the {@link List} of {@link Job} entries which are failed.
	 * @throws ReportingDbDAOException if any exception occurs when fetching failed jobs.
	 */
	@Override
	public List<Job> getAllFailedJobs(Connection connection, String database) throws ReportingDbDAOException {
		LOGGER.debug(".inside getAllFailedJobs(con) method of JobDAOImpl");
		PreparedStatement prepareStatement = null;
		ResultSet rs = null;
		try {
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_JOBS_FAILED
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, database);
//			LOGGER.debug("SELECT QUERY: " + selectQuery);
			prepareStatement = connection.prepareStatement(selectQuery);
//			LOGGER.debug("Executing query.....");
			rs = prepareStatement.executeQuery();
			List<Job> objList = getJobListFromResultSet(rs);
//			LOGGER.debug("Got FAILED Jobs List Size: "+objList.size());
			return objList;
			
		} catch (Exception e) {
			LOGGER.error("Unable to get all the Failed/RetryFailed Jobs, " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get all the Failed/RetryFailed Jobs, " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the Job rs, prepareStatement.");
			ReportDBMSSqlServerUtill.close(rs,prepareStatement, null);
		}
	}

	/**
	 * This method is used to fetch the first 200 newly created jobs from the Job table
	 * sorted by the createdTime in ascending order.
	 * @param connection connection used to perform the database operations.
	 * @param databaseName the database name which is having the Job table.
	 * @return the {@link List} of {@link Job} entries which are failed.
	 * @throws ReportingDbDAOException if any exception occurs when fetching new jobs.
	 */
	@Override
	public List<Job> getNewJobsSortedbyCreatedTime(Connection connection, String databaseName)
			throws ReportingDbDAOException {
		LOGGER.debug(".inside getAllFailedJobs(con) method of JobDAOImpl");
		PreparedStatement prepareStatement = null;
		ResultSet rs = null;
		try {
			String selectQuery = ReportingDatabaseQueryConstants.SELECT_JOBS_NEW
					.replace(ReportingDatabaseQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName);
//			LOGGER.debug("SELECT QUERY: " + selectQuery);
			prepareStatement = connection.prepareStatement(selectQuery);
//			LOGGER.debug("Executing query.....");
			rs = prepareStatement.executeQuery();
			List<Job> objList = getJobListFromResultSet(rs);
//			LOGGER.debug("Got NEW Jobs List Size: "+objList.size());
			return objList;
		} catch (Exception e) {
			LOGGER.error("Unable to get all the Failed/RetryFailed Jobs, " + e.getMessage());
			throw new ReportingDbDAOException("Unable to get all the Failed/RetryFailed Jobs, " + e.getMessage());
		} finally {
			LOGGER.debug("Closing the Job rs, prepareStatement ");
			ReportDBMSSqlServerUtill.close(rs,prepareStatement, null);
		}
	}

}
