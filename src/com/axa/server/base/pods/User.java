package com.axa.server.base.pods;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.axa.server.base.response.Link;
import com.google.appengine.api.datastore.Blob;
import com.google.gson.annotations.Expose;


@Entity
public class User {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Expose private Long userId;
	@Expose private String email;
	@Expose private String name;
	@Expose private String picture;	
	private Blob pictureBlob;	
	@Expose private String language;
	@Expose private String fbId;
	@Expose private List<String> goals = new ArrayList<String>();

	private String password;
	@Expose @Transient private List<Link> links = new ArrayList<Link>();
	

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
	
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	

	@Override
	public String toString() {
		return "User [userId=" + userId + ", email=" + email + ", name=" + name
				+ ", picture=" + picture + ", pictureBlob=" + pictureBlob
				+ ", language=" + language + ", fbId=" + fbId + ", goals="
				+ goals + ", password=" + password + "]";
	}
	

}
