package nz.co.goodspeed;

import nz.co.goodspeed.model.LinkedIdentity;
import nz.co.goodspeed.model.Product;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

public class ProductMonitor {

    private final static String TOPIC_READ = "PRODUCT_MONITOR";
    // For you to fill in: which address SendGrid should send from.
    private final static String TOPIC_WRITE = "product_linked_identities";
    private static final String GROUP_ID = "app-monitor";

    public static void main(final String[] args) {

        try {
            try (final KafkaConsumer<String, Product> consumer = new KafkaConsumer<>(Common.buildConsumerProperties(GROUP_ID))) {
                consumer.subscribe(Collections.singletonList(TOPIC_READ));
                while (true) {
                    final ConsumerRecords<String, Product> records = consumer.poll(Duration.ofMillis(100));
                    for (final ConsumerRecord<String, Product> record : records) {
                        final Product value = record.value();
                        if (value != null) {
                            if (value.getSyncHiveId() == null || value.getSyncHiveId().equals("") || value.getSyncHiveId().length() == 0) {
                                System.out.println("inserting");
                                simulateInsert(value);
                            } else {
                                System.out.println("update");
                                simulateUpdate(value);
                            }
                            System.out.println(value);
                        }
                    }
                }
            }
        } catch(Exception err) {
            err.printStackTrace();
            //main(args);
        }
    }

    private static void simulateInsert(Product value) {

        //need to write an identifier to the product identities stream.
        try(KafkaProducer<String, LinkedIdentity> writer = new KafkaProducer<>(Common.transactionalProducerProperties())) {
            writer.initTransactions();
            writer.beginTransaction();
            LinkedIdentity id = LinkedIdentity.newBuilder()
                            .setSyncHiveId(UUID.randomUUID().toString())
                            .setSourceSystemName("monitor")
                            .setSourceSystemIdentifier(value.getSourceSystemId())
                    .build();

            writer.send(new ProducerRecord<>(
                    TOPIC_WRITE,
                    String.valueOf(id.getSyncHiveId()),
                    id
            ), (metadata, exception) -> {
                if(exception == null) {
                    System.out.println(metadata);
                } else {
                    exception.printStackTrace();
                }
            });
            System.out.println("wrote to linked topic");
            System.out.println(id);
            writer.flush();
            writer.commitTransaction();
        }
    }

    private static void simulateUpdate(Product value) {
        System.out.println(value.getSyncHiveId());
    }



}
