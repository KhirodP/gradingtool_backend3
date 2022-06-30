package com.pwc.grading.masterdata.service;

import com.pwc.grading.masterdata.service.exception.MasterDataManagmentServiceException;

import io.micronaut.http.multipart.CompletedFileUpload;

/**
 * This IMasterDataManagmentService interface used for mapping the partner,
 * project and Training center
 * 
 *
 */
public interface IMasterDataManagmentService {
	/**
	 * This method is used to add mapping between the partner and project or
	 * training center and project
	 * 
	 * @param tenantId the database name.
	 * @param partner the partner details
	 * @return the response message of this method.
	 * @throws MasterDataManagmentServiceException if any exception occurs when performing this operation
	 */
	String addProjectMasterData(String tenantId, String partner) throws MasterDataManagmentServiceException;

	/**
	 * This method is used to add mapping between the partner and project or
	 * training center and project using the Excel file
	 * 
	 * @param tenantId the database name.
	 * @param multipart the excel upload instance which has to be processed.
	 * @param projectId it is a project which you are adding mapping
	 * @return the response message of this method.
	 * @throws MasterDataManagmentServiceException if any exception occurs when performing this operation
	 */
	String uploadProjectMasterDataFromExcel(String tenantId, CompletedFileUpload multipart, String projectId)
			throws MasterDataManagmentServiceException;

	/**
	 * This method used to get all the mapping data
	 * 
	 * @param tenantId the database name.
	 * @return the json response which has the master data.
	 * @throws MasterDataManagmentServiceException if any exception occurs when performing this operation
	 */
	String getMasterMappingData(String tenantId) throws MasterDataManagmentServiceException;

	/**
	 * This method is used to Update the mapping like tcName, tcAddress.. etc it is
	 * also used to change the Client-Sponsor for a partner or change the
	 * CenterIncharge for Training Cetner
	 * 
	 * @param tenantId the database name.
	 * @param mappingId the project mapping id.
	 * @param masterData the json which has the master data.
	 * @return the json response which has the master data.
	 * @throws MasterDataManagmentServiceException if any exception occurs when performing this operation
	 */
	String updateProjectMasterData(String tenantId, String mappingId, String masterData)
			throws MasterDataManagmentServiceException;

	/**
	 * This is used to filter the project Mapping data by projectName , partnerName
	 * and tcId
	 * 
	 * @param tenantId  the database name.
	 * @param projectName - filter by projectName
	 * @param partnerName - filter by partnerName
	 * @param tcId        - filter by tcId
	 * @return the json response which has the master data.
	 * @throws MasterDataManagmentServiceException if any exception occurs when performing this operation
	 */
	String filterProjectMasterData(String tenantId, String projectName, String partnerName, String tcName)
			throws MasterDataManagmentServiceException;

	/**
	 * This method is used to delete multiple Mapping by Id's
	 * 
	 * @param tenantId the database name.
	 * @param requestBody - request Body contains the mapping Id's
	 * @return the response message of this method.
	 * @throws MasterDataManagmentServiceException if any exception occurs when performing this operation
	 */
	String deleteMultipleMappingDataById(String tenantId, String requestBody)
			throws MasterDataManagmentServiceException;

}
