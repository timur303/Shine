package kg.kadyrbekov.controller;

import io.swagger.annotations.*;
import kg.kadyrbekov.dto.ReviewRequest;
import kg.kadyrbekov.dto.ReviewResponse;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/review")
@ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header")
})
public class ReviewsController {

    private final ReviewService reviewService;

    @ApiOperation("Create a new review for a car")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Review created successfully"),
            @ApiResponse(code = 404, message = "Car not found")
    })
    @PostMapping("carWrite/{id}")
    public ReviewResponse create(@RequestBody ReviewRequest request, @PathVariable Long id) throws NotFoundException {
        return reviewService.create(request, id);
    }

    @ApiOperation("Update an existing review")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Review updated successfully"),
            @ApiResponse(code = 404, message = "Review not found")
    })
    @PatchMapping("carUpdate/{id}")
    public ReviewResponse update(@RequestBody ReviewRequest request, @PathVariable Long id) throws NotFoundException {
        return reviewService.update(request, id);
    }

    @ApiOperation("Delete a review by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Review deleted successfully"),
            @ApiResponse(code = 404, message = "Review not found")
    })
    @DeleteMapping("carDelete/{id}")
    public void deleteById(@PathVariable Long id) throws NotFoundException {
        reviewService.deleteById(id);
    }
}
