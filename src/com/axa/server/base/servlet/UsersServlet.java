package com.axa.server.base.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.axa.server.base.Constants;
import com.axa.server.base.auth.Session;
import com.axa.server.base.persistence.EMFService;
import com.axa.server.base.persistence.Persistence;
import com.axa.server.base.persistence.UserDAO;
import com.axa.server.base.pods.User;
import com.axa.server.base.util.StringUtil;
import com.axa.server.base.util.Utils;
import com.axa.server.base.util.ValidationUtil;
import com.google.appengine.api.datastore.Blob;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@SuppressWarnings("serial")
public class UsersServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(UsersServlet.class.getName());
	
	private static final Gson GSON = new GsonBuilder().
			setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ").
			excludeFieldsWithoutExposeAnnotation().create();  
	

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		if (!Session.checkSignature(req)) {
			
			Utils.sendError(resp, GSON, HttpServletResponse.SC_UNAUTHORIZED, Utils.getUnauthorizedResponse(null));
			
		}
		else {
			User user = null;
			
			try {
				String[] pathItems = req.getRequestURI().split("/");
				long userId = pathItems.length > 2 ? Long.parseLong(pathItems[2]) : -1;
				String action = pathItems.length > 3 ? pathItems[3] : null;
				user = Persistence.getUserById(userId);	
				
				if (user == null) {

					Utils.sendError(resp, GSON, HttpServletResponse.SC_NOT_FOUND, Utils.getNotFoundResponse(null));
					
				} else if ("picture".equals(action)) {									
					
					if (user.getPictureBlob() == null) {
						
						Utils.sendError(resp, GSON, HttpServletResponse.SC_NOT_FOUND, Utils.getNotFoundResponse(null));
						
					} else {
						resp.setContentType("image/*");
						resp.getOutputStream().write(user.getPictureBlob().getBytes());
					}
					
				} else {
					Utils.setPictureURL(user, req);
					resp.setContentType(Constants.CONTENT_TYPE_JSON);
					resp.getWriter().append(GSON.toJson(Utils.getUserResponse(user)));
				}
				
			} catch (Exception e) {

				Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse(null));
				
			}	
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

			Utils.sendError(resp, GSON, HttpServletResponse.SC_UNAUTHORIZED, Utils.getUnauthorizedResponse(null));
			
		} else if (userId != 0) {
			doUpdate(req, resp, userId);
		} else {
			
			Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse(null));
			
		}
		
	}

	
	public void doRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
				
		User user = new User();
		
		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(req);
			
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				String name = item.getFieldName();
				InputStream is = item.openStream();
				
				if (item.isFormField()) {
					String value = IOUtils.toString(is, "UTF-8");
					if ("email".equals(name)) {
						user.setEmail(value);
					} else if ("name".equals(name)) {
						user.setName(value);
					} else if ("password".equals(name)) {
						user.setPassword(value);
					} else if ("goals".equals(name)) {
						user.getGoals().addAll(StringUtil.getStringListFromString(value, ","));
					}					
				} else if ("picture".equals(name)) {
					user.setPictureBlob(new Blob(IOUtils.toByteArray(is)));
				}
								
			}
			
			user.setLanguage(req.getHeader("language"));
			
		} catch (Exception e) {
			throw new IOException(e);
		}
		
		
		log.warning(user == null ? "User null" : "User: " + user.toString());
		
				
		if (ValidationUtil.anyEmpty(user.getGoals(), user.getName(), user.getEmail(), user.getPassword())) {
			
			Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse("Empty fields"));	
			
		} else if (!ValidationUtil.validateEmail(user.getEmail())) {
			
			Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse("Invalid email"));
			
		} else if (Persistence.getUserByEmail(user.getEmail()) != null) {
			
			Utils.sendError(resp, GSON, HttpServletResponse.SC_CONFLICT, Utils.getConflictResponse("Email already registered"));
			
		} else {			
			Persistence.insert(user);
			Utils.setPictureURL(user, req);
			Session.addNewTokenForUserId(user.getUserId(), resp);
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(Utils.getCreateUserResponse(user)));
			
			User owner = Persistence.getUserByEmail("fake.one@axa.com");
			// Add Users Mockup to DB						
			if(owner == null) {
				Utils.createFackeUserOne();
				Utils.createFackeUserTwo();
				owner = Persistence.getUserByEmail("fake.one@axa.com");
				// Add Boost Mockup to DB
				Utils.createNewBoost(user);
				
				owner = Persistence.getUserByEmail("fake.two@axa.com");
				
				// Add Boost Mockup to DB
				Utils.createDoingBoosts(user, owner);
				Utils.createDoneBoosts(user, owner);
			}					

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

				try {
					ServletFileUpload upload = new ServletFileUpload();
					FileItemIterator iterator = upload.getItemIterator(req);
					
					while (iterator.hasNext()) {
						FileItemStream item = iterator.next();
						String name = item.getFieldName();
						InputStream is = item.openStream();
						
						if (item.isFormField()) {
							String value = IOUtils.toString(is, "UTF-8");
							if ("email".equals(name)) {
								user.setEmail(value);
							} else if ("name".equals(name)) {
								user.setName(value);
							} else if ("password".equals(name)) {
								user.setPassword(value);
							} else if ("goals".equals(name)) {
								user.setPassword(value);
								user.setGoals(StringUtil.getStringListFromString(value, ","));
							}					
						} else if ("picture".equals(name)) {
							user.setPictureBlob(new Blob(IOUtils.toByteArray(is)));
						}
										
					}
					
					user.setLanguage(req.getHeader("language"));
					
				} catch (Exception e) {
					throw new IOException(e);
				}
				
				log.warning(user == null ? "User null" : "User: " + user.toString());
				
				if (ValidationUtil.anyEmpty(user.getGoals(), user.getName(), user.getPassword())) {
					
					Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse("Empty fields"));
					
				} else if (!user.getEmail().equalsIgnoreCase(req.getParameter("email"))) {
					
					log.warning("Email can not be modified");
					
					Utils.sendError(resp, GSON, HttpServletResponse.SC_BAD_REQUEST, Utils.getBadRequestResponse("Invalid email"));
					
				} else {
					em.persist(user);
					Utils.setPictureURL(user, req);					
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
