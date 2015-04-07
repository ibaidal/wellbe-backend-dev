package com.axa.server.base.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.axa.server.base.Constants;
import com.axa.server.base.persistence.Persistence;
import com.axa.server.base.pods.Boost;
import com.axa.server.base.pods.BoostActivity;
import com.axa.server.base.pods.BoostParticipants;
import com.axa.server.base.pods.User;
import com.axa.server.base.pods.UserBoosts;
import com.axa.server.base.response.Link;
import com.axa.server.base.response.Status;
import com.axa.server.base.response.WellBeResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;


public final class Utils {
	
	public static final int NO_SUCH_ACCOUNT = 212;
	public static final int CREATED = 201;
	public static final int NO_CONTENT = 204;
	
	
	public static final String fake_picture_one = "http://crackberry.com/sites/crackberry.com/files/styles/large/public/topic_images/2013/ANDROID.png";
	public static final String fake_picture_two = "http://pngimg.com/upload/apple_PNG4938.png";
	
	public static void setPictureURL(User user, HttpServletRequest req) {
		if (user.getPictureBlob() == null) {
			user.setPicture(null);
		} else {
			String idCompPath = "/" + user.getUserId();
			String url = req.getRequestURL().toString();
			if (url.contains(String.valueOf(idCompPath))) {
				url = url.substring(0, url.indexOf(idCompPath));
			}
			user.setPicture(url + idCompPath + "/picture");
		}
	}
	
	public static void setPictureURL(BoostActivity ba, HttpServletRequest req) {
		if (ba.getImageBlob() == null) {
			ba.setImage(null);
		} else {
			String idCompPath = "/" + ba.getActivityId();
			String url = req.getRequestURL().toString();
			if (url.contains(String.valueOf(idCompPath))) {
				url = url.substring(0, url.indexOf(idCompPath));
			}
			ba.setImage(url + idCompPath + "/picture");	
		}
	}
	
	public static void setPictureURL(User user, String urlBase) {
		if (user.getPictureBlob() == null) {
			user.setPicture(null);
		} else {
			String idCompPath = "/" + user.getUserId();
			user.setPicture(urlBase + idCompPath + "/picture");
		}
	}
	
	public static void setPictureURL(List<User> users, String urlBase) {
		for(User user : users) {
			setPictureURL(user, urlBase);
		}
	}
	
	public static void sendError(HttpServletResponse resp, Gson GSON, int status, WellBeResponse<Void> entity) throws IOException {
		resp.setStatus(status);
		resp.setContentType(Constants.CONTENT_TYPE_JSON);
		resp.getWriter().append(GSON.toJson(entity));	
	}
	
	public static void createFackeUserOne() {
		User user = new User();
		user.setEmail("fake.one@axa.com");
		List<String> goals = new ArrayList<String>();
		goals.add("nutrition");
		goals.add("physical activity");
		user.setGoals(goals);
		user.setLanguage("es");
		user.setName("Fake One");
		user.setPassword("123456");
		user.setPicture(fake_picture_one);
		
		Persistence.insert(user);
	}
	
	public static void createFackeUserTwo() {
		User user = new User();
		user.setEmail("fake.two@axa.com");
		List<String> goals = new ArrayList<String>();		
		goals.add("mental activity");
		goals.add("nutrition");
		user.setGoals(goals);
		user.setLanguage("es");
		user.setName("Fake Two");
		user.setPassword("123456");
		user.setPicture(fake_picture_two);
		
		Persistence.insert(user);
	}
	
	public static void createNewBoost(User owner) {
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
		boost.setGoal(owner.getGoals().get(0));
		boost.setOwnerId(owner.getUserId());
		boost.setPicture("http://blog.sportzone.es/wp-content/uploads/2014/10/keep-up-running-lake-28072011.jpg");
		boost.setStatus(Boost.Status.NEW.toString().toLowerCase());
		boost.setTitle("New Boost, goal: " + owner.getGoals().get(0) + ", owner: " + owner.getName());
		
		Persistence.insert(boost);
	}
	
