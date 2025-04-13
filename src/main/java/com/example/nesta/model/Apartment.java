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

    /**
     * Address entity containing location details.
     */
    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
}
