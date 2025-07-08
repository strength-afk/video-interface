-- MySQL dump 10.13  Distrib 9.3.0, for macos15.2 (arm64)
--
-- Host: localhost    Database: fb_video
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'root','2025-07-07 17:24:27',0,1),(2,'2','alter users email nullable','SQL','V2__alter_users_email_nullable.sql',-1899879582,'root','2025-07-07 17:24:27',35,1),(3,'3','alter users email nullable','SQL','V3__alter_users_email_nullable.sql',-378745019,'root','2025-07-07 19:26:06',4,1),(4,'3','alter users email nullable','DELETE','V3__alter_users_email_nullable.sql',-378745019,'root','2025-07-07 19:31:52',0,1),(5,'4','alter users email nullable again','SQL','V4__alter_users_email_nullable_again.sql',215115396,'root','2025-07-07 19:32:00',39,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_balance` decimal(10,2) DEFAULT NULL COMMENT '账户余额',
  `avatar` varchar(200) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱地址，唯一，不可为空',
  `is_locked` bit(1) DEFAULT NULL,
  `is_vip` bit(1) DEFAULT NULL,
  `last_login_ip` varchar(50) DEFAULT NULL,
  `last_login_time` datetime(6) DEFAULT NULL,
  `lock_reason` varchar(200) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL COMMENT '手机号码，最大长度20',
  `role` enum('ADMIN','USER','VIP') NOT NULL,
  `status` enum('ACTIVE','DELETED','INACTIVE','LOCKED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(255) NOT NULL COMMENT '用户名，唯一，不可为空',
  `vip_expire_time` datetime(6) DEFAULT NULL,
  `watch_time` bigint DEFAULT NULL COMMENT '观看时长（分钟）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,0.00,NULL,'2025-07-08 00:54:19.594058','test@example.com',_binary '\0',_binary '\0',NULL,NULL,NULL,'$2a$10$CtnJJ6703qIC4hlqJbTJJudH98LzmF2bFL5zE9dR5WNmHj2H4j/1G',NULL,'USER','ACTIVE','2025-07-08 00:54:19.594084','testuser',NULL,0),(2,0.00,'https://example.com/avatar.jpg','2025-07-08 01:11:55.382640','testuser1@example.com',_binary '\0',_binary '\0',NULL,'2025-07-08 01:12:03.038973',NULL,'$2a$10$ffPFjVgaWV0zfAZQyNL5vuoag.TDWSufbwSjY7SzoKRH2H7g9QFii','13800138002','USER','ACTIVE','2025-07-08 01:12:18.069862','testuser1',NULL,0),(3,0.00,NULL,'2025-07-08 01:24:41.454158',NULL,_binary '\0',_binary '\0',NULL,'2025-07-08 01:24:48.676579',NULL,'$2a$10$7PCi0O02j570r5cSAE0LvulLfJr1O/ITd.BCMSLWnmpM0gzfMIS9.',NULL,'USER','ACTIVE','2025-07-08 01:24:48.845055','testuser2',NULL,0),(4,0.00,NULL,'2025-07-08 01:26:00.788814',NULL,_binary '\0',_binary '\0',NULL,'2025-07-08 01:40:02.943029',NULL,'$2a$10$EgEwULOKUqSsgXfaEzWIM.FUViWFjNib3Cw4T6/wsfflGxd4a.Wm6',NULL,'USER','ACTIVE','2025-07-08 01:40:02.944245','as123',NULL,0),(5,0.00,NULL,'2025-07-08 03:03:11.150267','test123@example.com',_binary '\0',_binary '\0',NULL,NULL,NULL,'$2a$10$gY7rvT6qN08Ax5MLhAdXkOvIkpTVlwceb2D7rjepm0mJyC.uXK8Q6',NULL,'USER','ACTIVE','2025-07-08 03:03:11.150303','testUser123',NULL,0),(6,0.00,NULL,'2025-07-08 03:09:20.724635','test1751915360558@example.com',_binary '\0',_binary '\0',NULL,NULL,NULL,'$2a$10$F8RPblB4D0KM/CzDEmtemOcP8bAIVDPh0ti91y7h9y1kA2vbicqDq',NULL,'USER','ACTIVE','2025-07-08 03:09:20.724666','testUser1751915360558',NULL,0);
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

-- Dump completed on 2025-07-08 15:06:59
