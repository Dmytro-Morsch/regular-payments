package regularpayments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import regularpayments.model.PaymentTransaction;
import regularpayments.service.PaymentService;

import java.util.List;

@RestController
public class PaymentTransactionController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/api/payments/{paymentId}/transactions")
    public ResponseEntity<?> getPaymentTransactions(@PathVariable Long paymentId) {
        List<PaymentTransaction> transactions = paymentService.findPaymentTransactions(paymentId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/api/payments/{paymentId}/transactions")
    public ResponseEntity<?> createTransaction(@PathVariable Long paymentId) {
        PaymentTransaction transaction = paymentService.createTransaction(paymentId);
        return ResponseEntity.ok(transaction);
    }

    @PatchMapping("/api/payments/{paymentId}/transactions/{transactionId}")
    public ResponseEntity<?> reverseTransaction(@PathVariable Long paymentId,
                                                @PathVariable Long transactionId) {
        PaymentTransaction transaction = paymentService.reverseTransaction(paymentId, transactionId);
        return ResponseEntity.ok(transaction);
    }

}
