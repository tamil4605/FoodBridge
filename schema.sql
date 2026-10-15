-- ==============================================================
-- FOODBRIDGE DATABASE SCHEMA (MySQL 8.0)
-- Smart Food Donation and Redistribution Platform
-- ==============================================================

CREATE DATABASE IF NOT EXISTS foodbridge_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE foodbridge_db;

-- 1. USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    account_type VARCHAR(40) NOT NULL,
    role VARCHAR(40) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(20),
    organization_name VARCHAR(180),
    organization_type VARCHAR(40),
    verification_status VARCHAR(30),
    verification_notes TEXT,
    verified_at DATETIME,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 2. FOOD DONATIONS TABLE
CREATE TABLE IF NOT EXISTS food_donations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donor_id BIGINT NOT NULL,
    food_name VARCHAR(150) NOT NULL,
    category VARCHAR(40) NOT NULL,
    description TEXT,
    dietary_type VARCHAR(30) NOT NULL,
    quantity DOUBLE NOT NULL,
    remaining_quantity DOUBLE NOT NULL,
    unit VARCHAR(50) NOT NULL,
    preparation_date_time DATETIME NOT NULL,
    safe_collection_deadline DATETIME NOT NULL,
    storage_instructions VARCHAR(255),
    allergen_info VARCHAR(255),
    pickup_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    pincode VARCHAR(20) NOT NULL,
    pickup_instructions VARCHAR(500),
    image_path VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT fk_food_donor FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. MATERIAL DONATIONS TABLE
CREATE TABLE IF NOT EXISTS material_donations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donor_id BIGINT NOT NULL,
    item_name VARCHAR(150) NOT NULL,
    category VARCHAR(40) NOT NULL,
    description TEXT,
    quantity INT NOT NULL,
    remaining_quantity INT NOT NULL,
    unit VARCHAR(50) NOT NULL,
    condition_status VARCHAR(30) NOT NULL,
    expiry_date DATE,
    pickup_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    pincode VARCHAR(20) NOT NULL,
    preferred_pickup_date DATE,
    pickup_instructions VARCHAR(500),
    image_path VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT fk_material_donor FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. CASH CAMPAIGNS TABLE
CREATE TABLE IF NOT EXISTS cash_campaigns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    cause_category VARCHAR(40) NOT NULL,
    description TEXT,
    target_amount DECIMAL(12, 2) NOT NULL,
    raised_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    deadline DATE NOT NULL,
    approval_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    image_path VARCHAR(255),
    admin_review_notes TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT fk_campaign_org FOREIGN KEY (organization_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. CASH TRANSACTIONS TABLE
CREATE TABLE IF NOT EXISTS cash_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id BIGINT NOT NULL,
    donor_id BIGINT,
    donor_name VARCHAR(150) NOT NULL,
    donor_email VARCHAR(150) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payment_mode VARCHAR(50) NOT NULL DEFAULT 'DEMO_SIMULATION',
    transaction_reference VARCHAR(80) NOT NULL UNIQUE,
    donor_message VARCHAR(500),
    confirmed BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_tx_campaign FOREIGN KEY (campaign_id) REFERENCES cash_campaigns(id) ON DELETE CASCADE,
    CONSTRAINT fk_tx_donor FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 6. COMMUNITY NEEDS TABLE
CREATE TABLE IF NOT EXISTS community_needs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    category VARCHAR(40) NOT NULL,
    description TEXT,
    target_quantity DOUBLE NOT NULL,
    fulfilled_quantity DOUBLE NOT NULL DEFAULT 0.0,
    unit VARCHAR(50) NOT NULL,
    priority VARCHAR(30) NOT NULL DEFAULT 'MEDIUM',
    target_date DATE NOT NULL,
    city VARCHAR(100) NOT NULL,
    pincode VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT fk_need_org FOREIGN KEY (organization_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 7. DONATION REQUESTS TABLE
CREATE TABLE IF NOT EXISTS donation_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_org_id BIGINT NOT NULL,
    donation_type VARCHAR(30) NOT NULL,
    food_donation_id BIGINT,
    material_donation_id BIGINT,
    requested_quantity DOUBLE NOT NULL,
    unit VARCHAR(50),
    message TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    rejection_reason VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT fk_req_org FOREIGN KEY (requester_org_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_req_food FOREIGN KEY (food_donation_id) REFERENCES food_donations(id) ON DELETE CASCADE,
    CONSTRAINT fk_req_material FOREIGN KEY (material_donation_id) REFERENCES material_donations(id) ON DELETE CASCADE
);

-- 8. PICKUPS TABLE
CREATE TABLE IF NOT EXISTS pickups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donation_request_id BIGINT NOT NULL UNIQUE,
    donor_id BIGINT NOT NULL,
    recipient_org_id BIGINT NOT NULL,
    scheduled_date_time DATETIME NOT NULL,
    pickup_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    pincode VARCHAR(20) NOT NULL,
    special_instructions VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    collected_at DATETIME,
    handed_over_at DATETIME,
    cancellation_reason VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT fk_pickup_req FOREIGN KEY (donation_request_id) REFERENCES donation_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_pickup_donor FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_pickup_org FOREIGN KEY (recipient_org_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 9. NOTIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    title VARCHAR(180) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(40) NOT NULL DEFAULT 'GENERAL',
    link_url VARCHAR(255),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_notif_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 10. FEEDBACKS TABLE
CREATE TABLE IF NOT EXISTS feedbacks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    giver_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    pickup_id BIGINT,
    rating INT NOT NULL,
    comment TEXT NOT NULL,
    reported BOOLEAN NOT NULL DEFAULT FALSE,
    moderated BOOLEAN NOT NULL DEFAULT FALSE,
    report_reason VARCHAR(255),
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_fb_giver FOREIGN KEY (giver_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_fb_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_fb_pickup FOREIGN KEY (pickup_id) REFERENCES pickups(id) ON DELETE SET NULL
);

-- 11. AUDIT LOGS TABLE
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor VARCHAR(150) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_name VARCHAR(100),
    entity_id BIGINT,
    details TEXT,
    ip_address VARCHAR(60),
    timestamp DATETIME NOT NULL
);

-- INDEXES FOR HIGH-PERFORMANCE SEARCH & FILTERING
CREATE INDEX idx_food_status_deadline ON food_donations(status, safe_collection_deadline);
CREATE INDEX idx_food_city_pincode ON food_donations(city, pincode);
CREATE INDEX idx_material_status ON material_donations(status);
CREATE INDEX idx_campaign_status ON cash_campaigns(approval_status, status);
CREATE INDEX idx_need_status_priority ON community_needs(status, priority);
CREATE INDEX idx_notif_user_read ON notifications(recipient_id, is_read);
