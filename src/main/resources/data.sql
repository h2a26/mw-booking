-- Inserting Users
INSERT INTO users (username, email, password, country, is_verified, version) VALUES
('john_doe', 'john.doe@example.com', '$2a$10$ugZ7uS9B.sFAK4J0kuw6puAp3q5a5aIG2vc/aMlwNq/JCGSRbZeD6', 'SG',TRUE, 0),
('jane_smith', 'jane.smith@example.com', '$2a$10$ugZ7uS9B.sFAK4J0kuw6puAp3q5a5aIG2vc/aMlwNq/JCGSRbZeD6', 'MM',FALSE, 0);


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