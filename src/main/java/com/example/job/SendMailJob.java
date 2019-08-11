package com.example.job;

import com.example.mapper.UserMapper;
import com.example.model.po.Mail;
import com.example.model.po.MailContent;
import com.example.model.po.User;
import com.example.params.Params;
import com.example.service.IMailService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class SendMailJob extends QuartzJobBean {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IMailService mailService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("----------SendMailJob开始了----------");
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String username = jobDataMap.getString("username");
        String subject = jobDataMap.getString("subject");
        String content = jobDataMap.getString("content");
        Params<User> params = new Params<>(new User().setUsername(username));
        User user = userMapper.customSelectOne(params);
        if (user==null) {
            log.info("SendMailJob出错了: 用户名["+username+"]不存在");
            return;
        }
        Mail mail = new Mail();
        mail.setMailSubject(subject);
        mail.setMailType("info");
        mail.setMailContent(new MailContent().setContent(content));
        mail.setToUsers(Lists.newArrayList(user));
        mailService.customSave(mail);
        mail = mailService.send(mail.getId());
        mailService.send(mail);
        log.info("SendMailJob成功: 发送邮件到["+username+"]成功; email地址: ["+user.getEmail()+"]");
        log.info("----------SendMailJob结束了----------");
    }

}
