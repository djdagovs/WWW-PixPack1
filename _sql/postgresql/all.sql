\c template1

--re-create database
DROP DATABASE exdace;
DROP USER exdace;
CREATE USER exdace PASSWORD 'exdace';
CREATE DATABASE exdace WITH ENCODING='UTF8' OWNER exdace;

--change database
\c exdace

--revoke privileges from all users
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO exdace;

--logout
--\q
--ExDaCe-Import

--psql template1 -U pgsql
\c exdace

\i tsearch2.sql

--logout
--\q
--Image@ExDaCe
--TSearch2 has been build

--login

\c exdace

DROP SCHEMA image CASCADE;
REVOKE ALL ON pg_ts_cfg FROM image;
REVOKE ALL ON pg_ts_cfgmap FROM image;
REVOKE ALL ON pg_ts_dict FROM image;
REVOKE ALL ON pg_ts_parser FROM image;
DROP USER image;

CREATE USER image PASSWORD 'image';
CREATE SCHEMA image;
ALTER SCHEMA image OWNER TO image;
--Image@ExDaCe
--TSearch2 has been build

--login

\c exdace

--UserManager-Table
CREATE TABLE image.account (
	id serial NOT NULL,
	name varchar(128) NOT NULL UNIQUE,
	password varchar(64) NULL,
	status int2 NOT NULL DEFAULT 0,
	timestamp char(17) NOT NULL DEFAULT '00000000000000000',
	PRIMARY KEY (id)
);
CREATE INDEX account_user_idx ON image.account (name);

--INSERT admin, guest & anonymous
INSERT INTO image.account (name, password, status) VALUES ('admin','admin',1);
--guest & anonymous ar created but just placeholder (NULL password)
INSERT INTO image.account (name, status) VALUES ('guest',1);
INSERT INTO image.account (name, status) VALUES ('anonymous',1);
INSERT INTO image.account (name, status) VALUES ('system',1);

--Folder
CREATE TABLE image.folder (
	id serial NOT NULL,
	name varchar(64),
	key_user integer NOT NULL DEFAULT 3,
	PRIMARY KEY (id)
);

--Extension
CREATE TABLE image.extension (
	id serial NOT NULL,
	name char(3) NOT NULL UNIQUE,
	mimetype varchar(30) NOT NULL UNIQUE,
	description varchar(250),
	PRIMARY KEY (id)
);
CREATE INDEX extension_name_idx ON image.extension (name);

INSERT INTO image.extension (name, mimetype, description) VALUES ('jpg','image/jpeg','Joint Photographic Experts Group');
INSERT INTO image.extension (name, mimetype, description) VALUES ('png','image/png','Portable Network Graphics');
INSERT INTO image.extension (name, mimetype, description) VALUES ('gif','image/gif','Graphics Interchange Format');
INSERT INTO image.extension (name, mimetype, description) VALUES ('tif','image/tiff','Tagged Image File Format');
INSERT INTO image.extension (name, mimetype, description) VALUES ('psd','application/x-photoshop','Photoshop Document');
INSERT INTO image.extension (name, mimetype, description) VALUES ('bmp','image/bmp','Windows Bitmap');
INSERT INTO image.extension (name, mimetype, description) VALUES ('pcx','image/pcx','Pacific Exchange');
INSERT INTO image.extension (name, mimetype, description) VALUES ('pic','image/pict','Picture');
INSERT INTO image.extension (name, mimetype, description) VALUES ('ico','image/vnd.microsoft.icon','Icon');
INSERT INTO image.extension (name, mimetype, description) VALUES ('cur','image/x-win-bitmap','Windows Cursor');
INSERT INTO image.extension (name, mimetype, description) VALUES ('xbm','image/x-xbm','X Window System Bitmap');
INSERT INTO image.extension (name, mimetype, description) VALUES ('xpm','image/x-xpm','X Window System Pixmap');
INSERT INTO image.extension (name, mimetype, description) VALUES ('tga','image/tga','Truevision TGA');
INSERT INTO image.extension (name, mimetype, description) VALUES ('svg','image/svg+xml','Small Web Format');
INSERT INTO image.extension (name, mimetype, description) VALUES ('swf','application/x-shockwave-flash','Scalable Vector Graphics');
INSERT INTO image.extension (name, mimetype, description) VALUES ('wmf','image/x-wmf','Windows Metafile');


--File
CREATE TABLE image.file (
	id serial NOT NULL,
	server int2 NOT NULL DEFAULT -1,
	name varchar(128) NOT NULL,
	timestamp char(17) NOT NULL,
	key char(10) NOT NULL,
	last char(17) NOT NULL,
	count int8 NOT NULL DEFAULT 0,
	key_user integer NOT NULL DEFAULT 3,
	key_folder integer NOT NULL DEFAULT -1,
	key_extension integer NOT NULL,
	description text,
	public boolean NOT NULL DEFAULT true,
	adult boolean NOT NULL DEFAULT false,
	status int2 NOT NULL DEFAULT 0,
	ip varchar(15) NOT NULL DEFAULT '0.0.0.0',
	FOREIGN KEY (key_user) REFERENCES image.account (id),
	FOREIGN KEY (key_extension) REFERENCES image.extension (id),
	PRIMARY KEY (id)
);
--ALTER & UPDATE table
ALTER TABLE image.file ADD COLUMN vector tsvector;
--CREATE INDEX
CREATE INDEX file_vector_idx ON image.file USING gist(vector);
CREATE INDEX file_filename_idx ON image.file (timestamp, key);

--Voting
ALTER TABLE image.file ADD COLUMN addon_vote_count int8 NOT NULL DEFAULT 0;
ALTER TABLE image.file ADD COLUMN addon_vote_sum int8 NOT NULL DEFAULT 0;


--Voting - application
--UPDATE vote SET addon_vote_count=(SELECT addon_vote_count FROM image.file WHERE id=1)+1, sum=(SELECT addon_vote_sum FROM image.file WHERE id=1)+9 WHERE id=1;
--SELECT sum/count AS average FROM image.file;
--Image@ExDaCe
--TSearch2 has been build

--login

\c exdace

--Grant Database
GRANT ALL ON SCHEMA public TO image;

GRANT ALL ON image.account TO image;
GRANT ALL ON image.account_id_seq TO image;
GRANT ALL ON image.folder TO image;
GRANT ALL ON image.folder_id_seq TO image;
GRANT ALL ON image.extension TO image;
GRANT ALL ON image.extension_id_seq TO image;
GRANT ALL ON image.file TO image;
GRANT ALL ON image.file_id_seq TO image;


--TSearch 2 install (tsearch2 have to be installed at public)
--based on: http://www.sai.msu.su/~megera/postgres/gist/tsearch/V2/docs/tsearch-V2-intro.html

--GRANT VECTOR
GRANT SELECT ON pg_ts_cfg TO image;
GRANT SELECT ON pg_ts_cfgmap TO image;
GRANT SELECT ON pg_ts_dict TO image;
GRANT SELECT ON pg_ts_parser TO image;

