package regularpayments.service;

import org.junit.jupiter.api.Test;
import regularpayments.model.Payment;
import regularpayments.model.ValidationException;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static regularpayments.controller.TestHelper.createPayment;

class PaymentValidatorTest {

    private final PaymentValidator paymentValidator = new PaymentValidator();

    private final Payment validPayment = createPayment();

    @Test
    void testIsValidPayment() {
        paymentValidator.validate(validPayment); //no exception
    }

    @Test
    void testInvalidPayerName() {
        Payment invalidPayerName = new Payment(validPayment);
        invalidPayerName.setPayerName("");

        assertThrows(ValidationException.class, () -> paymentValidator.validate(invalidPayerName));
    }

    @Test
    void testInvalidPayerInn() {
        Payment invalidPayerName = new Payment(validPayment);
        invalidPayerName.setPayerName("");

        assertThrows(ValidationException.class, () -> paymentValidator.validate(invalidPayerName));
    }

    @Test
    void testInvalidPayerCardNumber() {
        Payment invalidPayerCardNumber = new Payment(validPayment);
        invalidPayerCardNumber.setPayerCardNumber("");

        assertThrows(ValidationException.class, () -> paymentValidator.validate(invalidPayerCardNumber));
    }

    @Test
    void testInvalidPayeeAccount() {
        Payment invalidPayeeAccount = new Payment(validPayment);
        invalidPayeeAccount.setPayeeAccount("");

        assertThrows(ValidationException.class, () -> paymentValidator.validate(invalidPayeeAccount));
    }

    @Test
    void testInvalidPayeeMfo() {
        Payment invalidPayeeMfo = new Payment(validPayment);
        invalidPayeeMfo.setPayeeMfo("");

        assertThrows(ValidationException.class, () -> paymentValidator.validate(invalidPayeeMfo));
    }

    @Test
    void testInvalidPayeeOkpo() {
        Payment invalidPayeeOkpo = new Payment(validPayment);
        invalidPayeeOkpo.setPayeeOkpo("");

        assertThrows(ValidationException.class, () -> paymentValidator.validate(invalidPayeeOkpo));
    }

    @Test
    void testInvalidPayeeName() {
        Payment invalidPayeeName = new Payment(validPayment);
        invalidPayeeName.setPayeeName("");

        assertThrows(ValidationException.class, () -> paymentValidator.validate(invalidPayeeName));
    }

    @Test
    void testInvalidPaymentPeriod() {
        Payment invalidPaymentPeriod = new Payment(validPayment);
        invalidPaymentPeriod.setPaymentPeriod(null);

        assertThrows(ValidationException.class, () -> paymentValidator.validate(invalidPaymentPeriod));
    }

    @Test
    void testNegativePaymentPeriod() {
        Payment negativePaymentPeriod = new Payment(validPayment);
        negativePaymentPeriod.setPaymentPeriod(Duration.ZERO);

        assertThrows(ValidationException.class, () -> paymentValidator.validate(negativePaymentPeriod));
    }

    @Test
    void testInvalidPaymentAmount() {
        Payment invalidPaymentAmount = new Payment(validPayment);
        invalidPaymentAmount.setPaymentAmount(null);

        assertThrows(ValidationException.class, () -> paymentValidator.validate(invalidPaymentAmount));
    }

    @Test
    void testNegativePaymentAmount() {
        Payment negativePaymentAmount = new Payment(validPayment);
        negativePaymentAmount.setPaymentAmount(new BigDecimal(-100));

        assertThrows(ValidationException.class, () -> paymentValidator.validate(negativePaymentAmount));
    }
}