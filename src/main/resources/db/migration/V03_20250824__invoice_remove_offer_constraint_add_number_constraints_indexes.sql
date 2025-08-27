ALTER TABLE rental_invoice
    ALTER COLUMN number TYPE varchar(40);

CREATE UNIQUE INDEX IF NOT EXISTS ux_rental_invoice_number
    ON rental_invoice (number)
    WHERE number IS NOT NULL;

CREATE INDEX IF NOT EXISTS ix_rental_invoice_user_created
    ON rental_invoice (user_id, created_at DESC);

CREATE INDEX IF NOT EXISTS ix_rental_invoice_paid
    ON rental_invoice (paid);

DO $$
    DECLARE
        cons_name text;
    BEGIN
        SELECT c.conname INTO cons_name
        FROM pg_constraint c
                 JOIN pg_class t ON t.oid = c.conrelid
        WHERE t.relname = 'rental_invoice'
          AND c.contype = 'u'
          AND cardinality(c.conkey) = 1
          AND (
                  SELECT att.attname
                  FROM pg_attribute att
                  WHERE att.attrelid = t.oid
                    AND att.attnum = c.conkey[1]
              ) = 'rental_offer_id';

        IF cons_name IS NOT NULL THEN
            EXECUTE format('ALTER TABLE rental_invoice DROP CONSTRAINT %I', cons_name);
        END IF;
    END $$;
