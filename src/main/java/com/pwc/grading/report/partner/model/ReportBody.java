package com.pwc.grading.report.partner.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ReportBody {
	@JsonProperty("centerRatingSummary")
	@JacksonXmlElementWrapper(localName = "centerRatingSummaries")
	private List<CenterRatingSummary> centerRatingSummary;
	private SummaryReport summaryReport;
	@JsonProperty("partnerGradingSummary")
	@JacksonXmlProperty(localName = "partnerGradingSummary")
	private PartnerGradingSummary partnerGradingSummary;

	public List<CenterRatingSummary> getCenterRatingSummary() {
		return centerRatingSummary;
	}

	public void setCenterRatingSummary(List<CenterRatingSummary> centerRatingSummary) {
		this.centerRatingSummary = centerRatingSummary;
	}

	public SummaryReport getSummaryReport() {
		return summaryReport;
	}

	@Override
	public String toString() {
		return "ReportBody [centerRatingSummary=" + centerRatingSummary + ", summaryReport=" + summaryReport
				+ ", partnerGradingSummary=" + partnerGradingSummary + "]";
	}

	public PartnerGradingSummary getPartnerGradingSummary() {
		return partnerGradingSummary;
	}

	public void setPartnerGradingSummary(PartnerGradingSummary partnerGradingSummary) {
		this.partnerGradingSummary = partnerGradingSummary;
	}

	public void setSummaryReport(SummaryReport summaryReport) {
		this.summaryReport = summaryReport;
	}

}
