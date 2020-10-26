package onlineshop;

import onlineshop.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyHandler {
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString) {

    }

    @Autowired
    PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_Pay(@Payload Ordered ordered) {

        if (ordered.isMe()) {
            //    System.out.println("##### listener Pay : " + ordered.toJson());

            Payment payment = new Payment();
            payment.setOrderId(ordered.getId());
            payment.setChargeAmount(ordered.getQty() * 100);
            payment.setStatus("PAIED");

            paymentRepository.save(payment);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderedCancled_Paycancel(@Payload OrderedCancled orderedCancled) {
        try {
            if (orderedCancled.isMe()) {
            //    System.out.println("##### listener Paycancel : " + orderedCancled.toJson());
                List<Payment> paymentList = paymentRepository.findByOrderId(orderedCancled.getId());
                for(Payment payment : paymentList){
                    payment.setStatus("PayCanceled");
                    paymentRepository.save(payment);
                    }
                 }
        }catch (Exception e){
                e.printStackTrace();
        }
    }
}
