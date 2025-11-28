DROP DATABASE IF EXISTS pizza_database;

CREATE DATABASE pizza_database;

USE pizza_database;

CREATE TABLE users (
	id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE orders (
	id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    created_at DATETIME NOT NULL,
    estimated_at DATETIME NOT NULL,
    status ENUM('Pending','Processing','Ready','Delivered','Canceld') DEFAULT 'Pending',
    delivery_price DECIMAL(10,2) NOT NULL,
    order_price DECIMAL(10,2) NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);
CREATE TABLE products(
	id 	INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    category ENUM('pizza', 'sauce', 'drink', 'ingredient') DEFAULT 'ingredient',
    price  DECIMAL(10,2) NOT NULL
);

CREATE TABLE order_items(
	id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    order_id INT NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('Pending','Processing','Ready','Delivered','Canceld') default 'Pending',
    
	FOREIGN KEY (product_id) REFERENCES products(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
        
	FOREIGN KEY (order_id) REFERENCES orders(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE reciepes(
	 id INT AUTO_INCREMENT PRIMARY KEY,
     pizza_id INT UNIQUE NOT NULL,
     
     FOREIGN KEY (pizza_id) REFERENCES  products(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE

);
-- TODO one to one only if the foreign key is primary key
CREATE TABLE stock(
	id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    details VARCHAR(50) NOT NULL,
	category ENUM('pizza', 'sauce', 'drink', 'ingredient') DEFAULT 'ingredient',
    quntity INT DEFAULT 0,
	unit ENUM('kg', 'g', 'piece') NOT NULL  
);

CREATE TABLE ingredients(
	id INT AUTO_INCREMENT PRIMARY KEY,
	stock_id INT NOT NULL,
    recipe_id INT UNIQUE NOT NULL ,
    quntity INT DEFAULT 0,
	unit ENUM('kg', 'g', 'piece') NOT NULL,
    
    FOREIGN KEY (stock_id) REFERENCES stock(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
        
	FOREIGN KEY (recipe_id)	REFERENCES reciepes(id)
		ON DELETE CASCADE
        ON UPDATE CASCADE
        
);


