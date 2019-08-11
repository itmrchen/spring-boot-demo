package com.example.service.impl;

import com.example.mapper.SummaryMapper;
import com.example.model.po.BroadcastMessage;
import com.example.model.po.Mail;
import com.example.model.vo.SummaryVO;
import com.example.params.Params;
import com.example.service.ISummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryService implements ISummaryService {

    @Autowired
    private SummaryMapper summaryMapper;

    @Override
    @Cacheable(cacheNames = {"summary:broadcast:month"}, keyGenerator = "defaultPageKeyGenerator")
    public List<BroadcastMessage> getBroadcastByTime(Params<BroadcastMessage> params) {
        return summaryMapper.selectBroadcastByTime(params);
    }

    @Override
    @Cacheable(cacheNames = {"summary:mail:month"}, keyGenerator = "defaultPageKeyGenerator")
    public List<Mail> getMailByTime(Params<Mail> params) {
        return summaryMapper.selectMailByTime(params);
    }

    @Override
    @Cacheable(cacheNames = {"summary:broadcast:user"}, keyGenerator = "defaultPageKeyGenerator")
    public List<SummaryVO> broadcastGroupBySendUser() {
        return summaryMapper.broadcastGroupBySendUser();
    }

    @Override
    @Cacheable(cacheNames = {"summary:mail:user"}, keyGenerator = "defaultPageKeyGenerator")
    public List<SummaryVO> mailGroupBySendUser() {
        return summaryMapper.mailGroupBySendUser();
    }

    @Override
    @Cacheable(cacheNames = {"summary:user:sex"}, keyGenerator = "defaultPageKeyGenerator")
    public List<SummaryVO> userGroupBySex() {
        return summaryMapper.userGroupBySex();
    }

}
