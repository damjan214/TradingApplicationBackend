package org.example.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.*;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.configuration.StripeConfig;
import org.example.dto.PaymentResponse;
import org.example.exceptions.*;
import org.example.model.user.User;
import org.example.model.payment.Payment;
import org.example.model.payment.PaymentType;
import org.example.repository.PaymentRepository;
import org.example.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final StripeConfig stripeConfig;
    private final PaymentRepository paymentRepository;
    private final PortfolioRepository portfolioRepository;

    private final AuthenticationService authenticationService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeConfig.getSecretKey();
    }

    public String checkoutPayment(String token, Long amount, String currency, PaymentType paymentType) throws StripeException {
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        retrieveOrCreateCustomerByEmail(user);

        return Session.create(SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomerEmail(user.getEmail())
                .setSuccessUrl(createSuccessUrl(stripeConfig.getSuccessUrl(), paymentType))
                .setCancelUrl(stripeConfig.getCancelUrl())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(currency)
                                .setUnitAmount(amount)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(paymentType.toString())
                                        .build())
                                .build())
                        .build())
                .build()).getUrl();
    }

    public PaymentResponse confirmPayment(String token, String sessionId) throws StripeException {
        User user = authenticationService.getUserByToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        Session session = Session.retrieve(sessionId);
        String successUrl = session.getSuccessUrl();
        PaymentType paymentType = PaymentType.valueOf(successUrl.substring(successUrl.indexOf("paymentType=") + 12));
        String customerEmail = session.getCustomerEmail();
        if (!user.getEmail().equals(customerEmail)) {
            throw new InvalidEmailException("Emails do not match!");
        }
        Long amount = session.getAmountSubtotal();
        String currency = session.getCurrency();
        paymentRepository.findByStripePaymentId(session.getPaymentIntent()).ifPresent((payment) -> {
            throw new PaymentExistsException("Payment already exists!");
        });
        PaymentIntent paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());
        if (!session.getPaymentStatus().equals("paid")) {
            throw new PaymentNotCompletedException("Payment not completed!");
        }
        if (paymentType.equals(PaymentType.WITHDRAW)) {
            if (user.getPortfolio().getCashBalance() < amount) {
                throw new InsufficientFundsException("Insufficient funds to withdraw!");
            }
            else {
                updatePortfolio(user, amount, paymentType);
                portfolioRepository.save(user.getPortfolio());
                paymentRepository.save(Payment.builder()
                        .stripePaymentId(session.getPaymentIntent())
                        .userEmail(customerEmail)
                        .amount(amount)
                        .currency(currency)
                        .paymentType(PaymentType.WITHDRAW)
                        .build());
                return new PaymentResponse("Withdraw confirmed!");
            }
        }
        else {
            updatePortfolio(user, amount, paymentType);
            paymentRepository.save(Payment.builder()
                    .stripePaymentId(session.getPaymentIntent())
                    .userEmail(customerEmail)
                    .amount(amount)
                    .currency(currency)
                    .paymentType(PaymentType.DEPOSIT)
                    .build());
            return new PaymentResponse("Deposit confirmed!");
        }
    }

    private Customer retrieveOrCreateCustomerByEmail(User user) throws StripeException {
        Customer customer = retrieveCustomerByEmail(user.getEmail());
        if (customer == null) {
            customer = Customer.create(CustomerCreateParams.builder()
                    .setEmail(user.getEmail())
                    .setName(user.getFirstName() + " " + user.getLastName())
                    .setPaymentMethod("pm_card_visa")
                    .build());
        }
        return customer;
    }

    private Customer retrieveCustomerByEmail(String email) {
        Map<String, Object> params = Map.of("email", email);
        try {
            return Customer.list(params).getData().stream().findFirst().orElse(null);
        } catch (StripeException e) {
            return null;
        }
    }

    private void updatePortfolio(User user, Long amount, PaymentType paymentType) {
        if (paymentType.equals(PaymentType.WITHDRAW)) {
            user.getPortfolio().setCashBalance(user.getPortfolio().getCashBalance() - amount);
        } else {
            user.getPortfolio().setCashBalance(user.getPortfolio().getCashBalance() + amount);
        }
        portfolioRepository.save(user.getPortfolio());
    }

    private String createSuccessUrl(String successUrl, PaymentType paymentType) {
        return successUrl + "&paymentType=" + paymentType.toString();
    }
}
