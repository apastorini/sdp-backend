package com.ude.sdp.dto;

import java.util.List;

public class UserDTO extends DTO {

	private String name;

	private String email;

	private String token;

	private String password;
	
	private String passwordExpiredDate;
	

	private String passwordToken;

	private String secondName;
	
	private boolean enable;
	
	

	private List<RoleDTO> roles;

	public List<RoleDTO> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleDTO> roles) {
		this.roles = roles;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserDTO(String email) {
		super();
		this.email = email;
	}

	
	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public UserDTO(String email, String token) {
		super();
		this.email = email;
		this.token = token;
	}

	public UserDTO() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserDTO(String name, String email, String token) {
		super();
		this.name = name;
		this.email = email;
		this.token = token;

	}
	
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public String getPasswordExpiredDate() {
		return passwordExpiredDate;
	}

	public void setPasswordExpiredDate(String passwordExpiredDate) {
		this.passwordExpiredDate = passwordExpiredDate;
	}

	public String getPasswordToken() {
		return passwordToken;
	}

	public void setPasswordToken(String passwordToken) {
		this.passwordToken = passwordToken;
	}

}