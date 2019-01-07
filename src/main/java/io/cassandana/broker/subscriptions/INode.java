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

import java.util.concurrent.atomic.AtomicReference;

class INode {
    private AtomicReference<CNode> mainNode = new AtomicReference<>();

    INode(CNode mainNode) {
        this.mainNode.set(mainNode);
        if (mainNode instanceof TNode) { // this should never happen
            throw new IllegalStateException("TNode should not be set on mainNnode");
        }
    }

    boolean compareAndSet(CNode old, CNode newNode) {
        return mainNode.compareAndSet(old, newNode);
    }

    boolean compareAndSet(CNode old, TNode newNode) {
        return mainNode.compareAndSet(old, newNode);
    }

    CNode mainNode() {
        return this.mainNode.get();
    }

    boolean isTombed() {
        return this.mainNode() instanceof TNode;
    }
}
