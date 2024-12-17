package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


@Slf4j
public class ReplicateMain {

    public static final String INPUT_TOPIC = ReplicateConfig.inputTopicName();
    public static final String OUTPUT_TOPIC = ReplicateConfig.outputTopicName();

    private static void replicate() throws Exception {
        Thread producerThread = new Thread(new DataProducer());
        producerThread.start();
        Thread consumerThread = new Thread(new DataConsumer());
        consumerThread.start();

        new TopicCreator().createTopics();
        KafkaSource<User> source = KafkaSource.<User>builder()
                .setBootstrapServers(ReplicateConfig.cluster1BootstrapServers())
                .setTopics(INPUT_TOPIC)
                .setGroupId("my-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new UserDeserializationSchema())
                .build();
        KafkaSink<User> sink = KafkaSink.<User>builder()
                .setBootstrapServers(ReplicateConfig.cluster2BootstrapServers())
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(OUTPUT_TOPIC)
                        .setValueSerializationSchema(new UserSerializationSchema())
                        .build()
                )
                .build();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.fromSource(source, WatermarkStrategy.noWatermarks(), "messages from kafka")
                .setParallelism(2)
                .map(user -> User.builder().uuid(user.getUuid()).name("XXXXXX").age(user.getAge()).build())
                .sinkTo(sink);
        env.execute("Replicate");
    }

    public static void main(String[] args) throws Exception {
        replicate();
    }

}