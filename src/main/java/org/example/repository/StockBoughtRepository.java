package org.example.repository;

import org.example.model.Portfolio;
import org.example.model.stocks.StockBought;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StockBoughtRepository extends JpaRepository<StockBought, Long> {
    Optional<StockBought> findByTimestamp(LocalDateTime timestamp);
    Optional<List<StockBought>> findByPortfolioAndSymbol(Portfolio portfolio, String symbol);
    Optional<List<StockBought>> findByPortfolio(Portfolio portfolio);
}
