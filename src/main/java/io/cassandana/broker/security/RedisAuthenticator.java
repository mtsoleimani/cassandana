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

import io.cassandana.broker.Utils;
import io.cassandana.broker.config.Config;
import io.cassandana.broker.security.cache.AuthenticationCache;
import io.cassandana.imdg.RedisHelper;

public class RedisAuthenticator implements IAuthenticator {
	
	private RedisHelper redis;
	
	private AuthenticationCache cache;
	
	public RedisAuthenticator(Config conf) {
		cache = AuthenticationCache.getInstance(conf);
		redis = RedisHelper.getInstance(conf.redisHost, conf.redisPort, conf.redisPassword);
	}

    @Override
    public boolean checkValid(String clientId, String username, byte[] password) {
    	String secret = cache.get(username);
    	if(secret == null) {
    		secret = redis.getSync(username);
    		if(secret != null)
    			cache.put(username, secret);
    	}
    	
    	if(secret != null && secret.equals(Utils.getSha256(password)))
    		return true;
        return false;
    }
    
}
