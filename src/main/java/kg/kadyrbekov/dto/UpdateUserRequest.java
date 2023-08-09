package kg.kadyrbekov.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {


    private String firstName;

    private String lastName;

    private int age;

    private String email;

    private String phoneNumber;

    private String avatarUrl;

}
