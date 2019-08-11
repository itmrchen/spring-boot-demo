package com.example.mapper;

import com.example.model.po.ChatMessage;
import com.example.params.Params;
import org.apache.ibatis.annotations.Param;

public interface ChatMessageMapper extends IBaseMapper<ChatMessage> {

    ChatMessage selectSelfById(Integer id, @Param("params") Params<ChatMessage> params);

}
