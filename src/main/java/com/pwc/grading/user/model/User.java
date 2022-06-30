package com.pwc.grading.user.model;

/**
 * A class which contains the details of the user.
 *
 */
public class User {

	private String userId;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String userRole;
	private String centerId;
	private String status;

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(String userId, String password, String firstName, String lastName, String email, String phone,
			String userRole, String centerId, String status) {
		super();
		this.userId = userId;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.userRole = userRole;
//		this.group = group;
//		this.userCenterGeoLocation = userCenterGeoLocation;
		this.centerId = centerId;
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", password=" + password + ", firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + ", phone=" + phone + ", userRole=" + userRole + ", centerId="
				+ centerId + ", status=" + status + "]";
	}

}
