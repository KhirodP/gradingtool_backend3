package com.pwc.grading.surveyresponse.model;

/**
 * A class which is having details of the Survey Response.
 *
 */
public class SurveyResponse {

	private String surveyResponseId;
	private String surveyId;
	private boolean isSubmited;
	private long saveTime;
	private long submitTime;
	private String userId;
	private String responderType;
	private String surveyResponseData;
	private int totalScore;
	private int maxMarks;

	private String auditFor;
	private String auditForId;
	private String partnerId;
	private String centerId;

	private String secondaryFieldAuditor;

	private String jwtToken;

	private String otp;

	public SurveyResponse() {
		super();
	}

	public int getMaxMarks() {
		return maxMarks;
	}

	public void setMaxMarks(int maxMarks) {
		this.maxMarks = maxMarks;
	}

	public String getSurveyResponseId() {
		return surveyResponseId;
	}

	public void setSurveyResponseId(String surveyResponseId) {
		this.surveyResponseId = surveyResponseId;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

	public boolean isSubmited() {
		return isSubmited;
	}

	public void setSubmited(boolean isSubmited) {
		this.isSubmited = isSubmited;
	}

	public long getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(long saveTime) {
		this.saveTime = saveTime;
	}

	public long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getResponderType() {
		return responderType;
	}

	public void setResponderType(String responderType) {
		this.responderType = responderType;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public String getSurveyResponseData() {
		return surveyResponseData;
	}

	public void setSurveyResponseData(String surveyResponseData) {
		this.surveyResponseData = surveyResponseData;
	}

	@Override
	public String toString() {
		return "SurveyResponse [surveyResponseId=" + surveyResponseId + ", surveyId=" + surveyId + ", isSubmited="
				+ isSubmited + ", saveTime=" + saveTime + ", submitTime=" + submitTime + ", userId=" + userId
				+ ", responderType=" + responderType + ", surveyResponseData=" + surveyResponseData + ", totalScore="
				+ totalScore + ", maxMarks=" + maxMarks + ", auditFor=" + auditFor + ", auditForId=" + auditForId
				+ ", partnerId=" + partnerId + ", centerId=" + centerId + ", secondaryFieldAuditor="
				+ secondaryFieldAuditor + ", jwtToken=" + jwtToken + ", otp=" + otp + "]";
	}

	public String getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}

	public String getAuditForId() {
		return auditForId;
	}

	public void setAuditForId(String auditForId) {
		this.auditForId = auditForId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public String getSecondaryFieldAuditor() {
		return secondaryFieldAuditor;
	}

	public void setSecondaryFieldAuditor(String secondaryFieldAuditor) {
		this.secondaryFieldAuditor = secondaryFieldAuditor;
	}

	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

}
