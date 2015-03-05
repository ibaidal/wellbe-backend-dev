package com.axa.server.base.persistence;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.axa.server.base.pods.Token;


class TokenDAO {
	
	
	static Token byAccess(EntityManager em, String access) {
		Query q = em.createQuery("select t from Token t where t.access = :access");
		q.setParameter("access", access);
		return (Token) q.getSingleResult();
	}

	
	public static Token byUserId(EntityManager em, long userId) {
		Query q = em.createQuery("select t from Token t where t.userId = :userId");
		q.setParameter("userId", userId);
		return (Token) q.getSingleResult();
	}
	
}
