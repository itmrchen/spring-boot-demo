package com.example.kafka;

import com.example.exception.ProjectException;
import com.example.util.SerializeUtil;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Arrays;
import java.util.Map;

public class CustomJsonDeserializer implements Deserializer<Object> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        try {
            return SerializeUtil.deserialize(data);
        } catch (Exception e) {
            throw new ProjectException("Can't deserialize data [" + Arrays.toString(data) + "] from topic [" + topic + "]", e);
        }
    }

    @Override
    public void close() {

    }

}
