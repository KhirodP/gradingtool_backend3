package com.pwc.grading.geolocation.dao;

/**
 * A class which holds all the constants related to geo location.
 *
 */
public class GeoLocationQueryConstants {
	public static final String DATABASE_PLACE_HOLDER = "#$DataBaseName#$";
	public static final String CALCULATE_DST_BTW_TC_AND_USER = "select (select tcGeoLocation from #$DataBaseName#$.dbo.TrainingCenterDetails where TcId=?).STDistance(geography::Point(?,?,4326)) as DISTANCE";
	public static final String DISTANCE = "DISTANCE";
	public static final int SRID_FOR_GEOGRAPHY = 4326;
}
