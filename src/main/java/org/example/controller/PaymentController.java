package org.example.controller;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.example.dto.PaymentDetails;
import org.example.dto.PaymentResponse;
import org.example.exceptions.*;
import org.example.model.payment.PaymentType;
import org.example.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/payment/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(@RequestHeader("Authorization") String token, @RequestHeader("X-Stripe-Session-Id") String sessionId) throws StripeException {
        try{
            return ResponseEntity.ok(paymentService.confirmPayment(token, sessionId));
        }
        catch (ResourceNotFoundException | InvalidEmailException | PaymentExistsException |
               PaymentNotCompletedException | InsufficientFundsException e) {
            return ResponseEntity.badRequest().body(new PaymentResponse(e.getMessage()));
        }
    }

    @PostMapping("/payment/deposit")
    public ResponseEntity<String> paymentDeposit(@RequestHeader("Authorization") String token, @RequestBody PaymentDetails paymentDetails) throws StripeException {
        try{
            return ResponseEntity.ok(paymentService.checkoutPayment(token, paymentDetails.getAmount(), paymentDetails.getCurrency(), PaymentType.DEPOSIT));
        }
        catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/payment/withdraw")
    public ResponseEntity<String> paymentWithdraw(@RequestHeader("Authorization") String token, @RequestBody PaymentDetails paymentDetails) throws StripeException {
        try{
            return ResponseEntity.ok(paymentService.checkoutPayment(token, paymentDetails.getAmount(), paymentDetails.getCurrency(), PaymentType.WITHDRAW));
        }
        catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
