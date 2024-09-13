package regularpayments.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import regularpayments.model.Payment;
import regularpayments.repository.PaymentRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static regularpayments.controller.TestHelper.createPayment;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void testCreatePayment() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "payerName": "Payer name",
                                    "payerInn": "0123456789",
                                    "payerCardNumber": "0123456789012345",
                                    "payeeAccount": "UA012345678901234567890123456",
                                    "payeeMfo": "012345",
                                    "payeeOkpo": "01234567",
                                    "payeeName": "Payee name",
                                    "paymentPeriod": "P1D",
                                    "paymentAmount": "100"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNumber());
    }

    @Test
    void testCreatePaymentWhenBadRequest() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPaymentByInn() throws Exception {
        paymentRepository.save(createPayment());
        mockMvc.perform(get("/api/payments/inn/{inn}", "0123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetPaymentByOkpo() throws Exception {
        paymentRepository.save(createPayment());
        mockMvc.perform(get("/api/payments/okpo/{okpo}", "01234567"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testIsTransactionNeeded() throws Exception {
        Payment payment = createPayment();
        paymentRepository.save(payment);
        mockMvc.perform(get("/api/payments/{paymentId}/transaction-needed", payment.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testIsTransactionNeededWhenNotFound() throws Exception {
        mockMvc.perform(get("/api/payments/{paymentId}/transaction-needed", -42L))
                .andExpect(status().isNotFound());
    }

}