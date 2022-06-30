package com.pwc.grading.reportingdb.processor;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.JobConstant;
import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.processor.exception.OperationFailedException;
import com.pwc.grading.reportingdb.service.IFormGradingService;
import com.pwc.grading.reportingdb.service.impl.FormGradingServiceImpl;

/**
 * Field Auditor Name and Phone updation Processor.
 * This processor is used to update the Field Auditor details for a Grading Form.
 *
 */
public class ReportDBUpdateGradingFADetailsProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportDBUpdateGradingFADetailsProcessor.class);
	
//	@Inject
	private IFormGradingService formGradingService;
	
	public ReportDBUpdateGradingFADetailsProcessor() {
		formGradingService = new FormGradingServiceImpl();
	}
	
	/**
	 * This method is used to update the Field auditor name and phone (given in the JSON)
	 * into the Form Grading Table.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json The JSON from the Job Object.
	 * @throws OperationFailedException if this operation fails.
	 */
	public void updateGradingFieldAuditorDetailsIntoReportingDatabase(String databaseName, String json) throws OperationFailedException {
		LOGGER.debug(".inside updateGradingFieldAuditorDetailsIntoReportingDatabase method of ReportDBUpdateGradingFADetailsProcessor class.");
		Connection connection = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);			
			String formUUIDFG = formGradingService.updateFAByPartnerProjectIdAndFormId(connection, databaseName, json);
			LOGGER.debug(" >>>>> Successfully Updated Field-Auditor data into FORMGRADINGTABLE, Form UUID: "+StringEscapeUtils.escapeJava(formUUIDFG));		
			connection.commit();
			LOGGER.info("Update Grading Field-Auditor's data to ReportDB is done.");
		}catch (Exception e) {
			LOGGER.error("Exception occurred when performing "+JobConstant.UPDATE_GRADING_FA_DETAILS+" action",e);
			try {
				connection.rollback();
			} catch (SQLException e1) {

			}
			throw new OperationFailedException(JobConstant.UPDATE_GRADING_FA_DETAILS+" Operation is failed because, "+e.getMessage(),e);
			
		}finally {
			LOGGER.debug("Closing the connection established for "+JobConstant.UPDATE_GRADING_FA_DETAILS+" action.");
			ReportDBMSSqlServerUtill.close(null, connection);
		}
	}
}
