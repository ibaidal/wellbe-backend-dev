package com.axa.server.base.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ServletHelper {

	private HttpServletRequest req;
	HttpServletResponse resp;
	
	
	public ServletHelper(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}
	
	
	public String getString(String name) {
		String value = req.getParameter(name);
		return (value == null) ? "" : value;
	}
	
	
	public boolean getBoolean(String name) {
		try {
			return Boolean.parseBoolean(getString(name));
		} catch (Exception e) {
			return false;
		}
	}
	
	
	public int getInt(String name) {
		try {
			return Integer.parseInt(getString(name));
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	public float getFloat(String name) {
		try {
			return Float.parseFloat(getString(name));
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	public long getLong(String name) {
		try {
			return Long.parseLong(getString(name));
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	public double getDouble(String name) {
		try {
			return Double.parseDouble(getString(name));
		} catch (Exception e) {
			return 0;
		}
	}
	
	
}
