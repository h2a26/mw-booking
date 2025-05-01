DROP TABLE IF EXISTS users, packages, user_packages, classes, bookings, waitlists, refunds, payments, business CASCADE;


-- Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,                               
    username VARCHAR(255) NOT NULL UNIQUE,                     
    email VARCHAR(255) NOT NULL UNIQUE,                       
    password VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,        
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'System',
    updated_by VARCHAR(255) DEFAULT 'System',
    version BIGINT DEFAULT 0 NOT NULL,
    CONSTRAINT email_unique UNIQUE(email),
	CONSTRAINT chk_is_verified CHECK (is_verified IN (FALSE, TRUE))
);

CREATE TABLE IF NOT EXISTS roles (
     role_id SERIAL PRIMARY KEY,
     name VARCHAR(255) NOT NULL UNIQUE
);

-- Permissions Table
CREATE TABLE IF NOT EXISTS permissions (
    permission_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- User Roles Table (Many-to-Many between users and roles)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

-- Role Permissions Table (Many-to-Many between roles and permissions)
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);

-- Index for quick email verification checks
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Index for role name
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles(name);