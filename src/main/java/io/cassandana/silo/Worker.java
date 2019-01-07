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

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import io.cassandana.database.IDatabaseOperation;
import io.cassandana.silo.scheduler.IScheduler;

public class Worker implements IScheduler, Runnable {

	private ConcurrentLinkedQueue<SiloMessage> queue;

	private BlockingQueue<Object> taskQueue;

	private long lastTask = 0;

	private AtomicInteger queueSize;

	private int thresholdCount;

	private int thresholdTime;

	private IDatabaseOperation database;

	public void stop() {
		taskQueue.add(null);
	}

	public Worker(int thresholdCount, int thresholdTime, IDatabaseOperation database) {
		this.thresholdCount = thresholdCount;
		this.thresholdTime = thresholdTime;

		queue = new ConcurrentLinkedQueue<>();
		taskQueue = new LinkedBlockingQueue<>();
		queueSize = new AtomicInteger(0);

		this.database = database;

	}

	public void enqueue(SiloMessage message) {
		queue.add(message);
		int size = queueSize.incrementAndGet();

		if (size >= thresholdCount)
			taskQueue.add(new Object());
	}

	@Override
	public void onTimer() {

		if (System.currentTimeMillis() - lastTask >= thresholdTime)
			taskQueue.add(new Object());
	}

	@Override
	public void run() {

		while (true) {

			Object task = null;
			try {
				task = taskQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (task == null)
				break;

			execute(false);
		}

	}

	private void execute(boolean force) {

		int size = queueSize.get();
		int readSize = thresholdCount;
		if (size < thresholdCount)
			readSize = size;
		
		if(force)
			readSize = size;

		ArrayList<SiloMessage> list = new ArrayList<>();
		for (int i = 0; i < readSize; i++) {
			SiloMessage message = queue.poll();
			if (message != null) {
				list.add(message);
				queueSize.decrementAndGet();
			} else {
				break;
			}
		}

		database.bulkInsert(list);
		lastTask = System.currentTimeMillis();
	}
	
	
	public void onShutDown() {
		execute(true);
	}

}
