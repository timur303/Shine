package kg.kadyrbekov.services;

import kg.kadyrbekov.dto.UserRequest;
import kg.kadyrbekov.dto.UserResponse;
import kg.kadyrbekov.exception.NotFoundException;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final CarsRepository carsRepository;

    public UserResponse register(UserRequest userRequest) {
        User user1 = new User();
        userRepository.findByEmail(user1.getEmail());
        User user = mapToEntity(userRequest);
        user.setPassword(encoder.encode(userRequest.getPassword()));
        userRepository.save(user);
        return mapToResponse(user);
    }

    public User findByIDUser(Long id) throws NotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id not found" + id));
    }


    public User mapToEntity(UserRequest request) {
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
        User user = getAuthentication();
        Cars car = carsRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car with ID " + carId + " not found"));

        user.getFavoriteCars().add(car);
        car.setUser(user);

        userRepository.save(user);
        carsRepository.save(car);
    }

    public void removeCarFromFavorites(Long carId) throws NotFoundException {
        User user = getAuthentication();
        Cars car = carsRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car with ID " + carId + " not found"));

        user.getFavoriteCars().remove(car);
        car.setUser(null);

        userRepository.save(user);
        carsRepository.save(car);
    }

    public User getAuthentication() throws NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User with email not found"));
    }


    public UserResponse updateProfile(UserRequest updatedUserRequest) {
        User existingUser = getAuthentication();

        if (existingUser == null) {
            throw new RuntimeException("User not found.");
        }

        existingUser.setFirstName(updatedUserRequest.getFirstName());
        existingUser.setLastName(updatedUserRequest.getLastName());
        existingUser.setEmail(updatedUserRequest.getEmail());
        existingUser.setAge(updatedUserRequest.getAge());
        existingUser.setPhoneNumber(updatedUserRequest.getPhoneNumber());
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


    public UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .age(user.getAge())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

}
