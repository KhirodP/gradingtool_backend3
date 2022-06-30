package com.pwc.grading.reportingdb.processor;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.JobConstant;
import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.processor.exception.OperationFailedException;
import com.pwc.grading.reportingdb.service.IFormRatingService;
import com.pwc.grading.reportingdb.service.impl.FormRatingServiceImpl;

/**
 * Training center details updation Processor.
 * This processor is used to update the training center details for a Rating Form.
 *
 */
public class ReportingDBUpdateTCProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingDBUpdateTCProcessor.class);
	
//	@Inject
	private IFormRatingService formRatingService;
	
	public ReportingDBUpdateTCProcessor() {
		formRatingService = new FormRatingServiceImpl();
	}
	
	/**
	 * This method is used to update the training center details (given in the JSON)
	 * into the FormRating Table.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json The JSON from the Job Object.
	 * @throws OperationFailedException if this operation fails.
	 */
	public void updateTrainingCenterDataIntoReportingDatabase(String databaseName, String json) throws OperationFailedException {
		LOGGER.debug(".inside UpdateTrainingCenterDataIntoReportingDatabase method of ReportingDBUpdateTCProcessor class.");
		Connection connection = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);			
			formRatingService.updateTCByPartnerProjectIdAndTcId(connection, databaseName, json);
			LOGGER.debug(" >>>>> Successfully Updated Training center data into FORMRATINGTABLE.");
			connection.commit();
			LOGGER.info("Update TrainingCenter to ReportDB is done.");
		}catch (Exception e) {
			LOGGER.error("Exception occurred when performing "+JobConstant.UPDATE_TC+" action",e);
			try {
				connection.rollback();
			} catch (SQLException e1) {

			}
			throw new OperationFailedException(JobConstant.UPDATE_TC+" Operation is failed because, "+e.getMessage(),e);
			
		}finally {
			LOGGER.debug("Closing the connection established for "+JobConstant.UPDATE_TC+" action.");
			ReportDBMSSqlServerUtill.close(null, connection);
		}
	}
}
