package com.pwc.grading.reportingdb.scheduler;

import java.util.Properties;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.ReportingDBConstants;
import com.pwc.grading.util.PropertiesUtil;

/**
 * A configuration class for the cron-job scheduler 
 * which performs some async operations for a defined period of time related 
 * to the reporting tables.
 *
 */
public class SchedulerConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerConfig.class);

	public static final String CRON_EXP_PROPERTY = "scheduler.cron.expression";
	
	public static final String QUARTZ_FILE_NAME = "quartz.properties";

	public static final String SCHEDULER_FILE = "reportdb.properties";

	/**
	 * This method is used to initialize the cron job scheduler which runs
	 * for every period of time.
	 * 
	 */
	public static void initializeCronScheduler() {
		LOGGER.debug(".inside initializeCronScheduler method of SchedulerConfig class.");
		JobDetail jobDetail = JobBuilder.newJob(SchedulerJobImpl.class).build();
		String cronExpression = getCronExpression();
		LOGGER.info("Cron Expression for Scheduling: " + cronExpression);
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("Cron-Trigger").withSchedule(scheduleBuilder)
				.build();

		// Or we can do SimpleScheduleBuilder for every 1 minute
		/*
		 * SimpleScheduleBuilder simpleSchedBuilder =
		 * SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(1).repeatForever
		 * (); Trigger trigger =
		 * TriggerBuilder.newTrigger().withSchedule(simpleSchedBuilder).build();
		 */
//		SimpleThreadPool threadPool = new SimpleThreadPool(1, Thread.NORM_PRIORITY);
		try {
			System.setProperty("org.quartz.properties" , ReportingDBConstants.REPORTING_DB_FOLDER_NAME + QUARTZ_FILE_NAME);
			Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
			
//			defaultScheduler.set
			defaultScheduler.start();
			defaultScheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOGGER.error("Cannot Start the Scheduler, " + e.getMessage(), e);
		}
	}

	private static String getCronExpression() {
		Properties propertiesFromResoures = PropertiesUtil
				.getPropertiesFromResoures(ReportingDBConstants.REPORTING_DB_FOLDER_NAME + SCHEDULER_FILE);
		String cronExp = propertiesFromResoures.getProperty(CRON_EXP_PROPERTY);
		return cronExp;
	}
}
