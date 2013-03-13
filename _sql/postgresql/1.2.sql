--Image@ExDaCe
--TSearch2 has been build

--login

\c exdace

--UserManager-Table
CREATE TABLE image.account (
	id serial NOT NULL,
	name varchar(128) NOT NULL UNIQUE,
	password varchar(64) NULL,
	mail varchar(128) NOT NULL UNIQUE,
	status int2 NOT NULL DEFAULT 0,
	timestamp char(17) NOT NULL DEFAULT '00000000000000000',
	last char(17) NOT NULL DEFAULT '00000000000000000',
	key_parent integer NOT NULL DEFAULT 4,
	FOREIGN KEY (key_parent) REFERENCES image.account (id),
	PRIMARY KEY (id)
);
CREATE INDEX account_user_idx ON image.account (name);
CREATE INDEX account_parent_idx ON image.account (key_parent);

--INSERT admin, guest & anonymous
INSERT INTO image.account (id, name, password, status, key_parent) VALUES (1, 'system', 'ipL1zUc9mN8=', 9, 1);
INSERT INTO image.account (id, name, password, status, key_parent) VALUES (2, 'admin', 'gmqWLKDCw1g=', 8, 1);
INSERT INTO image.account (id, name, password, status, key_parent) VALUES (3, 'anonymous', '8vKlrC5dUsY/x5IOVLowpg==', 1, 1);
INSERT INTO image.account (id, name, password, status, key_parent) VALUES (4, 'free', 'qINQRsD4ZVA=', 1, 1);
INSERT INTO image.account (id, name, password, status, key_parent) VALUES (5, 'pay', '44Kp3jHHViM=', 2, 1);
INSERT INTO image.account (id, name, password, status, key_parent) VALUES (6, 'business', 'ZaJ/eckXNOc/ykGICGdn8A==', 3, 1);

--Folder
CREATE TABLE image.folder (
	id serial NOT NULL,
	name varchar(64),
	key_user integer NOT NULL DEFAULT 3,
	PRIMARY KEY (id)
);
--CREATE INDEX folder_user_idx ON image.folder (key_user);

INSERT INTO image.folder (name) VALUES('animal');
INSERT INTO image.folder (name) VALUES('animation');
INSERT INTO image.folder (name) VALUES('art');
INSERT INTO image.folder (name) VALUES('cartoon');
INSERT INTO image.folder (name) VALUES('crash');
INSERT INTO image.folder (name) VALUES('dance');
INSERT INTO image.folder (name) VALUES('entertainment');
INSERT INTO image.folder (name) VALUES('fun');
INSERT INTO image.folder (name) VALUES('game');
INSERT INTO image.folder (name) VALUES('music');
INSERT INTO image.folder (name) VALUES('news');
INSERT INTO image.folder (name) VALUES('nature');
INSERT INTO image.folder (name) VALUES('people');
INSERT INTO image.folder (name) VALUES('science');
INSERT INTO image.folder (name) VALUES('school');
INSERT INTO image.folder (name) VALUES('sport');
INSERT INTO image.folder (name) VALUES('sex');
INSERT INTO image.folder (name) VALUES('technology');
INSERT INTO image.folder (name) VALUES('travel');
INSERT INTO image.folder (name) VALUES('video');
INSERT INTO image.folder (name) VALUES('vehicle');
INSERT INTO image.folder (name) VALUES('work');

--Extension
CREATE TABLE image.extension (
	id serial NOT NULL,
	name char(3) NOT NULL UNIQUE,
	mimetype varchar(30) NOT NULL UNIQUE,
	description varchar(250),
	write boolean NOT NULL DEFAULT false,
	web boolean NOT NULL DEFAULT false,
	PRIMARY KEY (id)
);
CREATE INDEX extension_name_idx ON image.extension (name);

