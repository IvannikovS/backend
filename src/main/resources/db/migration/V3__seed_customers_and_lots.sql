
INSERT INTO customers (
    customer_code, customer_name, customer_inn, customer_kpp,
    customer_legal_address, customer_postal_address, customer_email,
    customer_code_main, is_organization, is_person
) VALUES
      ('CUST001', 'ООО Ромашка', '7701234567', '770101001',
       'г. Москва, ул. Ленина, 1', '125009, Москва, ул. Ленина, 1',
       'info@romashka.ru', NULL, TRUE, FALSE),
      ('CUST002', 'ИП Иванов Иван Иванович', '7712345678', NULL,
       'г. Санкт-Петербург, ул. Невская, 10', '190000, СПб, ул. Невская, 10',
       'ivanov@example.com', 'CUST001', FALSE, TRUE),
      ('CUST003', 'ЗАО Ландыш', '7809876543', '780101002',
       'г. Казань, ул. Кремлёвская, 5', '420111, Казань, ул. Кремлёвская, 5',
       'contact@landysh.ru', NULL, TRUE, FALSE);

INSERT INTO lots (
    lot_name, customer_code, price,
    currency_code, nds_rate, place_delivery, date_delivery
) VALUES
      ('Лот №1: Офисная мебель', 'CUST001', 150000.00, 'RUB', '18%', 'Москва, Склад №3', '2025-08-01 10:00:00'),
      ('Лот №2: Компьютеры Dell', 'CUST001', 500000.00, 'RUB', '20%', 'Москва, Склад №1', '2025-08-05 09:00:00'),
      ('Лот №3: Консультационные услуги', 'CUST002', 20000.00, 'RUB', 'Без НДС', 'Удалённо',      '2025-07-20 00:00:00'),
      ('Лот №4: Программное обеспечение', 'CUST003', 3000.00, 'USD', '20%',  'Казань, Офис №2',   '2025-09-10 14:30:00');
