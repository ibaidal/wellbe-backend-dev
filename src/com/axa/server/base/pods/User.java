package com.axa.server.base.pods;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.google.gson.annotations.Expose;


@Entity
public class User {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Expose private Long   userId;
	@Expose private String email;
	@Expose private String name;
	@Expose private String phone;
	@Expose private String address;
	@Expose private String fbId;
	@Expose private String gpId;

	private String password;
	private String emailLowerCase;
	

	@PrePersist
	@PreUpdate
	public void prePersist() {
		if (email != null) {
			emailLowerCase = email.toLowerCase();
		} else {
			emailLowerCase = null;
		}
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailLowerCase() {
		return emailLowerCase;
	}

	public void setEmailLowerCase(String emailLowerCase) {
		this.emailLowerCase = emailLowerCase;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public String getGpId() {
		return gpId;
	}

	public void setGpId(String gpId) {
		this.gpId = gpId;
	}

}
