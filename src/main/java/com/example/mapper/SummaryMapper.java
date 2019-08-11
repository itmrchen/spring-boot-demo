package com.example.mapper;

import com.example.model.po.BroadcastMessage;
import com.example.model.po.Mail;
import com.example.model.vo.SummaryVO;
import com.example.params.Params;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SummaryMapper {

    List<BroadcastMessage> selectBroadcastByTime(@Param("params") Params<BroadcastMessage> params);

    List<Mail> selectMailByTime(@Param("params") Params<Mail> params);

    List<SummaryVO> broadcastGroupBySendUser();

    List<SummaryVO> mailGroupBySendUser();

    List<SummaryVO> userGroupBySex();

}
