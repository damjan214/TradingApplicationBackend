package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.exceptions.ResourceNotFoundException;
import org.example.model.Portfolio;
import org.example.model.stocks.StockBought;
import org.example.model.stocks.StockData;
import org.example.model.stocks.StockPending;
import org.example.model.stocks.StockStatus;
import org.example.model.user.User;
import org.example.repository.StockBoughtRepository;
import org.example.repository.StockPendingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockPendingService {
    private final StockPendingRepository stockPendingRepository;
    private final StockBoughtRepository stockBoughtRepository;
    private final AuthenticationService authenticationService;
    private final StockDataService stockDataService;
    private final PortfolioService portfolioService;

    public StockResponse buyStockPending(StockBuyRequest stockBuyRequest, User user) {
        Portfolio portfolio = user.getPortfolio();
        StockPending stockPending = StockPending.builder()
                .name(stockBuyRequest.getName())
                .symbol(stockBuyRequest.getSymbol())
                .balanceInvested(stockBuyRequest.getBalanceInvested())
                .stockStatus(StockStatus.BOUGHT)
                .quantity(0.0)
                .timestamp(LocalDateTime.now())
                .portfolio(portfolio)
                .build();
        portfolio.setBalanceAvailable(portfolio.getBalanceAvailable() - stockBuyRequest.getBalanceInvested());
        portfolioService.savePortfolio(portfolio);
        stockPendingRepository.save(stockPending);
        return new StockResponse("Stock added to pending list");
    }

    public StockResponse cancelBuyStockPending(String token, StockPendingDto stockPendingDto) {
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        StockPending stockPending = stockPendingRepository.findByTimestamp(LocalDateTime.parse(stockPendingDto.getTimestamp())).get();
        Portfolio portfolio = stockPending.getPortfolio();
        portfolio.setBalanceAvailable(portfolio.getBalanceAvailable() + stockPending.getBalanceInvested());
        portfolioService.savePortfolio(portfolio);
        stockPendingRepository.delete(stockPending);
        return new StockResponse("Stock removed from pending list");
    }

    public StockResponse sellStockPending(StockSellRequest stockSellRequest, User user) {
        Portfolio portfolio = user.getPortfolio();
        StockBought stockBought = stockBoughtRepository.findByTimestamp(LocalDateTime.parse(stockSellRequest.getTimestamp())).get();
        StockPending stockPending = StockPending.builder()
                .name(stockBought.getName())
                .symbol(stockBought.getSymbol())
                .balanceInvested(stockBought.getBalanceInvested())
                .quantity(stockBought.getQuantity())
                .timestamp(stockBought.getTimestamp())
                .stockStatus(StockStatus.SOLD)
                .portfolio(portfolio)
                .build();
        stockPendingRepository.save(stockPending);
        return new StockResponse("Stock added to pending list");
    }

    public StockResponse cancelSellStockPending(String token, StockPendingDto stockPendingDto) {
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        StockPending stockPending = stockPendingRepository.findByTimestamp(LocalDateTime.parse(stockPendingDto.getTimestamp())).get();
        Portfolio portfolio = stockPending.getPortfolio();
        portfolioService.savePortfolio(portfolio);
        stockPendingRepository.delete(stockPending);
        return new StockResponse("Stock removed from pending list");
    }

    public StockResponse sellAllStocksBySymbol(User user, SellAllRequest sellAllRequest) {
        Portfolio portfolio = user.getPortfolio();
        List<StockBought> stockBoughts = stockBoughtRepository.findByPortfolio(user.getPortfolio()).get();
        String symbol = sellAllRequest.getSymbol();
        for (StockBought stockBought : stockBoughts) {
            if (stockBought.getSymbol().equals(symbol)) {
                StockPending stockPending = StockPending.builder()
                        .name(stockBought.getName())
                        .symbol(stockBought.getSymbol())
                        .balanceInvested(stockBought.getBalanceInvested())
                        .quantity(stockBought.getQuantity())
                        .timestamp(stockBought.getTimestamp())
                        .stockStatus(StockStatus.SOLD)
                        .portfolio(portfolio)
                        .build();
                stockPendingRepository.save(stockPending);
            }
        }
        return new StockResponse("All stocks with symbol " + symbol + " added to pending list");
    }

    public List<StockPendingDto> getPendingStocks(String token) {
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        List<StockPending> stockPendings = stockPendingRepository.findByPortfolio(user.getPortfolio()).get();
        List<StockPendingDto> stockPendingDtos = new ArrayList<>();
        for (StockPending stockPending : stockPendings) {
            StockData stockData = stockDataService.getLastStockValue(stockPending.getSymbol());
            StockPendingDto stockPendingDto = StockPendingDto.builder()
                    .uuid(stockPending.getUuid())
                    .name(stockPending.getName())
                    .symbol(stockPending.getSymbol())
                    .balanceInvested(stockPending.getBalanceInvested())
                    .quantity(stockPending.getQuantity())
                    .currentPrice(stockData.getCurrentPrice())
                    .timestamp(stockPending.getTimestamp().toString())
                    .stockStatus(stockPending.getStockStatus().toString())
                    .build();
            stockPendingDtos.add(stockPendingDto);
        }
        return stockPendingDtos;
    }

    public Optional<StockPending> findByPortfolioAndSymbolAndTimestamp(Portfolio userPortfolio, String symbol, LocalDateTime timestamp) {
        return stockPendingRepository.findByPortfolioAndSymbolAndTimestamp(userPortfolio, symbol, timestamp);
    }

    public List<StockPending> findAll() {
        return stockPendingRepository.findAll();
    }

    public void deleteAll(List<StockPending> stockPendings) {
        stockPendingRepository.deleteAll(stockPendings);
    }
}
