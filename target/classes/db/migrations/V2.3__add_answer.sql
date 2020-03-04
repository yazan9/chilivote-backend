CREATE TABLE IF NOT EXISTS `answer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `chilivote_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkwquj56nugetxobeygytlr3og` (`chilivote_id`),
  KEY `FK68tbcw6bunvfjaoscaj851xpb` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8