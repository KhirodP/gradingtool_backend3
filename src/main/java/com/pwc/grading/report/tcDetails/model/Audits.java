package com.pwc.grading.report.tcDetails.model;

public class Audits {
	private String fieldAuditorName;
	private String formName;
	private String secondaryAuditorName;

	private String auditDate;

	public String getFieldAuditorName() {
		return fieldAuditorName;
	}

	public void setFieldAuditorName(String fieldAuditorName) {
		this.fieldAuditorName = fieldAuditorName;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getSecondaryAuditorName() {
		return secondaryAuditorName;
	}

	public void setSecondaryAuditorName(String secondaryAuditorName) {
		this.secondaryAuditorName = secondaryAuditorName;
	}

	public String getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(String auditDate) {
		this.auditDate = auditDate;
	}

	@Override
	public String toString() {
		return "Audits [fieldAuditorName=" + fieldAuditorName + ", formName=" + formName + ", secondaryAuditorName="
				+ secondaryAuditorName + ", auditDate=" + auditDate + "]";
	}

}
