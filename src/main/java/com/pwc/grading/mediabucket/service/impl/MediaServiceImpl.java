package com.pwc.grading.mediabucket.service.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.mediabucket.dao.IMediaDao;
import com.pwc.grading.mediabucket.model.Media;
import com.pwc.grading.mediabucket.service.IMediaService;
import com.pwc.grading.mediabucket.service.MediaServiceConstants;
import com.pwc.grading.mediabucket.service.exception.MediaServiceException;
import com.pwc.grading.util.JsonUtill;
import com.pwc.grading.util.ReadPropertiesFile;
/**
 * Implementation class for {@link IMediaService}
 *
 */
@Singleton
public class MediaServiceImpl implements IMediaService {
	private static final Logger logger = LoggerFactory.getLogger(MediaServiceImpl.class);
	private static final String UPLOAD_MEDIA_ID = "uploadMediaId";
	private static final String STATUS = "status";
	@Inject
	private IMediaDao mediaBucket;

	public MediaServiceImpl() {
	}

	/**
	 * This method is used to get the media.
	 * @param id the media to be fetched.
	 * @param tenantId the database name.
	 * @return the media object having media details.
	 * @throws MediaServiceException if any exception occurs while performing this operation.
	 */
	@Override
	public Media getMedia(String id, String tenantId) throws MediaServiceException {
		logger.debug("inside getMedia By id in MediaBucketService");
		try {
			if (id.isEmpty() || id == null) {
				logger.error("id cant be null or empty ");
				throw new MediaServiceException("invalid id");
			}
			Media media = mediaBucket.getMedia(id, tenantId);
			logger.debug("Media exist and Media is " + media);
			if (media == null) {
				logger.error("in Valid id ");
				throw new MediaServiceException("invalid id");
			}
			return media;
		} catch (Exception e) {
			logger.error("unable to get media " + e.getMessage());
			throw new MediaServiceException("unable to get media " + e.getMessage());
		}
	}

	/**
	 * This method is used to delete the media.
	 * @param id the media to be deleted.
	 * @param tenantId the database name.
	 * @return the success response if media is deleted.
	 * @throws MediaServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deleteMedia(String id, String tenantId) throws MediaServiceException {
		logger.debug("inside deleteMedia By id in MediaBucketService");
		try {
			if (id == null || id.isEmpty()) {
				logger.error("id cant be null or empty ");
				throw new MediaServiceException("id cant be null or empty ");
			}
			mediaBucket.deleteMedia(id, tenantId);
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("msg", ReadPropertiesFile.readResponseProperty("215"));
			return responseJSON.toString();
		} catch (Exception e) {
			logger.error("unable to delete media " + e.getMessage());
			throw new MediaServiceException("unable to delete media " + e.getMessage());
		}

	}

	/**
	 * This method is used to upload the media.
	 * @param tenantId the database name.
	 * @param jsonStringReq the media details.
	 * @return the success response if media is uploaded.
	 * @throws MediaServiceException if any exception occurs while performing this operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String uploadMedia(String tenantId, String jsonStringReq) throws MediaServiceException {
		try {
			Media media = new Media();
			JSONObject jsonRequest = (JSONObject) JSONValue.parseWithException(jsonStringReq);
			String fileName = JsonUtill.getString(jsonRequest, MediaServiceConstants.MEDIA_NAME);
			String data = JsonUtill.getString(jsonRequest, MediaServiceConstants.MEDIA_DATA);
			String type = JsonUtill.getString(jsonRequest, MediaServiceConstants.MEDIA_TYPE);

			byte[] decode = Base64.getDecoder().decode(data);
			String filePatter = UUID.randomUUID().toString() + "_" + fileName;
			File imgFile = new File(filePatter);
			logger.debug("");
			imgFile.createNewFile();

			FileOutputStream fos = new FileOutputStream(imgFile);
			fos.write(decode);
			fos.flush();
			fos.close();
			InputStream inputStream = new FileInputStream(imgFile);
			InputStream tnInputStream = null;
			File thumbnailFile = null;
			if (type.equalsIgnoreCase(MediaServiceConstants.IMAGE)) {
				logger.debug("MediaType is Image, Getting thumbnail properties..[width, height]");
				String width = ReadPropertiesFile.readRequestProperty(MediaServiceConstants.THUMBNAIL_WIDTH_PROP);
				String height = ReadPropertiesFile.readRequestProperty(MediaServiceConstants.THUMBNAIL_HEIGHT_PROP);
				int intWidth = Integer.parseInt(width);
				int intHeight = Integer.parseInt(height);
				logger.debug("Creating thumbnail of [width x height] = [" + width + " x " + height + "]");
				thumbnailFile = createThumbnail(imgFile, intWidth, intHeight);
				tnInputStream = new FileInputStream(thumbnailFile);
			}
			media.setMediaName(fileName);
			media.setMediaType(type);
			media.setMediaInputStream(inputStream);
			media.setThumbnailInputStream(tnInputStream);
			logger.debug("current media is " + media);
			String uploadMediaId = mediaBucket.uploadMedia(tenantId, media);
			logger.debug("adding media to db is completed");
			//Closing the opened streams, for file Deletion, if not closed, File deletion WILL NOT happen.
			inputStream.close();
			if(tnInputStream != null) {
				tnInputStream.close();
			}
			
			//Proceeding to Delete the files Created.
			boolean imgDelete = imgFile.delete();
			logger.debug("$$$ Media is deleted: " + imgDelete);
			if (thumbnailFile != null) {
				boolean thumbnailDelete = thumbnailFile.delete();
				logger.debug(" $$$ Media(Image) Thumbnail is deleted: " + thumbnailDelete);
			}			
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put(STATUS, ReadPropertiesFile.readResponseProperty("216"));
			jsonResponse.put(UPLOAD_MEDIA_ID, uploadMediaId);
			logger.debug("jsonResponse is ::" + jsonResponse.toString());
			return jsonResponse.toString();
		} catch (Exception e) {
			logger.error("unable to add media " + e.getMessage());
			throw new MediaServiceException("unable to add media ---" + e.getMessage());
		}
	}

	/**
	 * This method is used to get the template files for
	 * UserAccessManagement and MasterData Management
	 * @param fileType indicates which file to be fetched.
	 * @return the media object having media details.
	 * @throws MediaServiceException if any exception occurs while performing this operation.
	 */
	@Override
	public Media getResource(String fileType) throws MediaServiceException {
		logger.debug("inside getResource in MediaBucketService");
		logger.debug("Filetype is : " + fileType);
		try {
			Media media = new Media();
			if (fileType.contentEquals(MediaServiceConstants.FILE_USER_ACCESS_MANAGEMENT)) {
				String userAccessMgmtFile = ReadPropertiesFile
						.readRequestProperty(MediaServiceConstants.USER_ACCESS_MANAGEMENT_PROP);
				logger.debug("userAccessMgmtFile: " + userAccessMgmtFile);
				 
				InputStream is = getClass().getClassLoader().getResourceAsStream(userAccessMgmtFile);
				
				media.setMediaName(userAccessMgmtFile);
				media.setMediaInputStream(is);
				return media;
			} else if (fileType.contentEquals(MediaServiceConstants.FILE_MASTER_DATA_MANAGEMENT)) {
				String masterDataMgmtFile = ReadPropertiesFile
						.readRequestProperty(MediaServiceConstants.MASTER_DATA_MANAGEMENT_PROP);
				logger.debug("masterDataMgmtFile: " + masterDataMgmtFile);
				
				InputStream is = getClass().getClassLoader().getResourceAsStream(masterDataMgmtFile);
				
				media.setMediaName(masterDataMgmtFile);
				media.setMediaInputStream(is);
				return media;
			} else {
				logger.error("Invalid file type : " + fileType);
				throw new MediaServiceException("Invalid file type : " + fileType);
			}
		} catch (Exception e) {
			logger.error("unable to get resource, " + e.getMessage());
			throw new MediaServiceException("unable to get resource, " + e.getMessage());
		}
	}

