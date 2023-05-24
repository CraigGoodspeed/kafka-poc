package nz.co.goodspeed;

import nz.co.goodspeed.model.ProductDebug;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Random;

public class DataGenerator {

    private final static String TOPIC_WRITE = "products";

    public static void main(String [] args) throws IOException {
        System.out.println("press enter to generate 1000 products");
        System.in.read();
        for(int i = 0; i < 1000; i ++) {
            try (KafkaProducer<String, ProductDebug> data = new KafkaProducer<>(Common.producerProps())) {
                String key = String.valueOf(generateRandom(50));
                data.send(
                        new ProducerRecord<>(
                                TOPIC_WRITE,
                                key,
                                ProductDebug.newBuilder()
                                        .setDescription("description")
                                        .setPrice("price")
                                        .setSourceSystemId(key)
                                        .build()
                        )
                );
                data.flush();
            }
        }
        main(args);
    }

    static int generateRandom(int max) {
        Random rnd = new Random();
        return rnd.nextInt(max);
    }

}
