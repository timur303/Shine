package kg.kadyrbekov.dto;
import kg.kadyrbekov.model.enums.StarsRating;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;

    @Enumerated(EnumType.STRING)
    private StarsRating starRating;

    private String comments;

    private Long carsID;

    public ReviewResponse(Long id, StarsRating starRating, String comments, Long carsID) {
        this.id = id;
        this.starRating = starRating;
        this.comments = comments;
        this.carsID = carsID;
    }
}
