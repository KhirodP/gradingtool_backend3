package com.pwc.grading.report.tcDetails.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.xml.annotate.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.xml.annotate.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ReportBody {
	@JacksonXmlElementWrapper(localName = "forms")
	@JacksonXmlProperty(localName = "form")
	@JsonProperty("forms")
	private List<Form> form;

	public List<Form> getForm() {
		return form;
	}

	public void setForm(List<Form> form) {
		this.form = form;
	}

	@Override
	public String toString() {
		return "ReportBody [form=" + form + "]";
	}
}
