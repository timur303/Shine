package kg.kadyrbekov.dto;

import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.model.enums.Category;
import kg.kadyrbekov.model.enums.City;
import kg.kadyrbekov.model.enums.carsenum.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CarsRequest {

    private String brand;

    private String model;

    @Enumerated(EnumType.STRING)
    private Years YearOfIssue;

    private String mileage;

    @Enumerated(EnumType.STRING)
    private BodyType body;


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
    private Account accounting;

    @Enumerated(EnumType.STRING)
    private ExchangeCapability exchange;

    @Enumerated(EnumType.STRING)
    private Color color;

    private String description;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availability;

    private String stateCarNumber;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private double price;

    @Enumerated(EnumType.STRING)
    private City city;

    @Enumerated(EnumType.STRING)
    private CarsStatus carsStatus;

    private String engineCapacity;

}
