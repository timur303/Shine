package kg.kadyrbekov.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.model.enums.Category;
import kg.kadyrbekov.model.enums.City;
import lombok.Getter;
import lombok.Setter;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CarsRequest {

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

//    @ManyToOne
//    private List<Image> images = new ArrayList<>();

//    private User user;

//    private List<Review> reviews;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private City city;

    @Enumerated(EnumType.STRING)
    private CarsStatus carsStatus;

}
