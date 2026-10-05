USE mobile_recharge_db;

INSERT INTO CUSTOMERS (full_name, email, address) VALUES
('Monika M', 'monika@example.com', 'Chennai, Tamil Nadu'),
('Priyadharshini K', 'priya@example.com', 'Chennai, Tamil Nadu'),
('Arun Kumar', 'arun@example.com', 'Coimbatore, Tamil Nadu'),
('Di    vya S', 'divya@example.com', 'Madurai, Tamil Nadu'),
('Karthik R', 'karthik@example.com', 'Salem, Tamil Nadu');

INSERT INTO MOBILE_NUMBERS
(customer_id, mobile_number, operator_name, circle_name, status)
VALUES
(1, '9876543210', 'Jio', 'Tamil Nadu', 'EXPIRED'),
(2, '9876501234', 'Airtel', 'Tamil Nadu', 'EXPIRED'),
(3, '9123456789', 'Vi', 'Tamil Nadu', 'EXPIRED'),
(4, '9000011111', 'Jio', 'Tamil Nadu', 'EXPIRED'),
(5, '9000022222', 'Airtel', 'Tamil Nadu', 'EXPIRED');

INSERT INTO PLANS
(plan_name, operator_name, price, validity_days, data_gb, voice_minutes, sms_count, description)
VALUES
('Jio Daily 1.5GB', 'Jio', 299.00, 28, 42.00, 1000, 100, '1.5GB/day + unlimited calls'),
('Jio Super 2GB', 'Jio', 349.00, 28, 56.00, 1000, 100, '2GB/day + unlimited calls'),
('Airtel Smart 1.5GB', 'Airtel', 299.00, 28, 42.00, 1000, 100, '1.5GB/day + unlimited calls'),
('Airtel Max 2.5GB', 'Airtel', 549.00, 56, 140.00, 3000, 100, '2.5GB/day + unlimited calls'),
('Vi Hero 1.5GB', 'Vi', 299.00, 28, 42.00, 1000, 100, '1.5GB/day + unlimited calls'),
('Vi Long Validity', 'Vi', 999.00, 84, 126.00, 3000, 300, 'Long validity plan');

-- Initial recharges are inserted through the required procedure.
CALL perform_recharge(1, 1, 'UPI', 'TXN-DEMO-1001');
CALL perform_recharge(1, 1, 'UPI', 'TXN-DEMO-1002');
CALL perform_recharge(1, 2, 'CARD', 'TXN-DEMO-1003');
CALL perform_recharge(2, 3, 'UPI', 'TXN-DEMO-1004');
CALL perform_recharge(2, 3, 'WALLET', 'TXN-DEMO-1005');
CALL perform_recharge(2, 3, 'UPI', 'TXN-DEMO-1006');
CALL perform_recharge(3, 5, 'CARD', 'TXN-DEMO-1007');
CALL perform_recharge(4, 1, 'UPI', 'TXN-DEMO-1008');
CALL perform_recharge(5, 4, 'NET_BANKING', 'TXN-DEMO-1009');
