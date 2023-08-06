package kg.kadyrbekov.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import kg.kadyrbekov.dto.CarsRequest;
import kg.kadyrbekov.dto.CarsResponse;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Cars;
import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.model.entity.Url;
import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.repositories.CarsRepository;
import kg.kadyrbekov.repositories.ImagesRepository;
import kg.kadyrbekov.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarsRepository carsRepository;

    private final UserRepository userRepository;

    private final ImagesRepository imagesRepository;

    private final Cloudinary cloudinary;

    public CarsResponse createCar(CarsRequest request) throws NotFoundException, IOException {
        Cars cars = mapToEntity(request);
        User user = getAuthenticatedUser();

        List<Image> images = request.getImages().stream()
                .map(url -> {
                    Image image = new Image();
                    image.setUrl(url);
                    image.setCars(cars);
                    return image;
                })
                .collect(Collectors.toList());

        // Сохраняем список изображений в поле images сущности Cars
        cars.setImages(images);

        // Сохранение сущности Cars в базе данных
        carsRepository.save(cars);

        cars.setUser(user);
        cars.setUserId(user.getId());

        carsRepository.save(cars);

        return mapToResponse(cars);
    }


    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found!"));
    }

    private Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }

    public CarsResponse update(Long id, CarsRequest request) throws NotFoundException, IOException {
        Cars cars = carsRepository.findById(id).get();
        cars.setDescription(request.getDescription());
        cars.setBody(request.getBody());
        cars.setBrand(request.getBrand());
        cars.setColor(request.getColor());
        cars.setAccounting(request.getAccounting());
        cars.setEngineCapacity(request.getEngineCapacity());
        cars.setCondition(request.getCondition());
        cars.setAvailability(request.getAvailability());
        cars.setYearOfIssue(request.getYearOfIssue());
        cars.setTransmission(request.getTransmission());
        cars.setSteeringWheel(request.getSteeringWheel());
        cars.setPrice(request.getPrice());
        cars.setModel(request.getModel());
        cars.setMileage(request.getMileage());
        cars.setExchange(request.getExchange());
        cars.setEngine(request.getEngine());
        cars.setDriveUnit(request.getDriveUnit());
        cars.setStateCarNumber(request.getStateCarNumber());
        cars.setCurrency(request.getCurrency());
        carsRepository.save(cars);

        return carsResponse(cars);
    }

    public void deleteCar(Long id) throws NotFoundException {
        Cars car = carsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Car with id " + id + " not found"));
        carsRepository.delete(car);
    }

    public List<CarsResponse> getAllCars() {
        User user = getAuthenticatedUser();
        List<Cars> carsList = carsRepository.findAll();
        List<CarsResponse> responseList = new ArrayList<>();

        if (user != null) {
            List<Long> favoriteCarIds = user.getFavoriteCars().stream()
                    .map(Cars::getId)
                    .collect(Collectors.toList());

            for (Cars cars : carsList) {
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

                responseList.add(response);
            }

        }

        return responseList;
    }


    @Transactional
    public CarsResponse getByIdCars(Long id) throws NotFoundException {
        User user = getAuthenticatedUser();
        Cars cars = carsRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Car with id not found: " + id));

        CarsResponse response = new CarsResponse();
        response.setId(cars.getId());
        response.setBody(cars.getBody());
        response.setColor(cars.getColor());
        response.setBrand(cars.getBrand());
        response.setEngineCapacity(cars.getEngineCapacity());
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
        response.setLikes(cars.getLikes());
        if (user != null) {
            List<Long> favoriteCarIds = user.getFavoriteCars().stream()
                    .map(Cars::getId)
                    .collect(Collectors.toList());

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


            List<String> imageUrls = new ArrayList<>();
            for (Image image : cars.getImages()) {
                imageUrls.add(image.getUrl());
            }
            response.setImages(Collections.singletonList(String.join(", ", imageUrls)));

        }
        return response;

    }

    public List<CarsResponse> getAllCarsWithout() {
        List<Cars> carsList = carsRepository.findAll();
        List<CarsResponse> responseList = new ArrayList<>();

//        if (user != null) {
//            List<Long> favoriteCarIds = user.getFavoriteCars().stream()
//                    .map(Cars::getId)
//                    .collect(Collectors.toList());

        for (Cars cars : carsList) {
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
//                boolean isFavorite = favoriteCarIds.contains(cars.getId());
//            response.setFavorites(isFavorite);


            if (cars.getImages() != null && !cars.getImages().isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                for (Image image : cars.getImages()) {
                    imageUrls.add(image.getUrl());
                }
                response.setImages(imageUrls);
            } else {
                response.setImages(Collections.singletonList("No images available"));
            }

            responseList.add(response);
        }


        return responseList;
    }


    @Transactional
    public CarsResponse getByIdCarsWithout(Long id) throws NotFoundException {
        Cars cars = carsRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Car with id not found: " + id));

        CarsResponse response = new CarsResponse();
        response.setId(cars.getId());
        response.setBody(cars.getBody());
        response.setColor(cars.getColor());
        response.setBrand(cars.getBrand());
        response.setEngineCapacity(cars.getEngineCapacity());
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
        response.setLikes(cars.getLikes());
//        if (user != null) {
//            List<Long> favoriteCarIds = user.getFavoriteCars().stream()
//                    .map(Cars::getId)
//                    .collect(Collectors.toList());

//        boolean isFavorite = favoriteCarIds.contains(cars.getId());
//        response.setFavorites(isFavorite);

        if (cars.getImages() != null && !cars.getImages().isEmpty()) {
            List<String> imageUrls = new ArrayList<>();
            for (Image image : cars.getImages()) {
                imageUrls.add(image.getUrl());
            }
            response.setImages(imageUrls);
        } else {
            response.setImages(Collections.singletonList("No images available"));
        }


        List<String> imageUrls = new ArrayList<>();
        for (Image image : cars.getImages()) {
            imageUrls.add(image.getUrl());
        }
        response.setImages(Collections.singletonList(String.join(", ", imageUrls)));


        return response;

    }


    public void updateCarStatus(Long carId, CarsStatus status) throws NotFoundException {
        Cars car = carsRepository.findById(carId).orElseThrow(
                () -> new NotFoundException("Car with ID not found"));
        car.setCarsStatus(status);
        carsRepository.save(car);
    }


    public void cancelCarStatus(Long carId) throws NotFoundException {
        Cars car = carsRepository.findById(carId).orElseThrow(
                () -> new NotFoundException("Car with ID not found"));
        car.setCarsStatus(null);
        carsRepository.save(car);
    }

    public List<Cars> getCarsByStatus(CarsStatus status) {
        return carsRepository.findAllByCarsStatus(status);
    }

    public Cars mapToEntity(CarsRequest request) {
        Cars cars = new Cars();
        cars.setBody(request.getBody());
        cars.setDescription(request.getDescription());
        cars.setColor(request.getColor());
        cars.setBrand(request.getBrand());
        cars.setAccounting(request.getAccounting());
        cars.setAvailability(request.getAvailability());
        cars.setCondition(request.getCondition());
        cars.setCurrency(request.getCurrency());
        cars.setTransmission(request.getTransmission());
        cars.setEngineCapacity(request.getEngineCapacity());
        cars.setYearOfIssue(request.getYearOfIssue());
//        cars.setReviews(request.getReviews());
        cars.setSteeringWheel(request.getSteeringWheel());
        cars.setPrice(request.getPrice());
//        cars.setImages(request.getImages());
        cars.setModel(request.getModel());
        cars.setMileage(request.getMileage());
        cars.setExchange(request.getExchange());
        cars.setEngine(request.getEngine());
        cars.setDriveUnit(request.getDriveUnit());
        cars.setCategory(request.getCategory());
        cars.setCity(request.getCity());
        cars.setCarsStatus(request.getCarsStatus());
        cars.setDateOfCreated(LocalDateTime.now());
        cars.setStateCarNumber(request.getStateCarNumber());

        return cars;
    }

    public CarsResponse mapToResponse(Cars cars) {
        if (cars == null) {
            return null;
        }
        CarsResponse response = new CarsResponse();
        response.setId(cars.getId());
        response.setBody(cars.getBody());
        response.setEngineCapacity(cars.getEngineCapacity());
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
//        response.setImages(cars.getImages());
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

        List<String> imageUrls = cars.getImages().stream()
                .map(Image::getUrl)
                .collect(Collectors.toList());
        response.setImages(imageUrls);

        return response;
    }

    private CarsResponse carsResponse(Cars cars) {
        return CarsResponse.builder()
                .id(cars.getId())
                .accounting(cars.getAccounting())
                .availability(cars.getAvailability())
                .body(cars.getBody())
                .brand(cars.getBrand())
                .color(cars.getColor())
                .condition(cars.getCondition())
                .stateCarNumber(cars.getStateCarNumber())
                .currency(cars.getCurrency())
                .description(cars.getDescription())
                .driveUnit(cars.getDriveUnit())
                .engine(cars.getEngine())
                .exchange(cars.getExchange())
                .mileage(cars.getMileage())
                .price(cars.getPrice())
                .engineCapacity(cars.getEngineCapacity())
//                .images(cars.getImages())
                .steeringWheel(cars.getSteeringWheel())
                .transmission(cars.getTransmission())
                .YearOfIssue(cars.getYearOfIssue())
                .model(cars.getModel())
//                .reviews(cars.getReviews())
//                .userId(cars.getUserId())
                .carsStatus(cars.getCarsStatus())
                .dateOfCreated(LocalDateTime.now())
                .category(cars.getCategory())
                .city(cars.getCity())

                .build();
    }
}
