package com.pwc.grading.reportingdb.processor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.reportingdb.db.util.ReportDBMSSqlServerUtill;
import com.pwc.grading.reportingdb.processor.exception.OperationFailedException;
import com.pwc.grading.reportingdb.service.IFormGradingService;
import com.pwc.grading.reportingdb.service.IFormRatingService;
import com.pwc.grading.reportingdb.service.IParameterGradingService;
import com.pwc.grading.reportingdb.service.IParameterRatingService;
import com.pwc.grading.reportingdb.service.IPartnersReportingService;
import com.pwc.grading.reportingdb.service.impl.FormGradingServiceImpl;
import com.pwc.grading.reportingdb.service.impl.FormRatingServiceImpl;
import com.pwc.grading.reportingdb.service.impl.ParameterGradingServiceImpl;
import com.pwc.grading.reportingdb.service.impl.ParameterRatingServiceImpl;
import com.pwc.grading.reportingdb.service.impl.PartnersReportingServiceImpl;

/**
 * This processor is used to delete a project data from the reporting tables.
 *
 */
public class ReportingDBDeleteProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingDBDeleteProcessor.class);
	
//	@Inject
	private IPartnersReportingService partnersReportingService;
	
//	@Inject
	private IFormGradingService formGradingService;
	
//	@Inject
	private IFormRatingService formRatingService;
	
//	@Inject
	private IParameterGradingService parameterGradingService;
	
//	@Inject
	private IParameterRatingService parameterRatingService;
	
	public ReportingDBDeleteProcessor() {
		partnersReportingService = new PartnersReportingServiceImpl();
		formGradingService = new FormGradingServiceImpl();
		formRatingService = new FormRatingServiceImpl();
		parameterGradingService = new ParameterGradingServiceImpl();
		parameterRatingService = new ParameterRatingServiceImpl();
	}
	
	/**
	 * This method is used to delete the project data from the reporting tables.
	 * The working is,
	 * <p>(1) It will get all the partnerProjectIds for the projectId present in the JSON. </p>
	 * <p>(2) After getting all the partnerProjectIds, it will get all the formUUIDs for all the partnerProjectIds from the FormGrading table. </p>
	 * <p>(3) After getting all the formUUIDs, it will delete all parameters of this form in the ParameterGrading table. </p>
	 * <p>(4) Then, it will Delete all the forms in the FormGradingTable for all the formUUIDs. </p> 
	 * <p>(5) Now, it will get all the formUUIDs for all the partnerProjectIds from the FormRating table.</p>
	 * <p>(6) After getting all the formUUIDs, it will delete all parameters of this form in the ParameterRating table. </p>
	 * <p>(7) Then, it will Delete all the forms in the FormRatingTable for all the formUUIDs.  </p> 
	 * <p>(8) Finally, it deletes all the partnersReporting data for this projectId.</p>
	 * @param databaseName the database name indicating which database is used for this operation.
	 * @param json The JSON from the Job Object.
	 * @throws OperationFailedException if this operation fails.
	 */
	public void deleteDataFromReportingDatabase(String databaseName, String json) throws OperationFailedException {
		LOGGER.debug(".inside deleteDataFromReportingDatabase method of ReportingDBDeleteProcessor class.");
		Connection connection = null;
		try {
			connection = ReportDBMSSqlServerUtill.getConnection();
			connection.setAutoCommit(false);
			
			//Getting partnerProject ID list belonging to given projectId
			List<String> partnerProjectIdList = partnersReportingService.getPartnerProjectIdList(connection,databaseName, json);
			LOGGER.debug(" !!!! Successfully Fetched partnerProjectIdList from PARTNERSREPORTINGTABLE");
			
			//Getting FormUUID list for FormGradingTable belonging to given partnerprojectId list.
			List<String> formUUIDListFG = formGradingService.getFormUUIDList(connection,databaseName, partnerProjectIdList);
			LOGGER.debug(" !!!! Successfully Fetched formUUIDListFG from from FORMGRADINGTABLE");
			parameterGradingService.deleteParameterGradingData(connection,databaseName, formUUIDListFG);
			LOGGER.debug(" >>>>> Successfully DELETED data from PARAMETERGRADINGTABLE.");
			
			//Getting FormUUID list for FormRatingTable belonging to given partnerprojectId list.
			List<String> formUUIDListFR = formRatingService.getFormUUIDList(connection,databaseName, partnerProjectIdList);
			LOGGER.debug(" !!!! Successfully Fetched formUUIDListFR from FORMRATINGTABLE");			
			parameterRatingService.deleteParameterRatingData(connection,databaseName, formUUIDListFR); 
			LOGGER.debug(" >>>>> Successfully DELETED data from PARAMETERRATINGTABLE.");
			formGradingService.deleteFormGradingDataForPartnerProjectIdList(connection, databaseName, partnerProjectIdList);
			LOGGER.debug(" >>>>> Successfully DELETED data from FORMGRADINGTABLE.");
			formRatingService.deleteFormRatingDataForPartnerProjectIdList(connection, databaseName, partnerProjectIdList);
			LOGGER.debug(" >>>>> Successfully DELETED data from FORMRATINGTABLE.");
			
			//Finally deleting, data from PartnersReportingTable
			partnersReportingService.deletePartnersReportingByProjectId(connection, databaseName, json);
			LOGGER.debug(" >>>>> Successfully DELETED data from PARTNERSREPORTINGTABLE.");
			connection.commit();
			LOGGER.info("Delete project from ReportDB is done.");
		}catch (Exception e) {
			LOGGER.error("Exception occurred when performing DELETE action",e);
			try {
				connection.rollback();
			} catch (SQLException e1) {

			}
			throw new OperationFailedException("DELETE Operation is failed because, "+e.getMessage(),e);
			
		}finally {
			LOGGER.debug("Closing the connection established for DELETE action.");
			ReportDBMSSqlServerUtill.close(null, connection);
		}
	}

}
