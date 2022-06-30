package com.pwc.grading.reportingdb.scheduler;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.JobConstant;
import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.executorservice.ExecutorServiceConfig;
import com.pwc.grading.reportingdb.job.ReportDatabaseCallJob;
import com.pwc.grading.reportingdb.service.IJobService;
import com.pwc.grading.reportingdb.service.exp.ReportingDbServiceException;
import com.pwc.grading.reportingdb.service.impl.JobServiceImpl;
import com.pwc.grading.util.ReadPropertiesFile;

public class SchedulerJobImpl implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerJobImpl.class);

	private IJobService jobService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.debug("================= execute method of SchedulerJobImpl class =====================");
		Connection connection = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			assignFailedJobsToExecutorService(connection);
			assignNewJobToExecutorService(connection);
		} catch (Exception e) {
			LOGGER.error("Exception caught in CronJob Scheduler: "+e);
		} finally {
			LOGGER.debug("Closing the connection opened in Cron Scheduler.");
			ReportDBMSSqlServerUtill.close(null, connection);
		}
		
		LOGGER.debug("================= End of execute method of SchedulerJobImpl class =====================");
	}

	/**
	 * This method is used to assign the failed jobs to the executor service.
	 * First it will  fetch the failed jobs for each Tenant from the JOB table using the incoming connection passed to this 
	 * method, then it gets the executorservice instance for the tenant, then it will submit those failed jobs
	 * to the executor service.
	 * @param connection connection to perform database operations.
	 */
	public void assignFailedJobsToExecutorService(Connection connection) {
		LOGGER.debug("###################### assignFailedJobsToExecutorService ######################");
		List<com.pwc.grading.reportingdb.model.Job> allFailedJobs = null;
		try {
			jobService = new JobServiceImpl();
			List<String> allTenants = ReadPropertiesFile.getAllTenant(); // Getting all tenants.
			if (allTenants != null && allTenants.size() > 0) {
				for (String tenant : allTenants) {
//					LOGGER.debug("Fetching failed jobs in Tenant ID : "+tenant);
					boolean exp = false;
					String dataBaseName = ReadPropertiesFile.readTenantProperty(tenant);
					if (dataBaseName != null) {
						try {
							allFailedJobs = jobService.getAllFailedJobs(connection,dataBaseName);
						} catch (ReportingDbServiceException e) {
							LOGGER.error("Unable to get failed jobs for tenant '"+tenant+"', "+e.getMessage());
							exp = true;
						}
						if(exp) {
							LOGGER.debug("Cannot fetch failed jobs in Tenant ID : '"+tenant+"', Moving to next tenant..");
							continue;
						}
//						LOGGER.debug("Fetched Failed Jobs List size : "+allFailedJobs.size());
						LOGGER.debug("Number of failed jobs in database '" + dataBaseName + "' as per "+ LocalDateTime.now() + " is : " + allFailedJobs.size());
						ExecutorService executorService = ExecutorServiceConfig.getExecutorService(tenant);
						ThreadPoolExecutor executor = (ThreadPoolExecutor) executorService;
						int activeCount = executor.getActiveCount();
			//			LOGGER.debug("FAILED Job BS, [Active Count]: "+activeCount);
						long taskCount = executor.getTaskCount();
			//			LOGGER.debug("FAILED Job BS, [Task Count]: "+taskCount);
//						int currentQueueSize = executor.getQueue().size();
			//			LOGGER.debug("FAILED Job BS, [Queue Size]: "+currentQueueSize);
						long completedTaskCount = executor.getCompletedTaskCount();
			//			LOGGER.debug("FAILED Job BS, [Completed Task Count]: "+completedTaskCount);
						long tasksToDo = taskCount - completedTaskCount - activeCount;
			//			LOGGER.debug("FAILED JOB, Tasks to do for '"+tenant+"' : "+tasksToDo);

						if(tasksToDo <= 100 && allFailedJobs.size() > 0) {
							for (com.pwc.grading.reportingdb.model.Job job : allFailedJobs) {
//								LOGGER.debug("Picked up the Failed Job: " + job);
								if (job.getRetryCount() <= 10) {
									job.setStatus(JobConstant.RETRY_IN_PROGRESS);
									ReportDatabaseCallJob jobExec = new ReportDatabaseCallJob(dataBaseName);
									jobExec.setJob(job);									
									executorService.submit(jobExec);
								} else {
										// do something
									LOGGER.info("Retry count exceeded for the JOB: " + job);
								}
							}
						}else {
								//LOGGER.debug("In, Failed Job Execution --> The submitted 200 tasks is not completed yet.");
						}
//						LOGGER.debug("FAILED Job AS, [Active Count]: "+activeCount);
//						LOGGER.debug("FAILED Job AS, [Task Count]: "+taskCount);
//						LOGGER.debug("FAILED Job AS, [Queue Size]: "+currentQueueSize);
//						LOGGER.debug("FAILED Job AS, [Completed Task Count]: "+completedTaskCount);
					}

				}
			}

		} catch (Exception e) {
			LOGGER.error("Failed JOB, Cannot execute the given Scheduled job: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to assign the new jobs to the executor service.
	 * First it will  fetch the new jobs for each Tenant from the JOB table using the incoming connection passed to this 
	 * method, then it gets the executorservice instance for the tenant, then it will submit those new jobs
	 * to the executor service.
	 * @param connection connection to perform database operations.
	 */
	public void assignNewJobToExecutorService(Connection connection) {
		LOGGER.debug("###################### assignNewJobToExecutorService ######################");
		List<com.pwc.grading.reportingdb.model.Job> allNewJobs = null;
		try {
			jobService = new JobServiceImpl();
			List<String> allTenants = ReadPropertiesFile.getAllTenant(); // Getting all tenants.
			if (allTenants != null && allTenants.size() > 0) {
				for (String tenant : allTenants) {
					boolean exp = false;
					String dataBaseName = ReadPropertiesFile.readTenantProperty(tenant);
					if (dataBaseName != null) {
						try {
							allNewJobs = jobService.getAllNewJobs(connection,dataBaseName);
						} catch (ReportingDbServiceException e) {
							LOGGER.error("Unable to get NEW jobs for tenant '"+tenant+"', "+e.getMessage());
							exp = true;
						}
						if(exp) {
							LOGGER.debug("Cannot fetch NEW jobs in Tenant ID : '"+tenant+"', Moving to next tenant..");
							continue;
						}						
						LOGGER.debug("New Job List Size for '"+dataBaseName+"' is : "+allNewJobs.size());
						ExecutorService executorService = ExecutorServiceConfig.getExecutorService(tenant);
						ThreadPoolExecutor executor = (ThreadPoolExecutor) executorService;
						int activeCount = executor.getActiveCount();
		//				LOGGER.debug("NEW Job BS, [Active Count]: "+activeCount);
						long taskCount = executor.getTaskCount();
		//				LOGGER.debug("NEW Job BS, [Task Count]: "+taskCount);
//						int currentQueueSize = executor.getQueue().size();
		//				LOGGER.debug("NEW Job BS, [Queue Size]: "+currentQueueSize);
						long completedTaskCount = executor.getCompletedTaskCount();
		//				LOGGER.debug("NEW Job BS, [Completed Task Count]: "+completedTaskCount);
						long tasksToDo = taskCount - completedTaskCount - activeCount;
		//				LOGGER.debug("NEW JOB, Tasks to do for '"+tenant+"' : "+tasksToDo);
						if(tasksToDo <= 100 && allNewJobs.size() > 0) {
							for (com.pwc.grading.reportingdb.model.Job job : allNewJobs) {
								ReportDatabaseCallJob jobExec = new ReportDatabaseCallJob(dataBaseName);
								jobExec.setJob(job);
								executorService.submit(jobExec);								
							}
						}else {
						//	LOGGER.debug("In, New Job Execution --> The submitted 200 tasks is not completed yet.");
						}
//						LOGGER.debug("NEW Job AS, [Active Count]: "+activeCount);
//						LOGGER.debug("NEW Job AS, [Task Count]: "+taskCount);
//						LOGGER.debug("NEW Job AS, [Queue Size]: "+currentQueueSize);
//						LOGGER.debug("NEW Job AS, [Completed Task Count]: "+completedTaskCount);
					}

				}
			}

		} catch (Exception e) {
			LOGGER.error("NEW JOB, Cannot execute the given Scheduled job: " + e.getMessage(), e);
		}
	}
}