INSERT INTO image.extension (name, mimetype, description, write, web, write, web) VALUES ('art','image/art','PFS: 1st Publisher',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('avs','image/avs','AVS X image',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('bmp','image/bmp','Microsoft Windows bitmap',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('cin','image/cin','Kodak Cineon Image Format',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('cur','image/x-win-bitmap','Microsoft Cursor Icon',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('cut','image/cut','DR Halo',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('dcm','image/dcm','Digital Imaging and Communications in Medicine (DICOM) image',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('dcx','image/dcx','ZSoft IBM PC multi-page Paintbrush image',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('dib','image/dib','Microsoft Windows Device Independent Bitmap',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('dng','image/dng','Digital Negative',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('dot','image/dot','Graph Visualization',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('dpx','image/dpx','SMPTE Digital Moving Picture Exchange',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('emf','image/emf','Microsoft Enhanced Metafile (32-bit)',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('fax','image/fax','Group 3 TIFF',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('gif','image/gif','CompuServe Graphics Interchange Format',true,true);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('ico','image/vnd.microsoft.icon','Microsoft icon',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('jng','image/jng','Multiple-image Network Graphics',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('jp2','image/jp2','JPEG-2000 JP2 File Format Syntax',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('jpc','image/jpc','JPEG-2000 Code Stream Syntax',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('jpe','image/jpeg','Joint Photographic Experts Group JFIF format',true,true);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('jpg','image/jpeg','Joint Photographic Experts Group JFIF format',true,true);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('mif','image/miff','Magick image file format',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('mat','image/mat','MATLAB image format',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('mng','image/mng','Multiple-image Network Graphics',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('mpc','image/mpc','Magick Persistent Cache image file format',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('mtv','image/mtv','MTV Raytracing image format',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('mvg','image/mvg','Magick Vector Graphics',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('otb','image/otb','On-the-air Bitmap',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('pbm','image/pbm','Portable bitmap format (black and white)',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('pcd','image/pcd','Photo CD',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('pcx','image/pcx','ZSoft IBM PC Paintbrush file',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('pdb','image/pdb','Palm Database ImageViewer Format',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('pix','image/pix','Alias/Wavefront RLE image format',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('png','image/png','Portable Network Graphics',true,true);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('pnm','image/pnm','Portable anymap',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('ppm','image/ppm','Portable pixmap format (color)',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('psd','application/x-photoshop','Adobe Photoshop bitmap file',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('pwp','image/pwp','Seattle File Works multi-image file',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('rad','image/rad','Radiance image file',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('rla','image/rla','Alias/Wavefront image file',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('rle','image/rle','Utah Run length encoded image file',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('sct','image/sct','Scitex Continuous Tone Picture',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('sfw','image/sfw','Seattle File Works image',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('sgi','image/sgi','Irix RGB image',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('sun','image/sun','SUN Rasterfile',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('svg','image/svg+xml','Scalable Vector Graphics',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('tga','image/tga','Truevision Targa image',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('tif','image/tiff','Tagged Image File Format',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('wmf','image/x-wmf','Windows Metafile',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('wpg','image/wpg','Word Perfect Graphics File',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('xbm','image/x-xbm','X Windows system bitmap, black and white only',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('xcf','image/xcf','GIMP image',false,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('xpm','image/x-xpm','X Windows system pixmap',true,false);
INSERT INTO image.extension (name, mimetype, description, write, web) VALUES ('xwd','image/x-xwd','X Windows system window dump',true,false);


--File
CREATE TABLE image.file (
	id serial NOT NULL,
	server int2 NOT NULL DEFAULT -1,
	name varchar(128) NOT NULL,
	timestamp char(17) NOT NULL,
	key char(10) NOT NULL,
	last char(17) NOT NULL DEFAULT '00000000000000000',
	count int8 NOT NULL DEFAULT 0,
	description text,
	public boolean NOT NULL DEFAULT true,
	adult boolean NOT NULL DEFAULT false,
	status int2 NOT NULL DEFAULT 0,
	ip varchar(15) NOT NULL DEFAULT '0.0.0.0',
	width int2 NOT NULL DEFAULT 0,
	height int2 NOT NULL DEFAULT 0,
	size int NOT NULL DEFAULT 0,
	protect varchar(250),
	vote_count int4 NOT NULL DEFAULT 0,
	vote_sum int4 NOT NULL DEFAULT 0,
	vote_ip inet NOT NULL DEFAULT '0.0.0.0'::inet,
	key_user integer NOT NULL DEFAULT 3,
	key_folder integer NOT NULL DEFAULT -1,
	key_extension integer NOT NULL,
	FOREIGN KEY (key_user) REFERENCES image.account (id),
	FOREIGN KEY (key_extension) REFERENCES image.extension (id),
	PRIMARY KEY (id)
);
--ALTER & UPDATE table
ALTER TABLE image.file ADD COLUMN vector tsvector;
--CREATE INDEX
CREATE INDEX file_filename_idx ON image.file (timestamp, key);
CREATE INDEX file_user_idx ON image.file (key_user);
CREATE INDEX file_folder_idx ON image.file (key_folder);
CREATE INDEX file_vector_idx ON image.file USING gist(vector);
