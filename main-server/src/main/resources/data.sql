DELETE FROM comments;
DELETE FROM users;
DELETE FROM categories;
DELETE FROM locations;
DELETE FROM events;

INSERT INTO users (name, email) VALUES ('SpiderMan', 'spider_man@gmail.com');
INSERT INTO users (name, email) VALUES ('IronMan', 'iron_man@gmail.com');

INSERT INTO categories (name) VALUES ('NewYork');

INSERT INTO locations (lat, lon) VALUES ('40.42', '74.00');

INSERT INTO events (annotation, category_id, created_on, description, event_date, initiator_id, location_id, paid,
                    participant_limit, request_moderation, title)
VALUES ('полет по центральному парку', 1, '2025-03-26 23:14:00', 'только супер герои',
        '2025-05-03 15:00:00', 2, 1, 'false', 0, 'true', 'полет');
INSERT INTO events (annotation, category_id, created_on, description, event_date, initiator_id, location_id, paid,
                    participant_limit, request_moderation, title)
VALUES ('битва с Таносом', 1, '2019-03-26 23:14:00', 'драка ну кулаках с титаном',
        '2025-04-24 11:00:00', 2, 1, 'false', 0, 'true', 'драка');

UPDATE events SET state = 'PUBLISHED' WHERE id = 1;