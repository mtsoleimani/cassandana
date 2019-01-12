package io.cassandana.broker.security.cache;

import java.util.concurrent.ConcurrentHashMap;

import io.cassandana.Constants;
import io.cassandana.broker.config.Config;

public class AuthenticationCache {
	
	
	private static AuthenticationCache instance;
	
	private boolean isEnbaled = true;

	public synchronized static AuthenticationCache getInstance(Config conf) {
		if(instance == null)
			instance = new AuthenticationCache(conf);
		return instance;
	}

	private ConcurrentHashMap<String, CacheEntry<String>> authCache;
	
	private int cacheTtlMillis;
	
	private AuthenticationCache(Config conf) {
		isEnbaled = conf.cacheEnabled;
		this.authCache = new ConcurrentHashMap<>();
		cacheTtlMillis = conf.cacheTtl * Constants.SECOND_IN_MILLIS;
	}
	
	public void put(String username, String secret) {
		if(!isEnbaled)
			return;
		
		CacheEntry<String> entry = authCache.putIfAbsent(username, new CacheEntry<String>(secret));
		if(entry == null)
			entry = authCache.get(username);
		entry.created = System.currentTimeMillis();
	}
	
	public String remove(String username) {
		if(!isEnbaled)
			return null;
		
		CacheEntry<String> res = authCache.remove(username);
		if(res == null)
			return null;
		return (String) res.value;
	}
	
	public String get(String username) {
		if(!isEnbaled)
			return null;
		
		CacheEntry<String> entry = authCache.get(username);
		if(entry == null)
			return null;
		
		if(System.currentTimeMillis() - entry.created >= cacheTtlMillis) {
			authCache.remove(username);
			return null;
		}
		
		return entry.value;
	}
	
	public int size() {
		return authCache.size();
	}
}
