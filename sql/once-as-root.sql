DROP DATABASE IF EXISTS accounts;
CREATE DATABASE IF NOT EXISTS accounts;
USE `accounts`;
Drop User 'accounts'@'localhost';
Drop User 'accounts'@'%';
Create User 'accounts'@'localhost' Identified BY '';
Create User 'accounts'@'%' Identified BY '';
Grant All on accounts.* to 'accounts'@'localhost';
Grant All on accounts.* to 'accounts'@'%';
