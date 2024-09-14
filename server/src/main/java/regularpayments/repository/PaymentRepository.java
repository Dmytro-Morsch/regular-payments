package regularpayments.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import regularpayments.model.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    List<Payment> findByPayerInn(String payerInn);

    List<Payment> findByPayeeOkpo(String payeeOkpo);

}
