package com.pwc.grading.reportingdb.constant;

/**
 * A class containing the JOB related constant values.
 * Its Operation types and status values.
 */
public class JobConstant {

	// Type of Job Actions(operationType)
	public static final String ADD_GRADING = "ADD_GRADING";
	public static final String ADD_RATING = "ADD_RATING";
	public static final String UPDATE = "UPDATE";
	public static final String UPDATE_GRADING_FA_SUBMIT = "UPDATE_GRADING_FA_SUBMIT";
	public static final String UPDATE_GRADING_CS_SUBMIT = "UPDATE_GRADING_CS_SUBMIT";
	public static final String UPDATE_RATING_FA_SUBMIT = "UPDATE_RATING_FA_SUBMIT";
	public static final String UPDATE_RATING_CIC_SUBMIT = "UPDATE_RATING_CIC_SUBMIT";
	public static final String DELETE = "DELETE";
	public static final String UPDATE_TC = "UPDATE_TC";
	public static final String UPDATE_CIC = "UPDATE_CIC";
	public static final String UPDATE_CS = "UPDATE_CS";
	public static final String UPDATE_GRADING_FA_DETAILS = "UPDATE_GRADING_FA_DETAILS";
	public static final String UPDATE_RATING_FA_DETAILS = "UPDATE_RATING_FA_DETAILS";
	public static final String DELETE_GRADING = "DELETE_GRADING";
	public static final String DELETE_RATING = "DELETE_RATING";

	// Status contants
	public static final String STATUS_NEW = "NEW";
	public static final String STATUS_FAILED = "FAILED";
	public static final String STATUS_IN_PROGRESS = "IN PROGRESS";
	public static final String STATUS_SUCCESS = "SUCCESS";
	public static final String RETRY_IN_PROGRESS = "RETRY IN PROGRESS";
	public static final String STATUS_RETRY_FAILED = "RETRY FAILED";
}
