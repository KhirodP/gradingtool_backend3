package com.pwc.grading;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerDBConstants;
import com.pwc.grading.db.MSSqlServerInstance;
import com.pwc.grading.util.ReadPropertiesFile;

public class GradingToolTenant {

	private static final Logger logger = LoggerFactory.getLogger(GradingToolTenant.class);

	/**
	 * This method is used to load all the tenants which are configured in <b>Tenant.properties</b>
	 * Note that, configuration for atleast one tenant is mandatory.
	 * @throws GradingToolApplicationException if no tenants are provided.
	 */
	public static void loadApplicationTenants() throws GradingToolApplicationException {
		logger.info(" Loading Application Tenants");
		try {
			List<String> tenantList = ReadPropertiesFile.getAllTenant();
			logger.debug("tenantList ::" + tenantList);
			if (tenantList.size() == 0) {
				throw new GradingToolApplicationException(
						" Please Provide Atleast one Tenant Configuration to start the Application");
			}
			for (String tenant : tenantList) {
				validateAdminDetails(tenant);
			}
			for (String tenant : tenantList) {
				logger.info("loading Tenant : " + tenant);
				String dataBaseName = ReadPropertiesFile.readTenantProperty(tenant);
				if (dataBaseName == null) {
					throw new GradingToolApplicationException(" Tenant value can't be empty");
				}
				MSSqlServerInstance.createDataBases(dataBaseName, tenant);

			}
			logger.info(" Application all tenants loaded Successfully..");
		} catch (GradingToolApplicationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(" Unable to Load Tenants " + e.getMessage(), e);
			throw new GradingToolApplicationException(" Unable to Load Tenants  " + e.getMessage(), e);
		}

	}

	/**
	 * This method is used to validate the admin details for the given tenantId.
	 * @param tenantId the tenantId whose admin details has to be fetched from <b>TenantAdminUsers.properties</b>
	 * @throws GradingToolApplicationException if default user details are not found
	 */
	private static void validateAdminDetails(String tenantId) throws GradingToolApplicationException {
		logger.debug("tenant :" + tenantId + MSSqlServerDBConstants.USER_ADMIN_EMAIL);
		String firstName = ReadPropertiesFile
				.getAdminUserForTenant(tenantId + MSSqlServerDBConstants.USER_ADMIN_FIRST_NAME);
		String lastName = ReadPropertiesFile
				.getAdminUserForTenant(tenantId + MSSqlServerDBConstants.USER_ADMIN_LAST_NAME);
		String phone = ReadPropertiesFile.getAdminUserForTenant(tenantId + MSSqlServerDBConstants.USER_ADMIN_PHONE);
		String email = ReadPropertiesFile.getAdminUserForTenant(tenantId + MSSqlServerDBConstants.USER_ADMIN_EMAIL);
		String password = ReadPropertiesFile
				.getAdminUserForTenant(tenantId + MSSqlServerDBConstants.USER_ADMIN_PWD);

		logger.debug("firstName " + firstName);
		logger.debug("lastName " + lastName);
		logger.debug("phone " + phone);
		logger.debug("email " + email);
		if (firstName == null || lastName == null || phone == null || email == null || password == null
				|| firstName.isEmpty() || password.isEmpty() || phone.isEmpty() || email.isEmpty()) {
			throw new GradingToolApplicationException("userDetails not found for tenant: " + tenantId);
		}
	}

}
