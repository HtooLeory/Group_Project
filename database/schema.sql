CREATE DATABASE IF NOT EXISTS qc_lost_found;

USE qc_lost_found;

CREATE TABLE IF NOT EXISTS admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

INSERT IGNORE INTO admins (username, password)
VALUES 
('admin1', 'admin123');

CREATE TABLE IF NOT EXISTS found_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_type VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    found_location VARCHAR(255),
    found_date DATE,
    status VARCHAR(50) DEFAULT 'Available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lost_tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    student_id VARCHAR(50) NOT NULL,
    student_email VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    item_type VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    lost_location VARCHAR(255),
    lost_date DATE,
    proof_file_path VARCHAR(255),
    matched_found_item_id INT NULL,
    status VARCHAR(50) DEFAULT 'Pending',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_matched_found_item
    FOREIGN KEY (matched_found_item_id)
    REFERENCES found_items(id)
);