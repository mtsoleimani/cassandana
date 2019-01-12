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

import io.cassandana.broker.config.Config;
import io.cassandana.broker.security.cache.AuthorizationCache;
import io.cassandana.broker.security.cache.CacheAclEntry;
import io.cassandana.broker.subscriptions.Topic;
import io.cassandana.database.DatabaseWorkerPool;
import io.cassandana.database.IDatabaseOperation;

public class DatabaseAuthorizator implements IAuthorizatorPolicy {
	
	private IDatabaseOperation database;
	
	private AuthorizationCache cache;
	
	public DatabaseAuthorizator(Config conf) {
		database = DatabaseWorkerPool.getInstance(conf).getDatabaseWorker();
		cache = AuthorizationCache.getInstance(conf);
	}

    @Override
    public boolean canWrite(Topic topic, String user, String client) {
    	CacheAclEntry entry = cache.get(user, client, topic.toString());
    	if(entry != null && entry.canWrite != null)
    		return entry.canWrite;
    	
    	AclEntity acl = database.getAcl(topic.toString(), user, client);
    	if(acl != null) {
    		cache.put(user, client, topic.toString(), acl.canSubscribe, acl.canPublish);
    		return acl.canPublish;
    	}
    	
    	return false;
    }

    @Override
    public boolean canRead(Topic topic, String user, String client) {
    	CacheAclEntry entry = cache.get(user, client, topic.toString());
    	if(entry != null && entry.canRead != null)
    		return entry.canRead;
    	
    	AclEntity acl = database.getAcl(topic.toString(), user, client);
    	if(acl != null) {
    		cache.put(user, client, topic.toString(), acl.canSubscribe, acl.canPublish);
    		return acl.canSubscribe;
    	}
    	
    	return false;
    }
}
