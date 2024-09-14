package regularpayments.service;

import jakarta.transaction.Transactional;
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

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private PaymentValidator paymentValidator;

    public void createPayment(Payment payment) {
        paymentValidator.validate(payment);
        paymentRepository.save(payment);
        createTransaction(payment);
    }

    public List<Payment> findByPayerInn(String payerInn) {
        return paymentRepository.findByPayerInn(payerInn);
    }

    public List<Payment> findByPayeeOkpo(String payeeOkpo) {
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
        return transaction;
    }

    public List<PaymentTransaction> findPaymentTransactions(Long paymentId) {
        Payment payment = getPayment(paymentId);
        return paymentTransactionRepository.findByPayment(payment);
    }

    public PaymentTransaction createTransaction(Long paymentId) {
        Payment payment = getPayment(paymentId);
        return createTransaction(payment);
    }

    public boolean isTransactionNeeded(Long paymentId) {
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

        return actualTransactionCount < requiredTransactionCount;
    }

    private PaymentTransaction createTransaction(Payment payment) {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setPayment(payment);
        paymentTransaction.setTxTimestamp(Instant.now());
        paymentTransaction.setStatus(Status.A);
        paymentTransaction.setAmount(payment.getPaymentAmount());
        paymentTransactionRepository.save(paymentTransaction);
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
