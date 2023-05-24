package nz.co.goodspeed;

import nz.co.goodspeed.model.Customer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Random;

public class CustomerProducer {

    private static final String[] nameList = new String[]{"fred","bob","benjamin","william","damien"};
    private static final String[] phoneList = new String[]{"0122233123","0213121237","0122546735","12377433","879763424"};
    public static void main(String[] args) throws IOException {
        generateCustomerData();
    }


    public static void generateCustomerData() throws IOException {
        System.out.printf("press enter to generate 1000 customers");
        System.in.read();
        produce();
    }

    public static void produce() throws IOException {
        for (int i = 0; i < 1000; i++) {
            try (KafkaProducer<String, Customer> producer = new KafkaProducer<>(Common.transactionalProducerProperties())) {
                producer.initTransactions();

                int id = generateRandom(5);
                Customer data = buildRandomCustomer();
                System.out.printf("customer with identity %s%n%s", id, data);
                producer.beginTransaction();
                producer.send(
                        new ProducerRecord<>(
                                "CUSTOMERS",
                                String.valueOf(id),
                                data
                        )
                );
                producer.commitTransaction();
                producer.flush();
            }

        }
        generateCustomerData();
    }

    public static Customer buildRandomCustomer() {

        return Customer.newBuilder()
                .setCustomerName(nameList[generateRandom(nameList.length-1)])
                .setPhone(phoneList[generateRandom(phoneList.length-1)])
                .build();
    }

    public static int generateRandom(int max) {
        Random rnd = new Random();
        return rnd.nextInt(1,max);
    }
}
