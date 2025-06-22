-- ----------------------------
-- 1. 创建数据库（可选）
-- ----------------------------
CREATE DATABASE IF NOT EXISTS demo;
USE demo;

-- ----------------------------
-- 2. 买家表 (user)
-- ----------------------------
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
	balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 3. 商家表 (merchant)
-- ----------------------------
CREATE TABLE IF NOT EXISTS merchant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) UNIQUE,
	balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    balance_of_previous_day DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 4. 产品表 (product)
-- ----------------------------
CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    merchant_id BIGINT NOT NULL,
	price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchant(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 5. 库存表 (inventory)
-- ----------------------------
CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    product_id BIGINT UNIQUE NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (merchant_id) REFERENCES merchant(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    UNIQUE KEY uk_merchant_product (merchant_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 6. 订单表 (order)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
	product_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    quantity INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
	FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 7. 插入初始测试数据
-- ----------------------------
-- 插入买家数据
INSERT INTO user (name, email, phone, balance) VALUES
('Pony', 'pony@example.com', '13800138001', 0.0),
('Tom', 'tom@example.com', '13800138002', 0.0);

-- 插入商家数据
INSERT INTO merchant (name, email, phone) VALUES
('商家A', 'merchant_a@example.com', '13800138001'),
('商家B', 'merchant_b@example.com', '13800138002');

-- 插入产品数据
INSERT INTO product (name, merchant_id, price) VALUES
('产品A', 1, 10.5),
('产品A', 1, 1.0),
('产品B', 2, 5.5),
('产品B', 2, 3.3);

-- 插入库存数据
INSERT INTO inventory (merchant_id, product_id, quantity) VALUES
(1, 1, 100),
(1, 2, 200),
(2, 3, 150),
(2, 4, 250);

