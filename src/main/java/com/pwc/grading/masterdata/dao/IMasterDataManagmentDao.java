package com.pwc.grading.masterdata.dao;

import java.sql.Connection;
import java.util.List;

import com.pwc.grading.masterdata.dao.exception.MasterDataManagmentDaoException;
import com.pwc.grading.masterdata.model.GradingEnable;
import com.pwc.grading.masterdata.model.ProjectMapping;
import com.pwc.grading.partner.model.PartnerDetails;
import com.pwc.grading.partner.model.TrainingCenterDetails;

/**
 * An interface class which is used to perform all
 * Master Data Management related database operations.
 *
 */
public interface IMasterDataManagmentDao {
	/**
	 * This method is used to add partner.
	 * @param tenantId the database name.
	 * @param partner the partner details
	 * @return the response created by this method.
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	String addPartner(String tenantId, PartnerDetails partner) throws MasterDataManagmentDaoException;

	/**
	 * This method is used to add Training Center Details.
	 * @param tenantId the database name
	 * @param tcDetails the Training Center details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void addTrainingCenterDetails(String tenantId, TrainingCenterDetails tcDetails)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to add the project mapping .
	 * @param databaseName the database name the database name
	 * @param mapping the project mapping details the mapping details.
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void addProjectMapping(String databaseName, ProjectMapping mapping) throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get Project Mapping By TcId
	 * @param databaseName the database name the database name
	 * @param tcId the Training Center Id.
	 * @return list of project mapping details.
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	List<ProjectMapping> getProjectMappingByTcId(String databaseName, String tcId)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get Partner By Partner Name With Case InSensitive
	 * @param databaseName the database name the database name
	 * @param partnerName the partner Name
	 * @return the partner details 
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	PartnerDetails getPartnerByPartnerNameWithCaseInSensitive(String databaseName, String partnerName)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to update Partner 
	 * @param connection if this operation is to be performed in single transaction if this operation is to be performed in single transaction.
	 * @param databaseName the database name the database name
	 * @param partnerId the id of the partner the partner Id 
	 * @param partnerDetail the partner details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void updatePatnerById(Connection connection, String databaseName, String partnerId, PartnerDetails partnerDetail)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get Project Mapping Data.
	 * @param tenantId the database name
	 * @return  list of project mapping details.
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	List<ProjectMapping> getProjectMappingData(String tenantId) throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get the Project Mapping Data By ParterProjectId
	 * @param tenantId the database name
	 * @param parternProjectId the ParterProjectId.
	 * @return list of project mapping details.
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */ 
	List<ProjectMapping> getProjectMappingDataByParterProjectId(String tenantId, String parternProjectId)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to delete Mapping Data By mapping Id
	 * @param connection if this operation is to be performed in single transaction
	 * @param tenantId the database name
	 * @param mappingId the project mapping id
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void deleteMappingDataById(Connection connection, String tenantId, String mappingId)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to delete Grading Enable By PartnerId And ProjectId
	 * @param connection if this operation is to be performed in single transaction
	 * @param tenantId the database name
	 * @param partnerId the id of the partner
	 * @param projectId the id of the project the id of the project
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void deleteGradingEnableByPartnerIdAndProjectId(Connection connection, String tenantId, String partnerId,
			String projectId) throws MasterDataManagmentDaoException;

