-- Inserting Users
INSERT INTO users (username, email, password, country, is_verified) VALUES
('john_doe', 'john.doe@example.com', '$2a$10$ugZ7uS9B.sFAK4J0kuw6puAp3q5a5aIG2vc/aMlwNq/JCGSRbZeD6', 'SG',TRUE),
('jane_smith', 'jane.smith@example.com', '$2a$10$ugZ7uS9B.sFAK4J0kuw6puAp3q5a5aIG2vc/aMlwNq/JCGSRbZeD6', 'MM',FALSE);


INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');

-- Insert sample Permissions
INSERT INTO permissions (name) VALUES ('MANAGE_PACKAGES');
INSERT INTO permissions (name) VALUES ('MANAGE_CLASSES');


-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);

INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 1);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 2);

-- Inserting Packages
INSERT INTO packages (package_name, total_credits, price, expiry_days, country) VALUES
('Basic Plan', 10, 99.99, 30, 'SG'),
('Premium Plan', 20, 199.99, 60, 'SG'),
('Standard Plan', 15, 149.99, 45, 'MM');

-- Inserting User Packages
INSERT INTO user_packages (user_id, package_id, remaining_credits, status, expiration_date) VALUES
(1, 1, 10, 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '30 days'),
(2, 2, 20, 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '60 days');


-- Inserting Payments
INSERT INTO payments (user_id, amount, status) VALUES
                                                   (1, 99.99, 'COMPLETED'),
                                                   (2, 149.99, 'FAILED');
