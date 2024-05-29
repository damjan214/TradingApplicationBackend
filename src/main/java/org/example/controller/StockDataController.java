package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.MarketResponse;
import org.example.exceptions.ResourceNotFoundException;
import org.example.model.stocks.StockData;
import org.example.service.StockDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StockDataController {

    private final StockDataService stockDataService;

    @GetMapping("/stockdata/today")
    public ResponseEntity<StockData> getStockDataForToday(@RequestParam String symbol, @RequestHeader("Authorization") String token) {
        try{
            return ResponseEntity.ok(stockDataService.getLastStockValue(symbol));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
