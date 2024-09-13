package regularpayments.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import regularpayments.model.Payment;
import regularpayments.model.PaymentTransaction;

import java.util.List;

@Repository
public interface PaymentTransactionRepository extends CrudRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findByPayment(Payment payment);

}
