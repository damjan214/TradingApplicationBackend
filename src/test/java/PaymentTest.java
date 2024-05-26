import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.example.model.payment.Payment;
import org.example.model.payment.PaymentType;

class PaymentTest {

    @Test
    void testPrePersistSetsUuid() {
        Payment payment = new Payment();
        assertNull(payment.getUuid());

        payment.prePersist();

        assertNotNull(payment.getUuid());
    }

    @Test
    void testPaymentConstructor() {
        Payment payment = new Payment(1L, "123e4567-e89b-12d3-a456-426614174000", "stripe123", "user@example.com", 1000L, "USD", PaymentType.DEPOSIT);

        assertNotNull(payment);
        assertEquals("user@example.com", payment.getUserEmail());
        assertEquals("USD", payment.getCurrency());
        assertEquals(PaymentType.DEPOSIT, payment.getPaymentType());
        assertEquals(1000L, payment.getAmount());
        assertEquals("stripe123", payment.getStripePaymentId());
        assertEquals("123e4567-e89b-12d3-a456-426614174000", payment.getUuid());
    }

    @Test
    void testPaymentGettersAndSetters() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setUuid("123e4567-e89b-12d3-a456-426614174000");
        payment.setStripePaymentId("stripe123");
        payment.setUserEmail("user@example.com");
        payment.setAmount(1000L);
        payment.setCurrency("USD");
        payment.setPaymentType(PaymentType.DEPOSIT);

        assertEquals(1L, payment.getId());
        assertEquals("123e4567-e89b-12d3-a456-426614174000", payment.getUuid());
        assertEquals("stripe123", payment.getStripePaymentId());
        assertEquals("user@example.com", payment.getUserEmail());
        assertEquals(1000L, payment.getAmount());
        assertEquals("USD", payment.getCurrency());
        assertEquals(PaymentType.DEPOSIT, payment.getPaymentType());
    }
}