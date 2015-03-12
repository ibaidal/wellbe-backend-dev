package com.axa.server.base.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
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
import com.axa.server.base.pods.FacebookUser;
import com.axa.server.base.pods.User;
import com.axa.server.base.util.Utils;
import com.axa.server.base.util.ValidationUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	
    private static final Logger log = Logger.getLogger(LoginServlet.class.getName());

	private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create(); 

	private static final String FACEBOOK_USER_URL = "https://graph.facebook.com/me?access_token=%s";
	
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		User user = null;
		
		String[] pathItems = req.getRequestURI().split("/");
		String action = pathItems.length > 2 ? pathItems[2] : null;

		if ("facebook".equals(action)) {
			user = facebookLogin(
					req.getParameter("userId"),
					req.getParameter("token"),
					resp);
			
		} else {
			user = appLogin(
					req.getParameter("email"),
					req.getParameter("password"));
		}
		
		
		log.warning(user == null ? "User null" : "User: " + user.toString());
		
		
		if (user == null) {
			
			if("facebook".equals(action)) {
				Utils.sendError(resp, GSON, Utils.NO_SUCH_ACCOUNT, Utils.getNoSuchAccountResponse("The email address doesn’t exist."));
			}
			else {
				Utils.sendError(resp, GSON, HttpServletResponse.SC_UNAUTHORIZED, Utils.getUnauthorizedResponse("Invalid credentials"));
			}
			
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
	
	
	
	private User facebookLogin(String userId, String token, HttpServletResponse resp) throws IOException {
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
						
			user = UserDAO.byEmail(em, fbUser.email);
			
			if (user != null & 
				ValidationUtil.isEmpty(user.getFbId())) {
				
				try {	
					em.getTransaction().begin();
					
					user.setFbId(fbUser.id);
					em.persist(user);				
					
					log.warning("FB user updated");
					
			    	em.flush();
			    	em.getTransaction().commit();
		    	
				} catch (Exception e) {
			    	em.getTransaction().rollback();	
			    	e.printStackTrace();
			    	throw e;
			    } finally {
					em.close();
			    }
				
			}
			
		}
		
		return user;
	}
	

}
