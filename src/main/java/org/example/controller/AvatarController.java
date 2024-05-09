package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.AvatarResponse;
import org.example.exceptions.ResourceNotFoundException;
import org.example.model.user.User;
import org.example.service.AuthenticationService;
import org.example.service.AvatarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;
    private final AuthenticationService authenticationService;

    @PostMapping("/avatar/save")
    public ResponseEntity<AvatarResponse> saveAvatar(@RequestParam("file") MultipartFile multipartFile, @RequestHeader("Authorization") String token) {
        try{
            return ResponseEntity.ok(avatarService.saveAvatar(multipartFile, token));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new AvatarResponse(e.getMessage()));
        }
    }

    @GetMapping("/avatar/get")
    public ResponseEntity<String> getAvatar(@RequestHeader("Authorization") String token) {
        try{
            User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
            return ResponseEntity.ok(Base64.getEncoder().encodeToString(user.getAvatar().getData()));
        }
        catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
