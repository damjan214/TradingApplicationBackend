package org.example.repository;

import org.example.model.stocks.StockData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockDataRepository extends JpaRepository<StockData, Long> {

    List<StockData> findBySymbol(String symbol);
}
