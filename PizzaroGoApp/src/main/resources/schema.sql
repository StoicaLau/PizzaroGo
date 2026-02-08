CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'EMPLOYEE', 'CUSTOMER') DEFAULT 'CUSTOMER'
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estimated_at DATETIME NULL,
    status ENUM('PENDING', 'PROCESSING', 'READY', 'DELIVERED', 'CANCELED') DEFAULT 'PENDING',
    order_price DOUBLE NOT NULL DEFAULT 0,
    delivery_price DOUBLE NOT NULL DEFAULT 0,
    total_price DOUBLE NOT NULL DEFAULT 0,

    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS stock_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_URL VARCHAR(200),
    name VARCHAR(100) NOT NULL,
    category ENUM('INGREDIENT', 'PRODUCT') DEFAULT 'INGREDIENT',
    quantity DOUBLE NOT NULL DEFAULT 0,
    unit ENUM('KG', 'G', 'PIECE', 'L', 'ML')
);

CREATE TABLE IF NOT EXISTS menu_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_URL VARCHAR(5000),
    description VARCHAR(500),
    name VARCHAR(100) NOT NULL,
    product_category ENUM('PIZZA', 'SAUCE', 'DRINK'),
    price DOUBLE NOT NULL DEFAULT 0
);

-- Note: Removed ON DELETE CASCADE on stock_item_id so the trigger can find the link before it's gone
CREATE TABLE IF NOT EXISTS product_stock_usages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    stock_item_id BIGINT NOT NULL,
    quantity_per_unit DOUBLE NOT NULL DEFAULT 1,

    FOREIGN KEY (product_id) REFERENCES menu_products(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (stock_item_id) REFERENCES stock_items(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    total_price DOUBLE NOT NULL DEFAULT 0.0,
    status ENUM('PENDING', 'PROCESSING', 'READY', 'DELIVERED', 'CANCELED') DEFAULT 'PENDING',

    FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (product_id) REFERENCES menu_products(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Triggers to maintain relational integrity

-- 76. Ingredient -> Product: Delete all products using this ingredient
DROP TRIGGER IF EXISTS cleanup_stock_delete_recursive;
CREATE TRIGGER cleanup_stock_delete_recursive
BEFORE DELETE ON stock_items
FOR EACH ROW
DELETE FROM menu_products 
WHERE id IN (SELECT product_id FROM product_stock_usages WHERE stock_item_id = OLD.id);

-- 2. Product -> Orders: cleanup logic
DROP TRIGGER IF EXISTS cleanup_menu_product_orders;

-- Step A: Delete orders that will strictly become empty (contain only this product)
CREATE TRIGGER cleanup_menu_product_orders
BEFORE DELETE ON menu_products
FOR EACH ROW
DELETE FROM orders
WHERE id IN (
    SELECT order_id FROM (
        SELECT order_id
        FROM order_items
        WHERE order_id IN (SELECT DISTINCT order_id FROM order_items WHERE product_id = OLD.id)
        GROUP BY order_id
        HAVING COUNT(*) = SUM(CASE WHEN product_id = OLD.id THEN 1 ELSE 0 END)
    ) AS empty_orders
);
