ALTER TABLE rental_invoice RENAME COLUMN user_id TO issuer_user_id;

ALTER TABLE rental_invoice ADD COLUMN receiver_user_id text;

CREATE INDEX IF NOT EXISTS idx_rental_invoice_issuer ON rental_invoice(issuer_user_id);
CREATE INDEX IF NOT EXISTS idx_rental_invoice_receiver ON rental_invoice(receiver_user_id);

ALTER TABLE rental_invoice
    ALTER COLUMN issuer_user_id SET NOT NULL;
