package com.pwc.grading.partner.service;

import com.pwc.grading.partner.service.exception.PartnerServiceException;
/**
 * An interface class which is used to perform all
 * Partner related operations.
 *
 */
public interface IPartnerService {

	/**
	 * This method is used to get all the partners.
	 * @param tenantId the database name
	 * @return all the partners in the JSON
	 * @throws PartnerServiceException  if any exception occurs while performing this operation.
	 */
	String getAllPartners(String tenantId) throws PartnerServiceException;

	/**
	 * This method is used to get all the training centers.
	 * @param tenantId  the database name
	 * @param partnerId the id of the partner.
	 * @return all the training centers in the JSON
	 * @throws PartnerServiceException  if any exception occurs while performing this operation.
	 */
	String getAllTrainingCenters(String tenantId, String partnerId) throws PartnerServiceException;

	/**
	 * This method is used to get the partner by partnerId.
	 * @param tenantId  the database name
	 * @param partnerId the id of the partner.
	 * @return the partner details in the JSON.
	 * @throws PartnerServiceException if any exception occurs while performing this operation.
	 */
	String getPartnerByPartnerId(String tenantId, String partnerId) throws PartnerServiceException;

	/**
	 * This method is used to get the training center by tcId.
	 * @param tenantId the database name 
	 * @param partnerId the id of the partner.
	 * @param tcId the id of the training center.
	 * @return the training center details in the JSON
	 * @throws PartnerServiceException if any exception occurs while performing this operation.
	 */
	String getTrainingCenterByTcId(String tenantId, String partnerId, String tcId) throws PartnerServiceException;

}
