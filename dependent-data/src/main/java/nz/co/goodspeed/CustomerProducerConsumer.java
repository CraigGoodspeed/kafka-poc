package nz.co.goodspeed;

import nz.co.goodspeed.model.Customer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Random;

public class CustomerProducerConsumer {
    public static void main(String[] args) throws IOException {
        System.setOut(new CustomPrint(System.out));
        startConsumer();
        generateCustomerData();
    }

    public static void startConsumer() {
        Thread consumeData = new Thread(new CustomerConsumer());
        Thread consumeCustomerProductPrice = new Thread(new CustomerProductPriceConsumer());
        consumeData.start();
        consumeCustomerProductPrice.start();
    }
    public static void generateCustomerData() throws IOException {
        System.out.printf("press enter to generate a new customer");
        System.in.read();
        produce();
    }

    public static void produce() throws IOException {

        try(KafkaProducer<String, Customer> producer = new KafkaProducer<>(Common.transactionalProducerProperties())) {
            producer.initTransactions();

            int id = generateRandom(5);
            System.out.printf("customer with identity %s%n", id);
            producer.beginTransaction();
            producer.send(
                    new ProducerRecord<>(
                            "CUSTOMERS",
                            String.valueOf(id),
                            buildRandomCustomer()
                    )
            );
            producer.commitTransaction();
            producer.flush();
        }
        generateCustomerData();
    }

    public static Customer buildRandomCustomer() {
        return Customer.newBuilder()
                .setCustomerName("some name")
                .setPhone("somePhone")
                .build();
    }

    public static int generateRandom(int max) {
        Random rnd = new Random();
        return rnd.nextInt(1,max);
    }
}