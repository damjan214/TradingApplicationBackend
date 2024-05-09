package org.example.repository;

import org.example.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
     Optional<Payment> findByStripePaymentId(String stripePaymentId);

}
