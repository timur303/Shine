package kg.kadyrbekov.controller;

import io.swagger.annotations.*;
import kg.kadyrbekov.dto.CarsResponse;
import kg.kadyrbekov.dto.MessageInvalid;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.model.entity.Cars;
import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.services.CarService;
import kg.kadyrbekov.services.PostService;
import kg.kadyrbekov.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
@ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", required = true, dataType = "string", paramType = "header")
})
public class PostController {

    private final MessageSource messageSource;

    private final PostService postService;

    private final UserService userService;

    private final CarService carsService;

    @PostMapping("/like/{carId}")
    @ApiOperation("Like a car")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Car liked successfully"),
            @ApiResponse(code = 404, message = "Car or user not found"),
            @ApiResponse(code = 500, message = "Failed to like car")
    })
    public ResponseEntity<String> likeCar(HttpServletRequest request, @PathVariable Long carId) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            String messages = messageSource.getMessage("car.liked", null, locale);
            postService.likeCar(carId);
            return ResponseEntity.ok(messages);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            String messages = messageSource.getMessage("car.liked.invalid", null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messages);
        }
    }

    @GetMapping("/getAllByStatus/{status}")
    @ApiOperation("Get cars by status")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved cars by status"),
            @ApiResponse(code = 404, message = "No cars found with the specified status")
    })
    public ResponseEntity<List<CarsResponse>> getCarsByStatus(@PathVariable("status") CarsStatus status) {
        List<Cars> carsList = carsService.getCarsByStatus(status);
        List<CarsResponse> responseList = new ArrayList<>();

        for (Cars car : carsList) {
            CarsResponse response = new CarsResponse();
            response.setId(car.getId());
            response.setBrand(car.getBrand());
            responseList.add(response);
        }

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/getAllCars")
    @ApiOperation("Get all cars")
    @ApiResponse(code = 200, message = "Successfully retrieved all cars")
    public ResponseEntity<List<CarsResponse>> getAllCars() {
        List<Cars> carsList = carsService.getAllCars();
        List<CarsResponse> responseList = new ArrayList<>();

        for (Cars car : carsList) {
            CarsResponse response = new CarsResponse();
            response.setId(car.getId());
            response.setBrand(car.getBrand());

            responseList.add(response);
        }

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/getCarByID/{id}")
    @ApiOperation("Get car by ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved the car"),
            @ApiResponse(code = 404, message = "Car not found with the specified ID")
    })
    public ResponseEntity<?> getCarById(HttpServletRequest request, @PathVariable("id") Long id) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            CarsResponse response = carsService.getByIdCars(id);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            String messages = messageSource.getMessage("car.getID", null, locale);
            MessageInvalid response = new MessageInvalid();
            response.setMessages(messages);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/favorites/{carId}")
    @ApiOperation("Add car to favorites")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Car added to favorites successfully"),
            @ApiResponse(code = 404, message = "Car or user not found"),
            @ApiResponse(code = 500, message = "Failed to add car to favorites")
    })
    public ResponseEntity<String> addCarToFavorites(HttpServletRequest request, @PathVariable Long carId) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            String messages = messageSource.getMessage("favorite.added", null, locale);
            userService.addCarToFavorites(carId);
            return ResponseEntity.ok(messages);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            String messages = messageSource.getMessage("favorite.add.failed", null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messages);
        }
    }


    @DeleteMapping("removeFavorites/{carId}")
    @ApiOperation("Remove car from favorites")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Car removed from favorites successfully"),
            @ApiResponse(code = 404, message = "Car or user not found"),
            @ApiResponse(code = 500, message = "Failed to remove car from favorites")
    })
    public ResponseEntity<String> removeCarFromFavorites(HttpServletRequest request, @PathVariable Long carId) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            String messages = messageSource.getMessage("favorite.remove", null, locale);
            userService.removeCarFromFavorites(carId);
            return ResponseEntity.ok(messages);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            String messages = messageSource.getMessage("favorite.remove.failed", null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messages);
        }
    }

    @DeleteMapping("/cancelLike/{carId}")
    @ApiOperation("Cancel like on a car")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Like cancelled successfully"),
            @ApiResponse(code = 404, message = "Car or user not found"),
            @ApiResponse(code = 500, message = "Failed to cancel like on car")
    })
    public ResponseEntity<String> cancelLikeCar(HttpServletRequest request, @PathVariable Long carId) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            String messages = messageSource.getMessage("car.cancel.liked", null, locale);
            postService.cancelLikeCar(carId);
            return ResponseEntity.ok(messages);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            String messages = messageSource.getMessage("car.cancel.liked.filed", null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messages);
        }
    }


}