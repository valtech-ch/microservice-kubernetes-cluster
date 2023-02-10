/* user table */
CREATE TABLE user (
    email VARCHAR(100) NOT NULL,
    PRIMARY KEY (email)
);

/* message table */
CREATE TABLE message (
    key_id VARCHAR(100) NOT NULL,
    value VARCHAR(250),
    PRIMARY KEY (key_id)
);

/* auditing table */
CREATE TABLE auditing (
    id IDENTITY,
    modification_date DATE,
    user_email VARCHAR(100),
    message_key_id VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT fk_auditing_user foreign key (user_email) references user (email) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_auditing_message foreign key (message_key_id) references message (key_id) ON DELETE NO ACTION ON UPDATE NO ACTION
);
