package com.axa.server.base.servlet;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.axa.server.base.persistence.Persistence;
import com.axa.server.base.pods.Gcm;
import com.axa.server.base.util.ValidationUtil;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;


@SuppressWarnings("serial")
public class GcmServlet extends HttpServlet {
	
    private static final Logger log = Logger.getLogger(GcmServlet.class.getName());
	

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//resp.setHeader("Allow", "POST");
		//resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		
		// Test send push
		sendMessage(req, resp);
	}

	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		String regId = req.getParameter("regId");
		
		if (ValidationUtil.isEmpty(regId)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty fields");
		}

    	Gcm gcm = Persistence.getGcmByRegId(regId);
    	
    	if (gcm == null) {
    		gcm = new Gcm();
    		gcm.setRegId(regId);
	    	Persistence.insert(gcm);
	    	log.fine("New GCM registration: " + regId);
    	} else {
	    	log.fine("Already registered regId: " + regId);
    	}
	}

	
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		String regId = req.getParameter("regId");
		
		if (ValidationUtil.isEmpty(regId)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty fields");
		}

    	Gcm gcm = Persistence.getGcmByRegId(regId);
    	
    	if (gcm == null) {
	    	log.fine("regId not found: " + regId);
    		resp.sendError(HttpServletResponse.SC_NOT_FOUND, "regId not found");
    	} else {
    		Persistence.remove(gcm);
    	}
	}


	
	public void sendMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String message = req.getParameter("message");

		if (ValidationUtil.isEmpty(message)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty message");
		}

		List<Gcm> gcms = Persistence.getAllGcm();

		if (gcms.isEmpty()) {
            log.fine("No devices registered");
			
		} else {
	        Sender sender = new Sender("AIzaSyCq-Vq9MaEQyeZAGZpCT4ly2ooZdLDTM2o");
	        Message msg = new Message.Builder()
        		.addData("title", "Push notification")
        		.addData("message", message)
	        	.build();
	        
	        for (Gcm record : gcms) {
	            Result result = sender.send(msg, record.getRegId(), 5);
	            if (result.getMessageId() != null) {
	                log.info("Message sent to " + record.getRegId());
	                String canonicalRegId = result.getCanonicalRegistrationId();
	                if (canonicalRegId != null) {
	                    // if the regId changed, we have to update the datastore
	                    log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
	                    record.setRegId(canonicalRegId);
	            		Persistence.insert(record);
	                }
	            } else {
	                String error = result.getErrorCodeName();
	                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
	                    log.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
	            		Persistence.remove(record);
	                } else {
	                    log.warning("Error when sending message : " + error);
	                }
	            }
	        }
		}
	}
	
}
