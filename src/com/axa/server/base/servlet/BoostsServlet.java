package com.axa.server.base.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.axa.server.base.Constants;
import com.axa.server.base.auth.Session;
import com.axa.server.base.persistence.EMFService;
import com.axa.server.base.persistence.Persistence;
import com.axa.server.base.persistence.UserDAO;
import com.axa.server.base.pods.Boost;
import com.axa.server.base.pods.Recipe;
import com.axa.server.base.pods.Token;
import com.axa.server.base.pods.User;
import com.axa.server.base.response.Status;
import com.axa.server.base.util.StringUtil;
import com.axa.server.base.util.Utils;
import com.axa.server.base.util.ValidationUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


@SuppressWarnings("serial")
public class BoostsServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(BoostsServlet.class.getName());
	
	private static final Gson GSON = new GsonBuilder().
			setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ").
			excludeFieldsWithoutExposeAnnotation().create(); 
	

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		if (!Session.checkSignature(req)) {
			
			Utils.sendError(resp, GSON, HttpServletResponse.SC_UNAUTHORIZED, Utils.getUnauthorizedResponse(null));
			
		}
		else {
			try {
				
				Boost boost = null;
				long boostId = 0;
				
				String[] pathItems = req.getRequestURI().split("/");
				if (pathItems.length > 2) {
					boostId = Long.parseLong(pathItems[2]);
					boost = Persistence.getBoostById(boostId);
					
					if (boost == null) {
						
						Utils.sendError(resp, GSON, HttpServletResponse.SC_NOT_FOUND, Utils.getNotFoundResponse(null));
						
					} else {
						
						log.warning(boost.toString());
						
						// Add Users Mockup to DB						
						if(Persistence.getUserByEmail("fake.one@axa.com") == null) {
							Utils.createFackeUserOne();
							Utils.createFackeUserTwo();
						}
						
						User user = Persistence.getUserById(boost.getOwnerId());
						List<Boost> boosts = new ArrayList<Boost>();
						boosts.add(boost);
						List<User> users = new ArrayList<User>();
						setPictureURL(user, req);
						users.add(user);
						
						// One Boost
						resp.setContentType(Constants.CONTENT_TYPE_JSON);
						resp.getWriter().append(GSON.toJson(Utils.getUserBoostListResponse(boosts, users)));
					}
					
				}
				else {					
					// LIST OF BOOSTS
					Token token = Session.getToken(req);
					log.warning(token.toString());
					
					User user = Persistence.getUserById(token.getUserId());
					log.warning(user.toString());
					
					List<Boost> boosts = Persistence.getAllBoosts();					
					log.warning(""+boosts.size());
					
					List<User> users = new ArrayList<User>();
					setPictureURL(user, req);
					users.add(user);
					User aux = Persistence.getUserByEmail("fake.one@axa.com");
					aux.setPicture(Utils.fake_picture_one);
					users.add(aux);
					aux = Persistence.getUserByEmail("fake.two@axa.com");
					aux.setPicture(Utils.fake_picture_two);
					users.add(aux);
					
					log.warning(boosts.toString());
					log.warning(users.toString());
					
					resp.setContentType(Constants.CONTENT_TYPE_JSON);
					resp.getWriter().append(GSON.toJson(Utils.getUserBoostListResponse(boosts, users)));				
				}
									
				
			} catch (Exception e) {
				
				Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse(null));
				
			}
		}

	}
	
	private void setPictureURL(User user, HttpServletRequest req) {
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


	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setHeader("Allow", "GET");
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		
		//doRegister(req, resp);		
	}
	
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		resp.setHeader("Allow", "GET");
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		
		/*long boostId = 0;
		try {
			String[] pathItems = req.getRequestURI().split("/");
			if (pathItems.length > 2) {
				boostId = Long.parseLong(pathItems[2]);
			}
		} catch (Exception ignored) {
			// Ignore
		}
		
		if (!Session.checkSignature(req)) {
		
			Utils.sendError(resp, GSON, HttpServletResponse.SC_UNAUTHORIZED, Utils.getUnauthorizedResponse(null));
			
		} else if (boostId != 0) {
			doUpdate(req, resp, boostId);
		}*/	
		
	}

	
	public void doRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		User user = new User();
		user.setEmail(req.getParameter("email"));
		user.setName(req.getParameter("name"));
		user.setPicture(req.getParameter("picture"));
		user.setLanguage(req.getHeader("language"));
		user.setPassword(req.getParameter("password"));
		user.getGoals().addAll(StringUtil.getStringListFromString(req.getParameter("goals"), ","));
				
		if (ValidationUtil.anyEmpty(user.getGoals(), user.getName(), user.getEmail(), user.getPassword())) {

			Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse("Empty fields"));
			
		} else if (!ValidationUtil.validateEmail(user.getEmail())) {
			
			Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse("Invalid email"));
			
		} else if (Persistence.getUserByEmail(user.getEmail()) != null) {
			
			Utils.sendError(resp, GSON, HttpServletResponse.SC_CONFLICT, Utils.getConflictResponse("Email already registered"));
			
		} else {						
			Persistence.insert(user);
			Session.addNewTokenForUserId(user.getUserId(), resp);
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(Utils.getCreateUserResponse(user)));
			
			
			// Add Boost Mockup to DB
			Utils.createNewBoost(user);
			Utils.createDoingBoosts(user);
			Utils.createDoneBoosts(user);
		}
	}

	
	public void doUpdate(HttpServletRequest req, HttpServletResponse resp, long userId) throws IOException {
		EntityManager em = EMFService.createEntityManager();
		
		try {
			em.getTransaction().begin();
			
			User user = UserDAO.byId(em, userId);
			
			if (user == null) {
				
				Utils.sendError(resp, GSON, HttpServletResponse.SC_NOT_FOUND, Utils.getNotFoundResponse(null));
				
			} else {				
				user.setName(req.getParameter("name"));
				user.setPicture(req.getParameter("picture"));
				user.setLanguage(req.getHeader("language"));
				user.setPassword(req.getParameter("password"));
				user.setGoals(StringUtil.getStringListFromString(req.getParameter("goals"), ","));
				
				if (ValidationUtil.anyEmpty(user.getGoals(), user.getName(), user.getPassword())) {
					
					Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse("Empty fields"));
					
				} else if (!user.getEmail().equalsIgnoreCase(req.getParameter("email"))) {
					
					Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse("Invalid email"));
					
				} else {
					em.persist(user);
					resp.setContentType(Constants.CONTENT_TYPE_JSON);
					resp.getWriter().append(GSON.toJson(Utils.getUpdateUserResponse(user)));
				}
			}
			
	    	em.flush();
	    	em.getTransaction().commit();
			
		} catch (Exception e) {
	    	em.getTransaction().rollback();
	    	throw e;
	    } finally {
			em.close();
	    }
	}
	
}
