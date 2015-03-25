package com.axa.server.base.pods;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class BoostParticipants {
	
    @Expose private List<User> participants = new ArrayList<User>();
    @Expose private List<User> pending = new ArrayList<User>();
	public BoostParticipants() {
	}
	public List<User> getParticipants() {
		return participants;
	}
	public void setParticipants(List<User> participants) {
		this.participants = participants;
	}
	public List<User> getPending() {
		return pending;
	}
	public void setPending(List<User> pending) {
		this.pending = pending;
	}
    
}
