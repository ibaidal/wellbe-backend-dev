package com.axa.server.base.persistence;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.axa.server.base.pods.User;
import com.axa.server.base.pods.UserBoosts;


public class UserBoostsDAO {
	
	
	/*@SuppressWarnings("unchecked")
	public static List<UserBoosts> byUserId(EntityManager em, long userId) {
		try {
			Query q = em.createQuery("select u from User u where u.userId = :userId and ");
			q.setParameter("userId", userId);
			return (List<UserBoosts>) q.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<UserBoosts> byBoostId(EntityManager em, long boostId) {
		try {
			Query q = em.createQuery("select u from User u where u.userId = :userId");
			q.setParameter("userId", userId);
			return (List<UserBoosts>) q.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}*/
	

}
