package com.pwc.grading.partner.model;

/**
 * A class holds the details of the training center.
 *
 */
public class TrainingCenterDetails {
	private String partnerId;
	private String tcId;
	private String centerAddress;
	private String district;
	private String centerInchargeId;
	private String longitude;
	private String latitude;
	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	private String tcName;

	public String getTcName() {
		return tcName;
	}

	public void setTcName(String tcName) {
		this.tcName = tcName;
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

//	public String getTcName() {
//		return tcName;
//	}
//
//	public void setTcName(String tcName) {
//		this.tcName = tcName;
//	}

	public String getCenterInchargeId() {
		return centerInchargeId;
	}

	public void setCenterInchargeId(String centerInchargeId) {
		this.centerInchargeId = centerInchargeId;
	}

	@Override
	public String toString() {
		return "TrainingCenterDetails [partnerId=" + partnerId + ", tcId=" + tcId + ", centerAddress=" + centerAddress
				+ ", district=" + district + ", centerInchargeId=" + centerInchargeId + ", longitude=" + longitude
				+ ", latitude=" + latitude + ", tcName=" + tcName + "]";
	}

}
