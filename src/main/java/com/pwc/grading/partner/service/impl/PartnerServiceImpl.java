package com.pwc.grading.partner.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.partner.dao.IPartnerDao;
import com.pwc.grading.partner.model.PartnerDetails;
import com.pwc.grading.partner.model.TrainingCenterDetails;
import com.pwc.grading.partner.service.IPartnerService;
import com.pwc.grading.partner.service.PartnerServiceConstants;
import com.pwc.grading.partner.service.exception.PartnerServiceException;
import com.pwc.grading.user.dao.IUserDao;
import com.pwc.grading.user.dao.exception.UserDaoException;
import com.pwc.grading.user.model.User;

/**
 * Implementation class for {@link IPartnerService}
 *
 */
@Singleton
public class PartnerServiceImpl implements IPartnerService {
	private static final Logger logger = LoggerFactory.getLogger(PartnerServiceImpl.class);
	@Inject
	private IUserDao userDao;
	@Inject
	private IPartnerDao iPartnerDao;

	/**
	 * This method is used to get all the partners.
	 * @param tenantId the database name
	 * @return all the partners in the JSON
	 * @throws PartnerServiceException  if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getAllPartners(String tenantId) throws PartnerServiceException {
		try {
			logger.debug("in getAllPartners , DataBase is :" + tenantId);
			List<PartnerDetails> allPartner = iPartnerDao.getAllPartner(tenantId);
			logger.debug("list of partners: " + allPartner);
			JSONArray jsonArrayResponse = buildJsonFromJsonPartner(tenantId, allPartner);
			JSONObject jsonObjectResponse = new JSONObject();
			jsonObjectResponse.put(PartnerServiceConstants.PARTNERS, jsonArrayResponse);
			return jsonObjectResponse.toJSONString();
		} catch (Exception e) {
			throw new PartnerServiceException(" unable to get partners:: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get all the training centers.
	 * @param tenantId  the database name
	 * @param partnerId the id of the partner.
	 * @return all the training centers in the JSON
	 * @throws PartnerServiceException  if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getAllTrainingCenters(String tenantId, String partnerId) throws PartnerServiceException {
		try {
			logger.debug("in getAllTrainingCenters , DateBase is :" + tenantId);
			List<TrainingCenterDetails> allTrainingCenterDetails = iPartnerDao.getAllTrainingCenterDetailsByPartnerId(tenantId,
					partnerId);
			logger.debug("list of TrainingCenterDetails: " + allTrainingCenterDetails);
			JSONArray jsonArrayResponse = buildJsonFromJsonTrainingCenterDetails(tenantId, allTrainingCenterDetails);
			JSONObject jsonObjectResponse = new JSONObject();
			jsonObjectResponse.put(PartnerServiceConstants.TRANING_CENTER_DETAILS, jsonArrayResponse);
			return jsonObjectResponse.toJSONString();
		} catch (Exception e) {
			throw new PartnerServiceException(" unable to get TrainingCenterDetails:: " + e.getMessage(), e);
		}
	}

	/**
	 * Creating JSON Array from the list of training center details.
	 */
	@SuppressWarnings("unchecked")
	private JSONArray buildJsonFromJsonTrainingCenterDetails(String tenantId,
			List<TrainingCenterDetails> allTrainingCenterDetails) throws UserDaoException {
		JSONArray jsonarray = new JSONArray();
		for (TrainingCenterDetails tcDetails : allTrainingCenterDetails) {
			JSONObject jsonObject = buildJsonFromTrainingCenterDetails(tenantId, tcDetails);
			jsonarray.add(jsonObject);
		}
		return jsonarray;
	}

	/**
	 * Creating JSON Array from the list of partner details.
	 */
	@SuppressWarnings("unchecked")
	private JSONArray buildJsonFromJsonPartner(String tenantId, List<PartnerDetails> allPartner)
			throws UserDaoException {
		JSONArray jsonarray = new JSONArray();
		for (PartnerDetails partner : allPartner) {
			JSONObject jsonObject = buildJsonFromPartnerDetails(tenantId, partner);
			jsonarray.add(jsonObject);
		}
		return jsonarray;
	}

