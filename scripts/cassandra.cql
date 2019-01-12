CREATE KEYSPACE cassandana WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};


CREATE TABLE users(
	username varchar PRIMARY KEY,
	password varchar
);


CREATE TABLE acl(
	topic varchar,
	username varchar,
	clientId varchar,
	read int,
	write int,
	PRIMARY KEY (topic, username) 
);

CREATE INDEX idx_sec_acl_username ON acl (username);


CREATE TABLE silo (
	id uuid PRIMARY KEY,
	topic varchar,
	username varchar,
	qos int,
	message varchar,
	created timestamp
);

CREATE INDEX idx_sec_silo_topic ON silo (topic);
CREATE INDEX idx_sec_silo_username ON silo (username);





