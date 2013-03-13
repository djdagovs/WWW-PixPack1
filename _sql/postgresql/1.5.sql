--contact
CREATE TABLE image.contact (
	id serial NOT NULL,
	scope varchar(32) NULL,
	name varchar(128) NULL,
	subject varchar(128) NULL,
	message text NOT NULL,
	mail varchar(128) NULL,
	username varchar(128) NULL,
	timestamp char(17) NOT NULL DEFAULT '00000000000000000',
	language char(2) NOT NULL,
	status int2 NOT NULL default 0,
	PRIMARY KEY (id)
);
GRANT ALL ON image.contact TO image;
GRANT ALL ON image.contact_id_seq TO image;
