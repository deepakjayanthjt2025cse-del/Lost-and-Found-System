-- ============================================================
--  LOST AND FOUND SYSTEM  -  Database Script
--  Requires MySQL 8.0.16 or newer (CHECK constraints)
--  WARNING: running this script RESETS the tables and sample data.
-- ============================================================

CREATE DATABASE IF NOT EXISTS lost_and_found_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE lost_and_found_db;

-- Drop in reverse order of dependency so foreign keys do not block us
DROP TABLE IF EXISTS claims;
DROP TABLE IF EXISTS found_items;
DROP TABLE IF EXISTS lost_items;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS users;

-- ------------------------------------------------------------
-- 1. users : one row per person (identified by contact number)
-- ------------------------------------------------------------
CREATE TABLE users (
    user_id        INT          NOT NULL AUTO_INCREMENT,
    user_name      VARCHAR(100) NOT NULL,
    contact_number VARCHAR(10)  NOT NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_users_contact (contact_number),
    CONSTRAINT chk_users_contact CHECK (contact_number REGEXP '^[0-9]{10}$')
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 2. admins : login credentials
--    (plain text only for classroom demonstration)
-- ------------------------------------------------------------
CREATE TABLE admins (
    admin_id INT         NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    PRIMARY KEY (admin_id),
    UNIQUE KEY uq_admins_username (username)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3. lost_items
--    status: LOST     = still missing
--            RETURNED = owner got it back / case closed
-- ------------------------------------------------------------
CREATE TABLE lost_items (
    lost_id       INT          NOT NULL AUTO_INCREMENT,
    user_id       INT          NOT NULL,
    item_name     VARCHAR(100) NOT NULL,
    category      VARCHAR(50)  NOT NULL,
    description   VARCHAR(255) NOT NULL,
    lost_location VARCHAR(100) NOT NULL,
    lost_date     DATE         NOT NULL,
    status        ENUM('LOST','RETURNED') NOT NULL DEFAULT 'LOST',
    PRIMARY KEY (lost_id),
    CONSTRAINT fk_lost_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    INDEX idx_lost_name (item_name),
    INDEX idx_lost_category (category),
    INDEX idx_lost_location (lost_location)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 4. found_items
--    status: AVAILABLE = waiting to be claimed
--            CLAIMED   = a claim was approved, item handed over
-- ------------------------------------------------------------
CREATE TABLE found_items (
    found_id       INT          NOT NULL AUTO_INCREMENT,
    user_id        INT          NOT NULL,
    item_name      VARCHAR(100) NOT NULL,
    category       VARCHAR(50)  NOT NULL,
    description    VARCHAR(255) NOT NULL,
    found_location VARCHAR(100) NOT NULL,
    found_date     DATE         NOT NULL,
    status         ENUM('AVAILABLE','CLAIMED') NOT NULL DEFAULT 'AVAILABLE',
    PRIMARY KEY (found_id),
    CONSTRAINT fk_found_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    INDEX idx_found_name (item_name),
    INDEX idx_found_category (category),
    INDEX idx_found_location (found_location)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 5. claims
--    claim_status: PENDING / APPROVED / REJECTED
--    UNIQUE (found_id, claimant_contact) blocks duplicate claims
--    ON DELETE CASCADE: deleting a found item also removes its claims
-- ------------------------------------------------------------
CREATE TABLE claims (
    claim_id          INT          NOT NULL AUTO_INCREMENT,
    found_id          INT          NOT NULL,
    claimant_name     VARCHAR(100) NOT NULL,
    claimant_contact  VARCHAR(10)  NOT NULL,
    claim_description VARCHAR(255) NOT NULL,
    claim_date        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    claim_status      ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (claim_id),
    CONSTRAINT fk_claim_found FOREIGN KEY (found_id)
        REFERENCES found_items (found_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT uq_claim_once UNIQUE (found_id, claimant_contact),
    CONSTRAINT chk_claim_contact CHECK (claimant_contact REGEXP '^[0-9]{10}$')
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Initial admin account (demo credentials)
-- ------------------------------------------------------------
INSERT INTO admins (username, password) VALUES ('admin', 'admin123');

-- ------------------------------------------------------------
-- Sample data for testing / demonstration
-- ------------------------------------------------------------
INSERT INTO users (user_name, contact_number) VALUES
    ('Arun Kumar',  '9876543210'),
    ('Priya Sharma','9123456780'),
    ('Karthik R',   '9988776655');

INSERT INTO lost_items (user_id, item_name, category, description, lost_location, lost_date) VALUES
    (1, 'Black Wallet',  'Accessories', 'Leather wallet with college ID inside', 'Chennai Central', '2026-10-25'),
    (2, 'Blue Backpack', 'Bags',        'Laptop bag with red zipper',            'Coimbatore Bus Stand', '2026-10-28');

INSERT INTO found_items (user_id, item_name, category, description, found_location, found_date) VALUES
    (3, 'Wallet',       'Accessories', 'Black leather wallet found near platform 3', 'Chennai Central', '2026-10-26'),
    (3, 'Mobile Phone', 'Electronics', 'Silver phone with cracked screen guard',     'Coimbatore',      '2026-10-29');

INSERT INTO claims (found_id, claimant_name, claimant_contact, claim_description) VALUES
    (1, 'Arun Kumar', '9876543210', 'My college ID card is inside the wallet');

-- ------------------------------------------------------------
-- Quick check
-- ------------------------------------------------------------
SELECT 'users' AS tbl, COUNT(*) AS total_rows FROM users
UNION ALL SELECT 'admins',      COUNT(*) FROM admins
UNION ALL SELECT 'lost_items',  COUNT(*) FROM lost_items
UNION ALL SELECT 'found_items', COUNT(*) FROM found_items
UNION ALL SELECT 'claims',      COUNT(*) FROM claims;
