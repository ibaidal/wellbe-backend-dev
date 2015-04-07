package com.axa.server.base.pods;

import java.util.ArrayList;
import java.util.Date;
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
public class BoostActivity {
	
    public static enum Type {
        PROOF, TIP;
    };
    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Expose private Long activityId;
	@Expose private Long boostId;
	@Expose private Long userId;
	@Expose private String type;
	@Expose private String text;
	@Expose private String url;
	@Expose private String image;
	private Blob imageBlob;	
	@Expose private double latitude;
	@Expose private double longitude;
	@Expose private String place;
	@Expose private String placeId;
	@Expose private Date creation;
	
	@Expose @Transient private List<Link> links = new ArrayList<Link>();
	
	public BoostActivity() {
		super();
	}
	public Long getActivityId() {
		return activityId;
	}
	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}
	public Long getBoostId() {
		return boostId;
	}
	public void setBoostId(Long boostId) {
		this.boostId = boostId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Blob getImageBlob() {
		return imageBlob;
	}
	public void setImageBlob(Blob imageBlob) {
		this.imageBlob = imageBlob;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getPlaceId() {
		return placeId;
	}
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	public Date getCreation() {
		return creation;
	}
	public void setCreation(Date creation) {
		this.creation = creation;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	@Override
	public String toString() {
		return "BoostActivity [activityId=" + activityId + ", boostId="
				+ boostId + ", userId=" + userId + ", type=" + type + ", text="
				+ text + ", url=" + url + ", image=" + image + ", imageBlob="
				+ imageBlob + ", latitude=" + latitude + ", longitude="
				+ longitude + ", place=" + place + ", placeId=" + placeId
				+ ", creation=" + creation + ", links=" + links + "]";
	}
	

}
