package interview;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentClient {

    @Autowired
    private Environment environment;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();
    private static final Logger log = LoggerFactory.getLogger(Reglament.class);

    public boolean isTransactionNeeded(String paymentId) throws IOException, InterruptedException {
        log.info("Checking if transaction needed for payment {}", paymentId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(environment.getProperty("server.uri") + "/api/payments/" + paymentId + "/transaction-needed"))
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Bad response status " + response.statusCode());
        }
        boolean isNeeded = Boolean.parseBoolean(response.body());
        if (isNeeded) {
            log.info("New transaction is needed for payment {}", paymentId);
        } else {
            log.debug("New transaction is not needed for payment {}", paymentId);
        }
        return isNeeded;
    }

    public List<String> getPaymentIds() throws IOException, InterruptedException {
        log.info("Getting payments");
        HttpRequest request = HttpRequest.newBuilder()
                .header("Accept", "application/json")
                .uri(URI.create(environment.getProperty("server.uri") + "/api/payments"))
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Bad response status " + response.statusCode());
        }
        String json = response.body();

        JsonNode jsonNode = objectMapper.readTree(json);
        List<String> paymentIds = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            String id = node.get("id").asText();
            paymentIds.add(id);
        }
        log.info("Received {} payments", paymentIds.size());
        return paymentIds;
    }

    public void createTransaction(String paymentId) throws IOException, InterruptedException {
        log.info("Creating new transaction for payment {}", paymentId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(environment.getProperty("server.uri") + "/api/payments/" + paymentId + "/transactions"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            log.error("Cannot create transaction for payment{}, server returned status {}", paymentId, response.statusCode());
        } else {
            log.info("Payment created");
        }
    }

}
