package com.axa.server.base.pods;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Token {

	@Id
	private Long   userId;
	private String access;
	private String secret;
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getAccess() {
		return access;
	}
	
	public void setAccess(String access) {
		this.access = access;
	}
	
	public String getSecret() {
		return secret;
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	@Override
	public String toString() {
		return access + ":" + secret;
	}
	
}
