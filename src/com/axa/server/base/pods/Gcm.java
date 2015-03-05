package com.axa.server.base.pods;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Gcm {

	@Id
	private String regId;
	
	public String getRegId() {
		return regId;
	}
	
	public void setRegId(String regId) {
		this.regId = regId;
	}
	
}
