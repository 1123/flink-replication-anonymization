package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
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
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.put("group.id", "g1");
        long totalLantency = 0L;
        double numRecords = 0.0;
        try(KafkaConsumer<String, User> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(Collections.singleton(ReplicateMain.OUTPUT_TOPIC));
            while (true) {
                var iterator = consumer.poll(Duration.ofMillis(100)).iterator();
                while (iterator.hasNext()) {
                    var record = iterator.next();
                    numRecords++;
                    totalLantency += System.currentTimeMillis() - record.value().getTimestamp();
                    log.info("Received record: {}; average latency: {}", record.value(), totalLantency / numRecords);
                }
            }
        }
    }
}

