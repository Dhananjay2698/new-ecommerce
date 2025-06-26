-- Test data for orders table
INSERT INTO orders (customer_id, status, ordered_date, updated_date) VALUES
(1, 'PENDING', CURRENT_TIMESTAMP, NULL),
(2, 'COMPLETED', CURRENT_TIMESTAMP, NULL);

-- Test data for order_items table
INSERT INTO order_items (order_id, product_id) VALUES
(1, 1),
(1, 2),
(2, 3); 