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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import io.cassandana.broker.security.AclEntity;


public abstract class AbstractSqlDatabase extends DatabaseHelper implements IDatabaseOperation {

	protected Connection connection = null;

	public AbstractSqlDatabase(String host, int port, String dbUsername, String dbPassword, String dbName,
			long dbReconnectSeconds) {

		super(host, port, dbUsername, dbPassword, dbName, dbReconnectSeconds);
	}

	public AbstractSqlDatabase(String host, int port, String dbUsername, String dbPassword, String dbName) {
		super(host, port, dbUsername, dbPassword, dbName);
	}

	public AbstractSqlDatabase(String host, int port, String dbName) {
		super(host, port, dbName);
	}

	public AbstractSqlDatabase() {
		super();
	}

	@Override
	public void onConnected() {

	}

	public abstract void tryConnecting();

	@Override
	public void shutdown() {
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	
	@Override
	public String getSecret(String username) {
		if(connection == null || !connected)
			return null;
		
		String secret = null;
		
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement("select password from users where username = ?");
			statement.setString(1, username);
			ResultSet result = null;
			
			if(statement.execute())
				result = statement.getResultSet();
			
			if(result != null && result.next()) {
				secret = result.getString("password");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			if(statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					
				}
		}
		
		return secret;
	}

	@Override
	public AclEntity getAcl(String topic, String username, String clientId) {
		if(connection == null || !connected)
			return null;
		
		AclEntity acl = null;
		
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement("select * from acl where topic = ? and username = ?");
			statement.setString(1, topic);
			statement.setString(2, username);
			ResultSet result = null;
			
			if(statement.execute())
				result = statement.getResultSet();
			
			if(result != null && result.next()) {
				acl = new AclEntity();
				acl.username = username;
				acl.clientId = clientId;
				acl.topic = topic;
				acl.canPublish = (result.getInt("write") == 1);
				acl.canSubscribe = (result.getInt("read") == 1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			if(statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					
				}
		}
		
		return acl;
	}

	@Override
	public List<AclEntity> getAcl(String topic) {
		// TODO Auto-generated method stub
		return null;
	}

}
