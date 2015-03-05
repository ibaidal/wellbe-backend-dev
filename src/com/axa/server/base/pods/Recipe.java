package com.axa.server.base.pods;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.google.appengine.api.datastore.Blob;
import com.google.gson.annotations.Expose;


@Entity
public class Recipe {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Expose private Long   recipeId;
	@Expose private String title;
	@Expose private String comment;
	@Expose private int stars;
	@Expose @Transient private String picture;
	
	private Blob pictureBlob;


	public Long getRecipeId() {
		return recipeId;
	}

	
	public void setRecipeId(Long recipeId) {
		this.recipeId = recipeId;
	}

	
	public String getTitle() {
		return title;
	}

	
	public void setTitle(String title) {
		this.title = title;
	}

	
	public String getComment() {
		return comment;
	}

	
	public void setComment(String comment) {
		this.comment = comment;
	}

	
	public int getStars() {
		return stars;
	}
	
	
	public void setStars(int stars) {
		this.stars = stars;
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

	
}
