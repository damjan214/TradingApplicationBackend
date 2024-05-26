package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.exceptions.ResourceNotFoundException;
import org.example.model.user.User;
import org.example.service.AuthenticationService;
import org.example.service.StockBoughtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StockBoughtController {

    private final StockBoughtService stockBoughtService;

    private final AuthenticationService authenticationService;


    @PostMapping("/stocks/buy")
    public ResponseEntity<StockResponse> buyStock(@RequestHeader("Authorization") String token, @RequestBody StockBuyRequest stockBuyRequest) {
        try{
            User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return ResponseEntity.ok(stockBoughtService.buyStock(user, stockBuyRequest));
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.badRequest().body(new StockResponse(e.getMessage()));
        }
    }

    @PostMapping("/stocks/sell")
    public ResponseEntity<StockResponse> sellStock(@RequestHeader("Authorization") String token, @RequestBody StockSellRequest stockSellRequest) {
        try{
            User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return ResponseEntity.ok(stockBoughtService.sellStock(user, stockSellRequest));
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.badRequest().body(new StockResponse(e.getMessage()));
        }
    }

    @PostMapping("/stocks/sell/all/symbol")
    public ResponseEntity<StockResponse> sellAllStocksBySymbol(@RequestHeader("Authorization") String token, @RequestBody SellAllRequest sellAllRequest) {
        try{
            User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return ResponseEntity.ok(stockBoughtService.sellAllStocksBySymbol(user, sellAllRequest));
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.badRequest().body(new StockResponse(e.getMessage()));
        }
    }


    @GetMapping("/stocks/symbol")
    public ResponseEntity<List<StockBoughtDto>> getStocksBySymbol(@RequestParam String symbol, @RequestHeader("Authorization") String token) {
        try{
            return ResponseEntity.ok(stockBoughtService.getStocksBySymbol(symbol, token));
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/stocks")
    public ResponseEntity<List<StockBoughtDto>> getStocks(@RequestHeader("Authorization") String token) {
        try{
            return ResponseEntity.ok(stockBoughtService.getStocks(token));
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.badRequest().body(null);
        }
    }
}
