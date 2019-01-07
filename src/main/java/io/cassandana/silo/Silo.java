/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package io.cassandana.silo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.cassandana.broker.config.Config;
import io.cassandana.broker.subscriptions.Topic;
import io.cassandana.database.DatabaseWorkerPool;
import io.cassandana.silo.scheduler.Scheduler;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttQoS;

public class Silo {
	
	private Scheduler scheduler;
	
	private List<Worker> workerPool;
	
	private AtomicInteger roundRobinIndex;
	
	
	private ExecutorService executors;
	
	private static Silo instance;
	
	private int nThreads;

	public synchronized static Silo getInstance(Config conf) {
		if(instance == null)
			instance = new Silo(conf);
		return instance;
	}

	
	private Silo(Config conf) {
		this.nThreads = conf.threads;
		executors = Executors.newFixedThreadPool(nThreads);
		scheduler = new Scheduler(conf.siloIntervalSeconds, 1, TimeUnit.SECONDS);
		roundRobinIndex = new AtomicInteger(0);
		
		workerPool = new ArrayList<>();
		for(int i=0; i<nThreads; i++) {
			Worker worker = new Worker(conf.siloBulkCount, conf.siloIntervalSeconds, DatabaseWorkerPool.getInstance(conf).getDatabaseWorker());
			scheduler.addToSchedulerList(worker);
			workerPool.add(worker);
			executors.submit(worker);
		}
	}
	
	
	public void put(Topic topic, String username, ByteBuf payload, MqttQoS qos) {
		workerPool.get(roundRobinIndex.getAndIncrement() % nThreads).enqueue(
				new SiloMessage(topic.toString(), username, payload.toString(StandardCharsets.UTF_8), qos.ordinal()));
	}
	
	
	public void shutdown() {
		for(int i=0; i<nThreads; i++) {
			workerPool.get(i).onShutDown();
		}
	}

}
