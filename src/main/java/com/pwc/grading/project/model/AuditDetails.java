package com.pwc.grading.project.model;

/**
 * A class having all the audit details.
 *
 */
public class AuditDetails {
	private String auditFor;
	private String auditForId;
	private String name;

	public AuditDetails(String auditFor, String auditForId, String name) {
		super();
		this.auditFor = auditFor;
		this.auditForId = auditForId;
		this.name = name;
	}

	public AuditDetails() {
		// TODO Auto-generated constructor stub
	}

	public String getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}

	public String getAuditForId() {
		return auditForId;
	}

	public void setAuditForId(String auditForId) {
		this.auditForId = auditForId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "AuditDetails [auditFor=" + auditFor + ", auditForId=" + auditForId + ", name=" + name + "]";
	}

}
