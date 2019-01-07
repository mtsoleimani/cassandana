/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */
package io.cassandana.interception;

import io.cassandana.interception.messages.InterceptAcknowledgedMessage;
import io.cassandana.interception.messages.InterceptConnectMessage;
import io.cassandana.interception.messages.InterceptConnectionLostMessage;
import io.cassandana.interception.messages.InterceptDisconnectMessage;
import io.cassandana.interception.messages.InterceptPublishMessage;
import io.cassandana.interception.messages.InterceptSubscribeMessage;
import io.cassandana.interception.messages.InterceptUnsubscribeMessage;

/**
 * Basic abstract class usefull to avoid empty methods creation in subclasses.
 */
public abstract class AbstractInterceptHandler implements InterceptHandler {

    @Override
    public Class<?>[] getInterceptedMessageTypes() {
        return InterceptHandler.ALL_MESSAGE_TYPES;
    }

    @Override
    public void onConnect(InterceptConnectMessage msg) {
    }

    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {
    }

    @Override
    public void onConnectionLost(InterceptConnectionLostMessage msg) {
    }

    @Override
    public void onPublish(InterceptPublishMessage msg) {
    }

    @Override
    public void onSubscribe(InterceptSubscribeMessage msg) {
    }

    @Override
    public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
    }

    @Override
    public void onMessageAcknowledged(InterceptAcknowledgedMessage msg) {
    }
}
