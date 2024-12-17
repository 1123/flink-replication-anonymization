package org.example;

import com.google.common.collect.EvictingQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;


@Slf4j
public class DataConsumer implements Runnable {

    private static final int measurements = 100;

    public static void main(String[] args) {
        new DataProducer().run();
    }

    @Override
    public void run() {
        log.info("Starting up consumer. ");
        Properties properties = new Properties();
        properties.put("bootstrap.servers", ReplicateConfig.cluster2BootstrapServers());
        properties.put("key.deserializer", StringDeserializer.class);
        properties.put("value.deserializer", UserDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.put("group.id", "g1");
        long totalLatency = 0L;
        EvictingQueue<Long> evictingQueue = EvictingQueue.create(measurements);
        try(KafkaConsumer<String, User> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(Collections.singleton(ReplicateMain.OUTPUT_TOPIC));
            while (true) {
                log.debug("Polling for records");
                var iterator = consumer.poll(Duration.ofMillis(100)).iterator();
                log.debug("Received some records");
                while (iterator.hasNext()) {
                    var record = iterator.next();
                    if (record.value() == null) {
                        log.info("Received null record");
                        continue;
                    }
                    if (record.value().getTimestamp() == null) {
                        log.info("Received record with null timestamp");
                        continue;
                    }
                    long latency = System.currentTimeMillis() - record.value().getTimestamp();
                    log.debug("Putting latency measurement: {}, {}", record.value().getUuid().hashCode(), latency);
                    evictingQueue.add(latency);
                    log.debug("latencies: {}", evictingQueue);
                    totalLatency = evictingQueue.stream().reduce(0L, Long::sum);
                    log.info("Received record: {}; average latency: {}", record.value(), totalLatency / evictingQueue.size());
                }
            }
        }


    }



}

