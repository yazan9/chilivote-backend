ALTER TABLE user ADD COLUMN role_id bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE user ADD FOREIGN KEY (role_id) REFERENCES roles(id);
