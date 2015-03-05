package com.axa.server.base.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.axa.server.base.pods.Gcm;


class GcmDAO {
	
	
	public static Gcm byRegId(EntityManager em, String regId) {
		Query q = em.createQuery("select t from Gcm t where t.regId = :regId");
		q.setParameter("regId", regId);
		return (Gcm) q.getSingleResult();
	}

	
	@SuppressWarnings("unchecked")
	public static List<Gcm> fetchAll(EntityManager em) {
		return em.createQuery("select t from Gcm t").getResultList();
	}
	
}
