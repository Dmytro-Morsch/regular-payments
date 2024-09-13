package regularpayments.controller;

import regularpayments.model.Payment;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class TestHelper {

    public static Payment createPayment() {
        Payment payment = new Payment();
        payment.setPayerName("Payer name");
        payment.setPayerInn("0123456789");
        payment.setPayerCardNumber("0123456789012345");
        payment.setPayeeAccount("UA012345678901234567890123456");
        payment.setPayeeMfo("012345");
        payment.setPayeeOkpo("01234567");
        payment.setPayeeName("Payee name");
        payment.setPaymentPeriod(Duration.of(1, ChronoUnit.DAYS));
        payment.setPaymentAmount(BigDecimal.TEN);
        return payment;
    }
}
