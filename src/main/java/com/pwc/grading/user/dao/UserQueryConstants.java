package com.pwc.grading.user.dao;

/**
 * The class that holds all the database queries related to the user.
 *
 */
public class UserQueryConstants {
	public static final String DATA_BASE_PLACE_HOLDER = "#$DataBaseName#$";
	public static final String SELECT_USER_BY_USER_ID = "SELECT * FROM #$DataBaseName#$.DBO.UserDetails WHERE USERID=?";
	public static final String SELECT_USER_BY_EMAIL = "SELECT * FROM #$DataBaseName#$.DBO.UserDetails WHERE EMAIL=?";
//	public static final String SELECT_USER_FORGET_PASSWORD_BY_EMAIL = "SELECT * FROM #$DataBaseName#$.DBO.UserDetails WHERE EMAIL=?";
	public static final String INSERT_USER = "INSERT INTO #$DataBaseName#$.DBO.UserDetails VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String SELECT_ALL_USER = "SELECT * FROM #$DataBaseName#$.DBO.UserDetails";
	public static final String UPDATE_USER_PASSWORD_BY_USER_ID = "UPDATE #$DataBaseName#$.DBO.UserDetails SET PASSWORD=? WHERE USERID=?";
	public static final String UPDATE_USER_STATUS_BY_USER_ID = "UPDATE #$DataBaseName#$.DBO.UserDetails SET STATUS=? WHERE USERID=?";
	public static final String UPDATE_USER_CENTER_ID_BY_USER_ID = "UPDATE #$DataBaseName#$.DBO.UserDetails SET CENTERID=? WHERE USERID=?";
	public static final String UPDATE_USER_OTP_BY_USER_EMAIL = "UPDATE #$DataBaseName#$.DBO.UserDetails SET OTP=? ,EXPIRYDATE=? WHERE EMAIL=?";
//	public static final String UPDATE_USER_FORGET_PASSWORD_OTP_BY_USER_EMAIL = "UPDATE #$DataBaseName#$.DBO.USERDETAILS SET FORGETPASSWORDOTP=? ,FORGETPASSWORDEXPIRYDATE=? WHERE EMAIL=?";
	public static final String DELETE_USER_BY_USER_ID = "DELETE #$DataBaseName#$.DBO.UserDetails WHERE USERID=?";
	public static final String UPDATE_USER_BY_ID = "UPDATE #$DataBaseName#$.DBO.UserDetails SET FIRSTNAME=?,LASTNAME=?,EMAIL=?,PHONE=?,ROLE=?,CENTERID=? WHERE USERID=?";
	public static final String UPDATE_PASSWORD_STATUS_BY_USER_ID = "UPDATE #$DataBaseName#$.DBO.UserDetails SET PASSWORD=?,STATUS=? WHERE USERID=?";
	public static final String UPDATE_USER_FORGET_PASSWORD_OTPS_BY_USER_EMAIL = "UPDATE #$DataBaseName#$.DBO.USERDETAILS SET FORGETPASSWORDEMAILOTP=?,FORGETPASSWORDSMSOTP=? ,FORGETPASSWORDEXPIRYDATE=? WHERE EMAIL=?";
	public static final String UPDATE_USER_DETAILS_AND_STATUS_BY_ID = "UPDATE #$DataBaseName#$.DBO.UserDetails SET FIRSTNAME=?,LASTNAME=?,EMAIL=?,PHONE=?,ROLE=?,CENTERID=?,STATUS=?,PASSWORD=? WHERE USERID=?";
}
