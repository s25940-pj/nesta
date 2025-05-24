package com.example.nesta.model;

import com.example.nesta.model.enums.ParkingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "apartment")
    private RentalOffer rentalOffer;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @NotNull
    private Integer area;
    @NotNull
    private Integer numberOfRooms;
    @NotNull
    private Integer numberOfBathrooms;
    @NotNull
    private Integer floor;
    @NotNull
    private boolean furnished;
    @NotNull
    private boolean hasBalcony;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ParkingType parkingType;

    @NotNull
    private boolean hasElevator;

    /**
     * Whether the apartment is accessible for people with disabilities.
     */
    @NotNull
    private boolean isDisabledAccessible;

    @NotNull
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
