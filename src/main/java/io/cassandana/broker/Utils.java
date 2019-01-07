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

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;

import java.security.MessageDigest;
import java.util.Formatter;
import java.util.Map;

/**
 * Utility static methods, like Map get with default value, or elvis operator.
 */
public final class Utils {

    public static <T, K> T defaultGet(Map<K, T> map, K key, T defaultValue) {
        T value = map.get(key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static int messageId(MqttMessage msg) {
        return ((MqttMessageIdVariableHeader) msg.variableHeader()).messageId();
    }

    public static byte[] readBytesAndRewind(ByteBuf payload) {
        byte[] payloadContent = new byte[payload.readableBytes()];
        int mark = payload.readerIndex();
        payload.readBytes(payloadContent);
        payload.readerIndex(mark);
        return payloadContent;
    }

    private Utils() {
    	
    }
    
    
    public static String getSha256(byte[] password) {
	    String digest = "";
	    try {
	        MessageDigest crypt = MessageDigest.getInstance("SHA-256");
	        crypt.reset();
	        crypt.update(password);
	        digest = byteToHex(crypt.digest());
	        
	    } catch(Exception e) {
	    	e.printStackTrace();
	        return null;
	    }
	    return digest;
	}
    
    public static String byteToHex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash) {
	        formatter.format("%02x", b);
	    }
	    
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
}