/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */


package io.cassandana.broker.security;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cassandana.Constants;
import io.cassandana.broker.config.Config;

public class HttpAuthenticator implements IAuthenticator {
	private static final Logger LOG = LoggerFactory.getLogger(HttpAuthenticator.class);
	
	private Config conf;
	public HttpAuthenticator(Config conf) {
		this.conf = conf;
	}

    @Override
    public boolean checkValid(String clientId, String username, byte[] password) {
    	
    	HttpURLConnection connection = null;
    	boolean isValid = false;
    	
    	try {
    		JSONObject json = new JSONObject();
    		json.put(Constants.USERNAME, username);
    		json.put(Constants.PASSWORD, new String(password, StandardCharsets.UTF_8));
    		String payload = json.toString();
    		
    		connection = (HttpURLConnection) new URL(conf.authenticationHttpUrl).openConnection();
    	    connection.setRequestMethod("POST");
    	    connection.setRequestProperty("Content-Type", "application/json");
    	    connection.setRequestProperty("Content-Length", Integer.toString(payload.getBytes().length));
    	    connection.setUseCaches(false);
    	    connection.setDoOutput(true);
    	    
    	    DataOutputStream writer = new DataOutputStream (
    	            connection.getOutputStream());
    	    writer.writeBytes(payload);
    	    writer.close();
    	    
    	    int statusCode = connection.getResponseCode();
    	    if(statusCode / 200 == 1) // if response code is 2xx
    	    	isValid = true;
    	    
    	} catch (Exception e) {
    		e.printStackTrace();
    		LOG.error("HTTP Authentication error: ", e.getMessage());
    		
		} finally {
			if(connection != null)
				connection.disconnect();
		}
    	
        return isValid;
    }
    
    
}
