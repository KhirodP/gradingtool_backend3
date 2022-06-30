package com.pwc.grading.report.tcDetails.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.xml.annotate.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.xml.annotate.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ReportHeader {
	@JsonProperty("partnerSPOCEmailId")
	private String partnerSPOCEmailId;
	
	@JsonProperty("centerId")
	private String centerId;
	
	@JsonProperty("TCAddress")
	@JacksonXmlProperty(localName = "tcaddress")
	private String tcaddress;

	private String partnerSPOCPhone;

	private String centerInchargeContact;
	
	@JsonProperty("PIA")
	@JacksonXmlProperty(localName = "pia")
	private String pia;

	private String partnerProjectId;

	private String partnerSPOCName;
	
	@JsonProperty("centerInchargeName")
	private String centerInchargeName;
	
	@JsonProperty("centerInchargeEmail")
	private String centerInchargeEmail;

	private String projectName;
	
	@JacksonXmlElementWrapper(localName = "audits")
	@JacksonXmlProperty(localName = "audit")
	private List<Audits> audits;

	public String getPartnerSPOCEmailId() {
		return partnerSPOCEmailId;
	}

	public String getCenterId() {
		return centerId;
	}

	public String getTcaddress() {
		return tcaddress;
	}

	public String getPartnerSPOCPhone() {
		return partnerSPOCPhone;
	}

	public String getCenterInchargeContact() {
		return centerInchargeContact;
	}

	public String getPia() {
		return pia;
	}

	public String getPartnerProjectId() {
		return partnerProjectId;
	}

	public String getPartnerSPOCName() {
		return partnerSPOCName;
	}

	public String getCenterInchargeName() {
		return centerInchargeName;
	}

	public String getCenterInchargeEmail() {
		return centerInchargeEmail;
	}

	public String getProjectName() {
		return projectName;
	}

	public List<Audits> getAudits() {
		return audits;
	}

	public void setPartnerSPOCEmailId(String partnerSPOCEmailId) {
		this.partnerSPOCEmailId = partnerSPOCEmailId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public void setTcaddress(String tcaddress) {
		this.tcaddress = tcaddress;
	}

	public void setPartnerSPOCPhone(String partnerSPOCPhone) {
		this.partnerSPOCPhone = partnerSPOCPhone;
	}

	public void setCenterInchargeContact(String centerInchargeContact) {
		this.centerInchargeContact = centerInchargeContact;
	}

	public void setPia(String pia) {
		this.pia = pia;
	}

	public void setPartnerProjectId(String partnerProjectId) {
		this.partnerProjectId = partnerProjectId;
	}

	public void setPartnerSPOCName(String partnerSPOCName) {
		this.partnerSPOCName = partnerSPOCName;
	}

	public void setCenterInchargeName(String centerInchargeName) {
		this.centerInchargeName = centerInchargeName;
	}

	public void setCenterInchargeEmail(String centerInchargeEmail) {
		this.centerInchargeEmail = centerInchargeEmail;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setAudits(List<Audits> audits) {
		this.audits = audits;
	}

	@Override
	public String toString() {
		return "ReportHeader [partnerSPOCEmailId=" + partnerSPOCEmailId + ", centerId=" + centerId + ", tcaddress="
				+ tcaddress + ", partnerSPOCPhone=" + partnerSPOCPhone + ", centerInchargeContact="
				+ centerInchargeContact + ", pia=" + pia + ", partnerProjectId=" + partnerProjectId
				+ ", partnerSPOCName=" + partnerSPOCName + ", centerInchargeName=" + centerInchargeName
				+ ", centerInchargeEmail=" + centerInchargeEmail + ", projectName=" + projectName + ", audits=" + audits
				+ "]";
	}


	
}
