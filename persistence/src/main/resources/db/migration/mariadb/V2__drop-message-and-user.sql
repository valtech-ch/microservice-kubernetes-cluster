/* Drop foreign keys and rename existing auditing columns */
ALTER TABLE auditing DROP CONSTRAINT fk_auditing_user;
ALTER TABLE auditing DROP CONSTRAINT fk_auditing_message;
ALTER TABLE auditing CHANGE COLUMN user_email username VARCHAR(100);
ALTER TABLE auditing CHANGE COLUMN message_key_id filename VARCHAR(100);
ALTER TABLE auditing ADD COLUMN action VARCHAR(100) NOT NULL;

/* Drop message and user tables */
DROP TABLE message;
DROP TABLE user;