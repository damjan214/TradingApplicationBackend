package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.AvatarResponse;
import org.example.exceptions.ResourceNotFoundException;
import org.example.model.user.User;
import org.example.model.user.Avatar;
import org.example.repository.AvatarRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final AvatarRepository avatarRepository;

    private final AuthenticationService authenticationService;

    public AvatarResponse saveAvatar(MultipartFile file, String token) throws IOException {
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        Avatar userAvatar = user.getAvatar();
        userAvatar.setName(file.getOriginalFilename());
        userAvatar.setData(file.getBytes());
        avatarRepository.save(userAvatar);
        return new AvatarResponse("Avatar saved successfully!");
    }
}
