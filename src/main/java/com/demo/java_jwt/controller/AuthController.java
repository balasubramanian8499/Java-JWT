package com.demo.java_jwt.controller;

import com.demo.java_jwt.exception.ResourceNotFoundException;
import com.demo.java_jwt.model.response.*;
import com.demo.java_jwt.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) throws ResourceNotFoundException {
        if (loginRequest.getUserName() == null && loginRequest.getPassword() == null) {
            throw new ResourceNotFoundException("INVALID_CREDENTIAL");
        }
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh")
    public LoginResponse refreshToken(@RequestParam String refreshToken) throws ResourceNotFoundException {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/signUp")
    public SuccessResponse<Object> signup(@RequestBody UserSignupRequestVO request) {
        return authService.signup(request);
    }

}
