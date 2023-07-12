package kg.kadyrbekov.controller;

import io.swagger.annotations.*;
import kg.kadyrbekov.dto.CarsRequest;
import kg.kadyrbekov.dto.CarsResponse;
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
    @PostMapping(value = "/carCreate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCars(HttpServletRequest servletRequest,
                                        CarsRequest request,
                                        @RequestPart(value = "file1", required = false) MultipartFile file1,
                                        @RequestPart(value = "file2", required = false) MultipartFile file2,
                                        @RequestPart(value = "file3", required = false) MultipartFile file3,
                                        @RequestPart(value = "file4", required = false) MultipartFile file4,
                                        @RequestPart(value = "file5", required = false) MultipartFile file5,
                                        @RequestPart(value = "file6", required = false) MultipartFile file6,
                                        @RequestPart(value = "file7", required = false) MultipartFile file7,
                                        @RequestPart(value = "file8", required = false) MultipartFile file8,
                                        @RequestPart(value = "file9", required = false) MultipartFile file9,
                                        @RequestPart(value = "file10", required = false) MultipartFile file10,
                                        @RequestPart(value = "file11", required = false) MultipartFile file11,
                                        @RequestPart(value = "file12", required = false) MultipartFile file12
    ) throws NotFoundException, IOException {
        List<MultipartFile> files = new ArrayList<>();

        if (file1 != null) files.add(file1);
        if (file2 != null) files.add(file2);
        if (file3 != null) files.add(file3);
        if (file4 != null) files.add(file4);
        if (file5 != null) files.add(file5);
        if (file6 != null) files.add(file6);
        if (file7 != null) files.add(file7);
        if (file8 != null) files.add(file8);
        if (file9 != null) files.add(file9);
        if (file10 != null) files.add(file10);
        if (file11 != null) files.add(file11);
        if (file12 != null) files.add(file12);

        String selectedLanguage = (String) servletRequest.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            CarsResponse response = carsService.createCar(request, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            String message = messageSource.getMessage("request.invalid", null, locale);
            Error error = new Error(message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (UnauthorizedException e) {
            String message = messageSource.getMessage("unauthorized", null, locale);
            Error error = new Error(message);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }


    @ApiOperation(value = "Your endpoint", authorizations = {@Authorization(value = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Car updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    @PatchMapping("/carUpdate/{id}")
    public ResponseEntity<String> updateCar(HttpServletRequest servletRequest, @PathVariable("id") Long id, CarsRequest request) {
        String selectedLanguage = (String) servletRequest.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            carsService.update(id, request);
            String message = messageSource.getMessage("car.updated.success", null, locale);
            return ResponseEntity.ok(message);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            String message = messageSource.getMessage("car.updated.invalid", null, locale);
            return ResponseEntity.badRequest().body(message);
        }
    }

    @ApiOperation("Give a status to a car")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Car status updated successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    @PatchMapping("/giveStatus/{carId}")
    public ResponseEntity<String> giveCarStatus(HttpServletRequest servletRequest, @PathVariable("carId") Long carId, @RequestParam("status") CarsStatus status) {
        String selectedLanguage = (String) servletRequest.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            carsService.updateCarStatus(carId, status);
            String messages = messageSource.getMessage("car.status.success", null, locale);
            return ResponseEntity.ok(messages);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation("Cancel the status of a car")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Car status canceled successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    @PostMapping("/cancelStatus/{carId}")
    public ResponseEntity<String> cancelCarStatus(HttpServletRequest request, @PathVariable("carId") Long carId) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            carsService.cancelCarStatus(carId);
            String messages = messageSource.getMessage("car.status.cancel", null, locale);
            return ResponseEntity.ok(messages);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation("Delete a car")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Car deleted successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    @DeleteMapping("deleteCar/{id}")
    public ResponseEntity<String> deleteCar(HttpServletRequest request, @PathVariable("id") Long id) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            carsService.deleteCar(id);
            String messages = messageSource.getMessage("car.deleted.success", null, locale);
            return ResponseEntity.ok(messages);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
