package com.pwc.grading.reportingdb.service;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.reportingdb.service.exp.ReportingDbServiceException;
/**
 * An interface class which is used to perform all
 * Parameter Grading operations.
 *
 */
public interface IParameterGradingService {

	/**
	 * This method is used to add the parameters of a grading form if entry not exists.If exists it <strong>will not add a new entry again </strong>, but <strong>it will update the 
	 * existing entry.</strong>
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing all the sections and parameters.
	 * @param formUUID the FormUUID.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	void addParameterGradingData(Connection connection, String databaseName, String json, String formUUID) throws ReportingDbServiceException;
	
	/**
	 * This method is used to the update the parameter grading data, the <strong>entry should exists</strong> to update
	 * or this method will throw {@link ReportingDbServiceException}
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing all the sections and parameters.
	 * @param formUUID the FormUUID.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	void updateParameterGradingData(Connection connection,String databaseName, String json,String formUUID) throws ReportingDbServiceException;

	/**
	 * This method is used to update all the FA Scores in parameter grading data.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing all the sections and parameters.
	 * @param formUUID the FormUUID.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */ 
	void updateParameterGradingDataFA(Connection connection,String databaseName, String json,String formUUID) throws ReportingDbServiceException;
	
	/**
	 * This method is used to update all the SA Scores in parameter grading data.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json the JSON containing all the sections and parameters.
	 * @param formUUID the FormUUID.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */ 
	void updateParameterGradingDataCS(Connection connection,String databaseName, String json,String formUUID) throws ReportingDbServiceException;
	
	/**
	 * This method is used to delete the parameter grading data for all the given formUUIDs.
	 * @param connection used to perform the database operations in a single transaction.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param formUUIDList the list of formUUIDs.
	 * @throws ReportingDbServiceException if any exception occurs when processing the given JSON (or) when any error occurs while performing this operation.
	 */
	void deleteParameterGradingData(Connection connection,String databaseName, List<String> formUUIDList) throws ReportingDbServiceException;

	
}
