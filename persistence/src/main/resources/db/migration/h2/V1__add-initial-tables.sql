/* user table */
CREATE TABLE user (
    email VARCHAR(100) NOT NULL,
    PRIMARY KEY (email)
);

/* message table */
CREATE TABLE message (
    keyId VARCHAR(100) NOT NULL,
    value VARCHAR(250),
    PRIMARY KEY (keyId)
);

/* auditing table */
CREATE TABLE auditing (
    id SERIAL,
    modificationDate DATE,
    user VARCHAR(100),
    message VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT fk_auditing_user foreign key (user) references user (email) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_auditing_message foreign key (message) references message (keyId) ON DELETE NO ACTION ON UPDATE NO ACTION
);