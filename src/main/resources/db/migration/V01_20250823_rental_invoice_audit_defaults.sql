ALTER TABLE rental_invoice
    ALTER COLUMN created_at SET DEFAULT NOW(),
    ALTER COLUMN updated_at SET DEFAULT NOW();

UPDATE rental_invoice SET created_at = NOW() WHERE created_at IS NULL;
UPDATE rental_invoice SET updated_at = NOW() WHERE updated_at IS NULL;