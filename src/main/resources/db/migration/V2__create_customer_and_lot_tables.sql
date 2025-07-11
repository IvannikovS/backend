
-- Справочник контрагентов
CREATE TABLE IF NOT EXISTS customers (
    customer_code           VARCHAR(50)    PRIMARY KEY,
    customer_name           VARCHAR(255)   NOT NULL,
    customer_inn            VARCHAR(20),
    customer_kpp            VARCHAR(20),
    customer_legal_address  VARCHAR(255),
    customer_postal_address VARCHAR(255),
    customer_email          VARCHAR(255),
    customer_code_main      VARCHAR(50),
    is_organization         BOOLEAN        NOT NULL DEFAULT TRUE,
    is_person               BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_customer_parent
    FOREIGN KEY (customer_code_main)
    REFERENCES customers(customer_code)
    ON DELETE SET NULL
    );

-- Таблица лотов
CREATE TABLE IF NOT EXISTS lots (
    lot_id           BIGSERIAL        PRIMARY KEY,
    lot_name         VARCHAR(255)  NOT NULL,
    customer_code    VARCHAR(50)   NOT NULL,
    price            NUMERIC(15,2) NOT NULL,
    currency_code    VARCHAR(3)    NOT NULL,
    nds_rate         VARCHAR(10)   NOT NULL,
    place_delivery   VARCHAR(255),
    date_delivery    TIMESTAMP,
    CONSTRAINT fk_lot_customer
    FOREIGN KEY (customer_code)
    REFERENCES customers(customer_code)
    ON DELETE RESTRICT,
    CONSTRAINT chk_lot_currency
    CHECK (currency_code IN ('RUB','USD','EUR')),
    CONSTRAINT chk_lot_nds
    CHECK (nds_rate IN ('Без НДС','18%','20%'))
    );
