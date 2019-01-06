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

import java.sql.Connection;
import java.sql.DriverManager;

public class PostgresHelper extends AbstractSqlDatabase {

	protected Connection connection = null;

	public PostgresHelper(String host, int port, String dbUsername, String dbPassword, String dbName,
			long dbReconnectSeconds) {

		super(host, port, dbUsername, dbPassword, dbName, dbReconnectSeconds);
	}

	public PostgresHelper(String host, int port, String dbUsername, String dbPassword, String dbName) {
		super(host, port, dbUsername, dbPassword, dbName);
	}

	public PostgresHelper(String host, int port, String dbName) {
		super(host, port, dbName);
	}

	public PostgresHelper() {
		super();
	}

	public synchronized void tryConnecting() {

		try {
			Class.forName("org.postgresql.Driver");
			this.connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + dbName,
					dbUsername, dbPassword);

			setConnected(true);
			onConnected();
			System.out.println("connected to postgres server: " + host + ":" + port);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
