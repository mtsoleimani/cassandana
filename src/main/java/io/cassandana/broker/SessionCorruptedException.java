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

public class SessionCorruptedException extends RuntimeException {

    private static final long serialVersionUID = 5848069213104389412L;

    SessionCorruptedException(String msg) {
        super(msg);
    }
}
