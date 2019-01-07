/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */
package io.cassandana.interception.messages;

import io.netty.handler.codec.mqtt.MqttConnectMessage;

public class InterceptConnectMessage extends InterceptAbstractMessage {

    private final MqttConnectMessage msg;

    public InterceptConnectMessage(MqttConnectMessage msg) {
        super(msg);
        this.msg = msg;
    }

    public String getClientID() {
        return msg.payload().clientIdentifier();
    }

    public boolean isCleanSession() {
        return msg.variableHeader().isCleanSession();
    }

    public int getKeepAlive() {
        return msg.variableHeader().keepAliveTimeSeconds();
    }

    public boolean isPasswordFlag() {
        return msg.variableHeader().hasPassword();
    }

    public byte getProtocolVersion() {
        return (byte) msg.variableHeader().version();
    }

    public String getProtocolName() {
        return msg.variableHeader().name();
    }

    public boolean isUserFlag() {
        return msg.variableHeader().hasUserName();
    }

    public boolean isWillFlag() {
        return msg.variableHeader().isWillFlag();
    }

    public byte getWillQos() {
        return (byte) msg.variableHeader().willQos();
    }

    public boolean isWillRetain() {
        return msg.variableHeader().isWillRetain();
    }

    public String getUsername() {
        return msg.payload().userName();
    }

    public byte[] getPassword() {
        return msg.payload().passwordInBytes();// password().getBytes(StandardCharsets.UTF_8);
    }

    public String getWillTopic() {
        return msg.payload().willTopic();
    }

    public byte[] getWillMessage() {
        return msg.payload().willMessageInBytes();// willMessage().getBytes(StandardCharsets.UTF_8);
    }
}
