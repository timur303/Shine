package kg.kadyrbekov.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class UserResponse {

    private Long id;

    private String token;

    private String errorMessage;


}
