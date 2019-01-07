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

public class InterceptUnsubscribeMessage implements InterceptMessage {

    private final String topicFilter;
    private final String clientID;
    private final String username;

    public InterceptUnsubscribeMessage(String topicFilter, String clientID, String username) {
        this.topicFilter = topicFilter;
        this.clientID = clientID;
        this.username = username;
    }

    public String getTopicFilter() {
        return topicFilter;
    }

    public String getClientID() {
        return clientID;
    }

    public String getUsername() {
        return username;
    }
}
