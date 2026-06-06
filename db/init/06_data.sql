BEGIN;

INSERT INTO Customers(firstname, lastname, email)
VALUES
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
    ('Mlynki', NULL),
    ('Ekspresy', NULL),
    ('Kawa ziarnista', 1),
    ('Kawa mielona', 1),
    ('Kapsulki kawowe', 1),
    ('Mlynki reczne', 2),
    ('Mlynki automatyczne', 2),
    ('Ekspresy kolbowe', 3),
    ('Ekspresy automatyczne', 3),
    ('Ekspresy na kapsulki', 3);

INSERT INTO Products (CategoryID, Name, Price, Stock, Attributes)
VALUES
    (4, 'Lavazza Qualita Oro 1kg', 49.99, 20, '{"marka": "Lavazza", "kraj pochodzenia": "Ameryka Centralna", "rodzaj": "Arabika", "waga": "1kg", "stopień palenia": "średni"}'),
    (4, 'Dallmayr Prodomo Arabica 500g', 34.99, 15, '{"marka": "Dallmayr", "kraj pochodzenia": "Etiopia", "rodzaj": "Arabika", "waga": "500g", "stopień palenia": "średni"}'),
    (4, 'Wietnam Rizzi Vietnam Gia Lai 1kg', 39.99, 12, '{"marka": "Rizzi", "kraj pochodzenia": "Wietnam", "rodzaj": "Robusta", "waga": "1kg", "stopień palenia": "ciemny"}'),
    (5, 'Kawa mielona Lavazza Espresso Crema e Gusto 250g', 34.99, 30, '{"marka": "Lavazza", "kraj pochodzenia": ["Afryka", "Azja", "Brazylia"], "rodzaj": "20% Arabika, 80% Robusta", "waga": "250g", "stopień palenia": "średni"}'),
    (6, 'Nespresso Espresso Capsules 10 pcs', 22.99, 40, '{"marka": "Nespresso", "ilość kapsułek": 10, "intensywność": 8, "system": "Nespresso"}'),
    (7, 'Młynek do kawy Comandante C40 Mk4', 1439.00, 20, '{"marka": "Comandante", "średnica żaren": "30mm"}'),
    (7, 'Młynek do kawy Timemore C5 ESP PRO', 429.00, 50, '{"marka": "Timemore", "średnica żaren": "42mm"}'),
    (7, 'Młynek do kawy Comandante X25', 1059.00, 20, '{"marka": "Comandante", "średnica żaren": "39mm"}'),
    (8, 'Młynek do kawy Lelit William PL72', 2499.00, 15, '{"marka": "Lelit", "Pojemność zbiornika na kawę": "350g", "średnica żaren": "64 mm"}'),
    (8, 'Młynek do kawy Lelit Fred PL044MMT', 1499.00, 15, '{"marka": "Lelit", "Pojemność zbiornika na kawę": "250g", "średnica żaren": "38 mm"}'),
    (8, 'Młynek do kawy Varia VS4', 2299.00, 10, '{"marka": "Varia", "Pojemność zbiornika na kawę": "40g", "średnica żaren": "53 mm"}'),
    (9, 'Ekspres do kawy Stone Espresso Lite Premium Chrome', 4899.00, 10, '{"marka": "Stone Espresso", "ciśnienie": "15 bar", "średnica kolby": "58 mm"}'),
    (9, 'Ekspres do kawy Lelit Bianca Polished Stainless Steel', 11999.00, 15, '{"marka": "Lelit", "ciśnienie": "13 bar", "średnica kolby": "58 mm"}'),
    (10, 'Ekspres do kawy Melitta Barista T F83/0-002', 2599.00, 20, '{"marka": "Melitta", "wyjmowany spieniacz": "tak", "ilość dozowników": "2"}'),
    (10, 'Ekspres do kawy Nivona 792', 2999.00, 15, '{"marka": "Nivona", "wyjmowany spieniacz": "tak", "ilość dozowników": "1"}'),
    (10, 'Ekspres do kawy Philips EP2224/10', 1919.00, 20, '{"marka": "Philips", "wyjmowany spieniacz": "nie", "ilość dozowników": "2"}'),
    (11, 'Ekspres do kawy Ninja Prestige CFN802EU 2w1 ze spieniaczem', 1199.00, 30, '{"marka": "Ninja", "kompatybilność": "kapsułki Nespresso Original i kompatybilne kapsułki innych marek"}'),
    (11, 'Ekspres na kapsułki Lavazza Blue LB CLASSY', 1999.00, 25, '{"marka": "Lavazza", "kompatybilność": "Lavazza Blue"}');

