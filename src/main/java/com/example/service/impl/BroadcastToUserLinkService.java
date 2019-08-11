package com.example.service.impl;

import com.example.mapper.BroadcastToUserLinkMapper;
import com.example.model.po.BroadcastToUserLink;
import com.example.service.IBroadcastToUserLinkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BroadcastToUserLinkService extends BaseService<BroadcastToUserLinkMapper, BroadcastToUserLink> implements IBroadcastToUserLinkService {

    @Override
    @Transactional
    public void customUpdateBatchById(List<BroadcastToUserLink> links) {
        baseMapper.customUpdateBatchById(links);
    }

}
