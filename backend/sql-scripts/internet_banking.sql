CREATE DATABASE  IF NOT EXISTS `internet_banking` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `internet_banking`;
-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: internet_banking
-- ------------------------------------------------------
-- Server version	5.5.5-10.1.29-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_number` varchar(255) NOT NULL,
  `balance` decimal(38,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_primary` bit(1) NOT NULL,
  `type` enum('CHECKING','SAVINGS') NOT NULL,
  `customer_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6kplolsdtr3slnvx97xsy2kc8` (`account_number`),
  KEY `FKn6x8pdp50os8bq5rbb792upse` (`customer_id`),
  CONSTRAINT `FKn6x8pdp50os8bq5rbb792upse` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'074311598537',0.00,'2024-12-29 13:24:50.000000',_binary '','CHECKING',12),(2,'439030014096',15952591.22,'2024-12-31 16:52:47.000000',_binary '','CHECKING',14);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKanhsicqm3lc8ya77tr7r0je18` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
/*!40000 ALTER TABLE `admins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKrfbvkrffamfql7cjmen8v976v` (`email`),
  CONSTRAINT `FKpog72rpahj62h7nod9wwc28if` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES ('quockhanh2003.qk@gmail.com','Quoc Khanh','0706512668',12),('quockhanh2003.qk1@gmail.com','Quoc Khanh','0706512669',14);
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debt_reminders`
--

