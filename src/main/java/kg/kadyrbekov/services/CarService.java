package kg.kadyrbekov.services;

import kg.kadyrbekov.dto.CarsRequest;
import kg.kadyrbekov.dto.CarsResponse;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Cars;
import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.repositories.CarsRepository;
import kg.kadyrbekov.repositories.ImagesRepository;
import kg.kadyrbekov.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarsRepository carsRepository;

    private final UserRepository userRepository;

    private final ImagesRepository imagesRepository;

    public User getAuthentication() throws NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User with email not found"));
    }



//    public CarsResponse createCar(CarsRequest request, List<MultipartFile> files) throws NotFoundException, IOException {
//        Cars cars = mapToEntity(request);
//        User user = getAuthentication();
////        List<Image> images;
//
//        for (MultipartFile file : files) {
//            if (file.getSize() != 0) {
//                Image image = toImageEntity(file);
//                cars.addImageToCars(image);
//            }
//        }
//        cars.setUser(user);
//        cars.setUserId(user.getId());
//        Cars carsFromDB = carsRepository.save(cars);
//        if (!cars.getImages().isEmpty()) {
//            carsFromDB.setPreviewImageId(carsFromDB.getImages().get(0).getId());
//        }
//        carsRepository.save(cars);
//        return mapToResponse(cars);
//    }

    public CarsResponse createCar(CarsRequest request, List<MultipartFile> files) throws NotFoundException, IOException {
        Cars cars = mapToEntity(request);
        User user = getAuthentication();
        List<Image> images = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.getSize() != 0) {
                Image image = toImageEntity(file);
                image.setCars(cars);
                images.add(image);
            }
        }

        cars.setUser(user);
        cars.setUserId(user.getId());
        Cars carsFromDB = carsRepository.save(cars);

        if (!images.isEmpty()) {
            carsFromDB.setPreviewImageId(images.get(0).getId());
        }

        // Save the cars object and the associated images
        carsRepository.save(carsFromDB);
        imagesRepository.saveAll(images);

        return mapToResponse(carsFromDB);
    }

    public CarsResponse update(Long id, CarsRequest request) throws NotFoundException, IOException {
        Cars cars = carsRepository.findById(id).get();
        cars.setDescription(request.getDescription());
        cars.setBody(request.getBody());
        cars.setBrand(request.getBrand());
        cars.setColor(request.getColor());
        cars.setAccounting(request.getAccounting());
        cars.setCondition(request.getCondition());
        cars.setAvailability(request.getAvailability());
        cars.setCustoms(request.getCustoms());
        cars.setYearOfIssue(request.getYearOfIssue());
        cars.setTransmission(request.getTransmission());
        cars.setSteeringWheel(request.getSteeringWheel());
//        cars.setReviews(request.getReviews());
        cars.setPrice(request.getPrice());
        cars.setModel(request.getModel());
        cars.setRegionCityOfSale(request.getRegionCityOfSale());
        cars.setMileage(request.getMileage());
        cars.setExchange(request.getExchange());
        cars.setEngine(request.getEngine());
        cars.setDriveUnit(request.getDriveUnit());

        carsRepository.save(cars);

        return carsResponse(cars);
    }

    public void deleteCar(Long id) throws NotFoundException {
        Cars car = carsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Car with id " + id + " not found"));
        carsRepository.delete(car);
    }


    public List<Cars> getAllCars() {
        return carsRepository.findAll();
    }

    public CarsResponse findByIdCars(Long id) throws NotFoundException {
        Cars car = carsRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Car with id not found: " + id));

        CarsResponse response = new CarsResponse();
        response.setId(car.getId());
        response.setModel(car.getModel());

        return response;
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
        cars.setCustoms(request.getCustoms());
        cars.setTransmission(request.getTransmission());
        cars.setYearOfIssue(request.getYearOfIssue());
//        cars.setReviews(request.getReviews());
        cars.setSteeringWheel(request.getSteeringWheel());
        cars.setRegionCityOfSale(request.getRegionCityOfSale());
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
        return cars;
    }

    public CarsResponse mapToResponse(Cars cars) {
        if (cars == null) {
            return null;
        }
        CarsResponse response = new CarsResponse();
        response.setId(cars.getId());
        response.setBody(cars.getBody());
        response.setColor(cars.getColor());
        response.setBrand(cars.getBrand());
        response.setAccounting(cars.getAccounting());
        response.setAvailability(cars.getAvailability());
        response.setCondition(cars.getCondition());
        response.setCustoms(cars.getCustoms());
        response.setDescription(cars.getDescription());
        response.setYearOfIssue(cars.getYearOfIssue());
        response.setTransmission(cars.getTransmission());
        response.setSteeringWheel(cars.getSteeringWheel());
        response.setRegionCityOfSale(cars.getRegionCityOfSale());
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
                .customs(cars.getCustoms())
                .description(cars.getDescription())
                .driveUnit(cars.getDriveUnit())
                .engine(cars.getEngine())
                .exchange(cars.getExchange())
                .mileage(cars.getMileage())
                .price(cars.getPrice())
//                .images(cars.getImages())
                .steeringWheel(cars.getSteeringWheel())
                .transmission(cars.getTransmission())
                .YearOfIssue(cars.getYearOfIssue())
                .regionCityOfSale(cars.getRegionCityOfSale())
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
