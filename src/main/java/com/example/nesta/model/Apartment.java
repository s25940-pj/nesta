package com.example.nesta.model;

import com.example.nesta.model.enums.ParkingType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "landlord_id", nullable = false)
    private String landlordId;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ApartmentImage> images = new java.util.LinkedHashSet<>();

    @NotNull
    private Integer area;
    @NotNull
    private Integer numberOfRooms;
    @NotNull
    private Integer numberOfBathrooms;
    @NotNull
    private Integer floor;

    private boolean furnished;

    private boolean hasBalcony;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ParkingType parkingType;

    private boolean hasElevator;

    /**
     * Whether the apartment is accessible for people with disabilities.
     */
    private boolean isDisabledAccessible;

    private boolean hasStorageRoomInBasement;

    @NotNull
    private String streetName;
    @NotNull
    private String buildingNumber;
    @NotNull
    private String apartmentNumber;
    @NotNull
    private String city;
    @NotNull
    private String postalCode;
    @NotNull
    private String country;
}
