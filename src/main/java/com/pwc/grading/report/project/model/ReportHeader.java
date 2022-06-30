package com.pwc.grading.report.project.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.NONE)
public class ReportHeader {
	@XmlElement
	private String date;
	@XmlElement
	private String tcIncluded;
	@XmlElement
	private String partnersIncluded;
	@XmlElement
	private String projectName;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTcIncluded() {
		return tcIncluded;
	}

	public void setTcIncluded(String tcIncluded) {
		this.tcIncluded = tcIncluded;
	}

	public String getPartnersIncluded() {
		return partnersIncluded;
	}

	public void setPartnersIncluded(String partnersIncluded) {
		this.partnersIncluded = partnersIncluded;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public String toString() {
		return "ReportHeader [date=" + date + ", tcIncluded=" + tcIncluded + ", partnersIncluded=" + partnersIncluded
				+ ", projectName=" + projectName + "]";
	}
}
