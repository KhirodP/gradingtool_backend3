package com.pwc.grading.project.model;

import java.util.List;

/**
 * A class having all the details of the form
 *
 */
public class FormData {
	private String formId;
	private String projectId;
	private String formName;
	private long createdTime;
	private String surveyId;
	private List<String> usersRolesAllowed;
	private boolean publish;

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

	public List<String> getUsersRolesAllowed() {
		return usersRolesAllowed;
	}

	public void setUsersRolesAllowed(List<String> usersRolesAllowed) {
		this.usersRolesAllowed = usersRolesAllowed;
	}

	public boolean isPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}

	@Override
	public String toString() {
		return "FormData [formId=" + formId + ", projectId=" + projectId + ", formName=" + formName + ", surveyId="
				+ surveyId + ", usersRolesAllowed=" + usersRolesAllowed + "]";
	}

}
