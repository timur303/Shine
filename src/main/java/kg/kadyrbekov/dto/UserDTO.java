package kg.kadyrbekov.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kg.kadyrbekov.model.entity.Image;
import lombok.*;

import javax.persistence.Transient;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private int age;

    private String avatarUrl;

    @JsonIgnore
    @Transient
    private Image avatar;


}
