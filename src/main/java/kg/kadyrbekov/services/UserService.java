package kg.kadyrbekov.services;

import kg.kadyrbekov.config.jwt.JwtTokenUtil;
import kg.kadyrbekov.dto.UserRequest;
import kg.kadyrbekov.dto.UserResponse;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.exception.UserRegistrationException;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Cars;
import kg.kadyrbekov.model.enums.Role;
import kg.kadyrbekov.repositories.CarsRepository;
import kg.kadyrbekov.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final JwtTokenUtil jwtTokenUtil;

    private final CarsRepository carsRepository;


    public UserResponse register(UserRequest userRequest) {
        String number = userRequest.getPhoneNumber();

        Optional<User> existingUserByEmail = userRepository.findByPhoneNumber(number);
        if (existingUserByEmail.isPresent()) {
            throw new UserRegistrationException("User with this number already exists.");
        }

        Optional<User> existingUserByPhoneNumber = userRepository.findByPhoneNumber(userRequest.getPhoneNumber());
        if (existingUserByPhoneNumber.isPresent()) {
            throw new UserRegistrationException("User with this phone number already exists.");
        }

        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setAge(userRequest.getAge());
        user.setPassword(encoder.encode(userRequest.getPassword()));
        user.setPhoneNumber(number);

        if (number.equals("996507934333")) {
            if (isAdminAlreadyLoggedIn()) {
                throw new RuntimeException("Admin is already logged in");
            } else {
                user.setRole(Role.ADMIN);
                setAdminLoggedIn(true);
            }
        } else {
            user.setRole(Role.USER);
        }

        userRepository.save(user);

        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .token(jwtTokenUtil.generateToken(user))
                .role(user.getRole())
                .build();
    }


    public User mapToEntity(UserRequest request) {

        Optional<User> existingUserByEmail = userRepository.findByEmail(request.getEmail());
        if (existingUserByEmail.isPresent()) {
            throw new UserRegistrationException("User with this email already exists.");
        }

        Optional<User> existingUserByPhoneNumber = userRepository.findByPhoneNumber(request.getPhoneNumber());
        if (existingUserByPhoneNumber.isPresent()) {
            throw new UserRegistrationException("User with this phone number already exists.");
        }

        String email = request.getEmail();

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(email);
        user.setAge(request.getAge());
        user.setPassword(request.getPassword());
        user.setPhoneNumber(request.getPhoneNumber());
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email address already registered.");
        }

        if (email.equals("beka@gmail.com")) {
            if (isAdminAlreadyLoggedIn()) {
                throw new RuntimeException("Admin is already logged in");
            } else {
                user.setRole(Role.ADMIN);
                setAdminLoggedIn(true);
            }
        } else {
            user.setRole(Role.USER);
        }

        return user;
    }

    public void addCarToFavorites(Long carId) throws NotFoundException {
        User user = getAuthenticatedUser();
        Cars car = carsRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car with ID " + carId + " not found"));

        user.getFavoriteCars().add(car);
        car.setUser(user);

        userRepository.save(user);
        carsRepository.save(car);
    }

    public List<Cars> getFavoriteCarsByUser() {
        User user = getAuthenticatedUser();
        return user.getFavoriteCars();
    }


    public void removeCarFromFavorites(Long carId) throws NotFoundException {
        User user = getAuthenticatedUser();
        Cars car = carsRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car with ID " + carId + " not found"));

        user.getFavoriteCars().remove(car);
        car.setUser(null);

        userRepository.save(user);
        carsRepository.save(car);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found!"));
    }


    public UserResponse updateProfile(UserRequest updatedUserRequest) {
        User existingUser = getAuthenticatedUser();

        if (existingUser == null) {
            throw new RuntimeException("User not found.");
        }

        existingUser.setFirstName(updatedUserRequest.getFirstName());
        existingUser.setLastName(updatedUserRequest.getLastName());
        existingUser.setEmail(updatedUserRequest.getEmail());
        existingUser.setAge(updatedUserRequest.getAge());
        existingUser.setPhoneNumber(updatedUserRequest.getPhoneNumber());
        existingUser.setAvatarUrl(updatedUserRequest.getAvatarUrl());
        User updatedUser = userRepository.save(existingUser);

        return mapToResponse(updatedUser);
    }

    private static boolean adminLoggedIn = false;

    private synchronized boolean isAdminAlreadyLoggedIn() {
        return adminLoggedIn;
    }

    private synchronized void setAdminLoggedIn(boolean loggedIn) {
        adminLoggedIn = loggedIn;
    }


//    public UserResponse mapToResponses(User user) {
//        return UserResponse.builder()
//                .id(user.getId())
//                .firstName(user.getFirstName())
//                .lastName(user.getLastName())
//                .phoneNumber(user.getPhoneNumber())
//                .age(user.getAge())
//                .email(user.getEmail())
//                .password(user.getPassword())
//                .build();
//    }

}
