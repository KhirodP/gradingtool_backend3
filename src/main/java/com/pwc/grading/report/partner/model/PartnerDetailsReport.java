package com.pwc.grading.report.partner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "root")
public class PartnerDetailsReport {
	@JsonProperty("reportBody")
	private ReportBody reportBody;
	@JsonProperty("reportHeader")
	private ReportHeader reportHeader;
	private String warning;

	public ReportBody getReportBody() {
		return reportBody;
	}

	public void setReportBody(ReportBody reportBody) {
		this.reportBody = reportBody;
	}

	public ReportHeader getReportHeader() {
		return reportHeader;
	}

	public void setReportHeader(ReportHeader reportHeader) {
		this.reportHeader = reportHeader;
	}

	@Override
	public String toString() {
		return "PartnerDetailsReport [reportBody=" + reportBody + ", reportHeader=" + reportHeader + "]";
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}
}
