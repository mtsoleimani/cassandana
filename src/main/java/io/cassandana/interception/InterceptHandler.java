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

import io.cassandana.broker.subscriptions.Subscription;
import io.cassandana.interception.messages.*;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * This interface is used to inject code for intercepting broker events.
 * <p>
 * The events can act only as observers.
 * <p>
 * Almost every method receives a subclass of {@link MqttMessage}, except <code>onDisconnect</code>
 * that receives the client id string and <code>onSubscribe</code> and <code>onUnsubscribe</code>
 * that receive a {@link Subscription} object.
 */
public interface InterceptHandler {

    Class<?>[] ALL_MESSAGE_TYPES = {InterceptConnectMessage.class, InterceptDisconnectMessage.class,
            InterceptConnectionLostMessage.class, InterceptPublishMessage.class, InterceptSubscribeMessage.class,
            InterceptUnsubscribeMessage.class, InterceptAcknowledgedMessage.class};

    /**
     * @return the identifier of this intercept handler.
     */
    String getID();

    /**
     * @return the InterceptMessage subtypes that this handler can process. If the result is null or
     * equal to ALL_MESSAGE_TYPES, all the message types will be processed.
     */
    Class<?>[] getInterceptedMessageTypes();

    void onConnect(InterceptConnectMessage msg);

    void onDisconnect(InterceptDisconnectMessage msg);

    void onConnectionLost(InterceptConnectionLostMessage msg);

    void onPublish(InterceptPublishMessage msg);

    void onSubscribe(InterceptSubscribeMessage msg);

    void onUnsubscribe(InterceptUnsubscribeMessage msg);

    void onMessageAcknowledged(InterceptAcknowledgedMessage msg);
}
