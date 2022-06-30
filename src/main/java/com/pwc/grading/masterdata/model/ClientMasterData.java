package com.pwc.grading.masterdata.model;

/**
 * A class having all the details of client master data.
 *
 */
public class ClientMasterData {
	private String isGradingEnable;
	private String projectId;
	private String partnerName;
	private String clientSponsorFirstName;
	private String clientSponsorLastName;
	private String clientSponsorId;
	private String clientSponsorContact;
	private String clientSponsorEmail;
	private String partnerProjectId;
	private String tcId;
	private String centerAddress;
	private String district;
	private String centerInchargeEmail;
	private String centerInchargeId;
	private String centerInchargeFirstName;
	private String centerInchargeLastName;
	private String centerInchargeContact;
	private String longitude;
	private String latitude;
	private String tcName;

	public String getIsGradingEnable() {
		return isGradingEnable;
	}

	public void setIsGradingEnable(String isGradingEnable) {
		this.isGradingEnable = isGradingEnable;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getClientSponsorFirstName() {
		return clientSponsorFirstName;
	}

	public void setClientSponsorFirstName(String clientSponsorFirstName) {
		this.clientSponsorFirstName = clientSponsorFirstName;
	}

	public String getClientSponsorLastName() {
		return clientSponsorLastName;
	}

	public void setClientSponsorLastName(String clientSponsorLastName) {
		this.clientSponsorLastName = clientSponsorLastName;
	}

	public String getClientSponsorId() {
		return clientSponsorId;
	}

	public void setClientSponsorId(String clientSponsorId) {
		this.clientSponsorId = clientSponsorId;
	}

	public String getClientSponsorContact() {
		return clientSponsorContact;
	}

	public void setClientSponsorContact(String clientSponsorContact) {
		this.clientSponsorContact = clientSponsorContact;
	}

	public String getClientSponsorEmail() {
		return clientSponsorEmail;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void setClientSponsorEmail(String clientSponsorEmail) {
		this.clientSponsorEmail = clientSponsorEmail;
	}

	public String getPartnerProjectId() {
		return partnerProjectId;
	}

	public void setPartnerProjectId(String partnerProjectId) {
		this.partnerProjectId = partnerProjectId;
	}

	public String getTcId() {
		return tcId;
	}

	public void setTcId(String tcId) {
		this.tcId = tcId;
	}

	public String getCenterAddress() {
		return centerAddress;
	}

	public void setCenterAddress(String centerAddress) {
		this.centerAddress = centerAddress;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCenterInchargeEmail() {
		return centerInchargeEmail;
	}

	public void setCenterInchargeEmail(String centerInchargeEmail) {
		this.centerInchargeEmail = centerInchargeEmail;
	}

	public String getCenterInchargeId() {
		return centerInchargeId;
	}

	public void setCenterInchargeId(String centerInchargeId) {
		this.centerInchargeId = centerInchargeId;
	}

	public String getCenterInchargeFirstName() {
		return centerInchargeFirstName;
	}

	public void setCenterInchargeFirstName(String centerInchargeFirstName) {
		this.centerInchargeFirstName = centerInchargeFirstName;
	}

	public String getCenterInchargeLastName() {
		return centerInchargeLastName;
	}

	public void setCenterInchargeLastName(String centerInchargeLastName) {
		this.centerInchargeLastName = centerInchargeLastName;
	}

	public String getCenterInchargeContact() {
		return centerInchargeContact;
	}

	public void setCenterInchargeContact(String centerInchargeContact) {
		this.centerInchargeContact = centerInchargeContact;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getTcName() {
		return tcName;
	}

	public void setTcName(String tcName) {
		this.tcName = tcName;
	}

	@Override
	public String toString() {
		return "ClientMasterData [isGradingEnable=" + isGradingEnable + ", projectId=" + projectId + ", partnerName="
				+ partnerName + ", clientSponsorFirstName=" + clientSponsorFirstName + ", clientSponsorLastName="
				+ clientSponsorLastName + ", clientSponsorId=" + clientSponsorId + ", clientSponsorContact="
				+ clientSponsorContact + ", clientSponsorEmail=" + clientSponsorEmail + ", partnerProjectId="
				+ partnerProjectId + ", tcId=" + tcId + ", centerAddress=" + centerAddress + ", district=" + district
				+ ", centerInchargeEmail=" + centerInchargeEmail + ", centerInchargeId=" + centerInchargeId
				+ ", centerInchargeFirstName=" + centerInchargeFirstName + ", centerInchargeLastName="
				+ centerInchargeLastName + ", centerInchargeContact=" + centerInchargeContact + ", longitude="
				+ longitude + ", latitude=" + latitude + ", tcName=" + tcName + "]";
	}

}
