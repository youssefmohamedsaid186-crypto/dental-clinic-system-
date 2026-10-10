-- Database Schema for Dental Clinic Management System
-- Database Name: dental_clinic_db

CREATE DATABASE IF NOT EXISTS `dental_clinic_db`;
USE `dental_clinic_db`;

-- Drop tables in reverse order of dependencies if re-running
DROP TABLE IF EXISTS `payments`;
DROP TABLE IF EXISTS `appointments`;
DROP TABLE IF EXISTS `resources`;
DROP TABLE IF EXISTS `patients`;
DROP TABLE IF EXISTS `receptionists`;
DROP TABLE IF EXISTS `doctors`;
DROP TABLE IF EXISTS `users`;

-- 1. Users Table
CREATE TABLE `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` ENUM('ADMIN', 'DOCTOR', 'RECEPTIONIST', 'PATIENT') NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Doctors Table
CREATE TABLE `doctors` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `specialization` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `email` VARCHAR(100) NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Receptionists Table
CREATE TABLE `receptionists` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `email` VARCHAR(100) NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Patients Table
CREATE TABLE `patients` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL,
    `dob` DATE NOT NULL,
    `gender` VARCHAR(10) NOT NULL,
    `address` TEXT NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Appointments Table
CREATE TABLE `appointments` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `patient_id` INT NOT NULL,
    `doctor_id` INT NOT NULL,
    `appointment_date` DATE NOT NULL,
    `appointment_time` TIME NOT NULL,
    `amount_due` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `status` ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'PAID', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    `notes` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`patient_id`) REFERENCES `patients`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`doctor_id`) REFERENCES `doctors`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Resources Table
CREATE TABLE `resources` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `tool_name` VARCHAR(100) NOT NULL UNIQUE,
    `total_qty` INT NOT NULL DEFAULT 0,
    `available_qty` INT NOT NULL DEFAULT 0,
    `status` ENUM('AVAILABLE', 'LOW_STOCK', 'OUT_OF_STOCK') NOT NULL DEFAULT 'AVAILABLE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Payments Table
CREATE TABLE `payments` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `appointment_id` INT NOT NULL,
    `patient_id` INT NOT NULL,
    `amount_paid` DECIMAL(10,2) NOT NULL,
    `payment_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `payment_method` VARCHAR(50) NOT NULL DEFAULT 'CASH',
    `status` VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    FOREIGN KEY (`appointment_id`) REFERENCES `appointments`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`patient_id`) REFERENCES `patients`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================
-- DEFAULT SEED DATA
-- ==================================================

-- Insert Admin User (id = 1)
INSERT INTO `users` (`username`, `password`, `role`) VALUES 
('admin', 'admin123', 'ADMIN');

-- Insert Doctor User (id = 2) & Profile
INSERT INTO `users` (`username`, `password`, `role`) VALUES 
('dr_smith', 'doctor123', 'DOCTOR');
INSERT INTO `doctors` (`user_id`, `name`, `specialization`, `phone`, `email`) VALUES 
(2, 'Dr. Sarah Smith', 'General & Orthodontics', '555-0101', 'dr.smith@dental.com');

-- Insert Receptionist User (id = 3) & Profile
INSERT INTO `users` (`username`, `password`, `role`) VALUES 
('recep_john', 'recep123', 'RECEPTIONIST');
INSERT INTO `receptionists` (`user_id`, `name`, `phone`, `email`) VALUES 
(3, 'John Doe', '555-0102', 'john.recep@dental.com');

-- Insert Patient User (id = 4) & Profile
INSERT INTO `users` (`username`, `password`, `role`) VALUES 
('patient_alice', 'patient123', 'PATIENT');
INSERT INTO `patients` (`user_id`, `name`, `phone`, `email`, `dob`, `gender`, `address`) VALUES 
(4, 'Alice Johnson', '555-0103', 'alice@example.com', '1995-05-15', 'Female', '123 Maple Street, Cityville');

-- Insert Initial Appointments
INSERT INTO `appointments` (`patient_id`, `doctor_id`, `appointment_date`, `appointment_time`, `amount_due`, `status`, `notes`) VALUES 
(1, 1, '2026-08-15', '10:00:00', 150.00, 'PENDING', 'Routine dental checkup & cleaning'),
(1, 1, '2026-07-10', '14:30:00', 200.00, 'PAID', 'Teeth whitening session');

-- Insert Payments for initial paid appointment
INSERT INTO `payments` (`appointment_id`, `patient_id`, `amount_paid`, `payment_method`, `status`) VALUES 
(2, 1, 200.00, 'CREDIT_CARD', 'COMPLETED');

-- Insert Resources / Inventory Tools
INSERT INTO `resources` (`tool_name`, `total_qty`, `available_qty`, `status`) VALUES 
('Dental Mirror', 50, 45, 'AVAILABLE'),
('Explorer Probe', 40, 38, 'AVAILABLE'),
('Extraction Forceps', 30, 28, 'AVAILABLE'),
('Sterile Latex Gloves (Box)', 100, 15, 'LOW_STOCK'),
('Anesthetic Syringes', 80, 0, 'OUT_OF_STOCK'),
('Surgical Scalpel Handle', 25, 22, 'AVAILABLE');
