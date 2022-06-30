package com.pwc.grading.reportingdb.model;

/**
 * A POJO class which is having the details to delete the Rating forms from
 * the reporting tables.
 *
 */
public class ReportDbDeleteRating {
	private String projectId;
	private String partnerId;
	private String tcId;
	private String partnerProjectId;

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

	public String getTcId() {
		return tcId;
	}

	public void setTcId(String tcId) {
		this.tcId = tcId;
	}

	public String getPartnerProjectId() {
		return partnerProjectId;
	}

	public void setPartnerProjectId(String partnerProjectId) {
		this.partnerProjectId = partnerProjectId;
	}
}
