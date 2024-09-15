package regularpayments.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import regularpayments.model.*;
import regularpayments.repository.PaymentRepository;
import regularpayments.repository.PaymentTransactionRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private PaymentValidator paymentValidator;

    public void createPayment(Payment payment) {
        paymentValidator.validate(payment);
        paymentRepository.save(payment);
        log.info("Created new payment {}", payment.getId());
        createTransaction(payment);
    }

    public List<Payment> findAll() {
        log.debug("Find all payments");
        return (List<Payment>) paymentRepository.findAll();
    }

    public List<Payment> findByPayerInn(String payerInn) {
        log.debug("Find payments by payer INN {}", payerInn);
        return paymentRepository.findByPayerInn(payerInn);
    }

    public List<Payment> findByPayeeOkpo(String payeeOkpo) {
        log.debug("Find payments by payee OKPO {}", payeeOkpo);
        return paymentRepository.findByPayeeOkpo(payeeOkpo);
    }

    public PaymentTransaction reverseTransaction(Long paymentId, Long transactionId) {
        Payment payment = getPayment(paymentId);
        Optional<PaymentTransaction> maybeTransaction = paymentTransactionRepository.findById(transactionId);
        if (maybeTransaction.isEmpty()) {
            throw new NotFoundException("Transaction not found");
        }
        PaymentTransaction transaction = maybeTransaction.get();
        if (!transaction.getPayment().getId().equals(payment.getId())) {
            throw new ValidationException("Transaction does not belong to payment");
        }
        transaction.setStatus(Status.S);
        paymentTransactionRepository.save(transaction);
        log.info("Reversed transaction {} of payment {}", transactionId, paymentId);
        return transaction;
    }

    public List<PaymentTransaction> findPaymentTransactions(Long paymentId) {
        log.debug("Find all payment {} transactions", paymentId);
        Payment payment = getPayment(paymentId);
        return paymentTransactionRepository.findByPayment(payment);
    }

    public PaymentTransaction createTransaction(Long paymentId) {
        Payment payment = getPayment(paymentId);
        return createTransaction(payment);
    }

    public boolean isTransactionNeeded(Long paymentId) {
        log.debug("Checking if transaction needed for payment {}", paymentId);
        Payment payment = getPayment(paymentId);
        List<PaymentTransaction> transactions = paymentTransactionRepository.findByPayment(payment);

        // First transaction timestamp matches payment creation time
        Optional<Instant> paymentCreationTime = transactions.stream()
                .map(PaymentTransaction::getTxTimestamp)
                .min(Instant::compareTo);

        if (paymentCreationTime.isEmpty()) {
            return true;
        }

        Instant now = Instant.now();
        Duration passedTime = Duration.between(paymentCreationTime.get(), now);
        Duration paymentPeriod = payment.getPaymentPeriod();

        long requiredTransactionCount = passedTime.dividedBy(paymentPeriod) + 1;
        long actualTransactionCount = transactions.stream()
                .filter(transaction -> transaction.getStatus() == Status.A)
                .count();

        boolean isNeeded = actualTransactionCount < requiredTransactionCount;
        if (isNeeded) {
            log.debug("New transaction is needed for payment {}", paymentId);
        } else {
            log.debug("New transaction is not needed for payment {}", paymentId);
        }
        return isNeeded;
    }

    private PaymentTransaction createTransaction(Payment payment) {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setPayment(payment);
        paymentTransaction.setTxTimestamp(Instant.now());
        paymentTransaction.setStatus(Status.A);
        paymentTransaction.setAmount(payment.getPaymentAmount());
        paymentTransactionRepository.save(paymentTransaction);
        log.info("Create new transaction for payment {}", payment.getId());
        return paymentTransaction;
    }

    private Payment getPayment(Long paymentId) {
        Optional<Payment> maybePayment = paymentRepository.findById(paymentId);
        if (maybePayment.isEmpty()) {
            throw new NotFoundException("Payment not found");
        }
        return maybePayment.get();
    }
}
