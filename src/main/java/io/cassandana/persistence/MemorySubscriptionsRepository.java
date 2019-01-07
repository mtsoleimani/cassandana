/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package io.cassandana.persistence;

import io.cassandana.broker.ISubscriptionsRepository;
import io.cassandana.broker.subscriptions.Subscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemorySubscriptionsRepository implements ISubscriptionsRepository {

    private final List<Subscription> subscriptions = new ArrayList<>();

    @Override
    public List<Subscription> listAllSubscriptions() {
        return Collections.unmodifiableList(subscriptions);
    }

    @Override
    public void addNewSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    @Override
    public void removeSubscription(String topic, String clientID) {
        subscriptions.stream()
            .filter(s -> s.getTopicFilter().toString().equals(topic) && s.getClientId().equals(clientID))
            .findFirst()
            .ifPresent(subscriptions::remove);
    }
}
