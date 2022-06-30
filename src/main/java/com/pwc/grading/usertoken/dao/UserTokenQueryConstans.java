package com.pwc.grading.usertoken.dao;

/**
 * A class holds all the database queries related to the user token operations.
 */
public class UserTokenQueryConstans {
	public static final String DATA_BASE_PLACE_HOLDER = "#$DataBaseName#$";
	public static final String INSERT_USER_TOKEN = "INSERT INTO #$DataBaseName#$.DBO.UserToken VALUES(?,?,?,?,?,?)";
	public static final String SELECT_USER_TOKEN_BY_USERID_AND_DEVICEID = "SELECT * FROM #$DataBaseName#$.DBO.UserToken WHERE USERID=? AND DEVICEID=?";
	public static final String UPDATE_USER_TOKEN_BY_ID = "UPDATE #$DataBaseName#$.DBO.UserToken SET DEVICEID=?,EXPTIME=?,USERID=?,PLATFORM=?,ACCESTOKEN=? WHERE TOKENID=?";
	public static final String SELECT_USER_TOKEN_BY_ACCESTOKEN = "SELECT * FROM #$DataBaseName#$.DBO.UserToken WHERE ACCESTOKEN=?";
	public static final String DELETE_USER_TOKEN_BY_USERID_AND_DEVICEID = "DELETE FROM #$DataBaseName#$.DBO.UserToken WHERE USERID=? AND DEVICEID=?";
	public static final String DELETE_USER_TOKEN_BY_USERID = "DELETE FROM #$DataBaseName#$.DBO.UserToken WHERE USERID=?";

}
