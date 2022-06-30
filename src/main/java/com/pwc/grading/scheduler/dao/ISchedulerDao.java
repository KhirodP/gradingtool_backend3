package com.pwc.grading.scheduler.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

import com.pwc.grading.scheduler.dao.exception.SchedulerDaoException;
import com.pwc.grading.scheduler.model.GradingType;
import com.pwc.grading.scheduler.model.RatingType;
import com.pwc.grading.scheduler.model.SchedulerMapping;

/**
 * An interface class which is used to perform all
 * Scheduler Grading related database operations.
 *
 */
public interface ISchedulerDao {
	/**
	 * this method is used to update Grading Type Data
	 * 
	 * @param databaseName
	 * @param mapping
	 * @throws SchedulerDaoException
	 */
	void updateGradingTypeData(String databaseName, SchedulerMapping mapping) throws SchedulerDaoException;

	/**
	 * this method is used to update Rating Type Data
	 * 
	 * @param databaseName
	 * @param mapping
	 * @throws SchedulerDaoException
	 */
	void updateRatingTypeData(String databaseName, SchedulerMapping mapping) throws SchedulerDaoException;

	/**
	 * this method is used to add Grading Type Data
	 * 
	 * @param connection
	 * @param databaseName
	 * @param scheduler
	 * @throws SchedulerDaoException
	 */
	void addGradingTypeData(Connection connection, String databaseName, SchedulerMapping scheduler)
			throws SchedulerDaoException;

	/**
	 * this method is used to add Rating Type Data
	 * 
	 * @param connection
	 * @param databaseName
	 * @param scheduler
	 * @throws SchedulerDaoException
	 */
	void addRatingTypeData(Connection connection, String databaseName, SchedulerMapping scheduler)
			throws SchedulerDaoException;

	/**
	 * this method is used to check Grading type date exist or not
	 * 
	 * @param connection
	 * @param databaseName
	 * @param projectId
	 * @return
	 * @throws SchedulerDaoException
	 */
	boolean isGradingTypeDateExist(Connection connection, String databaseName, String projectId)
			throws SchedulerDaoException;

	/**
	 * this method is used to check rating type date exist or not
	 * 
	 * @param connection
	 * @param databaseName
	 * @param projectId
	 * @return
	 * @throws SchedulerDaoException
	 */
	boolean isRatingTypeDateExist(Connection connection, String databaseName, String projectId)
			throws SchedulerDaoException;

	/**
	 * this method is used to delete Rating type data
	 * 
	 * @param connection
	 * @param databaseName
	 * @param formId
	 * @throws SchedulerDaoException
	 */
	void deleteRatingTypeDate(Connection connection, String databaseName, String formId) throws SchedulerDaoException;

	/**
	 * this method is used to delete Grading type date by partnerId and project
	 * 
	 * @param connection
	 * @param databaseName
	 * @param formId
	 * @throws SchedulerDaoException
	 */
	void deleteGradingTypeDate(Connection connection, String databaseName, String formId) throws SchedulerDaoException;

	/**
	 * this method is used to get Grading type date by partnerId and project
	 * 
	 * @param connection
	 * @param tenantId
	 * @param partnerId
	 * @param projectId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<GradingType> getGradingTypeDataByPartnerIdAndProjectId(Connection connection, String tenantId,
			String partnerId, String projectId) throws SchedulerDaoException;

	/**
	 * this method is used to get Grading type date by partnerId and project
	 * 
	 * @param tenantId
	 * @param partnerId
	 * @param projectId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<GradingType> getGradingTypeDataByPartnerIdAndProjectId(String tenantId, String partnerId, String projectId)
			throws SchedulerDaoException;

	/**
	 * this method is used to get Grading type date
	 * 
	 * @param tenantId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<GradingType> getAllGradingTypeData(String tenantId) throws SchedulerDaoException;

	/**
	 * this method is used to get Rating type date
	 * 
	 * @param tenantId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<RatingType> getAllRatingTypeData(String tenantId) throws SchedulerDaoException;

	/**
	 * this method is used to get Grading type date
	 * 
	 * @param tenantId
	 * @param fieldAuditorId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<GradingType> getAllGradingTypeDataByFieldAuditorId(String tenantId, String fieldAuditorId)
			throws SchedulerDaoException;

	/**
	 * this method is used to get Rating type date
	 * 
	 * @param tenantId
	 * @param fieldAuditorId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<RatingType> getAllRatingTypeDataByFieldAuditorId(String tenantId, String fieldAuditorId)
			throws SchedulerDaoException;

	/**
	 * this method is used to delete Grading type date by partnerId and projectId
	 * 
	 * @param connection
	 * @param databaseName
	 * @param partnerId
	 * @param projectId
	 * @throws SchedulerDaoException
	 */
	void deleteGradingTypeData(Connection connection, String databaseName, String partnerId, String projectId)
			throws SchedulerDaoException;