INSERT INTO Orders (CustomerID, AddressID, Status, OrderDate, ShipDate)
VALUES
    (1, 1, 'pending',   '2026-05-01 10:15:00', NULL),
    (1, 2, 'packed',    '2026-05-02 14:30:00', NULL),
    (1, 1, 'delivered', '2026-05-03 09:45:00', '2026-05-04 12:00:00'),
    (2, 3, 'cancelled', '2026-05-04 16:20:00', NULL),
    (2, 3, 'delivered', '2026-05-05 11:10:00', '2026-05-06 15:30:00'),
    (2, 3, 'pending',   '2026-05-07 08:25:00', NULL),
    (3, 4, 'packed',    '2026-05-08 13:00:00', NULL),
    (3, 4, 'delivered', '2026-05-09 17:40:00', '2026-05-10 10:15:00'),
    (4, 5, 'pending',   '2026-05-10 12:05:00', NULL),
    (4, 5, 'cancelled', '2026-05-11 18:30:00', NULL),
    (4, 5, 'delivered', '2026-05-12 09:10:00', '2026-05-13 14:45:00'),
    (5, 6, 'packed',    '2026-05-13 15:20:00', NULL),
    (5, 6, 'pending',   '2026-05-14 10:00:00', NULL),
    (5, 6, 'delivered', '2026-05-15 11:35:00', '2026-05-16 16:10:00');

INSERT INTO OrderDetails (OrderID, ProductID, UnitPrice, Quantity)
VALUES
    (1, 1, 49.99, 1),
    (1, 4, 34.99, 2),
    (2, 2, 34.99, 1),
    (2, 5, 22.99, 3),
    (3, 1, 49.99, 2),
    (3, 7, 429.00, 1),
    (4, 4, 34.99, 1),
    (5, 9, 2499.00, 1),
    (5, 5, 22.99, 4),
    (6, 3, 39.99, 1),
    (6, 6, 1439.00, 1),
    (7, 12, 4899.00, 1),
    (7, 2, 34.99, 1),
    (7, 5, 22.99, 2),
    (8, 14, 2599.00, 1),
    (8, 4, 34.99, 3),
    (9, 3, 39.99, 2),
    (9, 17, 1199.00, 1),
    (10, 1, 49.99, 1),
    (11, 15, 2999.00, 1),
    (11, 3, 39.99, 2),
    (11, 4, 34.99, 1),
    (12, 18, 1999.00, 1),
    (12, 5, 22.99, 3),
    (13, 1, 49.99, 1),
    (13, 10, 1499.00, 1),
    (14, 2, 34.99, 2),
    (14, 11, 2299.00, 1),
    (14, 5, 22.99, 2);

INSERT INTO Payments (OrderID, PaymentMethodID, Amount, Status, CreatedAt, PaidAt)
VALUES
    (1, 1, 119.97, 'pending',   '2026-05-01 10:16:00', NULL),
    (2, 2, 103.96, 'completed', '2026-05-02 14:31:00', '2026-05-02 14:33:00'),
    (3, 1, 528.98, 'completed', '2026-05-03 09:46:00', '2026-05-03 09:48:00'),
    (4, 3, 34.99, 'cancelled',  '2026-05-04 16:21:00', NULL),
    (5, 4, 2590.96, 'completed','2026-05-05 11:11:00', '2026-05-05 11:13:00'),
    (6, 2, 1478.99, 'pending',  '2026-05-07 08:26:00', NULL),
    (7, 1, 4979.97, 'completed','2026-05-08 13:01:00', '2026-05-08 13:03:00'),
    (8, 3, 2703.97, 'completed','2026-05-09 17:41:00', '2026-05-09 17:43:00'),
    (9, 4, 1278.97, 'pending',  '2026-05-10 12:06:00', NULL),
    (10, 2, 49.99, 'cancelled', '2026-05-11 18:31:00', NULL),
    (11, 1, 3113.97, 'completed','2026-05-12 09:11:00', '2026-05-12 09:13:00'),
    (12, 4, 2067.97, 'completed','2026-05-13 15:21:00', '2026-05-13 15:23:00'),
    (13, 3, 1548.99, 'pending',  '2026-05-14 10:01:00', NULL),
    (14, 2, 2414.97, 'completed','2026-05-15 11:36:00', '2026-05-15 11:38:00');

COMMIT;