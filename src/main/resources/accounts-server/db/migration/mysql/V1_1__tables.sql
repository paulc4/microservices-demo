DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `number` varchar(25) CHARACTER SET utf8 NOT NULL,
  `owner` varchar(25) CHARACTER SET utf8 NOT NULL,
  `balance` decimal(10,0) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_ACCOUNT_NUMBER` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
