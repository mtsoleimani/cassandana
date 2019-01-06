/*
 * Copyright (c) 2012-2018 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
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
