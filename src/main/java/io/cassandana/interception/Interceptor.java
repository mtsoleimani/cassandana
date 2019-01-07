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
import io.cassandana.interception.messages.InterceptAcknowledgedMessage;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * This interface is to be used internally by the broker components.
 * <p>
 * An interface is used instead of a class to allow more flexibility in changing an implementation.
 * <p>
 * Interceptor implementations forward notifications to a <code>InterceptHandler</code>, that is
 * normally a field. So, the implementations should act as a proxy to a custom intercept handler.
 *
 * @see InterceptHandler
 */
public interface Interceptor {

    void notifyClientConnected(MqttConnectMessage msg);

    void notifyClientDisconnected(String clientID, String username);

    void notifyClientConnectionLost(String clientID, String username);

    void notifyTopicPublished(MqttPublishMessage msg, String clientID, String username);

    void notifyTopicSubscribed(Subscription sub, String username);

    void notifyTopicUnsubscribed(String topic, String clientID, String username);

    void notifyMessageAcknowledged(InterceptAcknowledgedMessage msg);

    void addInterceptHandler(InterceptHandler interceptHandler);

    void removeInterceptHandler(InterceptHandler interceptHandler);
}
