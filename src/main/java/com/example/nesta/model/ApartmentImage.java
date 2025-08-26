package com.example.nesta.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApartmentImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    @Column(nullable = false, length = 512)
    private String relativePath;

    @Column(nullable = false, length = 512)
    private String publicUrl;

    @Column(length = 128)  private String contentType;
    private long sizeBytes;
    private Integer width;
    private Integer height;
}
