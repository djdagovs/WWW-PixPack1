--template
CREATE TABLE image.template (
	id serial NOT NULL,
	name varchar(64) NOT NULL,
	title varchar(250) NOT NULL,
	author varchar(250) NULL,
	description text NULL,
	timestamp char(17) NOT NULL DEFAULT '00000000000000000',
	styles varchar(250),
	type int2 NOT NULL DEFAULT 0,
	key_parent int4 NOT NULL DEFAULT 4,
	FOREIGN KEY (key_parent) REFERENCES image.account (id),
	PRIMARY KEY (id)
);
GRANT ALL ON image.template TO image;
GRANT ALL ON image.template_id_seq TO image;

INSERT INTO image.template (name, title, author, description, key_parent) VALUES ('xml', 'XML', 'PixPack', 'Unfiltered XML result', 1);