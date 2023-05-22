package nz.co.goodspeed;

import nz.co.goodspeed.model.Customer;
import nz.co.goodspeed.model.CustomerLinkedIdentities;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

public class CustomerConsumer implements Runnable{
    @Override
    public void run() {
        try (final KafkaConsumer<String, Customer> consumer = new KafkaConsumer<>(Common.buildConsumerProperties("customer-group"))) {
            consumer.subscribe(Collections.singletonList("CUSTOMERS_LINKED"));
            System.out.println("subscribed to topic waiting for data....");
            while(true) {
                ConsumerRecords<String, Customer> data = consumer.poll(Duration.ofMillis(1000));
                data.forEach(record -> {
                    if(record != null) {
                        String uuid = UUID.randomUUID().toString();
                        System.out.println(record.value());
                        if(record.value().getSyncHiveId() == null) {
                            System.out.printf("received key %s linking identity %s%n", record.key(), uuid);
                            produceCustomerLinkedId(record.value(), uuid);
                        }
                        else {
                            System.out.println("Received a customer we already know, lets perform an update in target system...");
                            System.out.println(
                                    record.value()
                            );
                        }
                    }
                });
            }
        }
    }

    private void produceCustomerLinkedId(Customer source, String uuid) {
        try(KafkaProducer<String, CustomerLinkedIdentities> identityLinker = new KafkaProducer<>(Common.transactionalProducerProperties())) {
            identityLinker.initTransactions();
            identityLinker.beginTransaction();
            identityLinker.send(
                    new ProducerRecord<>(
                            "customer_linked_identities",
                            uuid,
                            CustomerLinkedIdentities.newBuilder()
                                    .setSyncHiveId(uuid)
                                    .setSourceSystemId(source.getSourceSystemId())
                                    .setSystemName("monitor")
                                    .build()
                    )
            );
            identityLinker.commitTransaction();
            identityLinker.flush();
        }
    }
}
