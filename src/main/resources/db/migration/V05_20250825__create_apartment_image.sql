CREATE TABLE apartment_image (
                                 id BIGSERIAL PRIMARY KEY,
                                 apartment_id BIGINT NOT NULL,
                                 relative_path VARCHAR(512) NOT NULL,
                                 public_url VARCHAR(512) NOT NULL,
                                 content_type VARCHAR(128),
                                 size_bytes BIGINT,
                                 width INT,
                                 height INT,
                                 CONSTRAINT fk_apartment_image_apartment
                                     FOREIGN KEY (apartment_id)
                                         REFERENCES apartment (id)
                                         ON DELETE CASCADE
);

CREATE INDEX idx_apartment_image_apartment_id
    ON apartment_image(apartment_id);
