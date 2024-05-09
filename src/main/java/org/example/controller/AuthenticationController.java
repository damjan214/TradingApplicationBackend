package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.exceptions.*;
import org.example.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) throws BadCredentialsException, InvalidEmailException {
        try {
            return ResponseEntity.ok(service.login(request));
        } catch (BadCredentialsException | InvalidEmailException e) {
            return ResponseEntity.badRequest().body(new LoginResponse(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) throws IOException {
        try {
            return ResponseEntity.ok(service.register(request));
        } catch (IOException | UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(new RegisterResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<DeleteResponse> delete(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(service.delete(token));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(new DeleteResponse(e.getMessage()));
        }
    }
}

