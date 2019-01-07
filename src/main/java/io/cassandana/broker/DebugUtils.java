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

import java.nio.charset.StandardCharsets;

public final class DebugUtils {

    public static String payload2Str(ByteBuf content) {
        final ByteBuf copy = content.copy();
        final byte[] bytesContent;
        if (copy.isDirect()) {
            final int size = copy.readableBytes();
            bytesContent = new byte[size];
            copy.readBytes(bytesContent);
        } else {
            bytesContent = copy.array();
        }
        return new String(bytesContent, StandardCharsets.UTF_8);
    }

    private DebugUtils() {
    }
}
