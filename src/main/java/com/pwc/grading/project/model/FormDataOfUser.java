package com.pwc.grading.project.model;

/**
 * A class having all the form details of the user.
 *
 */
public class FormDataOfUser {

	private FormData formData;
	private String centerId;
	private AuditDetails auditDetails;
	private String partnerId;
	private String partnerName;
	private long FormDeadLine;
	private boolean formExpire;

	public FormData getFormData() {
		return formData;
	}

	public void setFormData(FormData formData) {
		this.formData = formData;
	}

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public AuditDetails getAuditDetails() {
		return auditDetails;
	}

	public void setAuditDetails(AuditDetails auditDetails) {
		this.auditDetails = auditDetails;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	@Override
	public String toString() {
		return "FormDataOfUser [formData =" + formData + ", centerId=" + centerId + ", auditDetails=" + auditDetails
				+ ", partnerId=" + partnerId + "]";
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public long getFormDeadLine() {
		return FormDeadLine;
	}

	public void setFormDeadLine(long formDeadLine) {
		FormDeadLine = formDeadLine;
	}

	public boolean isFormExpire() {
		return formExpire;
	}

	public void setFormExpire(boolean formExpire) {
		this.formExpire = formExpire;
	}
}
