package com.example.model.vo;

import com.example.model.po.ChatMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChatMessageVO extends ChatMessage {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "阅读状态", allowableValues = "1,0")
    private Integer readStatus;

}
