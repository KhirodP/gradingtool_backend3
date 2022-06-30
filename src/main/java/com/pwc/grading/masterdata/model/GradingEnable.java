package com.pwc.grading.masterdata.model;

/**
 * A class having all the details of a grading enable.
 *
 */
public class GradingEnable {
	private String projectId;
	private String partnerId;
	private boolean isGradingEnable;

	public GradingEnable() {
		super();
	}

	public GradingEnable(String projectId, String partnerId, boolean isGradingEnable) {
		super();
		this.projectId = projectId;
		this.partnerId = partnerId;
		this.isGradingEnable = isGradingEnable;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public boolean isGradingEnable() {
		return isGradingEnable;
	}

	public void setGradingEnable(boolean isGradingEnable) {
		this.isGradingEnable = isGradingEnable;
	}

	@Override
	public String toString() {
		return "GradingEnable [projectId=" + projectId + ", partnerId=" + partnerId + ", isGradingEnable="
				+ isGradingEnable + "]";
	}
}
