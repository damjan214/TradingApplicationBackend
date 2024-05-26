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

    public AvatarResponse saveAvatar(MultipartFile file, User user) throws IOException {
        Avatar userAvatar = user.getAvatar();
        userAvatar.setName(file.getOriginalFilename());
        userAvatar.setData(file.getBytes());
        avatarRepository.save(userAvatar);
        return new AvatarResponse("Avatar saved successfully!");
    }

    public void deleteAvatar(Avatar avatar) {
        avatarRepository.delete(avatar);
    }

    public void saveAvatar(Avatar avatar) {
        avatarRepository.save(avatar);
    }
}
