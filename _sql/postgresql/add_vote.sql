ALTER TABLE image.file ADD COLUMN vote_count int4;
UPDATE image.file SET vote_count=0;
ALTER TABLE image.file ALTER COLUMN vote_count SET NOT NULL;
ALTER TABLE image.file ALTER COLUMN vote_count SET DEFAULT 0;

ALTER TABLE image.file ADD COLUMN vote_sum int4;
UPDATE image.file SET vote_sum=0;
ALTER TABLE image.file ALTER COLUMN vote_sum SET NOT NULL;
ALTER TABLE image.file ALTER COLUMN vote_sum SET DEFAULT 0;

ALTER TABLE image.file ADD COLUMN vote_ip inet;
UPDATE image.file SET vote_ip='0.0.0.0'::inet;
ALTER TABLE image.file ALTER COLUMN vote_ip SET NOT NULL;
ALTER TABLE image.file ALTER COLUMN vote_ip SET DEFAULT '0.0.0.0'::inet;


