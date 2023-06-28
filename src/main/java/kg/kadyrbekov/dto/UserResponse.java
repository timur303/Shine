package kg.kadyrbekov.dto;

import kg.kadyrbekov.model.enums.Role;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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
