package nz.co.goodspeed;

import nz.co.goodspeed.model.ProductDebug;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Random;

public class DataGenerator {

    private final static String TOPIC_WRITE = "products";

    public static void main(String [] args) throws IOException, InterruptedException {
        System.out.println("press enter to generate 100 products");
        System.in.read();
        for(int i = 0; i < 100; i ++) {
            try (KafkaProducer<String, ProductDebug> data = new KafkaProducer<>(Common.producerProps())) {
                String key = String.valueOf(generateRandom(10));
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
            Thread.sleep(200);
        }
        main(args);
    }

    static int generateRandom(int max) {
        Random rnd = new Random();
        return rnd.nextInt(max);
    }

}
