package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.StockPendingDto;
import org.example.dto.StockResponse;
import org.example.exceptions.ResourceNotFoundException;
import org.example.service.StockPendingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StockPendingController {
    private final StockPendingService stockPendingService;

    @GetMapping("/pending/stocks")
    public ResponseEntity<List<StockPendingDto>> getPendingStocks(@RequestHeader("Authorization") String token) {
        try{
            return ResponseEntity.ok(stockPendingService.getPendingStocks(token));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("pending/cancel/buy")
    public ResponseEntity<StockResponse> cancelBuyStockPending(@RequestHeader("Authorization") String token, @RequestBody StockPendingDto stockPendingDto) {
        try{
            return ResponseEntity.ok(stockPendingService.cancelBuyStockPending(token, stockPendingDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(new StockResponse(e.getMessage()));
        }
    }

    @PostMapping("pending/cancel/sell")
    public ResponseEntity<StockResponse> cancelSellStockPending(@RequestHeader("Authorization") String token, @RequestBody StockPendingDto stockPendingDto) {
        try{
            return ResponseEntity.ok(stockPendingService.cancelSellStockPending(token, stockPendingDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(new StockResponse(e.getMessage()));
        }
    }
}
