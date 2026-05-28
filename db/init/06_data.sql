BEGIN;

-- add data
insert into customers(firstname, lastname, email)
values
('Bogdan','Kwolek','kolek@agh.pl'),
('Marcin','Kurdziel','marcin@kurdziel.pl'),
('Jakub','Przybylo','przy@by.lo'),
('Robert','Marcjan','yerbam@te.pl'),
('Hubert','Myszka','hubert.myszk@wa.pl'),
('Sebastian','Flajszer','podatnik@malpa.pl');


INSERT INTO Addresses (CustomerID, Street, City, PostalCode)
VALUES
    (1, 'Karmelicka 12', 'Krakow', '31-128'),
    (1, 'Dluga 45', 'Krakow', '31-147'),
    (2, 'Marszalkowska 87', 'Warsaw', '00-683'),
    (3, 'Piotrkowska 120', 'Lodz', '90-006'),
    (4, 'Grunwaldzka 55', 'Gdansk', '80-241'),
    (5, 'Swidnicka 22', 'Wroclaw', '50-068'),
    (6, 'Koscielna 9', 'Poznan', '60-538'),
    (6, 'Sienkiewicza 34', 'Rzeszow', '35-216');

INSERT INTO PaymentMethods (Provider, Type)
VALUES
    ('BLIK', 'Mobile payment'),
    ('Visa', 'Card'),
    ('MasterCard','Card'),
    ('PayPal', 'Online wallet');

INSERT INTO Categories (CategoryName, ParentCategoryID)
VALUES
    ('Kawa', NULL),
    ('Kawa ziarnista', 1),
    ('Kawa mielona', 1),
    ('Kapsułki kawowe', 1),
    ('Arabica', 2),
    ('Robusta', 2);

INSERT INTO Products (CategoryID, Name, Price, Stock, Attributes)
VALUES
    (5, 'Lavazza Qualita Oro 1kg', 49.99, 20, '{"origin": "Central America", "weight": "1kg", "roast": "medium"}'),
    (5, 'Dallmayr Prodomo Arabica 500g', 34.99, 15, '{"origin": "Ethiopia", "weight": "500g", "roast": "medium"}'),
    (6, 'Vietnam Robusta Coffee 1kg', 39.99, 12, '{"origin": "Vietnam", "weight": "1kg", "roast": "dark"}'),
    (3, 'Jacobs Kronung Ground Coffee 500g', 27.99, 30, '{"weight": "500g", "roast": "medium", "grind": "medium"}'),
    (4, 'Nespresso Espresso Capsules 10 pcs', 22.99, 40, '{"capsules": 10, "intensity": 8, "system": "Nespresso"}');

INSERT INTO Orders (CustomerID, AddressID, Status, OrderDate, ShipDate)
VALUES
    (1, 1, 'pending',   '2026-05-01 10:15:00', NULL),
    ( 1, 2, 'packed',    '2026-05-02 14:30:00', NULL),
    ( 1, 1, 'delivered', '2026-05-03 09:45:00', '2026-05-04 12:00:00'),

    ( 2, 3, 'cancelled', '2026-05-04 16:20:00', NULL),
    ( 2, 3, 'delivered', '2026-05-05 11:10:00', '2026-05-06 15:30:00'),
    ( 2, 3, 'pending',   '2026-05-07 08:25:00', NULL),

    ( 3, 4, 'packed',    '2026-05-08 13:00:00', NULL),
    ( 3, 4, 'delivered', '2026-05-09 17:40:00', '2026-05-10 10:15:00'),

    ( 4, 5, 'pending',   '2026-05-10 12:05:00', NULL),
    ( 4, 5, 'cancelled','2026-05-11 18:30:00', NULL),
    ( 4, 5, 'delivered','2026-05-12 09:10:00', '2026-05-13 14:45:00'),

    ( 5, 6, 'packed',   '2026-05-13 15:20:00', NULL),
    ( 5, 6, 'pending',  '2026-05-14 10:00:00', NULL),
    ( 5, 6, 'delivered','2026-05-15 11:35:00', '2026-05-16 16:10:00');

