package com.pwc.grading.project.model;

/**
 * A class having all the details of project data of user
 *
 */
public class ProjectDetailOfUser {
	
	private String partnerName;
	private boolean status;
	private boolean assignedFormsCompleted;
	private ProjectData projectData;

	private String partnerId;

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public ProjectDetailOfUser(String partnerName, boolean status, ProjectData projectData) {
		super();
		this.partnerName = partnerName;
		this.status = status;
		this.projectData = projectData;
	}

	public ProjectDetailOfUser() {
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public ProjectData getProjectData() {
		return projectData;
	}

	public void setProjectData(ProjectData projectData) {
		this.projectData = projectData;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ProjectDetailOfUser [partnerName=" + partnerName + ", status=" + status + ", projectData=" + projectData
				+ "]";
	}

	public boolean isAssignedFormsCompleted() {
		return assignedFormsCompleted;
	}

	public void setAssignedFormsCompleted(boolean assignedFormsCompleted) {
		this.assignedFormsCompleted = assignedFormsCompleted;
	}

}
