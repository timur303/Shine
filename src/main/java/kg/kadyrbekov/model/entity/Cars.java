package kg.kadyrbekov.model.entity;

import io.swagger.annotations.ApiModelProperty;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.model.enums.Category;
import kg.kadyrbekov.model.enums.City;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;


@Data
@Entity
@Table(name = "cars")
public class Cars {

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

    @Column(nullable = false)
    private int likes;

    private LocalDateTime dateOfCreated;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private City city;

    @Enumerated(EnumType.STRING)
    private CarsStatus carsStatus;

    @ApiModelProperty(hidden = true)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "cars")
    private List<Image> images;

    @OneToMany(mappedBy = "car")
    private List<Review> reviews;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    private Long previewImageId;

    @Transient
    Long userId;

    @OneToMany(mappedBy = "car")
    private List<UserCarView> userCarViews = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "car_user_likes",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likedUsers = new HashSet<>();


    public Set<User> getLikedUsers() {
        return likedUsers;
    }

    public void addImageToCars(Image image) {
        image.setCars(this);
        images.add(image);
    }

}