INSERT INTO OrderDetails (OrderID, ProductID, UnitPrice, Quantity)
VALUES
    -- Order 1
    (1, 1, 49.99, 1),
    (1, 4, 27.99, 2),

    -- Order 2
    (2, 2, 34.99, 1),
    (2, 5, 22.99, 3),

    -- Order 3
    (3, 1, 49.99, 2),
    (3, 3, 39.99, 1),

    -- Order 4
    (4, 4, 27.99, 1),

    -- Order 5
    (5, 2, 34.99, 2),
    (5, 5, 22.99, 4),

    -- Order 6
    (6, 3, 39.99, 1),
    (6, 4, 27.99, 1),

    -- Order 7
    (7, 1, 49.99, 1),
    (7, 2, 34.99, 1),
    (7, 5, 22.99, 2),

    -- Order 8
    (8, 4, 27.99, 3),

    -- Order 9
    (9, 3, 39.99, 2),
    (9, 5, 22.99, 1),

    -- Order 10
    (10, 1, 49.99, 1),

    -- Order 11
    (11, 2, 34.99, 1),
    (11, 3, 39.99, 2),
    (11, 4, 27.99, 1),

    -- Order 12
    (12, 5, 22.99, 5),

    -- Order 13
    (13, 1, 49.99, 1),
    (13, 4, 27.99, 2),

    -- Order 14
    (14, 2, 34.99, 2),
    (14, 3, 39.99, 1),
    (14, 5, 22.99, 2);


INSERT INTO Payments (OrderID, PaymentMethodID, Amount, Status, CreatedAt, PaidAt)
VALUES
    -- Order 1: pending, total = 105.97
    (1, 1, 105.97, 'pending',   '2026-05-01 10:16:00', NULL),

    -- Order 2: packed, total = 103.96 -> payment must be completed
    (2, 2, 103.96, 'completed', '2026-05-02 14:31:00', '2026-05-02 14:33:00'),

    -- Order 3: delivered, total = 139.97 -> payment must be completed
    (3, 1, 139.97, 'completed', '2026-05-03 09:46:00', '2026-05-03 09:48:00'),

    -- Order 4: cancelled, total = 27.99
    (4, 3, 27.99, 'cancelled',  '2026-05-04 16:21:00', NULL),

    -- Order 5: delivered, total = 161.94 -> payment must be completed
    (5, 4, 161.94, 'completed', '2026-05-05 11:11:00', '2026-05-05 11:13:00'),

    -- Order 6: pending, total = 67.98
    (6, 2, 67.98, 'pending',    '2026-05-07 08:26:00', NULL),

    -- Order 7: packed, total = 130.96 -> payment must be completed
    (7, 1, 130.96, 'completed', '2026-05-08 13:01:00', '2026-05-08 13:03:00'),

    -- Order 8: delivered, total = 83.97 -> payment must be completed
    (8, 3, 83.97, 'completed',  '2026-05-09 17:41:00', '2026-05-09 17:43:00'),

    -- Order 9: pending, total = 102.97
    (9, 4, 102.97, 'pending',   '2026-05-10 12:06:00', NULL),

    -- Order 10: cancelled, total = 49.99
    (10, 2, 49.99, 'rejected',  '2026-05-11 18:31:00', NULL),

    -- Order 11: delivered, total = 142.96 -> payment must be completed
    (11, 1, 142.96, 'completed','2026-05-12 09:11:00', '2026-05-12 09:13:00'),

    -- Order 12: packed, total = 114.95 -> payment must be completed
    (12, 4, 114.95, 'completed','2026-05-13 15:21:00', '2026-05-13 15:23:00'),

    -- Order 13: pending, total = 105.97
    (13, 3, 105.97, 'pending',  '2026-05-14 10:01:00', NULL),

    -- Order 14: delivered, total = 155.95 -> payment must be completed
    (14, 2, 155.95, 'completed','2026-05-15 11:36:00', '2026-05-15 11:38:00');


COMMIT;