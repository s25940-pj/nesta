package com.example.nesta.model;

import com.example.nesta.model.enums.ParkingType;
import jakarta.persistence.*;
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

    private int area;
    private int numberOfRooms;
    private int numberOfBathrooms;
    private int floor;
    private boolean furnished;
    private boolean hasBalcony;

    @Enumerated(EnumType.STRING)
    private ParkingType parkingType;

    private boolean hasElevator;

    /**
     * Whether the apartment is accessible for people with disabilities.
     */
    private boolean isDisabledAccessible;

    private boolean hasStorageRoomInBasement;

    /**
     * Address entity containing location details.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
}
