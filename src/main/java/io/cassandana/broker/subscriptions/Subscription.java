/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */
package io.cassandana.broker.subscriptions;

import io.netty.handler.codec.mqtt.MqttQoS;

import java.io.Serializable;

/**
 * Maintain the information about which Topic a certain ClientID is subscribed and at which QoS
 */
public final class Subscription implements Serializable {

    private static final long serialVersionUID = -3383457629635732794L;
    private final MqttQoS requestedQos; // max QoS acceptable
    final String clientId;
    final String username;
    final Topic topicFilter;

    public Subscription(String username, String clientId, Topic topicFilter, MqttQoS requestedQos) {
        this.requestedQos = requestedQos;
        this.clientId = clientId;
        this.topicFilter = topicFilter;
        this.username = username;
    }

    public Subscription(Subscription orig) {
        this.requestedQos = orig.requestedQos;
        this.clientId = orig.clientId;
        this.topicFilter = orig.topicFilter;
        this.username = orig.username;
    }

    public String getClientId() {
        return clientId;
    }
    
    public String getUsername() {
        return username;
    }

    public MqttQoS getRequestedQos() {
        return requestedQos;
    }

    public Topic getTopicFilter() {
        return topicFilter;
    }

    public boolean qosLessThan(Subscription sub) {
        return requestedQos.value() < sub.requestedQos.value();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Subscription that = (Subscription) o;

        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null)
            return false;
        return !(topicFilter != null ? !topicFilter.equals(that.topicFilter) : that.topicFilter != null);
    }

    @Override
    public int hashCode() {
        int result = clientId != null ? clientId.hashCode() : 0;
        result = 31 * result + (topicFilter != null ? topicFilter.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("[filter:%s, clientID: %s, qos: %s]", topicFilter, clientId, requestedQos);
    }

    @Override
    public Subscription clone() {
        try {
            return (Subscription) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
