package regularpayments.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import regularpayments.model.Payment;
import regularpayments.model.PaymentTransaction;
import regularpayments.model.Status;
import regularpayments.repository.PaymentRepository;
import regularpayments.repository.PaymentTransactionRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Test
    void testTransactionNeededWhenNoActiveTransactions() {
        Payment payment = new Payment();
        payment.setPaymentPeriod(Duration.ofDays(1));
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTxTimestamp(Instant.now());
        transaction.setStatus(Status.S);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));
        when(paymentTransactionRepository.findByPayment(payment))
                .thenReturn(List.of(transaction));

        boolean result = paymentService.isTransactionNeeded(1L);

        assertTrue(result);
    }

    @Test
    void testTransactionNeededWhenUnpaidPeriod() {
        Payment payment = new Payment();
        payment.setPaymentPeriod(Duration.ofDays(1));
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTxTimestamp(Instant.now().minus(2, DAYS));
        transaction.setStatus(Status.A);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));
        when(paymentTransactionRepository.findByPayment(payment))
                .thenReturn(List.of(transaction));

        boolean result = paymentService.isTransactionNeeded(1L);

        assertTrue(result);
    }

    @Test
    void testTransactionNotNeeded() {
        Payment payment = new Payment();
        payment.setPaymentPeriod(Duration.ofDays(1));
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTxTimestamp(Instant.now().minus(1, HOURS));
        transaction.setStatus(Status.A);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));
        when(paymentTransactionRepository.findByPayment(payment))
                .thenReturn(List.of(transaction));

        boolean result = paymentService.isTransactionNeeded(1L);

        assertFalse(result);
    }
}