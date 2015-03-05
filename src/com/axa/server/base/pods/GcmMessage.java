package com.axa.server.base.pods;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;


public class GcmMessage {

	@Expose private List<String> registration_ids;
	@Expose private Map<String,?> data;
	
	public List<String> getRegistration_ids() {
		return registration_ids;
	}
	
	public void setRegistration_ids(List<String> registration_ids) {
		this.registration_ids = registration_ids;
	}
	
	public Map<String,?> getData() {
		return data;
	}
	
	public void setData(Map<String,?> data) {
		this.data = data;
	}
	
}
