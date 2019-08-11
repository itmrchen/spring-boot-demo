package com.example.controller;

import com.example.model.po.BroadcastMessage;
import com.example.model.po.Mail;
import com.example.model.vo.ResultVO;
import com.example.model.vo.SummaryVO;
import com.example.params.Params;
import com.example.service.ISummaryService;
import com.example.util.CalendarUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.example.model.vo.ResultVO.SUCCESS;

@RestController
@RequestMapping(value = "/summary", produces = "application/json; charset=UTF-8")
@Api(tags = "统计")
public class SummeryController {

    @Autowired
    private ISummaryService summaryService;

    @GetMapping("/broadcast/month")
    @ApiOperation(value = "统计最近一个月的广播")
    public ResultVO broadcastMonth() {
        Params<BroadcastMessage> params = new Params<>(new BroadcastMessage());
        params.put("timeCol", "t_broadcast_message.send_time");
        CalendarUtil calendarUtil = new CalendarUtil(Calendar.getInstance());
        calendarUtil.round();
        long endTime = calendarUtil.getTimeInMillis();
        params.put("endTime", endTime);
        calendarUtil.addMonth(-1);
        long startTime = calendarUtil.getTimeInMillis();
        params.put("startTime", startTime);
        List<BroadcastMessage> list = summaryService.getBroadcastByTime(params);
        Map<Long, Integer> map = new LinkedHashMap<>();
        while (startTime<endTime) {
            long start = startTime;
            long end = calendarUtil.addDay(1).getTimeInMillis();
            map.put(start, 0);
            list.forEach(item -> {
                int count = map.get(start);
                Long sendTime = item.getSendTime();
                if (sendTime!=null && sendTime>=start && sendTime<end) {
                    count++;
                }
                map.put(start, count);
            });
            startTime = end;
        }
        List<SummaryVO> result = new ArrayList<>();
        map.forEach((name, value) -> result.add(new SummaryVO(String.valueOf(name), String.valueOf(value))));
        return new ResultVO<>(SUCCESS, "", result);
    }

    @GetMapping("/mail/month")
    @ApiOperation(value = "统计最近一个月的邮件")
    public ResultVO mailMonth() {
        Params<Mail> params = new Params<>(new Mail());
        params.put("timeCol", "t_mail.send_time");
        CalendarUtil calendarUtil = new CalendarUtil(Calendar.getInstance());
        calendarUtil.round();
        long endTime = calendarUtil.getTimeInMillis();
        params.put("endTime", endTime);
        calendarUtil.addMonth(-1);
        long startTime = calendarUtil.getTimeInMillis();
        params.put("startTime", startTime);
        List<Mail> list = summaryService.getMailByTime(params);
        Map<Long, Integer> map = new LinkedHashMap<>();
        while (startTime<endTime) {
            long start = startTime;
            long end = calendarUtil.addDay(1).getTimeInMillis();
            map.put(start, 0);
            list.forEach(item -> {
                int count = map.get(start);
                Long sendTime = item.getSendTime();
                if (sendTime!=null && sendTime>=start && sendTime<end) {
                    count++;
                }
                map.put(start, count);
            });
            startTime = end;
        }
        List<SummaryVO> result = new ArrayList<>();
        map.forEach((name, value) -> result.add(new SummaryVO(String.valueOf(name), String.valueOf(value))));
        return new ResultVO<>(SUCCESS, "", result);
    }

    @GetMapping("/broadcast/senduser")
    @ApiOperation(value = "按照发送人统计广播")
    public ResultVO broadcastGroupBySendUser() {
        List<SummaryVO> list = summaryService.broadcastGroupBySendUser();
        return new ResultVO<>(SUCCESS, "", list);
    }

    @GetMapping("/mail/senduser")
    @ApiOperation(value = "按照发送人统计邮件")
    public ResultVO mailGroupBySendUser() {
        List<SummaryVO> list = summaryService.mailGroupBySendUser();
        return new ResultVO<>(SUCCESS, "", list);
    }

    @GetMapping("/user/sex")
    @ApiOperation(value = "按照性别统计用户")
    public ResultVO userGroupBySex() {
        List<SummaryVO> list = summaryService.userGroupBySex();
        return new ResultVO<>(SUCCESS, "", list);
    }

}
