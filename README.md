
# Cassandana
cassandan is an open source MQTT message broker which is entirely written in Java. This project began its life as a fork of [Moquette](http://andsel.github.io/moquette/) , and later underwent some major cleanup, optimization and adding extra features. Now it’s ready to work as an enterprise message broker.


# How to install
Cassandana is cross-platform, which could be deployed on Linux, FreeBSD, Mac and Windows.

## Build From Source
Download source code from GitHub by following command:
``git clone https://github.com/mtsoleimani/cassandana.git ``
the go to the downloaded directory 
``cd cassandana ``
cassandana uses maven to build:
``mvn clean package``
jar file will be created in a new directory is called *target*.

## Installation
After building the jar file from source code or grabbing it from [release](https://github.com/mtsoleimani/cassandana/releases), follow the instruction below:
`` mkdir -p /opt/cassandana``
in case of building from source code:
``cp -f ./target/cassandana-jar-with-dependencies.jar /opt/cassandana/cassandana.jar``
if you downloaded jar file from [release](https://github.com/mtsoleimani/cassandana/releases):
``cp ./cassandana-v0.0.1-ALPHA.jar /opt/cassandana/cassandana.jar``
copy YAML file (cassandana's configuration file) to /opt/cassandana/
``cp cassandana.yaml /opt/cassandana/cassandana.yaml``

## Init.d script
To make Cassandana runs at boot time in Debian use initd script from *script* directory which is provided in source code
``cp ./script/initd /etc/init.d/cassandana``
``chmod 755 /etc/init.d/cassandana``
``update-rc.d cassandana defaults``
tested in debian jessie and stretch
To start|stop|restart use the following command
``/etc/init.d/cassandana start|stop\restart``

## Configuration
The configuration file is written in YAML. 
``threads`` it's the number of process. It's highly recommended to equal the number of CPU cores. Value 0 means all available CPU cores (default value 0).
``host`` IP Address which server should listen to it (default value is 0.0.0.0).
``port`` Port number which server should listen to it (default value is default MQTT port 1883).


If you would like to use web-socket enabled it from this section otherwise set enabled to **no**.
```
websocket:
    enabled: yes  
    host: 0.0.0.0  
    port: 8080
```

This section determines if SSL should be enabled. 
```
ssl:
    enabled: no
    host: 0.0.0.0
    port: 1884  
```

If you would like to enable secure web-socket use the following configuration:

```
wss:
    enabled: yes
    host: 0.0.0.0
    port: 8084  
```

When you enabled SSL or WSS you should provide certification's information:
```
cert:
    provider: JDK
    key_manager_password: YOUR_PASSWORD
    key_store_password: YOUR_PASSWORD
    key_store_type: jks 
    path: cert/cassandana.jks
    client_auth: no
``` 

Authentication and Authorization could be configured as below:
```
security:
    authentication: database|permit|deny
    acl: database|permit|deny      
```
permit: permit all users to perform a task
deny: deny all users to perform a task
database: use database to ask


```allow_anonymous: yes```
**no** to accept only client connections with credentials and **yes** to accept client connection without credentials, validating only the one that provides.

``allow_zero_byte_client_id: no``
**no** to prohibit clients from connecting without a client-id. **yes** to allow clients to connect without a client-id. One will be generated for them.

If you would like to use database to authenticate and authorize users, use the following lines:
```
database:
    engine: mongodb|mysql|postgres
    host: HOST_TO_CONNECT
    port: PORT_NUMBER
    username: 
    password: 
    name: cassandana
```
At this time Cassandana supports only MySQL Server, PostgreSQL and MongoDB. We plan it to support more.

In a case of having more control on server you can use the following configuration for TCP server:
```
tcp:
    so_backlog: 128
    so_reuse_address: yes
    tcp_nodelay: yes
    so_keepalive: yes
    timeout_seconds: 10
```


# The origin of the name
[Cassandana](https://en.wikipedia.org/wiki/Cassandane) was an Achaemenian Persian noblewoman and the "dearly loved" wife of Cyrus the Great. Her daughter Atossa later played an important role in the Achaemenid royal family, as she married Darius the Great and bore him the next Achaemenid king, Xerxes I.

