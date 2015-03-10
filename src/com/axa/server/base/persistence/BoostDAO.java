package com.axa.server.base.persistence;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.axa.server.base.pods.Boost;
import com.axa.server.base.pods.Recipe;


public class BoostDAO {
	
	
	public static Boost byId(EntityManager em, long boostId) {
		try {
			Query q = em.createQuery("select u from Boost u where u.boostId = :boostId");
			q.setParameter("boostId", boostId);
			return (Boost) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<Boost> byOwner(EntityManager em, long userId) {
		Query q = em.createQuery("select t from Boost t where t.ownerId = :userId");
		q.setParameter("userId", userId);
		return (List<Boost>) q.getResultList();
	}

	
	@SuppressWarnings("unchecked")
	public static List<Boost> fetchAll(EntityManager em) {
		return em.createQuery("select t from Boost t").getResultList();
	}

}