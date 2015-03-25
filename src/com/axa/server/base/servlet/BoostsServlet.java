package com.axa.server.base.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.axa.server.base.Constants;
import com.axa.server.base.auth.Session;
import com.axa.server.base.persistence.Persistence;
import com.axa.server.base.pods.Boost;
import com.axa.server.base.pods.BoostActivity;
import com.axa.server.base.pods.Token;
import com.axa.server.base.pods.User;
import com.axa.server.base.pods.UserNameComparator;
import com.axa.server.base.util.StringUtil;
import com.axa.server.base.util.Utils;
import com.axa.server.base.util.ValidationUtil;
import com.google.appengine.api.datastore.Blob;
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
					String action = pathItems.length > 3 ? pathItems[3] : null;
					
					
					if (boost == null) {
						
						Utils.sendError(resp, GSON, HttpServletResponse.SC_NOT_FOUND, Utils.getNotFoundResponse(null));
						
					} else if ("participants".equals(action)) {		
						
						User user = Persistence.getUserById(boost.getOwnerId());
						List<User> friends = Persistence.getUserFriends(user.getUserId());
						List<User> participants = friends.subList(0, friends.size()/2);
						participants.add(user);
						List<User> pending = friends.subList(friends.size()/2, friends.size());
						
						// No participants and no pending
						if(participants.isEmpty() && pending.isEmpty()) {
							Utils.sendError(resp, GSON, Utils.NO_CONTENT, Utils.getNoContentResponse("The boost has no participants and pending requests"));
						}
						else {
							// Alphabetical order by name
							Collections.sort(participants, new UserNameComparator());
							Collections.sort(pending, new UserNameComparator());
							
							// Add picture: Removes /boosts/{boostId}/participants from URL to have /users/{participant userId}/picture
							String urlBase = req.getRequestURL().toString();
							urlBase = urlBase.substring(0, urlBase.lastIndexOf("/"));
							urlBase = urlBase.substring(0, urlBase.lastIndexOf("/"));
							urlBase = urlBase.substring(0, urlBase.lastIndexOf("/"));
							Utils.setPictureURL(participants, urlBase);
							Utils.setPictureURL(pending, urlBase);
							
							resp.setContentType(Constants.CONTENT_TYPE_JSON);
							resp.getWriter().append(GSON.toJson(Utils.getBoostParticipantsResponse(participants, pending)));
						}
						
					} else {
						
						User user = Persistence.getUserById(boost.getOwnerId());
						List<Boost> boosts = new ArrayList<Boost>();
						boosts.add(boost);
						List<User> users = new ArrayList<User>();
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
									
				
			} catch (NumberFormatException e) {

				Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse(null));
				
			} catch (Exception e) {

				Utils.sendError(resp, GSON, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Utils.getInternalServerErrorResponse(e.getMessage()));
				
			}	
			
			
		}

	}


	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		if (false/*!Session.checkSignature(req)*/) {
			
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
					String action = pathItems.length > 3 ? pathItems[3] : null;

					
					if (boost == null) {
						
						Utils.sendError(resp, GSON, HttpServletResponse.SC_NOT_FOUND, Utils.getNotFoundResponse(null));
						
					} else if ("activity".equals(action)) {	
						
						doAddBoostActivityAction(req, resp, boost);
						
						
					} else {
						
						Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse(null));
					}
					
				}
				else {					
					Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse(null));
				}
									
				
			} catch (NumberFormatException e) {

				Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse(null));
				
			} catch (Exception e) {

				Utils.sendError(resp, GSON, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Utils.getInternalServerErrorResponse(e.getMessage()));
				
			}	
			
			
		}

	}
	
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {		
		resp.setHeader("Allow", "GET");
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);		
	}
	
	private void doAddBoostActivityAction(HttpServletRequest req, HttpServletResponse resp, Boost boost) throws Exception {
		
		BoostActivity ba = new BoostActivity();
		

		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iterator = upload.getItemIterator(req);
		
		while (iterator.hasNext()) {
			FileItemStream item = iterator.next();
			String name = item.getFieldName();
			InputStream is = item.openStream();
			
			if (item.isFormField()) {
				String value = IOUtils.toString(is, "UTF-8");
				if ("type".equals(name)) {
					ba.setType(value);
				} else if ("text".equals(name)) {
					ba.setText(value);
				} else if ("latitude".equals(name)) {
					ba.setLatitude(Double.valueOf(value));
				} else if ("longitude".equals(name)) {
					ba.setLongitude(Double.valueOf(value));
				} else if ("place".equals(name)) {
					ba.setPlace(value);
				} else if ("googlePlaceId".equals(name)) {
					ba.setPlaceId(value);
				}					
				
			} else if ("image".equals(name)) {
				ba.setImageBlob(new Blob(IOUtils.toByteArray(is)));
			}
							
		}
			
			
		
		ba.setBoostId(boost.getBoostId());
		ba.setUserId(boost.getOwnerId());
		ba.setCreation(Calendar.getInstance().getTime());
		
		
		if (ba.getType() == null) {
			
			Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse("Empty type"));	
			
		} else if (!ba.getType().equalsIgnoreCase(BoostActivity.Type.PROOF.toString()) && !ba.getType().equalsIgnoreCase(BoostActivity.Type.TIP.toString())) {
			
			Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse("Invalid type"));
			
		} else {
			
			Persistence.insert(ba);
			Utils.setPictureURL(ba, req);			
	    	
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(Utils.getCreateBoostActivityResponse(ba)));
		}
		
	}

	
}
