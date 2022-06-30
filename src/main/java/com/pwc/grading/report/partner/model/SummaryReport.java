package com.pwc.grading.report.partner.model;

public class SummaryReport {
	private String projectGrading;
	private String centerRating;
	private String grade;
	private String finalAvg;

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

	@Override
	public String toString() {
		return "SummaryReport [projectGrading=" + projectGrading + ", centerRating=" + centerRating + ", grade=" + grade
				+ ", finalAvg=" + finalAvg + "]";
	}
}
