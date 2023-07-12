package kg.kadyrbekov.dto;

import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.model.enums.Category;
import kg.kadyrbekov.model.enums.City;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class CarsResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int YearOfIssue;

    private String mileage;

    private String body;

    private String color;

    private String engine;

    private String transmission;

    private String driveUnit;

    private String steeringWheel;

    private String condition;

    private String customs;

    private String exchange;

    private String availability;

    private String regionCityOfSale;

    private String accounting;

    private String description;

    private String brand;

    private String model;

    private double price;

    private LocalDateTime dateOfCreated;

    private String images;

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private City city;

    @Enumerated(EnumType.STRING)
    private CarsStatus carsStatus;


}
