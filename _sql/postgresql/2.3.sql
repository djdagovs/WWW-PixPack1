--preview
CREATE TABLE image.preview (
	id serial NOT NULL,
	timestamp char(17) NOT NULL DEFAULT '00000000000000000',
	key char(10) NOT NULL,
	link varchar(250) NULL,
	description text NULL,
	allfolder boolean NOT NULL DEFAULT false,
	subfolder boolean NOT NULL DEFAULT false,
	window boolean NOT NULL DEFAULT false,
	last char(17) NOT NULL DEFAULT '00000000000000000',
	count int4 NOT NULL DEFAULT 0,
	key_folder int4 NOT NULL,
	FOREIGN KEY (key_folder) REFERENCES image.folder (id),
	PRIMARY KEY (id)
);

--timestamp_key=unique id
CREATE INDEX preview_id_idx ON image.gallery (timestamp, key);

GRANT ALL ON image.preview TO image;
GRANT ALL ON image.preview_id_seq TO image;