	/**
	 * this method is used to delete Rating type date by partnerId and projectId
	 * 
	 * @param connection
	 * @param tenantId
	 * @param tcId
	 * @param oldProjectId
	 * @throws SchedulerDaoException
	 */
	void deleteRatingTypeDate(Connection connection, String tenantId, String tcId, String oldProjectId)
			throws SchedulerDaoException;

	/**
	 * this method is used to get Grading type date by partnerId, FormId and
	 * projectId
	 * 
	 * @param connection
	 * @param databaseName
	 * @param partnerId
	 * @param projectId
	 * @param formId
	 * @return
	 * @throws SchedulerDaoException
	 */
	GradingType getGradingTypeDataByPartnerIdProjectIdAndFormId(Connection connection, String databaseName,
			String partnerId, String projectId, String formId) throws SchedulerDaoException;

	/**
	 * used to update the self assessment of centerIncharge in ratingType Table
	 * 
	 * @param connection
	 * @param tanantId
	 * @param isSubmited
	 * @param partnerId
	 * @param projectId
	 * @param formId
	 * @param centerId
	 * @throws SchedulerDaoException
	 */
	void updateSelfAssigInRatingTypeByProjectIdPartnerIdFormIdAndTcId(Connection connection, String tanantId,
			boolean isSubmited, String partnerId, String projectId, String formId, String centerId)
			throws SchedulerDaoException;

	/**
	 * used to update the self assessment of client sponsor in GradingType table
	 * 
	 * @param connection
	 * @param tanantId
	 * @param isSubmited
	 * @param partnerId
	 * @param projectId
	 * @param formId
	 * @throws SchedulerDaoException
	 */
	void updateSelfAssigInGradingTypeByProjectIdPartnerIdAndFormId(Connection connection, String tanantId,
			boolean isSubmited, String partnerId, String projectId, String formId) throws SchedulerDaoException;

	/**
	 * this method is used to get Grading type date by partnerId,FormId and
	 * projectId
	 * 
	 * @param databaseName
	 * @param partnerId
	 * @param projectId
	 * @param formId
	 * @return
	 * @throws SchedulerDaoException
	 */
	GradingType getGradingTypeDataByPartnerIdProjectIdAndFormId(String databaseName, String partnerId, String projectId,
			String formId) throws SchedulerDaoException;

	/**
	 * this method is used to get Rating type date by partnerId,FormId and projectId
	 * 
	 * @param databaseName
	 * @param partnerId
	 * @param projectId
	 * @param formId
	 * @param tcId
	 * @return
	 * @throws SchedulerDaoException
	 */
	RatingType getRatingTypeDataByProjectIdParterIdFormIdAndTcId(String databaseName, String partnerId,
			String projectId, String formId, String tcId) throws SchedulerDaoException;

	/**
	 * used to update the audit status of field-auditor in gradingType table
	 * 
	 * @param connection
	 * @param databaseName
	 * @param auditStatus
	 * @param secondaryFieldAuditorName
	 * @param partnerId
	 * @param projectId
	 * @param formId
	 * @throws SchedulerDaoException
	 */
	void updateAuditStatusInGradingTypeByProjectIdPartnerIdAndFormId(Connection connection, String databaseName,
			boolean auditStatus, String secondaryFieldAuditorName, String partnerId, String projectId, String formId)
			throws SchedulerDaoException;

	/**
	 * used to update the audit status of field-auditor in ratingType table
	 * 
	 * @param connection
	 * @param databaseName
	 * @param auditStatus
	 * @param secondaryFieldAuditorName
	 * @param partnerId
	 * @param projectId
	 * @param formId
	 * @param tcId
	 * @throws SchedulerDaoException
	 */
	void updateAuditStatusInRatingTypeByProjectIdPartnerIdFormIdAndTcId(Connection connection, String databaseName,
			boolean auditStatus, String secondaryFieldAuditorName, String partnerId, String projectId, String formId,
			String tcId) throws SchedulerDaoException;

	/**
	 * used to get Rating Type Data By FieldAuditorId Unique By ProjectId
	 * 
	 * @param tenantId
	 * @param fieldAuditorId
	 * @return
	 * @throws SchedulerDaoException
	 */
	Set<String> getRatingTypeDataByFieldAuditorIdUniqueByProjectId(String tenantId, String fieldAuditorId)
			throws SchedulerDaoException;

	/**
	 * used to get Grading Type Data By FieldAuditorId Unique By ProjectId
	 * 
	 * @param tenantId
	 * @param fieldAuditorId
	 * @return
	 * @throws SchedulerDaoException
	 */
	Set<String> getGradingTypeDataByFieldAuditorIdUniqueByProjectId(String tenantId, String fieldAuditorId)
			throws SchedulerDaoException;

