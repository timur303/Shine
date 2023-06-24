package kg.kadyrbekov.controller;

import io.swagger.annotations.*;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.services.AvatarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/avatar")
@ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header")
})
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @ApiOperation("Upload a new avatar image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Avatar uploaded successfully"),
            @ApiResponse(code = 404, message = "Avatar not found")
    })
    @PostMapping("/uploadAvatar")
    public ResponseEntity<String> uploadAvatar(@RequestPart("file") MultipartFile file) {
        try {
            Image image = avatarService.uploadAvatar(file);
            return ResponseEntity.ok("Avatar uploaded successfully. Image ID: " + image.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload avatar.");
        }
    }

    @ApiOperation("Update an existing avatar image for a specific user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Avatar updated successfully"),
            @ApiResponse(code = 404, message = "Avatar not found")
    })
    @PatchMapping("/updateAvatar/{avatarId}")
    public ResponseEntity<String> updateAvatar(@PathVariable Long avatarId, @RequestPart("file") MultipartFile file) {
        try {
            avatarService.updateAvatar(avatarId, file);
            return ResponseEntity.ok("Avatar updated successfully for user with ID: " + avatarId);
        } catch (IOException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update avatar.");
        }
    }

    @ApiOperation("Delete an avatar image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Avatar deleted successfully"),
            @ApiResponse(code = 404, message = "Avatar not found")
    })


    @DeleteMapping("/deleteAvatar/{avatarID}")
    public ResponseEntity<String> deleteAvatar(@PathVariable Long avatarID) {
        try {
            avatarService.deleteAvatar(avatarID);
            return ResponseEntity.ok("Avatar deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during avatar deletion");
        }
    }
}
