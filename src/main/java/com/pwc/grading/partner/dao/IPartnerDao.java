package com.pwc.grading.partner.dao;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.partner.dao.exception.PartnerDaoException;
import com.pwc.grading.partner.model.PartnerDetails;
import com.pwc.grading.partner.model.TrainingCenterDetails;

/**
 * An interface class which is used to perform all
 * partner related database operations.
 *
 */
public interface IPartnerDao {

	/**
	 * Get all the partners.
	 * @param tenantId the database name.
	 * @return all the partner details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	List<PartnerDetails> getAllPartner(String tenantId) throws PartnerDaoException;

	/**
	 * Get all the training center belongs to a particular partner.
	 * @param tenantId the database name
	 * @param partnerId the partner id.
	 * @return all the training center details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	List<TrainingCenterDetails> getAllTrainingCenterDetailsByPartnerId(String tenantId, String partnerId)
			throws PartnerDaoException;

	/**
	 * Get all the training center details by the partnerid and projectId in mapping.
	 * @param tenantId the database name
	 * @param projectId the project id.
	 * @param partnerId the partner id.
	 * @return all the training center details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	List<TrainingCenterDetails> getAllTrainingCenterDetailsByPartnerIdAndProjectIdInMapping(String tenantId,
			String projectId, String partnerId) throws PartnerDaoException;

	/**
	 * Get the partner details by the partner Id.
	 * @param databaseNamec
	 * @param partnerId the partner id.
	 * @return  all the partner details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	PartnerDetails getPartnerById(String databaseName, String partnerId) throws PartnerDaoException;

	/**
	 * Get the partner details by the partner Id.
	 * @param connection if this operation to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param partnerId the partner id.
	 * @return the partner details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	PartnerDetails getPartnerById(Connection connection, String databaseName, String partnerId)
			throws PartnerDaoException;

	/**
	 * Get the training center details by tcId and the partnerID
	 * @param databaseName the database name
	 * @param partnerId the partner id.
	 * @param tcId the training center id
	 * @return the training center details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	TrainingCenterDetails getTrainingCenterDetailsByTCIdAndPartnerId(String databaseName, String partnerId, String tcId)
			throws PartnerDaoException;

	/**
	 * Get the training center details by tcId
	 * @param tenantId the database name
	 * @param tcId  the training center id
	 * @return the training center details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	TrainingCenterDetails getTrainingCenterDetailsByTcId(String tenantId, String tcId) throws PartnerDaoException;

	/**
	 * Update the training center details
	 * @param connection if this operation to be performed in a single transaction.
	 * @param databaseName  the database name
	 * @param tcDetails the training center details to be updated.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	void updateTrainingCenterDetails(Connection connection, String databaseName, TrainingCenterDetails tcDetails)
			throws PartnerDaoException;

	/**
	 * Update the Partner details by partnerId
	 * @param connection if this operation to be performed in a single transaction.
	 * @param databaseName the database name
	 * @param clientSponsorId the id of the client sponsor
	 * @param partnerId the partner id.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	void updatePartnerById(Connection connection, String databaseName, String clientSponsorId, String partnerId)
			throws PartnerDaoException;

	/**
	 * Get the training center details by tcId
	 * @param connection if this operation to be performed in a single transaction.
	 * @param tenantId the database name
	 * @param tcId  the training center id
	 * @return the training center details.
	 * @throws PartnerDaoException if any exception occurs while performing this operation.
	 */
	TrainingCenterDetails getTrainingCenterDetailsByTcId(Connection connection, String databaseName, String tcId)
			throws PartnerDaoException;

	/**
	 * Get the Partner details by clientSponsorId
	 * @param connection if this operation to be performed in a single transaction.
	 * @param tanantId  the database name
	 * @param userId the id of the client sponsor
	 * @return the partner details.
	 * @throws PartnerDaoException  if any exception occurs while performing this operation.
	 */
	PartnerDetails getPartnerByClientSponsorId(Connection connection, String tanantId, String userId)
			throws PartnerDaoException;

	/**
	 * Get the Partner details by clientSponsorId
	 * @param tanantId  the database name
	 * @param userId the id of the client sponsor
	 * @return the partner details.
	 * @throws PartnerDaoException  if any exception occurs while performing this operation.
	 */
	PartnerDetails getPartnerByClientSponsorId(String databaseName, String userId) throws PartnerDaoException;

	/**
	 * Get all the partners count.
	 * @param databaseName  the database name
	 * @return the count of partners.
	 * @throws PartnerDaoException  if any exception occurs while performing this operation.
	 */
	int getTotalPartnerCount(String databaseName) throws PartnerDaoException;

	//

}
