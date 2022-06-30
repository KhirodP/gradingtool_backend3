package com.pwc.grading.report.tcDetails.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.xml.annotate.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.xml.annotate.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Form {
	
	@JacksonXmlElementWrapper(localName = "sectionsDetails")
	@JacksonXmlProperty(localName = "sectionsDetail")
	private List<SectionsDetails> sectionsDetails;
	
	private String formName;
	
	@JsonProperty("FAScore")
	@JacksonXmlProperty(localName = "fascore")
	private String fascore;
	
	@JsonProperty("maxMarks")
	private String maxMarks;
	
	@JsonProperty("SAScore")
	@JacksonXmlProperty(localName = "sascore")
	private String sascore;


	public List<SectionsDetails> getSectionsDetails() {
		return sectionsDetails;
	}

	public void setSectionsDetails(List<SectionsDetails> sectionsDetails) {
		this.sectionsDetails = sectionsDetails;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getMaxMarks() {
		return maxMarks;
	}

	public void setMaxMarks(String maxMarks) {
		this.maxMarks = maxMarks;
	}

	public String getFascore() {
		return fascore;
	}

	public String getSascore() {
		return sascore;
	}

	public void setFascore(String fascore) {
		this.fascore = fascore;
	}

	public void setSascore(String sascore) {
		this.sascore = sascore;
	}

	@Override
	public String toString() {
		return "Form [sectionsDetails=" + sectionsDetails + ", formName=" + formName + ", fascore=" + fascore
				+ ", maxMarks=" + maxMarks + ", sascore=" + sascore + "]";
	}

	
}
