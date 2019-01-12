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

public class AclEntity {

	public String username;

	public String clientId;

	public String topic;

	public boolean canPublish = true;

	public boolean canSubscribe = true;

	public String toString() {
		return "username: " + username 
				+ " clientId:" + clientId 
				+ " topic:" + topic 
				+ " canPublish:" + canPublish
				+ " canSubscribe:" + canSubscribe;
	}

}
