package nz.co.goodspeed;

import nz.co.goodspeed.model.ProductDebug;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class DataGenerator {

    private final static String TOPIC_WRITE = "products";

    public static void main(String [] args) {
        try(KafkaProducer<String, ProductDebug> data = new KafkaProducer<>(Common.producerProps())) {
            data.send(
                    new ProducerRecord<>(
                            TOPIC_WRITE,
                            "35",
                            ProductDebug.newBuilder()
                                    .setDescription("description")
                                    .setPrice("price")
                                    .setSourceSystemId("35")
                                    .build()
                    )
            );
            data.flush();
        }
    }

}
