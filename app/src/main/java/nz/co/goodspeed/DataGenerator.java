package nz.co.goodspeed;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import nz.co.goodspeed.model.ProductDebug;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class DataGenerator {

    // Matches the broker port specified in the Docker Compose file.
    private final static String BOOTSTRAP_SERVERS = "localhost:29092";
    // Matches the Schema Registry port specified in the Docker Compose file.
    private final static String SCHEMA_REGISTRY_URL = "http://localhost:8081";
    // Matches the topic name specified in the ksqlDB CREATE TABLE statement.
    private final static String TOPIC_WRITE = "products";

    public static Properties producerProps() {
            Properties properties = new Properties();
            properties.setProperty("bootstrap.servers", BOOTSTRAP_SERVERS);
            properties.setProperty("acks", "all");
            properties.setProperty("retries", "10");
            // avro part
            properties.setProperty("key.serializer", StringSerializer.class.getName());
            properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
            properties.setProperty("schema.registry.url", SCHEMA_REGISTRY_URL);
            return properties;
    }

    public static void main(String [] args) {
        try(KafkaProducer<String, ProductDebug> data = new KafkaProducer<>(producerProps())) {
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
