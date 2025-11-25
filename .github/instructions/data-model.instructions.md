# Data Model Definition

 The project uses two Mysql relation databases to store the data:
  - `Feature` which store role assignments and user information
  
  
 Some of the tables can be found int both databases     

## Feature Database

The model for the Feature database is designed to manage user roles, contexts, and languages and is defined below.
```sql
    -- Table: Language
    CREATE TABLE Language (
            IdLanguage CHAR(36) PRIMARY KEY,
            Name VARCHAR(255) NOT NULL,
            Code CHAR(2)
        );

    -- Table: Country
        CREATE TABLE Country (
            IdCountry CHAR(36) PRIMARY KEY,
            Name VARCHAR(255) NOT NULL,
            Code CHAR(2) NOT NULL,
            UNIQUE (Code)
        );

    -- Table: Context
        CREATE TABLE Context (
            IdContext CHAR(36) PRIMARY KEY,
            IdContextParent CHAR(36),
            IdCountry CHAR(36) NOT NULL,
            Name VARCHAR(255) NOT NULL,
            OrganizationNumber VARCHAR(255) NOT NULL,
            FOREIGN KEY (IdContextParent) REFERENCES Context(IdContext),
            FOREIGN KEY (IdCountry) REFERENCES Country(IdCountry)
        );

        CREATE INDEX IX_Context_IdContextParent ON Context (IdContextParent);
        CREATE INDEX IX_Context_IdCountry ON Context (IdCountry);

    -- Table: Customer
        CREATE TABLE Customer (
            IdContext CHAR(36) PRIMARY KEY,
            FOREIGN KEY (IdContext) REFERENCES Context(IdContext)
        );

        CREATE INDEX IX_Customer_IdContext ON Customer (IdContext);

    -- Table: Role
        CREATE TABLE Role (
            IdRole CHAR(36) PRIMARY KEY,
            Name VARCHAR(255) NOT NULL
        );

    -- Table: User
        CREATE TABLE User (
            IdUser CHAR(36) PRIMARY KEY,
            IdLanguage CHAR(36) NOT NULL,
            Email VARCHAR(255) NOT NULL,
            FirstName VARCHAR(50) NOT NULL,
            LastName VARCHAR(50) NOT NULL,
            FOREIGN KEY (IdLanguage) REFERENCES Language(IdLanguage)
        );

        CREATE INDEX IX_User_IdLanguage ON User (IdLanguage);

    -- Table: UserRoleAssignment
        CREATE TABLE UserRoleAssignment (
            IdUserRoleAssignment CHAR(36) PRIMARY KEY,
            IdUser CHAR(36) NOT NULL,
            IdContext CHAR(36) NOT NULL,
            IdRole CHAR(36) NOT NULL,
            FOREIGN KEY (IdUser) REFERENCES User(IdUser),
            FOREIGN KEY (IdContext) REFERENCES Context(IdContext),
            FOREIGN KEY (IdRole) REFERENCES Role(IdRole),
            UNIQUE (IdUser, IdContext, IdRole)
        );

        CREATE INDEX IX_UserRoleAssignment_IdUser ON UserRoleAssignment (IdUser);
        CREATE INDEX IX_UserRoleAssignment_IdContext ON UserRoleAssignment (IdContext);
        CREATE INDEX IX_UserRoleAssignment_IdRole ON UserRoleAssignment (IdRole);
```
## Feature Database

 The model for the Feature database is designed to manage features, packages, and related entities and is defined below.
