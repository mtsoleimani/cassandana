/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */


package io.cassandana.silo;


public class SiloMessage {

	public SiloMessage() {

	}

	public SiloMessage(String topic, String username, String payload, int qos) {
		this.topic = topic;
		this.username = username;
		this.payload = payload;
		this.qos = qos;
	}

	
	public String topic;
	public String username;
	public String payload;
	public int qos;
	public long receivedAt = System.currentTimeMillis();

}
