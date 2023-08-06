package kg.kadyrbekov.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kg.kadyrbekov.dto.*;
import kg.kadyrbekov.exception.AvatarException;
import kg.kadyrbekov.exception.Error;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.exception.UserUpdateException;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.repositories.ImagesRepository;
import kg.kadyrbekov.repositories.UserRepository;
import kg.kadyrbekov.services.AdminService;
import kg.kadyrbekov.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("/api/avatar")
@ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", required = true, value = "", dataType = "string", paramType = "header")
})
public class AvatarController {

    private final AdminService adminService;

    private final MessageSource messageSource;
    private final UserRepository userRepository;
    private final ImagesRepository imageRepository;
    private final Cloudinary cloudinary;

    private final UserService userService;

    @PostMapping("/uploadAvatar")
    public ResponseEntity<?> uploadAvatar(HttpServletRequest request, @RequestPart("file") MultipartFile file) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

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
            MessageInvalid messageInvalid = new MessageInvalid();
            messageInvalid.setMessages(message);
            messageInvalid.setAvatarUrl(avatarUrl);
            return ResponseEntity.ok(messageInvalid);
        } catch (IOException e) {
            String message = messageSource.getMessage("avatar.failed", null, locale);
            MessageInvalid messageInvalid = new MessageInvalid();
            messageInvalid.setMessages(message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageInvalid);
        }
    }

    @PatchMapping("/updateAvatar/{avatarId}")
    public ResponseEntity<?> updateAvatar(HttpServletRequest request, @PathVariable Long avatarId, @RequestPart("file") MultipartFile file) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

        try {
            Image image = imageRepository.findById(avatarId)
                    .orElseThrow(() -> new NotFoundException("Avatar with ID not found"));

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String newAvatarUrl = (String) uploadResult.get("secure_url");

            image.setBytes(file.getBytes());
            image.setUrl(newAvatarUrl);
            imageRepository.save(image);
            String messages = messageSource.getMessage("updated.success", null, locale);
            MessageInvalid messageInvalid = new MessageInvalid();
            messageInvalid.setMessages(messages);
            messageInvalid.setAvatarUrl(newAvatarUrl);
            return ResponseEntity.ok(messageInvalid);
        } catch (IOException | NotFoundException e) {
            String messages = messageSource.getMessage("update.failed", null, locale);
            MessageInvalid messageInvalid = new MessageInvalid();
            messageInvalid.setMessages(messages);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageInvalid);
        }
    }


    @PatchMapping("/updateUser")
    public ResponseEntity<UserResponse> update(@RequestBody UpdateUserRequest request, HttpServletRequest servletRequest) {
        String selectedLanguage = (String) servletRequest.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }
//
        try {
            UserResponse response = userService.updateProfile(request);
            return ResponseEntity.ok(response);
        } catch (UserUpdateException e) {
            String errorMessage = messageSource.getMessage("update.failedUser", null, locale);
            UserResponse response = new UserResponse();
            response.setErrorMessage(errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @GetMapping("getUserAvatar/{userId}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success ", response = UserDTO.class),
            @ApiResponse(code = 404, message = "User not found ", response = Error.class)
    })
    public ResponseEntity<?> getUserByID(HttpServletRequest request, @PathVariable Long userId) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

        try {
            UserDTO user = adminService.getUserByID(userId);
            return ResponseEntity.ok(user);
        } catch (NotFoundException e) {
            String message = messageSource.getMessage("user.notfound", null, locale);
            Error error = new Error(message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setAge(user.getAge());
            userDTO.setEmail(user.getEmail());
            userDTO.setId(user.getId());
            if (user.getAvatar() != null) {
                userDTO.setAvatarUrl(user.getAvatar().getUrl());
            } else {
                userDTO.setAvatarUrl(null);
            }
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setPhoneNumber(user.getPhoneNumber());

            userDTOs.add(userDTO);
        }

        return userDTOs;
    }


    @DeleteMapping("/deleteAvatar/{avatarID}")
    public ResponseEntity<?> deleteAvatar(HttpServletRequest request, @PathVariable Long avatarID) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

        try {
            String message = messageSource.getMessage("avatar.getID", null, locale);
            MessageInvalid responses = new MessageInvalid();
            responses.setMessages(message);
            Image image = imageRepository.findById(avatarID).get();

            cloudinary.uploader().destroy(image.getUrl(), ObjectUtils.emptyMap());
            imageRepository.delete(image);
            String messages = messageSource.getMessage("avatar.deleted", null, locale);
            MessageInvalid response = new MessageInvalid();
            response.setMessages(messages);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            String messages = messageSource.getMessage("avatar.getID", null, locale);
            MessageInvalid invalid = new MessageInvalid();
            invalid.setMessages(messages);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(invalid);
        }
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found!"));
    }

}
