package com.axa.server.base.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.axa.server.base.Constants;
import com.axa.server.base.auth.Session;
import com.axa.server.base.persistence.EMFService;
import com.axa.server.base.persistence.Persistence;
import com.axa.server.base.persistence.UserDAO;
import com.axa.server.base.pods.Recipe;
import com.axa.server.base.pods.User;
import com.axa.server.base.response.Status;
import com.axa.server.base.util.StringUtil;
import com.axa.server.base.util.Utils;
import com.axa.server.base.util.ValidationUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


@SuppressWarnings("serial")
public class UsersServlet extends HttpServlet {

	private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create(); 
	

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		User user = null;
		
		try {
			String[] pathItems = req.getRequestURI().split("/");
			long userId = Long.parseLong(pathItems[2]);
			user = Persistence.getUserById(userId);
			
			if (user == null) {
				//resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				resp.setContentType(Constants.CONTENT_TYPE_JSON);
				resp.getWriter().append(GSON.toJson(Utils.getNotFoundResponse(null)));	
			} else {
				resp.setContentType(Constants.CONTENT_TYPE_JSON);
				resp.getWriter().append(GSON.toJson(Utils.getUserResponse(user)));
			}
			
		} catch (Exception e) {
			//resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(Utils.getBadRequestResponse(null)));
		}
	}


	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doRegister(req, resp);		
	}
	
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		long userId = 0;
		try {
			String[] pathItems = req.getRequestURI().split("/");
			if (pathItems.length > 2) {
				userId = Long.parseLong(pathItems[2]);
			}
		} catch (Exception ignored) {
			// Ignore
		}
		
		if (!Session.checkSignature(req)) {
			//resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(Utils.getUnauthorizedResponse(null)));
		} else if (userId != 0) {
			doUpdate(req, resp, userId);
		}	
		
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
			//resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty fields");
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(Utils.getBadRequestResponse("Empty fields")));
		} else if (!ValidationUtil.validateEmail(user.getEmail())) {
			//resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email");
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(Utils.getBadRequestResponse("Invalid email")));
		} else if (Persistence.getUserByEmail(user.getEmail()) != null) {
			//resp.sendError(HttpServletResponse.SC_CONFLICT, "Email already registered");
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(Utils.getConflictResponse("Email already registered")));
		} else {
			Persistence.insert(user);
			Session.addNewTokenForUserId(user.getUserId(), resp);
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(Utils.getCreateUserResponse(user)));
		}
	}

	
	public void doUpdate(HttpServletRequest req, HttpServletResponse resp, long userId) throws IOException {
		EntityManager em = EMFService.createEntityManager();
		
		try {
			em.getTransaction().begin();
			
			User user = UserDAO.byId(em, userId);
			
			if (user == null) {
				//resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				resp.setContentType(Constants.CONTENT_TYPE_JSON);
				resp.getWriter().append(GSON.toJson(Utils.getNotFoundResponse(null)));			
			} else {				
				user.setName(req.getParameter("name"));
				user.setPicture(req.getParameter("picture"));
				user.setLanguage(req.getHeader("language"));
				user.setPassword(req.getParameter("password"));
				user.setGoals(StringUtil.getStringListFromString(req.getParameter("goals"), ","));
				
				if (ValidationUtil.anyEmpty(user.getGoals(), user.getName(), user.getPassword())) {
					//resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty fields");
					resp.setContentType(Constants.CONTENT_TYPE_JSON);
					resp.getWriter().append(GSON.toJson(Utils.getBadRequestResponse("Empty fields")));
				} else if (!user.getEmail().equalsIgnoreCase(req.getParameter("email"))) {
					//resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email");
					resp.setContentType(Constants.CONTENT_TYPE_JSON);
					resp.getWriter().append(GSON.toJson(Utils.getBadRequestResponse("Invalid email")));
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
