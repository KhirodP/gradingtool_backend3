package com.pwc.grading.reportingdb.dao;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.model.FormGradingTable;
import com.pwc.grading.reportingdb.model.ReportDBFieldAuditor;

/**
 * An interface class which is used to perform all
 * Form Grading related database operations.
 *
 */
public interface IFormGradingDAO {

	/**
	 * This method is used to add the grading form data into FormGradingTable.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param formGradingObj object containing the details of the grading form.
	 * @return the formUUID of this entry.
	 * @throws ReportingDbDAOException if any exception occurs when creating the entry.
	 */
	String addFormGradingData(Connection connection, String databaseName, FormGradingTable formGradingObj) throws ReportingDbDAOException;
	
	/**
	 * This method is used to update the grading form data into FormGradingTable.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param id the formUUID.
	 * @param formGradingObj object containing the details of the grading form.
	 * @return  the formUUID of this entry.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	String updateFormGradingData(Connection connection,String databaseName, String id, FormGradingTable formGradingObj) throws ReportingDbDAOException;
	
	/**
	 * This method is used to get the grading form using formId and partnerProjectId.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param formId the id of the form.
	 * @param partnerProjectId the partnerProjectId.
	 * @return a {@link FormGradingTable} object which is having the details of the grading form.
	 * @throws ReportingDbDAOException  if any exception occurs when fetching the entry.
	 */
	FormGradingTable getFormByFormIdAndPartnerProjectId(Connection connection,String databaseName, String formId,String partnerProjectId) throws ReportingDbDAOException;
	
	/**
	 * This method is used to delete the grading forms for the given partnerprojectId list.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param partnerProjectIdList a list of partner project Ids.
	 * @throws ReportingDbDAOException  if any exception occurs when deleting the grading forms.
	 */
	void deleteFormGradingDataForPartnerProjectIdList(Connection connection,String databaseName,List<String> partnerProjectIdList) throws ReportingDbDAOException;
	
	/**
	 * This method is used to delete the grading forms for the given Form UUID list.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param formUUIDList a list of Form UUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when deleting the grading forms. 
	 */
	void deleteFormGradingDataForFormUUIDList(Connection connection,String databaseName,List<String> formUUIDList) throws ReportingDbDAOException;
	
	/**
	 * This method is used to get all the formUUID for the given partner project Ids.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param partnerProjectIdList a list of partner project Ids.
	 * @return a list which is containing all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the data.
	 */
	List<String> getFormUUIDForPartnerProjectIdList(Connection connection, String databaseName,	List<String> partnerProjectIdList) throws ReportingDbDAOException;

	/**
	 * This method is used to update the field auditor details.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param object details of the Field Auditor.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	void updateFADetails(Connection connection, String databaseName, ReportDBFieldAuditor object) throws ReportingDbDAOException;

	/**
	 * This method is used to update the form grading data for the given partnerproject Id.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the FormGradingTable table.
	 * @param partnerProjectIdIfExists the partner project Id.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	void updateFormGradingDataByPartnerProjectId(Connection connection, String databaseName,String partnerProjectIdIfExists) throws ReportingDbDAOException;
}
