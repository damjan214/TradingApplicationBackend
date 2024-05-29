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
import org.example.repository.PortfolioRepository;
import org.example.repository.StockBoughtRepository;
import org.example.repository.StockPendingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockBoughtService {
    private final StockBoughtRepository stockBoughtRepository;
    private final PortfolioService portfolioService;
    private final AuthenticationService authenticationService;
    private final StockDataService stockDataService;
    private final ReadFileService readFileService;
    private final StockPendingService stockPendingService;
    private String marketStatus = "CLOSED";

    public StockResponse buyStock(User user, StockBuyRequest stockBuyRequest) {
        String marketStatus = checkMarketStatus();
        if (marketStatus.equals("CLOSED")) {
            stockPendingService.buyStockPending(stockBuyRequest, user);
            return new StockResponse("Stock added to pending list");
        } else {
            Portfolio userPortfolio = portfolioService.getPortfolio(user);
            userPortfolio.setBalanceAvailable(userPortfolio.getBalanceAvailable() - stockBuyRequest.getBalanceInvested());
            StockData stockData = stockDataService.getLastStockValue(stockBuyRequest.getSymbol());
            if (stockData == null) {
                throw new ResourceNotFoundException("Stock not found!");
            }
            Double quantity = stockBuyRequest.getBalanceInvested() / stockData.getCurrentPrice() / 100;
            StockBought stockBought = new StockBought().builder()
                    .name(stockBuyRequest.getName())
                    .symbol(stockBuyRequest.getSymbol())
                    .balanceInvested(stockBuyRequest.getBalanceInvested())
                    .quantity(quantity)
                    .openPriceDay(stockData.getOpenPriceDay())
                    .portfolio(userPortfolio)
                    .timestamp(LocalDateTime.now())
                    .build();
            userPortfolio.getStockBoughts().add(stockBought);
            portfolioService.savePortfolio(userPortfolio);
            stockBoughtRepository.save(stockBought);
            return new StockResponse("Stock bought successfully!");
        }
    }

    public StockResponse buyStockFromPending(User user, StockBuyRequest stockBuyRequest) {
        Portfolio userPortfolio = portfolioService.getPortfolio(user);
        StockData stockData = stockDataService.getLastStockValue(stockBuyRequest.getSymbol());
        if (stockData == null) {
            throw new ResourceNotFoundException("Stock not found!");
        }
        Double quantity = stockBuyRequest.getBalanceInvested() / stockData.getCurrentPrice() / 100;
        StockBought stockBought = new StockBought().builder()
                .name(stockBuyRequest.getName())
                .symbol(stockBuyRequest.getSymbol())
                .balanceInvested(stockBuyRequest.getBalanceInvested())
                .quantity(quantity)
                .openPriceDay(stockData.getOpenPriceDay())
                .portfolio(user.getPortfolio())
                .timestamp(LocalDateTime.now())
                .build();
        userPortfolio.getStockBoughts().add(stockBought);
        portfolioService.savePortfolio(userPortfolio);
        stockBoughtRepository.save(stockBought);
        return new StockResponse("Stock bought successfully!");
    }

    public StockResponse sellStock(User user, StockSellRequest stockSellRequest) {
        String marketStatus = checkMarketStatus();
        if (marketStatus.equals("CLOSED")) {
            stockPendingService.sellStockPending(stockSellRequest, user);
            return new StockResponse("Stock added to pending list");
        } else {
            StockBought stockBought = stockBoughtRepository.findByTimestamp(LocalDateTime.parse(stockSellRequest.getTimestamp())).orElseThrow(() -> new ResourceNotFoundException("Stock not found!"));
            Portfolio userPortfolio = portfolioService.getPortfolio(user);
            StockData stockData = stockDataService.getLastStockValue(stockBought.getSymbol());
            if (stockData == null) {
                throw new ResourceNotFoundException("Stock not found!");
            }
            Double sellPrice = stockBought.getQuantity() * stockData.getCurrentPrice() * 100;
            userPortfolio.setBalanceAvailable(userPortfolio.getBalanceAvailable() + sellPrice);
            userPortfolio.getStockBoughts().remove(stockBought);
            portfolioService.savePortfolio(userPortfolio);
            stockBoughtRepository.delete(stockBought);
            return new StockResponse("Stock sold successfully!");
        }
    }

    public StockResponse sellAllStocksBySymbol(User user, SellAllRequest sellAllRequest) {
        String marketStatus = checkMarketStatus();
        if (marketStatus.equals("CLOSED")) {
            stockPendingService.sellAllStocksBySymbol(user, sellAllRequest);
            return new StockResponse("Stocks added to pending list");
        } else {
            Portfolio userPortfolio = portfolioService.getPortfolio(user);
            StockData stockData = stockDataService.getLastStockValue(sellAllRequest.getSymbol());
            List<StockBought> stockBoughts = stockBoughtRepository.findByPortfolioAndSymbol(userPortfolio, sellAllRequest.getSymbol()).get();
            Double sellPrice = 0.0;
            for (StockBought stockBought : stockBoughts) {
                sellPrice += stockBought.getQuantity() * stockData.getCurrentPrice() * 100;
                userPortfolio.getStockBoughts().remove(stockBought);
                stockBoughtRepository.delete(stockBought);
            }
            userPortfolio.setBalanceAvailable(userPortfolio.getBalanceAvailable() + sellPrice);
            portfolioService.savePortfolio(userPortfolio);
            return new StockResponse("All stocks sold successfully!");
        }
    }

    public List<StockBoughtDto> getStocksBySymbol(String symbol, String token) {
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        Portfolio userPortfolio = portfolioService.getPortfolio(user);
        StockData stockData = stockDataService.getLastStockValue(symbol);
        List<StockBought> stockBoughts = stockBoughtRepository.findByPortfolioAndSymbol(userPortfolio, symbol).get();
        List<StockBoughtDto> stockBoughtDtos = new ArrayList<>();
        for (StockBought stockBought : stockBoughts) {
            if (!stockPendingService.findByPortfolioAndSymbolAndTimestamp(userPortfolio, symbol, stockBought.getTimestamp()).isPresent()) {
                StockBoughtDto stockBoughtDto = StockBoughtDto.builder()
                        .uuid(stockBought.getUuid())
                        .name(stockBought.getName())
                        .symbol(stockBought.getSymbol())
                        .currentPrice(stockData.getCurrentPrice())
                        .balanceInvested(stockBought.getBalanceInvested())
                        .quantity(stockBought.getQuantity())
                        .profitOrLoss((stockBought.getQuantity() * stockData.getCurrentPrice()) - stockBought.getBalanceInvested() / 100)
                        .profitOrLossPercentage(((stockBought.getQuantity() * stockData.getCurrentPrice()) - stockBought.getBalanceInvested() / 100) / stockBought.getBalanceInvested() * 10000)
                        .openPriceDay(stockBought.getOpenPriceDay())
                        .timestamp(stockBought.getTimestamp().toString())
                        .build();
                stockBoughtDtos.add(stockBoughtDto);

            }
        }
        return stockBoughtDtos;
    }

    public List<StockBoughtDto> getStocks(String token) {
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        Portfolio userPortfolio = portfolioService.getPortfolio(user);
        List<StockBought> stockBoughts = stockBoughtRepository.findByPortfolio(userPortfolio).get();
        Map<String, String> symbolsAndNames = readFileService.getSymbolsAndNames();
        List<StockBoughtDto> stockBoughtDtos = new ArrayList<>();
        for (String symbolKey : symbolsAndNames.keySet()) {
            Double investedAmount = 0.0;
            Double quantity = 0.0;
            Double averageOpenPrice = 0.0;
            Double profitOrLoss = 0.0;
            Double profitOrLossPercentage;
            Integer stocksCount = 0;
            StockData stockData = stockDataService.getLastStockValue(symbolKey);
            for (StockBought stockBought : stockBoughts) {
                if (stockBought.getSymbol().equals(symbolKey)) {
                    investedAmount += stockBought.getBalanceInvested();
                    quantity += stockBought.getQuantity();
                    averageOpenPrice += stockBought.getOpenPriceDay();
                    profitOrLoss += (stockBought.getQuantity() * stockData.getCurrentPrice()) - stockBought.getBalanceInvested() / 100;
                    stocksCount++;
                }
            }
            averageOpenPrice /= stocksCount;
            profitOrLossPercentage = (profitOrLoss / investedAmount) * 10000;
            if (stocksCount != 0) {
                StockBoughtDto stockBoughtDto = StockBoughtDto.builder()
                        .name(symbolsAndNames.get(symbolKey))
                        .symbol(symbolKey)
                        .currentPrice(stockData.getCurrentPrice())
                        .balanceInvested(investedAmount)
                        .quantity(quantity)
                        .profitOrLoss(profitOrLoss)
                        .profitOrLossPercentage(profitOrLossPercentage)
                        .openPriceDay(averageOpenPrice)
                        .build();
                stockBoughtDtos.add(stockBoughtDto);
            }
        }
        return stockBoughtDtos;
    }
    public void resolveStocksPending() {
        List<StockPending> stockPendings = stockPendingService.findAll();
        for (StockPending stockPending : stockPendings) {
            User user = stockPending.getPortfolio().getUser();
            if (stockPending.getStockStatus().equals(StockStatus.BOUGHT)) {
                StockBuyRequest stockBuyRequest = StockBuyRequest.builder()
                        .name(stockPending.getName())
                        .symbol(stockPending.getSymbol())
                        .balanceInvested(stockPending.getBalanceInvested())
                        .build();
                buyStockFromPending(user, stockBuyRequest);
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
                sellStock(user, stockSellRequest);
            }
        }
        stockPendingService.deleteAll(stockPendings);
    }

    public String checkMarketStatus() {
        return marketStatus;
    }

    public MarketResponse checkMarketStatus(String token){
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        return new MarketResponse(marketStatus.equals("OPEN") ? "OPEN" : "CLOSED");
    }

    public MarketResponse changeMarketStatus(String token){
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        if (marketStatus.equals("OPEN")) {
            marketStatus = "CLOSED";
            return new MarketResponse("CLOSED");
        } else {
            marketStatus = "OPEN";
            resolveStocksPending();
            return new MarketResponse("OPEN");
        }
    }
}
