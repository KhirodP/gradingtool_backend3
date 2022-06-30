package com.pwc.grading.report.tcDetails.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Scorecard {
	
	@JsonProperty("SAScore")
	@JacksonXmlProperty(localName = "sAScore")
	private String sAScore;
	
	@JacksonXmlElementWrapper(localName = "optionsDetails")
	@JacksonXmlProperty(localName = "optionsDetail")
	private List<OptionsDetails> optionsDetails;
	
	@JsonProperty("parameter")
//	@JacksonXmlCData
//	@JacksonXmlText
	@JacksonXmlProperty(localName = "parameter")
	private String parameter;
	
	@JsonProperty("FAScore")
	@JacksonXmlProperty(localName = "fAScore")
	private String fAScore;
	
	private String maxMarks;

	public List<OptionsDetails> getOptionsDetails() {
		return optionsDetails;
	}

	public void setOptionsDetails(List<OptionsDetails> optionsDetails) {
		this.optionsDetails = optionsDetails;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getMaxMarks() {
		return maxMarks;
	}

	public void setMaxMarks(String maxMarks) {
		this.maxMarks = maxMarks;
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
		return "Scorecard [sAScore=" + sAScore + ", optionsDetails=" + optionsDetails + ", parameter=" + parameter
				+ ", fAScore=" + fAScore + ", maxMarks=" + maxMarks + "]";
	}

	
}
