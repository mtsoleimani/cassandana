/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package io.cassandana;

public class Constants {

	public static final String LOCAL_HOST = "127.0.0.1";
	
	public static final String ALL_HOSTS = "0.0.0.0";

	public static final int DEFAULT_MQTT_PORT = 1883;

	public static final int DEFAULT_SSL_MQTT_PORT = 1884;

	public static final int DEFAULT_WEBSOCKET_PORT = 8080;

	public static final int DEFAULT_HTTPS_PORT = 8443;

	public static final int DEFAULT_SECURE_WEBSOCKET_PORT = 8084;

	public static final int DEFAULT_HTTP_PORT = 8083;

	public static final int DEFAULT_MAX_BYTES_IN_MESSAGE = 8092;
	
	public static final int DEFAULT_REDIS_PORT = 6379;
	
	public static final int DEFAULT_MYSQL_PORT = 3306;
	
	public static final int DEFAULT_POSTGRES_PORT = 5432;
	
	public static final int DEFAULT_MONGODB_PORT = 27017;
	
	public static final int DEFAULT_CASSANDRA_PORT = 9042;
	
	public static final int DEFAULT_CACHE_TTL = 3600;
	
	public static final int SECOND_IN_MILLIS = 1000;
	
	public static final String DEFAULT_DATABASE_NAME = "cassandana";
	
	public static final String JDK = "JDK";

	public static final String JKS = "jks";

	public static final String DEFAULT_CERT_PATH = "cert/cassandana.jks";

	public static final String HOST = "host";

	public static final String PORT = "port";

	public static final String THREADS = "threads";

	public static final String WEBSOCKET = "websocket";

	public static final String ENABLED = "enabled";

	public static final String HTTP = "http";

	public static final String ALLOW_ANONYMOUS = "allow_anonymous";

	public static final String REAUTHORIZE_SUBSCRIPTIONS_ON_CONNECT = "reauthorize_subscriptions_on_connect";

	public static final String ALLOW_ZERO_BYTE_CLIENT_ID = "allow_zero_byte_client_id";

	public static final String USERNAME = "username";

	public static final String PASSWORD = "password";

	public static final String MYSQL = "mysql";

	public static final String POSTGRES = "postgres";

	public static final String MONGODB = "mongodb";
	
	public static final String CASSANDRA = "cassandra";
	
	public static final String NAME = "name";

	public static final String DATABASE = "database";

	public static final String ENGINE = "engine";

	public static final String COLLECTION_NAME = "cassandana";

	public static final String MAX_MESSAGE_BYTES = "max_message_bytes";

	
	public static final String EPOLL_ENABLED = "epoll_enabled";

	public static final String SSL = "ssl";
	public static final String WSS = "wss";
	public static final String HTTPS = "https";

	public static final int DEFAULT_SO_BACKLOG = 128;
	public static final int DEFAULT_TIMEOUT_SECONDS = 10;

	public static final String TCP = "tcp";
	public static final String SO_BACKLOG = "so_backlog";
	public static final String SO_REUSE_ADDRESS = "so_reuse_address";
	public static final String TCP_NODELAY = "tcp_nodelay";
	public static final String SO_KEEPALIVE = "so_keepalive";
	public static final String TIMEOUT_SECONDS = "timeout_seconds";

	public static final String CERT = "cert";
	public static final String PROVIDER = "provider";
	public static final String KEY_MANAGER_PASSWORD = "key_manager_password";
	public static final String KEY_STORE_TYPE = "key_store_type";
	public static final String PATH = "path";
	public static final String CLIENT_AUTH = "client_auth";
	public static final String KEY_STORE_PASSWORD = "key_store_password";

	public static final String PERMIT = "permit";
	public static final String DENY = "deny";
	public static final String SECURITY = "security";
	public static final String AUTHENTICATION = "authentication";
	public static final String ACL = "acl";
	
	
	public static final String AUTH_URL = "auth_url";
	public static final String ACL_URL = "acl_url";
	
	public static final String ID = "id";
	
	public static final String TOPIC = "topic";
	public static final String CLIENT_ID = "clientId";
	
	public static final String PUB = "pub";
	public static final String SUB = "sub";
	
	public static final String CREATED = "created";
	public static final String MESSAGE = "message";
	public static final String QOS = "qos";
	public static final String SILO = "silo";
	
	public static final String INTERVAL = "interval";
	public static final String COUNT = "count";
	
	
	public static final int DEFAULT_SILO_COUNT = 100;
	public static final int DEFAULT_SILO_INTERVAL = 5;
	
	public static final String REDIS = "redis";
	
	public static final String EXPIRATION = "expiration";
	public static final String CACHE = "cache";
	
	

}
