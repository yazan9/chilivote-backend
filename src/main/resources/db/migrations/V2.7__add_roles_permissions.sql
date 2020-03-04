 CREATE TABLE `permissions_roles` (
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `FKff6bcp6bbaup2irutar3dfaks` (`permission_id`),
  CONSTRAINT `FK9j7vx1vojmoa6rs21eggd46xn` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKff6bcp6bbaup2irutar3dfaks` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8