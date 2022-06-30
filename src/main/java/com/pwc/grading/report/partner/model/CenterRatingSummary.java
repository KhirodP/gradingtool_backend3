package com.pwc.grading.report.partner.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CenterRatingSummary {
	@JsonProperty("TCSPOCName")
	private String TCSPOCName;
	@JsonProperty("SAScore")
	private String SAScore;
	@JsonProperty("TCAddress")
	private String TCAddress;
	@JsonProperty("FAScore")
	private String FAScore;
	@JsonProperty("maxMarks")
	private String maxMarks;
	@JsonProperty("trainingCenter")
	private String trainingCenter;

	public String getTCSPOCName() {
		return TCSPOCName;
	}

	public void setTCSPOCName(String tCSPOCName) {
		TCSPOCName = tCSPOCName;
	}

	public String getSAScore() {
		return SAScore;
	}

	public void setSAScore(String sAScore) {
		SAScore = sAScore;
	}

	public String getTCAddress() {
		return TCAddress;
	}

	public void setTCAddress(String tCAddress) {
		TCAddress = tCAddress;
	}

	public String getFAScore() {
		return FAScore;
	}

	public void setFAScore(String fAScore) {
		FAScore = fAScore;
	}

	public String getMaxMarks() {
		return maxMarks;
	}

	public void setMaxMarks(String maxMarks) {
		this.maxMarks = maxMarks;
	}

	public String getTrainingCenter() {
		return trainingCenter;
	}

	public void setTrainingCenter(String trainingCenter) {
		this.trainingCenter = trainingCenter;
	}

	@Override
	public String toString() {
		return "CenterRatingSummary [TCSPOCName=" + TCSPOCName + ", SAScore=" + SAScore + ", TCAddress=" + TCAddress
				+ ", FAScore=" + FAScore + ", maxMarks=" + maxMarks + ", trainingCenter=" + trainingCenter + "]";
	}
}
