package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.exceptions.*;
import org.example.model.Portfolio;
import org.example.model.user.Avatar;
import org.example.model.user.User;
import org.example.repository.AvatarRepository;
import org.example.repository.PortfolioRepository;
import org.example.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final AvatarRepository avatarRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final String DEFAULT_AVATAR_PATH = "D:\\Facultate\\Licenta\\trading-application-frontend\\public\\images\\NewUser.png";

    private static final String DEFAULT_AVATAR_NAME = "Default Avatar";

    public RegisterResponse register(RegisterRequest request) throws IOException {
        userRepository.findByUsername(request.getUsername()).ifPresent(user -> {
            throw new UsernameAlreadyExistsException("User with this username already exists!");
        });
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new EmailAlreadyExistsException("User with this email already exists!");
        });
        String registerToken = UUID.randomUUID().toString();

        Avatar avatar = Avatar.builder()
                .name(DEFAULT_AVATAR_NAME)
                .data(convertAvatarToBytes(DEFAULT_AVATAR_PATH))
                .build();
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .countryOfResidence(request.getCountryOfResidence())
                .avatar(avatar)
                .registerToken(registerToken)
                .registerTokenExpiration(LocalDateTime.now().plusDays(1))
                .build();
        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .cashBalance(0.0)
                .currency("usd")
                .build();
        avatarRepository.save(avatar);
        userRepository.save(user);
        portfolioRepository.save(portfolio);
        return new RegisterResponse("Registered!");
    }

    public LoginResponse login(LoginRequest request) throws BadCredentialsException, InvalidEmailException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new InvalidEmailException("User with this email not found!"));
        String jwt = jwtService.generateToken(user);
        return LoginResponse.builder()
                .token(jwt)
                .build();
    }

    public Optional<User> getUserByToken(final String jwt) {
        String extractUsername = jwtService.extractUsername(JwtService.jwtFromHeader(jwt));
        return userRepository.findByUsername(extractUsername);
    }

    public byte[] convertAvatarToBytes(String avatarPath) throws IOException {
        File file = new File(avatarPath);
        BufferedImage bufferedImage = ImageIO.read(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        return baos.toByteArray();
    }

    public DeleteResponse delete(String token) {
        User user = getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        Avatar userAvatar = user.getAvatar();
        Portfolio userPortfolio = user.getPortfolio();
        portfolioRepository.delete(userPortfolio);
        userRepository.delete(user);
        avatarRepository.delete(userAvatar);
        return new DeleteResponse("User deleted successfully!");
    }
}
