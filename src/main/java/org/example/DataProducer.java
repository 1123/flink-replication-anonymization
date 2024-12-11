package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

@Slf4j
public class DataProducer {

    public static void main(String[] args) throws InterruptedException {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", ReplicateConfig.cluster1BootstrapServers());
        properties.put("key.serializer", StringSerializer.class);
        properties.put("value.serializer", UserSerializer.class);
        try(KafkaProducer<String, User> producer = new KafkaProducer<>(properties)) {
            while (true) {
                Thread.sleep(100);
                User user = User.builder()
                        .name(UUID.randomUUID().toString())
                        .age((int) (Math.random() * 100))
                        .uuid(UUID.randomUUID())
                        .build();
                log.info("Sending {}", user.toString());
                producer.send(new ProducerRecord<>(ReplicateMain.INPUT_TOPIC, user));
            }
        }
    }

}