	/**
	 * This method is used to update the Project Mapping
	 * @param databaseName the database name
	 * @param mappingId the project mapping id
	 * @param mapping the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void updateProjectMapping(String databaseName, String mappingId, ProjectMapping mapping)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get ProjectMapping By mapping Id
	 * @param databaseName the database name
	 * @param id the project mapping id
	 * @return the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	ProjectMapping getProjectMappingById(String databaseName, String id) throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get Project Mapping By ProjectId.
	 * @param databaseName the database name
	 * @param projectId the id of the project the id of the project
	 * @return all the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	List<ProjectMapping> getProjectMappingByProjectId(String databaseName, String projectId)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get All Training Center Details By PartnerId.
	 * @param databaseName the database name
	 * @param partnerId the id of the partner
	 * @return the training center details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	List<TrainingCenterDetails> getAllTrainingCenterDetailsByPartnerId(String databaseName, String partnerId)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to add GradingEnable data.
	 * @param connection if this operation is to be performed in single transaction
	 * @param tenantId the database name
	 * @param gradingEnable the grading enable details 
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void addGradingEnable(Connection connection, String tenantId, GradingEnable gradingEnable)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get grading enable by project id and partner id.
	 * @param connection if this operation is to be performed in single transaction
	 * @param tenantId the database name
	 * @param projectId the id of the project the id of the project
	 * @param partnerId the id of the partner
	 * @return the grading enable details 
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	GradingEnable getGradingEnableByProjectIdAndPartnerId(Connection connection, String tenantId, String projectId,
			String partnerId) throws MasterDataManagmentDaoException;

	/**
	 * This method is used to update Grading Enable.
	 * @param connection if this operation is to be performed in single transaction 
	 * @param tenantId the database name
	 * @param isGradingEnable
	 * @param partnerId the id of the partner
	 * @param projectId the id of the project the id of the project
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void updateGradingEnable(Connection connection, String tenantId, boolean isGradingEnable, String partnerId,
			String projectId) throws MasterDataManagmentDaoException;

	// for report
	/**
	 * This method is used to get project mapping by partner id, tc id and project id.
	 * @param databaseName the database name
	 * @param partnerId the id of the partner
	 * @param tcId the id of the training center
	 * @param projectId the id of the project the id of the project
	 * @return the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	ProjectMapping getProjectMappingByPartnerIdTcIdAndProjectId(String databaseName, String partnerId, String tcId,
			String projectId) throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get project mapping by project id and partner id.
	 * @param databaseName the database name
	 * @param projectId the id of the project the id of the project
	 * @param partnerId the id of the partner
	 * @return all the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	List<ProjectMapping> getProjectMappingByProjectIdAndPartnerId(String databaseName, String projectId,
			String partnerId) throws MasterDataManagmentDaoException;

	/**
	 * This method is used to add Training Center Details
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param tcDetails the training center details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void addTrainingCenterDetails(Connection connection, String databaseName, TrainingCenterDetails tcDetails)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to add Partner.
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param partner the partner details
	 * @return the partner Id.
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	String addPartner(Connection connection, String databaseName, PartnerDetails partner)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to add Project Mapping
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param mapping the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void addProjectMapping(Connection connection, String databaseName, ProjectMapping mapping)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get Project Mapping Data.
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @return all the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	List<ProjectMapping> getProjectMappingData(Connection connection, String databaseName)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get Partner By partner name With Case InSensitive
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param partnerName the name of the partner
	 * @return the partner details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	PartnerDetails getPartnerByPartnerNameWithCaseInSensitive(Connection connection, String databaseName,
			String partnerName) throws MasterDataManagmentDaoException;

	/**
	 * This method is used to update the Project Mapping.
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param mappingId the project mapping id
	 * @param mapping the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void updateProjectMapping(Connection connection, String databaseName, String mappingId, ProjectMapping mapping)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get Project Mapping By ProjectId.
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param projectId the id of the project
	 * @return the project mapping details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	List<ProjectMapping> getProjectMappingByProjectId(Connection connection, String databaseName, String projectId)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get Grading Enable By ProjectId And PartnerId.
	 * @param databaseName the database name
	 * @param projectId the id of the project
	 * @param partnerId the id of the partner
	 * @return  the grading enable details 
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	GradingEnable getGradingEnableByProjectIdAndPartnerId(String databaseName, String projectId, String partnerId)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get Grading Enable By Partner Id.
	 * @param databaseName the database name
	 * @param partnerId the id of the partner
	 * @return all the grading enable details 
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	List<GradingEnable> getGradingEnableByPartnerId(String databaseName, String partnerId)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get Grading Enable By Project Id.
	 * @param databaseName the database name
	 * @param ProjectId the id of the project
	 * @return  the grading enable details 
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	List<GradingEnable> getGradingEnableByProjectId(String databaseName, String ProjectId)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to get Partner Details By Project Id.
	 * @param databaseName the database name
	 * @param ProjectId the id of the project
	 * @return  the Partner Details
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	List<PartnerDetails> getPartnerDetailsByProjectId(String databaseName, String ProjectId)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to delete all the project mappings for given ProjectId
	 * @param connection if this operation is to be performed in single transaction
	 * @param databaseName the database name
	 * @param projectId the id of the project the id of the project
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void deleteProjectMappingForProjectId(Connection connection, String databaseName, String projectId)
			throws MasterDataManagmentDaoException;

	/**
	 * This method is used to delete Grading Enable By ProjectId
	 * @param connection if this operation is to be performed in single transaction
	 * @param tenantId the database name
	 * @param projectId the id of the project the id of the project
	 * @throws MasterDataManagmentDaoException if any exception occurs when performing this operation.
	 */
	void deleteGradingEnableByProjectId(Connection connection, String tenantId, String projectId) throws MasterDataManagmentDaoException;
}
