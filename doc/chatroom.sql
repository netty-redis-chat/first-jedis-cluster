DROP TABLE IF EXISTS `chatroom`;
CREATE TABLE `chatroom` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) not null default '',
  `capacity` int not null default 0,
  PRIMARY KEY (`id`)
);