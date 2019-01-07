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

import static com.mongodb.client.model.Filters.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;

import io.cassandana.Constants;
import io.cassandana.broker.security.AclEntity;
import io.cassandana.silo.SiloMessage;



public class MongodbHelper extends DatabaseHelper implements ServerMonitorListener, IDatabaseOperation {

	protected MongoClient mongoClient;
	protected MongoDatabase database;


	public MongodbHelper(String host, int port,
			String dbUsername, String dbPassword,
			String dbName,
			int dbPoolSize, long dbReconnectSeconds) {

		super(host, port, dbUsername, dbPassword, dbName, dbReconnectSeconds);
		this.dbPoolSize = dbPoolSize;
		this.dbReconnectSeconds = dbReconnectSeconds;
	}

	public MongodbHelper(String host, int port,
			String dbUsername, String dbPassword,
			String dbName) {
		super(host, port, dbUsername, dbPassword, dbName);
	}

	public MongodbHelper(String host, int port, String dbName) {
		super(host, port, dbName);
	}

	public MongodbHelper() {
		super();
	}


	protected int dbPoolSize = Runtime.getRuntime().availableProcessors();
	

	protected Runnable reconnectionRunnable = new Runnable() {

		@Override
		public void run() {
			tryConnecting();
		}
	};

	private MongoCollection<Document> userCollection;
	private MongoCollection<Document> aclCollection;
	private MongoCollection<Document> siloCollection;
	
	protected IDbmsConnectionStatus mIDbmsConnectionStatus;
	protected void setConnectionStatusListener(IDbmsConnectionStatus listener) {
		this.mIDbmsConnectionStatus = listener;
	}

	public synchronized void tryConnecting() {
		MongoClientOptions clientOptions = new MongoClientOptions.Builder()
			.addServerMonitorListener(this)
			.connectionsPerHost(dbPoolSize)
			.build();

		if(dbUsername != null && dbPassword != null) {
			MongoCredential credential = MongoCredential.createCredential(
					dbUsername, dbName,
					dbPassword.toCharArray());

			mongoClient = new MongoClient(new ServerAddress(host,
					port),
					Arrays.asList(credential),
					clientOptions);
		} else {
			mongoClient = new MongoClient(new ServerAddress(
					host,
					port),
					clientOptions);
		}

		database = mongoClient.getDatabase(dbName);
		setConnected(true);
		onConnected();
		System.out.println("connected to mongodb server: " + host + ":" + port);
	}

	
	
	public void onConnected() {
		userCollection = database.getCollection("users");
		aclCollection = database.getCollection("acl");
		siloCollection = database.getCollection("silo");
	}


	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}


	public void init() {
		Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
		tryConnecting();
	}


	public void shutdown() {
		if(mongoClient != null)
			mongoClient.close();

		if(executor != null)
			executor.shutdown();
	}



	@Override
	public void serverHearbeatStarted(ServerHeartbeatStartedEvent arg0) {

	}


	@Override
	public void serverHeartbeatFailed(ServerHeartbeatFailedEvent arg0) {
		setConnected(false);

		//try to reconnect
		executor.schedule(reconnectionRunnable, dbReconnectSeconds, TimeUnit.SECONDS);
	}


	@Override
	public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent arg0) {

	}

	@Override
	public String getSecret(String username) {
		FindIterable<Document> findIterable = userCollection.find(eq("username", username)).limit(1);
		MongoCursor<Document> cursor = findIterable.iterator();
		
		String secret = null;
		if(cursor.hasNext())
			secret = cursor.next().getString("password");
		
		cursor.close();
		return secret; 
	}

	@Override
	public AclEntity getAcl(String topic, String username, String clientId) {
		FindIterable<Document> findIterable = aclCollection.find(eq("username", username));
		MongoCursor<Document> cursor = findIterable.iterator();
		
		AclEntity acl = null;
		if(cursor.hasNext()) {
			Document document = cursor.next();
			acl = new AclEntity();
			acl.username = username;
			acl.clientId = clientId;
			acl.topic = topic;
			acl.canPublish = (document.getInteger("write") == 1);
			acl.canSubscribe = (document.getInteger("read") == 1);
		}
		
		cursor.close();
		return acl; 
	}

	@Override
	public List<AclEntity> getAcl(String topic) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public void bulkInsert(List<SiloMessage> list) {
		if(list == null || list.size() == 0)
			return;
		
		if(list.size() == 1) {
			Document document = new Document();
			document.put(Constants.TOPIC, list.get(0).topic);
			document.put(Constants.USERNAME, list.get(0).username);
			document.put(Constants.QOS, list.get(0).qos);
			document.put(Constants.MESSAGE, list.get(0).payload);
			document.put(Constants.CREATED, list.get(0).receivedAt);
			siloCollection.insertOne(document);
			return;
		}
		
		List<Document> documents = new ArrayList<>();
		for(SiloMessage entry: list) {
			Document document = new Document();
			document.put(Constants.TOPIC, entry.topic);
			document.put(Constants.USERNAME, entry.username);
			document.put(Constants.QOS, entry.qos);
			document.put(Constants.MESSAGE, entry.payload);
			document.put(Constants.CREATED, entry.receivedAt);
			documents.add(document);
		}
		siloCollection.insertMany(documents);
	}





}
