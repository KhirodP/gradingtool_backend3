package com.pwc.grading.report.tcDetails.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.xml.annotate.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.xml.annotate.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class SectionsDetails {
	
	@JsonProperty("sectionName")
	private String sectionName;
	
	private String sectionId;
	
	@JsonProperty("SAScore")
	@JacksonXmlProperty(localName = "sAScore")
	private String sAScore;
	
	@JsonProperty("FAScore")
	@JacksonXmlProperty(localName = "fAScore")
	private String fAScore;
	
	private String maxScore;
	
	@JacksonXmlElementWrapper(localName = "scorecards")
	@JacksonXmlProperty(localName = "scorecard")
	private List<Scorecard> scorecard;


	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(String maxScore) {
		this.maxScore = maxScore;
	}

	public List<Scorecard> getScorecard() {
		return scorecard;
	}

	public void setScorecard(List<Scorecard> scorecard) {
		this.scorecard = scorecard;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getsAScore() {
		return sAScore;
	}

	public String getfAScore() {
		return fAScore;
	}

	public void setsAScore(String sAScore) {
		this.sAScore = sAScore;
	}

	public void setfAScore(String fAScore) {
		this.fAScore = fAScore;
	}

	@Override
	public String toString() {
		return "SectionsDetails [sectionName=" + sectionName + ", sectionId=" + sectionId + ", sAScore=" + sAScore
				+ ", fAScore=" + fAScore + ", maxScore=" + maxScore + ", scorecard=" + scorecard + "]";
	}
	
	
}
