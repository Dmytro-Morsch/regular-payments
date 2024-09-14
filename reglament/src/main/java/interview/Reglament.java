package interview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class Reglament implements CommandLineRunner {

    @Autowired
    private PaymentClient paymentClient;

    @Override
    public void run(String... args) throws IOException, InterruptedException {
        List<String> paymentIds = paymentClient.getPaymentIds();

        for (String paymentId : paymentIds) {
            boolean isNeeded = paymentClient.isTransactionNeeded(paymentId);
            if (isNeeded) {
                paymentClient.createTransaction(paymentId);
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Reglament.class, args);
    }

}
