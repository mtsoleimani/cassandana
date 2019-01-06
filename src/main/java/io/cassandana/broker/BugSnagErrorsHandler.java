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


import com.bugsnag.Bugsnag;

import io.cassandana.broker.config.Config;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class BugSnagErrorsHandler extends ChannelInboundHandlerAdapter {

    private Bugsnag bugsnag;

    public void init(Config conf) {
        final String token = conf.bugsnagToken;
        this.bugsnag = new Bugsnag(token);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        bugsnag.notify(cause);
        ctx.fireExceptionCaught(cause);
    }
}
