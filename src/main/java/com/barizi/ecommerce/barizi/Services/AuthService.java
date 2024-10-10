package com.barizi.ecommerce.barizi.Services;

import com.barizi.ecommerce.barizi.Config.JwtService;
import com.barizi.ecommerce.barizi.DTOs.Request.LoginRequest;
import com.barizi.ecommerce.barizi.DTOs.Request.UserCreationRequest;
import com.barizi.ecommerce.barizi.DTOs.Response.AuthResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.User;
import com.barizi.ecommerce.barizi.Entities.Enums.Role;
import com.barizi.ecommerce.barizi.Entities.UserEntity;
import com.barizi.ecommerce.barizi.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;


    private static final String PHONE_NUMBER_REGEX = "^\\+?[0-9]{10,15}$";

    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Use the regex pattern to match the input phone number
        return Pattern.matches(PHONE_NUMBER_REGEX, phoneNumber);
    }
    public ResponseEntity<AuthResponse> registerUser(UserCreationRequest user){
        AuthResponse res = new AuthResponse();
        try {
            //check if user already exists
            AtomicReference<AuthResponse> resp = new AtomicReference<>(new AuthResponse());

            Optional<UserEntity> existingUserByUsername = userRepository.findByUsername(user.getUserName());
//            log.info("email "+savedEmail);

            // Check if email exists
            Optional<UserEntity> existingUserByEmail = userRepository.findByEmail(user.getEmail());

            // If either username or email already exists, return 409 status
            if (existingUserByUsername.isPresent() || existingUserByEmail.isPresent()){
                if (existingUserByUsername.isPresent()){
                    resp.get().setMessage("User with that username already exists");
                    resp.get().setStatus(409);
                } else {
                    resp.get().setMessage("User with that email already exists");
                    resp.get().setStatus(409);
                }
            } else if (!isValidPhoneNumber(user.getPhoneNumber())) {
                resp.get().setMessage("Please enter a valid phone number");
                resp.get().setStatus(400);
            } else {
                log.info("role "+user.getRole());
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                UserEntity newUser = UserEntity.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail().toLowerCase())
                        .phoneNumber(user.getPhoneNumber())
                        .userName(user.getUserName() != null ? user.getUserName() : "")
                        .password(encodedPassword)
                        .role(user.getRole() != null ? Role.valueOf(user.getRole().toUpperCase()): Role.CUSTOMER)
                        .build();
                userRepository.save(newUser);
                var jwtToken = jwtService.generateToken(newUser);
                User registeredUser = User.builder()
                        .firstName(newUser.getFirstName())
                        .lastName(newUser.getLastName())
                        .userName(newUser.getUsername())
                        .email(user.getEmail().toLowerCase())
                        .phoneNumber(user.getPhoneNumber())
                        .build();

                resp.get().setMessage("User created successfully for " + user.getFirstName());
                resp.get().setJwt(jwtToken);
                resp.get().setUser(registeredUser);
                resp.get().setStatus(200);
            }
            return ResponseEntity.status(resp.get().getStatus()).body(resp.get());
        } catch (Exception e){
            log.error("An error occured "+e.getMessage());
            res.setMessage("The system could not create user. Please try again later");
            res.setStatus(500);
            return ResponseEntity.internalServerError().body(res);
        }
    }

    public ResponseEntity<AuthResponse> login(LoginRequest user){
        AuthResponse response = new AuthResponse();

        try {
            //check if that user exists
            AtomicReference<AuthResponse> res = new AtomicReference<>(new AuthResponse());
            Optional<UserEntity> existingUser = userRepository.findByEmail(user.getEmail());
            existingUser.ifPresentOrElse(
                    user1 -> {
                        if (!passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())){
                            res.get().setMessage("Wrong credentials");
                            res.get().setStatus(401);
                        } else {
                            try {
                                var jwtToken = jwtService.generateToken(existingUser.get());
                                User loggedInUser = User.builder()
                                        .firstName(existingUser.get().getFirstName())
                                        .lastName(existingUser.get().getLastName())
                                        .email(existingUser.get().getEmail())
                                        .phoneNumber(existingUser.get().getPhoneNumber())
                                        .build();


                                log.info("user "+existingUser.get());
                                res.get().setMessage("Login successful");
                                res.get().setJwt(jwtToken);
                                res.get().setStatus(200);
                                res.get().setUser(loggedInUser);
                            } catch (Exception e){
                                log.error("Error trying to login user "+e.getMessage());
                                res.get().setStatus(500);
                                res.get().setMessage("An error occured while logging user in. Try again later");
                            }
                        }
                    },
                    () -> {
                        res.get().setStatus(404);
                        res.get().setMessage("No one with that email found in system");
                    }
            );
            return ResponseEntity.ok().body(res.get());

        } catch (Exception e){
            log.error("An error occured "+e.getMessage());
            response.setMessage("Could not login user");
            response.setStatus(500);
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
