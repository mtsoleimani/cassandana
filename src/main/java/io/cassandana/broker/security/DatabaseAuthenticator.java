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

import java.security.MessageDigest;
import java.util.Formatter;

import io.cassandana.broker.config.Config;
import io.cassandana.database.DatabaseWorkerPool;
import io.cassandana.database.IDatabaseOperation;

public class DatabaseAuthenticator implements IAuthenticator {
	
	private IDatabaseOperation database;
	
	public DatabaseAuthenticator(Config conf) {
		database = DatabaseWorkerPool.getInstance(conf).getDatabaseWorker();
	}

    @Override
    public boolean checkValid(String clientId, String username, byte[] password) {
    	String secret = database.getSecret(username);
    	if(secret != null && secret.equals(getSha256(password)))
    		return true;
        return false;
    }
    
    
    private static String getSha256(byte[] password) {
	    String digest = "";
	    try {
	        MessageDigest crypt = MessageDigest.getInstance("SHA-256");
	        crypt.reset();
	        crypt.update(password);
	        digest = byteToHex(crypt.digest());
	        
	    } catch(Exception e) {
	    	e.printStackTrace();
	        return null;
	    }
	    return digest;
	}
    
    private static String byteToHex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash) {
	        formatter.format("%02x", b);
	    }
	    
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
}
