package com.pwc.grading.reportingdb.model;

/**
 * A POJO class which is having the FieldAuditor details which is to be updated
 * in the reporting tables.
 *
 */
public class ReportDBFieldAuditor {

	private String projectId;
	private String partnerId;
	private String formId;
	private String faName;
	private String faPhone;
	private String partnerProjectId;
	private String tcId;
	
	
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

	public ReportDBFieldAuditor() {
		super();
	}

	public String getProjectId() {
		return projectId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public String getFormId() {
		return formId;
	}

	public String getFaName() {
		return faName;
	}

	public String getFaPhone() {
		return faPhone;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public void setFaName(String faName) {
		this.faName = faName;
	}

	public void setFaPhone(String faPhone) {
		this.faPhone = faPhone;
	}

	@Override
	public String toString() {
		return "ReportDBFieldAuditor [projectId=" + projectId + ", partnerId=" + partnerId + ", formId=" + formId
				+ ", faName=" + faName + ", faPhone=" + faPhone + "]";
	}
	
	
}
