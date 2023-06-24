package kg.kadyrbekov.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.model.entity.Review;
import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.model.enums.Category;
import kg.kadyrbekov.model.enums.City;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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

    @JsonIgnore
    private List<Image> images = new ArrayList<>();

//    private User user;

//    private Long userId;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private City city;

    @Enumerated(EnumType.STRING)
    private CarsStatus carsStatus;

//    private List<Review> reviews;

}
