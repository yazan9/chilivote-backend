CREATE TABLE IF NOT EXISTS `follow` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `from_user_fk` int DEFAULT NULL,
  `to_user_fk` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `from_user_fk` (`from_user_fk`,`to_user_fk`),
  KEY `FKt8g0cnwcx4suqqpvb1dagth67` (`to_user_fk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8