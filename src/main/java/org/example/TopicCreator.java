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
            tryDeleteTopics(admin1, admin2);
            Thread.sleep(1000);
            tryCreateTopics(admin1, admin2);
        }
    }

    private void tryCreateTopics(AdminClient adminClient1, AdminClient adminClient2) {
        try {
            adminClient1.createTopics(List.of(new NewTopic(ReplicateMain.INPUT_TOPIC, 6, (short) 3)))
                    .all().get();
            log.info("Created input topic");
        } catch (Exception e) {
            log.warn("Exception while creating input topic: {}", e.getMessage());
        }
        try {
            adminClient2.createTopics(List.of(new NewTopic(ReplicateMain.OUTPUT_TOPIC, 6, (short) 3)))
                    .all().get();
            log.info("Created output topic");
        } catch (Exception e) {
            log.warn("Exception while creating output topic: {}", e.getMessage());
        }
    }

    private void tryDeleteTopics(AdminClient adminClient1, AdminClient adminClient2) {
        try {
            adminClient1.deleteTopics(List.of(ReplicateMain.INPUT_TOPIC)).all().get();
            log.info("Deleted input topic");
        } catch (Exception e) {
            log.warn("Delete input topic failed");
        } try {
            adminClient2.deleteTopics(List.of(ReplicateMain.OUTPUT_TOPIC)).all().get();
            log.info("Deleted output topic");
        } catch (Exception e) {
            log.warn("Delete output topic failed");
        }
    }

}