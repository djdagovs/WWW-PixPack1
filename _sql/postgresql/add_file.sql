--Add column
ALTER TABLE image.file ADD COLUMN width int2;
UPDATE image.file SET width=0;
ALTER TABLE image.file ALTER COLUMN width SET NOT NULL;
ALTER TABLE image.file ALTER COLUMN width SET DEFAULT 0;

ALTER TABLE image.file ADD COLUMN height int2;
UPDATE image.file SET height=0;
ALTER TABLE image.file ALTER COLUMN height SET NOT NULL;
ALTER TABLE image.file ALTER COLUMN height SET DEFAULT 0;

ALTER TABLE image.file ADD COLUMN size int;
UPDATE image.file SET size=0;
ALTER TABLE image.file ALTER COLUMN size SET NOT NULL;
ALTER TABLE image.file ALTER COLUMN size SET DEFAULT 0;

ALTER TABLE image.file ADD COLUMN protect varchar(250);
