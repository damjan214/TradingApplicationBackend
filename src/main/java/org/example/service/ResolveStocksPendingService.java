package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.StockBuyRequest;
import org.example.dto.StockSellRequest;
import org.example.model.stocks.StockPending;
import org.example.model.stocks.StockStatus;
import org.example.model.user.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ResolveStocksPendingService {
    private final Logger LOGGER = Logger.getLogger(ResolveStocksPendingService.class.getName());
    private final StockBoughtService stockBoughtService;
    private final StockDataService stockDataService;
    private final StockPendingService stockPendingService;
    private final PortfolioService portfolioService;

    @Async
    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    public void resolveStocksPending() {
        LOGGER.info("Resolving stocks pending...");
        LOGGER.info("Checking market status...");
        String marketStatus = stockDataService.checkMarketStatus();
        if (!Boolean.parseBoolean(marketStatus)) {
            LOGGER.info("Market is closed!");
            return;
        } else {
            LOGGER.info("Market is open!");
            List<StockPending> stockPendings = stockPendingService.findAll();
            for (StockPending stockPending : stockPendings) {
                User user = stockPending.getPortfolio().getUser();
                if (stockPending.getStockStatus().equals(StockStatus.BOUGHT)) {
                    StockBuyRequest stockBuyRequest = StockBuyRequest.builder()
                            .name(stockPending.getName())
                            .symbol(stockPending.getSymbol())
                            .balanceInvested(stockPending.getBalanceInvested())
                            .build();
                    stockBoughtService.buyStockFromPending(user, stockBuyRequest);
                }
            }
            for (StockPending stockPending : stockPendings) {
                if (stockPending.getStockStatus().equals(StockStatus.SOLD)) {
                    User user = stockPending.getPortfolio().getUser();
                    StockSellRequest stockSellRequest = StockSellRequest.builder()
                            .name(stockPending.getName())
                            .symbol(stockPending.getSymbol())
                            .timestamp(stockPending.getTimestamp().toString())
                            .build();
                    stockBoughtService.sellStock(user, stockSellRequest);
                }
            }
            stockPendingService.deleteAll(stockPendings);
        }
    }
}
