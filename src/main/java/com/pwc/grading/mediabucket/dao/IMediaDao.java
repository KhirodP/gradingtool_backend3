package com.pwc.grading.mediabucket.dao;


import java.sql.Connection;
import java.util.List;
import java.util.Set;

import com.pwc.grading.mediabucket.dao.exception.MediaDaoException;
import com.pwc.grading.mediabucket.model.Media;

/**
 * An interface class which is used to perform all
 * media related database operations.
 *
 */
public interface IMediaDao {

	/**
	 * This method is used to upload the media to the database.
	 * @param databaseName the database name.
	 * @param media the media object having media details.
	 * @return the uploaded Media-Id.
	 * @throws MediaDaoException if any exception occurs while performing this operation.
	 */
	public String uploadMedia(String databaseName, Media media) throws MediaDaoException;

	/**
	 * This method is used to delete the media from the database.
	 * @param id the media to be deleted
	 * @param tenantId the database name.
	 * @throws MediaDaoException if any exception occurs while performing this operation.
	 */
	public void deleteMedia(String id, String tenantId) throws MediaDaoException;

	/**
	 * This method is used to get the media from the database.
	 * @param id the media to be fetched.
	 * @param dataBaseName the database name.
	 * @return  the media object having media details.
	 * @throws MediaDaoException if any exception occurs while performing this operation.
	 */
	public Media getMedia(String id, String dataBaseName) throws MediaDaoException;

	/**
	 * This method is used to get all the media from the database.
	 * @param mediaIdkeySet the media Ids to be fetched.
	 * @param tenantId the database name.
	 * @return all the media objects having media details.
	 * @throws MediaDaoException if any exception occurs while performing this operation.
	 */
	public List<Media> getMediaList(Set<String> mediaIdkeySet, String tenantId) throws MediaDaoException;

	/**
	 * This method is used to delete all the media from the database.
	 * @param mediaIdkeySet the media Ids to be deleted.
	 * @param connection if this is to be performed in one transaction.
	 * @param tenantId the database name.
	 * @throws MediaDaoException if any exception occurs while performing this operation.
	 */

	public void deleteMediaList(Connection connection, String tenantId, List<String> mediaIdList) throws MediaDaoException;
}
