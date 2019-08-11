package com.example.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.ChatMessageMapper;
import com.example.params.Params;
import com.example.model.po.ChatMessage;
import com.example.service.IChatMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService extends BaseService<ChatMessageMapper, ChatMessage> implements IChatMessageService {

    @Override
    public List<ChatMessage> customList(Params<ChatMessage> params) {
        return baseMapper.customSelectList(params);
    }

    @Override
    public IPage<ChatMessage> customPage(Page<ChatMessage> page, Params<ChatMessage> params) {
        return page.setRecords(baseMapper.customSelectPage(page, params));
    }

    @Override
    public ChatMessage customGetById(Integer id) {
        return baseMapper.customSelectById(id);
    }

    @Override
    public ChatMessage getSelfById(Integer id, Params<ChatMessage> params) {
        return baseMapper.selectSelfById(id, params);
    }

}
