package com.pwc.grading.reportingdb.model;
/**
 * A POJO which is having the details of the PartnersReportingTable table.
 *
 */
public class PartnersReportingTable {

	private String partnerProjectId;		// will be UUID
	private String projectId;
	private String projectName;
	private String partnerId;
	private String piaName;
	private String district;
	private String headPersonnel;
	private String contact;
	private String headPersonEmail;
	private double centerRating;
	private double projectGrading;
	private double finalPercentage;
	private String grade;
	private boolean status;
	
	public PartnersReportingTable() {
		super();
	}

	public String getPartnerProjectId() {
		return partnerProjectId;
	}

	public String getProjectId() {
		return projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public String getPiaName() {
		return piaName;
	}

	public String getDistrict() {
		return district;
	}

	public String getHeadPersonnel() {
		return headPersonnel;
	}

	public String getContact() {
		return contact;
	}

	public String getHeadPersonEmail() {
		return headPersonEmail;
	}

	public double getCenterRating() {
		return centerRating;
	}

	public double getProjectGrading() {
		return projectGrading;
	}

	public double getFinalPercentage() {
		return finalPercentage;
	}

	public String getGrade() {
		return grade;
	}

	public boolean getStatus() {
		return status;
	}

	public void setPartnerProjectId(String partnerProjectId) {
		this.partnerProjectId = partnerProjectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public void setPiaName(String piaName) {
		this.piaName = piaName;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public void setHeadPersonnel(String headPersonnel) {
		this.headPersonnel = headPersonnel;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public void setHeadPersonEmail(String headPersonEmail) {
		this.headPersonEmail = headPersonEmail;
	}

	public void setCenterRating(double centerRating) {
		this.centerRating = centerRating;
	}

	public void setProjectGrading(double projectGrading) {
		this.projectGrading = projectGrading;
	}

	public void setFinalPercentage(double finalPercentage) {
		this.finalPercentage = finalPercentage;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "PartnersReportingTable [partnerProjectId=" + partnerProjectId + ", projectId=" + projectId
				+ ", projectName=" + projectName + ", partnerId=" + partnerId + ", piaName=" + piaName + ", district="
				+ district + ", headPersonnel=" + headPersonnel + ", contact=" + contact + ", headPersonEmail="
				+ headPersonEmail + ", centerRating=" + centerRating + ", projectGrading=" + projectGrading
				+ ", finalPercentage=" + finalPercentage + ", grade=" + grade + ", status=" + status + "]";
	}

}
