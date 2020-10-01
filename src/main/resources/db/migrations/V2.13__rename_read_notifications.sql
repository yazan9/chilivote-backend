DROP TABLE notifications;

CREATE TABLE IF NOT EXISTS `notifications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `chilivote_id` int NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `seen` tinyint(1) DEFAULT 0,

  PRIMARY KEY (`id`),
  KEY `FKkwquj56nugetxobeygytlr000` (`chilivote_id`),
  KEY `FK68tbcw6bunvfjaoscaj851111` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8