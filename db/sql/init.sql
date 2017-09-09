USE challenge;

CREATE TABLE test(col VARCHAR(10));

INSERT INTO test(col) VALUES('ok');


CREATE TABLE users
(
	id int NOT NULL AUTO_INCREMENT,
	username varchar(255) NOT NULL,
	password varchar(255) NOT NULL,
	CONSTRAINT PK_users PRIMARY KEY (id),
	CONSTRAINT UQ_users_username UNIQUE (Username)
);
