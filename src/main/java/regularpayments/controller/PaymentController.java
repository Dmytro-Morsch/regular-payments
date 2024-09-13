package regularpayments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import regularpayments.model.Payment;
import regularpayments.service.PaymentService;

import java.util.List;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/api/payments")
    public ResponseEntity<?> createPayment(@RequestBody Payment payment) {
        paymentService.createPayment(payment);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/api/payments/inn/{inn}")
    public ResponseEntity<?> getPaymentsByInn(@PathVariable String inn) {
        List<Payment> payments = paymentService.findByPayerInn(inn);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/api/payments/okpo/{okpo}")
    public ResponseEntity<?> getPaymentsByOkpo(@PathVariable String okpo) {
        List<Payment> payments = paymentService.findByPayeeOkpo(okpo);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/api/payments/{paymentId}/transaction-needed")
    public ResponseEntity<?> isTransactionNeeded(@PathVariable Long paymentId) {
        boolean isNeeded = paymentService.isTransactionNeeded(paymentId);
        return ResponseEntity.ok(isNeeded);
    }

}
