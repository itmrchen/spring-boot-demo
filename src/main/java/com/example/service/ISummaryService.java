package com.example.service;

import com.example.model.po.BroadcastMessage;
import com.example.model.po.Mail;
import com.example.model.vo.SummaryVO;
import com.example.params.Params;

import java.util.List;

public interface ISummaryService {

    List<BroadcastMessage> getBroadcastByTime(Params<BroadcastMessage> params);

    List<Mail> getMailByTime(Params<Mail> params);

    List<SummaryVO> broadcastGroupBySendUser();

    List<SummaryVO> mailGroupBySendUser();

    List<SummaryVO> userGroupBySex();

}
