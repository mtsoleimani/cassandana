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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;

@Sharable
public class MoquetteIdleTimeoutHandler extends ChannelDuplexHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MoquetteIdleTimeoutHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState e = ((IdleStateEvent) evt).state();
            if (e == IdleState.READER_IDLE) {
                LOG.info("Firing channel inactive event. MqttClientId = {}.", NettyUtils.clientID(ctx.channel()));
                // fire a close that then fire channelInactive to trigger publish of Will
                ctx.close().addListener(CLOSE_ON_FAILURE);
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Firing Netty event CId = {}, eventClass = {}", NettyUtils.clientID(ctx.channel()),
                          evt.getClass().getName());
            }
            super.userEventTriggered(ctx, evt);
        }
    }
}
