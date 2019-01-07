


-- --------------------------------------------------------
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS `users` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `users` ADD PRIMARY KEY (`username`);

-- --------------------------------------------------------
-- --------------------------------------------------------



CREATE TABLE IF NOT EXISTS `acl` (
  `id` int(12) NOT NULL,
  `topic` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `clientId` varchar(255),
  `read` int NOT NULL DEFAULT 0,
  `write` int NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

ALTER TABLE `acl` ADD PRIMARY KEY (`id`);
ALTER TABLE `acl` MODIFY `id` int(12) NOT NULL AUTO_INCREMENT;
ALTER TABLE `acl` ADD INDEX(`username`);
ALTER TABLE `acl` ADD INDEX(`topic`);


-- --------------------------------------------------------
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS `silo` (
  `id` int(12) NOT NULL,
  `topic` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `qos` int NOT NULL,
  `message` TEXT NOT NULL,
  `created` BIGINT(19) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

ALTER TABLE `silo` ADD PRIMARY KEY (`id`);
ALTER TABLE `silo` MODIFY `id` int(12) NOT NULL AUTO_INCREMENT;
ALTER TABLE `silo` ADD INDEX(`username`);
ALTER TABLE `silo` ADD INDEX(`topic`);