	public static void createDoingBoosts(User user, User owner) {
		
		Calendar creation = Calendar.getInstance();
		creation.add(Calendar.MONTH, -1);

		Calendar start = Calendar.getInstance();
		start.add(Calendar.WEEK_OF_YEAR, -1);

		Calendar end = Calendar.getInstance();
		end.add(Calendar.WEEK_OF_YEAR, 5);
		
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
			boost.setTitle("Doing Boost (Ongoing), goal: " + s + ", owner: " + owner.getName());
			
			Persistence.insert(boost);
		}		
		
	}
	
	public static void createDoneBoosts(User user, User owner) {
		Calendar creation = Calendar.getInstance();
		creation.add(Calendar.MONTH, -1);

		Calendar start = (Calendar) creation.clone();
		start.add(Calendar.WEEK_OF_YEAR, 1);

		Calendar end = (Calendar) creation.clone();
		end.add(Calendar.WEEK_OF_YEAR, 2);
		
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
			boost.setTitle("Doing Boost (Accomplished), goal: " + s + ", owner: " + owner.getName());
			
			Persistence.insert(boost);
		}	
		
	}
	
	private static List<Link> getUserLinks(User user) {
		List<Link> links = new ArrayList<Link>();
		links.add(new Link("getBoosts", "/boosts"));
		links.add(new Link("createUser", "/users", Link.Method.POST.toString()));
		links.add(new Link("updateUser", "/users/" + user.getUserId().toString(), Link.Method.PUT.toString()));
		links.add(new Link("getUser", "/users/" + user.getUserId().toString(), Link.Method.GET.toString()));
		links.add(new Link("getUserFriends", "/users/" + user.getUserId().toString() + "/friends", Link.Method.GET.toString()));
		return links;
	}
	
	private static List<Link> getBoostLinks(Boost boost) {
		List<Link> links = new ArrayList<Link>();
		links.add(new Link("getBoosts", "/boosts"));
		links.add(new Link("getBoostParticipants", "/boosts/" + boost.getBoostId() + "/participants"));
		links.add(new Link("newBoostActivity", "/boosts/" + boost.getBoostId() + "/activity", Link.Method.POST.toString()));
		links.add(new Link("getBoostActivity", "/boosts/" + boost.getBoostId() + "/activity"));
		
		return links;
	}
	
	public static WellBeResponse<User> getCreateUserResponse(User user) {
		WellBeResponse<User> response = new WellBeResponse<User>();
		
		Status status = new Status(CREATED, "ok", "User created");	  
		
		response.setData(user);
		user.setLinks(getUserLinks(user));
		response.setStatus(status);
		
		return response;
	}
	
	public static WellBeResponse<UserBoosts> getUserBoostListResponse(List<Boost> boosts, List<User> people) {
		WellBeResponse<UserBoosts> response = new WellBeResponse<UserBoosts>();
		
		for(User p : people) {
			p.setLinks(getUserLinks(p));
		}
		
		for(Boost b : boosts) {
			b.setLinks(getBoostLinks(b));	
		}
		
		Status status = new Status(200, "ok", null);	  
		
		response.setData(new UserBoosts(boosts, people));		
		response.setStatus(status);
		
		return response;
	}
	
	public static WellBeResponse<User> getLoginUserResponse(User user) {
		WellBeResponse<User> response = getCreateUserResponse(user);
		user.setLinks(getUserLinks(user));
		
		response.setStatus(new Status(200, "ok", "User logged"));	  
		
		return response;
	}
	
	public static WellBeResponse<User> getUserResponse(User user) {
		WellBeResponse<User> response = getCreateUserResponse(user);
		user.setLinks(getUserLinks(user));
		
		response.setStatus(new Status(200, "ok", null));
	  	
		return response;
	}	
	
	public static WellBeResponse<User> getUpdateUserResponse(User user) {
		WellBeResponse<User> response = getCreateUserResponse(user);
		user.setLinks(getUserLinks(user));
		
		response.setStatus(new Status(200, "ok", "User updated"));
	  	
		return response;
	}		
	
	
	public static WellBeResponse<Void> getBadRequestResponse(String description) throws IOException {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(400, "Bad Request", description));
		
		return response;
	}
	
	public static WellBeResponse<Void> getUnauthorizedResponse(String description) throws IOException {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(401, "Unauthorized", description));	
		
		return response;
	}
	
	public static WellBeResponse<Void> getForbiddenResponse(String description) {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(403, "Forbidden", description));	
		
		return response;
	}
	
	public static WellBeResponse<Void> getNotFoundResponse(String description) throws IOException {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(404, "Not Found", description));	  
		
		return response;
	}

	public static WellBeResponse<Void> getConflictResponse(String description) throws IOException {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(409, "Conflict", description));
		
		return response;
	}
	

	public static WellBeResponse<Void> getNoSuchAccountResponse(String description) throws IOException {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(NO_SUCH_ACCOUNT, "No Such Account", description));	
		
		return response;
	}
	
	public static WellBeResponse<Void> getInternalServerErrorResponse(String description) {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(500, "Internal Server Error", description));	
		
		return response;
	}
	
	public static WellBeResponse<Void> getNoContentResponse(String description) throws IOException {
		WellBeResponse<Void> response = new WellBeResponse<Void>();	
		
		response.setStatus(new Status(NO_CONTENT, "No Content", description));	
		
		return response;
	}

	public static WellBeResponse<List<User>> getUserFriendsResponse(List<User> friends) {
		WellBeResponse<List<User>> response = new WellBeResponse<List<User>>();
		
		for(User f : friends) {
			f.setLinks(getUserLinks(f));
		}
		
		Status status = new Status(200, "ok", null);	  
		
		
		response.setData(friends);		
		response.setStatus(status);
		
		return response;
	}
	
	public static WellBeResponse<BoostParticipants> getBoostParticipantsResponse(List<User> participants, List<User> pending) {
		WellBeResponse<BoostParticipants> response = new WellBeResponse<BoostParticipants>();
		
		for(User f : participants) {
			f.setLinks(getUserLinks(f));
		}
		
		for(User f : pending) {
			f.setLinks(getUserLinks(f));
		}
		
		Status status = new Status(200, "ok", null);	
		
		BoostParticipants bp = new BoostParticipants();
		bp.setParticipants(participants);
		bp.setPending(pending);
		
		response.setData(bp);		
		response.setStatus(status);
		
		return response;
	}

	public static WellBeResponse<BoostActivity> getCreateBoostActivityResponse(BoostActivity ba) {
		WellBeResponse<BoostActivity> response = new WellBeResponse<BoostActivity>();
		
		Status status = new Status(CREATED, "ok", "Activity created");	  
		
		response.setData(ba);
		ba.setLinks(null);
		response.setStatus(status);
		
		return response;
	}
	

}
