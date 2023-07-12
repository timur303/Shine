package kg.kadyrbekov.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import kg.kadyrbekov.exception.AvatarException;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.repositories.ImagesRepository;
import kg.kadyrbekov.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/avatar")
@ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", required = true, value = "", dataType = "string", paramType = "header")
})
public class AvatarController {

    private final MessageSource messageSource;
    private final UserRepository userRepository;
    private final ImagesRepository imageRepository;
    private final Cloudinary cloudinary;

    @PostMapping("/uploadAvatar")
    public ResponseEntity<String> uploadAvatar(HttpServletRequest request, @RequestPart("file") MultipartFile file) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            User user = getAuthenticatedUser();

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
            String message = messageSource.getMessage("avatar.upload", null, locale);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            String message = messageSource.getMessage("avatar.failed", null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

    @PatchMapping("/updateAvatar/{avatarId}")
    public ResponseEntity<String> updateAvatar(HttpServletRequest request, @PathVariable Long avatarId, @RequestPart("file") MultipartFile file) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            Image image = imageRepository.findById(avatarId)
                    .orElseThrow(() -> new NotFoundException("Avatar with ID not found"));

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String newAvatarUrl = (String) uploadResult.get("secure_url");

            image.setBytes(file.getBytes());
            image.setUrl(newAvatarUrl);
            imageRepository.save(image);
            String messages = messageSource.getMessage("updated.success", null, locale);
            return ResponseEntity.ok(messages);
        } catch (IOException | NotFoundException e) {
            String messages = messageSource.getMessage("update.failed", null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messages);
        }
    }

    @DeleteMapping("/deleteAvatar/{avatarID}")
    public ResponseEntity<String> deleteAvatar(HttpServletRequest request, @PathVariable Long avatarID) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            Image image = imageRepository.findById(avatarID)
                    .orElseThrow(() -> new NotFoundException("Avatar with ID not found"));

            cloudinary.uploader().destroy(image.getUrl(), ObjectUtils.emptyMap());
            imageRepository.delete(image);
            String messages = messageSource.getMessage("avatar.deleted", null, locale);
            return ResponseEntity.ok(messages);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            String messages = messageSource.getMessage("avatar.deleted.failed", null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messages);
        }
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found!"));
    }

}
