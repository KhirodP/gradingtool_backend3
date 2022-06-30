package com.pwc.grading;

import com.pwc.grading.reportingdb.executorservice.ExecutorServiceConfig;
import com.pwc.grading.reportingdb.scheduler.SchedulerConfig;

import io.micronaut.runtime.Micronaut;

/**
 * This class is the starting point of the application.
 * Running this class main method, will start the application.
 * <br>The number of tenants configured in <b>Tenant.properties</b>, that many databases will create when starting the application.</br>
 * <br>Each tenant will have a default ADMIN user which can be configured in <b>TenantAdminUsers.properties</b></br>
 *
 */  	
public class GradingToolApplication {

	/**   
	 * Starting Point of the application.
	 * @param args arguments of main method
	 * @throws GradingToolApplicationException if the application is unable to start due to some issues.
	 */
	public static void main(String[] args) throws GradingToolApplicationException {
		Micronaut.run(GradingToolApplication.class, args);
		GradingToolTenant.loadApplicationTenants();
		ExecutorServiceConfig.initializeExecutorServiceForAllTenants();
		SchedulerConfig.initializeCronScheduler();
	}
}
