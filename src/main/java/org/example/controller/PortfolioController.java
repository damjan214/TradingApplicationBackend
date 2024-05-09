package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.PaymentDetails;
import org.example.exceptions.ResourceNotFoundException;
import org.example.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/portfolio/balance")
    public ResponseEntity<PaymentDetails> getPortfolioBalance(@RequestHeader("Authorization") String token) {
        try{
            return ResponseEntity.ok(portfolioService.getCashBalance(token));
        }
        catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(new PaymentDetails().builder().message(e.getMessage()).build());
        }
    }
}
