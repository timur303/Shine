package kg.kadyrbekov.model.entity;

import io.swagger.annotations.ApiModelProperty;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.model.enums.Category;
import kg.kadyrbekov.model.enums.City;
import kg.kadyrbekov.model.enums.carsenum.*;
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

    private String stateCarNumber;

    private LocalDateTime dateOfCreated;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private City city;

    @Enumerated(EnumType.STRING)
    private CarsStatus carsStatus;

    private String engineCapacity;

    @ApiModelProperty(hidden = true)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "cars")
    private List<Image> images;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private double price;

    @OneToMany(mappedBy = "car")
    private List<Review> reviews;

    @Column(nullable = false)
    private int likes;

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
