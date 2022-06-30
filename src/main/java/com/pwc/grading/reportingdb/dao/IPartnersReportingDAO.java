package com.pwc.grading.reportingdb.dao;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.reportingdb.dao.exception.ReportingDbDAOException;
import com.pwc.grading.reportingdb.model.PartnersReportingTable;
import com.pwc.grading.reportingdb.model.ReportDBClientSponsor;
/**
 * An interface class which is used to perform all
 * Partners Reporting related database operations.
 *
 */
public interface IPartnersReportingDAO {
	
	/**
	 * This method is used to add the partners reporting data into the PartnersReportingTable.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param partnersReportingObj object containing the details of PartnersReporting
	 * @return the partnerProjectId belongs to the created project and partner.
	 * @throws ReportingDbDAOException if any exception occurs when creating the entry.
	 */
	String addPartnersReportingData(Connection connection, String databaseName, PartnersReportingTable partnersReportingObj) throws ReportingDbDAOException;
	
	/**
	 * This method is used to update the details of Partners reporting data into the PartnersReportingTable
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param id the partnerProjectId
	 * @param partnersReportingObj object containing the details of PartnersReporting to be updated.
	 * @return  the partnerProjectId belongs to the updated project and partner.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	String updatePartnersReportingData(Connection connection,String databaseName, String id, PartnersReportingTable partnersReportingObj) throws ReportingDbDAOException;
	
	/**
	 * This method is used to delete the Partners Reporting data for the given project Id.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param projectId the id of the project.
	 * @throws ReportingDbDAOException if any exception occurs when deleting the entry.
	 */
	void deletePartnersReportingData(Connection connection,String databaseName,String projectId) throws ReportingDbDAOException;

	/**
	 * This method is used to fetch the Partners Reporting data for the given projectId and the partnerId.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param projectId the id of the project.
	 * @param partnerId the id of the partner.
	 * @return object containing the details of PartnersReporting
	 * @throws ReportingDbDAOException if any exception occurs when fetching the entry.
	 */
	PartnersReportingTable getDataByProjectIdAndPartnerId(Connection connection, String databaseName, String projectId, String partnerId) throws ReportingDbDAOException;

	/**
	 * This method is used to fetch the partnerProjectIds for the given projectId.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param projectId the id of the project.
	 * @return the list of String which contains the PartnerProjectIds.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the partnerProjectIds.
	 */
	List<String> getPartnerProjectIdsForProjectId(Connection connection, String databaseName, String projectId) throws ReportingDbDAOException;
	
	/**
	 * This method is used to fetch all the partners reporting data belonging to a particular project.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param projectId the id of the project.
	 * @return the {@link List} of {@link PartnersReportingTable} objects which is having the data.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the entries.
	 */
	List<PartnersReportingTable> getByProjectId(Connection connection,String databaseName, String projectId) throws ReportingDbDAOException;
	
	/**
	 * This method is used to fetch all the partners reporting data belonging to a particular Partner.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param partnerId the id of the partner.
	 * @return the {@link List} of {@link PartnersReportingTable} objects which is having the data.
	 * @throws ReportingDbDAOException if any exception occurs when fetching the entries.
	 */
	List<PartnersReportingTable> getByPartnerId(Connection connection, String databaseName, String partnerId) throws ReportingDbDAOException;

	/**
	 * This method is used to update the client sponsor details for the particular partner.
	 * @param connection used to perform the database operations in a transaction.
	 * @param databaseName the database name which is having the PartnersReportingTable table.
	 * @param obj the object containing the details of partner and the client sponsor.
	 * @throws ReportingDbDAOException if any exception occurs when updating the entry.
	 */
	void updateCSDetails(Connection connection, String databaseName, ReportDBClientSponsor obj) throws ReportingDbDAOException;
	
}
