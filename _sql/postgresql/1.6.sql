--Comment
CREATE TABLE image.comment (
	id serial NOT NULL,
	text text NOT NULL,
	timestamp char(17) NOT NULL,
	key_file int4 NOT NULL,
	key_user int4 NULL NULL,
	user_str varchar(64) DEFAULT NULL,
	UNIQUE (text, key_file, key_user),
	FOREIGN KEY (key_file) REFERENCES image.file (id),
	FOREIGN KEY (key_user) REFERENCES image.account (id),
	PRIMARY KEY (id)
);

CREATE INDEX comment_file_idx ON image.comment (key_file);

GRANT ALL ON image.comment TO image;
GRANT ALL ON image.comment_id_seq TO image;
