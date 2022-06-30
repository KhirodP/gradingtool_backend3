package com.pwc.grading.report.tcDetails.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OptionsDetails {
	
//	@JacksonXmlCData	
//	@JacksonXmlText
	@JsonProperty("optionValue")
	@JacksonXmlProperty(localName = "optionValue")
	private String optionValue;

	private String optionWeightage;

	public String getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}

	public String getOptionWeightage() {
		return optionWeightage;
	}

	public void setOptionWeightage(String optionWeightage) {
		this.optionWeightage = optionWeightage;
	}

	@Override
	public String toString() {
		return "OptionsDetails [optionValue=" + optionValue + ", optionWeightage=" + optionWeightage + "]";
	}

}
