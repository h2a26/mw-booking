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

-- Packages Table
CREATE TABLE IF NOT EXISTS packages (
                                        package_id SERIAL PRIMARY KEY,
                                        package_name VARCHAR(255) NOT NULL,
    total_credits INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    expiry_days INT NOT NULL,
    country VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'System',
    updated_by VARCHAR(255) DEFAULT 'System',
    version BIGINT DEFAULT 0 NOT NULL,
    CONSTRAINT chk_expiry_days CHECK (expiry_days > 0)
);


-- User Packages Table (Mapping between users and packages)
CREATE TABLE IF NOT EXISTS user_packages (
                                             user_package_id SERIAL PRIMARY KEY,
                                             user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    package_id INT NOT NULL REFERENCES packages(package_id) ON DELETE CASCADE,
    remaining_credits INT NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    expiration_date TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'System',
    updated_by VARCHAR(255) DEFAULT 'System',
    version BIGINT DEFAULT 0 NOT NULL,
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'EXPIRED'))
);

-- Indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_user_packages_user_id ON user_packages(user_id);
CREATE INDEX IF NOT EXISTS idx_user_packages_package_id ON user_packages(package_id);


-- Payments Table (Tracking user payments)
CREATE TABLE IF NOT EXISTS payments (
                                        payment_id SERIAL PRIMARY KEY,
                                        user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'COMPLETED',
    payment_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'System',
    updated_by VARCHAR(255) DEFAULT 'System',
    version BIGINT DEFAULT 0 NOT NULL,
    CONSTRAINT chk_payment_status CHECK (status IN ('COMPLETED', 'FAILED'))
    );

-- Index for performance on payment queries
CREATE INDEX IF NOT EXISTS idx_payments_user_id ON payments(user_id);

