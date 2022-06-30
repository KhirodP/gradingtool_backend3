package com.pwc.grading.mediabucket.model;

import java.io.InputStream;

/**
 * A class having all the details related to a media.
 *
 */
public class Media {

	private String mediaId;
	private InputStream mediaInputStream;
	private String mediaName;
	private long length;
	private String mediaType;
	private long uploadDate;
	private InputStream thumbnailInputStream;
 
	public Media() {
		super();
	}


	public long getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(long uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public InputStream getMediaInputStream() {
		return mediaInputStream;
	}

	public void setMediaInputStream(InputStream mediaInputStream) {
		this.mediaInputStream = mediaInputStream;
	}

	public String getMediaName() {
		return mediaName;
	}

	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public InputStream getThumbnailInputStream() {
		return thumbnailInputStream;
	}

	public void setThumbnailInputStream(InputStream thumbnailInputStream) {
		this.thumbnailInputStream = thumbnailInputStream;
	}

	@Override
	public String toString() {
		return "Media [mediaId=" + mediaId + ", mediaInputStream=" + mediaInputStream + ", mediaName=" + mediaName
				+ ", length=" + length + ", mediaType=" + mediaType + ", uploadDate=" + uploadDate
				+ ", thumbnailInputStream=" + thumbnailInputStream + "]";
	}

}
