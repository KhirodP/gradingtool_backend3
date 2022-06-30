package com.pwc.grading.report.project.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;


public class ReportBody {
	@JacksonXmlElementWrapper(localName = "partnersSummaries")
	private List<PartnersSummary> partnersSummary;

	public List<PartnersSummary> getPartnersSummary() {
		return partnersSummary;
	}

	public void setPartnersSummary(List<PartnersSummary> partnersSummary) {
		this.partnersSummary = partnersSummary;
	}

	@Override
	public String toString() {
		return "ReportBody [partnersSummary=" + partnersSummary + "]";
	}
}
