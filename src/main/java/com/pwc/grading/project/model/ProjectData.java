package com.pwc.grading.project.model;

/**
 * A class having all the details of project data
 *
 */
public class ProjectData {
	private String projectId;
	private String projectName;
	private String projectDescription;
	private long startDate;
	private long endDate;
	private long selfAssignmentDeadline;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "ProjectData [projectId=" + projectId + ", projectName=" + projectName + ", projectDescription="
				+ projectDescription + ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}

	public long getSelfAssignmentDeadline() {
		return selfAssignmentDeadline;
	}

	public void setSelfAssignmentDeadline(long selfAssignmentDeadline) {
		this.selfAssignmentDeadline = selfAssignmentDeadline;
	}

}
