package kg.kadyrbekov.dto;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarsResponseReview {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private List<ReviewResponse> reviewResponse;
}
