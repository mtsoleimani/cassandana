

-- --------------------------------------------------------
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS users (
  username varchar(255) PRIMARY KEY NOT NULL,
  password varchar(255) NOT NULL
);


-- --------------------------------------------------------
-- --------------------------------------------------------


CREATE TABLE IF NOT EXISTS acl (
  id serial8 PRIMARY KEY NOT NULL,
  topic varchar(255) NOT NULL,
  username varchar(255) NOT NULL,
  clientId varchar(255),
  read int NOT NULL DEFAULT 0,
  write int NOT NULL DEFAULT 0,
);


CREATE INDEX idx_topic ON acl (topic);
CREATE INDEX idx_username ON acl (username);



-- --------------------------------------------------------
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS silo (
  id serial8 PRIMARY KEY NOT NULL,
  topic varchar(255) NOT NULL,
  username varchar(255) NOT NULL,
  qos int NOT NULL,
  message TEXT NOT NULL,
  created bigint NOT NULL DEFAULT 0
);


CREATE INDEX idx_topic ON silo (topic);
CREATE INDEX idx_username ON silo (username);

-- --------------------------------------------------------
-- --------------------------------------------------------






