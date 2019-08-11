package com.example.websocket;

import lombok.Data;

@Data
public class MessageFromClient {

    private String type; // 消息类型
    private String subId; // 订阅id
    private String destination; // 主题
    private Integer contentLength; // 内容长度
    private String content; // 内容

}
