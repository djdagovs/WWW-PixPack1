--Log
CREATE TABLE image.log (
	id serial NOT NULL,
	status int2 NOT NULL DEFAULT 0,
	timestamp char(17) NOT NULL,
	message text,
	comment varchar(250),
	last char(17) NOT NULL,
	PRIMARY KEY (id)
);

GRANT ALL ON image.log TO image;
GRANT ALL ON image.log_id_seq TO image;
