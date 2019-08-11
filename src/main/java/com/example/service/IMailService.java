package com.example.service;

import com.example.model.po.Mail;

public interface IMailService extends IBaseService<Mail> {

    // 草稿
    String DRAFT_STATUS = "draft";

    // 发送中
    String SENDING_STATUS = "sending";

    // 已发送
    String SENT_STATUS = "sent";

    // 发送失败
    String FAIL_STATUS = "fail";

    Mail send(Integer id);

    void send(Mail mail, Integer maxRetry);

    void send(Mail mail);

}
