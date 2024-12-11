package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class UserSerializationSchema implements SerializationSchema<User> {

    private ObjectMapper m = new ObjectMapper();

    @Override
    public byte[] serialize(User user) {
        try {
            return m.writeValueAsBytes(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
