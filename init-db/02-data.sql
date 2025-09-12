SELECT 'CREATE DATABASE bookingsystem' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'bookingsystem');

-- Insert sample countries
INSERT INTO country  (id,name, code, currency, created_at)
VALUES
(1,'Singapore', 'SG', 'SGD', NOW()),
(2,'Myanmar', 'MM', 'MMK', NOW()),
(3,'Japan','JP','YEN',NOW()),
(4,'Thailand', 'TH', 'THB', NOW())
ON CONFLICT (id) DO NOTHING;

--Insert sample packages
INSERT INTO packages (id,package_name, credits, prices, expired_date, status, is_active, country_id, created_at) VALUES
(1,'Basic Package SG', 10, 29.99, '2025-12-31', 'ACTIVE', true, 1, NOW()),
(2,'Premium Package SG', 25, 69.99, '2025-12-31', 'ACTIVE', true, 1, NOW()),
(3,'Standard Package MM', 15, 49.99, '2025-12-31', 'ACTIVE', true, 2, NOW()),
(4,'Gold Package JP', 50, 149.99, '2025-12-31', 'ACTIVE', true, 3, NOW()),
(5,'Basic Package TH', 25, 56.99, '2025-12-31', 'ACTIVE', true, 4, NOW())
ON CONFLICT (id) DO NOTHING;

 --Insert sample classes
 INSERT INTO classes (
     id,
     class_name,
     start_time,
     end_time,
     course_duration,
     required_credits,
     max_capacity,
     is_completed,
     created_at,
     country_id
 ) VALUES
 (1, 'Mathematics 101', '09:00:00', '10:30:00', 90, 3, 30, false, '2024-01-15 08:00:00', 1),
 (2, 'Physics Advanced', '11:00:00', '12:30:00', 90, 4, 25, false, '2024-01-15 08:05:00', 1),
 (3, 'Chemistry Lab', '14:00:00', '16:00:00', 120, 2, 20, true, '2024-01-10 09:00:00', 2),
 (4, 'Computer Science', '10:00:00', '11:30:00', 90, 3, 35, false, '2024-01-16 07:30:00', 3),
 (5, 'English Literature', '13:00:00', '14:30:00', 90, 3, 28, false, '2024-01-16 08:15:00', 4),
 (6, 'History of Art', '15:00:00', '16:30:00', 90, 2, 22, true, '2024-01-12 10:00:00', 2),
 (7, 'Biology Seminar', '08:30:00', '10:00:00', 90, 4, 18, false, '2024-01-17 06:45:00', 4),
 (8, 'Economics Theory', '16:00:00', '17:30:00', 90, 3, 32, false, '2024-01-17 09:20:00', 3)
 ON CONFLICT (id) DO NOTHING;