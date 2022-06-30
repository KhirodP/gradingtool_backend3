package com.pwc.grading.report.project.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "root")
public class ProjectReport {
	private String warning;
	private ReportBody reportBody;
	private ReportHeader reportHeader;

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

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

}
