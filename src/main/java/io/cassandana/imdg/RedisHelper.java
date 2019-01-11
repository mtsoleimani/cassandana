/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */


package io.cassandana.imdg;

import java.util.HashMap;
import java.util.List;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisKeyAsyncCommands;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.api.sync.RedisKeyCommands;
import io.lettuce.core.api.sync.RedisStringCommands;

public class RedisHelper {

	private static RedisHelper instance;

	public synchronized static RedisHelper getInstance(String host, int port, String password) {
		if (instance == null)
			instance = new RedisHelper(host, port, password);
		return instance;
	}

	private RedisClient redisClient;
	private StatefulRedisConnection<String, String> connection;
	private RedisStringAsyncCommands<String, String> async;
	private RedisStringCommands<String, String> sync;
	
	
	private RedisKeyAsyncCommands<String, String> asyncKey;
	private RedisKeyCommands<String, String> syncKey;
	
	private RedisHelper(String host, int port, String password) {

		String connectionString = null;
		if(password == null)
			connectionString = "redis://" + host + ":" + port;
		else
			connectionString = "redis://" + password + "@" + host + ":" + port;
		
		redisClient = RedisClient.create(RedisURI.create(connectionString));
		connection = redisClient.connect();
		sync = connection.sync();
		async = connection.async();
		
		asyncKey = connection.async();
		syncKey = connection.sync();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				close();
			}
		});
	}

	public void setAsync(String key, String value) {
		async.set(key, value);
	}

	public void setAsync(String key, String value, long seconds) {
		async.setex(key, seconds, value);
	}

	public void setSync(String key, String value) {
		sync.set(key, value);
	}

	public void setSync(String key, String value, long seconds) {
		sync.setex(key, seconds, value);
	}

	public String getSync(String key) {
		return (String) sync.get(key);
	}

	public void close() {
		if (connection != null)
			connection.close();

		if (redisClient != null)
			redisClient.shutdown();

	}

	public List<KeyValue<String, String>> getMultipleKeys(String[] keys) {
		return sync.mget(keys);
	}

	public void setMultipleKeysValuesAsync(HashMap<String, String> keysValues) {
		async.mset(keysValues);
	}

	public void setMultipleKeysValues(HashMap<String, String> keysValues) {
		sync.mset(keysValues);
	}
	
	public void deleteAsync(String key) {
		asyncKey.del(key);
	}
	
	public void deleteSync(String key) {
		syncKey.del(key);
	}

}
