package com.axa.server.base.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;







import javax.servlet.http.HttpServletResponse;

import com.axa.server.base.Constants;
import com.axa.server.base.persistence.Persistence;
import com.axa.server.base.pods.Boost;
import com.axa.server.base.pods.User;
import com.axa.server.base.pods.UserBoosts;
import com.axa.server.base.response.Link;
import com.axa.server.base.response.Status;
import com.axa.server.base.response.WellBeResponse;
import com.google.gson.Gson;


public final class Utils {
	
	public static final int NO_SUCH_ACCOUNT = 212;
	
	public static final String fake_picture_one = "http://crackberry.com/sites/crackberry.com/files/styles/large/public/topic_images/2013/ANDROID.png";
	public static final String fake_picture_two = "https://lh3.googleusercontent.com/-0s48FykazSM/AAAAAAAAAAI/AAAAAAAAALg/aJ0-vobQsCI/photo.jpg";
	
	public static void sendError(HttpServletResponse resp, Gson GSON, int status, WellBeResponse<Void> entity) throws IOException {
		resp.setStatus(status);
		resp.setContentType(Constants.CONTENT_TYPE_JSON);
		resp.getWriter().append(GSON.toJson(entity));	
	}
	
	public static void createFackeUserOne() {
		User user = new User();
		user.setEmail("fake.one@axa.com");
		user.setEmailLowerCase("fake.one@axa.com");
		List<String> goals = new ArrayList<String>();
		goals.add("nutrition");
		goals.add("physical activity");
		user.setGoals(goals);
		user.setLanguage("es");
		user.setName("Fake One");
		user.setPassword("123456789");
		user.setPicture(fake_picture_one);
		
		Persistence.insert(user);
	}
	
	public static void createFackeUserTwo() {
		User user = new User();
		user.setEmail("fake.two@axa.com");
		user.setEmailLowerCase("fake.two@axa.com");
		List<String> goals = new ArrayList<String>();
		goals.add("nutrition");
		goals.add("mental activity");
		user.setGoals(goals);
		user.setLanguage("es");
		user.setName("Fake Two");
		user.setPassword("123456789");
		user.setPicture(fake_picture_two);
		
		Persistence.insert(user);
	}
	
	public static void createNewBoost(User user) {
		Calendar creation = Calendar.getInstance();
		creation.add(Calendar.MONTH, -1);

		Calendar start = Calendar.getInstance();
		start.add(Calendar.WEEK_OF_YEAR, 1);

		Calendar end = Calendar.getInstance();
		end.add(Calendar.WEEK_OF_YEAR, 7);
				
		Boost boost = new Boost();
		boost.setCreation(creation.getTime());
		boost.setStart(start.getTime());
		boost.setEnd(end.getTime());
		boost.setGoal(user.getGoals().get(0));
		boost.setOwnerId(user.getUserId());
		boost.setPicture("http://blog.sportzone.es/wp-content/uploads/2014/10/keep-up-running-lake-28072011.jpg");
		boost.setStatus(Boost.Status.NEW.toString().toLowerCase());
		boost.setTitle("New Boost, goal: " + user.getGoals().get(0) + ", owner: " + user.getName());
		
		Persistence.insert(boost);
	}
	
	public static void createDoingBoosts(User user) {
		
		Calendar creation = Calendar.getInstance();
		creation.add(Calendar.MONTH, -1);

		Calendar start = Calendar.getInstance();
		start.add(Calendar.WEEK_OF_YEAR, -1);

		Calendar end = Calendar.getInstance();
		end.add(Calendar.WEEK_OF_YEAR, 5);
		
		User owner = Persistence.getUserByEmail("fake.one@axa.com");
		
		Boost boost;
		
		for(String s : user.getGoals()) {
			boost = new Boost();
			boost.setCreation(creation.getTime());
			boost.setStart(start.getTime());
			boost.setEnd(end.getTime());
			boost.setGoal(s);
			boost.setOwnerId(owner.getUserId());
			boost.setPicture("http://www.definicionabc.com/wp-content/uploads/Paisaje-Natural.jpg");
			boost.setStatus(Boost.Status.ONGOING.toString().toLowerCase());
			boost.setTitle("Doing Boost (Ongoing), goal: " + s + ", owner: " + user.getName());
			
			Persistence.insert(boost);
		}		
		
	}
	
	public static void createDoneBoosts(User user) {
		Calendar creation = Calendar.getInstance();
		creation.add(Calendar.MONTH, -1);

		Calendar start = (Calendar) creation.clone();
		start.add(Calendar.WEEK_OF_YEAR, 1);

		Calendar end = (Calendar) creation.clone();
		end.add(Calendar.WEEK_OF_YEAR, 2);
		
		User owner = Persistence.getUserByEmail("fake.two@axa.com");
		
		Boost boost;
		
		for(String s : user.getGoals()) {
			boost = new Boost();
			boost.setCreation(creation.getTime());
			boost.setStart(start.getTime());
			boost.setEnd(end.getTime());
			boost.setGoal(s);
			boost.setOwnerId(owner.getUserId());
			boost.setPicture("http://www.islam.com.kw/wp-content/uploads/2015/01/Abraham-His-Children-and-the-One-Message.jpg");
			boost.setStatus(Boost.Status.ACCOMPLISHED.toString().toLowerCase());
			boost.setTitle("Doing Boost (Accomplished), goal: " + s + ", owner: " + user.getName());
			
			Persistence.insert(boost);
		}	
		
	}
	
	public static WellBeResponse<User> getCreateUserResponse(User user) {
		WellBeResponse<User> response = new WellBeResponse<User>();
		
		Status status = new Status(201, "ok", "User created");	  
		
		List<Link> links = new ArrayList<Link>();
		links.add(new Link("getBoosts", "/boosts"));
		links.add(new Link("createUser", "/users", Link.Method.POST.toString()));
		links.add(new Link("updateUser", "/users/" + user.getUserId().toString(), Link.Method.PUT.toString()));
		links.add(new Link("getUser", "/users/" + user.getUserId().toString(), Link.Method.GET.toString()));
		
		response.setData(user);
		response.setLinks(links);
		response.setStatus(status);
		
		return response;
	}
	
	public static WellBeResponse<UserBoosts> getUserBoostListResponse(List<Boost> boosts, List<User> people) {
		WellBeResponse<UserBoosts> response = new WellBeResponse<UserBoosts>();
		
		Status status = new Status(200, "ok", null);	  
		
		List<Link> links = new ArrayList<Link>();
		links.add(new Link("getBoosts", "/boosts"));
		
		response.setData(new UserBoosts(boosts, people));		
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
	

	public static WellBeResponse<Void> getNoSuchAccountResponse(String description) throws IOException {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(NO_SUCH_ACCOUNT, "No Such Account", description));	
		response.setLinks(null);
		
		return response;
	}
	

}
