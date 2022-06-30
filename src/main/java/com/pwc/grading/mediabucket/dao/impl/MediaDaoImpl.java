package com.pwc.grading.mediabucket.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.mediabucket.dao.IMediaDao;
import com.pwc.grading.mediabucket.dao.MediaDaoConstant;
import com.pwc.grading.mediabucket.dao.MediaQueryConstants;
import com.pwc.grading.mediabucket.dao.exception.MediaDaoException;
import com.pwc.grading.mediabucket.model.Media;

/**
 * Implementation class for {@link IMediaDao} 
 *
 */
@Singleton
public class MediaDaoImpl implements IMediaDao {

	private static final Logger logger = LoggerFactory.getLogger(MediaDaoImpl.class);

	/**
	 * This method is used to upload the media to the database.
	 * @param databaseName the database name.
	 * @param media the media object having media details.
	 * @return the uploaded Media-Id.
	 * @throws MediaDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public String uploadMedia(String databaseName, Media media) throws MediaDaoException {
		Connection connection = null;
		PreparedStatement preparedStatment = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			preparedStatment = connection.prepareStatement(
					MediaQueryConstants.INSERT_MEDIA.replace(MediaQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			String mediaId = UUID.randomUUID().toString();
			InputStream inputStream = media.getMediaInputStream();
			String fileName = media.getMediaName();
			String mediaType = media.getMediaType();
			long createdDate = new Date().getTime();
			preparedStatment.setString(1, mediaId);
			preparedStatment.setString(2, fileName);
			preparedStatment.setLong(3, createdDate);
			preparedStatment.setString(4, mediaType);
			preparedStatment.setBinaryStream(5, inputStream);
			preparedStatment.setBinaryStream(6, media.getThumbnailInputStream());
			int executeUpdate = preparedStatment.executeUpdate();
			logger.debug(executeUpdate + " row added in media table successfully");
			return mediaId;
		} catch (Exception e) {
			logger.error("unable to add Media  " + e.getMessage());
			throw new MediaDaoException("unable to add Media " + e.getMessage());
		} finally {
			logger.debug("closing the Media connections");
			MSSqlServerUtill.close(preparedStatment, connection);
		}
	}

	/**
	 * This method is used to delete the media from the database.
	 * @param id the media to be deleted
	 * @param tenantId the database name.
	 * @throws MediaDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public void deleteMedia(String id, String tenantId) throws MediaDaoException {
		Connection connection = null;
		PreparedStatement preparedStatment = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			preparedStatment = connection.prepareStatement(MediaQueryConstants.DELETE_MEDIA_BY_MEDIA_ID
					.replace(MediaQueryConstants.DATA_BASE_PLACE_HOLDER, tenantId));
			preparedStatment.setString(1, id);
			int count = preparedStatment.executeUpdate();
			logger.debug(count + " row deleted in media");
		} catch (Exception e) {
			logger.error("unable to delete Media  " + e.getMessage());
			throw new MediaDaoException("unable to delete Media " + e.getMessage());
		} finally {
			logger.debug("closing the Media connections");
			MSSqlServerUtill.close(preparedStatment, connection);
		}
	}

	/**
	 * This method is used to get the media from the database.
	 * @param id the media to be fetched.
	 * @param dataBaseName the database name.
	 * @return  the media object having media details.
	 * @throws MediaDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public Media getMedia(String id, String databaseName) throws MediaDaoException {
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement preparedStatment = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			preparedStatment = connection.prepareStatement(MediaQueryConstants.SELECT_MEDIA_BY_MEDIA_ID
					.replace(MediaQueryConstants.DATA_BASE_PLACE_HOLDER, databaseName));
			preparedStatment.setString(1, id);
			resultSet = preparedStatment.executeQuery();
			if (resultSet.next()) {
				Media media = buildMediaFromResultSet(resultSet);
				return media;
			}
		} catch (Exception e) {
			logger.error("unable to get Media  " + e.getMessage());
			throw new MediaDaoException("unable to get Media " + e.getMessage());
		} finally {
			logger.debug("closing the Media connections");
			MSSqlServerUtill.close(resultSet, preparedStatment, connection);
		}

		return null;
	}

	private Media buildMediaFromResultSet(ResultSet resultSet) throws SQLException, IOException {
		Media media = new Media();
		String mediaId = resultSet.getString(MediaDaoConstant.MEDIA_ID);
		String mediaName = resultSet.getString(MediaDaoConstant.MEDIA_NAME);
		String mediaType = resultSet.getString(MediaDaoConstant.FILE_TYPE);
		long uploadDate = resultSet.getLong(MediaDaoConstant.UPLOAD_DATE);
		byte[] bytes = resultSet.getBytes(MediaDaoConstant.INPUTSTREAM);
		byte[] thnBytes = resultSet.getBytes(MediaDaoConstant.THUMBNAIL_INPUTSTREAM);
		ByteArrayInputStream imageBytes = new ByteArrayInputStream(bytes);
		ByteArrayInputStream thumbnailByteArray = null;
		media.setMediaId(mediaId);
		media.setMediaName(mediaName);
		media.setMediaType(mediaType);
		media.setUploadDate(uploadDate);
		media.setMediaInputStream(imageBytes);
		if (thnBytes != null && thnBytes.length > 0) { // Thumbnail may (or) may not be Present.
			thumbnailByteArray = new ByteArrayInputStream(thnBytes);
		}
		media.setThumbnailInputStream(thumbnailByteArray);
//		imageBytes.close();
		return media;
	}

	/**
	 * This method is used to get all the media from the database.
	 * @param mediaIdkeySet the media Ids to be fetched.
	 * @param tenantId the database name.
	 * @return all the media objects having media details.
	 * @throws MediaDaoException if any exception occurs while performing this operation.
	 */
	@Override
	public List<Media> getMediaList(Set<String> mediaIdkeySet, String dataBaseName) throws MediaDaoException {
		Connection connection = null;
		ResultSet resultSet = null;
		String inStr = covertSetToInString(mediaIdkeySet);
		if (inStr.isEmpty()) {
			return new ArrayList<>();
		}
		PreparedStatement preparedStatment = null;
		try {
			String selectQuery = MediaQueryConstants.SELECT_MEDIA_LIST_MEDIA_ID
					.replace(MediaQueryConstants.DATA_BASE_PLACE_HOLDER, dataBaseName) + inStr;
			logger.debug("Select media list query : " + selectQuery);
			connection = MSSqlServerUtill.getConnection();
			preparedStatment = connection.prepareStatement(selectQuery);
			resultSet = preparedStatment.executeQuery();
			List<Media> mediaList = buildMediaListFromResultSet(resultSet);
			return mediaList;
		} catch (Exception e) {
			logger.error("unable to get Media List  " + e.getMessage());
			throw new MediaDaoException("unable to get Media List" + e.getMessage());
		} finally {
			logger.debug("closing the Media connections");
			MSSqlServerUtill.close(resultSet, preparedStatment, connection);
		}
	}

