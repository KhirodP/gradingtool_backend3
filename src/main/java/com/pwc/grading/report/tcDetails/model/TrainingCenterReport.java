package com.pwc.grading.report.tcDetails.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

//import com.fasterxml.jackson.xml.annotate.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "root")
public class TrainingCenterReport {
	private ReportBody reportBody;

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
		return "TrainingCenterReport [reportBody=" + reportBody + ", reportHeader=" + reportHeader + "]";
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

}
