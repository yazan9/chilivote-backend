CREATE TABLE IF NOT EXISTS `chilivote` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6ee2lmlk0djw2c49ywg34ex01` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8