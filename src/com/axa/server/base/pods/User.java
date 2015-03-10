package com.axa.server.base.pods;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import com.google.appengine.api.datastore.Blob;
import com.google.gson.annotations.Expose;


@Entity
public class User {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Expose private Long userId;
	@Expose private String email;
	@Expose private String name;
	@Expose @Transient private String picture;	
	private Blob pictureBlob;	
	@Expose private String language;
	@Expose private String fbId;
	@Expose private List<String> goals = new ArrayList<String>();

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


	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public Blob getPictureBlob() {
		return pictureBlob;
	}

	public void setPictureBlob(Blob pictureBlob) {
		this.pictureBlob = pictureBlob;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public List<String> getGoals() {
		return goals;
	}

	public void setGoals(List<String> goals) {
		this.goals = goals;
	}

}
