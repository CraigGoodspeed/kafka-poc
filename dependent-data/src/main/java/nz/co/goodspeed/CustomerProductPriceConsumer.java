package nz.co.goodspeed;

import nz.co.goodspeed.model.CustomerPrice;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;

public class CustomerProductPriceConsumer implements Runnable {


    @Override
    public void run() {
        try(KafkaConsumer<String, CustomerPrice> consumer = new KafkaConsumer<>(Common.buildConsumerProperties("cpp-consumer"))) {
            consumer.subscribe(Collections.singletonList("monitor_customer_product_price"));
            System.out.println("subscribed to customer product price");
            while(true){
                ConsumerRecords<String, CustomerPrice> data = consumer.poll(Duration.ofMillis(100));
                data.forEach(
                        record -> {
                            if(record != null) {
                                System.out.printf("received key %s on customer product price%n",record.key());
                                System.out.println(record.value());
                                System.out.println("we would normally trigger an event with dependant data.");
                            }
                        }
                );
            }
        }
    }
}
