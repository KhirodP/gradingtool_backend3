package com.pwc.grading.usertoken.model;

/**
 * A class having all the details related to User Log Tracking.
 *
 */
public class UserLogTracking {
	private String userId;
	private long loginTime;
	private long logOutTime;
	private String sessionId;

	public UserLogTracking(String userId, long loginTime, long logOutTime, String sessionId) {
		super();
		this.userId = userId;
		this.loginTime = loginTime;
		this.logOutTime = logOutTime;
		this.sessionId = sessionId;
	}

	public UserLogTracking() {
		super();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public long getLogOutTime() {
		return logOutTime;
	}

	public void setLogOutTime(long logOutTime) {
		this.logOutTime = logOutTime;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		return "UserLogTracking [userId=" + userId + ", loginTime=" + loginTime + ", logOutTime=" + logOutTime
				+ ", sessionId=" + sessionId + "]";
	}

}
