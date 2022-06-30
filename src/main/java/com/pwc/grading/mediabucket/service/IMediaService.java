package com.pwc.grading.mediabucket.service;

import com.pwc.grading.mediabucket.model.Media;

import com.pwc.grading.mediabucket.service.exception.MediaServiceException;

/**
 * An interface class which is used to perform all
 * Media related operations.
 *
 */
public interface IMediaService {

	/**
	 * This method is used to get the media.
	 * @param id the media to be fetched.
	 * @param tenantId the database name.
	 * @return the media object having media details.
	 * @throws MediaServiceException if any exception occurs while performing this operation.
	 */
	Media getMedia(String id, String tenantId) throws MediaServiceException;

	/**
	 * This method is used to delete the media.
	 * @param id the media to be deleted.
	 * @param tenantId the database name.
	 * @return the success response if media is deleted.
	 * @throws MediaServiceException if any exception occurs while performing this operation.
	 */
	String deleteMedia(String id, String tenantId) throws MediaServiceException;

	/**
	 * This method is used to upload the media.
	 * @param tenantId the database name.
	 * @param jsonStringReq the media details.
	 * @return the success response if media is uploaded.
	 * @throws MediaServiceException if any exception occurs while performing this operation.
	 */
	String uploadMedia(String tenantId, String jsonStringReq) throws MediaServiceException;

	/**
	 * This method is used to get the template files for
	 * UserAccessManagement and MasterData Management
	 * @param fileType indicates which file to be fetched.
	 * @return the media object having media details.
	 * @throws MediaServiceException if any exception occurs while performing this operation.
	 */
	Media getResource(String fileType) throws MediaServiceException;

	/**
	 * This method is used to get the thumbanail for the image.
	 * @param id the media whose thumbnail to be fetched.
	 * @param tenantId the database name.
	 * @return the media object having media details.
	 * @throws MediaServiceException if any exception occurs while performing this operation.
	 */
	Media getMediaThumbnail(String id, String tenantId) throws MediaServiceException;

}
