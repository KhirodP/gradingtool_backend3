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
import com.pwc.grading.reportingdb.util.FormsUtil;

/**
 * Rating Form - Field Auditor Submit
 * This processor is used to update the assessment done by the Field Auditor for 
 * a Rating Form.
 *
 */
public class ReportDBUpdateRatingFASubmitProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportDBUpdateRatingFASubmitProcessor.class);
	
//	@Inject
	private IPartnersReportingService partnersReportingService;
	
//	@Inject
	private IFormRatingService formRatingService;
	
//	@Inject
	private IParameterRatingService parameterRatingService;
	
	public ReportDBUpdateRatingFASubmitProcessor() {
		partnersReportingService = new PartnersReportingServiceImpl();
		formRatingService = new FormRatingServiceImpl();
		parameterRatingService = new ParameterRatingServiceImpl();
	}
	
	/**
	 * This method is used to update the self assessment done by the Field-Auditor
	 * The working is,
	 * <p>(1) It will update the PartnersReporting Data for the projectId and partnerId present in the JSON. </p>
	 * <p>(2) It will update the scores and other Field-Auditor details in the FormRating table. </p>
	 * <p>(3) It will update the FA scores for each parameter of this form in the ParameterRating table. </p>
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json The JSON from the Job Object.
	 * @throws OperationFailedException if this operation fails.
	 */
	public void updateRatingFASubmitIntoReportingDatabase(String databaseName, String json) throws OperationFailedException {
		LOGGER.debug(".inside updateRatingFASubmitIntoReportingDatabase method of ReportDBUpdateRatingFASubmitProcessor class.");
		Connection connection = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			if (FormsUtil.isRatingFormJSON(json)) {
				LOGGER.debug("Given JSON is a RATING Form");
				String partnerProjectId = partnersReportingService.updatePartnersReportingData(connection,databaseName, json);
				LOGGER.debug(" >>>>> Successfully UPDATED data into PARTNERSREPORTINGTABLE, PartnerProjectId: "+StringEscapeUtils.escapeJava(partnerProjectId));
				String formUUID = formRatingService.updateFormRatingDataFieldAuditorSubmit(connection,databaseName, json, partnerProjectId);
				LOGGER.debug(" >>>>> Successfully UPDATED FA Submit data into FORMRATINGTABLE, Form UUID: "+StringEscapeUtils.escapeJava(formUUID));
				parameterRatingService.updateParameterRatingDataFA(connection,databaseName, json, formUUID);
				LOGGER.debug(" >>>>> Successfully UPDATED FA Submit data into PARAMETERRATINGTABLE.");
				connection.commit();
				LOGGER.info("Update Rating Field-Auditor's Submit to ReportDB is done.");
			}else {
				LOGGER.error("Given JSON is not a Rating form.");
			}
		}catch (Exception e) {
			LOGGER.error("Exception occurred when performing "+JobConstant.UPDATE_RATING_FA_SUBMIT+" action",e);
			try {
				connection.rollback();
			} catch (SQLException e1) {

			}
			throw new OperationFailedException(JobConstant.UPDATE_RATING_FA_SUBMIT+" Operation is failed because, "+e.getMessage(),e);
			
		}finally {
			LOGGER.debug("Closing the connection established for "+JobConstant.UPDATE_RATING_FA_SUBMIT+" action.");
			ReportDBMSSqlServerUtill.close(null, connection);
		}
	}
}
