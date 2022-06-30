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
 * CenterIncharge Name and Phone updation Processor.
 * This processor is used to update the CenterIncharge details for a Rating Form.
 *
 */
public class ReportingDBUpdateCICProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingDBUpdateCICProcessor.class);
	
//	@Inject
	private IFormRatingService formRatingService;
	
	public ReportingDBUpdateCICProcessor() {
		formRatingService = new FormRatingServiceImpl();
	}
	
	/**
	 * This method is used to update the CenterIncharge name and phone (given in the JSON)
	 * into the Form Rating Table.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json The JSON from the Job Object.
	 * @throws OperationFailedException if this operation fails.
	 */
	public void updateCenterInchargeDataIntoReportingDatabase(String databaseName, String json) throws OperationFailedException {
		LOGGER.debug(".inside updateCenterInchargeDataIntoReportingDatabase method of ReportingDBUpdateCICProcessor class.");
		Connection connection = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);			
			formRatingService.updateCICByPartnerProjectIdAndTcId(connection, databaseName, json);
			LOGGER.debug(" >>>>> Successfully Updated CenterIncharge data into FORMRATINGTABLE.");
			connection.commit();
			LOGGER.info("Update Center-In-Charge data to ReportDB is done.");
		}catch (Exception e) {
			LOGGER.error("Exception occurred when performing "+JobConstant.UPDATE_CIC+" action",e);
			try {
				connection.rollback();
			} catch (SQLException e1) {

			}
			throw new OperationFailedException(JobConstant.UPDATE_CIC+" Operation is failed because, "+e.getMessage(),e);
			
		}finally {
			LOGGER.debug("Closing the connection established for "+JobConstant.UPDATE_CIC+" action.");
			ReportDBMSSqlServerUtill.close(null, connection);
		}
	}
}
