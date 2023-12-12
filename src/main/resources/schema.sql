
-- db.rooms definition

CREATE TABLE `rooms` (
  `capacity` int(11) NOT NULL,
  `price` double NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `room_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;


-- db.`user` definition

CREATE TABLE `user` (
  `inactive` tinyint(1) DEFAULT '0',
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(31) NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `surname` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `zipcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;


-- db.credit_cards definition

CREATE TABLE `credit_cards` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `card_name` varchar(255) DEFAULT NULL,
  `card_number` varchar(255) DEFAULT NULL,
  `expiration_date` varchar(255) DEFAULT NULL,
  `last_four_digits` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoetdi0nvya1n653aoqb2kpln7` (`user_id`),
  CONSTRAINT `FKoetdi0nvya1n653aoqb2kpln7` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- db.reservations definition

CREATE TABLE `reservations` (
  `check_in_date` date DEFAULT NULL,
  `check_out_date` date DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `room_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `credit_card_expiration` varchar(255) DEFAULT NULL,
  `credit_card_last_four_digits` varchar(255) DEFAULT NULL,
  `credit_card_number` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt0fnqb20ygbx0e030kdjlm28s` (`user_id`),
  CONSTRAINT `FKt0fnqb20ygbx0e030kdjlm28s` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;


-- db.reservation_rooms definition

CREATE TABLE `reservation_rooms` (
  `reservation_id` bigint(20) NOT NULL,
  `room_id` bigint(20) NOT NULL,
  PRIMARY KEY (`reservation_id`,`room_id`),
  KEY `FK81imhuuxynjj2ivy82koy38nm` (`room_id`),
  CONSTRAINT `FK81imhuuxynjj2ivy82koy38nm` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`),
  CONSTRAINT `FK96vqam6q3l5mic9bf9unp1q9j` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;