package com.pwc.grading.usertoken.model;

/**
 * A class having all the details related to token payload after decrypting JWT token.
 *
 */
public class TokenPayload {

	private String tenantId;
	private String userFirstName;
	private String userLastName;
	private String userEmail;
	private String userRole;
	private String sessionId;
	private boolean verification;
	private String tenantKey;

	public TokenPayload() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TokenPayload(String tenantId, String userFirstName, String userLastName, String userEmail, String userRole) {
		super();
		this.tenantId = tenantId;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
		this.userEmail = userEmail;
		this.userRole = userRole;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	@Override
	public String toString() {
		return "TokenPayload [tenantId=" + tenantId + ", userFirstName=" + userFirstName + ", userLastName="
				+ userLastName + ", userEmail=" + userEmail + ", userRole=" + userRole + ", sessionId=" + sessionId
				+ ", verification=" + verification + "]";
	}

	public boolean getVerification() {
		return verification;
	}

	public void setVerification(boolean verification) {
		this.verification = verification;
	}

	public String getTenantKey() {
		return tenantKey;
	}

	public void setTenantKey(String tenantKey) {
		this.tenantKey = tenantKey;
	}

}
