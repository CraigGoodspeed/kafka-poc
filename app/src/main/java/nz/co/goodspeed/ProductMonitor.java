package nz.co.goodspeed;

import io.confluent.kafka.serializers.*;
import nz.co.goodspeed.model.LinkedIdentity;
import nz.co.goodspeed.model.Product;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class ProductMonitor {

    // Matches the broker port specified in the Docker Compose file.
    private final static String BOOTSTRAP_SERVERS = "localhost:29092";
    // Matches the Schema Registry port specified in the Docker Compose file.
    private final static String SCHEMA_REGISTRY_URL = "http://localhost:8081";
    // Matches the topic name specified in the ksqlDB CREATE TABLE statement.
    private final static String TOPIC_READ = "PRODUCT_MONITOR";
    // For you to fill in: which address SendGrid should send from.
    private final static String TOPIC_WRITE = "product_linked_identities";

    public static Properties buildProperties(String topic) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "app-monitor");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("schema.registry.url", SCHEMA_REGISTRY_URL);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        return props;
    }

    public static Properties producerProperties() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", BOOTSTRAP_SERVERS);
        properties.setProperty("acks", "all");
        properties.setProperty("retries", "10");
        // avro part
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", SCHEMA_REGISTRY_URL);
        properties.setProperty("enable.idempotence","true");
        properties.setProperty("transactional.id", "linked-identity-1");
        return properties;
    }

    public static void main(final String[] args) {

        try {
            try (final KafkaConsumer<String, Product> consumer = new KafkaConsumer<>(buildProperties(TOPIC_READ))) {
                consumer.subscribe(Collections.singletonList(TOPIC_READ));
                while (true) {
                    final ConsumerRecords<String, Product> records = consumer.poll(Duration.ofMillis(100));
                    for (final ConsumerRecord<String, Product> record : records) {
                        final Product value = record.value();
                        System.out.println("checking null value;");
                        System.out.println(record.key());

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
        try(KafkaProducer<String, LinkedIdentity> writer = new KafkaProducer<>(producerProperties())) {
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
