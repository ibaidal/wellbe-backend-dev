package com.axa.server.base.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.axa.server.base.Constants;
import com.axa.server.base.auth.Session;
import com.axa.server.base.persistence.Persistence;
import com.axa.server.base.pods.Boost;
import com.axa.server.base.pods.Token;
import com.axa.server.base.pods.User;
import com.axa.server.base.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


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
					
					log.warning(boost == null ? "Boost null" : "Boost: " + boost.toString());
					
					if (boost == null) {
						
						Utils.sendError(resp, GSON, HttpServletResponse.SC_NOT_FOUND, Utils.getNotFoundResponse(null));
						
					} else {
						
						User user = Persistence.getUserById(boost.getOwnerId());
						List<Boost> boosts = new ArrayList<Boost>();
						boosts.add(boost);
						List<User> users = new ArrayList<User>();
						Utils.setPictureURL(user, req);
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
					log.warning("User: " + user.toString());
					
					User ownerOne = Persistence.getUserByEmail("fake.one@axa.com");
					log.warning("User One: " + ownerOne.toString());
					User ownerTwo = Persistence.getUserByEmail("fake.two@axa.com");
					log.warning("User Two: " + ownerTwo.toString());
					
					List<Boost> boosts = Persistence.getAllBoosts();
					log.warning("Boosts size: " + boosts.size());
										
					
					List<User> users = new ArrayList<User>();
					Utils.setPictureURL(user, req);
					users.add(user);
					User aux = Persistence.getUserByEmail("fake.one@axa.com");
					aux.setPicture(Utils.fake_picture_one);
					users.add(aux);
					aux = Persistence.getUserByEmail("fake.two@axa.com");
					aux.setPicture(Utils.fake_picture_two);
					users.add(aux);
					log.warning("Users size: " + users.size());
					
					resp.setContentType(Constants.CONTENT_TYPE_JSON);
					resp.getWriter().append(GSON.toJson(Utils.getUserBoostListResponse(boosts, users)));				
				}
									
				
			} catch (Exception e) {
				
				Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse(null));
				
			}
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
	}

	
}
