package com.pwc.grading.controller;

import javax.inject.Inject;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.controller.exception.MediaControllerException;
import com.pwc.grading.mediabucket.model.Media;
import com.pwc.grading.mediabucket.service.IMediaService;
import com.pwc.grading.mediabucket.service.MediaServiceConstants;
import com.pwc.grading.usertoken.model.TokenPayload;
import com.pwc.grading.util.ReadPropertiesFile;
import com.pwc.grading.util.TokenValidator;
import com.pwc.grading.util.exception.TokenValidatorException;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.server.types.files.StreamedFile;

/**
 * MediaController class is responsible for adding, getting and deleting the
 * Media(pdf, doc, images ..etc)
 * 
 * * see also: {@link}{@link ReportController}{@link}{@link ProjectController}
 * 
 *
 */
@Controller("gradingTool/media")
public class MediaController {
	private static final Logger logger = LoggerFactory.getLogger(MediaController.class);

	@Inject
	private IMediaService mediaBucketService;

	/**
	 * This method is used to add Media.
	 * 
	 * @param requestBody the json containing the media details.
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return the response message of this method.
	 * @throws MediaControllerException if any exception occurs while performing this operation.
	 */
	@Post(uri = "json/upload", consumes = MediaType.APPLICATION_JSON)
	public String uploadMedia(@Body String requestBody, @Header("jwtToken") String jwtToken)
			throws MediaControllerException {
		long start = System.currentTimeMillis();
		logger.debug(" inside uploadMedia method MediaController Uploading MultipleFiles :");
		try {
			validateRequestBody(requestBody);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
							401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(), 401,
						e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String uploadMedia = mediaBucketService.uploadMedia(tenantId, requestBody);
			long end = System.currentTimeMillis();
			logger.debug("Total time to upload is ::" + (end - start) + " ms");
			return uploadMedia;
		} catch (MediaControllerException e) {
			logger.error("Unable to add media, " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error("Unable to add media, " + e.getMessage(), e);
			throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("412"), e, 400, e.getMessage());
		}
	}

	/**
	 * To validate the request body for not empty.
	 * @param requestBody the request body.
	 * @throws MediaControllerException if request body is empty.
	 */
	private void validateRequestBody(String requestBody) throws MediaControllerException {
		if (requestBody == null || requestBody.isEmpty()) {
			throw new MediaControllerException(400, "invalid request", "requestBody cant be null or empty");
		}

	}

