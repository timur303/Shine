package kg.kadyrbekov.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kg.kadyrbekov.config.jwt.JwtTokenUtil;
import kg.kadyrbekov.dto.*;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.exception.UserRegistrationException;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Cars;
import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.model.enums.Role;
import kg.kadyrbekov.repositories.CarsRepository;
import kg.kadyrbekov.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final JwtTokenUtil jwtTokenUtil;

    private final CarsRepository carsRepository;


    public UserResponse register(UserRequest userRequest) {
        String email = userRequest.getEmail();

        Optional<User> existingUserByEmail = userRepository.findByEmail(email);
        if (existingUserByEmail.isPresent()) {
            throw new UserRegistrationException("User with this email already exists.");
        }

        Optional<User> existingUserByPhoneNumber = userRepository.findByPhoneNumber(userRequest.getPhoneNumber());
        if (existingUserByPhoneNumber.isPresent()) {
            throw new UserRegistrationException("User with this phone number already exists.");
        }

        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(email);
        user.setAge(userRequest.getAge());
        user.setPassword(encoder.encode(userRequest.getPassword()));
        user.setPhoneNumber(userRequest.getPhoneNumber());

        if (email.equals("istanbekbek@gmail.com")) {
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

    @Transactional
    public List<CarsResponse> getFavoriteCarsByUser() {

        User user = getAuthenticatedUser();
        List<Cars> carsList = carsRepository.findAll();
        List<CarsResponse> responses = new ArrayList<>();
        if (user != null) {
            List<Long> favoriteCarIds = user.getFavoriteCars().stream()
                    .map(Cars::getId)
                    .collect(Collectors.toList());

            List<Cars> favoriteCarsList = carsList.stream()
                    .filter(car -> favoriteCarIds.contains(car.getId()))
                    .collect(Collectors.toList());

            for (Cars cars : favoriteCarsList) {
                if (!favoriteCarIds.contains(cars.getId())) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found in favorites");
                }
                CarsResponse response = new CarsResponse();
                response.setId(cars.getId());
                response.setEngineCapacity(cars.getEngineCapacity());
                response.setBody(cars.getBody());
                response.setColor(cars.getColor());
                response.setBrand(cars.getBrand());
                response.setAccounting(cars.getAccounting());
                response.setAvailability(cars.getAvailability());
                response.setCondition(cars.getCondition());
                response.setCurrency(cars.getCurrency());
                response.setDescription(cars.getDescription());
                response.setYearOfIssue(cars.getYearOfIssue());
                response.setTransmission(cars.getTransmission());
                response.setSteeringWheel(cars.getSteeringWheel());
                response.setPrice(cars.getPrice());
                response.setModel(cars.getModel());
                response.setMileage(cars.getMileage());
                response.setExchange(cars.getExchange());
                response.setEngine(cars.getEngine());
                response.setDriveUnit(cars.getDriveUnit());
                response.setDateOfCreated(LocalDateTime.now());
                response.setCategory(cars.getCategory());
                response.setCarsStatus(cars.getCarsStatus());
                response.setCity(cars.getCity());
                response.setStateCarNumber(cars.getStateCarNumber());
                boolean isFavorite = favoriteCarIds.contains(cars.getId());
                response.setFavorites(isFavorite);

                if (cars.getImages() != null && !cars.getImages().isEmpty()) {
                    List<String> imageUrls = new ArrayList<>();
                    for (Image image : cars.getImages()) {
                        imageUrls.add(image.getUrl());
                    }
                    response.setImages(imageUrls);
                } else {
                    response.setImages(Collections.singletonList("No images available"));
                }

                responses.add(response);
            }
        }
        return responses;
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


    @Transactional
    @JsonIgnore
    public UserResponse updateProfile(UpdateUserRequest updatedUserRequest) {
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

}