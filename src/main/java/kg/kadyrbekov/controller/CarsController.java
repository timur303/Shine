package kg.kadyrbekov.controller;

import io.swagger.annotations.*;
import kg.kadyrbekov.dto.CarsRequest;
import kg.kadyrbekov.dto.CarsResponse;
import kg.kadyrbekov.dto.MessageInvalid;
import kg.kadyrbekov.exception.Error;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.exception.UnauthorizedException;
import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cars")
@ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header")
})
@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
public class CarsController {

    private final CarService carsService;

    private final MessageSource messageSource;


    @ApiOperation("Create car ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = " successfully"),
            @ApiResponse(code = 404, message = "not found")
    })
    @PostMapping("/carCreate")
    public ResponseEntity<CarsResponse> createCars(HttpServletRequest servletRequest,
                                                   @RequestBody CarsRequest request,
                                                   @ApiParam(value = "List of images", required = true) @RequestParam(value = "images", required = false) List<MultipartFile> images) throws NotFoundException, IOException {
        String selectedLanguage = (String) servletRequest.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

        try {
            CarsResponse response = carsService.createCar(request, images);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            String message = messageSource.getMessage("request.invalid", null, locale);
            Error error = new Error(message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CarsResponse(error));
        } catch (UnauthorizedException e) {
            String message = messageSource.getMessage("unauthorized", null, locale);
            Error error = new Error(message);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CarsResponse(error));
        }
    }


    @ApiOperation(value = "Your endpoint", authorizations = {@Authorization(value = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Car updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    @PatchMapping("/carUpdate/{id}")
    public ResponseEntity<?> updateCar(HttpServletRequest servletRequest, @PathVariable("id") Long id, CarsRequest request) {
        String selectedLanguage = (String) servletRequest.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

        try {
            carsService.update(id, request);
            String message = messageSource.getMessage("car.updated.success", null, locale);
            MessageInvalid invalid = new MessageInvalid();
            invalid.setMessages(message);
            return ResponseEntity.ok(invalid);
        } catch (NotFoundException e) {
            String messages = messageSource.getMessage("car.getID", null, locale);
            MessageInvalid invalid = new MessageInvalid();
            invalid.setMessages(messages);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(invalid);
        } catch (IOException e) {
            String message = messageSource.getMessage("car.updated.invalid", null, locale);
            MessageInvalid invalid = new MessageInvalid();
            invalid.setMessages(message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(invalid);
        }
    }

    @ApiOperation("Give a status to a car")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Car status updated successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    @PatchMapping("/giveStatus/{carId}")
    public ResponseEntity<?> giveCarStatus(HttpServletRequest servletRequest, @PathVariable("carId") Long carId, @RequestParam("status") CarsStatus status) {
        String selectedLanguage = (String) servletRequest.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

        try {
            carsService.updateCarStatus(carId, status);
            String messages = messageSource.getMessage("car.status.success", null, locale);
            MessageInvalid response = new MessageInvalid();
            response.setMessages(messages);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            String messages = messageSource.getMessage("car.getID", null, locale);
            MessageInvalid invalid = new MessageInvalid();
            invalid.setMessages(messages);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(invalid);
        }
    }

    @ApiOperation("Cancel the status of a car")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Car status canceled successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    @PostMapping("/cancelStatus/{carId}")
    public ResponseEntity<?> cancelCarStatus(HttpServletRequest request, @PathVariable("carId") Long carId) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

        try {
            carsService.cancelCarStatus(carId);
            String messages = messageSource.getMessage("car.status.cancel", null, locale);
            MessageInvalid response = new MessageInvalid();
            response.setMessages(messages);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            String messages = messageSource.getMessage("car.getID", null, locale);
            MessageInvalid invalid = new MessageInvalid();
            invalid.setMessages(messages);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(invalid);
        }
    }

    @ApiOperation("Delete a car")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Car deleted successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    @DeleteMapping("deleteCar/{id}")
    public ResponseEntity<?> deleteCar(HttpServletRequest request, @PathVariable("id") Long id) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

        try {
            carsService.deleteCar(id);
            String messages = messageSource.getMessage("car.deleted.success", null, locale);
            MessageInvalid response = new MessageInvalid();
            response.setMessages(messages);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            String messages = messageSource.getMessage("car.getID", null, locale);
            MessageInvalid invalid = new MessageInvalid();
            invalid.setMessages(messages);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(invalid);
        }
    }
}
