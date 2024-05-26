package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.PasswordRequest;
import org.example.dto.PasswordResponse;
import org.example.dto.UserDto;
import org.example.exceptions.NoUserUpdateException;
import org.example.exceptions.ResourceNotFoundException;
import org.example.model.user.User;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    public UserDto updateUser(String token, UserDto userDto) {
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        if (user.getFirstName().equals(userDto.getFirstName()) && user.getLastName().equals(userDto.getLastName()) && user.getUsername().equals(userDto.getUsername()) && user.getEmail().equals(userDto.getEmail()) && user.getCountryOfResidence().equals(userDto.getCountryOfResidence())) {
            throw new NoUserUpdateException("No changes were made!");
        }
        else {
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            user.setCountryOfResidence(userDto.getCountryOfResidence());
            return userRepository.save(user).toDto();
        }
    }

    public PasswordResponse updateUserPassword(String token, PasswordRequest passwordRequest) {
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Old password is incorrect!");
        } else {
            user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
            userRepository.save(user);
        }
        return new PasswordResponse("Password updated successfully!");
    }
}
