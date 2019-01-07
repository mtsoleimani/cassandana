/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */


package io.cassandana.silo.scheduler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;




public class Scheduler {
	
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	public static final int SECOND_IN_MILLIS = 1000;
	public static final int MINUTE_IN_SECONDS = 60;
	public static final int MINUTE_IN_MILLIS = MINUTE_IN_SECONDS * SECOND_IN_MILLIS;
	public static final int HOURS_IN_SECONDS = 3600;
	public static final int HOURS_IN_MILLIS = SECOND_IN_MILLIS * HOURS_IN_SECONDS;
	public static final int DAY_IN_HOURS = 24;
	public static final long DAY_IN_MILLIS = DAY_IN_HOURS * HOURS_IN_SECONDS * SECOND_IN_MILLIS; 
	
	protected long TIMER_INTERVAL;
	
	protected List<IScheduler> list;
	protected ScheduledExecutorService executor;
	protected ScheduledFuture<?> future;
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	public Scheduler(int interval, int threadPoolSize, TimeUnit unit) {
		list = new CopyOnWriteArrayList<IScheduler>();
		TIMER_INTERVAL = interval;
		initTimer(TIMER_INTERVAL, threadPoolSize, unit);
	}
	
	public Scheduler(int intervalSeconds, int threadPoolSize) {
		list = new CopyOnWriteArrayList<IScheduler>();
		TIMER_INTERVAL = intervalSeconds * SECOND_IN_MILLIS;
		initTimer(TIMER_INTERVAL, threadPoolSize);
	}
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	protected void initTimer(long intervalSeconds, int threadPoolSize, TimeUnit unit) {
		executor = Executors.newScheduledThreadPool(threadPoolSize);
		future = executor.scheduleAtFixedRate(SchedulerRunnable, TIMER_INTERVAL, TIMER_INTERVAL, unit);
	}
	
	protected void initTimer(long intervalSeconds, int threadPoolSize) {
		executor = Executors.newScheduledThreadPool(threadPoolSize);
		future = executor.scheduleAtFixedRate(SchedulerRunnable, TIMER_INTERVAL, TIMER_INTERVAL, TimeUnit.MILLISECONDS);
	}
	
	public long getTimerInterval() {
		return TIMER_INTERVAL;
	}
	
	public void stopTimer() {
		future.cancel(true);
		executor.shutdown();
	}
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	protected Runnable SchedulerRunnable = new Runnable() {
		
        @Override
        public void run() {
        	for(IScheduler listener : list){
        		if(listener == null)
        			list.remove(listener);
        		else
        			listener.onTimer();
        	}
        }
    };
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
    public void addToSchedulerList(IScheduler listener) {
    	if(listener != null)
    		list.add(listener);
    }
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
    public void removeFromSchedulerList(IScheduler listener) {
    	if(listener != null)
    		list.remove(listener);
    }
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
}
