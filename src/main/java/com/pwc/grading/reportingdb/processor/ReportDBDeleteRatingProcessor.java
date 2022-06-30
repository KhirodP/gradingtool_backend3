package com.pwc.grading.reportingdb.processor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.processor.exception.OperationFailedException;
import com.pwc.grading.reportingdb.service.IFormRatingService;
import com.pwc.grading.reportingdb.service.IParameterRatingService;
import com.pwc.grading.reportingdb.service.impl.FormRatingServiceImpl;
import com.pwc.grading.reportingdb.service.impl.ParameterRatingServiceImpl;

/**
 * A processor which is used to delete the rating forms in the reporting tables.
 *
 */
public class ReportDBDeleteRatingProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportDBDeleteRatingProcessor.class);

	private IFormRatingService iFormRating;
	private IParameterRatingService iParameterRating;

	public ReportDBDeleteRatingProcessor() {
		iFormRating = new FormRatingServiceImpl();
		iParameterRating = new ParameterRatingServiceImpl();
	}

	/**
	 * This method is used to delete the rating forms from the reporting tables.
	 * The working is,
	 *  <p>(1) First it will fetch all the FormUUIDs for the given projectId and partnerId in the JSON </p>
	 *  <p>(2) After it fetches all the formUUIDs, then it will delete all the ParameterRating entries for all the formUUIDs </p>
	 *  <p>(3) After deleting all the parameters data for the forms, it will delete the forms present in FormRatingTable table.</p>
	 *  
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json The JSON from the Job Object.
	 * @throws OperationFailedException if this operation fails.
	 */
	public void deleteRatingDataFromReportDB(String databaseName, String json) throws OperationFailedException {
		LOGGER.debug(".inside deleteRatingDataFromReportDB method of ReportDBDeleteRatingProcessor class.");
		Connection connection = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			List<String> formUUIDList = iFormRating.getFormUUIDListForPartnerProjectId(connection, databaseName, json);
			LOGGER.debug("DR>>>> Successfully fetched formUUIDList from FORM Rating TABLE." + StringEscapeUtils.escapeJava(formUUIDList.toString()));
			iParameterRating.deleteParameterRatingData(connection, databaseName, formUUIDList);
			LOGGER.debug("DR>>>> Successfully deleted entries from PARAMETER Rating TABLE.");
			iFormRating.deleteFormRatingDataByFormUUid(connection, databaseName, formUUIDList);
			LOGGER.debug("DR>>>> Successfully deleted entries from FORM RATING TABLE.");
			connection.commit();
		} catch (Exception e) {
			LOGGER.error("Exception occurred when performing DELETE_RATING action", e);
			try {
				connection.rollback();
			} catch (SQLException e1) {

			}
			throw new OperationFailedException("DELETE_RATING Operation is failed because, " + e.getMessage(), e);
		} finally {
			LOGGER.debug("Closing the connection established for DELETE_RATING action.");
			ReportDBMSSqlServerUtill.close(null, connection);
		}
	}
}
