package com.axa.server.base.servlet;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.axa.server.base.Constants;
import com.axa.server.base.auth.Session;
import com.axa.server.base.persistence.EMFService;
import com.axa.server.base.persistence.Persistence;
import com.axa.server.base.persistence.UserDAO;
import com.axa.server.base.pods.User;
import com.axa.server.base.util.ValidationUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


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
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else {
				resp.setContentType(Constants.CONTENT_TYPE_JSON);
				resp.getWriter().append(GSON.toJson(user));
			}
			
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}


	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		long userId = 0;
		try {
			String[] pathItems = req.getRequestURI().split("/");
			if (pathItems.length > 2) {
				userId = Long.parseLong(pathItems[2]);
			}
		} catch (Exception ignored) {
			// Ignore
		}

		if (userId == 0) {
			doRegister(req, resp);
		} else if (!Session.checkSignature(req)) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			doUpdate(req, resp, userId);
		}
	}

	
	public void doRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		User user = new User();
		user.setEmail(req.getParameter("email"));
		user.setName(req.getParameter("name"));
		user.setPhone(req.getParameter("phone"));
		user.setAddress(req.getParameter("address"));
		user.setPassword(req.getParameter("password"));
				
		if (ValidationUtil.anyEmpty(user.getName(), user.getEmail(), user.getPassword())) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty fields");
		} else if (!ValidationUtil.validateEmail(user.getEmail())) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email");
		} else if (Persistence.getUserByEmail(user.getEmail()) != null) {
			resp.sendError(HttpServletResponse.SC_CONFLICT, "Email already registered");
		} else {
			Persistence.insert(user);
			Session.addNewTokenForUserId(user.getUserId(), resp);
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(user));
		}
	}

	
	public void doUpdate(HttpServletRequest req, HttpServletResponse resp, long userId) throws IOException {
		EntityManager em = EMFService.createEntityManager();
		
		try {
			em.getTransaction().begin();
			
			User user = UserDAO.byId(em, userId);
			
			if (user == null) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				
			} else {
				user.setName(req.getParameter("name"));
				user.setPhone(req.getParameter("phone"));
				user.setAddress(req.getParameter("address"));
	
				if (ValidationUtil.isEmpty(user.getName())) {
					resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty fields");
				} else {
					em.persist(user);
					resp.setContentType(Constants.CONTENT_TYPE_JSON);
					resp.getWriter().append(GSON.toJson(user));
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
