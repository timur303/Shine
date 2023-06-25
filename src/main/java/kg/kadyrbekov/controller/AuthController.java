package kg.kadyrbekov.controller;


import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import kg.kadyrbekov.config.jwt.JwtTokenUtil;
import kg.kadyrbekov.dto.AuthResponse;
import kg.kadyrbekov.dto.LoginRequest;
import kg.kadyrbekov.dto.UserRequest;
import kg.kadyrbekov.dto.UserResponse;
import kg.kadyrbekov.exception.ErrorResponse;
import kg.kadyrbekov.exception.UserRegistrationException;
import kg.kadyrbekov.mapper.LoginMapper;
import kg.kadyrbekov.mapper.LoginResponse;
import kg.kadyrbekov.mapper.ValidationType;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.repositories.UserRepository;
import kg.kadyrbekov.services.ResetPasswordService;
import kg.kadyrbekov.services.UserService;
import kg.kadyrbekov.services.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jwt")
public class AuthController {
    private final LoginMapper loginMapper;

    private final ResetPasswordService resetPasswordService;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final UserServiceImpl userServiceImpl;

    private final UserService userService;


    @PostMapping("login")
    public ResponseEntity<LoginResponse> getLogin(@RequestBody LoginRequest request) {
        try {
            UserDetails userDetails = null;
            Authentication authentication = null;

            if (request.getEmail() != null) {
                // using email
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
                );
                userDetails = (UserDetails) authentication.getPrincipal();
            } else if (request.getPhoneNumber() != null) {
                // using phoneNumber
                userDetails = userServiceImpl.loadUserByUsername(request.getPhoneNumber());
                authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            }

            if (userDetails != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String token = jwtTokenUtil.generateToken(userDetails);
                User user = userRepository.findByEmail(userDetails.getUsername()).get();

                // Set the values in LoginResponse
                LoginResponse loginResponse = loginMapper.loginView(token, ValidationType.SUCCESSFUL, user);
                loginResponse.setJwtToken(token);
                loginResponse.setMessage("Login successful");
                loginResponse.setAuthorities(userDetails.getAuthorities().toString());

                return ResponseEntity.ok().body(loginResponse);
            } else {
                // Authentication failed
                LoginResponse loginResponse = loginMapper.loginView("", ValidationType.LOGIN_FAILED, null);
                loginResponse.setMessage("Invalid username or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
            }
        } catch (BadCredentialsException ex) {
            // Authentication failed
            LoginResponse loginResponse = loginMapper.loginView("", ValidationType.LOGIN_FAILED, null);
            loginResponse.setMessage("Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        }
    }


    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User registered successfully"),
            @ApiResponse(code = 400, message = "Invalid user data")
    })

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {
        try {

            if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
                String errorMessage = "Email already exists";
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
            }

            if (userRepository.findByPhoneNumber(userRequest.getPhoneNumber()).isPresent()) {
                String errorMessage = "Phone number already exists";
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
            }


            UserResponse response = userService.register(userRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserRegistrationException e) {
            String errorMessage = "User registration failed";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }


    @PatchMapping("/updateUser")
    public UserResponse update(@RequestBody UserRequest request) {
        return userService.updateProfile(request);
    }


    @PostMapping("/forgot_password")
    public String processForgotPassword(@RequestParam("email") String email, HttpServletRequest request) {
        return resetPasswordService.processForgotPassword(email, request);
    }

    @PostMapping("/reset_password")
    public AuthResponse resetPassword(@RequestParam String token, @RequestParam String password, @RequestParam String confirmPassword) {
        return resetPasswordService.save(token, password, confirmPassword);
    }
}
