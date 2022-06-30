package com.pwc.grading.report.partner.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ReportHeader {
	@JsonProperty("partnerSPOCPhone")
	private String partnerSPOCPhone;
	@JsonProperty("PIA")
	private String PIA;
	@JsonProperty("partnerSPOCName")
	private String partnerSPOCName;
	@JsonProperty("projectName")
	private String projectName;
	@JsonProperty("partnerProjectId")
	private String partnerProjectId;
	@JsonProperty("partnerSPOCEmailId")
	private String partnerSPOCEmailId;
	@JacksonXmlElementWrapper(localName = "audits")
	@JacksonXmlProperty(localName = "audit")
	@JsonProperty("audits")
	private List<Audits> audits;

	public String getPartnerSPOCPhone() {
		return partnerSPOCPhone;
	}

	public void setPartnerSPOCPhone(String partnerSPOCPhone) {
		this.partnerSPOCPhone = partnerSPOCPhone;
	}

	public String getPIA() {
		return PIA;
	}

	public void setPIA(String pIA) {
		PIA = pIA;
	}

	public String getPartnerSPOCName() {
		return partnerSPOCName;
	}

	public void setPartnerSPOCName(String partnerSPOCName) {
		this.partnerSPOCName = partnerSPOCName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public String toString() {
		return "ReportHeader [partnerSPOCPhone=" + partnerSPOCPhone + ", PIA=" + PIA + ", partnerSPOCName="
				+ partnerSPOCName + ", projectName=" + projectName + "]";
	}

	public String getPartnerSPOCEmailId() {
		return partnerSPOCEmailId;
	}

	public void setPartnerSPOCEmailId(String partnerSPOCEmailId) {
		this.partnerSPOCEmailId = partnerSPOCEmailId;
	}

	public List<Audits> getAudits() {
		return audits;
	}

	public void setAudits(List<Audits> audits) {
		this.audits = audits;
	}
}
