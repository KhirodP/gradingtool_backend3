package com.pwc.grading.reportingdb.service;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.reportingdb.service.exp.ReportingDbServiceException;
/**
 * An interface class which is used to perform all
 * Partner Reporting operations.
 *
 */
public interface IPartnersReportingService {

	/**
	 * This method is used to add the partners reporting data if entry not exists, if the entry already exists for
	 * the partner and project, it <strong>will not add a new entry again </strong>, but <strong>it will update the 
	 * existing entry.</strong>
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the details of PartnersReporting fields.
	 * @return the partnerProjectId belongs to the created project and partner.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	String addPartnersReportingData(Connection connection,String databaseName, String json) throws ReportingDbServiceException;
	
	/**
	 * This method is used to update the Partners reporting data, the <strong>entry should exists</strong> to update
	 * or this method will throw {@link ReportingDbServiceException}
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the details of PartnersReporting fields.
	 * @return the partnerProjectId belongs to the updated project and partner.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	String updatePartnersReportingData(Connection connection,String databaseName, String json) throws ReportingDbServiceException;
	
	/**
	 * This method is used to get all the partner projectIds belonging to a particular project.
	 * @param connection used to perform this operation in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the Project ID.
	 * @return all the partner project Id.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	List<String> getPartnerProjectIdList(Connection connection,String databaseName, String json) throws ReportingDbServiceException; 
	
	/**
	 * This method is used to delete all the data related to the given project.
	 * @param connection used to perform this operation in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the Project ID.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	void deletePartnersReportingByProjectId(Connection connection,String databaseName,String json) throws ReportingDbServiceException;

	/**
	 * This method is used to update the client sponsor details.
	 * <br>Note: Updating Client Sponsor <strong>reset the previous scores</strong> for the Grading forms submitted. </br>
	 * @param connection used to perform this operation in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the client sponsor details and projectId and partnerId.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	void updateCSByProjectIdAndPartnerId(Connection connection, String databaseName, String json) throws ReportingDbServiceException;

}
