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

class TNode extends CNode {

    @Override
    INode childOf(Token token) {
        throw new IllegalStateException("Can't be invoked on TNode");
    }

    @Override
    CNode copy() {
        throw new IllegalStateException("Can't be invoked on TNode");
    }

    @Override
    public void add(INode newINode) {
        throw new IllegalStateException("Can't be invoked on TNode");
    }

    @Override
    CNode addSubscription(Subscription newSubscription) {
        throw new IllegalStateException("Can't be invoked on TNode");
    }

    @Override
    boolean containsOnly(String clientId) {
        throw new IllegalStateException("Can't be invoked on TNode");
    }

    @Override
    public boolean contains(String clientId) {
        throw new IllegalStateException("Can't be invoked on TNode");
    }

    @Override
    void removeSubscriptionsFor(String clientId) {
        throw new IllegalStateException("Can't be invoked on TNode");
    }

    @Override
    boolean anyChildrenMatch(Token token) {
        return false;
    }
}
