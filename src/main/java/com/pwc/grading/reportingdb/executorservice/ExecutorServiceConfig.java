package com.pwc.grading.reportingdb.executorservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.ReportingDBConstants;
import com.pwc.grading.util.PropertiesUtil;
import com.pwc.grading.util.ReadPropertiesFile;

/**
 * A configuration class for the executor service which performs the
 * reporting tables related tasks asynchronously.
 * Each ExecutorService instances are created for each tenant.
 *
 */
public class ExecutorServiceConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceConfig.class);

//	private static ExecutorService executorService;

	public static final String THREAD_POOL_SIZE_PROPERTY = "executorService.thread.size";
	
	public static final String QUEUE_SIZE_PROPERTY = "executorService.queue.size";

	public static final String PROPERTY_FILE_NAME = "reportdb.properties";

	private static Map<String,ExecutorService> executorServiceMap =  new HashMap<String, ExecutorService>();
	
	public static final boolean ENABLE_EXEC_SERVICE = true;


//	public static ExecutorService initializeExecutorService() {
//		LOGGER.debug(".inside initializeExecutorService method of ExecutorServiceConfig class.");
//		int threadPoolSize = getThreadPoolSize();
//		int queueSize = getQueueSize();
//		LOGGER.info("Initializing Executor service with thread pool size : " + threadPoolSize);
//		if (executorService == null) {
//			final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(queueSize);
//			executorService = new ThreadPoolExecutor(threadPoolSize, threadPoolSize,
//			        0L, TimeUnit.MILLISECONDS,
//			        queue);
//		//	executorService = Executors.newFixedThreadPool(threadPoolSize);
//		}
//		LOGGER.info("ExecutorService Initialization Completed..Returning executorService..");
//		return executorService;
//	}

//	public static ExecutorService getExecutorService() {
//	LOGGER.debug(".inside getExecutorService method of ExecutorServiceConfig class.");
//	if (executorService != null) {
//		return executorService;
//	} else {
//		return initializeExecutorService();
//	}
//}
	/**
	 * This method is used to initialize executor Service for all the tenants.
	 *
	 */
	public static void initializeExecutorServiceForAllTenants() {
		LOGGER.debug(".inside initializeExecutorServiceForAllTenants method of ExecutorServiceConfig class.");
		int threadPoolSize = getThreadPoolSize();
		int queueSize = getQueueSize();
		
		List<String> allTenants = ReadPropertiesFile.getAllTenant();
		if(allTenants.size() == 0) {
			throw new RuntimeException("Please provide atleast one tenant to initialize ExecutorService.");
		}
		for(String tenantId : allTenants) {
			LOGGER.info("Initializing ExecutorService for '"+tenantId+"' [threadPoolSize= "+threadPoolSize+" , queueSize= "+queueSize+" ] ");
			final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(queueSize);
			ExecutorService execService = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, queue);
			executorServiceMap.put(tenantId, execService);
		}
		LOGGER.debug("Executor service Map size : "+executorServiceMap.size());
		LOGGER.info("ExecutorService Initialized for "+allTenants.size()+" Tenants.");
	}
	
	/**
	 * This method is used to get the {@link ExecutorService} instance for the given tenantId.
	 * @param tenantId tenantId of the particular tenant.
	 * @return the instance of the {@link ExecutorService}.
	 */
	public static ExecutorService getExecutorService(String tenantId) {
		LOGGER.debug(".inside getExecutorService method of ExecutorServiceConfig class.");
		LOGGER.debug("Tenant Id :: "+tenantId);
		if(executorServiceMap.containsKey(tenantId)) {
			return executorServiceMap.get(tenantId);
		}else {
			throw new RuntimeException("Executor service not found for tenantId: "+tenantId);
		}
		
	}
	
	private static int getQueueSize() {
		Properties propertiesFromResoures = PropertiesUtil
				.getPropertiesFromResoures(ReportingDBConstants.REPORTING_DB_FOLDER_NAME + PROPERTY_FILE_NAME);
		String poolSize = propertiesFromResoures.getProperty(QUEUE_SIZE_PROPERTY);
		int poolSizeInInt = Integer.parseInt(poolSize);
		return poolSizeInInt;
	}
	
	private static int getThreadPoolSize() {
		Properties propertiesFromResoures = PropertiesUtil
				.getPropertiesFromResoures(ReportingDBConstants.REPORTING_DB_FOLDER_NAME + PROPERTY_FILE_NAME);
		String poolSize = propertiesFromResoures.getProperty(THREAD_POOL_SIZE_PROPERTY);
		int poolSizeInInt = Integer.parseInt(poolSize);
		return poolSizeInInt;
	}
}
