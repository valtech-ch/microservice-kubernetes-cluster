SET @filename = 'test.txt';
SET @email = 'info@valtech.com';

INSERT INTO auditing VALUES (1, now(), @email, @filename, 0);
INSERT INTO auditing VALUES (2, now(), @email, @filename, 1);
INSERT INTO auditing VALUES (3, now(), @email, @filename, 2);