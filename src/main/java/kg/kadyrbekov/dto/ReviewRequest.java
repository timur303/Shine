package kg.kadyrbekov.dto;


import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Cars;
import kg.kadyrbekov.model.enums.StarsRating;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

@Getter
@Setter
public class ReviewRequest {

    @Enumerated(EnumType.STRING)
    private StarsRating starsRating;

    private String comments;

//    private User user;

//    private Cars cars;


}
