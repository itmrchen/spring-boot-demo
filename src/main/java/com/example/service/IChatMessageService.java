package com.example.service;

import com.example.model.po.ChatMessage;
import com.example.params.Params;

public interface IChatMessageService extends IBaseService<ChatMessage> {

    /** 已读 */
    Integer READ = 1;
    /** 未读 */
    Integer UNREAD = 0;

    ChatMessage getSelfById(Integer id, Params<ChatMessage> params);

}
