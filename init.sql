CREATE DATABASE store;
\c store;

CREATE TABLE Customers
(
    CustomerId BIGSERIAL PRIMARY KEY,
    CustomerName CHARACTER VARYING(255) NOT NULL,
    CustomerSurname CHARACTER VARYING(255) NOT NULL
);

CREATE TABLE Products
(
    ProductId BIGSERIAL PRIMARY KEY,
    ProductName CHARACTER VARYING(255) NOT NULL,
    Price NUMERIC(19, 2) NOT NULL
);

CREATE TABLE Purchases
(
    PurchaseId BIGSERIAL PRIMARY KEY,
    CustomerId BIGSERIAL REFERENCES Customers(CustomerId),
    ProductId BIGSERIAL REFERENCES Products(ProductId),
    PurchaseDate date NOT NULL CHECK (PurchaseDate <= CURRENT_TIMESTAMP)
);

INSERT INTO Customers (CustomerId, CustomerName, CustomerSurname)
VALUES
(1, 'Антон', 'Иванов'),
(2, 'Николай', 'Иванов'),
(3, 'Валентин', 'Петров'),
(4, 'Иван', 'Николаев'),
(5, 'Василий', 'Антонов');

INSERT INTO Products (ProductId, ProductName, Price)
VALUES
(1, 'Минеральная вода', 50),
(2, 'Сыр', 500),
(3, 'Сметана', 100),
(4, 'Хлеб', 30),
(5, 'Колбаса', 345);

INSERT INTO Purchases (PurchaseId, CustomerId, ProductId, PurchaseDate)
VALUES
(1, 1, 1, '2024-01-10'),
(2, 1, 2, '2024-01-11'),
(3, 3, 3, '2024-01-12'),
(4, 3, 3, '2024-01-12'),
(5, 1, 4, '2024-01-12'),
(6, 3, 5, '2024-01-12'),
(7, 2, 1, '2024-02-02'),
(8, 4, 2, '2024-03-04'),
(9, 5, 4, '2024-04-24'),
(10, 5, 2, '2024-05-27'),
(11, 1, 1, '2024-05-27'),
(12, 1, 1, '2024-05-27');


