package com.pwc.grading.reportingdb.processor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.processor.exception.OperationFailedException;
import com.pwc.grading.reportingdb.service.IFormGradingService;
import com.pwc.grading.reportingdb.service.IParameterGradingService;
import com.pwc.grading.reportingdb.service.impl.FormGradingServiceImpl;
import com.pwc.grading.reportingdb.service.impl.ParameterGradingServiceImpl;

/**
 * A processor which is used to delete the grading forms in the reporting tables.
 *
 */
public class ReportDBDeleteGradingProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportDBDeleteGradingProcessor.class);

//	@Inject
	private IFormGradingService formGradingService;

//	@Inject
	private IParameterGradingService parameterGradingService;

	public ReportDBDeleteGradingProcessor() {
		formGradingService = new FormGradingServiceImpl();
		parameterGradingService = new ParameterGradingServiceImpl();
	}

	/**
	 * This method is used to delete the grading forms from the reporting tables.
	 * The working is,
	 *  <p>(1) First it will fetch all the FormUUIDs for the given projectId and partnerId in the JSON </p>
	 *  <p>(2) After it fetches all the formUUIDs, then it will delete all the ParameterGrading entries for all the formUUIDs </p>
	 *  <p>(3) After deleting all the parameters data for the forms, it will delete the forms present in FormGradingTable table.</p>
	 *  
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json The JSON from the Job Object.
	 * @throws OperationFailedException if this operation fails.
	 */
	public void deleteGradingDataFromReportDB(String databaseName, String json) throws OperationFailedException {
		LOGGER.debug(".inside deleteGradingDataFromReportDB method of ReportDBDeleteGradingProcessor class.");
		Connection connection = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			List<String> formUUIDList = formGradingService.getFormUUIDListForPartnerProjectId(connection, databaseName,
					json);
			LOGGER.debug("DG>>>> Successfully fetched formUUIDList from FORMGRADINGTABLE.");
			parameterGradingService.deleteParameterGradingData(connection, databaseName, formUUIDList);
			LOGGER.debug("DG>>>> Successfully deleted entries from PARAMETERGRADINGTABLE.");
			formGradingService.deleteByFormUUIDList(connection, databaseName, formUUIDList);
			LOGGER.debug("DG>>>> Successfully deleted entries from FORMGRADINGTABLE.");
			connection.commit();
		} catch (Exception e) {
			LOGGER.error("Exception occurred when performing DELETE_GRADING action", e);
			try {
				connection.rollback();
			} catch (SQLException e1) {

			}
			throw new OperationFailedException("DELETE_GRADING Operation is failed because, " + e.getMessage(), e);
		} finally {
			LOGGER.debug("Closing the connection established for DELETE_GRADING action.");
			ReportDBMSSqlServerUtill.close(null, connection);
		}
	}
}
