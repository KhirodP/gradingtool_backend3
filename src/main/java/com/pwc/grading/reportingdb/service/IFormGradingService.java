package com.pwc.grading.reportingdb.service;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.reportingdb.service.exp.ReportingDbServiceException;
/**
 * An interface class which is used to perform all
 * Form Grading related operations.
 *
 */
public interface IFormGradingService {

	/**
	 * This method is used to add the grading form data if entry not exists, if the entry already exists it <strong>will not add a new entry again </strong>, but <strong>it will update the 
	 * existing entry.</strong>
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the grading form details.
	 * @param partnerProjectId the partnerprojectId.
	 * @return the formUUID belongs to the created entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	String addFormGradingData(Connection connection, String databaseName, String json, String partnerProjectId) throws ReportingDbServiceException;
		
	/**
	 * This method is used to the update the form grading data, the <strong>entry should exists</strong> to update
	 * or this method will throw {@link ReportingDbServiceException}
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the grading form details.
	 * @param partnerProjectId  the partnerprojectId.
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	String updateFormGradingData(Connection connection,String databaseName, String json,String partnerProjectId) throws ReportingDbServiceException;
	
	/**
	 * This method is used to update the field auditor response for the form which the field auditor is submitted.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the details of field-auditor response.
	 * @param partnerProjectId the partnerprojectId.
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	String updateFormGradingDataFieldAuditorSubmit(Connection connection,String databaseName, String json,String partnerProjectId) throws ReportingDbServiceException;
	
	/**
	 * This method is used to update the client sponsor response for the form which the client sponsor is submitted.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the details of client sponsor response.
	 * @param partnerProjectId the partnerprojectId.
	 * @return  the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	String updateFormGradingDataClientSponsorSubmit(Connection connection,String databaseName, String json,String partnerProjectId) throws ReportingDbServiceException;
		
	/**
	 * This method is used to get all the formUUIDs for all the given partnerProjectIds.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param partnerProjectIdList all the partnerProject Ids.
	 * @return all the formUUID in the form of {@link List}
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	List<String> getFormUUIDList(Connection connection, String databaseName, List<String> partnerProjectIdList) throws ReportingDbServiceException;
	
	/**
	 * This method is used to delete the form grading data for all the partnerProjectIds.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param partnerProjectIdList  all the partnerProject Ids.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	void deleteFormGradingDataForPartnerProjectIdList(Connection connection,String databaseName,List<String> partnerProjectIdList) throws ReportingDbServiceException;

	/**
	 * This method is used to update the Field Auditor details for the particular form.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the Field-Auditor details.
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	String updateFAByPartnerProjectIdAndFormId(Connection connection, String databaseName, String json) throws ReportingDbServiceException;

	/**
	 * This method is used to get all the formUUIDs for the given partnerProjectId
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the details of the projectId and partnerId.
	 * @return all the formUUID in the form of {@link List}
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	List<String> getFormUUIDListForPartnerProjectId(Connection connection, String databaseName, String json)  throws ReportingDbServiceException;;

	/**
	 * This method is used to delete the form grading entries for all the given formUUIDs.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param formUUIDList all the formUUIDs which are to be deleted.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	void deleteByFormUUIDList(Connection connection,String databaseName, List<String> formUUIDList) throws ReportingDbServiceException;
}
