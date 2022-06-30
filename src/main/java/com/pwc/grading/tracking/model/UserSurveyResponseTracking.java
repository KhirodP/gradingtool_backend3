package com.pwc.grading.tracking.model;

/**
 * A class having all the details related to the user
 * survey response tracking.
 *
 */
public class UserSurveyResponseTracking {
	private String projectId;
	private String surveyId;
	private String centerId;
	private String partnerId;
	private String auditFor;
	private String auditId;
	private double varaince;
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;
	private double latitude;
	private double longitude;

	public UserSurveyResponseTracking(String projectId, String surveyId, double varaince, String startDate,
			String startTime, String endDate, String endTime) {
		super();
		this.projectId = projectId;
		this.surveyId = surveyId;
		this.varaince = varaince;
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
	}

	public UserSurveyResponseTracking() {
		// TODO Auto-generated constructor stub
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public double getVaraince() {
		return varaince;
	}

	public void setVaraince(double varaince) {
		this.varaince = varaince;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}

	public String getAuditId() {
		return auditId;
	}

	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