	private String covertSetToInString(Set<String> mediaIdList) {
		if (mediaIdList != null && mediaIdList.size() > 0) {
			String inStr = "";
			String content = "";
			for (String mediaId : mediaIdList) {
				content += "'" + mediaId + "',";
			}
			content = content.substring(0, content.length() - 1);
			inStr = "(" + content + ")";
			return inStr;
		} else {
			return "";
		}
	}
	
	private String covertListToInString(List<String> mediaIdList) {
		if (mediaIdList != null && mediaIdList.size() > 0) {
			String inStr = "";
			String content = "";
			for (String mediaId : mediaIdList) {
				content += "'" + mediaId + "',";
			}
			content = content.substring(0, content.length() - 1);
			inStr = "(" + content + ")";
			return inStr;
		} else {
			return "";
		}
	}


	private List<Media> buildMediaListFromResultSet(ResultSet resultSet) throws SQLException {
		logger.debug("inside buildMediaListFromResultSet method of MediaDaoImpl");
		List<Media> mediaList = new ArrayList<>();
		while (resultSet.next()) {
			Media media = new Media();
			String mediaId = resultSet.getString(MediaDaoConstant.MEDIA_ID);
			String mediaName = resultSet.getString(MediaDaoConstant.MEDIA_NAME);
			String mediaType = resultSet.getString(MediaDaoConstant.FILE_TYPE);
			long uploadDate = resultSet.getLong(MediaDaoConstant.UPLOAD_DATE);
			byte[] imgBytes = resultSet.getBytes(MediaDaoConstant.INPUTSTREAM);
			byte[] thnBytes = resultSet.getBytes(MediaDaoConstant.THUMBNAIL_INPUTSTREAM);
			ByteArrayInputStream b = new ByteArrayInputStream(imgBytes);
			ByteArrayInputStream t = null;
			if (thnBytes != null && thnBytes.length > 0) { // Thumbnail may (or) may not be Present.
				t = new ByteArrayInputStream(thnBytes);
			}
			media.setMediaId(mediaId);
			media.setMediaName(mediaName);
			media.setMediaType(mediaType);
			media.setUploadDate(uploadDate);
			media.setMediaInputStream(b);
			media.setThumbnailInputStream(t);
			mediaList.add(media);
		}
		return mediaList;
	}

	@Override
	public void deleteMediaList(Connection connection, String dataBaseName, List<String> mediaIdList) throws MediaDaoException {
		logger.debug("inside deleteMediaList method of MediaDaoImpl");
		if(mediaIdList.size() == 0) {
			return;
		}
		String inStr = covertListToInString(mediaIdList);
		PreparedStatement preparedStatment = null;
		try {
			String deleteQuery = MediaQueryConstants.DELETE_MEDIA_BY_MEDIA_IDS_IN
					.replace(MediaQueryConstants.DATA_BASE_PLACE_HOLDER, dataBaseName) + inStr;
			logger.debug("Delete media list query : " + deleteQuery);
			preparedStatment = connection.prepareStatement(deleteQuery);
			int count = preparedStatment.executeUpdate();
			logger.debug(count + " rows deleted in Media");
		} catch (Exception e) {
			logger.error("Unable to delete Media List  " + e.getMessage());
			throw new MediaDaoException("Unable to delete Media List" + e.getMessage());
		} finally {
			logger.debug("closing the Media preparedStatment ");
			MSSqlServerUtill.close(preparedStatment, null);
		}
		
	}
}
