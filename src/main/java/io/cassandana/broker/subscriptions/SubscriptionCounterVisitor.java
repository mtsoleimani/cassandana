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

import java.util.concurrent.atomic.AtomicInteger;

class SubscriptionCounterVisitor implements CTrie.IVisitor<Integer> {

    private AtomicInteger accumulator = new AtomicInteger(0);

    @Override
    public void visit(CNode node, int deep) {
        accumulator.addAndGet(node.subscriptions.size());
    }

    @Override
    public Integer getResult() {
        return accumulator.get();
    }
}
