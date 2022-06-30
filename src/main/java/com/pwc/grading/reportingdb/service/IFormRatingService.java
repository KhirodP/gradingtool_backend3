package com.pwc.grading.reportingdb.service;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.reportingdb.service.exp.ReportingDbServiceException;
/**
 * An interface class which is used to perform all
 * Form Rating related operations.
 *
 */
public interface IFormRatingService {

	/**
	 * This method is used to add the Rating form data if entry not exists, if the entry already exists for
	 * the partner and project, it <strong>will not add a new entry again </strong>, but <strong>it will update the 
	 * existing entry.</strong>
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the rating form details.
	 * @param partnerProjectId the partnerprojectId. 
	 * @return the formUUID belongs to the created entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	String addFormRatingData(Connection connection, String databaseName, String json, String partnerProjectId)
			throws ReportingDbServiceException;

	/**
	 * This method is used to the update the form rating data, the <strong>entry should exists</strong> to update
	 * or this method will throw {@link ReportingDbServiceException}
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the rating form details.
	 * @param partnerProjectId the partnerprojectId. 
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	String updateFormRatingData(Connection connection, String databaseName, String json, String partnerProjectId)
			throws ReportingDbServiceException;

	/**
	 * This method is used to update the field auditor response for the form which the field auditor is submitted.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the details of field-auditor response.
	 * @param partnerProjectId the partnerprojectId.
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	String updateFormRatingDataFieldAuditorSubmit(Connection connection, String databaseName, String json,
			String partnerProjectId) throws ReportingDbServiceException;

	/**
	 * This method is used to update the Center-In-Charge response for the form which the Center-In-Charge is submitted.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the details of Center-In-Charge response.
	 * @param partnerProjectId the partnerprojectId.
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	String updateFormRatingDataCenterInChargeSubmit(Connection connection, String databaseName, String json,
			String partnerProjectId) throws ReportingDbServiceException;

	/**
	 * This method is used to get all the formUUIDs for all the given partnerProjectIds.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param partnerProjectIdList all the partnerProject Ids.
	 * @return all the formUUID in the form of {@link List}
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	List<String> getFormUUIDList(Connection connection, String databaseName, List<String> partnerProjectIdList)
			throws ReportingDbServiceException;

	/**
	 * This method is used to delete the form rating data for all the partnerProjectIds.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param partnerProjectIdList  all the partnerProject Ids.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	void deleteFormRatingDataForPartnerProjectIdList(Connection connection, String databaseName,
			List<String> partnerProjectIdList) throws ReportingDbServiceException;

	/**
	 * This method is used to update the training center details 
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param trainingCenterJson the JSON containing the trainingcenter details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	void updateTCByPartnerProjectIdAndTcId(Connection connection, String databaseName, String trainingCenterJson)
			throws ReportingDbServiceException;

	/**
	 * This method is used to update the CenterIncharge details 
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param cicJson the JSON containing the CenterIncharge details.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	void updateCICByPartnerProjectIdAndTcId(Connection connection, String databaseName, String cicJson)
			throws ReportingDbServiceException;

	/**
	 * This method is used to update the Field Auditor details 
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing the Field Auditor details.
	 * @return the formUUID belongs to the updated entry.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	String updateFieldAuditorDetails(Connection connection, String databaseName, String json)
			throws ReportingDbServiceException;

	/**
	 * This method is used to get all the formUUIDs for the given partnerProjectId
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param json the JSON containing the details of the projectId and partnerId.
	 * @return all the formUUID in the form of {@link List}
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	List<String> getFormUUIDListForPartnerProjectId(Connection connection, String databaseName, String json)
			throws ReportingDbServiceException;

	/**
	 * This method is used to delete the form rating entries for all the given formUUIDs.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation
	 * @param formUUIDList all the formUUIDs which are to be deleted.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	void deleteFormRatingDataByFormUUid(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbServiceException;

}
