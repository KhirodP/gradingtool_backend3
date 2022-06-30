package com.pwc.grading.report.partner.model;

public class Audits {
	private String tcName;

	private String fieldAuditorName;

	private String formName;

	private String secondaryAuditorName;

	private String auditDate;

	public String getTcName() {
		return tcName;
	}

	public void setTcName(String tcName) {
		this.tcName = tcName;
	}

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
		return "Audits [tcName=" + tcName + ", fieldAuditorName=" + fieldAuditorName + ", formName=" + formName
				+ ", secondaryAuditorName=" + secondaryAuditorName + ", auditDate=" + auditDate + "]";
	}

}
