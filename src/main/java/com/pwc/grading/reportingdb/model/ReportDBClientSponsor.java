package com.pwc.grading.reportingdb.model;

/**
 * A POJO class which is having the ClientSponsor details which is to be updated
 * in the reporting tables.
 *
 */
public class ReportDBClientSponsor {

	private String projectId;
	private String partnerId;
	private String csName;
	private String csPhone;
	private String csEmail;
	
	public ReportDBClientSponsor() {
		super();
	}

	public String getProjectId() {
		return projectId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public String getCsName() {
		return csName;
	}

	public String getCsPhone() {
		return csPhone;
	}

	public String getCsEmail() {
		return csEmail;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public void setCsName(String csName) {
		this.csName = csName;
	}

	public void setCsPhone(String csPhone) {
		this.csPhone = csPhone;
	}

	public void setCsEmail(String csEmail) {
		this.csEmail = csEmail;
	}
	
	
}
