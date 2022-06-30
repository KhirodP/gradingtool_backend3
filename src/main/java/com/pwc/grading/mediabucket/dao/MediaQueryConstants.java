package com.pwc.grading.mediabucket.dao;

/**
 * A class having all the queries related to the MEDIA table.
 *
 */
public class MediaQueryConstants {
	public static final String DATA_BASE_PLACE_HOLDER = "#$DataBaseName#$";
	public static final String INSERT_MEDIA="INSERT INTO #$DataBaseName#$.DBO.MEDIADATA VALUES(?,?,?,?,?,?)";
	public static final String SELECT_MEDIA_BY_MEDIA_ID="SELECT * FROM #$DataBaseName#$.DBO.MEDIADATA WHERE MEDIAID=?";
	public static final String DELETE_MEDIA_BY_MEDIA_ID="DELETE #$DataBaseName#$.DBO.MEDIADATA WHERE MEDIAID=?";
	public static final String SELECT_MEDIA_LIST_MEDIA_ID="SELECT * FROM #$DataBaseName#$.DBO.MEDIADATA WHERE MEDIAID IN ";
	
	public static final String DELETE_MEDIA_BY_MEDIA_IDS_IN="DELETE #$DataBaseName#$.DBO.MEDIADATA WHERE MEDIAID IN ";
}
