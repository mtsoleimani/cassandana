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

import java.sql.SQLException;
import com.mysql.cj.jdbc.MysqlDataSource;


public class MySqlHelper extends AbstractSqlDatabase {

	public MySqlHelper(String host, int port, String dbUsername, String dbPassword, String dbName,
			long dbReconnectSeconds) {
		super(host, port, dbUsername, dbPassword, dbName, dbReconnectSeconds);
	}

	public MySqlHelper(String host, int port, String dbUsername, String dbPassword, String dbName) {
		super(host, port, dbUsername, dbPassword, dbName);
	}

	public MySqlHelper(String host, int port, String dbName) {
		super(host, port, dbName);
	}

	public MySqlHelper() {
		super();
	}
	
	public synchronized void tryConnecting() {

		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setServerName(this.host);
		dataSource.setPort(port);
		dataSource.setUser(this.dbUsername);
		dataSource.setPassword(this.dbPassword);
		dataSource.setDatabaseName(this.dbName);

		try {
			dataSource.setCharacterEncoding("utf-8");
			connection = dataSource.getConnection();

			setConnected(true);
			onConnected();
			System.out.println("connected to mysql server: " + host + ":" + port);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
