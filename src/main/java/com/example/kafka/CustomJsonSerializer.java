package com.example.kafka;

import com.example.exception.ProjectException;
import com.example.util.SerializeUtil;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class CustomJsonSerializer implements Serializer<Object> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Object data) {
        try {
            return SerializeUtil.serialize(data);
        } catch (Exception e) {
            throw new ProjectException("Can't serialize data [" + data + "] for topic [" + topic + "]", e);
        }
    }

    @Override
    public void close() {

    }

}
