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
import com.pwc.grading.reportingdb.service.IParameterGradingService;
import com.pwc.grading.reportingdb.service.IPartnersReportingService;
import com.pwc.grading.reportingdb.service.impl.FormGradingServiceImpl;
import com.pwc.grading.reportingdb.service.impl.ParameterGradingServiceImpl;
import com.pwc.grading.reportingdb.service.impl.PartnersReportingServiceImpl;

/**
 * This processor is used to a Grading form to the reporting tables.
 *
 */
public class ReportingDBAddGradingProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingDBAddGradingProcessor.class);

//	@Inject
	private IPartnersReportingService partnersReportingService;

//	@Inject
	private IFormGradingService formGradingService;

//	@Inject
	private IParameterGradingService parameterGradingService;

	public ReportingDBAddGradingProcessor() {
		partnersReportingService = new PartnersReportingServiceImpl();
		formGradingService = new FormGradingServiceImpl();
		parameterGradingService = new ParameterGradingServiceImpl();
	}

	/**
	 * This method is used to add the grading form to the reporting tables. The
	 * working is,
	 * <p>
	 * (1) It will add the PartnersReporting Data for the projectId and partnerId
	 * present in the JSON.
	 * </p>
	 * <p>
	 * (2) It will add the details of the form in the FormGrading table.
	 * </p>
	 * <p>
	 * (3) It will add each parameter of this form in the ParameterGrading table.
	 * </p>
	 * 
	 * @param databaseName the database name indicating which database is used for
	 *                     this operation.
	 * @param json         The JSON from the Job Object.
	 * @throws OperationFailedException if this operation fails.
	 */
	public void insertGradingDataIntoReportDB(String databaseName, String json) throws OperationFailedException {
		LOGGER.debug(".inside insertGradingDataIntoReportDB method of ReportingDBAddGradingProcessor class.");
		Connection connection = null;
//		LOGGER.debug("Outside try block");
		long startTime = System.currentTimeMillis();
		try {
//			LOGGER.debug("Inside try block, Getting connection.");
			connection = ReportDBMSSqlServerUtill.getConnection();
//			LOGGER.debug("Got Connection, Opening Transaction..");
			connection.setAutoCommit(false);
//			LOGGER.debug("Setted Autocommit to false");
			String partnerProjectId = partnersReportingService.addPartnersReportingData(connection, databaseName, json);
			LOGGER.debug(" G>>>>> Successfully added data into PARTNERSREPORTINGTABLE, PartnerProjectId: "+ StringEscapeUtils.escapeJava(partnerProjectId));
			String formUUID = formGradingService.addFormGradingData(connection, databaseName, json, partnerProjectId);
			LOGGER.debug(" G>>>>> Successfully added data into FORMGRADINGTABLE, Form UUID: " + StringEscapeUtils.escapeJava(formUUID));
			parameterGradingService.addParameterGradingData(connection, databaseName, json, formUUID);
			LOGGER.debug(" G>>>>> Successfully added data into PARAMETERGRADINGTABLE.");
			connection.commit();
//			LOGGER.debug("Connection Committed..");
//			LOGGER.info("Add Grading Form to ReportDB is done.");
			long endTime = System.currentTimeMillis();
			LOGGER.debug("IT TOOK '" + (endTime - startTime) + "' ms to complete One Add Grading Operation.");
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {

			}
			LOGGER.error("Exception occurred when performing " + JobConstant.ADD_GRADING + " action", e);
			throw new OperationFailedException(
					JobConstant.ADD_GRADING + " Operation is failed because, " + e.getMessage(), e);

		} finally {
			LOGGER.debug("^^^ Closing the connection established for " + JobConstant.ADD_GRADING + " action.");
//			try {
//				LOGGER.debug("^^^ AddGrading, Before Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			ReportDBMSSqlServerUtill.close(null, connection);
//			try {
//				LOGGER.debug("^^^ AddGrading, After Connection Closed: "+connection.isClosed());
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

}
