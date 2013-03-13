ALTER TABLE image.account ADD COLUMN mail varchar(128);
UPDATE image.account SET mail='';
ALTER TABLE image.account ALTER COLUMN mail SET NOT NULL;
