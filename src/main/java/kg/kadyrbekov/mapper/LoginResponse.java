package kg.kadyrbekov.mapper;

import kg.kadyrbekov.model.enums.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
public class LoginResponse {

    private String jwtToken;

    private String message;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private Role role;


}
