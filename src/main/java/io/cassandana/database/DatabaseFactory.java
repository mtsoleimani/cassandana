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

import io.cassandana.broker.config.Config;

public class DatabaseFactory {
	

	public static IDatabaseOperation get(Config conf) {
		
		
		switch (conf.dbEngine) {
		case MYSQL:
			return new MySqlHelper(conf.dbHost, conf.dbPort, conf.dbUsername, conf.dbPassword, conf.dbName);
			
		case POSTGRES:
			return new PostgresHelper(conf.dbHost, conf.dbPort, conf.dbUsername, conf.dbPassword, conf.dbName);
			
		case MONGODB:
			return new MongodbHelper(conf.dbHost, conf.dbPort, conf.dbUsername, conf.dbPassword, conf.dbName);
			
		case UNKNOWN:
			return null;
		}
		
		return null;
	}
	
}
