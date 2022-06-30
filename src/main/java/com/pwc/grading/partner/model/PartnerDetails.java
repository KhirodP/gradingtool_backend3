package com.pwc.grading.partner.model;

/**
 * A class holds the details of the partner.
 *
 */
public class PartnerDetails {
	private String partnerId;
	private String partnerName;
	private String clientSponsorId;

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getClientSponsorId() {
		return clientSponsorId;
	}

	public void setClientSponsorId(String clientSponsorId) {
		this.clientSponsorId = clientSponsorId;
	}

	@Override
	public String toString() {
		return "PartnerDetails [partnerId=" + partnerId + ", partnerName=" + partnerName + ", clientSponsorId="
				+ clientSponsorId + "]";
	}


}
