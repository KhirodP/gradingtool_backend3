package com.pwc.grading.scheduler.model;

public class SchedulerMapping {
	private String formId;
	private String tcId;
	private String projectId;
	private String partnerId;
	private boolean selfAssignmentStatus;
	private String fieldAuditorId;
	private long auditDate;
	private boolean auditStatus;
	private boolean isAuditCancled;
//	private long selfAssessmentDeadLine;
	private String secondaryFieldAuditorName;

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

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public boolean getSelfAssignmentStatus() {
		return selfAssignmentStatus;
	}

	public void setSelfAssignmentStatus(boolean selfAssignmentStatus) {
		this.selfAssignmentStatus = selfAssignmentStatus;
	}

	public String getFieldAuditorId() {
		return fieldAuditorId;
	}

	public void setFieldAuditorId(String fieldAuditorId) {
		this.fieldAuditorId = fieldAuditorId;
	}

	public boolean isAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(boolean auditStatus) {
		this.auditStatus = auditStatus;
	}

	public boolean isAuditCancled() {
		return isAuditCancled;
	}

	public void setAuditCancled(boolean isAuditCancled) {
		this.isAuditCancled = isAuditCancled;
	}

	public long getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(long auditDate) {
		this.auditDate = auditDate;
	}

//	public long getSelfAssessmentDeadLine() {
//		return selfAssessmentDeadLine;
//	}
//
//	public void setSelfAssessmentDeadLine(long selfAssessmentDeadLine) {
//		this.selfAssessmentDeadLine = selfAssessmentDeadLine;
//	}

	public String getTcId() {
		return tcId;
	}

	public void setTcId(String tcId) {
		this.tcId = tcId;
	}

	@Override
	public String toString() {
		return "SchedulerMapping [formId=" + formId + ", tcId=" + tcId + ", projectId=" + projectId + ", partnerId="
				+ partnerId + ", selfAssignmentStatus=" + selfAssignmentStatus + ", fieldAuditorId=" + fieldAuditorId
				+ ", auditDate=" + auditDate + ", auditStatus=" + auditStatus + ", isAuditCancled=" + isAuditCancled
				+ "]";
	}

	public String getSecondaryFieldAuditorName() {
		return secondaryFieldAuditorName;
	}

	public void setSecondaryFieldAuditorName(String secondaryFieldAuditorName) {
		this.secondaryFieldAuditorName = secondaryFieldAuditorName;
	}

}
