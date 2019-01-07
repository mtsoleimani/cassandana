/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */
package io.cassandana.broker;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.EventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Resend inflight not ack'ed publish packets (QoS1 PUB and QoS2 PUB/PUBREL). It's inspired by IdleStateHandler but it's
 * specialized version, just invoking Session's resendInflightNotAcked by the channel after a period.
 */
public class InflightResender extends ChannelDuplexHandler {

    /**
     * Placeholder event to resend not-acked publish messages in the in flight window.
     * */
    public static class ResendNotAckedPublishes {
    }

    private final class WriterIdleTimeoutTask implements Runnable {

        private final ChannelHandlerContext ctx;

        WriterIdleTimeoutTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            if (!ctx.channel().isOpen()) {
                return;
            }
            long nextDelay = resenderTimeNanos - (System.nanoTime() - lastExecutionTime);
            if (nextDelay <= 0) {
                // Writer is idle - set a new timeout and notify the callback.
                resenderTimeout = ctx.executor().schedule(this, resenderTimeNanos, TimeUnit.NANOSECONDS);
                try {
                    resendNotAcked(ctx/* , event */);
                } catch (Throwable t) {
                    ctx.fireExceptionCaught(t);
                }
            } else {
                // Write occurred before the timeout - set a new timeout with shorter delay.
                resenderTimeout = ctx.executor().schedule(this, nextDelay, TimeUnit.NANOSECONDS);
            }
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(InflightResender.class);
    private static final long MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1);

    private final long resenderTimeNanos;
    volatile ScheduledFuture<?> resenderTimeout;
    volatile long lastExecutionTime;

    private volatile int state; // 0 - none, 1 - initialized, 2 - destroyed

    public InflightResender(long writerIdleTime, TimeUnit unit) {
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        resenderTimeNanos = Math.max(unit.toNanos(writerIdleTime), MIN_TIMEOUT_NANOS);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive() && ctx.channel().isRegistered()) {
            // channelActive() event has been fired already, which means this.channelActive() will
            // not be invoked. We have to initialize here instead.
            initialize(ctx);
        } else {
            // channelActive() event has not been fired yet. this.channelActive() will be invoked
            // and initialization will occur there.
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        destroy();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        // Initialize early if channel is active already.
        if (ctx.channel().isActive()) {
            initialize(ctx);
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // This method will be invoked only if this handler was added
        // before channelActive() event is fired. If a user adds this handler
        // after the channelActive() event, initialize() will be called by beforeAdd().
        initialize(ctx);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        destroy();
        super.channelInactive(ctx);
    }

    private void initialize(ChannelHandlerContext ctx) {
        // Avoid the case where destroy() is called before scheduling timeouts.
        // See: https://github.com/netty/netty/issues/143
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing autoflush handler on channel {}", ctx.channel());
        }
        switch (state) {
            case 1:
            case 2:
                return;
        }

        state = 1;

        EventExecutor loop = ctx.executor();

        lastExecutionTime = System.nanoTime();
        resenderTimeout = loop.schedule(new WriterIdleTimeoutTask(ctx), resenderTimeNanos, TimeUnit.NANOSECONDS);
    }

    private void destroy() {
        state = 2;

        if (resenderTimeout != null) {
            resenderTimeout.cancel(false);
            resenderTimeout = null;
        }
    }

    private void resendNotAcked(ChannelHandlerContext ctx/* , IdleStateEvent evt */) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Flushing idle Netty channel {} for clientId: {}", ctx.channel(),
                      NettyUtils.clientID(ctx.channel()));
        }
        ctx.fireUserEventTriggered(new ResendNotAckedPublishes());
    }
}
