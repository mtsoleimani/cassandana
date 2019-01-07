/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */
package io.cassandana.broker.security;

import static io.cassandana.broker.security.Authorization.Permission.READWRITE;

import io.cassandana.broker.subscriptions.Topic;

/**
 * Carries the read/write authorization to topics for the users.
 */
public class Authorization {

    protected final Topic topic;
    protected final Permission permission;

    /**
     * Access rights
     */
    enum Permission {
        READ, WRITE, READWRITE
    }

    Authorization(Topic topic) {
        this(topic, Permission.READWRITE);
    }

    Authorization(Topic topic, Permission permission) {
        this.topic = topic;
        this.permission = permission;
    }

    public boolean grant(Permission desiredPermission) {
        return permission == desiredPermission || permission == READWRITE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Authorization that = (Authorization) o;

        if (permission != that.permission)
            return false;
        if (!topic.equals(that.topic))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = topic.hashCode();
        result = 31 * result + permission.hashCode();
        return result;
    }
}
