package regularpayments.service;

import org.springframework.stereotype.Component;
import regularpayments.model.Payment;
import regularpayments.model.ValidationException;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PaymentValidator {
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("\\d{15,16}");
    private static final Pattern MFO_PATTERN = Pattern.compile("\\d{6}");
    private static final Pattern INN_PATTERN = Pattern.compile("\\d{10}");
    private static final Pattern OKPO_PATTERN = Pattern.compile("\\d{8}");
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("UA\\d{27}");

    public void validate(Payment payment) {
        validateNotBlank(payment.getPayerName(), "Payer name");
        validateNotBlank(payment.getPayerCardNumber(), "Payer card number");
        validateNotBlank(payment.getPayerInn(), "Payer INN");

        validateNotBlank(payment.getPayeeName(), "Payee name");
        validateNotBlank(payment.getPayeeAccount(), "Payee account");
        validateNotBlank(payment.getPayeeMfo(), "Payee MFO");
        validateNotBlank(payment.getPayeeOkpo(), "Payee OKPO");

        if (payment.getPaymentAmount() == null) {
            throw new ValidationException("Payment amount is required");
        }
        if (payment.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Payment amount must be positive");
        }
        if (payment.getPaymentPeriod() == null) {
            throw new ValidationException("Payment period is required");
        }
        if (payment.getPaymentPeriod().isNegative() || payment.getPaymentPeriod().isZero()) {
            throw new ValidationException("Payment period must be positive");
        }
        if (payment.getPayeeName().length() > 100) {
            throw new ValidationException("Payee name length is max 100 characters");
        }
        if (payment.getPayerName().length() > 100) {
            throw new ValidationException("Payer name length is max 100 characters");
        }

        Matcher cardNumberMatcher = CARD_NUMBER_PATTERN.matcher(payment.getPayerCardNumber());
        if (!cardNumberMatcher.matches()) {
            throw new ValidationException("Invalid card number");
        }

        Matcher mfoMatcher = MFO_PATTERN.matcher(payment.getPayeeMfo());
        if (!mfoMatcher.matches()) {
            throw new ValidationException("Invalid MFO");
        }

        Matcher innMatcher = INN_PATTERN.matcher(payment.getPayerInn());
        if (!innMatcher.matches()) {
            throw new ValidationException("Invalid INN");
        }

        Matcher okpoMatcher = OKPO_PATTERN.matcher(payment.getPayeeOkpo());
        if (!okpoMatcher.matches()) {
            throw new ValidationException("Invalid OKPO");
        }

        Matcher accountMatcher = ACCOUNT_PATTERN.matcher(payment.getPayeeAccount());
        if (!accountMatcher.matches()) {
            throw new ValidationException("Invalid account");
        }
    }

    private static void validateNotBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(name + " is required");
        }
    }
}
