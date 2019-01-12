package io.cassandana.broker.security.cache;

import java.util.concurrent.ConcurrentHashMap;

import io.cassandana.Constants;
import io.cassandana.broker.config.Config;

public class AuthorizationCache {

	private boolean isEnbaled = true;
	
	private static AuthorizationCache instance;

	public synchronized static AuthorizationCache getInstance(Config conf) {
		if(instance == null)
			instance = new AuthorizationCache(conf);
		return instance;
	}

	private ConcurrentHashMap<String, ConcurrentHashMap<String, CacheAclEntry>> aclCache;
	
	private int cacheTtlMillis;
	
	private AuthorizationCache(Config conf) {
		isEnbaled = conf.cacheEnabled;
		this.aclCache = new ConcurrentHashMap<>();
		cacheTtlMillis = conf.cacheTtl * Constants.SECOND_IN_MILLIS;
	}
	
	public void put(String username, String clientId, String topic, boolean canRead, boolean canWrite) {
		if(!isEnbaled)
			return;
		
		CacheAclEntry entry = getEntry(username, clientId, topic);
		entry.created = System.currentTimeMillis();
		entry.canRead = canRead;
		entry.canWrite = canWrite;
	}
	
	private CacheAclEntry getEntry(String username, String clientId, String topic) {
		ConcurrentHashMap<String, CacheAclEntry> topics = aclCache.putIfAbsent(username, new ConcurrentHashMap<String, CacheAclEntry>());
		if(topics == null)
			topics = aclCache.get(username);
		
		CacheAclEntry entry = topics.putIfAbsent(topic, new CacheAclEntry());
		if(entry == null)
			entry = topics.get(topic);
		
		return entry;
	}
	
	public void updateWritePermission(String username, String clientId, String topic, boolean canWrite) {
		if(!isEnbaled)
			return;
		
		CacheAclEntry entry = getEntry(username, clientId, topic);
		entry.created = System.currentTimeMillis();
		entry.canWrite = canWrite;
	}
	
	public void updateReadPermission(String username, String clientId, String topic, boolean canRead) {
		CacheAclEntry entry = getEntry(username, clientId, topic);
		entry.created = System.currentTimeMillis();
		entry.canRead = canRead;
	}
	
	public CacheAclEntry remove(String username, String clientId, String topic) {
		if(!isEnbaled)
			return null;
		
		ConcurrentHashMap<String, CacheAclEntry> topics = aclCache.get(username);
		if(topics == null)
			return null;
		
		return topics.remove(topic);
	}
	
	public CacheAclEntry get(String username, String clientId, String topic) {
		if(!isEnbaled)
			return null;
		
		ConcurrentHashMap<String, CacheAclEntry> topics = aclCache.get(username);
		if(topics == null)
			return null;
		
		CacheAclEntry entry = topics.get(topic);
		if(entry == null)
			return null;
		
		if(System.currentTimeMillis() - entry.created >= cacheTtlMillis) {
			topics.remove(topic);
			return null;
		}
		
		return entry;
		
	}
	
	public int size() {
		return aclCache.size();
	}
}
