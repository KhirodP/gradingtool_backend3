package com.pwc.grading.report.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PartnersSummary {

	private String projectGrading;
	private String centerRating;
	private String grade;
	private String finalAvg;
	@JsonProperty("PIA")
	private String PIA;

	public String getProjectGrading() {
		return projectGrading;
	}

	public void setProjectGrading(String projectGrading) {
		this.projectGrading = projectGrading;
	}

	public String getCenterRating() {
		return centerRating;
	}

	public void setCenterRating(String centerRating) {
		this.centerRating = centerRating;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getFinalAvg() {
		return finalAvg;
	}

	public void setFinalAvg(String finalAvg) {
		this.finalAvg = finalAvg;
	}

	public String getPIA() {
		return PIA;
	}

	public void setPIA(String pIA) {
		PIA = pIA;
	}

	@Override
	public String toString() {
		return "PartnersSummary [projectGrading=" + projectGrading + ", centerRating=" + centerRating + ", grade="
				+ grade + ", finalAvg=" + finalAvg + ", PIA=" + PIA + "]";
	}
}
