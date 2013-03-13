--random
DROP VIEW image.view_randomgallery;
CREATE OR REPLACE VIEW image.view_randomgallery AS 
	SELECT file.server, file.name, file."timestamp", file."key", file.width, file.height, file.size, file.status, file.key_user, extension.name AS extension
		FROM image.file, image.extension
		WHERE file.public = true AND file.adult = false AND (file.status>0 OR file.key_user>6) AND file.key_extension = extension.id
		ORDER BY random();

ALTER TABLE image.view_randomgallery OWNER TO image;

DROP VIEW image.view_last; 
CREATE OR REPLACE VIEW image.view_last AS 
	SELECT file.server, file.name, file."timestamp", file."key", file.width, file.height, file.size, file.status, file.key_user, extension.name AS extension
		FROM image.file, image.extension
		WHERE file.key_extension = extension.id
		ORDER BY timestamp DESC;

ALTER TABLE image.view_last OWNER TO image;