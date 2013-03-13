--creates a reference of 'parent' to a 'id'
ALTER TABLE image.account ADD CONSTRAINT account_parent FOREIGN KEY (parent) REFERENCES image.account (id);
CREATE INDEX account_parent_idx ON image.account(parent);

--rename
DROP INDEX image.account_parent_idx;
ALTER TABLE image.account RENAME parent TO key_parent;
CREATE INDEX account_key_parent_idx ON image.account(key_parent);

--default
ALTER TABLE image.account ALTER COLUMN key_parent SET DEFAULT 4;