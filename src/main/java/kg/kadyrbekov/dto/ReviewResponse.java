package kg.kadyrbekov.dto;
import kg.kadyrbekov.model.enums.StarsRating;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;

    @Enumerated(EnumType.STRING)
    private StarsRating starRating;

    private String comments;

    private Long carsID;


}
