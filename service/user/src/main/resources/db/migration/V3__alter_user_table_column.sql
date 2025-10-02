ALTER TABLE users
CHANGE encoded_password password VARCHAR(255) NOT NULL;

ALTER TABLE users
CHANGE phone_number phone VARCHAR(20) DEFAULT NULL;


