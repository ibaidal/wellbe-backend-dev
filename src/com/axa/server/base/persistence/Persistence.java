package com.axa.server.base.persistence;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.axa.server.base.pods.Gcm;
import com.axa.server.base.pods.Recipe;
import com.axa.server.base.pods.Token;
import com.axa.server.base.pods.User;
import com.axa.server.base.pods.Boost;
import com.axa.server.base.pods.UserNameComparator;


public class Persistence {

	
	public static void insert(Object obj) {
		EntityManager em = EMFService.createEntityManager();
		em.getTransaction().begin();
		em.persist(obj);
    	em.flush();
    	em.getTransaction().commit();
		em.close();
	}
	
	
	public static void remove(Object obj) {
		EntityManager em = EMFService.createEntityManager();
		em.getTransaction().begin();
		em.remove(obj);
    	em.flush();
    	em.getTransaction().commit();
		em.close();
	}


	public static User getUserById(long userId) {
		EntityManager em = EMFService.createEntityManager();
		try {
			return UserDAO.byId(em, userId);
	    } finally {
			em.close();
	    }
	}


	public static boolean update(User user) {
		EntityManager em = EMFService.createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(user);
	    	em.flush();
	    	em.getTransaction().commit();
			return true;
		} catch (NoResultException e) {
	    	em.getTransaction().rollback();
	        return false;
	    } finally {
			em.close();
	    }
	}


	public static boolean updateUserPassword(String email, String pass, String newPass) {
		EntityManager em = EMFService.createEntityManager();
		try {
			em.getTransaction().begin();
			User user = UserDAO.byEmailAndPassword(em, email, pass);
			if (user != null) {
				user.setPassword(newPass);
				em.persist(user);
		    	em.flush();
			}
	    	em.getTransaction().commit();
			return user != null;
		} catch (Exception e) {
	    	em.getTransaction().rollback();
	        throw e;
	    } finally {
			em.close();
	    }
	}


	public static User getUserByEmail(String email) {
		EntityManager em = EMFService.createEntityManager();
		try {
			return UserDAO.byEmail(em, email);
	    } finally {
			em.close();
	    }
	}


	public static User getUserByEmailAndPassword(String email, String pass) {
		EntityManager em = EMFService.createEntityManager();
		try {
			return UserDAO.byEmailAndPassword(em, email, pass);
	    } finally {
			em.close();
	    }
	}


	public static Token getTokenByAccess(String reqAccess) {
		EntityManager em = EMFService.createEntityManager();
		try {
			return TokenDAO.byAccess(em, reqAccess);
		} catch (NoResultException e) {
	        return null;
	    } finally {
			em.close();
	    }
	}


	public static Token getTokenByUserId(long userId) {
		EntityManager em = EMFService.createEntityManager();
		try {
			return TokenDAO.byUserId(em, userId);
		} catch (NoResultException e) {
	        return null;
	    } finally {
			em.close();
	    }
	}


	public static boolean update(Token token) {
		EntityManager em = EMFService.createEntityManager();
		try {
			em.getTransaction().begin();
			Token obj = TokenDAO.byUserId(em, token.getUserId());
			obj.setAccess(token.getAccess());
			obj.setSecret(token.getSecret());
			em.persist(obj);
	    	em.flush();
	    	em.getTransaction().commit();
			return true;
		} catch (NoResultException e) {
	    	em.getTransaction().rollback();
	        return false;
	    } finally {
			em.close();
	    }
	}

	
	public static List<Gcm> getAllGcm() {
		EntityManager em = EMFService.createEntityManager();
		List<Gcm> list = GcmDAO.fetchAll(em);
		em.close();
		return list;
	}


	public static Gcm getGcmByRegId(String regId) {
		EntityManager em = EMFService.createEntityManager();
		try {
			return GcmDAO.byRegId(em, regId);
		} catch (NoResultException e) {
	        return null;
	    } finally {
			em.close();
	    }
	}

	
	public static List<Recipe> getAllRecipes() {
		EntityManager em = EMFService.createEntityManager();
		List<Recipe> list = RecipeDAO.fetchAll(em);
		em.close();
		return list;
	}


	public static Recipe getRecipeById(long recipeId) {
		EntityManager em = EMFService.createEntityManager();
		try {
			return RecipeDAO.byId(em, recipeId);
		} catch (NoResultException e) {
	        return null;
	    } finally {
			em.close();
	    }
	}


	public static List<Boost> getBoostsbyOwner(long userId) {
		EntityManager em = EMFService.createEntityManager();
		List<Boost> list = BoostDAO.byOwner(em, userId);
		em.close();
		return list;
	}
	
	public static List<Boost> getAllBoosts() {
		EntityManager em = EMFService.createEntityManager();
		List<Boost> list = BoostDAO.fetchAll(em);
		em.close();
		return list;
	}
	
	
	public static Boost getBoostById(long boostId) {
		EntityManager em = EMFService.createEntityManager();
		try {
			return BoostDAO.byId(em, boostId);
		} catch (NoResultException e) {
	        return null;
	    } finally {
			em.close();
	    }
	}
	
	public static List<User> getUserFriends(long userId) {
		EntityManager em = EMFService.createEntityManager();
		try {
			return UserDAO.getUserFriends(em, userId);
		} catch (NoResultException e) {
	        return Collections.emptyList();
	    } finally {
			em.close();
	    }
	}
	
}
