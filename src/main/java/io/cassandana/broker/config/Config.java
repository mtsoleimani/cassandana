/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package io.cassandana.broker.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import io.cassandana.Constants;
import io.cassandana.broker.security.SecurityProvider;
import io.cassandana.database.DatabaseEngine;

public class Config {

	private final String confFilePath = "./cassandana.yaml";

	private static Config instance;

	public static Config getInstance() throws Exception {
		if (instance == null)
			instance = new Config();
		return instance;
	}

	private Config() throws Exception {
		if (!parse())
			throw new Exception("bad conf file");
	}

	@SuppressWarnings("unchecked")
	public boolean parse() {

		Yaml yaml = new Yaml();

		try (InputStream input = new FileInputStream(new File(confFilePath))) {

			Map<String, Object> parser = (Map<String, Object>) yaml.load(input);

			threads = (int) parser.get(Constants.THREADS);
			if (threads <= 0)
				threads = Runtime.getRuntime().availableProcessors();

			if (parser.get(Constants.HOST) == null)
				mqttServerHost = Constants.ALL_HOSTS;
			else
				mqttServerHost = parser.get(Constants.HOST).toString();

			if (parser.get(Constants.PORT) == null)
				mqttServerPort = Constants.DEFAULT_MQTT_PORT;
			else
				mqttServerPort = (int) parser.get(Constants.PORT);

			if (mqttServerPort <= 0 || mqttServerPort >= 65535)
				return false;

			if (parser.get(Constants.MAX_MESSAGE_BYTES) == null)
				maxMessageBytes = (int) parser.get(Constants.MAX_MESSAGE_BYTES);
			else
				maxMessageBytes = Constants.DEFAULT_MAX_BYTES_IN_MESSAGE;

			if (parser.get(Constants.ALLOW_ANONYMOUS) != null)
				allowAnonymous = (boolean) parser.get(Constants.ALLOW_ANONYMOUS);

			if (parser.get(Constants.ALLOW_ZERO_BYTE_CLIENT_ID) != null)
				allowZeroByteClientId = (boolean) parser.get(Constants.ALLOW_ZERO_BYTE_CLIENT_ID);

			if (parser.get(Constants.REAUTHORIZE_SUBSCRIPTIONS_ON_CONNECT) != null)
				reauthorizeSubscriptionsOnConnect = (boolean) parser
						.get(Constants.REAUTHORIZE_SUBSCRIPTIONS_ON_CONNECT);

			if (parser.get(Constants.EPOLL_ENABLED) != null)
				epollEnabled = (boolean) parser.get(Constants.EPOLL_ENABLED);

			Map<String, Object> websocket = (Map<String, Object>) parser.get(Constants.WEBSOCKET);
			if (websocket != null) {

				if (websocket.get(Constants.HOST) == null)
					websocketHost = Constants.ALL_HOSTS;
				else
					websocketHost = websocket.get(Constants.HOST).toString();

				websocketPort = (int) websocket.get(Constants.PORT);
				if (websocketPort <= 0 || websocketPort >= 65535)
					return false;

				if (websocket.get(Constants.ENABLED) != null)
					websocketEnabled = (boolean) websocket.get(Constants.ENABLED);
				else
					websocketEnabled = false;

			} else {
				websocketEnabled = false;
			}

			Map<String, Object> http = (Map<String, Object>) parser.get(Constants.HTTP);
			if (http != null) {

				if (http.get(Constants.HOST) == null)
					httpHost = Constants.ALL_HOSTS;
				else
					httpHost = http.get(Constants.HOST).toString();

				httpPort = (int) http.get(Constants.PORT);
				if (httpPort <= 0 || httpPort >= 65535)
					return false;

				if (http.get(Constants.ENABLED) != null)
					httpEnabled = (boolean) http.get(Constants.ENABLED);
				else
					httpEnabled = false;

			} else {
				httpEnabled = false;
			}

			Map<String, Object> database = (Map<String, Object>) parser.get(Constants.DATABASE);
			if (database != null) {
				dbHost = database.get(Constants.HOST).toString();
				if (database.get(Constants.USERNAME) != null)
					dbUsername = database.get(Constants.USERNAME).toString();

				if (database.get(Constants.PASSWORD) != null)
					dbPassword = database.get(Constants.PASSWORD).toString();

				dbName = database.get(Constants.NAME).toString();

				dbPort = (int) database.get(Constants.PORT);
				if (dbPort <= 0 || dbPort >= 65535)
					return false;

				String engine = database.get(Constants.ENGINE).toString();
				if (engine.equalsIgnoreCase(Constants.MYSQL))
					dbEngine = DatabaseEngine.MYSQL;
				else if (engine.equalsIgnoreCase(Constants.POSTGRES))
					dbEngine = DatabaseEngine.POSTGRES;
				else if (engine.equalsIgnoreCase(Constants.MONGODB))
					dbEngine = DatabaseEngine.MONGODB;
				else if (engine.equalsIgnoreCase(Constants.CASSANDRA))
					dbEngine = DatabaseEngine.CASSANDRA;
				else
					return false;

			} else {
				return false;
			}

			Map<String, Object> tcp = (Map<String, Object>) parser.get(Constants.TCP);
			if (tcp != null) {
				if (tcp.get(Constants.SO_REUSE_ADDRESS) != null)
					socketReuseAddress = (boolean) tcp.get(Constants.SO_REUSE_ADDRESS);
				else
					socketReuseAddress = true;

				if (tcp.get(Constants.TCP_NODELAY) != null)
					tcpNoDelay = (boolean) tcp.get(Constants.TCP_NODELAY);
				else
					tcpNoDelay = true;

				if (tcp.get(Constants.SO_KEEPALIVE) != null)
					socketKeepAlive = (boolean) tcp.get(Constants.SO_KEEPALIVE);
				else
					socketKeepAlive = true;

				if (tcp.get(Constants.SO_BACKLOG) != null)
					socketBacklog = (int) tcp.get(Constants.SO_BACKLOG);
				else
					socketBacklog = Constants.DEFAULT_SO_BACKLOG;

				if (tcp.get(Constants.TIMEOUT_SECONDS) != null)
					socketTimeoutSeconds = (int) tcp.get(Constants.TIMEOUT_SECONDS);
				else
					socketTimeoutSeconds = Constants.DEFAULT_TIMEOUT_SECONDS;

			} else {

				socketReuseAddress = true;
				tcpNoDelay = true;
				socketKeepAlive = true;
				socketBacklog = Constants.DEFAULT_SO_BACKLOG;
				socketTimeoutSeconds = Constants.DEFAULT_TIMEOUT_SECONDS;
			}

			Map<String, Object> https = (Map<String, Object>) parser.get(Constants.HTTPS);
			if (https != null) {

				if (https.get(Constants.HOST) == null)
					httpsHost = Constants.ALL_HOSTS;
				else
					httpsHost = https.get(Constants.HOST).toString();

				httpsPort = (int) https.get(Constants.PORT);
				if (httpsPort <= 0 || httpsPort >= 65535)
					return false;

				if (https.get(Constants.ENABLED) != null)
					httpsEnabled = (boolean) https.get(Constants.ENABLED);
				else
					httpsEnabled = false;

			} else {
				httpsEnabled = false;
			}

			Map<String, Object> wss = (Map<String, Object>) parser.get(Constants.WSS);
			if (wss != null) {

				if (wss.get(Constants.HOST) == null)
					wssHost = Constants.ALL_HOSTS;
				else
					wssHost = wss.get(Constants.HOST).toString();

				wssPort = (int) wss.get(Constants.PORT);
				if (wssPort <= 0 || wssPort >= 65535)
					return false;

				if (wss.get(Constants.ENABLED) != null)
					wssEnabled = (boolean) wss.get(Constants.ENABLED);
				else
					wssEnabled = false;

			} else {
				wssEnabled = false;
			}

			Map<String, Object> ssl = (Map<String, Object>) parser.get(Constants.SSL);
			if(ssl != null ) {
				
				if(ssl.get(Constants.HOST) == null)
					sslHost = Constants.ALL_HOSTS;
				else
					sslHost = ssl.get(Constants.HOST).toString();
				
				sslPort = (int) ssl.get(Constants.PORT);
				if(sslPort <= 0 || sslPort >= 65535)
					return false;
				
				if(ssl.get(Constants.ENABLED) != null)
					sslEnabled = (boolean) ssl.get(Constants.ENABLED);
				else
					sslEnabled = false;
				
			} else {
				sslEnabled = false;
			}

			Map<String, Object> cert = (Map<String, Object>) parser.get(Constants.CERT);
			if (cert != null) {

				if (cert.get(Constants.PROVIDER) == null)
					certProvider = Constants.JDK;
				else
					certProvider = cert.get(Constants.PROVIDER).toString();

				if (cert.get(Constants.KEY_MANAGER_PASSWORD) != null)
					certKeyManagerPassword = cert.get(Constants.KEY_MANAGER_PASSWORD).toString();

				if (cert.get(Constants.KEY_STORE_PASSWORD) != null)
					certKeyStorePassword = cert.get(Constants.KEY_MANAGER_PASSWORD).toString();

				if (cert.get(Constants.KEY_STORE_TYPE) == null)
					certKeyStoreType = Constants.JKS;
				else
					certKeyStoreType = cert.get(Constants.KEY_STORE_TYPE).toString();

				if (cert.get(Constants.PATH) == null)
					certPath = Constants.DEFAULT_CERT_PATH;
				else
					certPath = cert.get(Constants.PATH).toString();

				if (cert.get(Constants.CLIENT_AUTH) != null)
					certClientAuth = (boolean) cert.get(Constants.CLIENT_AUTH);
				else
					certClientAuth = false;

			} else {
				certProvider = Constants.JDK;
				certKeyManagerPassword = null;
				certKeyStoreType = Constants.JKS;
				certPath = Constants.DEFAULT_CERT_PATH;
				certClientAuth = false;
				certKeyStorePassword = null;
			}

			Map<String, Object> security = (Map<String, Object>) parser.get(Constants.SECURITY);
			if (security != null) {
				
				Map<String, Object> cache = (Map<String, Object>) security.get(Constants.CACHE);
				if (cache != null) {
					if (cache.get(Constants.ENABLED) != null)
						cacheEnabled = (boolean) cache.get(Constants.ENABLED);
					
					if (cache.get(Constants.EXPIRATION) != null)
						cacheExpirationInSeconds = (int) cache.get(Constants.EXPIRATION);
					
					if(cacheExpirationInSeconds == 0)
						cacheEnabled = false;
					
				} else {
					cacheEnabled = true;
					cacheExpirationInSeconds = Constants.DEFAULT_CACHE_TTL;
				}
					
				

				if (security.get(Constants.AUTHENTICATION) != null) {
					String tmp = security.get(Constants.AUTHENTICATION).toString();
					if (tmp.equalsIgnoreCase(SecurityProvider.DENY.name()))
						authProvider = SecurityProvider.DENY;
					else if (tmp.equalsIgnoreCase(SecurityProvider.DATABASE.name()))
						authProvider = SecurityProvider.DATABASE;
					else if (tmp.equalsIgnoreCase(SecurityProvider.HTTP.name()))
						authProvider = SecurityProvider.HTTP;
					else if (tmp.equalsIgnoreCase(SecurityProvider.REDIS.name()))
						authProvider = SecurityProvider.REDIS;
					else // if(tmp.equalsIgnoreCase(SecurityProvider.PERMIT.name()))
						authProvider = SecurityProvider.PERMIT;

					if (authProvider == SecurityProvider.HTTP) {
						if (security.get(Constants.AUTH_URL) != null)
							authenticationHttpUrl = security.get(Constants.AUTH_URL).toString().trim();
						else
							return false;
					}

				} else {
					authProvider = SecurityProvider.PERMIT;
				}

				if (security.get(Constants.ACL) != null) {
					String tmp = security.get(Constants.ACL).toString();
					if (tmp.equalsIgnoreCase(SecurityProvider.DENY.name()))
						aclProvider = SecurityProvider.DENY;
					else if (tmp.equalsIgnoreCase(SecurityProvider.DATABASE.name()))
						aclProvider = SecurityProvider.DATABASE;
					else if (tmp.equalsIgnoreCase(SecurityProvider.HTTP.name()))
						aclProvider = SecurityProvider.HTTP;
					else // if(tmp.equalsIgnoreCase(SecurityProvider.PERMIT.name()))
						aclProvider = SecurityProvider.PERMIT;

					if (aclProvider == SecurityProvider.HTTP) {
						if (security.get(Constants.ACL_URL) != null)
							authorizationHttpUrl = security.get(Constants.ACL_URL).toString().trim();
						else
							return false;
					}

				} else {
					aclProvider = SecurityProvider.PERMIT;
				}

			} else {
				authProvider = SecurityProvider.PERMIT;
				aclProvider = SecurityProvider.PERMIT;
				cacheEnabled = true;
				cacheExpirationInSeconds = Constants.DEFAULT_CACHE_TTL;
			}

			Map<String, Object> silo = (Map<String, Object>) parser.get(Constants.SILO);
			if (silo != null) {

				if (silo.get(Constants.ENABLED) == null)
					siloEnabled = false;
				else
					siloEnabled = (boolean) silo.get(Constants.ENABLED);

				if (silo.get(Constants.INTERVAL) != null)
					siloIntervalSeconds = (int) silo.get(Constants.INTERVAL);
				else
					siloIntervalSeconds = Constants.DEFAULT_SILO_INTERVAL;

				if (siloIntervalSeconds <= 0)
					siloIntervalSeconds = Constants.DEFAULT_SILO_INTERVAL;

				if (silo.get(Constants.COUNT) != null)
					siloBulkCount = (int) silo.get(Constants.COUNT);
				else
					siloBulkCount = Constants.DEFAULT_SILO_COUNT;

				if (siloBulkCount <= 0)
					siloBulkCount = Constants.DEFAULT_SILO_COUNT;

			} else {
				siloEnabled = false;
			}

			Map<String, Object> redis = (Map<String, Object>) parser.get(Constants.REDIS);
			if (redis != null) {
				redisEnabled = true;

				if (redis.get(Constants.HOST) != null)
					redisHost = redis.get(Constants.HOST).toString();
				else
					redisHost = Constants.LOCAL_HOST;

				if (redis.get(Constants.PORT) != null) {
					redisPort = (int) redis.get(Constants.PORT);
					if (redisPort <= 0 || redisPort >= 65535)
						return false;

				} else {
					redisPort = Constants.DEFAULT_REDIS_PORT;
				}

				if (redis.get(Constants.PASSWORD) != null)
					redisPassword = redis.get(Constants.PASSWORD).toString();
				else
					redisPassword = null;

			} else {
				if(authProvider == SecurityProvider.REDIS) {
					redisEnabled = true;
					redisHost = Constants.LOCAL_HOST;
					redisPort = Constants.DEFAULT_REDIS_PORT;
					redisPassword = null;
					
				} else {
					redisEnabled = false;
				}
			}
			
			
			
						
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public int threads;

	public String mqttServerHost = Constants.ALL_HOSTS;
	public int mqttServerPort = Constants.DEFAULT_MQTT_PORT;

	public String dbHost = "127.0.0.1";
	public int dbPort = 0;
	public String dbUsername;
	public String dbPassword;
	public String dbName;

	public DatabaseEngine dbEngine = DatabaseEngine.UNKNOWN;

	public boolean allowAnonymous = true;
	public boolean allowZeroByteClientId = false;

	public String websocketHost = Constants.ALL_HOSTS;
	public int websocketPort = Constants.DEFAULT_WEBSOCKET_PORT;
	public boolean websocketEnabled = false;

	public String httpHost = Constants.ALL_HOSTS;
	public int httpPort = Constants.DEFAULT_WEBSOCKET_PORT;
	public boolean httpEnabled = false;

	public boolean reauthorizeSubscriptionsOnConnect = false;

	public int maxMessageBytes = Constants.DEFAULT_MAX_BYTES_IN_MESSAGE;

	public boolean socketReuseAddress = true;
	public boolean tcpNoDelay = true;
	public boolean socketKeepAlive = true;
	public int socketBacklog = Constants.DEFAULT_SO_BACKLOG;
	public int socketTimeoutSeconds = Constants.DEFAULT_TIMEOUT_SECONDS;

	public boolean epollEnabled = true;

	public String httpsHost = Constants.ALL_HOSTS;
	public int httpsPort = Constants.DEFAULT_HTTPS_PORT;
	public boolean httpsEnabled = false;

	public String sslHost = Constants.ALL_HOSTS;
	public int sslPort = Constants.DEFAULT_SSL_MQTT_PORT;
	public boolean sslEnabled = false;

	public String wssHost = Constants.ALL_HOSTS;
	public int wssPort = Constants.DEFAULT_SECURE_WEBSOCKET_PORT;
	public boolean wssEnabled = false;

	public String certProvider = Constants.JDK;
	public String certKeyManagerPassword;
	public String certKeyStoreType = Constants.JKS;
	public String certPath = Constants.DEFAULT_CERT_PATH;
	public boolean certClientAuth = false;
	public String certKeyStorePassword;

	public SecurityProvider authProvider = SecurityProvider.PERMIT;
	public SecurityProvider aclProvider = SecurityProvider.PERMIT;

	public String authenticationHttpUrl;
	public String authorizationHttpUrl;

	public int siloIntervalSeconds = Constants.DEFAULT_SILO_INTERVAL;
	public int siloBulkCount = Constants.DEFAULT_SILO_COUNT;
	public boolean siloEnabled = false;

	public boolean redisEnabled = false;
	public String redisHost = Constants.LOCAL_HOST;
	public int redisPort = Constants.DEFAULT_REDIS_PORT;
	public String redisPassword = null;

	
	
	public int cacheTtl = Constants.DEFAULT_CACHE_TTL;
	
	
	public boolean cacheEnabled = true;
	public int cacheExpirationInSeconds = Constants.DEFAULT_CACHE_TTL;
}