	/**
	 * Creating JSON Object from the training center details.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildJsonFromTrainingCenterDetails(String tenantId,
			TrainingCenterDetails singleTrainingCenterDetails) throws UserDaoException {
		JSONObject trainingCenterJsonObj = new JSONObject();
		trainingCenterJsonObj.put(PartnerServiceConstants.TC_ID, singleTrainingCenterDetails.getTcId());
		trainingCenterJsonObj.put(PartnerServiceConstants.CENTER_ADDRESS,
				singleTrainingCenterDetails.getCenterAddress());
		trainingCenterJsonObj.put(PartnerServiceConstants.TC_NAME, singleTrainingCenterDetails.getTcName());
		trainingCenterJsonObj.put(PartnerServiceConstants.DISTRICT, singleTrainingCenterDetails.getDistrict());
		String centerInchargeId = singleTrainingCenterDetails.getCenterInchargeId();
		User user = userDao.getUserByUserId(tenantId, centerInchargeId);
		trainingCenterJsonObj.put(PartnerServiceConstants.CENTER_INCHARGE_ID, centerInchargeId);
		trainingCenterJsonObj.put(PartnerServiceConstants.CENTER_INCHARGE_FIRST_NAME, user.getFirstName());
		trainingCenterJsonObj.put(PartnerServiceConstants.CENTER_INCHARGE_LAST_NAME, user.getLastName());
		trainingCenterJsonObj.put(PartnerServiceConstants.CENTER_INCHARGE_EMAIL, user.getEmail());
		trainingCenterJsonObj.put(PartnerServiceConstants.CENTER_INCHARGE_CONTACT, user.getPhone());
		trainingCenterJsonObj.put(PartnerServiceConstants.LATITUDE, singleTrainingCenterDetails.getLatitude());
		trainingCenterJsonObj.put(PartnerServiceConstants.LONGITUDE, singleTrainingCenterDetails.getLongitude());
		return trainingCenterJsonObj;
	}

	/**
	 * Creating JSON Object from the partner details.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject buildJsonFromPartnerDetails(String tenantId, PartnerDetails partner) throws UserDaoException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(PartnerServiceConstants.PARTNER_NAME, partner.getPartnerName());
		jsonObject.put(PartnerServiceConstants.PARTNER_ID, partner.getPartnerId());
		String userId = partner.getClientSponsorId();
		User user = userDao.getUserByUserId(tenantId, userId);
		jsonObject.put(PartnerServiceConstants.CLIENT_SPONSOR_FIRST_NAME, user.getFirstName());
		jsonObject.put(PartnerServiceConstants.CLIENT_SPONSOR_LAST_NAME, user.getLastName());
		jsonObject.put(PartnerServiceConstants.CLIENT_SPONSOR_CONTACT, user.getPhone());
		jsonObject.put(PartnerServiceConstants.CLIENT_SPONSOR_EMAIL, user.getEmail());
		jsonObject.put(PartnerServiceConstants.CLIENT_SPONSOR_ID, userId);
		return jsonObject;
	}

	/**
	 * This method is used to get the partner by partnerId.
	 * @param tenantId  the database name
	 * @param partnerId the id of the partner.
	 * @return the partner details in the JSON.
	 * @throws PartnerServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getPartnerByPartnerId(String tenantId, String partnerId) throws PartnerServiceException {
		try {
			logger.debug("in getPartnerByPartnerId , DataBase is :" + tenantId);
			PartnerDetails partner = iPartnerDao.getPartnerById(tenantId, partnerId);
			logger.debug(" partners :: " + partner);
			JSONObject jsonObject = buildJsonFromPartnerDetails(tenantId, partner);
			JSONObject jsonObjectResponse = new JSONObject();
			jsonObjectResponse.put(PartnerServiceConstants.PARTNER, jsonObject);
			return jsonObjectResponse.toJSONString();
		} catch (Exception e) {
			throw new PartnerServiceException(" unable to get partners:: " + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to get the training center by tcId.
	 * @param tenantId the database name 
	 * @param partnerId the id of the partner.
	 * @param tcId the id of the training center.
	 * @return the training center details in the JSON
	 * @throws PartnerServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getTrainingCenterByTcId(String tenantId, String partnerId, String tcId)
			throws PartnerServiceException {
		try {
			logger.debug("in getTrainingCenterByTcId , DateBase is :" + tenantId);
			TrainingCenterDetails tcDetails = iPartnerDao.getTrainingCenterDetailsByTcId(tenantId, tcId);
			if (tcDetails == null) {
				throw new PartnerServiceException(" invalid TrainingCenter Id " + tcId);
			}
			logger.debug(" TrainingCenterDetails: " + tcDetails);
			JSONObject jsonObject = buildJsonFromTrainingCenterDetails(tenantId, tcDetails);
			JSONObject jsonObjectResponse = new JSONObject();
			jsonObjectResponse.put(PartnerServiceConstants.TRANING_CENTER_DETAILS, jsonObject);
			return jsonObjectResponse.toJSONString();
		} catch (Exception e) {
			throw new PartnerServiceException(" unable to get TrainingCenterDetails:: " + e.getMessage(), e);
		}
	}
}
