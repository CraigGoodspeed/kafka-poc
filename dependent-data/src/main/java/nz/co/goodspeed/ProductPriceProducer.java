package nz.co.goodspeed;

import nz.co.goodspeed.model.Customer;
import nz.co.goodspeed.model.CustomerPrice;
import nz.co.goodspeed.model.CustomerProductPrice;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.UUID;

import static nz.co.goodspeed.CustomerProducer.generateCustomerData;
import static nz.co.goodspeed.CustomerProducer.generateRandom;

public class ProductPriceProducer {
    public static void main(String[] args) throws IOException, InterruptedException {
        produce();
    }

    private static void produce() throws IOException, InterruptedException {
        System.out.println("press enter to generate 1000 random customer product price values");
        System.in.read();
        generateCustomerProductPrice();
    }


    public static void generateCustomerProductPrice() throws IOException, InterruptedException {
        for (int i = 0; i < 100; i++) {
            try (KafkaProducer<String, CustomerPrice> producer = new KafkaProducer<>(Common.transactionalProducerProperties(UUID.randomUUID().toString()))) {
                producer.initTransactions();

                int id = generateRandom(5);
                CustomerPrice data = buildCustomerProductPrice();
                System.out.printf("customer with identity %s%n%s", id, data);
                producer.beginTransaction();
                producer.send(
                        new ProducerRecord<>(
                                "customer_product_price",
                                data
                        )
                );
                producer.commitTransaction();
                producer.flush();
            }
            Thread.sleep(300);
        }
        produce();
    }

    private static CustomerPrice buildCustomerProductPrice() {
        return CustomerPrice.newBuilder()
                .setSourceSystemCustomerId(String.valueOf(generateRandom(35)))
                .setSourceSystemProductId(String.valueOf(generateRandom(5)))
                .setPrice(String.valueOf(generateRandom(100)/8))
                .setSourceSystemName("monitor")
                .build();
    }
}
