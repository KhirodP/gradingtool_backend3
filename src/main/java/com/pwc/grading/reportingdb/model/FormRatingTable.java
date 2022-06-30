package com.pwc.grading.reportingdb.model;

/**
 * A POJO which is having the details of the FormRatingTable table.
 *
 */
public class FormRatingTable {

	private String id;
	private String formId;
	private String partnerProjectId;
	private String formName;
	private String tcName;
	private String tcId;
	private String centerAddress;
	private String district;
	private String centerInchargeName;
	private String centerInchargePhone;
	private String centerLocation;
	private int maxMarks;
	private int saScore;
	private int faScore;
	private int variance;
	private boolean status;
	private String saDateAssigned;
	private String saDateCompletion;
	private String saStartTime;
	private String saEndTime;
	private String faName;
	private String faPhone;
	private String secondaryAuditorName;
	private String faLocation;
	private String faDateAssigned;
	private String faDateCompleted;
	private String faStartTime;
	private String faEndTime;
	private String signoffTime;
	private String otp;
	
	public FormRatingTable() {
		
	}

	public String getId() {
		return id;
	}

	public String getFormId() {
		return formId;
	}

	public String getPartnerProjectId() {
		return partnerProjectId;
	}

	public String getFormName() {
		return formName;
	}

	public String getTcName() {
		return tcName;
	}

	public String getTcId() {
		return tcId;
	}

	public String getCenterAddress() {
		return centerAddress;
	}

	public String getCenterInchargeName() {
		return centerInchargeName;
	}

	public String getCenterInchargePhone() {
		return centerInchargePhone;
	}

	public String getCenterLocation() {
		return centerLocation;
	}


	public int getMaxMarks() {
		return maxMarks;
	}

	public int getSaScore() {
		return saScore;
	}

	public int getFaScore() {
		return faScore;
	}

	public int getVariance() {
		return variance;
	}

	public boolean getStatus() {
		return status;
	}

	public String getSaDateAssigned() {
		return saDateAssigned;
	}

	public String getSaDateCompletion() {
		return saDateCompletion;
	}

	public String getSaStartTime() {
		return saStartTime;
	}

	public String getSaEndTime() {
		return saEndTime;
	}

	public String getFaName() {
		return faName;
	}

	public String getFaPhone() {
		return faPhone;
	}

	public String getSecondaryAuditorName() {
		return secondaryAuditorName;
	}

	public String getFaLocation() {
		return faLocation;
	}

	public String getFaDateAssigned() {
		return faDateAssigned;
	}

	public String getFaDateCompleted() {
		return faDateCompleted;
	}

	public String getFaStartTime() {
		return faStartTime;
	}

	public String getFaEndTime() {
		return faEndTime;
	}

	public String getSignoffTime() {
		return signoffTime;
	}

	public String getOtp() {
		return otp;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public void setPartnerProjectId(String partnerProjectId) {
		this.partnerProjectId = partnerProjectId;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}
	
	public void setTcName(String tcName) {
		this.tcName = tcName;
	}

	public void setTcId(String tcId) {
		this.tcId = tcId;
	}

	public void setCenterAddress(String centerAddress) {
		this.centerAddress = centerAddress;
	}

	public void setCenterInchargeName(String centerInchargeName) {
		this.centerInchargeName = centerInchargeName;
	}

	public void setCenterInchargePhone(String centerInchargePhone) {
		this.centerInchargePhone = centerInchargePhone;
	}

	public void setCenterLocation(String centerLocation) {
		this.centerLocation = centerLocation;
	}

	public void setMaxMarks(int maxMarks) {
		this.maxMarks = maxMarks;
	}

	public void setSaScore(int saScore) {
		this.saScore = saScore;
	}

	public void setFaScore(int faScore) {
		this.faScore = faScore;
	}

	public void setVariance(int variance) {
		this.variance = variance;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void setSaDateAssigned(String saDateAssigned) {
		this.saDateAssigned = saDateAssigned;
	}

	public void setSaDateCompletion(String saDateCompletion) {
		this.saDateCompletion = saDateCompletion;
	}

	public void setSaStartTime(String saStartTime) {
		this.saStartTime = saStartTime;
	}

	public void setSaEndTime(String saEndTime) {
		this.saEndTime = saEndTime;
	}

	public void setFaName(String faName) {
		this.faName = faName;
	}

	public void setFaPhone(String faPhone) {
		this.faPhone = faPhone;
	}

	public void setSecondaryAuditorName(String secondaryAuditorName) {
		this.secondaryAuditorName = secondaryAuditorName;
	}

	public void setFaLocation(String faLocation) {
		this.faLocation = faLocation;
	}

	public void setFaDateAssigned(String faDateAssigned) {
		this.faDateAssigned = faDateAssigned;
	}

	public void setFaDateCompleted(String faDateCompleted) {
		this.faDateCompleted = faDateCompleted;
	}

	public void setFaStartTime(String faStartTime) {
		this.faStartTime = faStartTime;
	}

	public void setFaEndTime(String faEndTime) {
		this.faEndTime = faEndTime;
	}

	public void setSignoffTime(String signoffTime) {
		this.signoffTime = signoffTime;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
	
	@Override
	public String toString() {
		return "FormRatingTable [id=" + id + ", formId=" + formId + ", partnerProjectId=" + partnerProjectId
				+ ", formName=" + formName + ", tcName=" + tcName + ", tcId=" + tcId + ", centerAddress="
				+ centerAddress + ", centerInchargeName=" + centerInchargeName + ", centerInchargePhone="
				+ centerInchargePhone + ", centerLocation=" + centerLocation + ", maxMarks=" + maxMarks + ", saScore="
				+ saScore + ", faScore=" + faScore + ", variance=" + variance + ", status=" + status
				+ ", saDateAssigned=" + saDateAssigned + ", saDateCompletion=" + saDateCompletion + ", saStartTime="
				+ saStartTime + ", saEndTime=" + saEndTime + ", faName=" + faName + ", faPhone=" + faPhone
				+ ", secondaryAuditorName=" + secondaryAuditorName + ", faLocation=" + faLocation + ", faDateAssigned="
				+ faDateAssigned + ", faDateCompleted=" + faDateCompleted + ", faStartTime=" + faStartTime
				+ ", faEndTime=" + faEndTime + ", signoffTime=" + signoffTime + ", otp=" + otp + "]";
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

}
