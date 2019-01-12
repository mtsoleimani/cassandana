
# Cassandana
Cassandana is an open source MQTT message broker which is entirely written in Java. This project began its life as a fork of [Moquette](https://github.com/andsel/moquette) , and later underwent some cleanup, optimization and adding extra features. Now it’s ready to work as an enterprise message broker.


# Features

 - MQTT compliant broker.
 - Supports QoS 0, QoS 1 and QoS 2
 - TLS (SSL) Encryption
 - PostgreSQL, MySQL and MongoDB Authentication and Authorization
 - Supports Cassandra for Authentication, Authorization and Message Archiving
 - Supports HTTP REST API for Authentication and Authorization
 - Supports Redis for Authentication
 - Supports In-memory caching mechanism to reduce I/O operations
 -  MQTT message archiver ([Silo](https://github.com/mtsoleimani/silo) ported to Cassandana) 
 - Easy configurable (YAML based)
 - Supports WebSocket

# How to install
Cassandana is cross-platform, which could be deployed on Linux, FreeBSD, Mac and Windows.

## Build From Source
Download source code from GitHub by following command:
```
git clone https://github.com/mtsoleimani/cassandana.git 
```

the go to the downloaded directory 
```
cd cassandana 
```

cassandana uses maven to build:
```
mvn clean package
```
jar file will be created in a new directory is called *target*.

## Installation
After building the jar file from source code or grabbing it from [release](https://github.com/mtsoleimani/cassandana/releases), follow the instruction below:
```
mkdir -p /opt/cassandana
```
in case of building from source code:
```
cp -f ./target/cassandana-jar-with-dependencies.jar /opt/cassandana/cassandana.jar
```
if you downloaded jar file from [release](https://github.com/mtsoleimani/cassandana/releases):
```
cp ./cassandana-v0.0.1-ALPHA.jar /opt/cassandana/cassandana.jar
```
copy YAML file (cassandana's configuration file) to /opt/cassandana/
```
cp cassandana.yaml /opt/cassandana/cassandana.yaml
```

## Init.d script
To make Cassandana runs at boot time in Debian use initd script from *scripts* directory which is provided in source code
```cp ./scripts/initd /etc/init.d/cassandana
chmod 755 /etc/init.d/cassandana
update-rc.d cassandana defaults
```
tested in debian jessie and stretch
To start|stop|restart use the following command
```
/etc/init.d/cassandana start|stop\restart
```

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

```allow_anonymous: yes```
**no** to accept only client connections with credentials and **yes** to accept client connection without credentials, validating only the one that provides.

``allow_zero_byte_client_id: no``
**no** to prohibit clients from connecting without a client-id. **yes** to allow clients to connect without a client-id. One will be generated for them.

If you would like to use database to authenticate and authorize users, use the following lines:
```
database:
    engine: mongodb|mysql|postgres|cassandra
    host: HOST_TO_CONNECT
    port: PORT_NUMBER
    username: 
    password: 
    name: cassandana
```
At this time Cassandana supports only MySQL Server, PostgreSQL , MongoDB and Cassandra. We plan it to support more.

In a case of having more control on server you can use the following configuration for TCP server:
```
tcp:
    so_backlog: 128
    so_reuse_address: yes
    tcp_nodelay: yes
    so_keepalive: yes
    timeout_seconds: 10
```

To archive MQTT message use following configuration:
```
silo:
    enabled: no
    interval: 30
    count: 100
```
Silo is an open source tool to archive MQTT messages. Silo store all messages in bulk mode. 
To enable archiving MQTT messge set enabled to ``yes`` default value is ``no``.
``interval`` in second means how many seconds it should wait to store flight-messages in queues
``count`` means on how many messages count, messages phase should run.

**Note:** Scripts for making table in MySQL and PosgreSQL can be found in *scripts* directory

# Authentication and Authorization
Cassandana uses several methods to authenticate and authorize the users:
```
security:
    authentication: database|permit|deny|redis
    acl: database|permit|deny|http    
    # if REST API will be used as auth/acl backend
    auth_url: http://127.0.0.1:9999/mqtt/auth 
    acl_url: http://127.0.0.1:9999/mqtt/acl  
```

 - Database (MySQL, PostgreSQL, MongoDB, Cassandra)
 - Permit All 
 - Deny All
 - HTTP REST API
 - Redis (In memory key-value database)

**Note:** Redis can be used just for authentication

**Note:** Scripts for making tables in MySQL , PosgreSQL and Cassandra can be found    in *scripts* directory

## HTTP Authentication and Authorization
HTTP-based authentication can be performed as below:
```
Authentication: http://HOST:PORT/mqtt/auth 
Method: POST 
Content: JSON

Sample body:
{
	"username": "MY_USERNAME",
	"password": "MY_PASSWORD"
}

Returns 2xx if successful 
```

HTTP-based authorization can be performed as below:
```
Authorization: http://HOST:PORT/mqtt/acl 
Method: POST 
Content: JSON

Sample body:
{
	"username": "MY_USERNAME",
	"clientId": "CLIENT_ID",
	"topic": "TOPIC_NAME ",
	"acl": "pub"
}

Returns 2xx if successful 
```
if asks for publishing ``acl`` should be set to ``pub`` otherwise in case of subscription set it to ``sub``

**Note:** There is a sample HTTP server written in NodeJS in *example* directory.

## Redis-Based Authentication
Authentication can be performed by Redis as a backend to store username and password. Password should be hashed with sha256. To configuring Redis connection use the below parameters:
```
redis:
    host: REDI_HOST
    port: REDIS_PORT
    password: LEAVE_IT_EMPTY_IF_NOT_PROVIDED
```


# The origin of the name
Cassandana was an Achaemenian Persian noblewoman and the "dearly loved" wife of Cyrus the Great. Her daughter Atossa later played an important role in the Achaemenid royal family, as she married Darius the Great and bore him the next Achaemenid king, Xerxes I. ([wikipedia](https://en.wikipedia.org/wiki/Cassandane) )


