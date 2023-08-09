package kg.kadyrbekov.controller;


import io.swagger.annotations.*;
import kg.kadyrbekov.dto.UserDTO;
import kg.kadyrbekov.exception.Error;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.repositories.ImagesRepository;
import kg.kadyrbekov.repositories.UserRepository;
import kg.kadyrbekov.services.AdminService;
import lombok.RequiredArgsConstructor;
import io.swagger.annotations.ApiResponses;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
//@ApiImplicitParams({
//        @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header")
//})
@RequestMapping("/api/admin")
//@PreAuthorize("hasAnyAuthority('ADMIN')")
@Api(tags = "Admin API")
public class AdminController {

    private final AdminService adminService;

    private final MessageSource messageSource;

    private final UserRepository userRepository;

    private final ImagesRepository imagesRepository;

    @DeleteMapping("clearUser")
    public void clear() {
        userRepository.deleteAll();
    }


    @DeleteMapping("clearImages")
    public void clears() {
        imagesRepository.deleteAll();
    }

    @GetMapping("getUser/{userId}")
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
    
    @GetMapping("/getAllUsers")
    @ApiOperation(value = "Get all users", notes = "Retrieves a list of all users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = adminService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Optional<User>> searchUserByEmail(@RequestParam("email") String email) {
        Optional<User> user = adminService.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/manager")
    @ApiOperation("Assign Manager Role")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Manager role assigned successfully"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public ResponseEntity<Void> assignManagerRole(@RequestParam("email") String userEmail) throws NotFoundException {
        adminService.givesRoles(userEmail);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/block")
    @ApiOperation("Block User")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User blocked successfully"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public String blockUser(@PathVariable Long userId) throws NotFoundException {
        adminService.blockUser(userId);
        return "Successful blocked " + userId;
    }

    @PostMapping("/{userId}/unblock")
    @ApiOperation("Unblock User")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User unblocked successfully"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public String unblockUser(@PathVariable Long userId) throws NotFoundException {
        adminService.unblockUser(userId);
        return "Successful unblocked " + userId;
    }

    @DeleteMapping("/deleteUser/{id}")
    @ApiOperation("Delete User by ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User deleted successfully"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public String delete(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return "Successful removed " + id;
    }

    @GetMapping("/getPhoneNumber/{phoneNumber}")
    public ResponseEntity<String> checkUserByPhoneNumber(HttpServletRequest request, @PathVariable String phoneNumber) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isPresent()) {
            return ResponseEntity.ok("true");
        } else {
            String messages = messageSource.getMessage("user.notfoundPhone", null, locale);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messages + phoneNumber);
        }
    }


    @GetMapping("/getEmail/{email}")
    public ResponseEntity<?> getUserByEmail(HttpServletRequest request, @PathVariable String email) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale;
        if (selectedLanguage != null) {
            locale = new Locale(selectedLanguage);
        } else {
            locale = new Locale("ru");
        }

        try {
            UserDTO user = adminService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (NotFoundException e) {
            String message = messageSource.getMessage("user.notfound", null, locale);
            Error error = new Error(message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

}
