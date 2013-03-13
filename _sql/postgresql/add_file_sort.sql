ALTER TABLE image.file ADD COLUMN sort "char";
UPDATE image.file SET sort=CAST(0 AS char);
ALTER TABLE image.file ALTER COLUMN sort SET NOT NULL;
ALTER TABLE image.file ALTER COLUMN sort SET DEFAULT CAST(0 AS char);
