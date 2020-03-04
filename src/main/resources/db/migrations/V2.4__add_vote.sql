CREATE TABLE IF NOT EXISTS `vote` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `answer_id` int DEFAULT NULL,
  `chilivote_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKp8nb2v4m56kax9qu8ube45tbi` (`chilivote_id`,`user_id`),
  KEY `FKa3aku0wi48fisg9cwprvmhy76` (`answer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8