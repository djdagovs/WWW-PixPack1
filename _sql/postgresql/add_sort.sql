ALTER TABLE image.gallery ADD COLUMN sort_timestamp boolean;
ALTER TABLE image.gallery ADD COLUMN sort_order boolean;
UPDATE image.gallery SET sort_timestamp=false, sort_order=false;
ALTER TABLE image.gallery ALTER COLUMN sort_timestamp SET NOT NULL;
ALTER TABLE image.gallery ALTER COLUMN sort_order SET NOT NULL;
ALTER TABLE image.gallery ALTER COLUMN sort_timestamp SET DEFAULT false;
ALTER TABLE image.gallery ALTER COLUMN sort_order SET DEFAULT false;
