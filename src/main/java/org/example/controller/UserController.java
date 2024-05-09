package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.PasswordRequest;
import org.example.dto.PasswordResponse;
import org.example.dto.UserDto;
import org.example.exceptions.NoUserUpdateException;
import org.example.exceptions.ResourceNotFoundException;
import org.example.service.AuthenticationService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthenticationService authenticationService;

    @GetMapping("/user/get")
    public ResponseEntity<UserDto> getUserByToken(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!")).toDto());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(new UserDto().builder().message(e.getMessage()).build());
        }
    }

    @PutMapping("/user/update/data")
    public ResponseEntity<UserDto> updateUser(@RequestHeader("Authorization") String token, @RequestBody UserDto userDto) {
        try {
            return ResponseEntity.ok(userService.updateUser(token, userDto));
        } catch (ResourceNotFoundException | NoUserUpdateException e) {
            return ResponseEntity.badRequest().body(new UserDto().builder().message(e.getMessage()).build());
        }
    }

    @PutMapping("/user/update/password")
    public ResponseEntity<PasswordResponse> updateUserPassword(@RequestHeader("Authorization") String token, @RequestBody PasswordRequest passwordRequest) {
        try {
            return ResponseEntity.ok(userService.updateUserPassword(token, passwordRequest));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(new PasswordResponse(e.getMessage()));
        }
    }
}
