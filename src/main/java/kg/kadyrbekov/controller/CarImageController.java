package kg.kadyrbekov.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import kg.kadyrbekov.dto.MessageInvalid;
import kg.kadyrbekov.exception.AvatarException;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Cars;
import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.repositories.CarsRepository;
import kg.kadyrbekov.repositories.ImagesRepository;
import kg.kadyrbekov.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/imageCar")
@ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", required = true, value = "", dataType = "string", paramType = "header")
})
public class CarImageController {

    private final MessageSource messageSource;

    private final UserRepository userRepository;

    private final ImagesRepository imageRepository;
    private final Cloudinary cloudinary;

    private final CarsRepository carsRepository;


    @PostMapping("/uploadCarImage")
    public ResponseEntity<?> uploadCarImage(HttpServletRequest request, @RequestPart("file") MultipartFile file) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("No file uploaded.");
            }

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String avatarUrl = (String) uploadResult.get("secure_url");

            String message = messageSource.getMessage("image.upload", null, locale);
            MessageInvalid messageInvalid = new MessageInvalid();
            messageInvalid.setMessages(message);
            messageInvalid.setAvatarUrl(avatarUrl); // Add the avatar URL to the response
            return ResponseEntity.ok(messageInvalid);
        } catch (IOException e) {
            String message = messageSource.getMessage("image.failed", null, locale);
            MessageInvalid messageInvalid = new MessageInvalid();
            messageInvalid.setMessages(message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageInvalid);
        } catch (IllegalArgumentException e) {
            String message = messageSource.getMessage("no.file.uploaded", null, locale);
            MessageInvalid messageInvalid = new MessageInvalid();
            messageInvalid.setMessages(message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageInvalid);
        }
    }

}
