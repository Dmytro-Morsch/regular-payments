package regularpayments.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import regularpayments.model.Payment;
import regularpayments.model.PaymentTransaction;
import regularpayments.model.Status;
import regularpayments.repository.PaymentTransactionRepository;
import regularpayments.service.PaymentService;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static regularpayments.controller.TestHelper.createPayment;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class PaymentTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Test
    void testCreateTransaction() throws Exception {
        Payment payment = createPayment();
        paymentService.createPayment(payment);
        mockMvc.perform(post("/api/payments/{paymentId}/transactions", payment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNumber());
    }

    @Test
    void testCreateTransactionWhenNotFound() throws Exception {
        mockMvc.perform(post("/api/payments/{paymentId}/transactions", -42L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPaymentTransactions() throws Exception {
        Payment payment = createPayment();
        paymentService.createPayment(payment);
        mockMvc.perform(get("/api/payments/{paymentId}/transactions", payment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetPaymentTransactionsWhenNotFound() throws Exception {
        mockMvc.perform(get("/api/payments/{paymentId}/transactions", -42L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testReverseTransaction() throws Exception {
        Payment payment = createPayment();
        paymentService.createPayment(payment);
        PaymentTransaction transaction = paymentTransactionRepository.findByPayment(payment).get(0);
        mockMvc.perform(patch("/api/payments/{paymentId}/transactions/{transactionId}", payment.getId(), transaction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.S.name()));
    }

}