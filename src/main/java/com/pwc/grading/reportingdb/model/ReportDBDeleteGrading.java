package com.pwc.grading.reportingdb.model;

/**
 * A POJO class which is having the details to delete the grading forms from
 * the reporting tables.
 *
 */
public class ReportDBDeleteGrading {

	private String projectId;
	private String partnerId;
	private String formId;
	private String partnerProjectId;
	
	public ReportDBDeleteGrading() {
	
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

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getPartnerProjectId() {
		return partnerProjectId;
	}

	public void setPartnerProjectId(String partnerProjectId) {
		this.partnerProjectId = partnerProjectId;
	}

	
	
	
}
