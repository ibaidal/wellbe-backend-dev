package com.axa.server.base.servlet;

import java.io.BufferedReader;
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
import com.axa.server.base.util.ValidationUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;


@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	
    private static final Logger log = Logger.getLogger(LoginServlet.class.getName());

	private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create(); 

	private static final String FACEBOOK_USER_URL = "https://graph.facebook.com/me?access_token=%s";

	
	private static class PassAXAToken {
		@Expose public String email;
		@Expose public String timestamp;
		@Expose public String apptoken;
		@Expose public String token;
	}
	
	
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

		if ("passaxa".equals(action)) {
			user = passAxaLogin(
					req.getParameter("email"),
					req.getParameter("token"),
					req.getParameter("timestamp"),
					req.getParameter("appToken"));
			
		} else if ("facebook".equals(action)) {
			user = facebookLogin(
					req.getParameter("userId"),
					req.getParameter("token"));
			
		} else if ("google".equals(action)) {
			user = googlePlusLogin(
					req.getParameter("userId"),
					req.getParameter("token"));
			
		} else {
			user = appLogin(
					req.getParameter("email"),
					req.getParameter("password"));
		}
		
		if (user == null) {
			log.warning("Login failed");
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
		} else {
			Session.addNewTokenForUserId(user.getUserId(), resp);
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(user));
		}
	}
	
	
	private User appLogin(String email, String password) {
		if (ValidationUtil.anyEmpty(email, password)) return null;
		return Persistence.getUserByEmailAndPassword(email, password);
	}
	
	
	private User passAxaLogin(String email, String token, String timestamp, String appToken) {
		if (ValidationUtil.anyEmpty(email, token, timestamp)) return null;

		User user = null;
		HttpURLConnection urlConnection = null;

        try {
        	PassAXAToken paxa = new PassAXAToken();
			paxa.email = email;
			paxa.token = token;
			paxa.timestamp = timestamp;
			paxa.apptoken = appToken;
			
            URL url = new URL("https://apps.axa.com/TokenValidation/CheckValidToken");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStream os = urlConnection.getOutputStream();
            os.write(GSON.toJson(paxa).getBytes("UTF-8"));
            os.close();
            
            String response = IOUtils.toString(urlConnection.getInputStream());
            boolean validToken = Boolean.parseBoolean(response);
            
            if (validToken) {
    			user = Persistence.getUserByEmail(email);
    			if (user == null) {
    				// Register
    				user = new User();
    				user.setEmail(email);
    				user.setName(userNameFromEmail(email));
    				Persistence.insert(user);
    			}
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        
		return user;
	}
	
	
	private String userNameFromEmail(String email) {
		return (email == null)
			? null
			: WordUtils.capitalizeFully(email.split("@")[0].replaceAll("\\.", " "));
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
	
	
	private User googlePlusLogin(String userId, String token) {
		if (ValidationUtil.anyEmpty(userId, token)) return null;

		User user = null;
		GoogleUser gpUser = null;
		
		HttpURLConnection urlConnection = null;

        try {
            URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);

			Reader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			gpUser = new Gson().fromJson(reader, GoogleUser.class);
			
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        
		if (gpUser != null && gpUser.id != null) {
			EntityManager em = EMFService.createEntityManager();
			
			try {
				em.getTransaction().begin();
				user = UserDAO.byEmail(em, gpUser.email);
				
				if (user == null) {
					// Register
					user = new User();
					user.setEmail(gpUser.email);
					user.setGpId(gpUser.id);
					user.setName((gpUser.given_name + " " + gpUser.family_name).trim());
					em.persist(user);	

				} else if (ValidationUtil.isEmpty(user.getGpId())) {
					user.setGpId(gpUser.id);
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
