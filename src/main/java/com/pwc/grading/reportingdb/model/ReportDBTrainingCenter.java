package com.pwc.grading.reportingdb.model;

/**
 * A POJO class which is having the Training Center details which is to be updated
 * in the reporting tables.
 *
 */
public class ReportDBTrainingCenter {

	private String tcId;
	private String tcName;
	private String centerAddress;
	private String district;
	private String centerLocation;

	private String partnerProjectId;
	private String partnerId;
	private String projectId;
	
	public ReportDBTrainingCenter() {
		super();
	}

	public String getTcId() {
		return tcId;
	}

	public String getTcName() {
		return tcName;
	}

	public String getCenterAddress() {
		return centerAddress;
	}

	public String getDistrict() {
		return district;
	}

	public String getCenterLocation() {
		return centerLocation;
	}

	public void setTcId(String tcId) {
		this.tcId = tcId;
	}

	public void setTcName(String tcName) {
		this.tcName = tcName;
	}

	public void setCenterAddress(String centerAddress) {
		this.centerAddress = centerAddress;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public void setCenterLocation(String centerLocation) {
		this.centerLocation = centerLocation;
	}

	public String getPartnerProjectId() {
		return partnerProjectId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setPartnerProjectId(String partnerProjectId) {
		this.partnerProjectId = partnerProjectId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	@Override
	public String toString() {
		return "ReportDBTrainingCenter [tcId=" + tcId + ", tcName=" + tcName + ", centerAddress=" + centerAddress
				+ ", district=" + district + ", centerLocation=" + centerLocation + "]";
	}

		
}
