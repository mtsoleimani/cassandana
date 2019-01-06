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

    public static void main(String[] args) throws Exception {
        final Server server = new Server();
        server.startServer();
        System.out.println("Server started, version 0.0.1-ALPHA");
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
        
        /*final String persistencePath = config.getProperty(BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME);
        LOG.debug("Configuring Using persistent store file, path: {}", persistencePath);
        if (persistencePath != null && !persistencePath.isEmpty()) {/
            LOG.trace("Configuring H2 subscriptions store to {}", persistencePath);
            h2Builder = new H2Builder(config, scheduler).initStore();
            subscriptionsRepository = h2Builder.subscriptionsRepository();
            queueRepository = h2Builder.queueRepository();
            retainedRepository = h2Builder.retainedRepository();
        } else {
            LOG.trace("Configuring in-memory subscriptions store");
            subscriptionsRepository = new MemorySubscriptionsRepository();
            queueRepository = new MemoryQueueRepository();
            retainedRepository = new MemoryRetainedRepository();
        }*/

        ISubscriptionsDirectory subscriptions = new CTrieSubscriptionDirectory();
        subscriptions.init(subscriptionsRepository);
        SessionRegistry sessions = new SessionRegistry(subscriptions, queueRepository);
        dispatcher = new PostOffice(subscriptions, authorizatorPolicy, retainedRepository, sessions, interceptor);
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
        /*String authorizatorClassName = props.getProperty(BrokerConstants.AUTHORIZATOR_CLASS_NAME, "");
        if (authorizatorPolicy == null && !authorizatorClassName.isEmpty()) {
            authorizatorPolicy = loadClass(authorizatorClassName, IAuthorizatorPolicy.class, IConfig.class, props);
        }

        if (authorizatorPolicy == null) {
            String aclFilePath = props.getProperty(BrokerConstants.ACL_FILE_PROPERTY_NAME, "");
            if (aclFilePath != null && !aclFilePath.isEmpty()) {
                authorizatorPolicy = new DenyAllAuthorizatorPolicy();
                try {
                    LOG.info("Parsing ACL file. Path = {}", aclFilePath);
                    IResourceLoader resourceLoader = props.getResourceLoader();
                    authorizatorPolicy = ACLFileParser.parse(resourceLoader.loadResource(aclFilePath));
                } catch (ParseException pex) {
                    LOG.error("Unable to parse ACL file. path=" + aclFilePath, pex);
                }
            } else {
                authorizatorPolicy = new PermitAllAuthorizatorPolicy();
            }
            LOG.info("Authorizator policy {} instance will be used", authorizatorPolicy.getClass().getName());
        }
        return authorizatorPolicy;
        */
        
        if(conf.aclProvider == SecurityProvider.DENY)
        	return new DenyAllAuthorizatorPolicy();
        else if(conf.aclProvider == SecurityProvider.DATABASE)
        	return new DatabaseAuthorizator(conf);
        else //if(conf.aclProvider == SecurityProvider.PERMIT)
        	return new PermitAllAuthorizatorPolicy();
        
    }

    private IAuthenticator initializeAuthenticator(IAuthenticator authenticator, Config conf) {

        LOG.debug("Configuring MQTT authenticator");
        /*String authenticatorClassName = props.getProperty(BrokerConstants.AUTHENTICATOR_CLASS_NAME, "");

        if (authenticator == null && !authenticatorClassName.isEmpty()) {
            authenticator = loadClass(authenticatorClassName, IAuthenticator.class, IConfig.class, props);
        }

        IResourceLoader resourceLoader = props.getResourceLoader();
        if (authenticator == null) {
            String passwdPath = props.getProperty(BrokerConstants.PASSWORD_FILE_PROPERTY_NAME, "");
            if (passwdPath.isEmpty()) {
                authenticator = new AcceptAllAuthenticator();
            } else {
                authenticator = new ResourceAuthenticator(resourceLoader, passwdPath);
            }
            LOG.info("An {} authenticator instance will be used", authenticator.getClass().getName());
        }
        return authenticator;
        */
        
        if(conf.authProvider == SecurityProvider.DENY)
        	return new RejectAllAuthenticator();
        else if(conf.authProvider == SecurityProvider.DATABASE)
        	return new DatabaseAuthenticator(conf);
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

//    @SuppressWarnings("unchecked")
//    private <T, U> T loadClass(String className, Class<T> intrface, Class<U> constructorArgClass, U props) {
//        T instance = null;
//        try {
//            // check if constructor with constructor arg class parameter
//            // exists
//            LOG.info("Invoking constructor with {} argument. ClassName={}, interfaceName={}",
//                     constructorArgClass.getName(), className, intrface.getName());
//            instance = this.getClass().getClassLoader()
//                .loadClass(className)
//                .asSubclass(intrface)
//                .getConstructor(constructorArgClass)
//                .newInstance(props);
//        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
//            LOG.warn("Unable to invoke constructor with {} argument. ClassName={}, interfaceName={}, cause={}, " +
//                     "errorMessage={}", constructorArgClass.getName(), className, intrface.getName(), ex.getCause(),
//                     ex.getMessage());
//            return null;
//        } catch (NoSuchMethodException | InvocationTargetException e) {
//            try {
//                LOG.info("Invoking default constructor. ClassName={}, interfaceName={}", className, intrface.getName());
//                // fallback to default constructor
//                instance = this.getClass().getClassLoader()
//                    .loadClass(className)
//                    .asSubclass(intrface)
//                    .getDeclaredConstructor().newInstance();
//            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException |
//                NoSuchMethodException | InvocationTargetException ex) {
//                LOG.error("Unable to invoke default constructor. ClassName={}, interfaceName={}, cause={}, " +
//                          "errorMessage={}", className, intrface.getName(), ex.getCause(), ex.getMessage());
//                return null;
//            }
//        }
//
//        return instance;
//    }

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

        LOG.info("Moquette integration has been stopped.");
    }

    /**
     * SPI method used by Broker embedded applications to get list of subscribers. Returns null if
     * the broker is not started.
     *
     * @return list of subscriptions.
     */
// TODO reimplement this
//    public List<Subscription> getSubscriptions() {
//        if (m_processorBootstrapper == null) {
//            return null;
//        }
//        return this.subscriptionsStore.listAllSubscriptions();
//    }

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
