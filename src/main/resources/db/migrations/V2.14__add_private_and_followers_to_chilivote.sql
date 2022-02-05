ALTER TABLE chilivotes ADD COLUMN is_private bit DEFAULT 0;
ALTER TABLE chilivotes ADD COLUMN followers TEXT DEFAULT NULL;

