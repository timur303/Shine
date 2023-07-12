package kg.kadyrbekov.services;

import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Cars;
import kg.kadyrbekov.repositories.CarsRepository;
import kg.kadyrbekov.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final CarsRepository carsRepository;

    private final UserRepository userRepository;



    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found!"));
    }


    public void likeCar(Long carId) throws NotFoundException {
        User user = getAuthenticatedUser();
        Cars car = carsRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car with ID " + carId + " not found"));

        if (!carsRepository.existsByIdAndLikedUsers(carId, user)) {
            car.getLikedUsers().add(user);
            car.setLikes(car.getLikes() + 1);
            carsRepository.save(car);
        }
    }

    public void cancelLikeCar(Long carId) throws NotFoundException {
        User user = getAuthenticatedUser();
        Cars car = carsRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car with ID " + carId + " not found"));

        if (car.getLikedUsers().contains(user)) {
            car.getLikedUsers().remove(user);
            car.setLikes(car.getLikes() - 1);
            carsRepository.save(car);
        }
    }

}
