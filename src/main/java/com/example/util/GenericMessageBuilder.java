package com.example.util;

import com.alibaba.fastjson.JSON;
import org.springframework.messaging.support.GenericMessage;

import java.nio.charset.StandardCharsets;

public class GenericMessageBuilder {

    private Object payload;
    private MessageHeadersBuilder headersBuilder = new MessageHeadersBuilder();

    public GenericMessageBuilder payload(Object payload) {
        this.payload = payload;
        return this;
    }

    public GenericMessageBuilder destination(String destination) {
        this.headersBuilder.destination(destination);
        return this;
    }

    public GenericMessageBuilder leaveMutable(boolean leaveMutable) {
        this.headersBuilder.leaveMutable(leaveMutable);
        return this;
    }

    public GenericMessage<byte[]> build() {
        return new GenericMessage<>(JSON.toJSONString(payload).getBytes(StandardCharsets.UTF_8), this.headersBuilder.build());
    }

}
