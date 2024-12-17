package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;


@Slf4j
public class DataConsumer implements Runnable{

    public static void main(String[] args) {
        new DataProducer().run();
    }

    @Override
    public void run() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", ReplicateConfig.cluster2BootstrapServers());
        properties.put("key.deserializer", StringDeserializer.class);
        properties.put("value.deserializer", UserDeserializer.class);
        properties.put("group.id", "g1");
        try(KafkaConsumer<String, User> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(Collections.singleton(ReplicateMain.OUTPUT_TOPIC));
            while (true) {
                consumer.poll(java.time.Duration.ofMillis(100)).forEach(record -> {
                    log.info("Received record: {}", record.value());
                });
            }
        }
    }
}

