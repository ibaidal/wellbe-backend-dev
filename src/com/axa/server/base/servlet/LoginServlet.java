package com.axa.server.base.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.axa.server.base.Constants;
import com.axa.server.base.auth.Session;
import com.axa.server.base.persistence.EMFService;
import com.axa.server.base.persistence.Persistence;
import com.axa.server.base.persistence.UserDAO;
import com.axa.server.base.pods.FacebookUser;
import com.axa.server.base.pods.GoogleUser;
import com.axa.server.base.pods.User;
import com.axa.server.base.util.Utils;
import com.axa.server.base.util.ValidationUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;


@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	
    private static final Logger log = Logger.getLogger(LoginServlet.class.getName());

	private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create(); 

	private static final String FACEBOOK_USER_URL = "https://graph.facebook.com/me?access_token=%s";
	private static final String FACEBOOK_USER_PICTURE_URL = "http://graph.facebook.com/%s/picture?height=" + 
			String.valueOf(Constants.IMAGE_MAX_SIZE_PX) + "&width=" + 
			String.valueOf(Constants.IMAGE_MAX_SIZE_PX) + "&type=square";

	
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setHeader("Allow", "POST");
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}


	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		User user = null;
		
		String[] pathItems = req.getRequestURI().split("/");
		String action = pathItems.length > 2 ? pathItems[2] : null;

		if ("facebook".equals(action)) {
			user = facebookLogin(
					req.getParameter("userId"),
					req.getParameter("token"));
			
		} else {
			user = appLogin(
					req.getParameter("email"),
					req.getParameter("password"));
		}
		
		if (user == null) {
			log.warning("Login failed");
			//resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");			
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(Utils.getUnauthorizedResponse("Invalid credentials")));	
		} else {
			Session.addNewTokenForUserId(user.getUserId(), resp);
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(Utils.getLoginUserResponse(user)));
		}
	}
	
	
	private User appLogin(String email, String password) {
		if (ValidationUtil.anyEmpty(email, password)) return null;
		return Persistence.getUserByEmailAndPassword(email, password);
	}
	
	
	
	private User facebookLogin(String userId, String token) {
		if (ValidationUtil.anyEmpty(userId, token)) return null;

		User user = null;
		FacebookUser fbUser = null;
		
		try {
			URL url = new URL(String.format(FACEBOOK_USER_URL, token));
			Reader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			fbUser = new Gson().fromJson(reader, FacebookUser.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (fbUser != null) {
			EntityManager em = EMFService.createEntityManager();
			
			try {
				em.getTransaction().begin();
				user = UserDAO.byEmail(em, fbUser.email);
				
				if (user == null) {
					// Register
					user = new User();
					user.setEmail(fbUser.email);
					user.setFbId(fbUser.id);
					user.setName((fbUser.first_name + " " + fbUser.last_name).trim());
					// TODO GET FACEBOOK USER PROFILE PICTURE
					em.persist(user);	
					
				} else if (ValidationUtil.isEmpty(user.getFbId())) {
					user.setFbId(fbUser.id);
					em.persist(user);	
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
		
		return user;
	}
	

}
