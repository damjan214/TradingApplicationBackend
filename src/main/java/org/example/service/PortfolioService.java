package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.PaymentDetails;
import org.example.exceptions.ResourceNotFoundException;
import org.example.model.Portfolio;
import org.example.model.user.User;
import org.example.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    private final AuthenticationService authenticationService;

    public PaymentDetails getCashBalance(String token) {
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        Portfolio userPortfolio = portfolioRepository.findByUser(user).get();
        return new PaymentDetails().builder()
                .amount(userPortfolio.getCashBalance().longValue())
                .currency(userPortfolio.getCurrency())
                .build();
    }
}
