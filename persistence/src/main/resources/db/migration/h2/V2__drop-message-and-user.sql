/* Drop foreign keys and rename existing auditing columns */
ALTER TABLE auditing DROP CONSTRAINT fk_auditing_user;
ALTER TABLE auditing DROP CONSTRAINT fk_auditing_message;
ALTER TABLE auditing RENAME COLUMN user_email TO username;
ALTER TABLE auditing RENAME COLUMN message_key_id TO filename;
ALTER TABLE auditing ADD COLUMN action VARCHAR(100) NOT NULL;

/* Drop message and user tables */
DROP TABLE message;
DROP TABLE user;