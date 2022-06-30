package com.pwc.grading.reportingdb.dao;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.model.ParameterGradingTable;

/**
 * An interface class which is used to perform all
 * Parameter Grading related database operations.
 *
 */
public interface IParameterGradingDAO {

	/**
	 * This method is used to add the Parameters List into the ParameterGradingTable table.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the ParameterGradingTable table.
	 * @param parameterGradingList the list of grading entries to be inserted.
	 * @throws ReportingDbDAOException if any exception occurs when creating the entry.
	 */
	void addParameterGradingDataList(Connection connection, String databaseName,
			List<ParameterGradingTable> parameterGradingList) throws ReportingDbDAOException;

	/**
	 * This method is used to add the parameters of a grading form into the ParameterGradingTable
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the ParameterGradingTable table.
	 * @param parameterGradingObj the object having the single parameter data.
	 * @throws ReportingDbDAOException if any exception occurs when creating the entry.
	 */
	void addParameterGradingData(Connection connection, String databaseName, ParameterGradingTable parameterGradingObj)
			throws ReportingDbDAOException;
	
	/**
	 * This method is used to update the parameters of a grading form into the ParameterGradingTable
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the ParameterGradingTable table.
	 * @param parameterGradingObj the object having the single parameter data to be updated.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	void updateParameterGradingData(Connection connection, String databaseName,
			ParameterGradingTable parameterGradingObj) throws ReportingDbDAOException;

	/**
	 * This method is used to get a single parameter data of a grading form.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the ParameterGradingTable table.
	 * @param formId the formUUID of the grading form.
	 * @param sectionId the sectionId of the section.
	 * @param parameterId the parameterId of the parameter.
	 * @return a single parameter data.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the entry.
	 */
	ParameterGradingTable getPGDataByFormSectionParameterIds(Connection connection, String databaseName, String formId,
			String sectionId, String parameterId) throws ReportingDbDAOException;

	/**
	 * This method is used to delete all the parameters for all given formUUIDs.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the ParameterGradingTable table.
	 * @param formUUIDList the list having all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when deleting the entries.
	 */
	void deleteParameterGradingData(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbDAOException;

	/**
	 * This method is used to update all the parameters for all given formUUIDs.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the ParameterGradingTable table.
	 * @param formUUIDList the list having all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entries.
	 */
	void updateParameterGradingDataByFormUUId(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbDAOException;
}
