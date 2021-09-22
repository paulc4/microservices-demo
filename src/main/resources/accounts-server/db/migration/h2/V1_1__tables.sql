CREATE TABLE ACCOUNT (
  id identity NOT NULL AUTO_INCREMENT,
  number varchar(25) NOT NULL,
  owner varchar(25) NOT NULL,
  balance decimal(10,0) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY `UQ_ACCOUNT_NUMBER` (number)
);
