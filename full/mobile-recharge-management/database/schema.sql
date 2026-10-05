DROP DATABASE IF EXISTS mobile_recharge_db;
CREATE DATABASE mobile_recharge_db;
USE mobile_recharge_db;

CREATE TABLE CUSTOMERS (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    address VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE MOBILE_NUMBERS (
    mobile_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    mobile_number VARCHAR(15) NOT NULL UNIQUE,
    operator_name VARCHAR(50) NOT NULL,
    circle_name VARCHAR(80),
    validity_end_date DATE NULL,
    status ENUM('ACTIVE','EXPIRED','SUSPENDED') NOT NULL DEFAULT 'EXPIRED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mobile_customer
        FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(customer_id)
        ON DELETE CASCADE
);

CREATE TABLE PLANS (
    plan_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_name VARCHAR(120) NOT NULL,
    operator_name VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    validity_days INT NOT NULL,
    data_gb DECIMAL(8,2) DEFAULT 0,
    voice_minutes INT DEFAULT 0,
    sms_count INT DEFAULT 0,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_plan_price CHECK (price > 0),
    CONSTRAINT chk_validity_days CHECK (validity_days > 0)
);

CREATE TABLE RECHARGES (
    recharge_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mobile_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    recharge_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('UPI','CARD','NET_BANKING','CASH','WALLET') NOT NULL,
    transaction_ref VARCHAR(80) NOT NULL UNIQUE,
    status ENUM('SUCCESS','FAILED') NOT NULL DEFAULT 'SUCCESS',
    CONSTRAINT fk_recharge_mobile
        FOREIGN KEY (mobile_id) REFERENCES MOBILE_NUMBERS(mobile_id),
    CONSTRAINT fk_recharge_plan
        FOREIGN KEY (plan_id) REFERENCES PLANS(plan_id),
    CONSTRAINT chk_recharge_amount CHECK (amount > 0)
);

CREATE INDEX idx_mobile_customer ON MOBILE_NUMBERS(customer_id);
CREATE INDEX idx_mobile_number ON MOBILE_NUMBERS(mobile_number);
CREATE INDEX idx_plan_operator ON PLANS(operator_name);
CREATE INDEX idx_recharge_mobile ON RECHARGES(mobile_id);
CREATE INDEX idx_recharge_plan ON RECHARGES(plan_id);
CREATE INDEX idx_recharge_date ON RECHARGES(recharge_date);

DROP FUNCTION IF EXISTS calculate_recharge_validity;
DELIMITER $$

CREATE FUNCTION calculate_recharge_validity(
    p_recharge_date DATE,
    p_validity_days INT
)
RETURNS DATE
DETERMINISTIC
BEGIN
    RETURN DATE_ADD(p_recharge_date, INTERVAL p_validity_days - 1 DAY);
END$$

DELIMITER ;

DROP PROCEDURE IF EXISTS perform_recharge;
DELIMITER $$

CREATE PROCEDURE perform_recharge(
    IN p_mobile_id BIGINT,
    IN p_plan_id BIGINT,
    IN p_payment_method VARCHAR(30),
    IN p_transaction_ref VARCHAR(80)
)
BEGIN
    DECLARE v_amount DECIMAL(10,2);
    DECLARE v_status VARCHAR(20);
    DECLARE v_mobile_exists INT DEFAULT 0;
    DECLARE v_plan_exists INT DEFAULT 0;

    SELECT COUNT(*) INTO v_mobile_exists
    FROM MOBILE_NUMBERS
    WHERE mobile_id = p_mobile_id;

    IF v_mobile_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Mobile number not found';
    END IF;

    SELECT COUNT(*), COALESCE(MAX(price), 0)
    INTO v_plan_exists, v_amount
    FROM PLANS
    WHERE plan_id = p_plan_id AND active = TRUE;

    IF v_plan_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Active plan not found';
    END IF;

    SELECT status INTO v_status
    FROM MOBILE_NUMBERS
    WHERE mobile_id = p_mobile_id;

    IF v_status = 'SUSPENDED' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Mobile number is suspended';
    END IF;

    IF p_payment_method NOT IN ('UPI','CARD','NET_BANKING','CASH','WALLET') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid payment method';
    END IF;

    IF p_transaction_ref IS NULL OR TRIM(p_transaction_ref) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Transaction reference is required';
    END IF;

    INSERT INTO RECHARGES (
        mobile_id, plan_id, amount, payment_method, transaction_ref, status
    )
    VALUES (
        p_mobile_id, p_plan_id, v_amount, p_payment_method, p_transaction_ref, 'SUCCESS'
    );
END$$

DELIMITER ;

DROP TRIGGER IF EXISTS trg_recharges_after_insert;
DELIMITER $$

CREATE TRIGGER trg_recharges_after_insert
AFTER INSERT ON RECHARGES
FOR EACH ROW
BEGIN
    DECLARE v_validity_days INT;

    SELECT validity_days INTO v_validity_days
    FROM PLANS
    WHERE plan_id = NEW.plan_id;

    UPDATE MOBILE_NUMBERS
    SET validity_end_date =
            CASE
                WHEN validity_end_date IS NOT NULL
                     AND validity_end_date >= DATE(NEW.recharge_date)
                THEN DATE_ADD(validity_end_date, INTERVAL v_validity_days DAY)
                ELSE calculate_recharge_validity(DATE(NEW.recharge_date), v_validity_days)
            END,
        status = 'ACTIVE'
    WHERE mobile_id = NEW.mobile_id;
END$$

DELIMITER ;

DROP VIEW IF EXISTS vw_recharge_details;

CREATE VIEW vw_recharge_details AS
SELECT
    r.recharge_id,
    c.customer_id,
    c.full_name AS customer_name,
    c.email,
    m.mobile_id,
    m.mobile_number,
    m.operator_name,
    m.validity_end_date,
    m.status AS mobile_status,
    p.plan_id,
    p.plan_name,
    p.price,
    p.validity_days,
    p.data_gb,
    p.voice_minutes,
    p.sms_count,
    r.recharge_date,
    r.amount,
    r.payment_method,
    r.transaction_ref,
    r.status AS recharge_status
FROM RECHARGES r
JOIN MOBILE_NUMBERS m ON r.mobile_id = m.mobile_id
JOIN CUSTOMERS c ON m.customer_id = c.customer_id
JOIN PLANS p ON r.plan_id = p.plan_id;
