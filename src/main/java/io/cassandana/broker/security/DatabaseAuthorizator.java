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
import io.cassandana.broker.subscriptions.Topic;
import io.cassandana.database.DatabaseWorkerPool;
import io.cassandana.database.IDatabaseOperation;

public class DatabaseAuthorizator implements IAuthorizatorPolicy {
	
	private IDatabaseOperation database;
	
	public DatabaseAuthorizator(Config conf) {
		database = DatabaseWorkerPool.getInstance(conf).getDatabaseWorker();
	}

    @Override
    public boolean canWrite(Topic topic, String user, String client) {
    	AclEntity acl = database.getAcl(topic.toString(), user, client);
    	if(acl != null && acl.canPublish)
    		return true;
    	return false;
    }

    @Override
    public boolean canRead(Topic topic, String user, String client) {
    	AclEntity acl = database.getAcl(topic.toString(), user, client);
    	if(acl != null && acl.canSubscribe)
    		return true;
    	return false;
    }
}
