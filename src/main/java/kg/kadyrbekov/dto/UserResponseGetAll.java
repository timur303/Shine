package kg.kadyrbekov.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder

public class UserResponseGetAll {


    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private int age;


}
