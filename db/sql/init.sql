USE challenge;

CREATE TABLE test(col VARCHAR(10));

INSERT INTO test(col) VALUES('ok');


CREATE TABLE users
(
	id int NOT NULL AUTO_INCREMENT,
	username varchar(255) NOT NULL,
	password varchar(255) NOT NULL,
	creationDate datetime NOT NULL DEFAULT GETDATE(),
	CONSTRAINT PK_users PRIMARY KEY (id),
	CONSTRAINT UQ_users_username UNIQUE (Username)
);

CREATE TABLE messageTypes
(
	id int NOT NULL AUTO_INCREMENT,
	name varchar(255) NOT NULL,
	CONSTRAINT PK_messageTypes PRIMARY KEY (Id)
);

INSERT INTO messageTypes
	(name)
VALUES
	('text'), ('image'), ('video');


CREATE TABLE messages
(
	id int NOT NULL AUTO_INCREMENT,
	senderId int NOT NULL,
	recipientId int NOT NULL,
	messageTypeId int NOT NULL,
	content varchar(255) NOT NULL,
	creationTime datetime NOT NULL DEFAULT GETDATE(),
	CONSTRAINT PK_messageTypes PRIMARY KEY (id),
	CONSTRAINT FK_messageTypes_senderId FOREIGN KEY (senderId) REFERENCES users(id),
	CONSTRAINT FK_messageTypes_recipientId FOREIGN KEY (recipientId) REFERENCES users(id),
	CONSTRAINT FK_messageTypes_messageTypeId FOREIGN KEY (messageTypeId) REFERENCES messageTypes(id)
);


CREATE TABLE imageMetadata
(
	messageId int NOT NULL,
	width int NOT NULL,
	length int NOT NULL,
	CONSTRAINT PK_imageMetadata PRIMARY KEY (messageId),
	CONSTRAINT FK_imageMetadata_messageId FOREIGN KEY (messageId) REFERENCES messages(id)
);


CREATE TABLE videoMetadata
(
	messageId int NOT NULL,
	length int NOT NULL,
	source varchar,
	CONSTRAINT PK_videoMetadata PRIMARY KEY (messageId),
	CONSTRAINT FK_videoMetadata_messageId FOREIGN KEY (messageId) REFERENCES messages(id)
);
