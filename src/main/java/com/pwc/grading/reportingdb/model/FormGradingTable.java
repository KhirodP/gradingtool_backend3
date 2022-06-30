package com.pwc.grading.reportingdb.model;

/**
 * A POJO which is having the details of the FormGradingTable table.
 *
 */
public class FormGradingTable {

	private String id;
	private String formId;
	private String partnerProjectId;
	private String formName;
	private int maxMarks;
	private int saScore;
	private int faScore;
	private int variance;
	private boolean status;
	private String piaDateAssigned;
	private String piaDateCompletion;
	private String piaStartTime;
	private String piaEndTime;
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
	
	public FormGradingTable() {
		
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

	public String getPiaDateAssigned() {
		return piaDateAssigned;
	}

	public String getPiaDateCompletion() {
		return piaDateCompletion;
	}

	public String getPiaStartTime() {
		return piaStartTime;
	}

	public String getPiaEndTime() {
		return piaEndTime;
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

	public void setPiaDateAssigned(String piaDateAssigned) {
		this.piaDateAssigned = piaDateAssigned;
	}

	public void setPiaDateCompletion(String piaDateCompletion) {
		this.piaDateCompletion = piaDateCompletion;
	}

	public void setPiaStartTime(String piaStartTime) {
		this.piaStartTime = piaStartTime;
	}

	public void setPiaEndTime(String piaEndTime) {
		this.piaEndTime = piaEndTime;
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
		return "FormGradingTable [id=" + id + ", formId=" + formId + ", partnerProjectId=" + partnerProjectId
				+ ", formName=" + formName + ", maxMarks=" + maxMarks + ", saScore=" + saScore + ", faScore=" + faScore
				+ ", variance=" + variance + ", status=" + status + ", piaDateAssigned=" + piaDateAssigned
				+ ", piaDateCompletion=" + piaDateCompletion + ", piaStartTime=" + piaStartTime + ", piaEndTime="
				+ piaEndTime + ", faName=" + faName + ", faPhone=" + faPhone + ", secondaryAuditorName="
				+ secondaryAuditorName + ", faLocation=" + faLocation + ", faDateAssigned=" + faDateAssigned
				+ ", faDateCompleted=" + faDateCompleted + ", faStartTime=" + faStartTime + ", faEndTime=" + faEndTime
				+ ", signoffTime=" + signoffTime + ", otp=" + otp + "]";
	}
	
	
	
}
