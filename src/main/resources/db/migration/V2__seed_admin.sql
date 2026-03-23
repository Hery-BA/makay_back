-- V2__seed_admin.sql
-- Mot de passe par défaut: Admin@Makay2025!
-- À changer impérativement en production
INSERT INTO admin_users (email, password, full_name, role) VALUES (
                                                                      'admin@makay-adventures.com',
                                                                      '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQyCgKkYuGJ0RU6VQzFkIlTHy',
                                                                      'Wonder Mum of 4',
                                                                      'ADMIN'
                                                                  );
