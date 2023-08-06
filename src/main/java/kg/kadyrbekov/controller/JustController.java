package kg.kadyrbekov.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kg.kadyrbekov.dto.CarsResponse;
import kg.kadyrbekov.dto.MessageInvalid;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.model.entity.Cars;
import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/just")
public class JustController {


    private final MessageSource messageSource;

    private final CarService carsService;



    @GetMapping("/getAllByStatusJust/{status}")
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

    @GetMapping("/getAllCarsJust")
    @ApiOperation("Get all cars")
    @ApiResponse(code = 200, message = "Successfully retrieved all cars")
    public ResponseEntity<List<CarsResponse>> getAllCars() {
        List<CarsResponse> carsList = carsService.getAllCarsWithout();

        if (carsList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(carsList);
    }

    @GetMapping("/getCarByIDJust/{id}")
    @ApiOperation("Get car by ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved the car"),
            @ApiResponse(code = 404, message = "Car not found with the specified ID")
    })
    public ResponseEntity<?> getCarById(HttpServletRequest request, @PathVariable("id") Long id) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

        try {
            CarsResponse response = carsService.getByIdCarsWithout(id);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            String messages = messageSource.getMessage("car.getID", null, locale);
            MessageInvalid response = new MessageInvalid();
            response.setMessages(messages);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