DROP TABLE IF EXISTS `debt_reminders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debt_reminders` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `message` varchar(255) NOT NULL,
  `status` enum('CANCELLED','PAID','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `creator_account_id` int(11) NOT NULL,
  `debtor_account_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3xcqxgjkqpr0okdt5g7m35upb` (`creator_account_id`),
  KEY `FK36dm5jhs8lgqc59x33i9qw9os` (`debtor_account_id`),
  CONSTRAINT `FK36dm5jhs8lgqc59x33i9qw9os` FOREIGN KEY (`debtor_account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `FK3xcqxgjkqpr0okdt5g7m35upb` FOREIGN KEY (`creator_account_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debt_reminders`
--

LOCK TABLES `debt_reminders` WRITE;
/*!40000 ALTER TABLE `debt_reminders` DISABLE KEYS */;
/*!40000 ALTER TABLE `debt_reminders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employees` (
  `name` varchar(255) NOT NULL,
  `status` enum('ACTIVE','LOCKED') NOT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKd6th9xowehhf1kmmq1dsseq28` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES ('Qu?c Khánh','ACTIVE',1);
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interbank_transactions`
--

DROP TABLE IF EXISTS `interbank_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interbank_transactions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) NOT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `external_account_number` varchar(255) DEFAULT NULL,
  `external_bank_code` varchar(255) DEFAULT NULL,
  `fee_amount` decimal(38,2) DEFAULT NULL,
  `fee_payer` varchar(255) DEFAULT NULL,
  `is_incoming` bit(1) NOT NULL,
  `request_signature` varchar(255) DEFAULT NULL,
  `response_signature` varchar(255) DEFAULT NULL,
  `status` enum('COMPLETED','FAILED','PENDING') NOT NULL,
  `destination_account_id` int(11) DEFAULT NULL,
  `source_account_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8u72i44exi5uqo9ud0a6co2j2` (`destination_account_id`),
  KEY `FKlk4gr20maafxowfep7g90rwqr` (`source_account_id`),
  CONSTRAINT `FK8u72i44exi5uqo9ud0a6co2j2` FOREIGN KEY (`destination_account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `FKlk4gr20maafxowfep7g90rwqr` FOREIGN KEY (`source_account_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `interbank_transactions`
--

LOCK TABLES `interbank_transactions` WRITE;
/*!40000 ALTER TABLE `interbank_transactions` DISABLE KEYS */;
INSERT INTO `interbank_transactions` VALUES (1,6286.80,NULL,'2025-01-05 00:00:34.000000','','1234567890','BANK001',82.38,'sender',_binary '',NULL,NULL,'PENDING',2,NULL),(2,6286.80,NULL,'2025-01-06 09:05:00.000000','','1234567890','BANK001',82.38,'sender',_binary '',NULL,NULL,'PENDING',2,NULL),(3,6286.80,NULL,'2025-01-06 09:16:12.000000','','1234567890','BANK001',82.38,'sender',_binary '',NULL,NULL,'PENDING',2,NULL),(4,6204.42,NULL,'2025-01-06 09:19:35.000000','','1234567890','BANK001',82.38,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(5,990000.00,NULL,'2025-01-06 16:37:13.000000','','1234567890','BANK3',10000.00,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(6,990000.00,NULL,'2025-01-06 16:42:52.000000','','1234567890','BANK3',10000.00,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(7,990000.00,NULL,'2025-01-06 16:43:26.000000','','1234567890','BANK3',10000.00,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(8,990000.00,NULL,'2025-01-06 16:44:05.000000','','1234567890','BANK3',10000.00,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(9,990000.00,NULL,'2025-01-06 16:57:25.000000','','1234567890','BANK3',10000.00,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(10,990000.00,NULL,'2025-01-06 19:28:25.000000','','1234567890','BANK3',10000.00,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL);
/*!40000 ALTER TABLE `interbank_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `linked_banks`
--

DROP TABLE IF EXISTS `linked_banks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `linked_banks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bank_code` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(255) NOT NULL,
  `private_key` longtext NOT NULL,
  `public_key` longtext NOT NULL,
  `their_public_key` longtext NOT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `linked_banks`
--

LOCK TABLES `linked_banks` WRITE;
/*!40000 ALTER TABLE `linked_banks` DISABLE KEYS */;
INSERT INTO `linked_banks` VALUES (1,'WNC','2024-12-28 16:52:02.024572','le lam','-----BEGIN RSA PRIVATE KEY-----\nMIICWwIBAAKBgQDZmjCPLjynjYv5OReKswNxdszzvvdVnPwqp26U4Z3EpIgor/pi\n8tBdwpGnZmQ8dafanIsCwxA46Lva1LANLhmRnY7Qb0qH79r8EPZnCzW2353j+G1u\nOJM47JVg3n3qrwNwQvlKjFxuHOr9h4wqkOxMQlozD7eACpVqe7MGH5XYnwIDAQAB\nAoGAHKadoCyRVepgNLdcryl02Zqvz6QkQsSBu0gCWl3+fObA+sJ48FBSORYmQWlM\n510CddlIwFtYpnWWTnDUoVehi/lcgHKNlKvIccWzt4voOOhFpJtu2MV+cWyT8iK9\nzRGQJJOvLVDlUorcQg2v7TtggZCUKqV1ogkTY4lFiutJ8ckCQQD/6/CgAC6YIC00\nmspmGwOfcHUUI8wvEbAKRkQIImll3wBTYxiCYgGlvEKnxtggy23814IUKUkWQpp8\nRFcPEf0VAkEA2as/AsM1FA4RZoEiP5bdIFuRztqzUrgBqM15f3aoBUHNx2yhpUSj\ndqeJxrPSFns1C9C9VdrUtGCtvfQ9pqpz4wJAEcyJCbe5hd8HBdeWKA/M6rPZkLME\nqmligvRsGty509n0ndgQhlDmqG41fJG5yl4bojAi3eAZMzEiqW3fWC76GQJAEYUa\nFkPWEt4rAmAkfI/NDpywOMEbq+3JnGelft/zGDE6ufGBdUb4DUXg7hBtOYgDNgdA\nJhfuBPK/KUnuaGrR1wJAK9Y999oYdYlaUYDs+lkHNOICbQfUIJNVNEC7d9zU8fI9\nOCuE0rlvscB7pZYCeNTN9EvqLhLh63698jInpfP47w==\n-----END RSA PRIVATE KEY-----','-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZmjCPLjynjYv5OReKswNxdszz\nvvdVnPwqp26U4Z3EpIgor/pi8tBdwpGnZmQ8dafanIsCwxA46Lva1LANLhmRnY7Q\nb0qH79r8EPZnCzW2353j+G1uOJM47JVg3n3qrwNwQvlKjFxuHOr9h4wqkOxMQloz\nD7eACpVqe7MGH5XYnwIDAQAB\n-----END PUBLIC KEY-----','-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHHtLo6QckEEpDqX7JBcXeNGJa\nIJE0wV7X+GGduVd0tBOeXu8EZuFjOV2YtAKHkeX7Abm5DJFT/JBR7uvIaGGYIwIY\nC/b5zUOnJGknODY2vzXc3AXKYQ1y6avPMOBwXhfsQA6jEb0TBJCR3pwqt5QvbNgM\nYNQUWTQkBqG/ADfqRQIDAQAB\n-----END PUBLIC KEY-----','RSA'),(2,'BANK3','2024-12-28 16:52:02.024572','lmao','-----BEGIN PGP PRIVATE KEY BLOCK-----\nVersion: Keybase OpenPGP v1.0.0\nComment: https://keybase.io/crypto\n\n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\n0QDIOA==\n=EbT9\n-----END PGP PRIVATE KEY BLOCK-----\n','-----BEGIN PGP PUBLIC KEY BLOCK-----\nVersion: Keybase OpenPGP v1.0.0\nComment: https://keybase.io/crypto\n\n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\n5duikGYTeV4j8dmQILYgHLMXXyKygtEAyDg=\n=E10s\n-----END PGP PUBLIC KEY BLOCK-----\n','-----BEGIN PGP PUBLIC KEY BLOCK-----\nVersion: Keybase OpenPGP v1.0.0\nComment: https://keybase.io/crypto\n\n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\n5duikGYTeV4j8dmQILYgHLMXXyKygtEAyDg=\n=E10s\n-----END PGP PUBLIC KEY BLOCK-----\n','PGP');
/*!40000 ALTER TABLE `linked_banks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recipients`
--

DROP TABLE IF EXISTS `recipients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recipients` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_number` varchar(255) NOT NULL,
  `alias_name` varchar(255) NOT NULL,
  `bank_code` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `customer_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK87h7a6stapx3cjqo59rp0ej2w` (`customer_id`),
  CONSTRAINT `FK87h7a6stapx3cjqo59rp0ej2w` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipients`
--

LOCK TABLES `recipients` WRITE;
/*!40000 ALTER TABLE `recipients` DISABLE KEYS */;
/*!40000 ALTER TABLE `recipients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) NOT NULL,
  `bank_code` varchar(255) DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `fee` decimal(38,2) NOT NULL,
  `fee_payer` enum('RECEIVER','SENDER') NOT NULL,
  `message` varchar(255) NOT NULL,
  `otp_verified` bit(1) NOT NULL,
  `status` varchar(255) NOT NULL,
  `type` enum('DEBT_REMINDER','DEPOSIT','RECEIVE','TRANSFER') NOT NULL,
  `destination_account_id` int(11) DEFAULT NULL,
  `source_account_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5598b948ilps8u4o3qvfo4j52` (`destination_account_id`),
  KEY `FKr5dnv4t6f45ltol1wu5u27cqu` (`source_account_id`),
  CONSTRAINT `FK5598b948ilps8u4o3qvfo4j52` FOREIGN KEY (`destination_account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `FKr5dnv4t6f45ltol1wu5u27cqu` FOREIGN KEY (`source_account_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (1,10000000.00,NULL,'2024-12-31 16:53:00.000000','2024-12-31 16:53:00.000000',0.00,'SENDER','Deposit',_binary '','COMPLETED','DEPOSIT',2,2),(2,100.00,NULL,'2024-12-31 16:55:17.000000','2024-12-31 16:55:17.000000',0.00,'SENDER','Deposit',_binary '','COMPLETED','DEPOSIT',2,2);
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `role` varchar(31) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('EMPLOYEE',1,'2024-12-28 16:52:02.024572','$2a$12$5GiOs84RLQIfoPNCIJvN9elAWslbyBkXwQJjpL6HLdbxL.xrXcm9i','employee1'),('CUSTOMER',12,'2024-12-29 13:24:50.000000','$2a$10$RsPrW7muDkhu0n8Yk5BN4.GXoEFsju0Gh4.c6atUfwpH2bBn7yoAS','khanh.nguyenle'),('CUSTOMER',14,'2024-12-31 16:52:47.000000','$2a$10$RsPrW7muDkhu0n8Yk5BN4.GXoEFsju0Gh4.c6atUfwpH2bBn7yoAS','BaOfBu');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-06 20:18:25
