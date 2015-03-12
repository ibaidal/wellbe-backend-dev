package com.axa.server.base.persistence;


import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.axa.server.base.pods.User;


public class UserDAO {
	
	
	public static User byId(EntityManager em, long userId) {
		try {
			Query q = em.createQuery("select u from User u where u.userId = :userId");
			q.setParameter("userId", userId);
			return (User) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	
	public static User byEmail(EntityManager em, String email) {
		try {
			Query q = em.createQuery("select u from User u where u.email = :email");
			q.setParameter("email", email);
			return (User) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	
	static User byEmailAndPassword(EntityManager em, String email, String pass) {
		try {
			Query q = em.createQuery("select u from User u where u.email = :email and u.password = :pass");
			q.setParameter("email", email);
			q.setParameter("pass", pass);
			return (User) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	

}
