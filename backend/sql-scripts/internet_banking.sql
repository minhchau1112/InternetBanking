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
INSERT INTO `accounts` VALUES (1,'074311598537',0.00,'2024-12-29 13:24:50.000000',_binary '','CHECKING',12),(2,'439030014096',16952591.22,'2024-12-31 16:52:47.000000',_binary '','CHECKING',14);
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `interbank_transactions`
--

LOCK TABLES `interbank_transactions` WRITE;
/*!40000 ALTER TABLE `interbank_transactions` DISABLE KEYS */;
INSERT INTO `interbank_transactions` VALUES (1,6286.80,NULL,'2025-01-05 00:00:34.000000','','1234567890','BANK001',82.38,'sender',_binary '',NULL,NULL,'PENDING',2,NULL),(2,6286.80,NULL,'2025-01-06 09:05:00.000000','','1234567890','BANK001',82.38,'sender',_binary '',NULL,NULL,'PENDING',2,NULL),(3,6286.80,NULL,'2025-01-06 09:16:12.000000','','1234567890','BANK001',82.38,'sender',_binary '',NULL,NULL,'PENDING',2,NULL),(4,6204.42,NULL,'2025-01-06 09:19:35.000000','','1234567890','BANK001',82.38,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(5,990000.00,NULL,'2025-01-06 16:37:13.000000','','1234567890','BANK3',10000.00,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(6,990000.00,NULL,'2025-01-06 16:42:52.000000','','1234567890','BANK3',10000.00,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(7,990000.00,NULL,'2025-01-06 16:43:26.000000','','1234567890','BANK3',10000.00,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(8,990000.00,NULL,'2025-01-06 16:44:05.000000','','1234567890','BANK3',10000.00,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(9,990000.00,NULL,'2025-01-06 16:57:25.000000','','1234567890','BANK3',10000.00,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(10,990000.00,NULL,'2025-01-06 19:28:25.000000','','1234567890','BANK3',10000.00,'RECEIVER',_binary '',NULL,NULL,'PENDING',2,NULL),(11,1000000.00,NULL,'2025-01-07 13:38:03.000000','this is the description','1234567890','MyBank',1000.00,'sender',_binary '',NULL,NULL,'PENDING',2,NULL);
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
INSERT INTO `linked_banks` VALUES (1,'WNC','2024-12-28 16:52:02.024572','le lam','-----BEGIN RSA PRIVATE KEY-----\nMIICWwIBAAKBgQDZmjCPLjynjYv5OReKswNxdszzvvdVnPwqp26U4Z3EpIgor/pi\n8tBdwpGnZmQ8dafanIsCwxA46Lva1LANLhmRnY7Qb0qH79r8EPZnCzW2353j+G1u\nOJM47JVg3n3qrwNwQvlKjFxuHOr9h4wqkOxMQlozD7eACpVqe7MGH5XYnwIDAQAB\nAoGAHKadoCyRVepgNLdcryl02Zqvz6QkQsSBu0gCWl3+fObA+sJ48FBSORYmQWlM\n510CddlIwFtYpnWWTnDUoVehi/lcgHKNlKvIccWzt4voOOhFpJtu2MV+cWyT8iK9\nzRGQJJOvLVDlUorcQg2v7TtggZCUKqV1ogkTY4lFiutJ8ckCQQD/6/CgAC6YIC00\nmspmGwOfcHUUI8wvEbAKRkQIImll3wBTYxiCYgGlvEKnxtggy23814IUKUkWQpp8\nRFcPEf0VAkEA2as/AsM1FA4RZoEiP5bdIFuRztqzUrgBqM15f3aoBUHNx2yhpUSj\ndqeJxrPSFns1C9C9VdrUtGCtvfQ9pqpz4wJAEcyJCbe5hd8HBdeWKA/M6rPZkLME\nqmligvRsGty509n0ndgQhlDmqG41fJG5yl4bojAi3eAZMzEiqW3fWC76GQJAEYUa\nFkPWEt4rAmAkfI/NDpywOMEbq+3JnGelft/zGDE6ufGBdUb4DUXg7hBtOYgDNgdA\nJhfuBPK/KUnuaGrR1wJAK9Y999oYdYlaUYDs+lkHNOICbQfUIJNVNEC7d9zU8fI9\nOCuE0rlvscB7pZYCeNTN9EvqLhLh63698jInpfP47w==\n-----END RSA PRIVATE KEY-----','-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZmjCPLjynjYv5OReKswNxdszz\nvvdVnPwqp26U4Z3EpIgor/pi8tBdwpGnZmQ8dafanIsCwxA46Lva1LANLhmRnY7Q\nb0qH79r8EPZnCzW2353j+G1uOJM47JVg3n3qrwNwQvlKjFxuHOr9h4wqkOxMQloz\nD7eACpVqe7MGH5XYnwIDAQAB\n-----END PUBLIC KEY-----','-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHHtLo6QckEEpDqX7JBcXeNGJa\nIJE0wV7X+GGduVd0tBOeXu8EZuFjOV2YtAKHkeX7Abm5DJFT/JBR7uvIaGGYIwIY\nC/b5zUOnJGknODY2vzXc3AXKYQ1y6avPMOBwXhfsQA6jEb0TBJCR3pwqt5QvbNgM\nYNQUWTQkBqG/ADfqRQIDAQAB\n-----END PUBLIC KEY-----','RSA'),(2,'MyBank','2024-12-28 16:52:02.024572','Hoang Nhan','-----BEGIN RSA PRIVATE KEY-----\nMIICWwIBAAKBgQDZmjCPLjynjYv5OReKswNxdszzvvdVnPwqp26U4Z3EpIgor/pi\n8tBdwpGnZmQ8dafanIsCwxA46Lva1LANLhmRnY7Qb0qH79r8EPZnCzW2353j+G1u\nOJM47JVg3n3qrwNwQvlKjFxuHOr9h4wqkOxMQlozD7eACpVqe7MGH5XYnwIDAQAB\nAoGAHKadoCyRVepgNLdcryl02Zqvz6QkQsSBu0gCWl3+fObA+sJ48FBSORYmQWlM\n510CddlIwFtYpnWWTnDUoVehi/lcgHKNlKvIccWzt4voOOhFpJtu2MV+cWyT8iK9\nzRGQJJOvLVDlUorcQg2v7TtggZCUKqV1ogkTY4lFiutJ8ckCQQD/6/CgAC6YIC00\nmspmGwOfcHUUI8wvEbAKRkQIImll3wBTYxiCYgGlvEKnxtggy23814IUKUkWQpp8\nRFcPEf0VAkEA2as/AsM1FA4RZoEiP5bdIFuRztqzUrgBqM15f3aoBUHNx2yhpUSj\ndqeJxrPSFns1C9C9VdrUtGCtvfQ9pqpz4wJAEcyJCbe5hd8HBdeWKA/M6rPZkLME\nqmligvRsGty509n0ndgQhlDmqG41fJG5yl4bojAi3eAZMzEiqW3fWC76GQJAEYUa\nFkPWEt4rAmAkfI/NDpywOMEbq+3JnGelft/zGDE6ufGBdUb4DUXg7hBtOYgDNgdA\nJhfuBPK/KUnuaGrR1wJAK9Y999oYdYlaUYDs+lkHNOICbQfUIJNVNEC7d9zU8fI9\nOCuE0rlvscB7pZYCeNTN9EvqLhLh63698jInpfP47w==\n-----END RSA PRIVATE KEY-----','-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZmjCPLjynjYv5OReKswNxdszz\nvvdVnPwqp26U4Z3EpIgor/pi8tBdwpGnZmQ8dafanIsCwxA46Lva1LANLhmRnY7Q\nb0qH79r8EPZnCzW2353j+G1uOJM47JVg3n3qrwNwQvlKjFxuHOr9h4wqkOxMQloz\nD7eACpVqe7MGH5XYnwIDAQAB\n-----END PUBLIC KEY-----','-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDSSPoLNzgQurpAYGmV2vr2tg1/\nrnHlA4WCR1LWbxSpotBVKrMHABy3QcoPaUeZJRK/qGTPCgcLS+Zo2PI9aMWm9Vru\nTua0bq5dNBUoFI86bsx5VIEzqUlu9KOjne2wIFzWmdv+XP+Ixvook+NpnssHqlrP\nt3bGMwK559qSayYOWwIDAQAB\n-----END PUBLIC KEY-----','RSA');
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

-- Dump completed on 2025-01-07 13:40:54
