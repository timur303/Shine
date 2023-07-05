package kg.kadyrbekov.controller;

import io.swagger.annotations.*;
import kg.kadyrbekov.dto.CarsRequest;
import kg.kadyrbekov.dto.CarsResponse;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.exception.UnauthorizedException;
import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.model.enums.CarsStatus;
import kg.kadyrbekov.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cars")
@ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header")
})
@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
public class CarsController {

    private final CarService carsService;


    @ApiOperation("Create car ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = " successfully"),
            @ApiResponse(code = 404, message = "not found")
    })
    @PostMapping("/carCreate")
    public ResponseEntity<?> createCars(
            @RequestBody CarsRequest request,
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

        try {
            CarsResponse response = carsService.createCar(request, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }


    @ApiOperation(value = "Your endpoint", authorizations = {@Authorization(value = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Car updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    @PatchMapping("/carUpdate/{id}")
    public ResponseEntity<String> updateCar(@PathVariable("id") Long id, CarsRequest request) {
        try {
            carsService.update(id, request);
            return ResponseEntity.ok("Car updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Invalid request payload");
        }
    }

    @ApiOperation("Give a status to a car")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Car status updated successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    @PatchMapping("/giveStatus/{carId}")
    public ResponseEntity<String> giveCarStatus(@PathVariable("carId") Long carId, @RequestParam("status") CarsStatus status) {
        try {
            carsService.updateCarStatus(carId, status);
            return ResponseEntity.ok("Car status updated successfully");
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
    public ResponseEntity<String> cancelCarStatus(@PathVariable("carId") Long carId) {
        try {
            carsService.cancelCarStatus(carId);
            return ResponseEntity.ok("Car status canceled successfully");
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
    public ResponseEntity<String> deleteCar(@PathVariable("id") Long id) {
        try {
            carsService.deleteCar(id);
            return ResponseEntity.ok("Car deleted successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
