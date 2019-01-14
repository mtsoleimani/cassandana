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

import io.cassandana.broker.config.*;
import io.cassandana.broker.security.*;
import io.cassandana.broker.subscriptions.CTrieSubscriptionDirectory;
import io.cassandana.broker.subscriptions.ISubscriptionsDirectory;
import io.cassandana.interception.BrokerInterceptor;
import io.cassandana.interception.InterceptHandler;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.cassandana.persistence.MemorySubscriptionsRepository;
import io.cassandana.silo.Silo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.cassandana.logging.LoggingUtils.getInterceptorIds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Server {

    private static final Logger LOG = LoggerFactory.getLogger(io.cassandana.broker.Server.class);

    private ScheduledExecutorService scheduler;
    private NewNettyAcceptor acceptor;
    private volatile boolean initialized;
    private PostOffice dispatcher;
    private BrokerInterceptor interceptor;
    
    private Silo silo;

    public static void main(String[] args) throws Exception {
        final Server server = new Server();
        server.startServer();
        System.out.println("Server started, version 0.1.1-ALPHA");
        //Bind a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(server::stopServer));
    }

    /**
     * Starts Cassandana bringing the configuration from the file located at ./cassandana.yaml
     * @throws Exception 
     */
    public void startServer() throws Exception {
        startServer(Config.getInstance());
    }


    /**
     * Starts Cassandana bringing the configuration files from the given Config implementation.
     *
     * @param config the configuration to use to start the broker.
     * @throws IOException in case of any IO Error.
     */
    public void startServer(Config config) throws IOException {
        LOG.debug("Starting cassandana integration using IConfig instance");
        startServer(config, null);
    }

    /**
     * Starts Moquette with config provided by an implementation of IConfig class and with the set
     * of InterceptHandler.
     *
     * @param config   the configuration to use to start the broker.
     * @param handlers the handlers to install in the broker.
     * @throws IOException in case of any IO Error.
     */
    public void startServer(Config config, List<? extends InterceptHandler> handlers) throws IOException {
        LOG.debug("Starting cassandana integration using IConfig instance and intercept handlers");
        startServer(config, handlers, null, null, null);
    }

    public void startServer(Config config, List<? extends InterceptHandler> handlers, ISslContextCreator sslCtxCreator,
                            IAuthenticator authenticator, IAuthorizatorPolicy authorizatorPolicy) throws IOException {
        final long start = System.currentTimeMillis();
        if (handlers == null) {
            handlers = Collections.emptyList();
        }
        LOG.trace("Starting Cassandana Server. MQTT message interceptors={}", getInterceptorIds(handlers));

        scheduler = Executors.newScheduledThreadPool(1);

//        final String handlerProp = System.getProperty(BrokerConstants.INTERCEPT_HANDLER_PROPERTY_NAME);
//        if (handlerProp != null) {
//            config.setProperty(BrokerConstants.INTERCEPT_HANDLER_PROPERTY_NAME, handlerProp);
//        }
        
        initInterceptors(config, handlers);
        LOG.debug("Initialized MQTT protocol processor");
        if (sslCtxCreator == null) {
            LOG.info("Using default SSL context creator");
            sslCtxCreator = new DefaultCassandanaSslContextCreator(config);
        }
        authenticator = initializeAuthenticator(authenticator, config);
        authorizatorPolicy = initializeAuthorizatorPolicy(authorizatorPolicy, config);

        
        final ISubscriptionsRepository subscriptionsRepository;
        final IQueueRepository queueRepository;
        final IRetainedRepository retainedRepository;
        
        
        LOG.trace("Configuring in-memory subscriptions store");
        subscriptionsRepository = new MemorySubscriptionsRepository();
        queueRepository = new MemoryQueueRepository();
        retainedRepository = new MemoryRetainedRepository();

        silo = (config.siloEnabled)? Silo.getInstance(config) : null;
        
        ISubscriptionsDirectory subscriptions = new CTrieSubscriptionDirectory();
        subscriptions.init(subscriptionsRepository);
        SessionRegistry sessions = new SessionRegistry(subscriptions, queueRepository);
        dispatcher = new PostOffice(subscriptions, authorizatorPolicy, retainedRepository, sessions, interceptor, silo);
        final BrokerConfiguration brokerConfig = new BrokerConfiguration(config);
        MQTTConnectionFactory connectionFactory = new MQTTConnectionFactory(brokerConfig, authenticator, sessions,
                                                                            dispatcher);

        final NewNettyMQTTHandler mqttHandler = new NewNettyMQTTHandler(connectionFactory);
        acceptor = new NewNettyAcceptor();
        acceptor.initialize(mqttHandler, config, sslCtxCreator);

        final long startTime = System.currentTimeMillis() - start;
        LOG.info("Cassandana integration has been started successfully in {} ms", startTime);
        initialized = true;
    }
    
    private IAuthorizatorPolicy initializeAuthorizatorPolicy(IAuthorizatorPolicy authorizatorPolicy, Config conf) {

        LOG.debug("Configuring MQTT authorizator policy");
        if(conf.aclProvider == SecurityProvider.DENY)
        	return new DenyAllAuthorizatorPolicy();
        else if(conf.aclProvider == SecurityProvider.DATABASE)
        	return new DatabaseAuthorizator(conf);
        else if(conf.aclProvider == SecurityProvider.HTTP)
        	return new HttpAuthorizator(conf);
        else //if(conf.aclProvider == SecurityProvider.PERMIT)
        	return new PermitAllAuthorizatorPolicy();
        
    }

    private IAuthenticator initializeAuthenticator(IAuthenticator authenticator, Config conf) {

        LOG.debug("Configuring MQTT authenticator");
        if(conf.authProvider == SecurityProvider.DENY)
        	return new RejectAllAuthenticator();
        else if(conf.authProvider == SecurityProvider.DATABASE)
        	return new DatabaseAuthenticator(conf);
        else if(conf.authProvider == SecurityProvider.HTTP)
        	return new HttpAuthenticator(conf);
        else if(conf.authProvider == SecurityProvider.REDIS)
        	return new RedisAuthenticator(conf);
        else //if(conf.aclProvider == SecurityProvider.PERMIT)
        	return new AcceptAllAuthenticator();
        
    }

    private void initInterceptors(Config conf, List<? extends InterceptHandler> embeddedObservers) {
        LOG.info("Configuring message interceptors...");

        List<InterceptHandler> observers = new ArrayList<>(embeddedObservers);
        /*String interceptorClassName = props.getProperty(BrokerConstants.INTERCEPT_HANDLER_PROPERTY_NAME);
        if (interceptorClassName != null && !interceptorClassName.isEmpty()) {
            InterceptHandler handler = loadClass(interceptorClassName, InterceptHandler.class,
                                                 io.cassandana.broker.Server.class, this);
            if (handler != null) {
                observers.add(handler);
            }
        }*/
        interceptor = new BrokerInterceptor(conf, observers);
    }


    /**
     * Use the broker to publish a message. It's intended for embedding applications. It can be used
     * only after the integration is correctly started with startServer.
     *
     * @param msg      the message to forward.
     * @param clientId the id of the sending integration.
     * @throws IllegalStateException if the integration is not yet started
     */
    public void internalPublish(MqttPublishMessage msg, final String clientId) {
        final int messageID = msg.variableHeader().packetId();
        if (!initialized) {
            LOG.error("Moquette is not started, internal message cannot be published. CId: {}, messageId: {}", clientId,
                      messageID);
            throw new IllegalStateException("Can't publish on a integration is not yet started");
        }
        LOG.trace("Internal publishing message CId: {}, messageId: {}", clientId, messageID);
        dispatcher.internalPublish(msg);
    }

    public void stopServer() {
        LOG.info("Unbinding integration from the configured ports");
        acceptor.close();
        LOG.trace("Stopping MQTT protocol processor");
        initialized = false;

        // calling shutdown() does not actually stop tasks that are not cancelled,
        // and SessionsRepository does not stop its tasks. Thus shutdownNow().
        scheduler.shutdownNow();
        
        if(silo != null)
        	silo.shutdown();

        LOG.info("Moquette integration has been stopped.");
    }

    /**
     * SPI method used by Broker embedded applications to add intercept handlers.
     *
     * @param interceptHandler the handler to add.
     */
    public void addInterceptHandler(InterceptHandler interceptHandler) {
        if (!initialized) {
            LOG.error("Moquette is not started, MQTT message interceptor cannot be added. InterceptorId={}",
                interceptHandler.getID());
            throw new IllegalStateException("Can't register interceptors on a integration that is not yet started");
        }
        LOG.info("Adding MQTT message interceptor. InterceptorId={}", interceptHandler.getID());
        interceptor.addInterceptHandler(interceptHandler);
    }

    /**
     * SPI method used by Broker embedded applications to remove intercept handlers.
     *
     * @param interceptHandler the handler to remove.
     */
    public void removeInterceptHandler(InterceptHandler interceptHandler) {
        if (!initialized) {
            LOG.error("Moquette is not started, MQTT message interceptor cannot be removed. InterceptorId={}",
                interceptHandler.getID());
            throw new IllegalStateException("Can't deregister interceptors from a integration that is not yet started");
        }
        LOG.info("Removing MQTT message interceptor. InterceptorId={}", interceptHandler.getID());
        interceptor.removeInterceptHandler(interceptHandler);
    }
}
