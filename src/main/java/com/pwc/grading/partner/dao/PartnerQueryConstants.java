package com.pwc.grading.partner.dao;

/**
 * The class containing all the queries for operation related to
 * the partner operations.
 *
 */
public class PartnerQueryConstants {
	public static final String DATA_BASE_PLACE_HOLDER = "#$DataBaseName#$";

	public static final String UPDATE_TC_BY_TC_ID = "UPDATE #$DataBaseName#$.DBO.TRAININGCENTERDETAILS  SET TCNAME=?,DISTRICT=?,CENTERADDRESS=?,CENTERINCHARGEID=?,LATITUDE=?,LONGITUDE=? WHERE TCID=?";

	public static final String UPDATE_PARTNER_BY_PARTNER_ID = "UPDATE #$DataBaseName#$.DBO.PARTNERDETAILS SET CLIENTSPONSORID=? WHERE PARTNERID=?";
}
