import org.example.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentRepositoryTest {

    @Mock
    private PaymentRepository paymentRepository;


    @BeforeAll
    public static void setUpAll() {
        System.out.println("Before all");
    }

    @BeforeEach
    public void setUp() {
        System.out.println("Before each");
    }

    // @Test
    // public void testAdd() {
    //     System.out.println("Test add");
    // }

}
