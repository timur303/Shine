package kg.kadyrbekov.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import kg.kadyrbekov.exception.AvatarException;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.repositories.ImagesRepository;
import kg.kadyrbekov.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/avatar")
@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
public class AvatarController {

    private final UserRepository userRepository;
    private final ImagesRepository imageRepository;
    private final Cloudinary cloudinary;


    @PostMapping("/uploadAvatar/{id}")
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with ID not found"));

            if (user.getAvatar() != null) {
                throw new AvatarException("User already has an avatar. Cannot set a new one.");
            }

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String avatarUrl = (String) uploadResult.get("secure_url");

            Image image = new Image();
            image.setName(file.getName());
            image.setOriginalFileName(file.getOriginalFilename());
            image.setContentType(file.getContentType());
            image.setSize(file.getSize());
            image.setBytes(file.getBytes());
            image.setUser(user);
            image.setUrl(avatarUrl);

            imageRepository.save(image);

            user.setAvatar(image);
            userRepository.save(user);

            return ResponseEntity.ok("Avatar uploaded successfully. Image ID: " + image.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload avatar.");
        }
    }

    @PatchMapping("/updateAvatar/{avatarId}")
    public ResponseEntity<String> updateAvatar(@PathVariable Long avatarId, @RequestPart("file") MultipartFile file) {
        try {
            Image image = imageRepository.findById(avatarId)
                    .orElseThrow(() -> new NotFoundException("Avatar with ID not found"));

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String newAvatarUrl = (String) uploadResult.get("secure_url");

            image.setBytes(file.getBytes());
            image.setUrl(newAvatarUrl);
            imageRepository.save(image);

            return ResponseEntity.ok("Avatar updated successfully for user with ID: " + image.getUser().getId());
        } catch (IOException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update avatar.");
        }
    }

    @DeleteMapping("/deleteAvatar/{avatarID}")
    public ResponseEntity<String> deleteAvatar(@PathVariable Long avatarID) {
        try {
            Image image = imageRepository.findById(avatarID)
                    .orElseThrow(() -> new NotFoundException("Avatar with ID not found"));

            cloudinary.uploader().destroy(image.getUrl(), ObjectUtils.emptyMap());
            imageRepository.delete(image);

            return ResponseEntity.ok("Avatar deleted successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during avatar deletion");
        }
    }
}
