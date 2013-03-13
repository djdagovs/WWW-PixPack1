--gallery
CREATE TABLE image.gallery (
	id serial NOT NULL,
	timestamp char(17) NOT NULL DEFAULT '00000000000000000',
	key char(10) NOT NULL,
	title varchar(250) NULL,
	description text NULL,
	col int2 NULL,
	row int2 NULL,
	sort_timestamp boolean NOT NULL DEFAULT false,
	sort_order boolean NOT NULL DEFAULT false,
	subfolder boolean NOT NULL DEFAULT false,
	style varchar(16) NULL,
	last char(17) NOT NULL DEFAULT '00000000000000000',
	count int4 NOT NULL DEFAULT 0,
	key_folder int4 NOT NULL,
	key_template int4 NOT NULL,
	FOREIGN KEY (key_folder) REFERENCES image.folder (id),
	FOREIGN KEY (key_template) REFERENCES image.template (id),
	PRIMARY KEY (id)
);

--timestamp_key=unique id
CREATE INDEX gallery_id_idx ON image.gallery (timestamp, key);

GRANT ALL ON image.gallery TO image;
GRANT ALL ON image.gallery_id_seq TO image;
