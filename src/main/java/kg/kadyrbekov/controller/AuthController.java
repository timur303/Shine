package kg.kadyrbekov.controller;


import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import kg.kadyrbekov.config.jwt.JwtTokenUtil;
import kg.kadyrbekov.dto.AuthResponse;
import kg.kadyrbekov.dto.LoginRequest;
import kg.kadyrbekov.dto.UserRequest;
import kg.kadyrbekov.dto.UserResponse;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jwt")
public class AuthController {
    private final LoginMapper loginMapper;

    private final PasswordEncoder passwordEncoder;

    private final ResetPasswordService resetPasswordService;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final UserServiceImpl userServiceImpl;

    private final UserService userService;

    private final MessageSource messageSource;


    @PostMapping("login")
    public ResponseEntity<LoginResponse> getLogin(HttpServletRequest request, @RequestBody LoginRequest loginRequest) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);

        try {
            UserDetails userDetails = null;
            Authentication authentication = null;

            if (StringUtils.isNotBlank(loginRequest.getEmail()) && StringUtils.isNotBlank(loginRequest.getPassword())) {
                // using email
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
                );
                userDetails = (UserDetails) authentication.getPrincipal();
            } else if (StringUtils.isNotBlank(loginRequest.getPhoneNumber()) && StringUtils.isNotBlank(loginRequest.getPassword())) {
                // using phoneNumber
                userDetails = userServiceImpl.loadUserByUsername(loginRequest.getPhoneNumber());
                if (userDetails != null) {
                    if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                        throw new BadCredentialsException("Invalid password");
                    }
                } else {
                    throw new UsernameNotFoundException("User with phone number - " + loginRequest.getPhoneNumber() + " not found");
                }
                authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            }

            if (userDetails != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String token = jwtTokenUtil.generateToken(userDetails);
                User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);

                LoginResponse loginResponse = loginMapper.loginView(token, ValidationType.SUCCESSFUL, user);
                loginResponse.setJwtToken(token);
                String messages = messageSource.getMessage("login.successfully", null, locale);
                loginResponse.setMessage(messages);
                loginResponse.setUserId(user != null ? user.getId() : null);

                return ResponseEntity.ok().body(loginResponse);
            } else {
                // Authentication failed
                LoginResponse loginResponse = loginMapper.loginView("", ValidationType.LOGIN_FAILED, null);
                String messages = messageSource.getMessage("login.invalid", null, locale);
                loginResponse.setMessage(messages);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
            }
        } catch (BadCredentialsException ex) {
            // Authentication failed
            LoginResponse loginResponse = loginMapper.loginView("", ValidationType.LOGIN_FAILED, null);
            String messages = messageSource.getMessage("login.invalid", null, locale);
            loginResponse.setMessage(messages);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        } catch (UsernameNotFoundException ex) {
            // User not found
            LoginResponse loginResponse = loginMapper.loginView("", ValidationType.LOGIN_FAILED, null);
            String messages = messageSource.getMessage("login.invalid", null, locale);
            loginResponse.setMessage(messages);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        }
    }



    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User registered successfully"),
            @ApiResponse(code = 400, message = "Invalid user data")
    })
    @PostMapping("/registration")
    public ResponseEntity<UserResponse> registerUser(HttpServletRequest request, @RequestBody UserRequest userRequest) {
        String selectedLanguage = (String) request.getSession().getAttribute("language");
        Locale locale = new Locale(selectedLanguage);


        try {
            if (StringUtils.isBlank(userRequest.getEmail()) || StringUtils.isBlank(userRequest.getPassword()) || StringUtils.isBlank(userRequest.getPhoneNumber())) {
                String errorMessage = messageSource.getMessage("registration.invalid", null, locale);
                UserResponse response = new UserResponse();
                response.setErrorMessage(errorMessage);
                return ResponseEntity.badRequest().body(response);
            }

            if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
                String errorMessage = messageSource.getMessage("car.getID", null, locale);
                UserResponse response = new UserResponse();
                response.setErrorMessage(errorMessage);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            if (userRepository.findByPhoneNumber(userRequest.getPhoneNumber()).isPresent()) {
                String errorMessage = messageSource.getMessage("phone.exists", null, locale);
                UserResponse response = new UserResponse();
                response.setErrorMessage(errorMessage);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            UserResponse response = userService.register(userRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserRegistrationException e) {
            String errorMessage = messageSource.getMessage("registration.failed", null, locale);
            UserResponse response = new UserResponse();
            response.setErrorMessage(errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
