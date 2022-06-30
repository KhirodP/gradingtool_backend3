package com.pwc.grading.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.GradingToolApplicationConstant;

/**
 * An utility class read property values from the file.
 *
 */
public class ReadPropertiesFile {

	private static final Logger logger = LoggerFactory.getLogger(ReadPropertiesFile.class);

	private static Properties requestProperties;
	private static Properties responseProperties;
	private static Properties tenantProperties;
	private static Properties tenantAdminProperties;

	static {
		requestProperties = PropertiesUtil.getPropertiesFromResoures(GradingToolApplicationConstant.REQUEST_FILE_NAME);
		responseProperties = PropertiesUtil
				.getPropertiesFromResoures(GradingToolApplicationConstant.RESPONSE_FILE_NAME);
		tenantProperties = PropertiesUtil.getPropertiesFromResoures(GradingToolApplicationConstant.TENANT_FILE_NAME);
		tenantAdminProperties = PropertiesUtil
				.getPropertiesFromResoures(GradingToolApplicationConstant.TENANT_ADMIN_USER);
	}

	/**
	 * Read the request properties in the file.
	 * @param key the key whose value has to be fetched.
	 * @return the value of the key
	 */
	public static String readRequestProperty(String key) {
		logger.debug("inside readRequestProperty :key: " + key);
		String value = requestProperties.getProperty(key);
		return value;
	}

	/**
	 * Read the response properties in the file.
	 * @param key the key whose value has to be fetched.
	 * @return  the value of the key
	 */
	public static String readResponseProperty(String key) {
		logger.debug("inside readResponseProperty :key: " + key);
		String value = responseProperties.getProperty(key);
		return value;
	}

	/**
	 * Read the tenant properties in the file.
	 * @param key the key whose value has to be fetched.
	 * @return the value of the key
	 */
	public static String readTenantProperty(String key) {
		logger.debug("inside readTenantProperty :key: " + key);
		String value = tenantProperties.getProperty(key);
		return value;
	}

	/**
	 * Get all the tenants from the properties file. 
	 * @param key the key whose value has to be fetched.
	 * @return the value of the key
	 */
	public static List<String> getAllTenant() {
		logger.debug("inside getAllTenant ");
		List<String> tenantsList = new ArrayList<String>();
		tenantProperties.keySet().forEach(tenant -> tenantsList.add((String) tenant));
		return tenantsList;
	}

	/**
	 * Get admin user for the tenant
	 * @param key the key whose value has to be fetched.
	 * @return the value of the key
	 */
	public static String getAdminUserForTenant(String key) {
		logger.debug("inside getAdminUserForTenant, key:: " + key);
		String value = tenantAdminProperties.getProperty(key);
		return value;
	}
}
