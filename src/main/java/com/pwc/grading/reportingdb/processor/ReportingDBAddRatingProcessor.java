package com.pwc.grading.reportingdb.processor;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.constant.JobConstant;
import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.processor.exception.OperationFailedException;
import com.pwc.grading.reportingdb.service.IFormRatingService;
import com.pwc.grading.reportingdb.service.IParameterRatingService;
import com.pwc.grading.reportingdb.service.IPartnersReportingService;
import com.pwc.grading.reportingdb.service.impl.FormRatingServiceImpl;
import com.pwc.grading.reportingdb.service.impl.ParameterRatingServiceImpl;
import com.pwc.grading.reportingdb.service.impl.PartnersReportingServiceImpl;

/**
 * This processor is used to a Rating form to the reporting tables.
 *
 */
public class ReportingDBAddRatingProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingDBAddRatingProcessor.class);
	
//	@Inject
	private IPartnersReportingService partnersReportingService;
	
//	@Inject
	private IFormRatingService formRatingService;
	
//	@Inject
	private IParameterRatingService parameterRatingService;
	
	public ReportingDBAddRatingProcessor() {
		partnersReportingService = new PartnersReportingServiceImpl();
		formRatingService = new FormRatingServiceImpl();
		parameterRatingService = new ParameterRatingServiceImpl();
	}
	
	/**
	 * This method is used to add the rating form to the reporting tables.
	 * The working is,
	 * <p>(1) It will add the PartnersReporting Data for the projectId and partnerId present in the JSON. </p>
	 * <p>(2) It will add the details of the form in the FormRating table. </p>
	 * <p>(3) It will add each parameter of this form in the ParameterRating table. </p>
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json The JSON from the Job Object.
	 * @throws OperationFailedException if this operation fails.
	 */
	public void insertRatingDataIntoReportDB(String databaseName, String json) throws OperationFailedException {
		LOGGER.debug(".inside insertRatingDataIntoReportDB method of ReportingDBAddRatingProcessor class.");
		Connection connection = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			LOGGER.debug("Given JSON is a RATING Form");
			String partnerProjectId = partnersReportingService.addPartnersReportingData(connection,databaseName, json);
			LOGGER.debug(" R>>>>> Successfully added data into PARTNERSREPORTINGTABLE, PartnerProjectId: "+StringEscapeUtils.escapeJava(partnerProjectId));
			String formUUID = formRatingService.addFormRatingData(connection,databaseName, json, partnerProjectId);
			LOGGER.debug(" R>>>>> Successfully added data into FORMRATINGTABLE, Form UUID: "+StringEscapeUtils.escapeJava(formUUID));
			parameterRatingService.addParameterRatingData(connection,databaseName, json, formUUID);
			LOGGER.debug(" R>>>>> Successfully added data into PARAMETERRATINGTABLE.");
			connection.commit();
//			LOGGER.info("Add RatingForm to ReportDB is done.");
		}catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {

			}
			LOGGER.error("Exception occurred when performing "+JobConstant.ADD_RATING+" action",e);				
			throw new OperationFailedException(JobConstant.ADD_RATING+" Operation is failed because, "+e.getMessage(),e);
						
		}finally {
			LOGGER.debug("Closing the connection established for ADD action.");
			ReportDBMSSqlServerUtill.close(null, connection);
		}
	}
	
	
}