	/**
	 * This method is used to get Media by MediaId
	 * 
	 * @param id  it is the id used to identify the Media Uniquely
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return it will get the media file downloaded.
	 * @throws MediaControllerException if any exception occurs while performing this operation.
	 */
	@Get(produces = MediaType.APPLICATION_OCTET_STREAM, uri = "/{id}")
	public HttpResponse<StreamedFile> getMediaById(@PathVariable String id, @Header("jwtToken") String jwtToken)
			throws MediaControllerException {
		logger.debug(".inside getMediaById method MediaController");
		try {
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
							401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(), 401,
						e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			Media media = mediaBucketService.getMedia(id, tenantId);
			logger.debug("in controller media " + media);
			StreamedFile streamedFile = new StreamedFile(media.getMediaInputStream(),
					MediaType.APPLICATION_OCTET_STREAM_TYPE);
			logger.debug("dispaching payload media.getMediaInputStream() " + media.getMediaInputStream());
			return HttpResponse.ok(streamedFile).header(HttpHeaders.CONTENT_DISPOSITION,
					ControllerConstants.CONTENT_DISPOSITION_VALUE + media.getMediaName());

		} catch (MediaControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Unable to get media By id, " + e.getMessage(), e);
			throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("414"), e, 400, e.getMessage());
		}

	}

	/**
	 * This method is used to delete the Media by unique id
	 * 
	 * @param id it is the id used to identify the Media Uniquely
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return  the response message of this method.
	 * @throws MediaControllerException if any exception occurs while performing this operation.
	 */
	@Delete("/{id}")
	public HttpResponse<String> deleteMediaById(@PathVariable String id, @Header("jwtToken") String jwtToken)
			throws MediaControllerException {
		try {
			logger.debug(".inside deleteMediaById method MediaController, id is ::" + id);
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
							401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(), 401,
						e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			String deleteMedia = mediaBucketService.deleteMedia(id, tenantId);
			logger.debug("response:: " + deleteMedia);
			return HttpResponse.ok().contentType(MediaType.APPLICATION_JSON).body(deleteMedia);
		} catch (MediaControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Unable to delete media By id, " + e.getMessage(), e);
			throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("411"), e, 400, e.getMessage());
		}
	}

	/**
	 * This method is used to get or download the template related to
	 * MasterDataManagment or UserAccessManagment
	 * 
	 * @param fileType - it can either MasterDataManagment file or
	 *                 UserAccessManagment file
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return  the response message of this method.
	 * @throws MediaControllerException if any exception occurs while performing this operation.
	 */
	@Get(produces = MediaType.APPLICATION_OCTET_STREAM, uri = "/resource")
	public HttpResponse<StreamedFile> getResource(@QueryValue(value = "fileType") String fileType,
			@Header("jwtToken") String jwtToken) throws MediaControllerException {
		logger.debug(".inside getResource method MediaController");
		try {
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
							401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(), 401,
						e.getMessage());
			}
			validateRequestParam(fileType);
			Media media = mediaBucketService.getResource(fileType);
			logger.debug("in controller media " + media);
			StreamedFile streamedFile = new StreamedFile(media.getMediaInputStream(),
					MediaType.APPLICATION_OCTET_STREAM_TYPE);
			logger.debug("dispaching payload media.getMediaInputStream() " + media.getMediaInputStream());
			return HttpResponse.ok(streamedFile).header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*").header(
					HttpHeaders.CONTENT_DISPOSITION,
					ControllerConstants.CONTENT_DISPOSITION_VALUE + media.getMediaName());

		} catch (MediaControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Unable to get Resource, " + e.getMessage(), e);
			throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("416"), e, 400, e.getMessage());
		}

	}

	/**
	 * This method is used to get the thumbnail of the uploaded image.
	 * @param id  is the id used to identify the Media Uniquely
	 * @param jwtToken the jwt token which is to be passed in the request header.
	 * @return  the response message of this method.
	 * @throws MediaControllerException if any exception occurs while performing this operation.
	 */
	@Get(uri = "/{id}/thumbnail")
	public HttpResponse<?> getMediaThumbnailById(@PathVariable String id, @Header("jwtToken") String jwtToken)
			throws MediaControllerException {
		logger.debug(".inside getMediaThumbnailById method MediaController");
		try {
			TokenPayload tokenPayload = null;
			try {
				tokenPayload = TokenValidator.validateJWTTokenAndGetTokenPayload(jwtToken);
				if (tokenPayload == null) {
					throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(),
							401, ReadPropertiesFile.readResponseProperty("101"));
				}
			} catch (TokenValidatorException e) {
				throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("101"), new Throwable(), 401,
						e.getMessage());
			}
			String tenantId = tokenPayload.getTenantId();
			Media media = mediaBucketService.getMediaThumbnail(id, tenantId);
			logger.debug("in controller media " + media);
			if (media.getThumbnailInputStream() != null) {
				logger.debug("Thumbnail Found..Processing thumbnail...");
				StreamedFile streamedFile = new StreamedFile(media.getThumbnailInputStream(),
						MediaType.APPLICATION_OCTET_STREAM_TYPE);
				logger.debug("dispatching thumbnail payload media.getThumbnailInputStream() :"
						+ media.getThumbnailInputStream());
				return HttpResponse.ok(streamedFile).header(HttpHeaders.CONTENT_DISPOSITION,
						ControllerConstants.CONTENT_DISPOSITION_VALUE + MediaServiceConstants.THUMBNAIL_FILE_CONST
								+ media.getMediaName());

			} else {
				logger.debug("Thumbnail NOT Found, media ID: " + id);
				JSONObject object = new JSONObject();
				return HttpResponse.ok(object.toString());
			}

		} catch (MediaControllerException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Unable to get media By id, " + e.getMessage(), e);
			throw new MediaControllerException(ReadPropertiesFile.readResponseProperty("424"), e, 400, e.getMessage());
		}

	}

	/**
	 * To validate the request parameter for not empty.
	 * @param fileType the request body.
	 * @throws MediaControllerException if fileType is empty.
	 */
	private void validateRequestParam(String fileType) throws MediaControllerException {
		if (fileType == null || fileType.isEmpty()) {
			throw new MediaControllerException(400, "invalid request", "FileType cant be null or empty");
		}

	}
}