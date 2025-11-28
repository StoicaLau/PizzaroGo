DROP DATABASE IF EXISTS pizza_database;
CREATE DATABASE pizza_database;
USE pizza_database;

-- =========================================================
-- USERS
-- =========================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('admin','employee','customer') NOT NULL DEFAULT 'customer'
);

-- =========================================================
-- ORDERS
-- =========================================================
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT NOW(),
    estimated_at DATETIME NULL,
    status ENUM('Pending','Processing','Ready','Delivered','Canceled') DEFAULT 'Pending',
    delivery_price DECIMAL(10,2) NOT NULL DEFAULT 0,
    order_price DECIMAL(10,2) NOT NULL DEFAULT 0,

    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- =========================================================
-- MENU PRODUCTS: pizza, sauce, drink
-- =========================================================
CREATE TABLE menu_products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category ENUM('pizza','sauce','drink') NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

-- =========================================================
-- ORDER ITEMS
-- =========================================================
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('Pending','Processing','Ready','Delivered','Canceled') DEFAULT 'Pending',

    FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (product_id) REFERENCES menu_products(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- =========================================================
-- STOCK ITEMS (ingrediente, sucuri, sosuri)
-- =========================================================
CREATE TABLE stock_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit ENUM('kg','g','piece','l','ml') NOT NULL,
    quantity DECIMAL(10,2) NOT NULL DEFAULT 0
);

-- =========================================================
-- RECIPES (1 la 1 cu pizza)
-- =========================================================
CREATE TABLE recipes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT UNIQUE NOT NULL,

    FOREIGN KEY (product_id) REFERENCES menu_products(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- =========================================================
-- RECIPE ITEMS (ingredientele unei pizza)
-- Cantitatea este în unitatea definită în stock_items
-- =========================================================
CREATE TABLE recipe_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recipe_id INT NOT NULL,
    stock_item_id INT NOT NULL,
    quantity DECIMAL(10,2) NOT NULL, 

    FOREIGN KEY (recipe_id) REFERENCES recipes(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (stock_item_id) REFERENCES stock_items(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- =========================================================
-- PRODUCT-STOCK LINK
--      1 produs vandut = X cantitate scazuta din stoc
-- =========================================================
CREATE TABLE product_stock_link (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,       -- din menu_products (doar drink / sauce)
    stock_item_id INT NOT NULL,    -- din stock_items
    quantity_per_unit DECIMAL(10,2) NOT NULL DEFAULT 1,

    FOREIGN KEY (product_id) REFERENCES menu_products(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (stock_item_id) REFERENCES stock_items(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
