package com.axa.server.base.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


public class LogFilter implements Filter {
	
    private static final Logger log = Logger.getLogger(LogFilter.class.getName());

    
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
    	HttpServletRequest httpReq = (HttpServletRequest) req;

    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("\n");
    	sb.append("\n=======================");
    	sb.append("\n" + httpReq.getMethod() + ":" + httpReq.getRequestURI());
		
		@SuppressWarnings("unchecked")
		Enumeration<String> names = req.getParameterNames();
		
		while (names.hasMoreElements()) {
			String name = names.nextElement();
		    String[] values = req.getParameterValues(name);
		    String value = Arrays.toString(values);
		    if (value != null && value.length() > 100) {
		    	value = value.substring(0, 100) + "...";
		    }
		    sb.append("\n" + name + " = " + value);
		}
		
		sb.append("\n=======================");
		sb.append("\n");
		
		log.warning(sb.toString());
		
        filterChain.doFilter(req, resp);
    }

	
	public void init(FilterConfig config) {
	}

	
	public void destroy() {
	}

}
