package com.pwc.grading.reportingdb.dao;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.model.ParameterRatingTable;
/**
 * An interface class which is used to perform all
 * Parameter Rating related database operations.
 *
 */
public interface IParameterRatingDAO {

	/**
	 * This method is used to add the Parameters List into the ParameterRatingTable table.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the ParameterRatingTable table.
	 * @param parameterRatingList the list of rating entries to be inserted.
	 * @throws ReportingDbDAOException if any exception occurs when creating the entry.
	 */
	void addParameterRatingDataList(Connection connection, String databaseName,
			List<ParameterRatingTable> parameterRatingList) throws ReportingDbDAOException;

	/**
	 * This method is used to add the parameters of a rating form into the ParameterRatingTable
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the ParameterRatingTable table.
	 * @param parameterRatingObj the object having the single parameter data.
	 * @throws ReportingDbDAOException if any exception occurs when creating the entry.
	 */
	void addParameterRatingData(Connection connection, String databaseName, ParameterRatingTable parameterRatingObj)
			throws ReportingDbDAOException;

	/**
	 * This method is used to update the parameters of a rating form into the ParameterRatingTable
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the ParameterRatingTable table.
	 * @param parameterRatingObj the object having the single parameter data to be updated.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	void updateParameterRatingData(Connection connection, String databaseName, ParameterRatingTable parameterRatingObj)
			throws ReportingDbDAOException;

	/**
	 * This method is used to get a single parameter data of a rating form.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the ParameterRatingTable table.
	 * @param formId the formUUID of the rating form.
	 * @param sectionId the sectionId of the section.
	 * @param parameterId the parameterId of the parameter.
	 * @return a single parameter data.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the entry.
	 */
	ParameterRatingTable getPRDataByFormSectionParameterIds(Connection connection, String databaseName, String formId,
			String sectionId, String parameterId) throws ReportingDbDAOException;

	/**
	 * This method is used to delete all the parameters for all given formUUIDs.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the ParameterRatingTable table.
	 * @param formUUIDList the list having all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when deleting the entries.
	 */
	void deleteParameterRatingData(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbDAOException;

	/**
	 * This method is used to update all the parameters for all given formUUIDs.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the ParameterRatingTable table.
	 * @param formUUIDList the list having all the formUUIDs.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entries.
	 */
	void updateParameterRatingDataByFormUUid(Connection connection, String databaseName, List<String> formUUIDList)
			throws ReportingDbDAOException;

}
