package com.axa.server.base.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.axa.server.base.Constants;
import com.axa.server.base.auth.Session;
import com.axa.server.base.persistence.Persistence;
import com.axa.server.base.pods.Recipe;
import com.axa.server.base.util.ValidationUtil;
import com.google.appengine.api.datastore.Blob;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


@SuppressWarnings("serial")
public class RecipesServlet extends HttpServlet {
	
    private static final Logger log = Logger.getLogger(RecipesServlet.class.getName());

	private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create(); 
	

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		long recipeId;
		String action;
		
		try {
			String[] pathItems = req.getRequestURI().split("/");
			recipeId = pathItems.length > 2 ? Long.parseLong(pathItems[2]) : -1;
			action = pathItems.length > 3 ? pathItems[3] : null;
		} catch (Exception ignored) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (recipeId == -1) {
			
			List<Recipe> fromDB = Persistence.getAllRecipes();
			for (Recipe recipe : fromDB) {
				setPictureURL(recipe, req);
			}
			
			List<Recipe> allRecipes = getRecipesFomFile();
			allRecipes.addAll(fromDB);

			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(allRecipes));
			return;
		}
		
		Recipe recipe = Persistence.getRecipeById(recipeId);
		
		if (recipe == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		if ("picture".equals(action)) {
			resp.setContentType("image/*");
			if (recipe.getPictureBlob() == null) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else {
				resp.getOutputStream().write(recipe.getPictureBlob().getBytes());
			}
		} else {
			setPictureURL(recipe, req);
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(recipe));
		}
	}


	private List<Recipe> getRecipesFomFile() throws FileNotFoundException {
		Type listType = new TypeToken<ArrayList<Recipe>>() {}.getType();
		Reader reader = new FileReader(new File("content/recipes.json"));
		return new Gson().fromJson(reader, listType);
	}


	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!Session.checkSignature(req)) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		Recipe recipe = new Recipe();

		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(req);
			
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				String name = item.getFieldName();
				InputStream is = item.openStream();
				
				if (item.isFormField()) {
					String value = IOUtils.toString(is, "UTF-8");
					if ("title".equals(name)) {
						recipe.setTitle(value);
					} else if ("comment".equals(name)) {
						recipe.setComment(value);
					}
				} else if ("picture".equals(name)) {
					recipe.setPictureBlob(new Blob(IOUtils.toByteArray(is)));
				}
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
		
		if (ValidationUtil.anyEmpty(recipe.getTitle(), recipe.getComment())) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty fields");
		} else {
			Persistence.insert(recipe);
			setPictureURL(recipe, req);
			resp.setContentType(Constants.CONTENT_TYPE_JSON);
			resp.getWriter().append(GSON.toJson(recipe));
			log.finer(recipe.getPicture());
		}
	}
	
	
	private void setPictureURL(Recipe recipe, HttpServletRequest req) {
		if (recipe.getPictureBlob() == null) {
			recipe.setPicture(null);
		} else {
			String idCompPath = "/" + recipe.getRecipeId();
			String url = req.getRequestURL().toString();
			if (url.contains(String.valueOf(idCompPath))) {
				url = url.substring(0, url.indexOf(idCompPath));
			}
			recipe.setPicture(url + idCompPath + "/picture");
		}
	}

}
