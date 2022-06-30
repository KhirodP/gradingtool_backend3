package com.pwc.grading.masterdata.model;

/**
 * A class having all the details of a Project Mapping.
 *
 */
public class ProjectMapping {
	private String projectMappingId;
	private String partnerProjectId;
	private String projectId;
	private String partnerId;
	private String tcId;




	public String getProjectMappingId() {
		return projectMappingId;
	}

	public void setProjectMappingId(String projectMappingId) {
		this.projectMappingId = projectMappingId;
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

	@Override
	public String toString() {
		return "ProjectMapping [projectMappingId=" + projectMappingId + ", partnerProjectId=" + partnerProjectId
				+ ", projectId=" + projectId + ", partnerId=" + partnerId + ", tcId=" + tcId  + "]";
	}

}