	/**
	 * This method is used to get the thumbanail for the image.
	 * @param id the media whose thumbnail to be fetched.
	 * @param tenantId the database name.
	 * @return the media object having media details.
	 * @throws MediaServiceException if any exception occurs while performing this operation.
	 */
	@Override
	public Media getMediaThumbnail(String id, String tenantId) throws MediaServiceException {
		logger.debug(".inside getMediaThumbnail method of MediaServiceImpl.");
		logger.debug("Media ID is: " + id);
		try {
			if (id.isEmpty() || id == null) {
				logger.error("id cant be null or empty ");
				throw new MediaServiceException("invalid id");
			}
			Media media = mediaBucket.getMedia(id, tenantId);
			if (media == null) {
				logger.error("Media does not exist.");
				throw new MediaServiceException("Media does not exist.");
			}
			logger.debug("Media exist, and Media is: " + media);
			return media;
		} catch (Exception e) {
			logger.error("unable to get media thumbnail, " + e.getMessage());
			throw new MediaServiceException("unable to get media thumbnail, " + e.getMessage());
		}
	}

	/**
	 * This method is used to create the thumbnail if the media is an image.
	 * If its not an image, it will not create a thumbnail for it.
	 * @param inputImgFile the image file.
	 * @param thumnail_width the width of the thumbnail to be created.
	 * @param thumbnail_height the height of the thumbnail to be created.
	 * @return the created thumbnail
	 */
	private File createThumbnail(File inputImgFile, int thumnail_width, int thumbnail_height) {
		logger.debug(".inside createThumbnail method of MediaServiceImpl.");
		logger.debug("inputImgFile:" + inputImgFile);
//		logger.debug("AbsolutePath: "+inputImgFile.getAbsolutePath());
//	    logger.debug("ParentFile: "+inputImgFile.getParentFile());
		File outputFile = null;
		try {
			BufferedImage img = new BufferedImage(thumnail_width, thumbnail_height, BufferedImage.TYPE_INT_RGB);
			img.createGraphics().drawImage(
					ImageIO.read(inputImgFile).getScaledInstance(thumnail_width, thumbnail_height, Image.SCALE_SMOOTH),
					0, 0, null);
			outputFile = new File(MediaServiceConstants.THUMBNAIL_FILE_CONST + inputImgFile.getName());
			ImageIO.write(img, "jpg", outputFile);
			return outputFile;
		} catch (Exception e) {
			System.out.println("Exception while generating thumbnail " + e.getMessage());
			return null;
		}
	}

}