```sql
-- --------------------------------------------------------
-- Host:                         subscriptiondb-instance-1.c7244oq0k56a.eu-north-1.rds.amazonaws.com
-- Server version:               8.0.39 - 8bc99e28
-- Server OS:                    Linux
-- HeidiSQL Version:             9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for useraccess
DROP DATABASE IF EXISTS `useraccess`;
CREATE DATABASE IF NOT EXISTS `useraccess` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `useraccess`;

-- Dumping structure for table useraccess.Context
DROP TABLE IF EXISTS `Context`;
CREATE TABLE IF NOT EXISTS `Context` (
  `IdContext` char(36) NOT NULL,
  `IdContextParent` char(36) DEFAULT NULL,
  `IdCountry` char(36) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `OrganizationNumber` varchar(255) NOT NULL,
  PRIMARY KEY (`IdContext`),
  KEY `IX_Context_IdContextParent` (`IdContextParent`),
  KEY `IX_Context_IdCountry` (`IdCountry`),
  CONSTRAINT `Context_ibfk_1` FOREIGN KEY (`IdContextParent`) REFERENCES `Context` (`IdContext`),
  CONSTRAINT `Context_ibfk_2` FOREIGN KEY (`IdCountry`) REFERENCES `Country` (`IdCountry`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.
-- Dumping structure for table useraccess.Country
DROP TABLE IF EXISTS `Country`;
CREATE TABLE IF NOT EXISTS `Country` (
  `IdCountry` char(36) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Code` char(2) NOT NULL,
  PRIMARY KEY (`IdCountry`),
  UNIQUE KEY `Code` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.
-- Dumping structure for table useraccess.Customer
DROP TABLE IF EXISTS `Customer`;
CREATE TABLE IF NOT EXISTS `Customer` (
  `IdContext` char(36) NOT NULL,
  PRIMARY KEY (`IdContext`),
  KEY `IX_Customer_IdContext` (`IdContext`),
  CONSTRAINT `Customer_ibfk_1` FOREIGN KEY (`IdContext`) REFERENCES `Context` (`IdContext`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.
-- Dumping structure for table useraccess.Language
DROP TABLE IF EXISTS `Language`;
CREATE TABLE IF NOT EXISTS `Language` (
  `IdLanguage` char(36) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Code` char(2) DEFAULT NULL,
  PRIMARY KEY (`IdLanguage`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.
-- Dumping structure for table useraccess.Role
DROP TABLE IF EXISTS `Role`;
CREATE TABLE IF NOT EXISTS `Role` (
  `IdRole` char(36) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `InvariantKey` varchar(50) NOT NULL,
  `Description` text,
  `WhenEdited` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `RecordVersion` bigint NOT NULL,
  PRIMARY KEY (`IdRole`),
  KEY `Role_Name` (`Name`),
  KEY `Role_InvariantKey` (`InvariantKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.
-- Dumping structure for table useraccess.User
DROP TABLE IF EXISTS `User`;
CREATE TABLE IF NOT EXISTS `User` (
  `IdUser` char(36) NOT NULL,
  `IdLanguage` char(36) NOT NULL,
  `Email` varchar(255) NOT NULL,
  `FirstName` varchar(50) NOT NULL,
  `LastName` varchar(50) NOT NULL,
  `RecordVersion` bigint NOT NULL,
  `WhenEdited` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`IdUser`),
  KEY `IX_User_IdLanguage` (`IdLanguage`),
  CONSTRAINT `User_ibfk_1` FOREIGN KEY (`IdLanguage`) REFERENCES `Language` (`IdLanguage`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.
-- Dumping structure for table useraccess.UserRoleAssignment
DROP TABLE IF EXISTS `UserRoleAssignment`;
CREATE TABLE IF NOT EXISTS `UserRoleAssignment` (
  `IdUserRoleAssignment` char(36) NOT NULL,
  `IdUser` char(36) NOT NULL,
  `IdContext` char(36) NOT NULL,
  `IdRole` char(36) NOT NULL,
  PRIMARY KEY (`IdUserRoleAssignment`),
  UNIQUE KEY `IdUser` (`IdUser`,`IdContext`,`IdRole`),
  KEY `IX_UserRoleAssignment_IdUser` (`IdUser`),
  KEY `IX_UserRoleAssignment_IdContext` (`IdContext`),
  KEY `IX_UserRoleAssignment_IdRole` (`IdRole`),
  CONSTRAINT `UserRoleAssignment_ibfk_1` FOREIGN KEY (`IdUser`) REFERENCES `User` (`IdUser`),
  CONSTRAINT `UserRoleAssignment_ibfk_2` FOREIGN KEY (`IdContext`) REFERENCES `Context` (`IdContext`),
  CONSTRAINT `UserRoleAssignment_ibfk_3` FOREIGN KEY (`IdRole`) REFERENCES `Role` (`IdRole`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



);
```
## Common Tables
 Here is the list of tables that are common to both databases:
 - `Country`
 - `Language`
 - `Context`
 - `Customer`
 
 