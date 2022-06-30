package com.pwc.grading.usertoken.model;

/**
 * A class having all the details related to User token.
 *
 */
public class UserToken {

	private String tokenId;
	private String userId;
	private String accessToken;
	private String deviceId;
	private Long expiryTime;
	private String platform;

	public UserToken() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserToken(String tokenId, String userId, String accessToken, String deviceId, Long expiryTime,
			String platform) {
		super();
		this.tokenId = tokenId;
		this.userId = userId;
		this.accessToken = accessToken;
		this.deviceId = deviceId;
		this.expiryTime = expiryTime;
		this.platform = platform;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Long getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Long expiryTime) {
		this.expiryTime = expiryTime;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	@Override
	public String toString() {
		return "UserToken [tokenId=" + tokenId + ", userId=" + userId + ", accessToken=" + accessToken + ", deviceId="
				+ deviceId + ", expiryTime=" + expiryTime + ", platform=" + platform + "]";
	}

}
