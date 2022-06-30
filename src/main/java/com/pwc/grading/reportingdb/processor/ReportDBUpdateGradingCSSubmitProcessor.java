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
import com.pwc.grading.reportingdb.util.FormsUtil;

/**
 * Grading Form - Client Sponsor Submit Processor.
 * This processor is used to update the self assessment done by the Client Sponsor for 
 * a Grading Form.
 *
 */
public class ReportDBUpdateGradingCSSubmitProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportDBUpdateGradingCSSubmitProcessor.class);
	
//	@Inject
	private IPartnersReportingService partnersReportingService;
	
//	@Inject
	private IFormGradingService formGradingService;
	
//	@Inject
	private IParameterGradingService parameterGradingService;
	
	public ReportDBUpdateGradingCSSubmitProcessor() {
		partnersReportingService = new PartnersReportingServiceImpl();
		formGradingService = new FormGradingServiceImpl();
		parameterGradingService = new ParameterGradingServiceImpl();
	}
	
	/**
	 * This method is used to update the self assessment done by the Client Sponsor
	 * The working is,
	 * <p>(1) It will update the PartnersReporting Data for the projectId and partnerId present in the JSON. </p>
	 * <p>(2) It will update the scores and other client sponsor details in the FormGrading  table. </p>
	 * <p>(3) It will update the scores for each parameter of this form in the ParameterGrading table. </p>
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json The JSON from the Job Object.
	 * @throws OperationFailedException if this operation fails.
	 */
	public void updateGradingCSSubmitIntoReportingDatabase(String databaseName, String json) throws OperationFailedException {
		LOGGER.debug(".inside updateGradingCSSubmitIntoReportingDatabase method of ReportDBUpdateGradingCSSubmitProcessor class.");
		Connection connection = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			if(FormsUtil.isGradingFormJSON(json)) {
				LOGGER.debug("Given JSON is a GRADING Form");
				String partnerProjectId = partnersReportingService.updatePartnersReportingData(connection,databaseName, json);
				LOGGER.debug(" >>>>> Successfully UPDATED data into PARTNERSREPORTINGTABLE, PartnerProjectId: "+StringEscapeUtils.escapeJava(partnerProjectId));
				String formUUID = formGradingService.updateFormGradingDataClientSponsorSubmit(connection,databaseName, json, partnerProjectId);
				LOGGER.debug(" >>>>> Successfully UPDATED CS Submit data into FORMGRADINGTABLE, Form UUID: "+StringEscapeUtils.escapeJava(formUUID));
				parameterGradingService.updateParameterGradingDataCS(connection,databaseName, json, formUUID);
				LOGGER.debug(" >>>>> Successfully UPDATED CS Submit data into PARAMETERGRADINGTABLE.");
				connection.commit();
				LOGGER.info("Update Grading Client Sponsor's Submit to ReportDB is done.");
			}else {
				LOGGER.error("Given JSON is NOT a GRADING Form");
			}
		}catch (Exception e) {
			LOGGER.error("Exception occurred when performing "+JobConstant.UPDATE_GRADING_CS_SUBMIT+" action",e);
			try {
				connection.rollback();
			} catch (SQLException e1) {

			}
			throw new OperationFailedException(JobConstant.UPDATE_GRADING_CS_SUBMIT+" operation is failed because, "+e.getMessage(),e);
			
		}finally {
			LOGGER.debug("Closing the connection established for "+JobConstant.UPDATE_GRADING_CS_SUBMIT+" action.");
			ReportDBMSSqlServerUtill.close(null, connection);
		}
	}
}
