package com.pwc.grading.reportingdb.processor;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.JobConstant;
import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.processor.exception.OperationFailedException;
import com.pwc.grading.reportingdb.service.IPartnersReportingService;
import com.pwc.grading.reportingdb.service.impl.PartnersReportingServiceImpl;

/**
 * ClientSponsor Name and Phone updation Processor.
 * This processor is used to update the ClientSponsor details for a Grading Form.
 *
 */
public class ReportingDBUpdateCSProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingDBUpdateCSProcessor.class);
	
//	@Inject
	private IPartnersReportingService partnersReportingService;
	
	public ReportingDBUpdateCSProcessor() {
		partnersReportingService = new PartnersReportingServiceImpl();
	}
	
	/**
	 * This method is used to update the ClientSponsor details (given in the JSON)
	 * into the PartnersReporting Table.
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json The JSON from the Job Object.
	 * @throws OperationFailedException if this operation fails.
	 */
	public void updateClientSponsorDataIntoReportingDatabase(String databaseName, String json) throws OperationFailedException {
		LOGGER.debug(".inside updateClientSponsorDataIntoReportingDatabase method of ReportingDBUpdateCSProcessor class.");
		Connection connection = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);			
			partnersReportingService.updateCSByProjectIdAndPartnerId(connection, databaseName, json);
			LOGGER.debug(" >>>>> Successfully Updated Client-Sponsor data into PARTNERSREPORTINGTABLE.");
			connection.commit();
			LOGGER.info("Update Client-Sponsor data to ReportDB is done.");
		}catch (Exception e) {
			LOGGER.error("Exception occurred when performing "+JobConstant.UPDATE_CS+" action",e);
			try {
				connection.rollback();
			} catch (SQLException e1) {

			}
			throw new OperationFailedException(JobConstant.UPDATE_CS+" Operation is failed because, "+e.getMessage(),e);
			
		}finally {
			LOGGER.debug("Closing the connection established for "+JobConstant.UPDATE_CS+" action.");
			ReportDBMSSqlServerUtill.close(null, connection);
		}
	}
}
