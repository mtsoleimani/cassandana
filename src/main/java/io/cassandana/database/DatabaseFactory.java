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

import io.cassandana.Constants;
import io.cassandana.broker.config.Config;

public class DatabaseFactory {
	

	public static IDatabaseOperation get(Config conf) {
		
		if(conf.dbName == null || conf.dbName.isEmpty())
			conf.dbName = Constants.DEFAULT_DATABASE_NAME;
		
		if(conf.dbHost == null || conf.dbHost.isEmpty())
			conf.dbHost = Constants.LOCAL_HOST;
		
		switch (conf.dbEngine) {
		case MYSQL:
			if(conf.dbPort == 0)
				conf.dbPort = Constants.DEFAULT_MYSQL_PORT;
			return new MySqlHelper(conf.dbHost, conf.dbPort, conf.dbUsername, conf.dbPassword, conf.dbName);
			
		case POSTGRES:
			if(conf.dbPort == 0)
				conf.dbPort = Constants.DEFAULT_POSTGRES_PORT;
			return new PostgresHelper(conf.dbHost, conf.dbPort, conf.dbUsername, conf.dbPassword, conf.dbName);
			
		case MONGODB:
			if(conf.dbPort == 0)
				conf.dbPort = Constants.DEFAULT_MONGODB_PORT;
			return new MongodbHelper(conf.dbHost, conf.dbPort, conf.dbUsername, conf.dbPassword, conf.dbName);
			
		case CASSANDRA:
			if(conf.dbPort == 0)
				conf.dbPort = Constants.DEFAULT_CASSANDRA_PORT;
			return new CassandraHelper(conf.dbHost, conf.dbPort, conf.dbUsername, conf.dbPassword, conf.dbName);
			
		case UNKNOWN:
			return null;
		}
		
		return null;
	}
	
}
