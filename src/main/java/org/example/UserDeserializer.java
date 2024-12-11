package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class UserDeserializer implements Deserializer<User> {

    private ObjectMapper m = new ObjectMapper();

    @Override
    public User deserialize(String topic, byte[] data) {
        try {
            return m.readValue(data, User.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
