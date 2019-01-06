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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import io.cassandana.broker.config.Config;

public class DatabaseWorkerPool {
	
	private static DatabaseWorkerPool instance;
	public synchronized static DatabaseWorkerPool getInstance(Config conf) {
		if(instance == null)
			instance = new DatabaseWorkerPool(conf);
		return instance;
	}
	
	private int poolSize;
	
	private AtomicInteger index;
	
	private ArrayList<IDatabaseOperation> dbPool;
	
	
	private DatabaseWorkerPool(Config conf) {
		init(conf);
	}

	private void init(Config conf) {
		this.poolSize = conf.threads;
		index = new AtomicInteger(0);
		dbPool = new ArrayList<>();
		
		for(int i=0; i<poolSize; i++) {
			IDatabaseOperation dbWorker = DatabaseFactory.get(conf);
			dbWorker.tryConnecting();
			this.dbPool.add(dbWorker);
		}
	}
	
	
	public IDatabaseOperation getDatabaseWorker() {
		return dbPool.get(index.getAndIncrement() % poolSize);
	}

	

}
