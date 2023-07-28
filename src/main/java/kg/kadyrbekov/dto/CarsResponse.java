package kg.kadyrbekov.dto;

import kg.kadyrbekov.exception.Error;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Cars;
import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.model.enums.Category;
import kg.kadyrbekov.model.enums.City;
import kg.kadyrbekov.model.enums.carsenum.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarsResponse {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;

    private String model;

    @Enumerated(EnumType.STRING)
    private Years YearOfIssue;

    private String mileage;

    @Enumerated(EnumType.STRING)
    private BodyType body;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Enumerated(EnumType.STRING)
    private EngineType engine;

    @Enumerated(EnumType.STRING)
    private TransmissionType transmission;

    @Enumerated(EnumType.STRING)
    private DriveType driveUnit;

    @Enumerated(EnumType.STRING)
    private SteeringWheelPosition steeringWheel;

    @Enumerated(EnumType.STRING)
    private State condition;

    @Enumerated(EnumType.STRING)
    private ExchangeCapability exchange;

    @Enumerated(EnumType.STRING)
    private Account accounting;

    private String description;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availability;


    @Enumerated(EnumType.STRING)
    private Currency currency;

    private double price;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private City city;

    private String stateCarNumber;

    @Enumerated(EnumType.STRING)
    private CarsStatus carsStatus;

    private List<String> images;

    private int likes;

    private String engineCapacity;

    private LocalDateTime dateOfCreated;

    private boolean isFavorites;

    private Error error;

    public CarsResponse(Error error) {
        this.error = error;
    }



}
