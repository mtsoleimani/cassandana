/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package io.cassandana.database;

import java.util.List;

import io.cassandana.broker.security.AclEntity;
import io.cassandana.silo.SiloMessage;

public interface IDatabaseOperation {

	
	public void onConnected();

	public void tryConnecting();

	public  void shutdown();

	public boolean isConnected();
	
	public String getSecret(String username);
	
	public AclEntity getAcl(String topic, String username, String clientId);
	
	public List<AclEntity> getAcl(String topic);
	
	public void bulkInsert(List<SiloMessage> list);
}
