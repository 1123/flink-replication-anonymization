package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class UserDeserializationSchema implements DeserializationSchema<User> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public User deserialize(byte[] message) throws IOException {
        return objectMapper.readValue(message, User.class);
    }

    @Override
    public boolean isEndOfStream(User nextElement) {
        return false;
    }

    @Override
    public TypeInformation<User> getProducedType() {
        return TypeInformation.of(User.class);
    }
}
