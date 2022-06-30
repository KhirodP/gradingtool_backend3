package com.pwc.grading.user.util;

import com.pwc.grading.user.model.User;
import com.pwc.grading.user.service.registration.UserAccessManagementServiceConstants;

public class UserUtil {
	
	/**
	 * To check user is un-verified.
	 * @param user user details
	 * @return true if user is un-verified
	 */
	public static boolean isUserUnVerified(User user) {
		String status = user.getStatus();
		if(status.equals(UserAccessManagementServiceConstants.USER_UN_VERIFIED)) {
			return true;
		}
		return false;
	}
	
	/**
	 * To check user is disbled.
	 * @param user user details
	 * @return true if user is disbled
	 */
	public static boolean isUserDisabled(User user) {
		String status = user.getStatus();
		if(status.equals(UserAccessManagementServiceConstants.USER_DISABLE)) {
			return true;
		}
		return false;
	}
	
	/**
	 * To check user is active
	 * @param user user details
	 * @return true if user is active
	 */
	public static boolean isUserActive(User user) {
		String status = user.getStatus();
		if(status.equals(UserAccessManagementServiceConstants.USER_ACTIVE)) {
			return true;
		}
		return false;
	}
}
