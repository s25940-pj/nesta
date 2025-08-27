-- Create table
CREATE TABLE IF NOT EXISTS move_in_application (
    id                      BIGSERIAL PRIMARY KEY,
    rental_offer_id         BIGINT NOT NULL,
    rentier_id              VARCHAR(255),
    viewing_datetime        TIMESTAMPTZ NOT NULL,
    landlord_status         VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    rentier_status          VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    landlord_decision_reason VARCHAR(300),
    rentier_decision_reason  VARCHAR(300),
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    landlord_decided_at     TIMESTAMPTZ,
    rentier_decided_at      TIMESTAMPTZ
);

-- Foreign key to rental_offer(id)
ALTER TABLE move_in_application
    ADD CONSTRAINT fk_move_in_application_rental_offer
        FOREIGN KEY (rental_offer_id) REFERENCES rental_offer(id);

-- Helpful indexes
CREATE INDEX IF NOT EXISTS idx_move_in_application_rental_offer_id
    ON move_in_application (rental_offer_id);

CREATE INDEX IF NOT EXISTS idx_move_in_application_rentier_id
    ON move_in_application (rentier_id);

CREATE INDEX IF NOT EXISTS idx_move_in_application_created_at
    ON move_in_application (created_at);