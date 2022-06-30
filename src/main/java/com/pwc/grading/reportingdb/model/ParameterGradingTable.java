package com.pwc.grading.reportingdb.model;

/**
 * A POJO which is having the details of the ParameterGradingTable table.
 *
 */
public class ParameterGradingTable {

	private String id;
	private String formId;
	private String sectionId;
	private String parameterId;
	private int maxmarks;
	private int saScore;
	private int faScore;
	private int variance;
	private String faRemark;
	private String saRemark;

	public ParameterGradingTable() {

	}

	public String getId() {
		return id;
	}

	public String getFormId() {
		return formId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public String getParameterId() {
		return parameterId;
	}

	public int getMaxmarks() {
		return maxmarks;
	}

	public int getSaScore() {
		return saScore;
	}

	public int getFaScore() {
		return faScore;
	}

	public int getVariance() {
		return variance;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	public void setMaxmarks(int maxmarks) {
		this.maxmarks = maxmarks;
	}

	public void setSaScore(int saScore) {
		this.saScore = saScore;
	}

	public void setFaScore(int faScore) {
		this.faScore = faScore;
	}

	public void setVariance(int variance) {
		this.variance = variance;
	}

	public String getFaRemark() {
		return faRemark;
	}

	public void setFaRemark(String faRemark) {
		this.faRemark = faRemark;
	}

	public String getSaRemark() {
		return saRemark;
	}

	public void setSaRemark(String saRemark) {
		this.saRemark = saRemark;
	}

}
