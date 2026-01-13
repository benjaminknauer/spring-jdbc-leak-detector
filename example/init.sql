-- Initialize example database with sample data

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO users (name, email) VALUES
    ('Alice Johnson', 'alice@example.com'),
    ('Bob Smith', 'bob@example.com'),
    ('Charlie Brown', 'charlie@example.com'),
    ('Diana Prince', 'diana@example.com'),
    ('Eve Wilson', 'eve@example.com');

INSERT INTO orders (user_id, amount, status) VALUES
    (1, 99.99, 'completed'),
    (1, 149.50, 'pending'),
    (2, 299.00, 'completed'),
    (3, 49.99, 'shipped'),
    (4, 199.99, 'pending'),
    (5, 79.99, 'completed');
