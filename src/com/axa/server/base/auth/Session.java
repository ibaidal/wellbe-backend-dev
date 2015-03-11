package com.axa.server.base.auth;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;

import com.axa.server.base.persistence.Persistence;
import com.axa.server.base.pods.Token;
import com.axa.server.base.util.StringUtil;


public class Session {
	
    private static final Logger log = Logger.getLogger(Session.class.getName());
    
    
    public static void addNewTokenForUserId(long userId, HttpServletResponse resp) {
    	log.warning("addNewTokenForUserId: " + userId);
    	String access = StringUtil.randomString(20);
    	String secret = StringUtil.randomString(40);
    	
    	Token token = Persistence.getTokenByUserId(userId);
    	
    	if (token == null) {
    		token = new Token();
        	token.setUserId(userId);
	    	token.setAccess(access);
	    	token.setSecret(secret);
	    	Persistence.insert(token);
    	} else {
	    	token.setAccess(access);
	    	token.setSecret(secret);
	    	Persistence.update(token);
    	}
    	
		resp.addHeader("ags-auth-token", token.toString());
		log.warning("addHeader: " + token.toString());
    }
    
    
    public static boolean checkSignature(HttpServletRequest req) {
    	log.warning("Authorization: " + req.getHeader("Authorization"));
    	log.warning("Date: " + req.getHeader("Date"));
    	
    	try {
	    	String[] reqAuth = req.getHeader("Authorization").replaceAll("AGS ", "").split(":");
	    	String reqDate = req.getHeader("Date");
	    	String reqAccess = reqAuth[0];
	    	String reqSignature = reqAuth[1];
	    	
	    	Token token = Persistence.getTokenByAccess(reqAccess);

	    	if (token == null) {
	        	return false;
			}
	    	
	    	String calcSignature = Base64.encodeBase64String(HmacUtils.hmacSha1(token.getSecret(), reqDate));
	    	
	    	if (calcSignature.equals(reqSignature)) {
				return true;
			}	    	
    	} catch (Exception e) {
    		e.printStackTrace();
            log.warning(e.getMessage());
    	}
    	return false;
    }
    
    public static Token getToken(HttpServletRequest req) {
    	try {    		    		
	    	String[] reqAuth = req.getHeader("Authorization").replaceAll("AGS ", "").split(":");
	    	String reqAccess = reqAuth[0];
	    	
	    	return Persistence.getTokenByAccess(reqAccess);
    	
    	} catch (Exception e) {
    		e.printStackTrace();
            log.warning(e.getMessage());
    	}
    	
    	return null;
    }
    
}
