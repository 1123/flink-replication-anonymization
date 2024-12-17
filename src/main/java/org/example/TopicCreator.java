package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class TopicCreator {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new TopicCreator().createTopics();
    }

    public void createTopics() throws ExecutionException, InterruptedException {
        Properties properties1 = new Properties();
        properties1.put("bootstrap.servers", ReplicateConfig.cluster1BootstrapServers());
        Properties properties2 = new Properties();
        properties2.put("bootstrap.servers", ReplicateConfig.cluster2BootstrapServers());
        try (AdminClient admin1 = AdminClient.create(properties1);
             AdminClient admin2 = AdminClient.create(properties2)) {
            try {
                admin1.deleteTopics(List.of(ReplicateMain.INPUT_TOPIC)).all().get();
                admin2.deleteTopics(List.of(ReplicateMain.OUTPUT_TOPIC)).all().get();
            } catch (Exception e) {
                log.warn("Delete topics failed");
            }
            Thread.sleep(1000);
            try {
                admin1.createTopics(List.of(new NewTopic(ReplicateMain.INPUT_TOPIC, 1, (short) 1)))
                        .all().get();
            } catch (Exception e) {
                log.warn("Exception while creating input topic: {}", e.getMessage());
            }
            try {
                admin2.createTopics(List.of(new NewTopic(ReplicateMain.OUTPUT_TOPIC, 1, (short) 1)))
                        .all().get();
            } catch (Exception e) {
                log.warn("Exception while creating output topic: {}", e.getMessage());
            }
            log.info("Created topics");
        }
    }

}