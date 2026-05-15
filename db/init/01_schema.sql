BEGIN;

CREATE TABLE IF NOT EXISTS Categories (
    CategoryID int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    CategoryName varchar(200) NOT NULL,
    ParentCategoryID int NULL
);

ALTER TABLE Categories
ADD CONSTRAINT Categories_fk1 FOREIGN KEY
(ParentCategoryID) REFERENCES Categories (CategoryID);


CREATE TABLE IF NOT EXISTS Products (
    ProductID int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    CategoryID int NOT NULL,
    Name varchar(200) NOT NULL,
    Price decimal(10, 2) NOT NULL,
    Stock int NOT NULL,
    IsActive boolean NOT NULL DEFAULT TRUE,
    Attributes jsonb NOT NULL DEFAULT '{}'
);

ALTER TABLE Products
ADD CONSTRAINT Products_fk1 FOREIGN KEY
(CategoryID) REFERENCES Categories (CategoryID);


CREATE TABLE IF NOT EXISTS Customers (
    CustomerID int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    FirstName varchar(100) NOT NULL,
    LastName varchar(100) NOT NULL,
    Email varchar(200) NOT NULL UNIQUE,
    Phone varchar(20) NULL,
    PasswordHash varchar(200) NULL,
    CreatedAt timestamp NOT NULL DEFAULT now()
);


CREATE TABLE IF NOT EXISTS Addresses (
    AddressID int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    CustomerID int NOT NULL,
    Label varchar(100) NULL,
    Street varchar(100) NOT NULL,
    City varchar(100) NOT NULL,
    PostalCode varchar(20) NOT NULL,
    Country varchar(100) NOT NULL DEFAULT 'Poland',
    IsActive boolean NOT NULL DEFAULT TRUE,
    IsDefault boolean NOT NULL DEFAULT FALSE
);

ALTER TABLE Addresses
ADD CONSTRAINT Addresses_fk1 FOREIGN KEY
(CustomerID) REFERENCES Customers (CustomerID);


CREATE TABLE IF NOT EXISTS Orders (
    OrderID int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    CustomerID int NOT NULL,
    AddressID int NOT NULL,
    Status varchar(50) NOT NULL DEFAULT 'pending',
    OrderDate timestamp NOT NULL DEFAULT now(),
    ShipDate timestamp NULL
);

ALTER TABLE Orders
ADD CONSTRAINT Orders_fk1 FOREIGN KEY
(CustomerID) REFERENCES Customers (CustomerID);

ALTER TABLE Orders
ADD CONSTRAINT Orders_fk2 FOREIGN KEY
(AddressID) REFERENCES Addresses (AddressID);


CREATE TABLE IF NOT EXISTS OrderDetails (
    OrderID int NOT NULL,
    ProductID int NOT NULL,
    UnitPrice decimal(10, 2) NOT NULL,
    Quantity int NOT NULL
);

ALTER TABLE OrderDetails
ADD CONSTRAINT OrderDetails_fk1 FOREIGN KEY
(OrderID) REFERENCES Orders (OrderID);

ALTER TABLE OrderDetails
ADD CONSTRAINT OrderDetails_fk2 FOREIGN KEY
(ProductID) REFERENCES Products (ProductID);

ALTER TABLE OrderDetails
ADD CONSTRAINT OrderDetails_pk PRIMARY KEY
(OrderID, ProductID);


CREATE TABLE IF NOT EXISTS PaymentMethods (
    PaymentMethodID int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    Provider varchar(50) NOT NULL,
    Type varchar(50) NOT NULL,
    IsActive boolean NOT NULL DEFAULT TRUE
);


CREATE TABLE IF NOT EXISTS Payments (
    PaymentID int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    OrderID int NOT NULL,
    PaymentMethodID int NOT NULL,
    Amount decimal(10, 2) NOT NULL,
    Status varchar(50) NOT NULL DEFAULT 'pending',
    CreatedAt timestamp NOT NULL DEFAULT now(),
    PaidAt timestamp NULL
);

ALTER TABLE Payments
ADD CONSTRAINT Payments_fk1 FOREIGN KEY
(OrderID) REFERENCES Orders (OrderID);

ALTER TABLE Payments
ADD CONSTRAINT Payments_fk2 FOREIGN KEY
(PaymentMethodID) REFERENCES PaymentMethods (PaymentMethodID);

COMMIT;