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

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cassandana.Constants;
import io.cassandana.broker.config.Config;
import io.cassandana.broker.security.cache.AuthorizationCache;
import io.cassandana.broker.security.cache.CacheAclEntry;
import io.cassandana.broker.subscriptions.Topic;

public class HttpAuthorizator implements IAuthorizatorPolicy {
	
private static final Logger LOG = LoggerFactory.getLogger(HttpAuthorizator.class);
	

	private AuthorizationCache cache;

	private Config conf;
	public HttpAuthorizator(Config conf) {
		this.conf = conf;
		cache = AuthorizationCache.getInstance(conf);
	}

    @Override
    public boolean canWrite(Topic topic, String user, String client) {
    	CacheAclEntry entry = cache.get(user, client, topic.toString());
    	if(entry != null && entry.canWrite != null)
    		return entry.canWrite;
    	
    	return isValid(topic, user, client, Constants.PUB);
    }

    @Override
    public boolean canRead(Topic topic, String user, String client) {
    	CacheAclEntry entry = cache.get(user, client, topic.toString());
    	if(entry != null && entry.canRead != null)
    		return entry.canRead;
    	
    	return isValid(topic, user, client, Constants.SUB);
    }
    
    private boolean isValid(Topic topic, String username, String clientId, String acl) {
    	HttpURLConnection connection = null;
    	boolean isValid = false;
    	
    	try {
    		JSONObject json = new JSONObject();
    		json.put(Constants.USERNAME, username);
    		json.put(Constants.TOPIC, topic.toString());
    		json.put(Constants.CLIENT_ID, clientId);
    		json.put(Constants.ACL, acl);
    		String payload = json.toString();
    		
    		connection = (HttpURLConnection) new URL(conf.authorizationHttpUrl).openConnection();
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
    	    if(statusCode / 200 == 1) { // if response code is 2xx
    	    	isValid = true;
    	    	if(acl.equalsIgnoreCase(Constants.PUB))
    	    		cache.updateWritePermission(username, clientId, topic.toString(), true);
    	    	else if(acl.equalsIgnoreCase(Constants.SUB))
    	    		cache.updateReadPermission(username, clientId, topic.toString(), true);
    	    	
    	    }
    	    
    	} catch (Exception e) {
    		e.printStackTrace();
    		LOG.error("HTTP Authorization error: ", e.getMessage());
    		
		} finally {
			if(connection != null)
				connection.disconnect();
		}
    	
        return isValid;
    }
}
