package kg.kadyrbekov.controller;

import io.swagger.annotations.*;
import kg.kadyrbekov.dto.CarsResponseReview;
import kg.kadyrbekov.dto.ReviewRequest;
import kg.kadyrbekov.dto.ReviewResponse;
import kg.kadyrbekov.exception.ErrorResponse;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.model.entity.Review;
import kg.kadyrbekov.repositories.ReviewRepository;
import kg.kadyrbekov.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/review")
@ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header")
})
public class ReviewsController {

    private final ReviewService reviewService;

    private final ReviewRepository reviewRepository;

    @Transactional
    @ApiOperation("Create a new review for a car")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Review created successfully"),
            @ApiResponse(code = 404, message = "Car not found")
    })
    @PostMapping("carWrite/{id}")
    public ReviewResponse create(@RequestBody ReviewRequest request, @PathVariable Long id) throws NotFoundException {
        return reviewService.create(request, id);
    }

    @Transactional
    @ApiOperation("Update an existing review")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Review updated successfully"),
            @ApiResponse(code = 404, message = "Review not found")
    })
    @PatchMapping("carUpdate/{id}")
    public ReviewResponse update(@RequestBody ReviewRequest request, @PathVariable Long id) throws NotFoundException {
        return reviewService.update(request, id);
    }

    @Transactional
    @ApiOperation("Delete a review by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Review deleted successfully"),
            @ApiResponse(code = 404, message = "Review not found")
    })
    @DeleteMapping("carDelete/{id}")
    public void deleteById(@PathVariable Long id) throws NotFoundException {
        reviewService.deleteById(id);
    }

    @Transactional
    @ApiOperation("Get review by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Review deleted successfully"),
            @ApiResponse(code = 404, message = "Review not found")
    })
    @GetMapping("carReview/{id}")
    public ResponseEntity<?> getReviewCarId(@PathVariable Long id) {
        CarsResponseReview reviewResponse = reviewService.getByIdCarsReview(id);
        if (reviewResponse != null) {
            return ResponseEntity.ok(reviewResponse);
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Entity Not Found", "Review with id " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    private static class ErrorResponse {
        private final String error;
        private final String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}
