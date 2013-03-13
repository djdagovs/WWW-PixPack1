--Extension
CREATE TABLE image.extension (
	id serial NOT NULL,
	name char(3) NOT NULL UNIQUE,
	mimetype varchar(30) NOT NULL UNIQUE,
	description varchar(250),
	PRIMARY KEY (id)
);
CREATE INDEX extension_name_idx ON image.extension (name);

INSERT INTO image.extension (name, mimetype, description) VALUES ('art','image/art','PFS: 1st Publisher',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('avs','image/avs','AVS X image',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('bmp','image/bmp','Microsoft Windows bitmap',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('cin','image/cin','Kodak Cineon Image Format',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('cur','image/x-win-bitmap','Microsoft Cursor Icon',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('cut','image/cut','DR Halo',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('dcm','image/dcm','Digital Imaging and Communications in Medicine (DICOM) image',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('dcx','image/dcx','ZSoft IBM PC multi-page Paintbrush image',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('dib','image/dib','Microsoft Windows Device Independent Bitmap',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('dng','image/dng','Digital Negative',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('dot','image/dot','Graph Visualization',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('dpx','image/dpx','SMPTE Digital Moving Picture Exchange',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('emf','image/emf','Microsoft Enhanced Metafile (32-bit)',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('fax','image/fax','Group 3 TIFF',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('gif','image/gif','CompuServe Graphics Interchange Format',true,true);
INSERT INTO image.extension (name, mimetype, description) VALUES ('ico','image/vnd.microsoft.icon','Microsoft icon',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('jng','image/jng','Multiple-image Network Graphics',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('jp2','image/jp2','JPEG-2000 JP2 File Format Syntax',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('jpc','image/jpc','JPEG-2000 Code Stream Syntax',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('jpe','image/jpeg','Joint Photographic Experts Group JFIF format',true,true);
INSERT INTO image.extension (name, mimetype, description) VALUES ('jpg','image/jpeg','Joint Photographic Experts Group JFIF format',true,true);
INSERT INTO image.extension (name, mimetype, description) VALUES ('mif','image/miff','Magick image file format',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('mat','image/mat','MATLAB image format',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('mng','image/mng','Multiple-image Network Graphics',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('mpc','image/mpc','Magick Persistent Cache image file format',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('mtv','image/mtv','MTV Raytracing image format',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('mvg','image/mvg','Magick Vector Graphics',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('otb','image/otb','On-the-air Bitmap',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('pbm','image/pbm','Portable bitmap format (black and white)',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('pcd','image/pcd','Photo CD',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('pcx','image/pcx','ZSoft IBM PC Paintbrush file',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('pdb','image/pdb','Palm Database ImageViewer Format',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('pix','image/pix','Alias/Wavefront RLE image format',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('png','image/png','Portable Network Graphics',true,true);
INSERT INTO image.extension (name, mimetype, description) VALUES ('pnm','image/pnm','Portable anymap',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('ppm','image/ppm','Portable pixmap format (color)',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('psd','application/x-photoshop','Adobe Photoshop bitmap file',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('pwp','image/pwp','Seattle File Works multi-image file',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('rad','image/rad','Radiance image file',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('rla','image/rla','Alias/Wavefront image file',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('rle','image/rle','Utah Run length encoded image file',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('sct','image/sct','Scitex Continuous Tone Picture',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('sfw','image/sfw','Seattle File Works image',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('sgi','image/sgi','Irix RGB image',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('sun','image/sun','SUN Rasterfile',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('svg','image/svg+xml','Scalable Vector Graphics',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('tga','image/tga','Truevision Targa image',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('tif','image/tiff','Tagged Image File Format',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('wmf','image/x-wmf','Windows Metafile',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('wpg','image/wpg','Word Perfect Graphics File',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('xbm','image/x-xbm','X Windows system bitmap, black and white only',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('xcf','image/xcf','GIMP image',false,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('xpm','image/x-xpm','X Windows system pixmap',true,false);
INSERT INTO image.extension (name, mimetype, description) VALUES ('xwd','image/x-xwd','X Windows system window dump',true,false);


--Add column
ALTER TABLE image.file
   ADD COLUMN key_extension integer;
ALTER TABLE image.file
   ALTER COLUMN key_extension SET NOT NULL;
ALTER TABLE image.file
   ALTER COLUMN key_extension SET DEFAULT 1;

--Foreign key
ALTER TABLE image.file ADD FOREIGN KEY (key_extension) REFERENCES image.extension (id);

--Delete DEFAULT
ALTER TABLE image.file
   ALTER COLUMN key_extension DROP DEFAULT;
   
--Permission
GRANT ALL ON image.extension TO image;
GRANT ALL ON image.extension_id_seq TO image;
