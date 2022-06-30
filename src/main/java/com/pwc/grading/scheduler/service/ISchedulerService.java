package com.pwc.grading.scheduler.service;

import com.pwc.grading.scheduler.service.exception.SchedulerServiceException;
/**
 * An interface class which is used to perform all
 * Scheduler related operations.
 *
 */
public interface ISchedulerService {
	/**
	 * This method is used to update the scheduler.
	 * @param tenantId the database name.
	 * @param requestBody the json response which has the scheduler data.
	 * @param schedulerType it can be either ratingForm or gradingForm
	 * @return the response message of this method.
	 * @throws SchedulerServiceException if any exception occurs when performing this operation.
	 */
	public String updateScheduler(String tenantId, String requestBody, String schedulerType)
			throws SchedulerServiceException;

	/**
	 * This method is used to get the scheduler.
	 * @param tenantId the database name.
	 * @param schedulerType it can be either ratingForm or gradingForm
	 * @return  the response message of this method.
	 * @throws SchedulerServiceException if any exception occurs when performing this operation.
	 */
	public String getScheduler(String tenantId, String schedulerType) throws SchedulerServiceException;

	/**
	 *  This method is used to filter the scheduler.
	 * @param tenantId  the database name.
	 * @param projectName the name of the project
	 * @param partnerName the name of the partner
	 * @param schedulerType it can be either ratingForm or gradingForm
	 * @return  the response message of this method.
	 * @throws SchedulerServiceException if any exception occurs when performing this operation.
	 */
	public String filterSchedulers(String tenantId, String projectName, String partnerName, String schedulerType)
			throws SchedulerServiceException;
}
