package com.axa.server.base.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import com.axa.server.base.Constants;


@SuppressWarnings("serial")
public class CitiesServlet extends HttpServlet {
	

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String fileStr = FileUtils.readFileToString(new File("content/cities.json"));
		resp.setContentType(Constants.CONTENT_TYPE_JSON);
		resp.getWriter().append(fileStr);
		return;
	}

}
