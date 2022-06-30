package com.pwc.grading.reportingdb.model;

/**
 * A POJO class which is having the Center-In-Charge details which is to be updated
 * in the reporting tables.
 *
 */
public class ReportDBCenterIncharge {

	private String projectId;
	private String partnerId;
	private String tcid;
	private String centerInchargeName;
	private String centerInchargePhone;
	private String partnerProjectId;
	
	public ReportDBCenterIncharge() {
		super();
	}

	public String getProjectId() {
		return projectId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public String getTcid() {
		return tcid;
	}

	public String getCenterInchargeName() {
		return centerInchargeName;
	}

	public String getCenterInchargePhone() {
		return centerInchargePhone;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public void setTcid(String tcid) {
		this.tcid = tcid;
	}

	public void setCenterInchargeName(String centerInchargeName) {
		this.centerInchargeName = centerInchargeName;
	}

	public void setCenterInchargePhone(String centerInchargePhone) {
		this.centerInchargePhone = centerInchargePhone;
	}
	
	public String getPartnerProjectId() {
		return partnerProjectId;
	}

	public void setPartnerProjectId(String partnerProjectId) {
		this.partnerProjectId = partnerProjectId;
	}

	@Override
	public String toString() {
		return "ReportDBCenterIncharge [projectId=" + projectId + ", partnerId=" + partnerId + ", tcid=" + tcid
				+ ", centerInchargeName=" + centerInchargeName + ", centerInchargePhone=" + centerInchargePhone
				+ ", partnerProjectId=" + partnerProjectId + "]";
	}

	
	
}
