CREATE TABLE PRODUCTS
(
    ID       IDENTITY PRIMARY KEY,
    NAME     VARCHAR(255)   NOT NULL,
    CURRENCY VARCHAR(10)    NOT NULL,
    PRICE    NUMERIC(20, 4) NOT NULL
);

INSERT INTO PRODUCTS (NAME, CURRENCY, PRICE) VALUES('Awesome Product', 'HKD', 99.99);
INSERT INTO PRODUCTS (NAME, CURRENCY, PRICE) VALUES('Random Wand', 'HKD', 88.88);
INSERT INTO PRODUCTS (NAME, CURRENCY, PRICE) VALUES('Bad Coffee Beans', 'USD', 77.77);
