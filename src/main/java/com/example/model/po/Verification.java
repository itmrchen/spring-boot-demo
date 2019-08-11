package com.example.model.po;

import com.example.model.BaseModel;
import lombok.Data;

@Data
public class Verification extends BaseModel {

    private static final long serialVersionUID = 1L;
    private String  code;               // 验证码值
    private Long    nextCanSendTime;    // 下次可发送时间
    private boolean status;

}
