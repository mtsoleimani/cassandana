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

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import io.cassandana.Constants;
import io.cassandana.broker.security.AclEntity;
import io.cassandana.silo.SiloMessage;

public class CassandraHelper extends DatabaseHelper implements IDatabaseOperation {

	private Cluster cluster;
    private Session session;
    
    
	public CassandraHelper(String host, int port, String dbUsername, String dbPassword, String dbName,
			long dbReconnectSeconds) {

		super(host, port, dbUsername, dbPassword, dbName, dbReconnectSeconds);
	}

	public CassandraHelper(String host, int port, String dbUsername, String dbPassword, String dbName) {
		super(host, port, dbUsername, dbPassword, dbName);
	}

	public CassandraHelper(String host, int port, String dbName) {
		super(host, port, dbName);
	}

	public CassandraHelper() {
		super();
	}

	@Override
	public void onConnected() {
		System.out.println("connected to Cassandra server: " + host + ":" + port);
	}

	public void tryConnecting() {
		Builder b = Cluster.builder().addContactPoint(host);
		b.withPort(port);
        cluster = b.build();
        session = cluster.connect(dbName);//keyspace
        onConnected();
	}

	@Override
	public void shutdown() {
		if(session != null)
			session.close();
		
		if(cluster != null)
			cluster.close();
	}

	@Override
	public String getSecret(String username) {
		String query = "select password from users where username = '" + username + "' limit 1";
		ResultSet resultSet = session.execute(query);
		if(resultSet == null)
			return null;
		
		List<Row> list = resultSet.all();
		if(list == null || list.size() == 0)
			return null;
		
		return list.get(0).getString("password");
	}

	@Override
	public AclEntity getAcl(String topic, String username, String clientId) {
		String query = "select * from acl where topic = '" + topic + "' AND username = '" + username + "' limit 1";
		ResultSet resultSet = session.execute(query);
		if(resultSet == null)
			return null;
		
		List<Row> list = resultSet.all();
		if(list == null || list.size() == 0)
			return null;
		
		AclEntity acl = new AclEntity();
		acl.username = username;
		acl.clientId = clientId;
		acl.topic = topic;
		acl.canPublish = (list.get(0).getInt("write") == 1);
		acl.canSubscribe = (list.get(0).getInt("read") == 1);
		return acl;
	}

	@Override
	public List<AclEntity> getAcl(String topic) {
		return null;
	}

	@Override
	public void bulkInsert(List<SiloMessage> list) {
		if(list == null || list.size() == 0)
			return;
		
		String query = "";
		int count = list.size();
		for(int i = 0; i<count; i++) {
			SiloMessage message = list.get(i);
			query += "INSERT INTO silo (" 
				+ Constants.ID + "," 
				+ Constants.TOPIC + "," 
				+ Constants.USERNAME + "," 
				+ Constants.QOS + "," 
				+ Constants.MESSAGE + "," 
				+ Constants.CREATED + ") VALUES(uuid(), '" 
				+ message.topic + "','" 
				+ message.username + "',"
				+ message.qos + ",'"
				+ message.payload + "',"
				+ message.receivedAt + ");";
		}
		
		if(!query.isEmpty())
			session.execute(query);
		
	}

}
