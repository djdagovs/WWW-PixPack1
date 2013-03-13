ALTER TABLE image.gallery ADD COLUMN subfolder boolean;
UPDATE image.gallery SET subfolder=false;
ALTER TABLE image.gallery ALTER COLUMN subfolder SET NOT NULL;
ALTER TABLE image.gallery ALTER COLUMN subfolder SET DEFAULT false;
