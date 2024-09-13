package regularpayments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.Duration;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String payerName;

    private String payerInn;

    private String payerCardNumber;

    private String payeeAccount;

    private String payeeMfo;

    private String payeeOkpo;

    private String payeeName;


    private Duration paymentPeriod;

    private BigDecimal paymentAmount;

    public Payment() {
    }

    public Payment(Payment other) {
        this.id = other.id;
        this.payerName = other.payerName;
        this.payerInn = other.payerInn;
        this.payerCardNumber = other.payerCardNumber;
        this.payeeAccount = other.payeeAccount;
        this.payeeMfo = other.payeeMfo;
        this.payeeOkpo = other.payeeOkpo;
        this.payeeName = other.payeeName;
        this.paymentPeriod = other.paymentPeriod;
        this.paymentAmount = other.paymentAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayerInn() {
        return payerInn;
    }

    public void setPayerInn(String payerInn) {
        this.payerInn = payerInn;
    }

    public String getPayerCardNumber() {
        return payerCardNumber;
    }

    public void setPayerCardNumber(String payerCardNumber) {
        this.payerCardNumber = payerCardNumber;
    }

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }

    public String getPayeeMfo() {
        return payeeMfo;
    }

    public void setPayeeMfo(String payeeMfo) {
        this.payeeMfo = payeeMfo;
    }

    public String getPayeeOkpo() {
        return payeeOkpo;
    }

    public void setPayeeOkpo(String payeeOkpo) {
        this.payeeOkpo = payeeOkpo;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public Duration getPaymentPeriod() {
        return paymentPeriod;
    }

    public void setPaymentPeriod(Duration paymentPeriod) {
        this.paymentPeriod = paymentPeriod;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
}
