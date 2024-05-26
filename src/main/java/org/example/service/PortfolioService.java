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

    public PaymentDetails getCashBalance(User user) {
        Portfolio userPortfolio = portfolioRepository.findByUser(user).get();
        return new PaymentDetails().builder()
                .amount(userPortfolio.getBalanceAvailable().longValue())
                .currency(userPortfolio.getCurrency())
                .build();
    }

    public void savePortfolio(Portfolio portfolio) {
        portfolioRepository.save(portfolio);
    }

    public Portfolio getPortfolio(User user) {
        return portfolioRepository.findByUser(user).get();
    }

    public void deletePortfolio(Portfolio userPortfolio) {
        portfolioRepository.delete(userPortfolio);
    }
}
