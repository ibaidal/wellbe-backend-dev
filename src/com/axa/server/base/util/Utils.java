package com.axa.server.base.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;




import com.axa.server.base.pods.Boost;
import com.axa.server.base.pods.User;
import com.axa.server.base.pods.UserBoostsData;
import com.axa.server.base.response.Link;
import com.axa.server.base.response.Status;
import com.axa.server.base.response.WellBeResponse;
public final class Utils {
	
	public static WellBeResponse<User> getCreateUserResponse(User user) {
		WellBeResponse<User> response = new WellBeResponse<User>();
		
		Status status = new Status(201, "ok", "User created");	  
		
		List<Link> links = new ArrayList<Link>();
		links.add(new Link("getBoosts", "/boosts"));
		links.add(new Link("getBoost", "/boost/{boostId}"));
		links.add(new Link("createUser", "/user", Link.Method.POST.toString()));
		links.add(new Link("updateUser", "/user/" + user.getUserId().toString(), Link.Method.PUT.toString()));
		links.add(new Link("getUser", "/user/" + user.getUserId().toString(), Link.Method.GET.toString()));
		
		response.setData(user);
		response.setLinks(links);
		response.setStatus(status);
		
		return response;
	}
	
	public static WellBeResponse<UserBoostsData> getUserBoostListResponse(List<Boost> boosts, List<User> people) {
		WellBeResponse<UserBoostsData> response = new WellBeResponse<UserBoostsData>();
		
		Status status = new Status(200, "ok", null);	  
		
		List<Link> links = new ArrayList<Link>();
		links.add(new Link("getBoosts", "/boosts"));
		links.add(new Link("getBoost", "/boost/{boostId}"));
		links.add(new Link("createUser", "/user", Link.Method.POST.toString()));
		links.add(new Link("updateUser", "/user/{userId}", Link.Method.PUT.toString()));
		links.add(new Link("getUser", "/user/{userId}", Link.Method.GET.toString()));
		
		response.setData(new UserBoostsData(boosts, people));		
		response.setLinks(links);
		response.setStatus(status);
		
		return response;
	}
	
	public static WellBeResponse<User> getLoginUserResponse(User user) {
		WellBeResponse<User> response = getCreateUserResponse(user);
		
		response.setStatus(new Status(200, "ok", "User logged"));	  
		
		return response;
	}
	
	public static WellBeResponse<User> getUserResponse(User user) {
		WellBeResponse<User> response = getCreateUserResponse(user);
		
		response.setStatus(new Status(200, "ok", null));
	  	
		return response;
	}	
	
	public static WellBeResponse<User> getUpdateUserResponse(User user) {
		WellBeResponse<User> response = getCreateUserResponse(user);
		
		response.setStatus(new Status(200, "ok", "User updated"));
	  	
		return response;
	}		
	
	
	public static WellBeResponse<Void> getBadRequestResponse(String description) throws IOException {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(400, "Bad Request", description));
		response.setLinks(null);
		
		return response;
	}
	
	public static WellBeResponse<Void> getUnauthorizedResponse(String description) throws IOException {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(401, "Unauthorized", description));	
		response.setLinks(null);
		
		return response;
	}
	
	public static WellBeResponse<Void> getNotFoundResponse(String description) throws IOException {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(404, "Not Found", description));	  
		response.setLinks(null);
		
		return response;
	}

	public static WellBeResponse<Void> getConflictResponse(String description) throws IOException {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(409, "Conflict", description));
		response.setLinks(null);
		
		return response;
	}
	


}