	/**
	 * used to get Grading Type Data By FieldAuditorId ProjectId And PartnerId
	 * 
	 * @param tenantId
	 * @param fieldAuditorId
	 * @param projectId
	 * @param partnerId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<GradingType> getGradingTypeDataByFieldAuditorIdProjectIdAndPartnerId(String tenantId, String fieldAuditorId,
			String projectId, String partnerId) throws SchedulerDaoException;

	/**
	 * used to get Rating Type Data By FieldAuditorId ProjectId And PartnerId
	 * 
	 * @param tenantId
	 * @param fieldAuditorId
	 * @param projectId
	 * @param partnerId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<RatingType> getRatingTypeDataByFieldAuditorIdProjectIdAndPartnerId(String tenantId, String fieldAuditorId,
			String projectId, String partnerId) throws SchedulerDaoException;

	/**
	 * used to get Rating Type Data By FieldAuditorId And ProjectId
	 * 
	 * @param tenantId
	 * @param fieldAuditorId
	 * @param projectId
	 * @return
	 * @throws SchedulerDaoException
	 */
	Set<String> getRatingTypeDataByFieldAuditorIdAndProjectId(String tenantId, String fieldAuditorId, String projectId)
			throws SchedulerDaoException;

	/**
	 * used to get Grading Type Data By FieldAuditorId And ProjectId
	 * 
	 * @param tenantId
	 * @param fieldAuditorId
	 * @param projectId
	 * @return
	 * @throws SchedulerDaoException
	 */
	Set<String> getGradingTypeDataByFieldAuditorIdAndProjectId(String tenantId, String fieldAuditorId, String projectId)
			throws SchedulerDaoException;

	/**
	 * used to get Rating Type Data By ProjectId, ParterId And TcId
	 * 
	 * @param connection
	 * @param databaseName
	 * @param partnerId
	 * @param projectId
	 * @param tcId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<RatingType> getRatingTypeDataByProjectIdParterIdAndTcId(Connection connection, String databaseName,
			String partnerId, String projectId, String tcId) throws SchedulerDaoException;

	// ********************** For Report Generation *************************//
	/**
	 * used to get Rating Type Data By ProjectId, ParterId And TcId
	 * 
	 * @param databaseName
	 * @param partnerId
	 * @param projectId
	 * @param tcId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<RatingType> getRatingTypeDataByProjectIdParterIdAndTcId(String databaseName, String partnerId,
			String projectId, String tcId) throws SchedulerDaoException;

	/**
	 * used to get Rating Type Data By ProjectId And ParterId
	 * 
	 * @param tenantId
	 * @param partnerId
	 * @param projectId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<RatingType> getRatingTypeDataByProjectIdAndParterId(String tenantId, String partnerId, String projectId)
			throws SchedulerDaoException;

	/**
	 * used to get Grading Type Data By ProjectId And PartnerId
	 * 
	 * @param tenantId
	 * @param partnerId
	 * @param projectId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<GradingType> getGradingTypeDataByProjectIdAndPartnerId(String tenantId, String partnerId, String projectId)
			throws SchedulerDaoException;

	/**
	 * used to delete Rating Type Date By tcId ProjectId And PartnerId
	 * 
	 * @param connection
	 * @param databaseName
	 * @param tcId
	 * @param projectId
	 * @param partnerId
	 * @throws SchedulerDaoException
	 */
	void deleteRatingTypeDateBytcIdProjectIdAndPartnerId(Connection connection, String databaseName, String tcId,
			String projectId, String partnerId) throws SchedulerDaoException;

	/**
	 * used to get Rating Type Data By ProjectId, ParterId, FormId And TcId
	 * 
	 * @param connection
	 * @param databaseName
	 * @param partnerId
	 * @param projectId
	 * @param formId
	 * @param tcId
	 * @return
	 * @throws SchedulerDaoException
	 */
	RatingType getRatingTypeDataByProjectIdParterIdFormIdAndTcId(Connection connection, String databaseName,
			String partnerId, String projectId, String formId, String tcId) throws SchedulerDaoException;

	/**
	 * this method is used to get RatingType Data By ProjectId And ParterId
	 * 
	 * @param connection
	 * @param databaseName
	 * @param partnerId
	 * @param projectId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<RatingType> getRatingTypeDataByProjectIdAndParterId(Connection connection, String databaseName,
			String partnerId, String projectId) throws SchedulerDaoException;

	/**
	 * used to get GradingType Data By ProjectId And PartnerId
	 * 
	 * @param connection
	 * @param databaseName
	 * @param partnerId
	 * @param projectId
	 * @return
	 * @throws SchedulerDaoException
	 */
	List<GradingType> getGradingTypeDataByProjectIdAndPartnerId(Connection connection, String databaseName,
			String partnerId, String projectId) throws SchedulerDaoException;

}
