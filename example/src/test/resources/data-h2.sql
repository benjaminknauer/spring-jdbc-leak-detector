-- Sample data for integration tests

INSERT INTO users (name, email) VALUES
    ('Alice Johnson', 'alice@example.com'),
    ('Bob Smith', 'bob@example.com'),
    ('Charlie Brown', 'charlie@example.com');

INSERT INTO orders (user_id, amount, status) VALUES
    (1, 99.99, 'completed'),
    (1, 149.50, 'pending'),
    (2, 299.00, 'completed'),
    (3, 49.99, 'shipped');
