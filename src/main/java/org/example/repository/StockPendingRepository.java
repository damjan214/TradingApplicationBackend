package org.example.repository;

import org.example.model.Portfolio;
import org.example.model.stocks.StockPending;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StockPendingRepository extends JpaRepository<StockPending, Long> {
    Optional<List<StockPending>> findByPortfolio(Portfolio portfolioId);
    Optional<StockPending> findByTimestamp(LocalDateTime timestamp);
    Optional<StockPending> findByPortfolioAndSymbolAndTimestamp(Portfolio userPortfolio, String symbol, LocalDateTime timestamp);
}
