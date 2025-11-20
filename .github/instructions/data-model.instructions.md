# Data Model Definition

 The project uses two Mysql relation databases to store the data:
  - `UserAccess` which store role assignments and user information
  - `Subscription` which store subscription information, packages, and related entities
  
 Some of the tables can be found int both databases     

## UserAccess Database

The model for the UserAccess database is designed to manage user roles, contexts, and languages and is defined below.
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
## Subscription Database

 The model for the Subscription database is designed to manage subscriptions, packages, and related entities and is defined below.
```sql
   CREATE DATABASE IF NOT EXISTS subscription;
USE subscription;

CREATE TABLE Country (
    IdCountry CHAR(36) NOT NULL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Code CHAR(2) NOT NULL UNIQUE,
	CurrencyCode char(3) NOT NULL,
	INDEX IdxName (Name),
	INDEX IdxCurrencyCode (CurrencyCode)
);

CREATE TABLE Context (
    IdContext CHAR(36) NOT NULL PRIMARY KEY,
    IdContextParent CHAR(36),
    IdCountry CHAR(36) NOT NULL,
    Name VARCHAR(255) NOT NULL,
    OrganizationNumber VARCHAR(255) NOT NULL,
	UserNameChangedBy VARCHAR(255),
    WhenEdited DATETIME DEFAULT '1900-01-01 00:00:00',
	FOREIGN KEY (IdContextParent) REFERENCES Context(IdContext),
    FOREIGN KEY (IdCountry) REFERENCES Country(IdCountry),
	INDEX IdxContextParent (IdContextParent),
	INDEX IdxCountry (IdCountry)
);


CREATE TABLE Distributor (
    IdDistributor CHAR(36) NOT NULL PRIMARY KEY,
    URISubscriptionSystem VARCHAR(255) NOT NULL,
    FOREIGN KEY (IdDistributor) REFERENCES Context(IdContext)
);


CREATE TABLE Suite (
    IdSuite CHAR(36) NOT NULL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
	UserNameChangedBy VARCHAR(255),
    WhenEdited DATETIME DEFAULT '1900-01-01 00:00:00'
);

CREATE TABLE Product (
    IdProduct CHAR(36) NOT NULL PRIMARY KEY,
    IdDistributor CHAR(36) NOT NULL,
    Name VARCHAR(255) NOT NULL,
    IsBaseNotAddOn TINYINT(1) NOT NULL DEFAULT 1,
	UserNameChangedBy VARCHAR(255),
    WhenEdited DATETIME DEFAULT '1900-01-01 00:00:00',
    FOREIGN KEY (IdDistributor) REFERENCES Distributor(IdDistributor),
	UNIQUE (IdProduct, IdDistributor),
	INDEX IdxBusinessUnit (IdDistributor)
);


CREATE TABLE SuiteProduct (
    IdSuiteProduct CHAR(36) NOT NULL PRIMARY KEY,
    IdSuite CHAR(36) NOT NULL,
    IdProduct CHAR(36) NOT NULL
	UserNameChangedBy VARCHAR(255),
    WhenEdited DATETIME DEFAULT '1900-01-01 00:00:00',
    FOREIGN KEY (IdSuite) REFERENCES Suite(IdSuite),
    FOREIGN KEY (IdProduct) REFERENCES Product(IdProduct),
	UNIQUE(IdSuite, IdProduct),
	INDEX IdxSuite (IdSuite),
	INDEX IdxProduct (IdProduct)
);


CREATE TABLE ProductDependency (
    IdProductDependency CHAR(36) NOT NULL PRIMARY KEY,
	IdDistributor CHAR(36) NOT NULL,
	IdProduct CHAR(36) NOT NULL,
	IdProductPrerequisite CHAR(36) NOT NULL,
	UserNameChangedBy VARCHAR(255),
    WhenEdited DATETIME DEFAULT '1900-01-01 00:00:00',
    FOREIGN KEY (IdProduct, IdDistributor) REFERENCES Product(IdProduct, IdDistributor),
	FOREIGN KEY (IdProductPrerequisite, IdDistributor) REFERENCES Product(IdProduct, IdDistributor),
	UNIQUE(IdProduct, IdProductPrerequisite),
	INDEX IdxProductBusinessUnit (IdProduct, IdDistributor),
	INDEX IdxProductPrerequisiteBusinessUnit (IdProductPrerequisite, IdDistributor)
);


CREATE TABLE Role (
    IdRole CHAR(36) NOT NULL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
	UserNameChangedBy VARCHAR(255),
    WhenEdited DATETIME DEFAULT '1900-01-01 00:00:00'
);


CREATE TABLE Feature (
    IdFeature CHAR(36) NOT NULL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    DisplayName VARCHAR(255) NOT NULL,
	UserNameChangedBy VARCHAR(255),
    WhenEdited DATETIME DEFAULT '1900-01-01 00:00:00'
);



CREATE TABLE FeatureRole (
    IdFeatureRole CHAR(36) NOT NULL PRIMARY KEY,
    IdFeature CHAR(36) NOT NULL,
    IdRole CHAR(36) NOT NULL,
	UserNameChangedBy VARCHAR(255),
    WhenEdited DATETIME DEFAULT '1900-01-01 00:00:00',
    FOREIGN KEY (IdFeature) REFERENCES Feature(IdFeature),
    FOREIGN KEY (IdRole) REFERENCES Role(IdRole),
	INDEX IdxFeature (IdFeature),
    INDEX IdxRole (IdRole)
);


CREATE TABLE ProductFeature (
    IdProductFeature CHAR(36) NOT NULL PRIMARY KEY,
    IdProduct CHAR(36) NOT NULL,
    IdFeature CHAR(36) NOT NULL,
	UserNameChangedBy VARCHAR(255),
    WhenEdited DATETIME DEFAULT '1900-01-01 00:00:00',
    FOREIGN KEY (IdProduct) REFERENCES Product(IdProduct),
    FOREIGN KEY (IdFeature) REFERENCES Feature(IdFeature),
	UNIQUE(IdProduct,IdFeature),
	INDEX IdxProduct (IdProduct),
	INDEX IdxFeature (IdFeature)
);


CREATE TABLE CountryFeature (
    IdCountryFeature CHAR(36) NOT NULL PRIMARY KEY,
    IdCountry CHAR(36) NOT NULL,
    IdFeature CHAR(36) NOT NULL,
	UserNameChangedBy VARCHAR(255),
    WhenEdited DATETIME DEFAULT '1900-01-01 00:00:00',
    FOREIGN KEY (IdCountry) REFERENCES Country(IdCountry),
    FOREIGN KEY (IdFeature) REFERENCES Feature(IdFeature),
	INDEX IdxCountry (IdCountry),
	INDEX IdxFeature (IdFeature)
);


CREATE TABLE Customer (
    IdCustomer CHAR(36) NOT NULL PRIMARY KEY,
    FOREIGN KEY (IdCustomer) REFERENCES Context(IdContext)
);

CREATE TABLE Partner (
    IdPartner CHAR(36) NOT NULL PRIMARY KEY,
    FOREIGN KEY (IdPartner) REFERENCES Context(IdContext)
);


CREATE TABLE Subscription (
    IdSubscription CHAR(36) NOT NULL PRIMARY KEY,
    IdDistributor CHAR(36) NOT NULL,
    IdCustomer CHAR(36) NOT NULL,
    IdPartner CHAR(36) NOT NULL,
	UserNameChangedBy VARCHAR(255),
    WhenEdited DATETIME DEFAULT '1900-01-01 00:00:00',
    FOREIGN KEY (IdDistributor) REFERENCES Distributor(IdDistributor),
    FOREIGN KEY (IdCustomer) REFERENCES Customer(IdCustomer),
    FOREIGN KEY (IdPartner) REFERENCES Partner(IdPartner),
	UNIQUE(IdSubscription, IdDistributor),
	INDEX IdxBusinessUnit (IdDistributor),
	INDEX IdxCustomer (IdCustomer),
	INDEX IdxPartner (IdPartner)
);


CREATE TABLE SubscriptionProduct (
    IdSubscriptionProduct CHAR(36) NOT NULL PRIMARY KEY,
    IdSubscription CHAR(36) NOT NULL,
	IdDistributor CHAR(36) NOT NULL,
    IdProduct CHAR(36) NOT NULL,
	UserNameChangedBy VARCHAR(255),
    WhenEdited DATETIME DEFAULT '1900-01-01 00:00:00',
    FOREIGN KEY (IdSubscription, IdDistributor) REFERENCES Subscription(IdSubscription, IdDistributor),
    FOREIGN KEY (IdProduct, IdDistributor) REFERENCES Product(IdProduct, IdDistributor),
    INDEX IdxSubscriptionBusinessUnit (IdSubscription, IdDistributor),
	INDEX IdxProductBusinessUnit (IdProduct, IdDistributor) 
);
```
## Common Tables
 Here is the list of tables that are common to both databases:
 - `Country`
 - `Language`
 - `Context`
 - `Customer`
 
 