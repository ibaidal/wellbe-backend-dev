package com.axa.server.base.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.axa.server.base.pods.Recipe;


class RecipeDAO {
	
	
	public static Recipe byId(EntityManager em, long recipeId) {
		Query q = em.createQuery("select t from Recipe t where t.recipeId = :recipeId");
		q.setParameter("recipeId", recipeId);
		return (Recipe) q.getSingleResult();
	}

	
	@SuppressWarnings("unchecked")
	public static List<Recipe> fetchAll(EntityManager em) {
		return em.createQuery("select t from Recipe t").getResultList();
	}
	
}
