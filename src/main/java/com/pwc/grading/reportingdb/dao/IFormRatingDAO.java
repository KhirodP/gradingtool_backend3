package com.pwc.grading.reportingdb.dao;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.model.FormRatingTable;
import com.pwc.grading.reportingdb.model.ReportDBCenterIncharge;
import com.pwc.grading.reportingdb.model.ReportDBFieldAuditor;
import com.pwc.grading.reportingdb.model.ReportDBTrainingCenter;
/**
 * An interface class which is used to perform all
 * Form Rating related database operations.
 *
 */
public interface IFormRatingDAO {

	/**
	 * This method is used to add the rating form data into FormRatingTable.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param formRatingObj object containing the details of the rating form.
	 * @return the formUUID of this entry.
	 * @throws ReportingDbDAOException if any exception occurs when creating the entry.
	 */
	String addFormRatingData(Connection connection, String databaseName, FormRatingTable formRatingObj)
			throws ReportingDbDAOException;

	/**
	 * This method is used to update the rating form data into FormRatingTable.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param id the formUUID.
	 * @param formRatingObj object containing the details of the rating form.
	 * @return  the formUUID of this entry.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	String updateFormRatingData(Connection connection, String databaseName, String id, FormRatingTable formRatingObj)
			throws ReportingDbDAOException;

	/**
	 * This method is used to get the rating form using formId and partnerProjectId.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param formId the id of the form.
	 * @param partnerProjectId the partnerProjectId.
	 * @return a {@link FormRatingTable} object which is having the details of the rating form.
	 * @throws ReportingDbDAOException  if any exception occurs when fetching the entry.
	 */
	FormRatingTable getFormByFormIdAndPartnerProjectId(Connection connection, String databaseName, String formId,
			String partnerProjectId) throws ReportingDbDAOException;

	/**
	 * This method is used to get the rating form using formId, tcId and partnerProjectId.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param formId the id of the form.
	 * @param tcId the training center Id.
	 * @param partnerProjectId the partnerProjectId.
	 * @return a {@link FormRatingTable} object which is having the details of the rating form.
	 * @throws ReportingDbDAOException  if any exception occurs when fetching the entry.
	 */
	FormRatingTable getFormByFormIdAndTcIdAndPartnerProjectId(Connection connection, String databaseName, String formId,
			String tcId, String partnerProjectId) throws ReportingDbDAOException;

	/**
	 * This method is used to delete the rating forms for the given partnerprojectId list.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param partnerProjectIdList a list of partner project Ids.
	 * @throws ReportingDbDAOException  if any exception occurs when deleting the rating forms.
	 */
	void deleteFormRatingDataForPartnerProjectIdList(Connection connection, String databaseName,
			List<String> partnerProjectIdList) throws ReportingDbDAOException;

	/**
	 * This method is used to get all the formUUID for the given partner project Ids.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param partnerProjectIdList a list of partner project Ids.
	 * @return a list which is containing all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the data.
	 */
	List<String> getFormUUIDForPartnerProjectIdList(Connection connection, String databaseName,
			List<String> partnerProjectIdList) throws ReportingDbDAOException;

	/**
	 * This method is used to update the training center details for the rating forms.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param obj the object which is containing the training center details.
	 * @throws ReportingDbDAOException if any exception occurs when updating the data.
	 */
	void updateTCDetails(Connection connection, String databaseName, ReportDBTrainingCenter obj)
			throws ReportingDbDAOException;

	/**
	 * This method is used to update the Center-In-Charge details for the rating forms.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param obj the object which is containing the Center-In-Charge details.
	 * @throws ReportingDbDAOException if any exception occurs when updating the data.
	 */
	void updateCICDetails(Connection connection, String databaseName, ReportDBCenterIncharge obj)
			throws ReportingDbDAOException;

	/**
	 * This method is used to update the Field Auditor details for the rating forms.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param obj the object which is containing the Field Auditor details.
	 * @throws ReportingDbDAOException if any exception occurs when updating the data.
	 */
	void updateFADetails(Connection connection, String databaseName, String formUUID, ReportDBFieldAuditor object)
			throws ReportingDbDAOException;

	/**
	 * This method is used to delete the rating form entries for all the given formUUIDs.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param formUUIDList all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when deleting the data.
	 */
	void deleteFormRatingDataByFormUUid(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbDAOException;

	/**
	 * This method is used to get all the formUUIDs for the given tcId and partnerProjectId.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormRatingTable table.
	 * @param partnerProjectId the partner projectId
	 * @param tcId the id of the training center.
	 * @return all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the data.
	 */
	List<String> getFormUUIDForPartnerProjectIdAndTcId(Connection connection, String databaseName,
			String partnerProjectId, String tcId) throws ReportingDbDAOException;
}
