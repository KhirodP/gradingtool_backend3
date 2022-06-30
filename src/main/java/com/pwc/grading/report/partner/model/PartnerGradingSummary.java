package com.pwc.grading.report.partner.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PartnerGradingSummary {
	@JsonProperty("SAScore")
	private String SAScore;
	@JsonProperty("FAScore")
	private String FAScore;
	@JsonProperty("maxMarks")
	private String maxMarks;

	public String getSAScore() {
		return SAScore;
	}

	public void setSAScore(String sAScore) {
		SAScore = sAScore;
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

	@Override
	public String toString() {
		return "ProjectGradingSummary [SAScore=" + SAScore + ", FAScore=" + FAScore + ", maxMarks=" + maxMarks + "]";
	}
}
