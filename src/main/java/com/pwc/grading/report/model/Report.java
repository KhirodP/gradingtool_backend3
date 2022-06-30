package com.pwc.grading.report.model;

import java.io.InputStream;

/**
 * A class having all the details related to the report.
 *
 */
public class Report {
	private String reportName;
	private InputStream inputstream;

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public InputStream getInputstream() {
		return inputstream;
	}

	public void setInputstream(InputStream inputstream) {
		this.inputstream = inputstream;
	}

	@Override
	public String toString() {
		return "Report [reportName=" + reportName + ", inputstream=" + inputstream + "]";
	}

}
